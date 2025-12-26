package africa.semicolon.safereportbackend.dtos.modeldtos;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GhostReporterDto {
    private String id;
    private String deviceSignatureHash;
}
