package africa.semicolon.safereportbackend.data.repositories;

import africa.semicolon.safereportbackend.data.models.GhostReporter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GhostReporters extends JpaRepository<GhostReporter,String> {
    Optional<GhostReporter> findByRecoveryPhraseHash(String recoveryPhraseHash);

    Optional<GhostReporter> findByDeviceSignatureHash(String deviceSignatureHash);
}
