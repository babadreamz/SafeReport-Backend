package africa.semicolon.safereportbackend.dtos.modeldtos;

import africa.semicolon.safereportbackend.data.models.ReportStatus;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ReportSummary implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String id;
    private String incidentType;
    private String description;
    private LocalDateTime createdTimestamp;
    private String street;
    private String lga;
    private String state;
    private ReportStatus reportStatus;
    private String responderUnitId;
    private String ghostReporterId;
    private boolean publicReport;
}
