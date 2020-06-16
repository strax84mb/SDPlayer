package rs.trznica.dragan.dto.struja;

import rs.trznica.dragan.entities.struja.BrojiloSql;
import rs.trznica.dragan.entities.struja.OcitavanjeSql;
import rs.trznica.dragan.entities.struja.VrstaBrojila;
import rs.trznica.dragan.forms.support.DecimalFormater;

public class OcitavanjeDto {

	private BrojiloSql brojilo;
	
	private String mesec;
	
	private String kwVT;
	
	private String kwNT;
	
	private String cenaVT;
	
	private String cenaNT;
	
	private String pristup;
	
	private String podsticaj;
	
	private String kwReaktivna;
	
	private String cenaKW;

	public BrojiloSql getBrojilo() {
		return brojilo;
	}

	public void setBrojilo(BrojiloSql brojilo) {
		this.brojilo = brojilo;
	}

	public String getMesec() {
		return mesec;
	}

	public void setMesec(String mesec) {
		this.mesec = mesec;
	}

	public String getKwVT() {
		return kwVT;
	}

	public void setKwVT(String kwVT) {
		this.kwVT = kwVT;
	}

	public String getKwNT() {
		return kwNT;
	}

	public void setKwNT(String kwNT) {
		this.kwNT = kwNT;
	}

	public String getCenaVT() {
		return cenaVT;
	}

	public void setCenaVT(String cenaVT) {
		this.cenaVT = cenaVT;
	}

	public String getCenaNT() {
		return cenaNT;
	}

	public void setCenaNT(String cenaNT) {
		this.cenaNT = cenaNT;
	}

	public String getPristup() {
		return pristup;
	}

	public void setPristup(String pristup) {
		this.pristup = pristup;
	}

	public String getPodsticaj() {
		return podsticaj;
	}

	public void setPodsticaj(String podsticaj) {
		this.podsticaj = podsticaj;
	}

	public String getKwReaktivna() {
		return kwReaktivna;
	}

	public void setKwReaktivna(String kwReaktivna) {
		this.kwReaktivna = kwReaktivna;
	}

	public String getCenaKW() {
		return cenaKW;
	}

	public void setCenaKW(String cenaKW) {
		this.cenaKW = cenaKW;
	}

	public OcitavanjeDto(BrojiloSql brojilo, String mesec, String kwVT,
						 String kwNT, String cenaVT, String cenaNT, String pristup,
						 String podsticaj, String kwReaktivna, String cenaKW) {
		super();
		this.brojilo = brojilo;
		this.mesec = mesec;
		this.kwVT = kwVT;
		this.kwNT = kwNT;
		this.cenaVT = cenaVT;
		this.cenaNT = cenaNT;
		this.pristup = pristup;
		this.podsticaj = podsticaj;
		this.kwReaktivna = kwReaktivna;
		this.cenaKW = cenaKW;
	}
	
	public OcitavanjeSql getEntity() {
		OcitavanjeSql.OcitavanjeSqlBuilder builder = OcitavanjeSql.builder();
		builder.brojiloId(brojilo.getId())
				.brojiloVrsta(brojilo.getVrstaBrojila())
				.brojiloBroj(brojilo.getBroj())
				.brojiloED(brojilo.getEd())
				.mesec(mesec)
				.kwNT(DecimalFormater.parseToLong(kwNT, 0))
				.cenaNT(DecimalFormater.parseToLong(cenaNT, 2));
		if (!VrstaBrojila.SIR_POT_JED.equals(brojilo.getVrstaBrojila())) {
			builder.kwVT(DecimalFormater.parseToLong(kwVT, 0))
					.cenaVT(DecimalFormater.parseToLong(cenaVT, 2));
		}
		builder.pristup(DecimalFormater.parseToLong(pristup, 2))
				.podsticaj(DecimalFormater.parseToLong(podsticaj, 2));
		if (VrstaBrojila.MAXIGRAF.equals(brojilo.getVrstaBrojila())) {
			builder.kwReaktivna(DecimalFormater.parseToLong(kwReaktivna, 0))
					.cenaReaktivna(DecimalFormater.parseToLong(cenaKW, 3));
		}
		return builder.build();
	}
}
