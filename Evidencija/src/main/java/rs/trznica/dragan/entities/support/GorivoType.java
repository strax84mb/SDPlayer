package rs.trznica.dragan.entities.support;

public enum GorivoType {
	BMB("BMB"),
	ED("Evro dizel");

	private String label;

	private GorivoType(String label) {
		this.label = label;
	}

	public String getName() {
		return name();
	}

	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return getLabel();
	}
}
