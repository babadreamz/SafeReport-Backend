package africa.semicolon.safereportbackend.data.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class DispatchLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String reportId;
    private String responderUnitId;
    private String actionTaken;
    private LocalDateTime arrivalTime;
    private String outcome;
    private boolean isDeleted;
}
