package rs.trznica.dragan.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.trznica.dragan.entities.putninalog.PutniNalogSql;

import java.util.List;
import java.util.Optional;

public interface PutniNalogRepository extends JpaRepository<PutniNalogSql, Long> {

    Optional<PutniNalogSql> findByIdVozilaAndRedniBroj(Long idVozila, Long redniBroj);

    List<PutniNalogSql> findByIdVozila(Long idVozila);

    List<PutniNalogSql> findByIdVozilaAndDatumBetween(Long idVozila, String pocetniDatum, String zavrsniDatum);

    @Query("select max(pn.redniBroj) from PutniNalog pn where pn.idVozila = :idVozila")
    Long getMaxRB(@Param("idVozila") Long idVozila);
}
