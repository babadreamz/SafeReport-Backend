package africa.semicolon.safereportbackend.utils.mappers;

import africa.semicolon.safereportbackend.data.models.GhostReporter;
import africa.semicolon.safereportbackend.dtos.modeldtos.GhostReporterDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GhostReporterMapper {
    GhostReporterDto mapToDto(GhostReporter ghostReporter);
    GhostReporter mapToEntity(GhostReporterDto ghostReporterDto);

}
