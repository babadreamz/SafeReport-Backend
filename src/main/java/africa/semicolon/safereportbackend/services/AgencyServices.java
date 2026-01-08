package africa.semicolon.safereportbackend.services;

import africa.semicolon.safereportbackend.dtos.modeldtos.ReportSummary;
import africa.semicolon.safereportbackend.dtos.modeldtos.ResponderUnitDto;
import africa.semicolon.safereportbackend.dtos.requests.AgencyRequest;
import africa.semicolon.safereportbackend.dtos.requests.LoginRequest;
import africa.semicolon.safereportbackend.dtos.requests.ResponderRequest;
import africa.semicolon.safereportbackend.dtos.responses.*;

import java.util.List;

public interface AgencyServices {
    AgencyResponse createAgency(AgencyRequest request);
    AgencyLoginResponse login(LoginRequest request);
    String logout(String authHeader);
    ResponderResponse createResponderUnit(String agencyId, ResponderRequest request);
    List<ReportSummary> getUnassignedReports(String agencyId);
    ReportResponse manuallyAssignReport(String reportId, String responderUnitId);
    void transferReportToAnotherAgency(String reportId, String targetAgencyId);
    List<ResponderUnitDto> getAllResponderUnits(String agencyId);
    void toggleResponderUnitStatus(String agencyId, String unitId, boolean isActive);
//    Page<ReportSummary> getAgencyReports(String agencyId, int page, int size, String status);
//    List<HeatmapAggregateDto> getAgencyHeatmapData(String agencyId);
//    List<UnitPerformanceMetricDto> getAgencyPerformanceMetrics(String agencyId);
}
