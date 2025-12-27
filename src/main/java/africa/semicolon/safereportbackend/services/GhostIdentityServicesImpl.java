package africa.semicolon.safereportbackend.services;

import africa.semicolon.safereportbackend.data.models.GhostReporter;
import africa.semicolon.safereportbackend.data.repositories.GhostReporters;
import africa.semicolon.safereportbackend.dtos.modeldtos.GhostReporterDto;
import africa.semicolon.safereportbackend.dtos.requests.RecoveryRequest;
import africa.semicolon.safereportbackend.dtos.responses.GhostReporterResponse;
import africa.semicolon.safereportbackend.exceptions.GhostReporterNotFoundException;
import africa.semicolon.safereportbackend.utils.mappers.GhostReporterMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.MnemonicUtils;
import java.security.SecureRandom;


@Slf4j
@Service
@AllArgsConstructor
@Getter
public class GhostIdentityServicesImpl implements GhostIdentityServices {

    @Autowired
    private final GhostReporters ghostReporters;
    @Autowired
    private final GhostReporterMapper ghostReporterMapper;
    @Autowired
    private AnonymityServices anonymityServices;

    @Override
    public GhostReporterResponse createIdentity(String deviceSignature) {

        String deviceSignatureHash = anonymityServices.hashSignature(deviceSignature);
        anonymityServices.checkSpam(deviceSignatureHash);
        String recoveryPhrase = generateRecoveryCodes();
        String hashedRecoveryPhrase = anonymityServices.hashSignature(recoveryPhrase);
        GhostReporter ghostReporter = new GhostReporter();
        ghostReporter.setRecoveryPhraseHash(hashedRecoveryPhrase);
        ghostReporter.setDeviceSignatureHash(deviceSignatureHash);
        GhostReporter savedGhostReporter = ghostReporters.save(ghostReporter);
        GhostReporterResponse response = new GhostReporterResponse();
        response.setId(savedGhostReporter.getId());
        response.setRecoveryPhrase(recoveryPhrase);
        return response;
    }

    @Override
    public GhostReporterDto recoverAccount(RecoveryRequest recoveryRequest, String deviceSignature) {
        String hashedInput = anonymityServices.hashSignature(recoveryRequest.getRecoveryPhrase());
        GhostReporter ghostReporter = ghostReporters.findByRecoveryPhraseHash(hashedInput).orElseThrow(
                () -> {
                    log.error("Invalid recovery phrase, GhostReporter not found");
                    return new GhostReporterNotFoundException("Invalid recovery phrase, GhostReporter not found");
                }
        );
        String deviceSignatureHash = anonymityServices.hashSignature(deviceSignature);
        ghostReporter.setDeviceSignatureHash(deviceSignatureHash);
        GhostReporter savedGhostReporter = ghostReporters.save(ghostReporter);
        return ghostReporterMapper.mapToDto(savedGhostReporter);
    }



    private String generateRecoveryCodes(){
        byte[] initialEntropy = new byte[16];
        new SecureRandom().nextBytes(initialEntropy);
        return MnemonicUtils.generateMnemonic(initialEntropy);
    }
}
