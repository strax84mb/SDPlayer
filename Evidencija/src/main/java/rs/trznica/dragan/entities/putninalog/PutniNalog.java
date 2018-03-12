package rs.trznica.dragan.entities.putninalog;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PutniNalog {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false, name = "redni_broj")
	private Integer redniBroj;
	
	@Column(nullable = false, name = "vozilo_id")
	private Long voziloId;
	
	@Column(nullable = false, name = "vozac", length = 40)
	private String vozac;
	
	@Column(nullable = false, name = "relacija", length = 100)
	private String relacija;
	
	@Column(nullable = false)
	private Date datum;
	
	@Column(nullable = false, name = "vrsta_prevoza", length = 2)
	private String vrstaPrevoza;
	
	@Column(nullable = false, name = "korisnik", length = 40)
	private String korisnik;
	
	@Column(nullable = false, name = "posada", length = 150)
	private String posada;
	
	@Column(nullable = false, name = "ro", length = 10)
	private String ro;
	
	@Column(nullable = false, name = "adresa_garaze", length = 60)
	private String adresaGaraze;
	
	@Column(nullable = false, name = "mesto", length = 30)
	private String mesto;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getRedniBroj() {
		return redniBroj;
	}

	public void setRedniBroj(Integer redniBroj) {
		this.redniBroj = redniBroj;
	}

	public Long getVoziloId() {
		return voziloId;
	}

	public void setVoziloId(Long voziloId) {
		this.voziloId = voziloId;
	}

	public String getVozac() {
		return vozac;
	}

	public void setVozac(String vozac) {
		this.vozac = vozac;
	}

	public String getRelacija() {
		return relacija;
	}

	public void setRelacija(String relacija) {
		this.relacija = relacija;
	}

	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}

	public String getVrstaPrevoza() {
		return vrstaPrevoza;
	}

	public void setVrstaPrevoza(String vrstaPrevoza) {
		this.vrstaPrevoza = vrstaPrevoza;
	}

	public String getKorisnik() {
		return korisnik;
	}

	public void setKorisnik(String korisnik) {
		this.korisnik = korisnik;
	}

	public String getPosada() {
		return posada;
	}

	public void setPosada(String posada) {
		this.posada = posada;
	}

	public String getRo() {
		return ro;
	}

	public void setRo(String ro) {
		this.ro = ro;
	}

	public String getAdresaGaraze() {
		return adresaGaraze;
	}

	public void setAdresaGaraze(String adresaGaraze) {
		this.adresaGaraze = adresaGaraze;
	}

	public String getMesto() {
		return mesto;
	}

	public void setMesto(String mesto) {
		this.mesto = mesto;
	}
}
