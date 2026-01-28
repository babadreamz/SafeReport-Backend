package africa.semicolon.safereportbackend.dtos.requests;

import africa.semicolon.safereportbackend.data.models.LocationSource;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ReportRequest {
    private String incidentType;
    private String description;
    private boolean isHappeningNow;
    private Double deviceLatitude;
    private Double deviceLongitude;
    private Double incidentLatitude;
    private Double incidentLongitude;
    private LocationSource locationSource;
    private String agencyName;
    private boolean publicReport;
}
