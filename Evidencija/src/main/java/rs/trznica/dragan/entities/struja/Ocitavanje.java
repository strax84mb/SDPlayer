package rs.trznica.dragan.entities.struja;

public class Ocitavanje extends BasicEntity {

	private String brojiloId;
	
	private VrstaBrojila brojiloVrsta;
	
	private String mesec;
	
	private Long kwVT;
	
	private Long kwNT;
	
	private Long cenaVT;
	
	private Long cenaNT;
	
	private Long pristup;
	
	private Long podsticaj;
	
	private Long kwReaktivna;
	
	private Long cenaKW;
	
	public String getBrojiloId() {
		return brojiloId;
	}

	public void setBrojiloId(String brojiloId) {
		this.brojiloId = brojiloId;
	}

	public VrstaBrojila getBrojiloVrsta() {
		return brojiloVrsta;
	}

	public void setBrojiloVrsta(VrstaBrojila brojiloVrsta) {
		this.brojiloVrsta = brojiloVrsta;
	}

	public String getMesec() {
		return mesec;
	}

	public void setMesec(String mesec) {
		this.mesec = mesec;
	}

	public Long getKwVT() {
		return kwVT;
	}

	public void setKwVT(Long kwVT) {
		this.kwVT = kwVT;
	}

	public Long getKwNT() {
		return kwNT;
	}

	public void setKwNT(Long kwNT) {
		this.kwNT = kwNT;
	}

	public Long getCenaVT() {
		return cenaVT;
	}

	public void setCenaVT(Long cenaVT) {
		this.cenaVT = cenaVT;
	}

	public Long getCenaNT() {
		return cenaNT;
	}

	public void setCenaNT(Long cenaNT) {
		this.cenaNT = cenaNT;
	}

	public Long getPristup() {
		return pristup;
	}

	public void setPristup(Long pristup) {
		this.pristup = pristup;
	}

	public Long getPodsticaj() {
		return podsticaj;
	}

	public void setPodsticaj(Long podsticaj) {
		this.podsticaj = podsticaj;
	}

	public Long getKwReaktivna() {
		return kwReaktivna;
	}

	public void setKwReaktivna(Long kwReaktivna) {
		this.kwReaktivna = kwReaktivna;
	}

	public Long getCenaKW() {
		return cenaKW;
	}

	public void setCenaKW(Long cenaKW) {
		this.cenaKW = cenaKW;
	}

}
