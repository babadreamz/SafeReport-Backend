package africa.semicolon.safereportbackend.dtos.responses;


import africa.semicolon.safereportbackend.data.models.ReportStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReportResponse {
    private String id;
    private String incidentType;
    private ReportStatus reportStatus;
    private LocalDateTime timeReceived;
    private String responderUnitId;
    private String message;
}
