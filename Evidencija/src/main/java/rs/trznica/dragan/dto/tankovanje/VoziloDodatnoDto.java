package rs.trznica.dragan.dto.tankovanje;


@SuppressWarnings("rawtypes")
public class VoziloDodatnoDto extends BaseDto {

	private boolean teretnjak;
	private String podrucje;
	private Integer brojSedista = 0;
	private Integer snagaMotora = 0;
	private Integer tezina = 0;
	private Integer nosivost = 0;
	private String vozaci;
	
	public boolean isTeretnjak() {
		return teretnjak;
	}

	public void setTeretnjak(boolean teretnjak) {
		this.teretnjak = teretnjak;
	}

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
