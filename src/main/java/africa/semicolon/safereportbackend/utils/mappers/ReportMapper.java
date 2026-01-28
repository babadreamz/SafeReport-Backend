package africa.semicolon.safereportbackend.utils.mappers;

import africa.semicolon.safereportbackend.data.models.Report;
import africa.semicolon.safereportbackend.data.models.ReportStatus;
import africa.semicolon.safereportbackend.dtos.requests.ReportRequest;
import africa.semicolon.safereportbackend.dtos.responses.ReportResponse;

import java.time.LocalDateTime;

public class ReportMapper {
    public static Report mapToReport(ReportRequest request){
        Report report = new Report();
        report.setIncidentType(request.getIncidentType());
        report.setDescription(request.getDescription());
        report.setCreatedTimestamp(LocalDateTime.now());
        report.setReportStatus(ReportStatus.PENDING);
        report.setDeviceLatitude(request.getDeviceLatitude());
        report.setDeviceLongitude(request.getDeviceLongitude());
        report.setIncidentLatitude(request.getIncidentLatitude());
        report.setIncidentLongitude(request.getIncidentLongitude());
        report.setLocationSource(request.getLocationSource());
        report.setPublicReport(request.isPublicReport());
        report.setDeleted(false);
        return report;
    }
    public static ReportResponse mapToResponse(Report report) {
        ReportResponse response = new ReportResponse();
        response.setReportId(report.getId());
        response.setGhostReporterId(report.getGhostReporterId());
        response.setStatus(report.getReportStatus());
        response.setAssignedPriority(report.getPriorityLevel());
        response.setTimeReceived(report.getCreatedTimestamp());
        response.setResolvedAddress(report.getStreet() + ", " + report.getLga());
        response.setResponderUnitId(report.getResponderUnitId());
        response.setMessage("Report received successfully.");
        response.setPublicReport(report.isPublicReport());
        return response;
    }
}
