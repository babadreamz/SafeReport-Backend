package africa.semicolon.safereportbackend.data.repositories;

import africa.semicolon.safereportbackend.data.models.DispatchLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispatchLogs extends JpaRepository<DispatchLog,String> {
}
