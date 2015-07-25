package rs.trznica.dragan.dao;

import org.springframework.data.repository.CrudRepository;

import rs.trznica.dragan.entities.tankovanje.Potrosac;

public interface PotrosacDao extends CrudRepository<Potrosac, Long> {

}
