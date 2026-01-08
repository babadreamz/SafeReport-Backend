package africa.semicolon.safereportbackend.dtos.requests;

import africa.semicolon.safereportbackend.configs.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AgencyRequest {
    @NotNull
    @NotBlank
    private String name;
    @Email
    private String email;
    @NotNull
    @NotBlank
    private String username;
    @NotNull
    @ValidPassword
    private String password;
}