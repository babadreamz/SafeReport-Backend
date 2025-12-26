package africa.semicolon.safereportbackend.data.repositories;

import africa.semicolon.safereportbackend.data.models.HeatMapAggregate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeatMapAggregates extends JpaRepository<HeatMapAggregate,String> {

}
