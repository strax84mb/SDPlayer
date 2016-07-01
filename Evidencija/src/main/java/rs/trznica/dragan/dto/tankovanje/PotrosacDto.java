package rs.trznica.dragan.dto.tankovanje;

import rs.trznica.dragan.entities.support.GorivoType;
import rs.trznica.dragan.entities.tankovanje.Potrosac;

public class PotrosacDto extends BaseDto<Potrosac> {

	private Long id;

	private String regOznaka;

	private String marka;

	private String tip;

	private Boolean vozilo;

	private Boolean teretnjak;

	private GorivoType gorivo;

	private Boolean aktivan;

	private Boolean koristiKM;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRegOznaka() {
		return regOznaka;
	}

	public void setRegOznaka(String regOznaka) {
		this.regOznaka = regOznaka;
	}

	public String getMarka() {
		return marka;
	}

	public void setMarka(String marka) {
		this.marka = marka;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public Boolean getVozilo() {
		return vozilo;
	}

	public void setVozilo(Boolean vozilo) {
		this.vozilo = vozilo;
	}

	public Boolean getTeretnjak() {
		return teretnjak;
	}

	public void setTeretnjak(Boolean teretnjak) {
		this.teretnjak = teretnjak;
	}

	public GorivoType getGorivo() {
		return gorivo;
	}

	public void setGorivo(GorivoType gorivo) {
		this.gorivo = gorivo;
	}

	public Boolean getAktivan() {
		return aktivan;
	}

	public void setAktivan(Boolean aktivan) {
		this.aktivan = aktivan;
	}

	public Boolean getKoristiKM() {
		return koristiKM;
	}

	public void setKoristiKM(Boolean koristiKM) {
		this.koristiKM = koristiKM;
	}

	public PotrosacDto(Long id, String regOznaka, String marka, String tip,
			Boolean vozilo, Boolean teretnjak, GorivoType gorivo, Boolean aktivan, 
			Boolean koristiKM) {
		this.id = id;
		this.regOznaka = (regOznaka != null) ? regOznaka.trim() : regOznaka;
		this.marka = (marka != null)? marka.trim() : marka;
		this.tip = (tip != null)? tip.trim() : tip;
		this.vozilo = vozilo;
		this.teretnjak = teretnjak;
		this.gorivo = gorivo;
		this.aktivan = aktivan;
		this.koristiKM = koristiKM;
	}

	public Potrosac createNewEntity() {
		return (Potrosac) createEntityFromData();
	}

	@Override
	public Potrosac createEntityFromData() {
		Potrosac entity = new Potrosac();
		entity.setId(id);
		entity.setRegOznaka(regOznaka);
		entity.setMarka(marka);
		entity.setTip(tip);
		entity.setVozilo(vozilo);
		entity.setTeretnjak(teretnjak);
		entity.setGorivo(gorivo);
		entity.setAktivan(aktivan);
		entity.setMeriKm(koristiKM);
		return entity;
	}
}
