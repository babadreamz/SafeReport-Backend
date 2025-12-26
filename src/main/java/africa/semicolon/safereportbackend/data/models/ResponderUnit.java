package africa.semicolon.safereportbackend.data.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ResponderUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Agency agency;
    private String assignedZone;
    private String contactNumber;
    private boolean isActive;
}
