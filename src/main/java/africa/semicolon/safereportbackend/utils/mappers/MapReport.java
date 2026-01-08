package africa.semicolon.safereportbackend.utils.mappers;

import africa.semicolon.safereportbackend.data.models.Report;
import africa.semicolon.safereportbackend.dtos.modeldtos.ReportSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MapReport {
    @Mapping(source = "responderUnitId", target = "responderUnitId")
    ReportSummary mapToSummary(Report report);
    List<ReportSummary> mapToSummaryList(List<Report> reports);
}
