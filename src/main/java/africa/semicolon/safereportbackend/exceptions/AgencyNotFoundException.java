package africa.semicolon.safereportbackend.exceptions;

public class AgencyNotFoundException extends RuntimeException {
    public AgencyNotFoundException(String message) {
        super(message);
    }
}
