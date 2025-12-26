package africa.semicolon.safereportbackend.services;

import africa.semicolon.safereportbackend.data.models.GhostReporter;
import africa.semicolon.safereportbackend.data.repositories.GhostReporters;
import africa.semicolon.safereportbackend.dtos.modeldtos.GhostReporterDto;
import africa.semicolon.safereportbackend.dtos.requests.RecoveryRequest;
import africa.semicolon.safereportbackend.dtos.responses.GhostReporterResponse;
import africa.semicolon.safereportbackend.exceptions.GhostReporterNotFoundException;
import africa.semicolon.safereportbackend.exceptions.HashingFailedException;
import africa.semicolon.safereportbackend.utils.mappers.GhostReporterMapper;
import io.lettuce.core.RedisException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.web3j.crypto.MnemonicUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@AllArgsConstructor
@Getter
public class GhostIdentityServicesImpl implements GhostIdentityServices {

    @Autowired
    private final GhostReporters ghostReporters;
    @Autowired
    private final StringRedisTemplate redisTemplate;
    @Autowired
    private final GhostReporterMapper ghostReporterMapper;

    private static final int MAX_ACCOUNTS = 3;
    private static final long SPAM_WINDOW_HOURS = 24;
    @Override
    public GhostReporterResponse createIdentity(String deviceSignature) {

        String deviceSignatureHash = hashSignature(deviceSignature);
        checkSpam(deviceSignatureHash);

        String recoveryPhrase = generateRecoveryCodes();
        String hashedRecoveryPhrase = hashSignature(recoveryPhrase);
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
        String hashedInput = hashSignature(recoveryRequest.getRecoveryPhrase());
        GhostReporter ghostReporter = ghostReporters.findByRecoveryPhraseHash(hashedInput).orElseThrow(
                () -> {
                    log.error("Invalid recovery phrase, GhostReporter not found");
                    return new GhostReporterNotFoundException("Invalid recovery phrase, GhostReporter not found");
                }
        );
        String deviceSignatureHash = hashSignature(deviceSignature);
        ghostReporter.setDeviceSignatureHash(deviceSignatureHash);
        GhostReporter savedGhostReporter = ghostReporters.save(ghostReporter);
        return ghostReporterMapper.mapToDto(savedGhostReporter);
    }

    private void checkSpam(String deviceSignatureHash) {
        String redisKey = "spam:device:" + deviceSignatureHash;
        Long currentCount = redisTemplate.opsForValue().increment(redisKey);
        if (currentCount == null) throw new RedisException("Error connecting to spam filter service");
        if (currentCount==1){
            redisTemplate.expire(redisKey, SPAM_WINDOW_HOURS, TimeUnit.HOURS);
        }
        if (currentCount==MAX_ACCOUNTS){
            throw new RedisException("Spam detected: Too many reports created from this device.");
        }
    }

    private String hashSignature(String input) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        }catch (Exception e){
            throw new HashingFailedException("Device Signature Hashing Failed");
        }
    }

    private String generateRecoveryCodes(){
        byte[] initialEntropy = new byte[16];
        new SecureRandom().nextBytes(initialEntropy);
        return MnemonicUtils.generateMnemonic(initialEntropy);
    }
}
