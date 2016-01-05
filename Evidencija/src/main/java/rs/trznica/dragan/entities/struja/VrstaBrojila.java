package rs.trznica.dragan.entities.struja;

public enum VrstaBrojila {
	
	SIR_POT("široke potrošnje"),
	MAXIGRAF("sa maksigrafom");
	
	private String description;
	
	private VrstaBrojila(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static VrstaBrojila getForName(String name) {
		for (VrstaBrojila vb : VrstaBrojila.values()) {
			if (vb.name().equals(name)) {
				return vb;
			}
		}
		return null;
	}

}
