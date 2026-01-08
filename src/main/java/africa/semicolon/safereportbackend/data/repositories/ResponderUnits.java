package africa.semicolon.safereportbackend.data.repositories;

import africa.semicolon.safereportbackend.data.models.ResponderUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResponderUnits extends JpaRepository<ResponderUnit,String> {

    Optional<ResponderUnit> findByUsername(String username);
    List<ResponderUnit> findByAgencyIdAndIsActiveTrue(String agencyId);
}
