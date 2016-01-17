package rs.trznica.dragan.entities.struja;

public enum VrstaBrojila {
	
	SIR_POT_JED("Široke potrošnje JT", "J"),
	SIR_POT_DVO("Široke potrošnje DT", "D"),
	MAXIGRAF("Niskog napona", "N");
	
	private String description;
	private String abrev;
	
	private VrstaBrojila(String description, String abrev) {
		this.description = description;
		this.abrev = abrev;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getAbrev() {
		return abrev;
	}
	
	public static VrstaBrojila getForName(String name) {
		for (VrstaBrojila vb : VrstaBrojila.values()) {
			if (vb.name().equals(name)) {
				return vb;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return getDescription();
	}

}
