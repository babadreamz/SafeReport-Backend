package africa.semicolon.safereportbackend.services;

import africa.semicolon.safereportbackend.data.models.GhostReporter;
import africa.semicolon.safereportbackend.data.models.LocationSource;
import africa.semicolon.safereportbackend.data.models.Report;
import africa.semicolon.safereportbackend.data.models.ReportStatus;
import africa.semicolon.safereportbackend.data.repositories.Reports;
import africa.semicolon.safereportbackend.dtos.requests.ReportRequest;
import africa.semicolon.safereportbackend.dtos.responses.GhostReporterResponse;
import africa.semicolon.safereportbackend.dtos.responses.ReportResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReportServicesImplTest {
    @Autowired
    private ReportServicesImpl reportServices;
    @Autowired
    private Reports reports;
    @Autowired
    private GhostIdentityServicesImpl ghostIdentityServices;

    @Test
    void testThatReportsCanBeSubmittedSuccessfully(){
        String deviceSignature = "testDevice1";
        GhostReporterResponse ghostResponse = ghostIdentityServices.createIdentity(deviceSignature);
        assertNotNull(ghostResponse);
        Optional<GhostReporter> foundReporter = ghostIdentityServices.getGhostReporters().findById(ghostResponse.getId());
        assertTrue(foundReporter.isPresent());

        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setDescription("Electricity Vandalism");
        reportRequest.setDeviceLatitude(6.5095);
        reportRequest.setDeviceLongitude(3.3711);
        reportRequest.setIncidentLatitude(6.5059);
        reportRequest.setIncidentLongitude(3.3711);
        reportRequest.setIncidentType("ELECTRICITY Vandalism");
        reportRequest.setHappeningNow(true);
        reportRequest.setLocationSource(LocationSource.MANUAL_PIN);
        ReportResponse reportResponse = reportServices.submitReport(deviceSignature, reportRequest);
        assertNotNull(reportResponse);
        Optional<Report> foundReport = reports.findById(reportResponse.getReportId());
        assertTrue(foundReport.isPresent());
        assertEquals(foundReport.get().getGhostReporterId(),reportResponse.getGhostReporterId());
    }
    @Test
    void testThatReportStatusCanBeKnown(){
        String deviceSignature = "testDevice1";
        GhostReporterResponse ghostResponse = ghostIdentityServices.createIdentity(deviceSignature);
        assertNotNull(ghostResponse);
        Optional<GhostReporter> foundReporter = ghostIdentityServices.getGhostReporters().findById(ghostResponse.getId());
        assertTrue(foundReporter.isPresent());

        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setDescription("Electricity Vandalism");
        reportRequest.setDeviceLatitude(6.5095);
        reportRequest.setDeviceLongitude(3.3711);
        reportRequest.setIncidentLatitude(6.5059);
        reportRequest.setIncidentLongitude(3.3711);
        reportRequest.setIncidentType("ELECTRICITY Vandalism");
        reportRequest.setHappeningNow(true);
        reportRequest.setLocationSource(LocationSource.MANUAL_PIN);
        ReportResponse reportResponse = reportServices.submitReport(deviceSignature, reportRequest);
        assertNotNull(reportResponse);
        Optional<Report> foundReport = reports.findById(reportResponse.getReportId());
        assertTrue(foundReport.isPresent());
        assertEquals(foundReport.get().getGhostReporterId(),reportResponse.getGhostReporterId());

        ReportStatus reportStatus = reportServices.checkReportStatus(reportResponse.getReportId());
        assertEquals(ReportStatus.PENDING, reportStatus);
    }
//    @Test
//    void testThatMediaCanBeAttachedToReport(){
//        String deviceSignature = "testDevice1";
//        GhostReporterResponse ghostResponse = ghostIdentityServices.createIdentity(deviceSignature);
//        assertNotNull(ghostResponse);
//        Optional<GhostReporter> foundReporter = ghostIdentityServices.getGhostReporters().findById(ghostResponse.getId());
//        assertTrue(foundReporter.isPresent());
//
//        ReportRequest reportRequest = new ReportRequest();
//        reportRequest.setDescription("Electricity Vandalism");
//        reportRequest.setDeviceLatitude(6.5095);
//        reportRequest.setDeviceLongitude(3.3711);
//        reportRequest.setIncidentLatitude(6.5059);
//        reportRequest.setIncidentLongitude(3.3711);
//        reportRequest.setIncidentType("ELECTRICITY Vandalism");
//        reportRequest.setHappeningNow(true);
//        reportRequest.setLocationSource(LocationSource.MANUAL_PIN);
//        ReportResponse reportResponse = reportServices.submitReport(deviceSignature, reportRequest);
//        assertNotNull(reportResponse);
//        Optional<Report> foundReport = reports.findById(reportResponse.getReportId());
//        assertTrue(foundReport.isPresent());
//        assertEquals(foundReport.get().getGhostReporterId(),reportResponse.getGhostReporterId());
//
//        ReportStatus reportStatus = reportServices.checkReportStatus(reportResponse.getReportId());
//        assertEquals(ReportStatus.PENDING, reportStatus);
//
//
//    }


}