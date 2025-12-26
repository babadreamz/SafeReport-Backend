package africa.semicolon.safereportbackend.dtos.responses;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GhostReporterResponse {
    private String id;
    private String recoveryPhrase;
}
