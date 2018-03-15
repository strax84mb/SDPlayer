package rs.trznica.dragan.entities.putninalog;

import rs.trznica.dragan.entities.struja.BasicEntity;

public class PutniNalog extends BasicEntity {
	
	public static final String PUTNICKI = "P";
	public static final String TERETNI = "T";

	private Long redniBroj;
	
	private Long idVozila;
	
	// Moze biti P ili T
	private String namenaVozila;
	
	private String tipVozila;
	
	private String markaVozila;

	private String regOznaka;
	
	private Integer snagaMotora;
	
	private Integer brojSedista;
	
	private Integer tezina;
	
	private Integer nosivost;
	
	private String vozac;
	
	private String relacija;
	
	private String datum;
	
	private String vrstaPrevoza;
	
	private String korisnik;
	
	private String posada;
	
	private String adresaGaraze;
	
	private String mesto;

	public Long getRedniBroj() {
		return redniBroj;
	}

	public void setRedniBroj(Long redniBroj) {
		this.redniBroj = redniBroj;
	}

	public Long getIdVozila() {
		return idVozila;
	}

	public void setIdVozila(Long idVozila) {
		this.idVozila = idVozila;
	}

	public String getNamenaVozila() {
		return namenaVozila;
	}

	public void setNamenaVozila(String namenaVozila) {
		this.namenaVozila = namenaVozila;
	}

	public String getTipVozila() {
		return tipVozila;
	}

	public void setTipVozila(String tipVozila) {
		this.tipVozila = tipVozila;
	}

	public String getMarkaVozila() {
		return markaVozila;
	}

	public void setMarkaVozila(String markaVozila) {
		this.markaVozila = markaVozila;
	}

	public String getRegOznaka() {
		return regOznaka;
	}

	public void setRegOznaka(String regOznaka) {
		this.regOznaka = regOznaka;
	}

	public Integer getSnagaMotora() {
		return snagaMotora;
	}

	public void setSnagaMotora(Integer snagaMotora) {
		this.snagaMotora = snagaMotora;
	}

	public Integer getBrojSedista() {
		return brojSedista;
	}

	public void setBrojSedista(Integer brojSedista) {
		this.brojSedista = brojSedista;
	}

	public Integer getTezina() {
		return tezina;
	}

	public void setTezina(Integer tezina) {
		this.tezina = tezina;
	}

	public Integer getNosivost() {
		return nosivost;
	}

	public void setNosivost(Integer nosivost) {
		this.nosivost = nosivost;
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

	public String getDatum() {
		return datum;
	}

	public void setDatum(String datum) {
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

	@Override
	public String toString() {
		return redniBroj + " - " + datum + " - " + vozac;
	}

}
