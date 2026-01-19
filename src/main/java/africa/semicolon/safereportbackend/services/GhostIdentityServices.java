package africa.semicolon.safereportbackend.services;

import africa.semicolon.safereportbackend.dtos.modeldtos.GhostReporterDto;
import africa.semicolon.safereportbackend.dtos.requests.RecoveryRequest;
import africa.semicolon.safereportbackend.dtos.responses.GhostReporterResponse;

public interface GhostIdentityServices {
    GhostReporterResponse createIdentity(String deviceSignature);
    GhostReporterDto recoverAccount(RecoveryRequest recoveryRequest, String deviceSignature);
}
