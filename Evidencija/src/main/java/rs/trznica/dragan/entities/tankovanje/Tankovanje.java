package rs.trznica.dragan.entities.tankovanje;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Tankovanje {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@ManyToOne
	private Potrosac potrosac;

	@Column(nullable = false)
	private Long jedCena = 0L;

	@Column(nullable = false)
	private Long kolicina = 0L;

	@Column(nullable = false)
	private Date datum = new Date();

	@Column(length = 10, nullable = false)
	private String mesec = "";

	@Column(nullable = false)
	private Long kilometraza;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Potrosac getPotrosac() {
		return potrosac;
	}

	public void setPotrosac(Potrosac potrosac) {
		this.potrosac = potrosac;
	}

	public Long getJedCena() {
		return jedCena;
	}

	public void setJedCena(Long jedCena) {
		this.jedCena = jedCena;
	}

	public Long getKolicina() {
		return kolicina;
	}

	public void setKolicina(Long kolicina) {
		this.kolicina = kolicina;
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

	public Long getKilometraza() {
		return kilometraza;
	}

	public void setKilometraza(Long kilometraza) {
		this.kilometraza = kilometraza;
	}

}
