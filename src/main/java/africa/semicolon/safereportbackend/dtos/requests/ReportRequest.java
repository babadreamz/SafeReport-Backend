package africa.semicolon.safereportbackend.dtos.requests;

import africa.semicolon.safereportbackend.dtos.modeldtos.GeoZoneDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReportRequest {
    private String incidentType;
    private String description;
    private double latitude;
    private double longitude;
    private String deviceSignature;
}
