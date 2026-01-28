package africa.semicolon.safereportbackend.data.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @Enumerated(EnumType.STRING)
    private PriorityLevel priorityLevel;
    private Double calculatedDistanceMetadata;
    private Double incidentLatitude;
    private Double incidentLongitude;
    private Double deviceLatitude;
    private Double deviceLongitude;
    @Enumerated(EnumType.STRING)
    private LocationSource locationSource;
    private String street;
    private String lga;
    private String state;
    private String agencyId;
    private String responderUnitId;
    private String ghostReporterId;
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MediaAttachment> mediaAttachments = new ArrayList<>();
    private boolean isDeleted;
    private boolean publicReport;
}