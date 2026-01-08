package africa.semicolon.safereportbackend.data.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Agency {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(unique = true, nullable = false)
    private String name;
    private String email;
    private boolean isDeleted;
    @Column(unique = true)
    private String username;
    private String password;
    @OneToMany(mappedBy = "agency", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ResponderUnit> responderUnits = new ArrayList<>();
    private List<Role> roles = new ArrayList<>();
}