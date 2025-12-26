package africa.semicolon.safereportbackend.services;

import africa.semicolon.safereportbackend.data.models.ReportStatus;
import africa.semicolon.safereportbackend.dtos.requests.ReportRequest;
import africa.semicolon.safereportbackend.dtos.responses.ReportResponse;

public interface ReportServices {
    ReportResponse submitReport(ReportRequest request);
    ReportStatus checkReportStatus(String reportId);
}
