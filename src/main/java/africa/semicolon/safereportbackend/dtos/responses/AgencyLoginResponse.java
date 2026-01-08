package africa.semicolon.safereportbackend.dtos.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class AgencyLoginResponse {
    private String token;
    private String agencyId;
    private String agencyName;
    private String agencyUsername;
}
