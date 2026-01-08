package africa.semicolon.safereportbackend.utils.mappers;

import africa.semicolon.safereportbackend.data.models.ResponderUnit;
import africa.semicolon.safereportbackend.dtos.modeldtos.ResponderUnitDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ResponderUnitMapper {
    ResponderUnitDto mapToDto(ResponderUnit responderUnit);
    List<ResponderUnitDto> mapToList(List<ResponderUnit> responderUnits);
}
