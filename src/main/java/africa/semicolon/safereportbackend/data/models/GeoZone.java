package africa.semicolon.safereportbackend.data.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class GeoZone {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String lga;
    private String area;
    private String street;
    private String riskScore;
}
