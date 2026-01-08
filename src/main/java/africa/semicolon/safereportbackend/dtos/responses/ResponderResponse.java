package africa.semicolon.safereportbackend.dtos.responses;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponderResponse {
    private String id;
    private String name;
    private String username;
    private String agencyName;
    private Double baseLatitude;
    private Double baseLongitude;
    private String contactNumber;
    private boolean isActive;
}
