package rs.trznica.dragan.dto.tankovanje;

import java.util.Date;

import rs.trznica.dragan.entities.putninalog.PutniNalog;
import rs.trznica.dragan.entities.tankovanje.Potrosac;
import rs.trznica.dragan.forms.support.DateUtils;
import rs.trznica.dragan.forms.support.DecimalFormater;

public class PutniNalogDto extends BaseDto<PutniNalog> {

	private String redniBroj;

	private Potrosac vozilo;
	
	private String vozac;
	
	private String relacija;
	
	private Date datum;
	
	private String vrstaPrevoza;
	
	private String korisnik;
	
	private String posada;
	
	private String regOznaka;
	
	private String adresaGaraze;
	
	private String mesto;

	public PutniNalogDto(String redniBroj, Potrosac vozilo, String vozac,
			String relacija, Date datum, String vrstaPrevoza, String korisnik,
			String posada, String regOznaka, String adresaGaraze, String mesto) {
		super();
		this.redniBroj = redniBroj;
		this.vozilo = vozilo;
		this.vozac = vozac;
		this.relacija = relacija;
		this.datum = datum;
		this.vrstaPrevoza = vrstaPrevoza;
		this.korisnik = korisnik;
		this.posada = posada;
		this.regOznaka = regOznaka;
		this.adresaGaraze = adresaGaraze;
		this.mesto = mesto;
	}

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

	public String getRegOznaka() {
		return regOznaka;
	}

	public String getAdresaGaraze() {
		return adresaGaraze;
	}

	public String getMesto() {
		return mesto;
	}

	@Override
	public PutniNalog createEntityFromData() {
		PutniNalog nalog = new PutniNalog();
		nalog.setRedniBroj(Long.valueOf(redniBroj));
		nalog.setNamenaVozila(vozilo.getTeretnjak() ? PutniNalog.TERETNI : PutniNalog.PUTNICKI);
		nalog.setTipVozila(vozilo.getTip());
		nalog.setMarkaVozila(vozilo.getMarka());
		nalog.setRegOznaka(regOznaka);
		if (vozilo.getTeretnjak()) {
			nalog.setTezina(DecimalFormater.formatFromLong(vozilo.getTezina().longValue(), 3));
			nalog.setNosivost(DecimalFormater.formatFromLong(vozilo.getNosivost().longValue(), 3));
			nalog.setPosada(posada);
		} else {
			nalog.setSnagaMotora(vozilo.getSnagaMotora());
			nalog.setBrojSedista(vozilo.getBrojSedista());
			nalog.setKorisnik(korisnik);
		}
		nalog.setVozac(vozac);
		nalog.setRelacija(relacija);
		nalog.setDatum(DateUtils.toTimestamp(datum));
		nalog.setVrstaPrevoza(vrstaPrevoza);
		nalog.setAdresaGaraze(adresaGaraze);
		nalog.setMesto(mesto);
		return nalog;
	}

}
