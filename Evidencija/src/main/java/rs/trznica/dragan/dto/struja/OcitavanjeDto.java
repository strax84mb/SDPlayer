package rs.trznica.dragan.dto.struja;

import rs.trznica.dragan.entities.struja.Brojilo;
import rs.trznica.dragan.entities.struja.Ocitavanje;
import rs.trznica.dragan.entities.struja.VrstaBrojila;
import rs.trznica.dragan.forms.support.DecimalFormater;

public class OcitavanjeDto {

	private Brojilo brojilo;
	
	private String mesec;
	
	private String kwVT;
	
	private String kwNT;
	
	private String cenaVT;
	
	private String cenaNT;
	
	private String pristup;
	
	private String podsticaj;
	
	private String kwReaktivna;
	
	private String cenaKW;

	public Brojilo getBrojilo() {
		return brojilo;
	}

	public void setBrojilo(Brojilo brojilo) {
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

	public OcitavanjeDto(Brojilo brojilo, String mesec, String kwVT,
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
	
	public Ocitavanje getEntity() {
		Ocitavanje entity = new Ocitavanje();
		entity.setBrojiloId(brojilo.getId());
		entity.setBrojiloVrsta(brojilo.getVrstaBrojila());
		entity.setBrojiloBroj(brojilo.getBroj());
		entity.setBrojiloED(brojilo.getEd());
		entity.setMesec(mesec);
		entity.setKwNT(DecimalFormater.parseToLong(kwNT, 0));
		entity.setCenaNT(DecimalFormater.parseToLong(cenaNT, 2));
		if (VrstaBrojila.SIR_POT_DVO.equals(brojilo.getVrstaBrojila())) {
			entity.setKwVT(DecimalFormater.parseToLong(kwVT, 0));
			entity.setCenaVT(DecimalFormater.parseToLong(cenaVT, 2));
		}
		entity.setPristup(DecimalFormater.parseToLong(pristup, 2));
		entity.setPodsticaj(DecimalFormater.parseToLong(podsticaj, 2));
		if (VrstaBrojila.MAXIGRAF.equals(brojilo.getVrstaBrojila())) {
			entity.setKwReaktivna(DecimalFormater.parseToLong(kwReaktivna, 0));
			entity.setCenaKW(DecimalFormater.parseToLong(cenaKW, 3));
		}
		return entity;
	}
}
