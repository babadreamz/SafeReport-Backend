package africa.semicolon.safereportbackend.services;

import africa.semicolon.safereportbackend.data.models.*;
import africa.semicolon.safereportbackend.data.repositories.Agencies;
import africa.semicolon.safereportbackend.data.repositories.Reports;
import africa.semicolon.safereportbackend.data.repositories.ResponderUnits;
import africa.semicolon.safereportbackend.dtos.modeldtos.ReportSummary;
import africa.semicolon.safereportbackend.dtos.modeldtos.ResponderUnitDto;
import africa.semicolon.safereportbackend.dtos.requests.AgencyRequest;
import africa.semicolon.safereportbackend.dtos.requests.LoginRequest;
import africa.semicolon.safereportbackend.dtos.requests.ReportRequest;
import africa.semicolon.safereportbackend.dtos.requests.ResponderRequest;
import africa.semicolon.safereportbackend.dtos.responses.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReportingServicesTest {
    @Autowired
    private ReportServicesImpl reportServices;
    @Autowired
    private Reports reports;
    @Autowired
    private GhostIdentityServicesImpl ghostIdentityServices;
    @Autowired
    private AgencyServicesImpl agencyServicesImpl;
    @Autowired
    private Agencies agencies;
    @Autowired
    private ResponderServices responderServices;
    @Autowired
    private ResponderUnits responderUnits;


    @Test
    void testThatAgencyCanBeCreated(){
        AgencyRequest agencyRequest = new AgencyRequest();
        agencyRequest.setName("Naija Police");
        agencyRequest.setEmail("naijapolice@gmail.com");
        agencyRequest.setPassword("1StrongPassword!");
        agencyRequest.setUsername("NaijaPolice01");
        AgencyResponse agencyResponse = agencyServicesImpl.createAgency(agencyRequest);
        assertNotNull(agencyResponse);
        Optional<Agency> npf = agencies.findById(agencyResponse.getId());
        assertTrue(npf.isPresent());
        assertEquals(npf.get().getUsername(),agencyResponse.getUsername());
        assertEquals(npf.get().getEmail(),agencyResponse.getEmail());
    }
    @Test
    void testThatAgencyCanCreateResponderUnits(){
        AgencyRequest agencyRequest = new AgencyRequest();
        agencyRequest.setName("Naija Police");
        agencyRequest.setEmail("naijapolice@gmail.com");
        agencyRequest.setPassword("1StrongPassword!");
        agencyRequest.setUsername("NaijaPolice01");
        AgencyResponse agencyResponse = agencyServicesImpl.createAgency(agencyRequest);
        assertNotNull(agencyResponse);

        ResponderRequest responderRequest = new ResponderRequest();
        responderRequest.setName("NPF-Responder-01");
        responderRequest.setPassword("1StrongPassword!");
        responderRequest.setAgencyName(agencyResponse.getName());
        responderRequest.setBaseLatitude(6.524379);
        responderRequest.setBaseLongitude(3.379206);
        responderRequest.setUsername("NPFResponder01");
        responderRequest.setContactNumber("07034567890");
        ResponderResponse responderResponse = agencyServicesImpl.createResponderUnit(agencyResponse.getId(), responderRequest);
        assertNotNull(responderResponse);
        Optional<ResponderUnit> foundUnit = responderUnits.findByUsername(responderResponse.getUsername());
        assertTrue(foundUnit.isPresent());

        assertEquals(foundUnit.get().getAgency().getName(),agencyResponse.getName());
        assertEquals(foundUnit.get().getContactNumber(),responderResponse.getContactNumber());
    }

    @Test
    void testThatReportsCanBeSubmittedSuccessfully_AndReportStatusBeKnown(){
        String deviceSignature = "testDevice-01";
        GhostReporterResponse ghostResponse = ghostIdentityServices.createIdentity(deviceSignature);
        assertNotNull(ghostResponse);
        Optional<GhostReporter> foundReporter = ghostIdentityServices.getGhostReporters().findById(ghostResponse.getId());
        assertTrue(foundReporter.isPresent());

        AgencyRequest agencyRequest = new AgencyRequest();
        agencyRequest.setName("Naija Police");
        agencyRequest.setEmail("naijapolice@gmail.com");
        agencyRequest.setPassword("1StrongPassword!");
        agencyRequest.setUsername("NaijaPolice01");
        AgencyResponse agencyResponse = agencyServicesImpl.createAgency(agencyRequest);
        assertNotNull(agencyResponse);

        ResponderRequest responderRequest = new ResponderRequest();
        responderRequest.setName("NPF-Responder-01");
        responderRequest.setPassword("1StrongPassword!");
        responderRequest.setAgencyName(agencyResponse.getName());
        responderRequest.setBaseLatitude(6.524379);
        responderRequest.setBaseLongitude(3.379206);
        responderRequest.setUsername("NPFResponder01");
        responderRequest.setContactNumber("07034567890");
        ResponderResponse responderResponse = agencyServicesImpl.createResponderUnit(agencyResponse.getId(), responderRequest);
        assertNotNull(responderResponse);
        Optional<ResponderUnit> foundUnit = responderUnits.findByUsername(responderResponse.getUsername());
        assertTrue(foundUnit.isPresent());

        ReportRequest reportRequest = getReportRequest(agencyResponse);
        ReportResponse reportResponse = reportServices.submitReport(deviceSignature, reportRequest);
        assertNotNull(reportResponse);
        Optional<Report> foundReport = reports.findById(reportResponse.getReportId());
        assertTrue(foundReport.isPresent());
        assertEquals(foundReport.get().getGhostReporterId(),reportResponse.getGhostReporterId());

        ReportStatus reportStatus = reportServices.checkReportStatus(reportResponse.getReportId());
        assertEquals(ReportStatus.PENDING, reportStatus);
    }

    private ReportRequest getReportRequest(AgencyResponse agencyResponse) {
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setDescription("Electricity Vandalism");
        reportRequest.setDeviceLatitude(6.5095);
        reportRequest.setDeviceLongitude(3.3711);
        reportRequest.setIncidentLatitude(6.5059);
        reportRequest.setIncidentLongitude(3.3711);
        reportRequest.setIncidentType("ELECTRICITY Vandalism");
        reportRequest.setHappeningNow(true);
        reportRequest.setAgencyName(agencyResponse.getName());
        reportRequest.setLocationSource(LocationSource.GPS_AUTO);
        return reportRequest;
    }

    @Test
    void testThatUnAssignedReportsCanBeAccessedByAgency(){
        String deviceSignature = "test-Device-07";
        GhostReporterResponse ghostResponse = ghostIdentityServices.createIdentity(deviceSignature);
        assertNotNull(ghostResponse);
        Optional<GhostReporter> foundReporter = ghostIdentityServices.getGhostReporters().findById(ghostResponse.getId());
        assertTrue(foundReporter.isPresent());

        AgencyRequest agencyRequest = new AgencyRequest();
        agencyRequest.setName("Naija Police");
        agencyRequest.setEmail("naijapolice@gmail.com");
        agencyRequest.setPassword("1StrongPassword!");
        agencyRequest.setUsername("NaijaPolice01");
        AgencyResponse agencyResponse = agencyServicesImpl.createAgency(agencyRequest);
        assertNotNull(agencyResponse);

        ResponderRequest responderRequest = new ResponderRequest();
        responderRequest.setName("NPF-Responder-01");
        responderRequest.setPassword("1StrongPassword!");
        responderRequest.setAgencyName(agencyResponse.getName());
        responderRequest.setBaseLatitude(6.524379);
        responderRequest.setBaseLongitude(3.379206);
        responderRequest.setUsername("NPFResponder01");
        responderRequest.setContactNumber("07034567890");
        ResponderResponse responderResponse = agencyServicesImpl.createResponderUnit(agencyResponse.getId(), responderRequest);
        assertNotNull(responderResponse);
        Optional<ResponderUnit> foundUnit = responderUnits.findByUsername(responderResponse.getUsername());
        assertTrue(foundUnit.isPresent());

        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setDescription("Electricity Vandalism");
        reportRequest.setDeviceLatitude(15.5323);
        reportRequest.setDeviceLongitude( 3.3792);
        reportRequest.setIncidentLatitude(15.5323);
        reportRequest.setIncidentLongitude( 3.3792);
        reportRequest.setIncidentType("ELECTRICITY Vandalism");
        reportRequest.setHappeningNow(true);
        reportRequest.setAgencyName(agencyResponse.getName());
        reportRequest.setLocationSource(LocationSource.GPS_AUTO);
        ReportResponse reportResponse = reportServices.submitReport(deviceSignature, reportRequest);
        assertNotNull(reportResponse);

        ReportRequest reportRequest2 = new ReportRequest();
        reportRequest2.setDescription("Electricity Vandalism");
        reportRequest2.setDeviceLatitude(6.4503);
        reportRequest2.setDeviceLongitude( 12.3837);
        reportRequest2.setIncidentLatitude(6.4503);
        reportRequest2.setIncidentLongitude( 12.3837);
        reportRequest2.setIncidentType("ELECTRICITY Vandalism");
        reportRequest2.setHappeningNow(true);
        reportRequest2.setAgencyName(agencyResponse.getName());
        reportRequest2.setLocationSource(LocationSource.GPS_AUTO);
        ReportResponse reportResponse2 = reportServices.submitReport(deviceSignature, reportRequest2);
        assertNotNull(reportResponse2);

        List<ReportSummary> reportSummaries = agencyServicesImpl.getUnassignedReports(agencyResponse.getId());
        assertEquals(2,reportSummaries.size());
        assertEquals(ReportStatus.UNASSIGNED, reportSummaries.getFirst().getReportStatus());
        assertEquals(ReportStatus.UNASSIGNED, reportSummaries.getLast().getReportStatus());
    }
    @Test
    void testThatPublicReportsCanBeAccessed(){
        String deviceSignature = "test-Device-07";
        GhostReporterResponse ghostResponse = ghostIdentityServices.createIdentity(deviceSignature);
        assertNotNull(ghostResponse);
        Optional<GhostReporter> foundReporter = ghostIdentityServices.getGhostReporters().findById(ghostResponse.getId());
        assertTrue(foundReporter.isPresent());

        AgencyRequest agencyRequest = new AgencyRequest();
        agencyRequest.setName("Naija Police");
        agencyRequest.setEmail("naijapolice@gmail.com");
        agencyRequest.setPassword("1StrongPassword!");
        agencyRequest.setUsername("NaijaPolice01");
        AgencyResponse agencyResponse = agencyServicesImpl.createAgency(agencyRequest);
        assertNotNull(agencyResponse);

        ResponderRequest responderRequest = new ResponderRequest();
        responderRequest.setName("NPF-Responder-01");
        responderRequest.setPassword("1StrongPassword!");
        responderRequest.setAgencyName(agencyResponse.getName());
        responderRequest.setBaseLatitude(6.524379);
        responderRequest.setBaseLongitude(3.379206);
        responderRequest.setUsername("NPFResponder01");
        responderRequest.setContactNumber("07034567890");
        ResponderResponse responderResponse = agencyServicesImpl.createResponderUnit(agencyResponse.getId(), responderRequest);
        assertNotNull(responderResponse);
        Optional<ResponderUnit> foundUnit = responderUnits.findByUsername(responderResponse.getUsername());
        assertTrue(foundUnit.isPresent());

        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setDescription("Electricity Vandalism");
        reportRequest.setDeviceLatitude(15.5323);
        reportRequest.setDeviceLongitude( 3.3792);
        reportRequest.setIncidentLatitude(15.5323);
        reportRequest.setIncidentLongitude( 3.3792);
        reportRequest.setIncidentType("ELECTRICITY Vandalism");
        reportRequest.setHappeningNow(true);
        reportRequest.setAgencyName(agencyResponse.getName());
        reportRequest.setLocationSource(LocationSource.GPS_AUTO);
        reportRequest.setPublicReport(true);
        ReportResponse reportResponse = reportServices.submitReport(deviceSignature, reportRequest);
        assertNotNull(reportResponse);

        ReportRequest reportRequest2 = new ReportRequest();
        reportRequest2.setDescription("Electricity Vandalism");
        reportRequest2.setDeviceLatitude(6.4503);
        reportRequest2.setDeviceLongitude( 12.3837);
        reportRequest2.setIncidentLatitude(6.4503);
        reportRequest2.setIncidentLongitude( 12.3837);
        reportRequest2.setIncidentType("ELECTRICITY Vandalism");
        reportRequest2.setHappeningNow(true);
        reportRequest2.setAgencyName(agencyResponse.getName());
        reportRequest2.setLocationSource(LocationSource.GPS_AUTO);
        reportRequest2.setPublicReport(false);
        ReportResponse reportResponse2 = reportServices.submitReport(deviceSignature, reportRequest2);
        assertNotNull(reportResponse2);

        List<ReportSummary> reportSummaryList = reportServices.findPublicReports();
        assertEquals(1,reportSummaryList.size());
    }
    @Test
    void testThatReportsCanBeManuallyAssignedToResponderByAgency(){
        String deviceSignature = "testDevice-08";
        GhostReporterResponse ghostResponse = ghostIdentityServices.createIdentity(deviceSignature);
        assertNotNull(ghostResponse);
        Optional<GhostReporter> foundReporter = ghostIdentityServices.getGhostReporters().findById(ghostResponse.getId());
        assertTrue(foundReporter.isPresent());

        AgencyRequest agencyRequest = new AgencyRequest();
        agencyRequest.setName("Naija Police");
        agencyRequest.setEmail("naijapolice@gmail.com");
        agencyRequest.setPassword("1StrongPassword!");
        agencyRequest.setUsername("NaijaPolice01");
        AgencyResponse agencyResponse = agencyServicesImpl.createAgency(agencyRequest);
        assertNotNull(agencyResponse);

        ResponderRequest responderRequest = new ResponderRequest();
        responderRequest.setName("NPF-Responder-01");
        responderRequest.setPassword("1StrongPassword!");
        responderRequest.setAgencyName(agencyResponse.getName());
        responderRequest.setBaseLatitude(6.524379);
        responderRequest.setBaseLongitude(3.379206);
        responderRequest.setUsername("NPFResponder01");
        responderRequest.setContactNumber("07034567890");
        ResponderResponse responderResponse = agencyServicesImpl.createResponderUnit(agencyResponse.getId(), responderRequest);
        assertNotNull(responderResponse);

        ResponderRequest responderRequest2 = new ResponderRequest();
        responderRequest2.setName("NPF-Responder-02");
        responderRequest2.setPassword("1StrongPassword!");
        responderRequest2.setAgencyName(agencyResponse.getName());
        responderRequest2.setBaseLatitude(6.5095);
        responderRequest2.setBaseLongitude(3.3711);
        responderRequest2.setUsername("NPFResponder-02");
        responderRequest2.setContactNumber("07064567891");
        ResponderResponse responderResponse2 = agencyServicesImpl.createResponderUnit(agencyResponse.getId(), responderRequest2);
        assertNotNull(responderResponse2);

        ReportRequest reportRequest = getReportRequest(agencyResponse);
        ReportResponse reportResponse = reportServices.submitReport(deviceSignature,reportRequest);
        assertNotNull(reportResponse);
        Optional<Report> foundReport = reports.findById(reportResponse.getReportId());
        assertTrue(foundReport.isPresent());
        assertEquals(foundReport.get().getResponderUnitId(),reportResponse.getResponderUnitId());

        //Manually Assigning to second responder Unit
        ReportResponse manuallyAssignedReport = agencyServicesImpl.manuallyAssignReport(reportResponse.getReportId(),responderResponse2.getId());
        assertNotNull(manuallyAssignedReport);
        Optional<Report> foundReport2 = reports.findById(manuallyAssignedReport.getReportId());
        assertTrue(foundReport2.isPresent());
        assertEquals(foundReport2.get().getId(),foundReport.get().getId());
        assertEquals(foundReport2.get().getResponderUnitId(),responderResponse2.getId());
        assertNotEquals(foundReport2.get().getResponderUnitId(),responderResponse.getId());
    }
    @Test
    void testThatReportsCanBeTransferredToAnotherAgency_AndAlsoReceivedByTheTargetAgency(){
        String deviceSignature = "test-Device12";
        GhostReporterResponse ghostResponse = ghostIdentityServices.createIdentity(deviceSignature);
        assertNotNull(ghostResponse);
//First Agency: Naija Police
        AgencyRequest npfRequest = new AgencyRequest();
        npfRequest.setName("Naija Police");
        npfRequest.setEmail("naijapolice@gmail.com");
        npfRequest.setPassword("1StrongPassword!");
        npfRequest.setUsername("NaijaPolice01");
        AgencyResponse npfResponse = agencyServicesImpl.createAgency(npfRequest);
        assertNotNull(npfResponse);
//police Responder Unit
        ResponderRequest npfResponderRequest = new ResponderRequest();
        npfResponderRequest.setName("NPF-Responder-01");
        npfResponderRequest.setPassword("1StrongPassword!");
        npfResponderRequest.setAgencyName(npfResponse.getName());
        npfResponderRequest.setBaseLatitude(6.5095);
        npfResponderRequest.setBaseLongitude(3.3711);
        npfResponderRequest.setUsername("NPFResponder01");
        npfResponderRequest.setContactNumber("07034567890");
        ResponderResponse npfResponderResponse = agencyServicesImpl.createResponderUnit(npfResponse.getId(), npfResponderRequest);
        assertNotNull(npfResponderResponse);

//Second agency: Fire Service
        AgencyRequest fireServiceRequest = new AgencyRequest();
        fireServiceRequest.setName("Fire Service");
        fireServiceRequest.setEmail("fireService@gmail.com");
        fireServiceRequest.setPassword("1StrongPassword!");
        fireServiceRequest.setUsername("fireService01");
        AgencyResponse fireServiceResponse = agencyServicesImpl.createAgency(fireServiceRequest);
        assertNotNull(fireServiceResponse);
//fire service responder unit
        ResponderRequest fireResponderRequest = new ResponderRequest();
        fireResponderRequest.setName("FIRE-Responder-01");
        fireResponderRequest.setPassword("1StrongPassword!");
        fireResponderRequest.setAgencyName(fireServiceResponse.getName());
        fireResponderRequest.setBaseLatitude(6.524379);
        fireResponderRequest.setBaseLongitude(3.379206);
        fireResponderRequest.setUsername("FIREResponder01");
        fireResponderRequest.setContactNumber("07094582890");
        ResponderResponse fireResponderResponse = agencyServicesImpl.createResponderUnit(fireServiceResponse.getId(), fireResponderRequest);
        assertNotNull(fireResponderResponse);
//Ghost Reporter reporting a fire incident to police
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setDescription("Fire outbreak at Filling station");
        reportRequest.setDeviceLatitude(6.5095);
        reportRequest.setDeviceLongitude(3.3711);
        reportRequest.setIncidentLatitude(6.5059);
        reportRequest.setIncidentLongitude(3.3711);
        reportRequest.setIncidentType("Fire Outbreak");
        reportRequest.setHappeningNow(true);
        reportRequest.setAgencyName(npfResponse.getName());
        reportRequest.setLocationSource(LocationSource.GPS_AUTO);
        ReportResponse reportResponse = reportServices.submitReport(deviceSignature, reportRequest);
        assertNotNull(reportResponse);
        Optional<Report> foundReport = reports.findById(reportResponse.getReportId());
        assertTrue(foundReport.isPresent());
        assertEquals(foundReport.get().getResponderUnitId(),reportResponse.getResponderUnitId());
        //Confirmation that report was sent to police
        assertEquals(foundReport.get().getAgencyId(),npfResponse.getId());
        assertEquals(foundReport.get().getResponderUnitId(),npfResponderResponse.getId());

        //police transferring the fire report to the fire service
        agencyServicesImpl.transferReportToAnotherAgency(foundReport.get().getId(),fireServiceResponse.getId());
        Optional<Report> transferredReport = reports.findById(foundReport.get().getId());
        assertTrue(transferredReport.isPresent());
        assertEquals(transferredReport.get().getAgencyId(),fireServiceResponse.getId());
        assertEquals(transferredReport.get().getResponderUnitId(),fireResponderResponse.getId());
    }
    @Test
    void testThatAgencyCanGetAllResponderUnitsUnderIt_AndCanToggleActiveStatus(){
        AgencyRequest fireServiceRequest = new AgencyRequest();
        fireServiceRequest.setName("Fire Service");
        fireServiceRequest.setEmail("fireService@gmail.com");
        fireServiceRequest.setPassword("1StrongPassword!");
        fireServiceRequest.setUsername("fireService01");
        AgencyResponse fireServiceResponse = agencyServicesImpl.createAgency(fireServiceRequest);
        assertNotNull(fireServiceResponse);
//fire service responder unit
        ResponderRequest fireResponderRequest = new ResponderRequest();
        fireResponderRequest.setName("FIRE-Responder-01");
        fireResponderRequest.setPassword("1StrongPassword!");
        fireResponderRequest.setAgencyName(fireServiceResponse.getName());
        fireResponderRequest.setBaseLatitude(6.524379);
        fireResponderRequest.setBaseLongitude(3.379206);
        fireResponderRequest.setUsername("FIREResponder01");
        fireResponderRequest.setContactNumber("07094582890");
        ResponderResponse fireResponderResponse = agencyServicesImpl.createResponderUnit(fireServiceResponse.getId(), fireResponderRequest);
        assertNotNull(fireResponderResponse);

        ResponderRequest fireResponderRequest2 = new ResponderRequest();
        fireResponderRequest2.setName("FIRE-Responder-02");
        fireResponderRequest2.setPassword("1StrongPassword!");
        fireResponderRequest2.setAgencyName(fireServiceResponse.getName());
        fireResponderRequest2.setBaseLatitude(6.524379);
        fireResponderRequest2.setBaseLongitude(3.379206);
        fireResponderRequest2.setUsername("FIREResponder02");
        fireResponderRequest2.setContactNumber("08024588760");
        ResponderResponse fireResponderResponse2 = agencyServicesImpl.createResponderUnit(fireServiceResponse.getId(), fireResponderRequest2);
        assertNotNull(fireResponderResponse2);

        List<ResponderUnitDto> allResponderUnits = agencyServicesImpl.getAllResponderUnits(fireServiceResponse.getId());
        assertEquals(2,allResponderUnits.size());
        assertNotNull(allResponderUnits.getFirst().getId());

        agencyServicesImpl.toggleResponderUnitStatus(fireServiceResponse.getId(), fireResponderResponse2.getId(), false);
        Optional<ResponderUnit> resUnit = responderUnits.findById(fireResponderResponse2.getId());
        assertTrue(resUnit.isPresent());
        assertFalse(resUnit.get().isActive());
    }
    @Test
    void testThatResponderUnitsCanLogInAndLogout(){
        AgencyRequest fireServiceRequest = new AgencyRequest();
        fireServiceRequest.setName("Fire Service");
        fireServiceRequest.setEmail("fireService@gmail.com");
        fireServiceRequest.setPassword("1StrongPassword!");
        fireServiceRequest.setUsername("fireService01");
        AgencyResponse fireServiceResponse = agencyServicesImpl.createAgency(fireServiceRequest);
        assertNotNull(fireServiceResponse);
//fire service responder unit
        ResponderRequest fireResponderRequest = new ResponderRequest();
        fireResponderRequest.setName("FIRE-Responder-01");
        fireResponderRequest.setPassword("1StrongPassword!");
        fireResponderRequest.setAgencyName(fireServiceResponse.getName());
        fireResponderRequest.setBaseLatitude(6.524379);
        fireResponderRequest.setBaseLongitude(3.379206);
        fireResponderRequest.setUsername("FIREResponder01");
        fireResponderRequest.setContactNumber("07094582890");
        ResponderResponse fireResponderResponse = agencyServicesImpl.createResponderUnit(fireServiceResponse.getId(), fireResponderRequest);
        assertNotNull(fireResponderResponse);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword("1StrongPassword!");
        loginRequest.setUsername("FIREResponder01");
        LoginResponse loginResponse = responderServices.login(loginRequest);
        assertNotNull(loginResponse);
        assertEquals(fireResponderResponse.getId(),loginResponse.getResponderId());
        String authHeader = "Bearer " + loginResponse.getToken();
        String message = responderServices.logout(authHeader);
        assertTrue(message.contains("successfully"));
    }

    @Test
    void testThatRespondersCanGetResponderFeed_GetReportDetails_AndUpdateReportStatus(){
        AgencyRequest fireServiceRequest = new AgencyRequest();
        fireServiceRequest.setName("Fire Service");
        fireServiceRequest.setEmail("fireService@gmail.com");
        fireServiceRequest.setPassword("1StrongPassword!");
        fireServiceRequest.setUsername("fireService01");
        AgencyResponse fireServiceResponse = agencyServicesImpl.createAgency(fireServiceRequest);
        assertNotNull(fireServiceResponse);
//fire service responder unit
        ResponderRequest fireResponderRequest = new ResponderRequest();
        fireResponderRequest.setName("FIRE-Responder-01");
        fireResponderRequest.setPassword("1StrongPassword!");
        fireResponderRequest.setAgencyName(fireServiceResponse.getName());
        fireResponderRequest.setBaseLatitude(6.524379);
        fireResponderRequest.setBaseLongitude(3.379206);
        fireResponderRequest.setUsername("FIREResponder01");
        fireResponderRequest.setContactNumber("07094582890");
        ResponderResponse fireResponderResponse = agencyServicesImpl.createResponderUnit(fireServiceResponse.getId(), fireResponderRequest);
        assertNotNull(fireResponderResponse);

        String deviceSignature = "test-device-5";
        GhostReporterResponse ghostResponse = ghostIdentityServices.createIdentity(deviceSignature);
        assertNotNull(ghostResponse);
        Optional<GhostReporter> foundReporter = ghostIdentityServices.getGhostReporters().findById(ghostResponse.getId());
        assertTrue(foundReporter.isPresent());

        ReportRequest reportRequest = getReportRequest(fireServiceResponse);
        ReportResponse reportResponse = reportServices.submitReport(deviceSignature,reportRequest);
        assertNotNull(reportResponse);

        ReportRequest reportRequest2 = getReportRequest(fireServiceResponse);
        ReportResponse reportResponse2 = reportServices.submitReport(deviceSignature,reportRequest2);
        assertNotNull(reportResponse2);

        List<ReportSummary> reportSummaries = responderServices.getResponderFeed(fireResponderResponse.getId(),ReportStatus.PENDING);
         assertEquals(2,reportSummaries.size());
         assertNotNull(reportSummaries.getFirst().getId());

         ReportSummary reportDetails = responderServices.getReportDetails(reportResponse.getReportId(),fireResponderResponse.getId());
         assertNotNull(reportDetails);
         assertEquals(reportResponse.getResponderUnitId(),reportDetails.getResponderUnitId());
         assertNotNull(reportDetails.getResponderUnitId());
         assertEquals(reportDetails.getGhostReporterId(),foundReporter.get().getId());

         ReportSummary reportSummary = responderServices.updateReportStatus(reportResponse.getReportId(),ReportStatus.RESOLVED,fireResponderResponse.getId());
         assertNotNull(reportSummary);
         Optional<Report> foundSummary = reports.findById(reportSummary.getId());
         assertTrue(foundSummary.isPresent());
         assertNotEquals(foundSummary.get().getReportStatus(), reportDetails.getReportStatus());
    }
    @Test
    void testThatErrorIsThrownWhenAnAgencyTriesToAccessReportsThatDontBelongToThem(){
        String deviceSignature = "test-device2";
        GhostReporterResponse ghostResponse = ghostIdentityServices.createIdentity(deviceSignature);
        assertNotNull(ghostResponse);
//First Agency: Naija Police
        AgencyRequest npfRequest = new AgencyRequest();
        npfRequest.setName("Naija Police");
        npfRequest.setEmail("naijapolice@gmail.com");
        npfRequest.setPassword("1StrongPassword!");
        npfRequest.setUsername("NaijaPolice01");
        AgencyResponse npfResponse = agencyServicesImpl.createAgency(npfRequest);
        assertNotNull(npfResponse);
//police Responder Unit
        ResponderRequest npfResponderRequest = new ResponderRequest();
        npfResponderRequest.setName("NPF-Responder-01");
        npfResponderRequest.setPassword("1StrongPassword!");
        npfResponderRequest.setAgencyName(npfResponse.getName());
        npfResponderRequest.setBaseLatitude(6.5095);
        npfResponderRequest.setBaseLongitude(3.3711);
        npfResponderRequest.setUsername("NPFResponder01");
        npfResponderRequest.setContactNumber("07034567890");
        ResponderResponse npfResponderResponse = agencyServicesImpl.createResponderUnit(npfResponse.getId(), npfResponderRequest);
        assertNotNull(npfResponderResponse);

//Second agency: Fire Service
        AgencyRequest fireServiceRequest = new AgencyRequest();
        fireServiceRequest.setName("Fire Service");
        fireServiceRequest.setEmail("fireService@gmail.com");
        fireServiceRequest.setPassword("1StrongPassword!");
        fireServiceRequest.setUsername("fireService01");
        AgencyResponse fireServiceResponse = agencyServicesImpl.createAgency(fireServiceRequest);
        assertNotNull(fireServiceResponse);
//fire service responder unit
        ResponderRequest fireResponderRequest = new ResponderRequest();
        fireResponderRequest.setName("FIRE-Responder-01");
        fireResponderRequest.setPassword("1StrongPassword!");
        fireResponderRequest.setAgencyName(fireServiceResponse.getName());
        fireResponderRequest.setBaseLatitude(6.524379);
        fireResponderRequest.setBaseLongitude(3.379206);
        fireResponderRequest.setUsername("FIREResponder01");
        fireResponderRequest.setContactNumber("07094582890");
        ResponderResponse fireResponderResponse = agencyServicesImpl.createResponderUnit(fireServiceResponse.getId(), fireResponderRequest);
        assertNotNull(fireResponderResponse);

        ReportRequest reportRequest = getReportRequest(fireServiceResponse);
        ReportResponse reportResponse = reportServices.submitReport(deviceSignature,reportRequest);
        assertNotNull(reportResponse);

        assertThrows(SecurityException.class,()->responderServices.getReportDetails(reportResponse.getReportId(),npfResponderResponse.getId()));

        List<AgencyResponse> agencyResponses = agencyServicesImpl.findAllAgencies();
        assertNotNull(agencyResponses);
    }

}