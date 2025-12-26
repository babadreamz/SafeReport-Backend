package africa.semicolon.safereportbackend.data.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class MediaAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String reportId;
    private String fileUrl;
    private String mediaType;
    private String hash;
}
