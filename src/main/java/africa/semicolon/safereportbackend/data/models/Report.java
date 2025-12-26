package africa.semicolon.safereportbackend.data.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String incidentType;
    private String description;
    private LocalDateTime createdTimestamp;
    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;
    private double latitude;
    private double longitude;
    @ManyToOne
    @JoinColumn(name = "geo_zone_id")
    private GeoZone geoZone;
    private String responderUnitId;
    private String ghostReporterId;
}
