package africa.semicolon.safereportbackend.services;

import africa.semicolon.safereportbackend.exceptions.HashingFailedException;
import io.lettuce.core.RedisException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AnonymityServicesImpl implements AnonymityServices {
    private final StringRedisTemplate redisTemplate;
    private static final int MAX_REPORTS = 3;
    private static final long TIME_WINDOW_MINUTES = 10;
    private static final int MAX_ACCOUNTS = 3;
    private static final long SPAM_WINDOW_HOURS = 24;

    @Override
    public boolean isAllowedToPost(String deviceSignatureHash) {
        String banKey = "blacklist:" + deviceSignatureHash;
        if (redisTemplate.hasKey(banKey)) return false;
        return checkRateLimit(deviceSignatureHash);
    }
    @Override
    public void checkSpam(String deviceSignatureHash) {
        String redisKey = "spam:device:" + deviceSignatureHash;
        Long currentCount = redisTemplate.opsForValue().increment(redisKey);
        if (currentCount == null) throw new RedisException("Error connecting to spam filter service");
        if (currentCount==1){
            redisTemplate.expire(redisKey, SPAM_WINDOW_HOURS, TimeUnit.HOURS);
        }
        if (currentCount==MAX_ACCOUNTS){
            throw new RedisException("Spam detected: Too many Accounts created from this device.");
        }
    }
    @Override
    public String hashSignature(String input) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        }catch (Exception e){
            throw new HashingFailedException("Device Signature Hashing Failed");
        }
    }

    private boolean checkRateLimit(String deviceSignatureHash){
        String redisKey = "rate_limit:report:" + deviceSignatureHash;
        Long currentCount = redisTemplate.opsForValue().increment(redisKey);
        if (currentCount == null) throw new RedisException("Error connecting to spam filter service");
        if (currentCount == 1) {
            redisTemplate.expire(redisKey, TIME_WINDOW_MINUTES, TimeUnit.MINUTES);
        }
        return currentCount <= MAX_REPORTS;
    }
}
