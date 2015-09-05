package rs.trznica.dragan.dto.tankovanje;

import java.util.Date;

public class TankovanjeDto {

	private Long potrosacId;

	private Date datum;

	private String mesec;

	private String kolicina;

	private String jedCena;

	private String kilometraza;

	public Long getPotrosacId() {
		return potrosacId;
	}

	public void setPotrosacId(Long potrosacId) {
		this.potrosacId = potrosacId;
	}

	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}

	public String getMesec() {
		return mesec;
	}

	public void setMesec(String mesec) {
		this.mesec = mesec;
	}

	public String getKolicina() {
		return kolicina;
	}

	public void setKolicina(String kolicina) {
		this.kolicina = kolicina;
	}

	public String getJedCena() {
		return jedCena;
	}

	public void setJedCena(String jedCena) {
		this.jedCena = jedCena;
	}

	public String getKilometraza() {
		return kilometraza;
	}

	public void setKilometraza(String kilometraza) {
		this.kilometraza = kilometraza;
	}

	public TankovanjeDto() {}

	public TankovanjeDto(Long potrosacId, Date datum, String mesec,
			String kolicina, String jedCena, String kilometraza) {
		this.potrosacId = potrosacId;
		this.datum = datum;
		this.mesec = mesec;
		this.kolicina = kolicina;
		this.jedCena = jedCena;
		this.kilometraza = kilometraza;
	}

}
