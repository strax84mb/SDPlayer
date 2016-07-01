package rs.trznica.dragan.dto.tankovanje;

import java.util.Date;

import rs.trznica.dragan.entities.putninalog.PutniNalog;
import rs.trznica.dragan.entities.tankovanje.Potrosac;

public class PutniNalogDto extends BaseDto<PutniNalog> {

	private String redniBroj;
	
	private Potrosac vozilo;
	
	private String vozac;
	
	private String relacija;
	
	private Date datum;
	
	private String vrstaPrevoza;
	
	private String korisnik;
	
	private String posada;
	
	private String ro;
	
	private String adresaGaraze;
	
	private String mesto;

	public String getRedniBroj() {
		return redniBroj;
	}

	public Potrosac getVozilo() {
		return vozilo;
	}

	public String getVozac() {
		return vozac;
	}

	public String getRelacija() {
		return relacija;
	}

	public Date getDatum() {
		return datum;
	}

	public String getVrstaPrevoza() {
		return vrstaPrevoza;
	}

	public String getKorisnik() {
		return korisnik;
	}

	public String getPosada() {
		return posada;
	}

	public String getRo() {
		return ro;
	}

	public String getAdresaGaraze() {
		return adresaGaraze;
	}

	public String getMesto() {
		return mesto;
	}

	public PutniNalogDto(String redniBroj, Potrosac vozilo, String vozac,
			String relacija, Date datum, String vrstaPrevoza, String korisnik,
			String posada, String ro, String adresaGaraze, String mesto) {
		super();
		this.redniBroj = trim(redniBroj);
		this.vozilo = vozilo;
		this.vozac = trim(vozac);
		this.relacija = trim(relacija);
		this.datum = datum;
		this.vrstaPrevoza = trim(vrstaPrevoza);
		this.korisnik = trim(korisnik);
		this.posada = trim(posada);
		this.ro = trim(ro);
		this.adresaGaraze = trim(adresaGaraze);
		this.mesto = trim(mesto);
	}

	@Override
	public PutniNalog createEntityFromData() {
		PutniNalog nalog = new PutniNalog();
		nalog.setRedniBroj(Integer.getInteger(redniBroj));
		nalog.setVoziloId(vozilo.getId());
		nalog.setVozac(vozac);
		nalog.setRelacija(relacija);
		nalog.setDatum(datum);
		nalog.setVrstaPrevoza(vrstaPrevoza);
		nalog.setAdresaGaraze(adresaGaraze);
		nalog.setMesto(mesto);
		nalog.setRo(ro);
		if (vozilo.getTeretnjak()) {
			nalog.setPosada(posada);
		} else {
			nalog.setKorisnik(korisnik);
		}
		return nalog;
	}

}
