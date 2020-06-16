package rs.trznica.dragan.entities.support;

import rs.trznica.dragan.entities.struja.Ocitavanje;
import rs.trznica.dragan.entities.struja.OcitavanjeSql;
import rs.trznica.dragan.entities.struja.VrstaBrojila;

public class OcitavanjeSuma {

	private Long kwVT = 0L;
	
	private Long kwNT = 0L;
	
	private Long kwST = 0L;
	
	private Long cenaVT = 0L;
	
	private Long cenaNT = 0L;
	
	private Long cenaST = 0L;
	
	private Long pristup = 0L;
	
	private Long podsticaj = 0L;
	
	private Long kwReaktivna = 0L;
	
	private Long cenaReaktivna = 0L;

	public Long getKwVT() {
		return kwVT;
	}

	public Long getKwNT() {
		return kwNT;
	}

	public Long getKwST() {
		return kwST;
	}

	public Long getCenaVT() {
		return cenaVT;
	}

	public Long getCenaNT() {
		return cenaNT;
	}

	public Long getCenaST() {
		return cenaST;
	}

	public Long getPristup() {
		return pristup;
	}

	public Long getPodsticaj() {
		return podsticaj;
	}

	public Long getKwReaktivna() {
		return kwReaktivna;
	}

	public Long getCenaReaktivna() {
		return cenaReaktivna;
	}

	public void addReading(OcitavanjeSql object) {
		if (VrstaBrojila.SIR_POT_JED.equals(object.getBrojiloVrsta())) {
			kwST += object.getKwNT();
			cenaST += object.getCenaNT();
		} else {
			kwNT += object.getKwNT();
			kwVT += object.getKwVT();
			cenaNT += object.getCenaNT();
			cenaVT += object.getCenaVT();
		}
		pristup += object.getPristup();
		podsticaj += object.getPodsticaj();
		if (VrstaBrojila.MAXIGRAF.equals(object.getBrojiloVrsta())) {
			kwReaktivna += object.getKwReaktivna();
			cenaReaktivna += object.getCenaReaktivna();
		}
	}
}
