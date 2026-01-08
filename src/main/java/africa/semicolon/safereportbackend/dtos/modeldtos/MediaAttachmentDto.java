package africa.semicolon.safereportbackend.dtos.modeldtos;

import africa.semicolon.safereportbackend.data.models.EvidenceStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MediaAttachmentDto {
    private String id;
    private String fileUrl;
    private String mediaType;
    private String hash;
    private LocalDateTime uploadedAt;
    private EvidenceStatus evidenceStatus;
    private String flagReason;
}
