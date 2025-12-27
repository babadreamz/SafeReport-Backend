package africa.semicolon.safereportbackend.services;

import java.util.Map;

public interface GeoCodingService {
    Map<String, String> getAddressDetails(double latitude, double longitude);
}