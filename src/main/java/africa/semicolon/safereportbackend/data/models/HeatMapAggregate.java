package africa.semicolon.safereportbackend.data.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class HeatMapAggregate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private int incidentCount24hrs;
    private Long incidentCount7days;
}
