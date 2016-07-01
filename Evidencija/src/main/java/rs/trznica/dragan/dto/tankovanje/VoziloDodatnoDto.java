package rs.trznica.dragan.dto.tankovanje;


public class VoziloDodatnoDto extends BaseDto {

	private String podrucje;
	private Integer brojSedista = 0;
	private Integer snagaMotora = 0;
	private Integer tezina = 0;
	private Integer nosivost = 0;
	private Integer rBNaloga = 0;
	private String vozaci;
	
	public String getPodrucje() {
		return podrucje;
	}

	public void setPodrucje(String podrucje) {
		this.podrucje = podrucje;
	}

	public Integer getBrojSedista() {
		return brojSedista;
	}

	public void setBrojSedista(Integer brojSedista) {
		this.brojSedista = brojSedista;
	}

	public Integer getSnagaMotora() {
		return snagaMotora;
	}

	public void setSnagaMotora(Integer snagaMotora) {
		this.snagaMotora = snagaMotora;
	}

	public Integer getTezina() {
		return tezina;
	}

	public void setTezina(Integer tezina) {
		this.tezina = tezina;
	}

	public Integer getNosivost() {
		return nosivost;
	}

	public void setNosivost(Integer nosivost) {
		this.nosivost = nosivost;
	}

	public Integer getrBNaloga() {
		return rBNaloga;
	}

	public void setrBNaloga(Integer rBNaloga) {
		this.rBNaloga = rBNaloga;
	}

	public String getVozaci() {
		return vozaci;
	}

	public void setVozaci(String vozaci) {
		this.vozaci = vozaci;
	}

	@Override
	public Object createEntityFromData() {
		return this;
	}

}
