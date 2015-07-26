package rs.trznica.dragan.dao;

import org.springframework.data.repository.CrudRepository;

import rs.trznica.dragan.entities.tankovanje.Tankovanje;

public interface TankovanjeDao extends CrudRepository<Tankovanje, Long> {

}
