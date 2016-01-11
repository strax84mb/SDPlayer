package rs.trznica.dragan.entities.support;

import java.util.Comparator;

import rs.trznica.dragan.entities.struja.Brojilo;

public class BrojiloComparator implements Comparator<Brojilo> {

	@Override
	public int compare(Brojilo o1, Brojilo o2) {
		return o1.getBroj().compareTo(o2.getBroj());
	}

}
