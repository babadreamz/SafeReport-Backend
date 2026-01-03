package africa.semicolon.safereportbackend.dtos.modeldtos;

import africa.semicolon.safereportbackend.data.models.Report;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaAttachmentDto {
    private String id;
    private String fileUrl;
    private String mediaType;
    private String hash;

}
