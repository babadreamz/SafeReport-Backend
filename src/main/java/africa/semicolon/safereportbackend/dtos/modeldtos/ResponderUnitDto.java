package africa.semicolon.safereportbackend.dtos.modeldtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponderUnitDto {
    private String id;
    private String name;
    private String username;
    private Double baseLatitude;
    private Double baseLongitude;
    private String contactNumber;
    private boolean isActive;
    private boolean isDeleted;
}
