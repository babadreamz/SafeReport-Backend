package africa.semicolon.safereportbackend.services;

import africa.semicolon.safereportbackend.data.models.EvidenceStatus;
import africa.semicolon.safereportbackend.data.models.ReportStatus;
import africa.semicolon.safereportbackend.dtos.modeldtos.MediaAttachmentDto;
import africa.semicolon.safereportbackend.dtos.modeldtos.ReportSummary;
import africa.semicolon.safereportbackend.dtos.requests.LoginRequest;
import africa.semicolon.safereportbackend.dtos.responses.LoginResponse;

import java.util.List;

public interface ResponderServices {
    LoginResponse login(LoginRequest request);
    String logout(String authHeader);
    List<ReportSummary> getResponderFeed(String responderId, ReportStatus status);
    void invalidateResponderCache(String responderId, String status);
    List<MediaAttachmentDto> getMediaForReport(String reportId, String responderUsername);
    void flagMediaEvidence(String mediaAttachmentId, String reason, EvidenceStatus evidenceStatus);
    ReportSummary updateReportStatus(String reportId, ReportStatus newStatus, String responderId);
    ReportSummary getReportDetails(String reportId, String responderId);
}
