package africa.semicolon.safereportbackend.services;

import africa.semicolon.safereportbackend.data.models.*;
import africa.semicolon.safereportbackend.data.repositories.MediaAttachments;
import africa.semicolon.safereportbackend.data.repositories.Reports;
import africa.semicolon.safereportbackend.data.repositories.ResponderUnits;
import africa.semicolon.safereportbackend.dtos.modeldtos.MediaAttachmentDto;
import africa.semicolon.safereportbackend.dtos.modeldtos.ReportSummary;
import africa.semicolon.safereportbackend.dtos.requests.LoginRequest;
import africa.semicolon.safereportbackend.dtos.responses.LoginResponse;
import africa.semicolon.safereportbackend.exceptions.MediaNotFoundException;
import africa.semicolon.safereportbackend.exceptions.ReportNotFoundException;
import africa.semicolon.safereportbackend.exceptions.ResponderNotFoundException;
import africa.semicolon.safereportbackend.security.JwtUtil;
import africa.semicolon.safereportbackend.security.TokenBlackList;
import africa.semicolon.safereportbackend.utils.mappers.MapReport;
import africa.semicolon.safereportbackend.utils.mappers.MediaAttachmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResponderServicesImpl implements ResponderServices {
    @Autowired
    private final ResponderUnits responderUnits;
    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private final JwtUtil jwtUtil;
    @Autowired
    private final TokenBlackList tokenBlackList;
    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private final Reports reports;
    @Autowired
    private final MapReport reportMapper;
    @Autowired
    private final MediaAttachmentMapper mediaAttachmentMapper;
    @Autowired
    private final MediaAttachments mediaAttachments;

    private static final long CACHE_LIFESPAN = 10;

    @Override
    public LoginResponse login(LoginRequest request) {
        ResponderUnit responder = responderUnits.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid Credentials"));
        if (!passwordEncoder.matches(request.getPassword(), responder.getPassword())) {
            throw new RuntimeException("Invalid Credentials");
        }
        if (!responder.isActive()) {
            throw new RuntimeException("Account is deactivated. Contact HQ.");
        }
        String token = jwtUtil.generateResponderToken(responder);
        return LoginResponse.builder().
                token(token)
                .responderId(responder.getId())
                .agencyName(responder.getAgency().getName())
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
    public List<ReportSummary> getResponderFeed(String responderId, ReportStatus status) {
        ResponderUnit responder = findResponderUnitById(responderId);
        String cacheKey = "feed:" + responderId + ":" + status;
        @SuppressWarnings("unchecked")
        List<ReportSummary> cachedFeed = (List<ReportSummary>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedFeed != null) {
            log.info("Fetching feed from Redis Cache for: {}", responderId);
            return cachedFeed;
        }
        log.info("Cache miss. Fetching feed from DB for: {}", responderId);
        List<Report> reportList = reports.findByResponderUnitIdAndReportStatusOrderByCreatedTimestampDesc(responderId,status);
        for (Report report : reportList) {
            validateResponderUnit(report,responder);
        }
        List<ReportSummary> reportSummaries = reportMapper.mapToSummaryList(reportList);
        redisTemplate.opsForValue().set(cacheKey,reportSummaries,CACHE_LIFESPAN, TimeUnit.MINUTES);
        return reportSummaries;
    }
    @Override
    public void invalidateResponderCache(String responderId, String status) {
        String cacheKey = "feed:" + responderId + ":" + status;
        redisTemplate.delete(cacheKey);
        log.info("Invalidated Cache for: {}", cacheKey);
    }
    @Override
    public List<MediaAttachmentDto> getMediaForReport(String reportId, String responderUsername) {
        Report report = findReportById(reportId);
        ResponderUnit responder = responderUnits.findByUsername(responderUsername)
                .orElseThrow(()-> new ResponderNotFoundException("Invalid Responder Username"));
        validateResponderUnit(report,responder);
        List<MediaAttachment> media = report.getMediaAttachments();
        return mediaAttachmentMapper.mapToDtoList(media);
    }

    @Override
    public void flagMediaEvidence(String mediaAttachmentId, String reason, EvidenceStatus evidenceStatus) {
        MediaAttachment media = mediaAttachments.findById(mediaAttachmentId)
                .orElseThrow(() -> new MediaNotFoundException("Invalid Media Attachment"));
        media.setEvidenceStatus(evidenceStatus);
        media.setFlagReason(reason);
        mediaAttachments.save(media);
    }

    @Override
    public ReportSummary updateReportStatus(String reportId, ReportStatus newStatus, String responderId) {
        Report report = findReportById(reportId);
        ResponderUnit responder = findResponderUnitById(responderId);
        validateResponderUnit(report, responder);
        report.setReportStatus(newStatus);
        Report savedReport = reports.save(report);
        return reportMapper.mapToSummary(savedReport);
    }

    @Override
    public ReportSummary getReportDetails(String reportId, String responderId) {
        Report report = findReportById(reportId);
        ResponderUnit responder = findResponderUnitById(responderId);
        validateResponderUnit(report, responder);
        return reportMapper.mapToSummary(report);
    }

    private void validateResponderUnit(Report report, ResponderUnit responder) {
        if(!report.getAgencyId().equals(responder.getAgency().getId())){
            log.warn("Unauthorized access, report not owned by responder");
            throw new SecurityException("Access Denied: You cannot access this report");
        }
    }

    private Report findReportById(String reportId){
        return reports.findById(reportId)
                .orElseThrow(()->{
                    log.error("Report not found");
                    return new ReportNotFoundException("Report not found.");
                });
    }
    private ResponderUnit findResponderUnitById(String responderId){
        return responderUnits.findById(responderId)
                .orElseThrow(()-> {
                    log.error("Responder not found");
                    return new ResponderNotFoundException("Invalid Responder Id");
                });
    }
}
