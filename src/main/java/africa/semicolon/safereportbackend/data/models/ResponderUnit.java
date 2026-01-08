package africa.semicolon.safereportbackend.data.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class ResponderUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(unique = true,nullable = false)
    private String name;
    @Column(unique = true,nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @ManyToOne(optional = false)
    private Agency agency;
    private Double baseLatitude;
    private Double baseLongitude;
    private String contactNumber;
    private boolean isActive;
    private List<Role> roles = new ArrayList<>();
    private boolean isDeleted;
}
