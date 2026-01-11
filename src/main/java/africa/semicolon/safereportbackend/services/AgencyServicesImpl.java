package africa.semicolon.safereportbackend.services;

import africa.semicolon.safereportbackend.data.models.*;
import africa.semicolon.safereportbackend.data.repositories.Agencies;
import africa.semicolon.safereportbackend.data.repositories.Reports;
import africa.semicolon.safereportbackend.data.repositories.ResponderUnits;
import africa.semicolon.safereportbackend.dtos.modeldtos.ReportSummary;
import africa.semicolon.safereportbackend.dtos.modeldtos.ResponderUnitDto;
import africa.semicolon.safereportbackend.dtos.requests.AgencyRequest;
import africa.semicolon.safereportbackend.dtos.requests.LoginRequest;
import africa.semicolon.safereportbackend.dtos.requests.ResponderRequest;
import africa.semicolon.safereportbackend.dtos.responses.*;
import africa.semicolon.safereportbackend.exceptions.AgencyNotFoundException;
import africa.semicolon.safereportbackend.exceptions.InvalidLoginCredentialsException;
import africa.semicolon.safereportbackend.exceptions.ReportNotFoundException;
import africa.semicolon.safereportbackend.exceptions.ResponderNotFoundException;
import africa.semicolon.safereportbackend.security.JwtUtil;
import africa.semicolon.safereportbackend.security.TokenBlackList;
import africa.semicolon.safereportbackend.utils.mappers.MapReport;
import africa.semicolon.safereportbackend.utils.mappers.ReportMapper;
import africa.semicolon.safereportbackend.utils.mappers.ResponderUnitMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static africa.semicolon.safereportbackend.utils.mappers.AgencyModelMapper.*;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AgencyServicesImpl implements AgencyServices {
    @Autowired
    private final Agencies agencies;
    @Autowired
    private final ResponderUnits responderUnits;
    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private final Reports reports;
    @Autowired
    private MapReport reportMapper;
    @Autowired
    private final ReportServices reportServices;
    @Autowired
    private final ResponderServices responderService;
    @Autowired
    private ResponderUnitMapper responderUnitMapper;
    @Autowired
    private GeoCodingService geoCodingService;
    @Autowired
    private final JwtUtil jwtUtil;
    @Autowired
    private final TokenBlackList tokenBlackList;

    @Override
    @CacheEvict(value = "agenciesList", allEntries = true)
    public AgencyResponse createAgency(AgencyRequest request) {
        Agency agency = mapToAgency(request);
        agency.setPassword(passwordEncoder.encode(agency.getPassword()));
        Agency savedAgency = agencies.save(agency);
        return mapToAgencyResponse(savedAgency);
    }

    @Override
    public AgencyLoginResponse login(LoginRequest request) {
        Agency agency = agencies.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid Credentials"));
        if (!passwordEncoder.matches(request.getPassword(), agency.getPassword())) {
            throw new InvalidLoginCredentialsException("Invalid Credentials");
        }

        String token = jwtUtil.generateResponderToken(agency);
        return AgencyLoginResponse.builder().
                token(token)
                .agencyId(agency.getId())
                .agencyName(agency.getName())
                .agencyUsername(agency.getUsername())
                .build();
    }

    @Override
    public String logout(String authHeader) {
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlackList.addToken(token);
        }
        return "Logged out successfully";
    }

    @Override
    @Transactional
    public ResponderResponse createResponderUnit(String agencyId, ResponderRequest request) {
        Agency agency = agencies.findById(agencyId)
                .orElseThrow(()->new AgencyNotFoundException("Agency not found"));
        ResponderUnit responderUnit = mapToResponderUnit(agency,request);
        responderUnit.setPassword(passwordEncoder.encode(responderUnit.getPassword()));
        ResponderUnit savedResponderUnit = responderUnits.save(responderUnit);
        agency.getResponderUnits().add(savedResponderUnit);
        agencies.save(agency);

        geoCodingService.addResponderLocation(
                responderUnit.getAgency().getId(),
                responderUnit.getId(),
                responderUnit.getBaseLatitude(),
                responderUnit.getBaseLongitude()
        );
        return mapToResponderResponse(savedResponderUnit);
    }

    @Override
    @Cacheable(value = "agenciesList")
    public List<AgencyResponse> findAllAgencies() {
        List<Agency> agencyList =  agencies.findAll()
                .stream()
                .toList();
        List<AgencyResponse> agenciesList = new ArrayList<>();
        for (Agency agency : agencyList) {
            AgencyResponse agencyResponse = mapToAgencyResponse(agency);
            agenciesList.add(agencyResponse);
        }
        return agenciesList;
    }

    @Override
    public List<ReportSummary> getUnassignedReports(String agencyId) {
        List<Report> reportList = reports.findByAgencyIdAndReportStatusOrderByCreatedTimestampDesc(agencyId, ReportStatus.UNASSIGNED);
        return reportMapper.mapToSummaryList(reportList);
    }

    @Override
    public ReportResponse manuallyAssignReport(String reportId, String responderUnitId) {
        Report report = findReportById(reportId);
        report.setResponderUnitId(responderUnitId);
        report.setReportStatus(ReportStatus.PENDING);
        responderService.invalidateResponderCache(responderUnitId,"PENDING");
        Report savedReport = reports.save(report);
        return ReportMapper.mapToResponse(savedReport);
    }

    @Override
    public void transferReportToAnotherAgency(String reportId, String targetAgencyId) {
        Agency foundAgency = findAgencyById(targetAgencyId);
        Report report = findReportById(reportId);
        report.setAgencyId(foundAgency.getId());
        ResponderUnit nearestUnit = reportServices.findNearestResponder(foundAgency,report.getIncidentLatitude(),report.getIncidentLongitude());
        if(nearestUnit == null){
            log.warn("No responder units available for agency: {}", foundAgency.getName());
            report.setResponderUnitId(null);
            report.setReportStatus(ReportStatus.UNASSIGNED);
        }else {
            report.setResponderUnitId(nearestUnit.getId());
            report.setReportStatus(ReportStatus.PENDING);
            log.info("Report auto-dispatched to unit: {}", nearestUnit.getUsername());
            responderService.invalidateResponderCache(nearestUnit.getId(),"PENDING");
        }
        reports.save(report);
    }

    @Override
    public List<ResponderUnitDto> getAllResponderUnits(String agencyId) {
        Agency foundAgency = agencies.findByIdWithResponderUnit(agencyId)
                .orElseThrow(()-> new AgencyNotFoundException("Agency not found"));
        List<ResponderUnit> responders = foundAgency.getResponderUnits();
        return responderUnitMapper.mapToList(responders);
    }

    @Override
    public void toggleResponderUnitStatus(String agencyId, String unitId, boolean isActive) {
        Agency foundAgency = findAgencyById(agencyId);
        ResponderUnit responderUnit = responderUnits.findById(unitId)
                .orElseThrow(()-> new ResponderNotFoundException("Responder unit not found"));
        if(!responderUnit.getAgency().getId().equals(foundAgency.getId())){
            log.warn("Unauthorized access, responder unit does not belong to this agency");
            throw new SecurityException("Access Denied: responder unit does not belong to this agency");
        }
        responderUnit.setActive(isActive);
        responderUnits.save(responderUnit);
        if(isActive){
            geoCodingService.addResponderLocation(
                    responderUnit.getAgency().getId(),
                    responderUnit.getId(),
                    responderUnit.getBaseLatitude(),
                    responderUnit.getBaseLongitude()
            );
        }else {
            geoCodingService.removeResponderLocation(
                    responderUnit.getAgency().getId(),
                    responderUnit.getId()
            );
        }
    }

private Report findReportById(String reportId){
        return reports.findById(reportId)
                .orElseThrow(()->{
                    log.error("Report not found");
                    return new ReportNotFoundException("Report not found.");
                });
    }
    private Agency findAgencyById(String agencyId){
        return agencies.findById(agencyId)
                .orElseThrow(()->{
                    log.error("Agency not found");
                    return new AgencyNotFoundException("Agency not found");
                });
    }
}
