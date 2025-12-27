package africa.semicolon.safereportbackend.services;

public interface AnonymityServices {
    boolean isAllowedToPost(String deviceSignatureHash);
    void checkSpam(String deviceSignatureHash);
    String hashSignature(String input);
}
