package rs.trznica.dragan.entities.tankovanje;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import rs.trznica.dragan.entities.support.GorivoType;

@Entity
public class Potrosac {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(unique = true, length = 11)
	private String regOznaka;

	@Column(length = 15, nullable = true)
	private String marka;

	@Column(length = 15, nullable = true)
	private String tip;

	@Column(nullable = false)
	private Boolean vozilo = false;

	@Column(nullable = false)
	private Boolean teretnjak = false;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private GorivoType gorivo = GorivoType.BMB;

	@Column(nullable = false)
	private Boolean aktivan = true;

	@Column(nullable = false, name = "meri_km")
	private Boolean meriKm = true;
	
	@Column(nullable = true, name = "podrucje")
	private String podrucje;
	
	@Column(nullable = false, name = "broj_sedista")
	private Integer brojSedista = 0;

	@Column(nullable = false, name = "snaga_motora")
	private Integer snagaMotora = 0;
	
	@Column(nullable = false, name = "tezina")
	private Integer tezina = 0;
	
	@Column(nullable = false, name = "nosivost")
	private Integer nosivost = 0;
	
	@Column(nullable = true, name = "vozaci")
	private String vozaci;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRegOznaka() {
		return regOznaka;
	}

	public void setRegOznaka(String regOznaka) {
		this.regOznaka = regOznaka;
	}

	public String getMarka() {
		return marka;
	}

	public void setMarka(String marka) {
		this.marka = marka;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public Boolean getVozilo() {
		return vozilo;
	}

	public void setVozilo(Boolean vozilo) {
		this.vozilo = vozilo;
	}

	public Boolean getTeretnjak() {
		return teretnjak;
	}

	public void setTeretnjak(Boolean teretnjak) {
		this.teretnjak = teretnjak;
	}

	public GorivoType getGorivo() {
		return gorivo;
	}

	public void setGorivo(GorivoType gorivo) {
		this.gorivo = gorivo;
	}

	public Boolean getAktivan() {
		return aktivan;
	}

	public void setAktivan(Boolean aktivan) {
		this.aktivan = aktivan;
	}

	public Boolean getMeriKm() {
		return meriKm;
	}

	public void setMeriKm(Boolean meriKm) {
		this.meriKm = meriKm;
	}

	public String getPodrucje() {
		return podrucje;
	}

	public void setPodrucje(String podrucje) {
		this.podrucje = podrucje;
	}

	public Integer getBrojSedista() {
		return brojSedista;
	}

	public void setBrojSedista(Integer brojSedista) {
		this.brojSedista = brojSedista;
	}

	public Integer getSnagaMotora() {
		return snagaMotora;
	}

	public void setSnagaMotora(Integer snagaMotora) {
		this.snagaMotora = snagaMotora;
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

	public String getVozaci() {
		return vozaci;
	}

	public void setVozaci(String vozaci) {
		this.vozaci = vozaci;
	}

	@Override
	public String toString() {
		return (Boolean.TRUE.equals(vozilo)) ? id + " - " + regOznaka + " - " + marka + " " + tip : id + " - " + tip ;
	}

}
