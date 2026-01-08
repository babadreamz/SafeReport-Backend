package africa.semicolon.safereportbackend.services;

import java.util.Map;
import java.util.Optional;

public interface GeoCodingService {
    Map<String, String> getAddressDetails(double latitude, double longitude);
    void addResponderLocation(String agencyId, String responderId, double lat, double lon);
    void removeResponderLocation(String agencyId, String responderId);
    Optional<String> findNearestResponderId(String agencyId, double incidentLat, double incidentLon);
}