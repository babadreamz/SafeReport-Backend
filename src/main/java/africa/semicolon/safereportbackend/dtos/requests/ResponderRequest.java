package africa.semicolon.safereportbackend.dtos.requests;

import africa.semicolon.safereportbackend.configs.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponderRequest {
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String username;
    @NotNull
    @ValidPassword
    private String password;
    private String agencyName;
    private Double baseLatitude;
    private Double baseLongitude;
    private String contactNumber;
}
