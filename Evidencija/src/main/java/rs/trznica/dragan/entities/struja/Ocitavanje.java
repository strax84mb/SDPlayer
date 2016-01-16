package rs.trznica.dragan.entities.struja;

public class Ocitavanje extends BasicEntity {

	private Long brojiloId;
	
	private VrstaBrojila brojiloVrsta;

	private String brojiloBroj;

	private String brojiloED;
	
	private String mesec;
	
	private Long kwVT = 0L;
	
	private Long kwNT = 0L;
	
	private Long cenaVT = 0L;
	
	private Long cenaNT = 0L;
	
	private Long pristup = 0L;
	
	private Long podsticaj = 0L;
	
	private Long kwReaktivna = 0L;
	
	private Long cenaKW = 0L;
	
	public Long getBrojiloId() {
		return brojiloId;
	}

	public void setBrojiloId(Long brojiloId) {
		this.brojiloId = brojiloId;
	}

	public VrstaBrojila getBrojiloVrsta() {
		return brojiloVrsta;
	}

	public void setBrojiloVrsta(VrstaBrojila brojiloVrsta) {
		this.brojiloVrsta = brojiloVrsta;
	}

	public String getBrojiloBroj() {
		return brojiloBroj;
	}

	public void setBrojiloBroj(String brojiloBroj) {
		this.brojiloBroj = brojiloBroj;
	}

	public String getBrojiloED() {
		return brojiloED;
	}

	public void setBrojiloED(String brojiloED) {
		this.brojiloED = brojiloED;
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

	public Long getCenaReaktivna() {
		return cenaKW * kwReaktivna / 10L;
	}
	
	public String getBrojiloString() {
		return new StringBuilder(brojiloED).append(" - ").append(brojiloBroj).append(" - ").append(brojiloVrsta.getAbrev()).toString();
	}
}
