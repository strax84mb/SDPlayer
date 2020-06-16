package rs.trznica.dragan.dto.tankovanje;

import rs.trznica.dragan.entities.putninalog.PutniNalog;
import rs.trznica.dragan.entities.putninalog.PutniNalogSql;
import rs.trznica.dragan.entities.tankovanje.Potrosac;
import rs.trznica.dragan.forms.support.DateUtils;

import java.util.Date;

public class PutniNalogDto extends BaseDto<PutniNalogSql> {

	private String redniBroj;

	private Potrosac vozilo;
	
	private String vozac;
	
	private String relacija;
	
	private Date datum;
	
	private String vrstaPrevoza;
	
	private String korisnik;
	
	private String posada;
	
	private String radnaOrganizacija;
	
	private String adresaGaraze;
	
	private String mesto;

	public PutniNalogDto(String redniBroj, Potrosac vozilo, String vozac,
			String relacija, Date datum, String vrstaPrevoza, String korisnik,
			String posada, String radnaOrganizacija, String adresaGaraze, String mesto) {
		super();
		this.redniBroj = redniBroj;
		this.vozilo = vozilo;
		this.vozac = vozac;
		this.relacija = relacija;
		this.datum = datum;
		this.vrstaPrevoza = vrstaPrevoza;
		this.korisnik = korisnik;
		this.posada = posada;
		this.radnaOrganizacija = radnaOrganizacija;
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

	public String getRadnaOrganizacija() {
		return radnaOrganizacija;
	}

	public String getAdresaGaraze() {
		return adresaGaraze;
	}

	public String getMesto() {
		return mesto;
	}

	@Override
	public PutniNalogSql createEntityFromData() {
		PutniNalogSql.PutniNalogSqlBuilder builder = PutniNalogSql.builder();
		builder.redniBroj(Long.valueOf(redniBroj))
				.idVozila(vozilo.getId())
				.namenaVozila(vozilo.getTeretnjak() ? PutniNalog.TERETNI : PutniNalog.PUTNICKI)
				.tipVozila(vozilo.getTip())
				.markaVozila(vozilo.getMarka())
				.regOznaka(vozilo.getRegOznaka());
		if (vozilo.getTeretnjak()) {
			builder.tezina(vozilo.getTezina())
					.nosivost(vozilo.getNosivost())
					.posada(posada);
		} else {
			builder.snagaMotora(vozilo.getSnagaMotora())
					.brojSedista(vozilo.getBrojSedista())
					.korisnik(korisnik);
		}
		builder.vozac(vozac)
				.relacija(relacija)
				.datum(DateUtils.toTimestamp(datum))
				.vrstaPrevoza(vrstaPrevoza)
				.radnaOrganizacija(radnaOrganizacija)
				.adresaGaraze(adresaGaraze)
				.mesto(mesto);
		return builder.build();
	}

}
