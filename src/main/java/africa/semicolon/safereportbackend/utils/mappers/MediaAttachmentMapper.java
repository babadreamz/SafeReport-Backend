package africa.semicolon.safereportbackend.utils.mappers;

import africa.semicolon.safereportbackend.data.models.MediaAttachment;
import africa.semicolon.safereportbackend.dtos.modeldtos.MediaAttachmentDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MediaAttachmentMapper {
    MediaAttachmentDto mapToDto(MediaAttachment mediaAttachment);
    MediaAttachment mapToEntity(MediaAttachmentDto mediaAttachmentDto);
}
