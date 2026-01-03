package africa.semicolon.safereportbackend.services;

import org.springframework.web.multipart.MultipartFile;

public interface AnonymityServices {
    boolean isAllowedToPost(String deviceSignatureHash);
    void checkSpam(String deviceSignatureHash);
    String hashSignature(String input);
    String calculateFileHash(MultipartFile file);
}
