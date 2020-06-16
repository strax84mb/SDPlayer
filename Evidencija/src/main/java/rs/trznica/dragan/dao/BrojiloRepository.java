package rs.trznica.dragan.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.trznica.dragan.entities.struja.BrojiloSql;

import java.util.Optional;

public interface BrojiloRepository extends JpaRepository<BrojiloSql, Long> {

    Optional<BrojiloSql> findByBrojAndEd(String broj, String ed);
}
