package rs.trznica.dragan.entities.struja;

import org.springframework.util.StringUtils;


public class Brojilo extends BasicEntity {
	
	private String broj;
	
	private String ed;
	
	private String opis;
	
	private Boolean uFunkciji = true;
	
	private VrstaBrojila vrstaBrojila;

	public String getBroj() {
		return broj;
	}

	public void setBroj(String broj) {
		this.broj = broj;
	}

	public String getEd() {
		return ed;
	}

	public void setEd(String ed) {
		this.ed = ed;
	}

	public String getOpis() {
		return opis;
	}

	public void setOpis(String opis) {
		this.opis = opis;
	}

	public Boolean getuFunkciji() {
		return uFunkciji;
	}

	public void setuFunkciji(Boolean uFunkciji) {
		this.uFunkciji = uFunkciji;
	}

	public VrstaBrojila getVrstaBrojila() {
		return vrstaBrojila;
	}

	public void setVrstaBrojila(VrstaBrojila vrstaBrojila) {
		this.vrstaBrojila = vrstaBrojila;
	}

	@Override
	public String toString() {
		return (StringUtils.isEmpty(broj) || StringUtils.isEmpty(ed)) ? "Greska" : 
			new StringBuilder(ed).append(" - ").append(broj).append(" - ").append(vrstaBrojila.getAbrev()).toString();
	}
}
