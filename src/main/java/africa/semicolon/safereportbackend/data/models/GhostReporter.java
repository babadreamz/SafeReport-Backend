package africa.semicolon.safereportbackend.data.models;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
public class GhostReporter {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String deviceSignatureHash;
    private String recoveryPhraseHash;
    private boolean isDeleted;
}
