package africa.semicolon.safereportbackend.data.repositories;

import africa.semicolon.safereportbackend.data.models.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Reports extends JpaRepository<Report,String> {
}
