package africa.semicolon.safereportbackend.dtos.modeldtos;

import africa.semicolon.safereportbackend.data.models.ReportStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ReportSummary implements Serializable {
    private String id;
    private String incidentType;
    private String description;
    private LocalDateTime createdTimestamp;
    private String street;
    private String lga;
    private String state;
    private ReportStatus reportStatus;
    private String responderUnitId;
}
