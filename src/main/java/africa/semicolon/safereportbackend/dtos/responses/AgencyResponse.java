package africa.semicolon.safereportbackend.dtos.responses;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AgencyResponse {
    private String id;
    private String username;
    private String name;
    private String email;
}
