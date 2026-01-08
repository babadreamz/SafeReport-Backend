package africa.semicolon.safereportbackend.services;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeoCodingServiceImpl implements GeoCodingService {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String USER_AGENT = "SafeReportApp/1.0";
    @Autowired
    private final RedisTemplate<String,Object> redisTemplate;

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

    @Override
    public void addResponderLocation(String agencyId, String responderId, double lat, double lon) {
        String key = "agency:" + agencyId + ":locations";
        redisTemplate.opsForGeo().add(key,new Point(lon,lat),responderId);
    }

    @Override
    public void removeResponderLocation(String agencyId, String responderId) {
        String key = "agency:" + agencyId + ":locations";
        redisTemplate.opsForGeo().remove(key, responderId);
    }

    @Override
    public Optional<String> findNearestResponderId(String agencyId, double incidentLat, double incidentLon) {
        String key = "agency:" + agencyId + ":locations";
        Distance radius = new Distance(50, Metrics.KILOMETERS);
        Circle searchArea = new Circle(new Point(incidentLon, incidentLat), radius);

        RedisGeoCommands.GeoRadiusCommandArgs args =
                RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                        .includeDistance()
                        .sortAscending()
                        .limit(1);
        GeoResults<RedisGeoCommands.GeoLocation<Object>> results =
                redisTemplate.opsForGeo().radius(key, searchArea, args);
        if (results != null && !results.getContent().isEmpty()) {
            String responderId = (String) results.getContent().getFirst().getContent().getName();
            return Optional.of(responderId);
        }
        return Optional.empty();
    }
}
