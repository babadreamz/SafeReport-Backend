package africa.semicolon.safereportbackend.dtos.responses;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
public class AgencyResponse implements Serializable {
//    @Serial
//    private static final long serialVersionUID = 1L;
    private String id;
    private String username;
    private String name;
    private String email;
}
