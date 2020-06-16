package rs.trznica.dragan.entities.support;

import rs.trznica.dragan.entities.struja.BrojiloSql;

import java.util.Comparator;

public class BrojiloComparator implements Comparator<BrojiloSql> {

	@Override
	public int compare(BrojiloSql o1, BrojiloSql o2) {
		if (o1.getVrstaBrojila().compareTo(o2.getVrstaBrojila()) == 0) {
			return o1.getBroj().compareTo(o2.getBroj());
		} else {
			return o1.getVrstaBrojila().compareTo(o2.getVrstaBrojila());
		}
	}

}
