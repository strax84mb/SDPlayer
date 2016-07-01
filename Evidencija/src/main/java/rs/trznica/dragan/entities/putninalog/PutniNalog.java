package rs.trznica.dragan.entities.putninalog;

import java.util.Date;

public class PutniNalog {

	private Long id;
	
	private Integer redniBroj;
	
	private Long voziloId;
	
	private String vozac;
	
	private String relacija;
	
	private Date datum;
	
	private String vrstaPrevoza;
	
	private String korisnik;
	
	private String posada;
	
	private String ro;
	
	private String adresaGaraze;
	
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
