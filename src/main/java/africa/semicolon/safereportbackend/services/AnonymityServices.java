package africa.semicolon.safereportbackend.services;

public interface AnonymityServices {
    boolean isAllowedToPost(String deviceSignatureHash);
    void recordActivity(String deviceSignatureHash);
}
