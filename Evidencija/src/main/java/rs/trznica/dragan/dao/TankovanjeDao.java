package rs.trznica.dragan.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import rs.trznica.dragan.entities.tankovanje.Tankovanje;

public interface TankovanjeDao extends CrudRepository<Tankovanje, Long> {

	@Query("select t from Tankovanje t where t.mesec >= :fromMonth and t.mesec <= :tilMonth order by t.mesec asc, t.potrosac.id asc")
	Iterable<Tankovanje> listInInterval(@Param("fromMonth") String fromMonth, @Param("tilMonth") String tilMonth);

	@Query("select t from Tankovanje t where t.mesec >= :fromMonth order by t.mesec asc, t.potrosac.id asc")
	Iterable<Tankovanje> listFromMonth(@Param("fromMonth") String fromMonth);

	@Query("select t from Tankovanje t where t.mesec <= :tilMonth order by t.mesec asc, t.potrosac.id asc")
	Iterable<Tankovanje> listTilMonth(@Param("tilMonth") String tilMonth);

	@Query("select t from Tankovanje t where t.potrosac.id = :potrosacId and t.mesec >= :fromMonth and t.mesec <= :tilMonth order by t.mesec asc, t.datum asc")
	Iterable<Tankovanje> listInIntervalForConsumer(@Param("potrosacId") Long potrosacId,  @Param("fromMonth") String fromMonth, 
			@Param("tilMonth") String tilMonth);

	@Query("select t from Tankovanje t where t.potrosac.id = :potrosacId order by t.mesec asc")
	Iterable<Tankovanje> listForConsumer(@Param("potrosacId") Long potrosacId);

	@Query("select t from Tankovanje t where t.potrosac.id = :potrosacId and t.mesec >= :fromMonth order by t.mesec asc")
	Iterable<Tankovanje> listFromMonthForConsumer(@Param("potrosacId") Long potrosacId,  @Param("fromMonth") String fromMonth);

	@Query("select t from Tankovanje t where t.potrosac.id = :potrosacId and t.mesec <= :tilMonth order by t.mesec asc")
	Iterable<Tankovanje> listTilMonthForConsumer(@Param("potrosacId") Long potrosacId,  @Param("tilMonth") String tilMonth);

	@Query("select t from Tankovanje t where t.potrosac.id in :potrosacIds order by t.mesec asc")
	Iterable<Tankovanje> listForConsumers(@Param("potrosacIds") List<Long> potrosacIds);

	@Query("select t from Tankovanje t where t.potrosac.id in :potrosacIds and t.mesec >= :fromMonth and t.mesec <= :tilMonth order by t.mesec asc")
	Iterable<Tankovanje> listInIntervalForConsumers(@Param("potrosacIds") List<Long> potrosacIds,  @Param("fromMonth") String fromMonth, 
			@Param("tilMonth") String tilMonth);

	@Query("select t from Tankovanje t where t.potrosac.id in :potrosacIds and t.mesec >= :fromMonth order by t.mesec asc")
	Iterable<Tankovanje> listFromMonthForConsumers(@Param("potrosacIds") List<Long> potrosacIds,  @Param("fromMonth") String fromMonth);

	@Query("select t from Tankovanje t where t.potrosac.id in :potrosacIds and t.mesec <= :tilMonth order by t.mesec asc")
	Iterable<Tankovanje> listTilMonthForConsumers(@Param("potrosacIds") List<Long> potrosacIds,  @Param("tilMonth") String tilMonth);

	@Query("select t from Tankovanje t where t.potrosac.id = :potrosacId and t.mesec < :beforeMonth order by t.datum desc")
	List<Tankovanje> getLastFill(@Param("potrosacId") Long potrosacId, @Param("beforeMonth") String beforeMonth, Pageable pageable);
}
