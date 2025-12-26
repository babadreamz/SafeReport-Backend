package africa.semicolon.safereportbackend.services;

import africa.semicolon.safereportbackend.data.models.GhostReporter;
import africa.semicolon.safereportbackend.dtos.modeldtos.GhostReporterDto;
import africa.semicolon.safereportbackend.dtos.requests.RecoveryRequest;
import africa.semicolon.safereportbackend.dtos.responses.GhostReporterResponse;
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
class GhostIdentityServicesImplTest {
    @Autowired
    private GhostIdentityServicesImpl ghostIdentityServices;

    @Test
    void testThatIdentityCanBeCreated() {
        String deviceSignature = "testDeviceSignature";
        GhostReporterResponse response = ghostIdentityServices.createIdentity(deviceSignature);
        assertNotNull(response);
        Optional<GhostReporter> foundReporter = ghostIdentityServices.getGhostReporters().findById(response.getId());
        assertTrue(foundReporter.isPresent());
    }
    @Test
    void testThatAccountCanBeRecovered(){
        String deviceSignature = "testDeviceSignature";
        GhostReporterResponse response = ghostIdentityServices.createIdentity(deviceSignature);
        assertNotNull(response);
        Optional<GhostReporter> foundReporter = ghostIdentityServices.getGhostReporters().findById(response.getId());
        assertTrue(foundReporter.isPresent());

        RecoveryRequest recoveryRequest = new RecoveryRequest();
        recoveryRequest.setRecoveryPhrase(response.getRecoveryPhrase());
        GhostReporterDto ghostReporterDto = ghostIdentityServices.recoverAccount(recoveryRequest, deviceSignature);
        assertNotNull(ghostReporterDto);
        assertEquals(ghostReporterDto.getId(), response.getId());
    }
}