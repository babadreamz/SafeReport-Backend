package africa.semicolon.safereportbackend.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.rest.core.mapping.HttpMethods;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class GeoCodingServiceImpl implements GeoCodingService {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String USER_AGENT = "SafeReportApp/1.0";

    @Override
    public Map<String, String> getAddressDetails(double latitude, double longitude) {
        String url = String.format("https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f", latitude, longitude);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("User-Agent", USER_AGENT);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            if (response.getBody() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> addressBlock = (Map<String, Object>) response.getBody().get("address");

                Map<String, String> result = new HashMap<>();
                if (addressBlock != null) {
                    result.put("street", (String) addressBlock.getOrDefault("road", "Unknown Street"));
                    result.put("lga", (String) addressBlock.getOrDefault("county", addressBlock.getOrDefault("city", "Unknown LGA")));
                    result.put("state", (String) addressBlock.getOrDefault("state", "Unknown State"));
                    return result;
                }
            }
        } catch (Exception e) {
            log.error("Geocoding failed for coordinates: {}, {}", latitude, longitude, e);
        }
        return Map.of("street", "Unknown", "lga", "Unknown", "state", "Unknown");
    }
}
