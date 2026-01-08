package africa.semicolon.safereportbackend.data.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class MediaAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;
    private String fileUrl;
    private String mediaType;
    private String hash;
    private boolean isDeleted;
    private LocalDateTime uploadedAt;
    @Enumerated(EnumType.STRING)
    private EvidenceStatus evidenceStatus;
    private String flagReason;
}
