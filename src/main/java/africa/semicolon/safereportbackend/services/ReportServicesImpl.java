package africa.semicolon.safereportbackend.services;

import africa.semicolon.safereportbackend.data.models.*;
import africa.semicolon.safereportbackend.data.repositories.*;
import africa.semicolon.safereportbackend.dtos.modeldtos.MediaAttachmentDto;
import africa.semicolon.safereportbackend.dtos.requests.ReportRequest;
import africa.semicolon.safereportbackend.dtos.responses.ReportResponse;
import africa.semicolon.safereportbackend.exceptions.*;
import africa.semicolon.safereportbackend.utils.mappers.MediaAttachmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static africa.semicolon.safereportbackend.utils.mappers.ReportMapper.mapToReport;
import static africa.semicolon.safereportbackend.utils.mappers.ReportMapper.mapToResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServicesImpl implements ReportServices {
    @Autowired
    private final Reports reports;
    @Autowired
    private final AnonymityServices anonymityServices;
    @Autowired
    private final GhostReporters ghostReporters;
    @Autowired
    private final GeoCodingService geoCodingService;
    @Autowired
    private final MediaStorageService cloudinaryService;
    @Autowired
    private final MediaAttachments mediaAttachments;
    @Autowired
    private final MediaAttachmentMapper mediaAttachmentMapper;
    @Autowired
    private final ResponderUnits responderUnits;
    @Autowired
    private Agencies agencies;
    @Autowired
    private ResponderServices responderServices;

    @Override
    public ReportResponse submitReport(String deviceSignature, ReportRequest request) {
        String hashedDeviceSignature = anonymityServices.hashSignature(deviceSignature);
        if(!anonymityServices.isAllowedToPost(hashedDeviceSignature)){
            throw new SpamReportException("Spam detected: Request blocked,rate limit exceeded or user banned.");
        }
        GhostReporter reporter = ghostReporters.findByDeviceSignatureHash(hashedDeviceSignature)
                .orElseThrow(()-> new GhostReporterNotFoundException("Identity not found. Please register first."));
        Agency agency = agencies.findById(request.getAgencyId()).orElseThrow(()-> new AgencyNotFoundException("Invalid agency id."));

        Report report = mapToReport(request);
        report.setGhostReporterId(reporter.getId());
        report.setAgencyId(agency.getId());

        calculatePriorityAndDistance(report, request.isHappeningNow());

        try{
            Map<String, String> address = geoCodingService.getAddressDetails(
                    request.getIncidentLatitude(),request.getIncidentLongitude());
            report.setStreet(address.get("street"));
            report.setLga(address.get("lga"));
            report.setState(address.get("state"));
        }catch (Exception e){
            log.warn("Geocoding failed, proceeding with empty address.");
        }
        ResponderUnit nearestResponder = findNearestResponder(agency,report.getIncidentLatitude(),report.getIncidentLongitude());
        if(nearestResponder != null){
            report.setResponderUnitId(nearestResponder.getId());
            log.info("Report auto-dispatched to unit: {}", nearestResponder.getUsername());
            responderServices.invalidateResponderCache(nearestResponder.getId(),"PENDING");
        }else {
            log.warn("No responder units available for agency: {}", agency.getName());
            report.setResponderUnitId(null);
            report.setReportStatus(ReportStatus.UNASSIGNED);
        }
        Report savedReport = reports.save(report);
        return mapToResponse(savedReport);
    }

    @Override
    public ReportStatus checkReportStatus(String reportId) {
        Report report = findReportById(reportId);
        return report.getReportStatus();
    }

    @Override
    public MediaAttachmentDto attachMediaToReport(String reportId, MultipartFile file) {
        Report report = findReportById(reportId);
        String fileUrl;
        String fileHash = anonymityServices.calculateFileHash(file);
        Optional<MediaAttachment> existingFile = mediaAttachments.findFirstByHash(fileHash);
        if(existingFile.isPresent()){
            log.info("Duplicate media detected. Reusing existing file.");
            fileUrl = existingFile.get().getFileUrl();
        }else {
            fileUrl = cloudinaryService.uploadFile(file);
        }
        String type = determineMediaType(file.getContentType());
        MediaAttachment mediaAttachment = new MediaAttachment();
        mediaAttachment.setReport(report);
        mediaAttachment.setFileUrl(fileUrl);
        mediaAttachment.setHash(fileHash);
        mediaAttachment.setMediaType(type);
        mediaAttachment.setUploadedAt(LocalDateTime.now());
        mediaAttachment.setEvidenceStatus(EvidenceStatus.PENDING_VERIFICATION);
        MediaAttachment savedMediaAttachment = mediaAttachments.save(mediaAttachment);
        report.getMediaAttachments().add(savedMediaAttachment);
        reports.save(report);
        return mediaAttachmentMapper.mapToDto(savedMediaAttachment);
    }

    @Override
    public ResponderUnit findNearestResponder(Agency agency, double incidentLatitude, double incidentLongitude){
        return geoCodingService.findNearestResponderId(agency.getId(), incidentLatitude, incidentLongitude)
                .flatMap(responderUnits::findById)
                .orElse(null);

//        List<ResponderUnit> responderUnitList = agency.getResponderUnits();
//        ResponderUnit nearestResponder = null;
//        double minDistance = Double.MAX_VALUE;
//        for(ResponderUnit responderUnit : responderUnitList){
//            if(!responderUnit.isActive()){
//                continue;
//            }
//            if(responderUnit.getBaseLatitude() == null || responderUnit.getBaseLongitude() == null){
//                continue;
//            }
//            double distance = haversineDistance(incidentLatitude,incidentLongitude,
//                    responderUnit.getBaseLatitude(),responderUnit.getBaseLongitude());
//            if(distance < minDistance){
//                minDistance = distance;
//                nearestResponder = responderUnit;
//            }
//        }
//        return nearestResponder;
    }


    private void calculatePriorityAndDistance(Report report, boolean isHappeningNow) {
        if(report.getDeviceLatitude() == null || report.getDeviceLongitude() == null){
            report.setPriorityLevel(PriorityLevel.LOW);
        }
        double distance = haversineDistance(
                report.getDeviceLatitude(),report.getDeviceLongitude(),
                report.getIncidentLatitude(),report.getIncidentLongitude());
        report.setCalculatedDistanceMetadata(distance);
        if(!isHappeningNow){
            report.setPriorityLevel(PriorityLevel.MEDIUM);
        } else if (distance <= 100) {
            report.setPriorityLevel(PriorityLevel.CRITICAL);
        }else {
            report.setPriorityLevel(PriorityLevel.HIGH);
        }
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;
        double latDistance = Math.toRadians(lat2-lat1);
        double lonDistance = Math.toRadians(lon2-lon1);
        double a = Math.sin(latDistance/2) * Math.sin(latDistance/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance/2) * Math.sin(lonDistance/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
    private String determineMediaType(String contentType) {
        if (contentType == null) {
            return "UNKNOWN";
        }
        if (contentType.startsWith("image/")) {
            return "IMAGE";
        }
        if (contentType.startsWith("video/")) {
            return "VIDEO";
        }
        if (contentType.startsWith("audio/")) {
            return "AUDIO";
        }
        return "DOCUMENT";
    }

    private Report findReportById(String reportId){
        return reports.findById(reportId)
                .orElseThrow(()->{
                    log.error("Report not found");
                    return new ReportNotFoundException("Report not found.");
                });
    }

}
