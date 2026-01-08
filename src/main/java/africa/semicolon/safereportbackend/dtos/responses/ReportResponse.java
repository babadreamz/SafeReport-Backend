package africa.semicolon.safereportbackend.dtos.responses;


import africa.semicolon.safereportbackend.data.models.PriorityLevel;
import africa.semicolon.safereportbackend.data.models.ReportStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReportResponse {
    private String reportId;
    private ReportStatus status;
    private PriorityLevel assignedPriority;
    private String resolvedAddress;
    private LocalDateTime timeReceived;
    private String message;
    private String ghostReporterId;
    private String responderUnitId;
}
