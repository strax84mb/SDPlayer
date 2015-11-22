package rs.trznica.dragan.poi;

import rs.trznica.dragan.entities.support.GorivoType;

public class ConsumerStatSummary {

	private long kmBmb = 0L;

	private long kmEd = 0L;

	private long litBmb = 0L;

	private long litEd = 0L;

	private long price = 0L;

	public long getKmBmb() {
		return kmBmb;
	}

	public long getKmEd() {
		return kmEd;
	}

	public long getLitBmb() {
		return litBmb;
	}

	public long getLitEd() {
		return litEd;
	}

	public long getPrice() {
		return price;
	}

	public long addKmBmb(long value) {
		return (kmBmb += value);
	}

	public long addKmEd(long value) {
		return (kmEd += value);
	}

	public void addKm(GorivoType type, long value) {
		if (GorivoType.BMB.equals(type)) {
			addKmBmb(value);
		} else {
			addKmEd(value);
		}
	}

	public long addLitBmb(long value) {
		return (litBmb += value);
	}

	public long addLitEd(long value) {
		return (litEd += value);
	}

	public void addLit(GorivoType type, long value) {
		if (GorivoType.BMB.equals(type)) {
			addLitBmb(value);
		} else {
			addLitEd(value);
		}
	}

	public long addPrice(long value) {
		return (price += value);
	}
}
