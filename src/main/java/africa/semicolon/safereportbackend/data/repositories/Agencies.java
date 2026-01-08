package africa.semicolon.safereportbackend.data.repositories;

import africa.semicolon.safereportbackend.data.models.Agency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface Agencies extends JpaRepository<Agency,String> {

    @Query("SELECT agency FROM Agency agency LEFT JOIN FETCH agency.responderUnits WHERE agency.id = :id")
    Optional<Agency> findByIdWithResponderUnit(@Param("id") String agencyId);
    Optional<Agency> findByUsername(String username);
}
