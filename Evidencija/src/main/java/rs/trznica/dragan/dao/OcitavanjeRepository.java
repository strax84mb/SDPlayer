package rs.trznica.dragan.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.trznica.dragan.entities.struja.OcitavanjeSql;

import java.util.List;
import java.util.Optional;

public interface OcitavanjeRepository extends JpaRepository<OcitavanjeSql, Long> {

    Optional<OcitavanjeSql> findByBrojiloEDAndMesec(String brojiloED, String mesec);

    Long countByBrojiloId(Long brojiloId);

    List<OcitavanjeSql> findByBrojiloIdInAndMesecBetween(List<Long> ids, String startMonth, String endMonth);
}
