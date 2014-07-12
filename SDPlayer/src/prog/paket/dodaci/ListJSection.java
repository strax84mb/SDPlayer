package prog.paket.dodaci;

import prog.paket.playlist.generator.struct.ProgSectionType;

/**
 * Nadgradnja klase ListJItem koja opisuje početak termina.
 * 
 * @author Strahinja Dobrijević
 *
 */
public class ListJSection extends ListJItem {

	/*
	 * fullPath - sadrzi ime kategorije
	 */
	public long startTime;
	public long scheduledTime;
	public String catName;
	public boolean startToon = false;
	public boolean endToon = false;
	public boolean popunitiDoKraja = false;
	public byte prioritet = 6;
	public ProgSectionType sectionType = ProgSectionType.TERMIN;

	/*
	 * Snima se po sledecem redu:
	 * duration
	 * fullPath
	 * fileName
	 * crossfade
	 * startTimeInMins
	 * catName
	 * popunitiDoKraja
	 */
	public ListJSection(){
		duration = -1;
		fullPath = null;
		fileName = null;
		crossfade = false;
		startTime = -1;
		scheduledTime = -1;
		catName = null;
		popunitiDoKraja = false;
	}

	@Override
	public String toString() {
		return fileName;
	}

	@Override
	public boolean equals(Object obj) {
		if((obj == null) || !(obj instanceof ListJSection))
			return false;
		ListJSection sec = (ListJSection)obj;
		return (startTime == sec.startTime) && (scheduledTime == sec.scheduledTime) && 
				(catName.equals(sec.catName));
	}

	public boolean equals(ListJSection sec) {
		if(sec == null) return false;
		return (startTime == sec.startTime) && (scheduledTime == sec.scheduledTime) && 
				(catName.equals(sec.catName));
	}

	@Override
	public boolean isItem() {
		return false;
	}

	@Override
	public boolean isSection() {
		return true;
	}

}
