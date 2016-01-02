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

	@Override
	public String toString() {
		return (Boolean.TRUE.equals(vozilo)) ? regOznaka + " - " + marka + " " + tip : tip ;
	}

}
