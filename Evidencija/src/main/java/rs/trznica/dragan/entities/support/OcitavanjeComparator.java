package rs.trznica.dragan.entities.support;

import java.util.Comparator;

import rs.trznica.dragan.entities.struja.Ocitavanje;

public class OcitavanjeComparator implements Comparator<Ocitavanje> {

	@Override
	public int compare(Ocitavanje o1, Ocitavanje o2) {
		int res = o1.getMesec().compareTo(o2.getMesec());
		return (res != 0) ? res : o1.getBrojiloId().compareTo(o2.getBrojiloId());
	}

}
