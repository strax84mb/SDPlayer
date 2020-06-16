package rs.trznica.dragan.entities.support;

import rs.trznica.dragan.entities.struja.OcitavanjeSql;

import java.util.Comparator;

public class OcitavanjeComparator implements Comparator<OcitavanjeSql> {

	@Override
	public int compare(OcitavanjeSql o1, OcitavanjeSql o2) {
		int res = o1.getMesec().compareTo(o2.getMesec());
		return (res != 0) ? res : o1.getBrojiloId().compareTo(o2.getBrojiloId());
	}

}
