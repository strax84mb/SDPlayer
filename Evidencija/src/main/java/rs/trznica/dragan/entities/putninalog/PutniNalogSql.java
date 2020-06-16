package rs.trznica.dragan.entities.putninalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.trznica.dragan.forms.support.DateUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "PutniNalog")
public class PutniNalogSql {

    public static final String PUTNICKI = "P";
    public static final String TERETNI = "T";

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name = "redni_broj", nullable = false)
    private Long redniBroj;

    @Column(name = "vozilo_id", nullable = false)
    private Long idVozila;

    // Moze biti P ili T
    @Column(name = "vozilo_namena", nullable = false, length = 1)
    private String namenaVozila;

    @Column(name = "vozilo_tip", nullable = false, length = 50)
    private String tipVozila;

    @Column(name = "vozilo_marka", nullable = false, length = 50)
    private String markaVozila;

    @Column(name = "reg_oznaka", nullable = false, length = 12)
    private String regOznaka;

    @Column(name = "snaga_motora")
    private Integer snagaMotora;

    @Column(name = "broj_sedista")
    private Integer brojSedista;

    @Column(name = "tezina")
    private Integer tezina;

    @Column(name = "nosivost")
    private Integer nosivost;

    @Column(name = "vozac", nullable = false, length = 65)
    private String vozac;

    @Column(name = "relacija", nullable = false, length = 50)
    private String relacija;

    @Column(name = "datum", nullable = false, length = 8)
    private String datum;

    @Column(name = "vrsta_prevoza", nullable = false, length = 50)
    private String vrstaPrevoza;

    @Column(name = "korisnik", length = 100)
    private String korisnik;

    @Column(name = "posada")
    private String posada;

    @Column(name = "radna_organizacija", nullable = false, length = 50)
    private String radnaOrganizacija;

    @Column(name = "adresa_garaze", nullable = false, length = 50)
    private String adresaGaraze;

    @Column(name = "mesto", nullable = false, length = 50)
    private String mesto;

    @Override
    public String toString() {
        return redniBroj + " - " + DateUtils.getReadableDate(datum) + " - " + vozac;
    }
}
