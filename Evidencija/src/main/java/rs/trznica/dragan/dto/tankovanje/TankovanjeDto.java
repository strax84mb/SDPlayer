package rs.trznica.dragan.dto.tankovanje;

import java.util.Date;

public class TankovanjeDto {

	private Long potrosacId;

	private Boolean vozilo;

	private Date datum;

	private String mesec;

	private String kolicina;

	private String jedCena;

	private String kilometraza;

	public Long getPotrosacId() {
		return potrosacId;
	}

	public Boolean getVozilo() {
		return vozilo;
	}

	public Date getDatum() {
		return datum;
	}

	public String getMesec() {
		return mesec;
	}

	public String getKolicina() {
		return kolicina;
	}

	public String getJedCena() {
		return jedCena;
	}

	public String getKilometraza() {
		return kilometraza;
	}

	public TankovanjeDto() {}

	public TankovanjeDto(Long potrosacId, Boolean vozilo, Date datum, String mesec,
			String kolicina, String jedCena, String kilometraza) {
		this.potrosacId = potrosacId;
		this.vozilo = vozilo;
		this.datum = datum;
		this.mesec = mesec;
		this.kolicina = kolicina;
		this.jedCena = jedCena;
		this.kilometraza = kilometraza;
	}

}
