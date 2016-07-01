package rs.trznica.dragan.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import rs.trznica.dragan.entities.tankovanje.Potrosac;

public interface PotrosacDao extends CrudRepository<Potrosac, Long> {
	
	@Query("select p from Potrosac p where p.vozilo=true and p.aktivan=true order by p.id asc")
	Iterable<Potrosac> listVehicles();
}
