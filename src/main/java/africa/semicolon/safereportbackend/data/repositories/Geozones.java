package africa.semicolon.safereportbackend.data.repositories;

import africa.semicolon.safereportbackend.data.models.GeoZone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Geozones extends JpaRepository<GeoZone, String> {
}
