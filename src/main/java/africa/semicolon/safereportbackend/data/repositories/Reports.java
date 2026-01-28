package africa.semicolon.safereportbackend.data.repositories;

import africa.semicolon.safereportbackend.data.models.Report;
import africa.semicolon.safereportbackend.data.models.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Reports extends JpaRepository<Report,String> {
    List<Report> findByResponderUnitIdAndReportStatusOrderByCreatedTimestampDesc(
            String responderUnitId,
            ReportStatus status
    );
    List<Report> findByAgencyIdAndReportStatusOrderByCreatedTimestampDesc(String agencyId, ReportStatus reportStatus);

    List<Report> findByPublicReportTrue();
}
