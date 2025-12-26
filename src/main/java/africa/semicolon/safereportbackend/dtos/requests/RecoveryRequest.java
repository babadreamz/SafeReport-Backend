package africa.semicolon.safereportbackend.dtos.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecoveryRequest {
    private String recoveryPhrase;
}
