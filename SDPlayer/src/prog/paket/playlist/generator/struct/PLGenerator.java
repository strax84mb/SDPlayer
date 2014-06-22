package prog.paket.playlist.generator.struct;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import javax.sound.sampled.UnsupportedAudioFileException;

import prog.paket.dodaci.ListJItem;
import prog.paket.dodaci.TimeEntry;
import prog.paket.forme.reklame.ScheduledItem;
import prog.paket.forme.reklame.ScheduledItemsType;
import prog.paket.playlist.generator.PlayerWin;

public class PLGenerator {

	// Kalendar za racunanje vremena
	private GregorianCalendar cal = new GregorianCalendar();
	public List<ProgSection> sections = new ArrayList<ProgSection>();
	private List<ScheduledItemsType> schTypes = new ArrayList<ScheduledItemsType>();
	private List<CatWithList> cats = new ArrayList<CatWithList>();
	private Random rand = new Random();
	private long plStart = 0, plEnd = 0;

	final int REJECTED = -1;
	final int ACCEPTED = 0;
	final int INSERTED = 1;

	public PLGenerator(){
		cal.setTimeInMillis(System.currentTimeMillis());
	}

	private void sortedInput(List<Integer> list, int val){
		int i = 0, len = list.size();
		for(;i<len;i++){
			if(list.get(i).intValue() == val) return;
			if(list.get(i).intValue() > val) break;
		}
		list.add(val);
	}

	/*
	 * Izracunaj termine emitovanja
	 */
	private List<Integer> getReservedTimes(MusicCategory cat){
		List<Integer> ret = new ArrayList<Integer>();
		// Ubacivanje termina
		if(cat.terminsko){
			if(cat.termin1 != -1) sortedInput(ret, cat.termin1);
			if(cat.termin2 != -1) sortedInput(ret, cat.termin2);
			if(cat.termin3 != -1) sortedInput(ret, cat.termin3);
			if(cat.termin4 != -1) sortedInput(ret, cat.termin4);
			if(cat.termin5 != -1) sortedInput(ret, cat.termin5);
			if(cat.termin6 != -1) sortedInput(ret, cat.termin6);
			if(cat.termin7 != -1) sortedInput(ret, cat.termin7);
			if(cat.termin8 != -1) sortedInput(ret, cat.termin8);
			if(cat.termin9 != -1) sortedInput(ret, cat.termin9);
			if(cat.termin10 != -1) sortedInput(ret, cat.termin10);
		}
		if(cat.periodicno){
			int temp = cat.prvo, end = cat.zadnje, step = cat.na_svakih;
			while(temp <= end){
				sortedInput(ret, temp);
				temp += step;
			}
		}
		return ret;
	}

	private boolean loadCategories(){
		cats.clear();
		File files[];
		try{
			File dir = new File("mcats");
			files = dir.listFiles(new PesFileFilter());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			PlayerWin.getErrDlg().showError("Desila se greška tokom listanja definisanih kategorija.");
			return false;
		}
		String str = null;
		CatWithList cat = null;
		for (int i=0,len=files.length;i<len;i++){
			str = files[i].getName();
			str = str.substring(0, str.length() - 4);
			try{
				cat = CatWithList.load(str);
				cats.add(cat);
			}catch(Exception ex){
				ex.printStackTrace(System.out);
				PlayerWin.getErrDlg().showError("Desila se greška čitanja kategorije " + str);
			}
		}
		return true;
	}

	private boolean loadScheduledTypes(){
		schTypes.clear();
		File files[];
		try{
			File dir = new File("mcats");
			files = dir.listFiles(new RekFileFilter());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			PlayerWin.getErrDlg().showError("Desila se greška tokom listanja definisanih reklamno/oglasnih kategorija.");
			return false;
		}
		String str = null;
		ScheduledItemsType type = null;
		ScheduledItem item = null;
		TimeEntry entry = null;
		int count_len, count, j, jLen;;
		for(int i=0,len=files.length;i<len;i++){
			str = files[i].getName();
			str = str.substring(0, str.length() - 4);
			type = new ScheduledItemsType();
			type.name = str;
			try{
				String pathStr;
				FileInputStream fis = new FileInputStream(files[i]);
				ObjectInputStream ois = new ObjectInputStream(fis);
				type.name = ois.readUTF();
				pathStr = ois.readUTF();
				type.najava = (pathStr.equals("null"))?null:new ListJItem(pathStr);
				pathStr = ois.readUTF();
				type.odjava = (pathStr.equals("null"))?null:new ListJItem(pathStr);
				type.prioritet = ois.readByte();
				count_len = ois.readInt();
				for(count=0;count<count_len;count++){
					item = new ScheduledItem(ois.readUTF());
					item.begin = ois.readLong();
					item.end = ois.readLong();
					item.mestoUBloku = ois.readByte();
					item.ponedeljak = ois.readBoolean();
					item.utorak = ois.readBoolean();
					item.sreda = ois.readBoolean();
					item.cetvrtak = ois.readBoolean();
					item.petak = ois.readBoolean();
					item.subota = ois.readBoolean();
					item.nedelja = ois.readBoolean();
					jLen = ois.readInt();
					for(j=0;j<jLen;j++){
						entry = new TimeEntry();
						entry.time = ois.readInt();
						entry.rank = ois.readByte();
						item.runTimes.add(entry);
					}
					type.items.add(item);
				}
				ois.close();
				fis.close();
			}catch(IOException ioe){
				ioe.printStackTrace(System.out);
				PlayerWin.getErrDlg().showError("Desila se greška tokom čitanja oglasno/reklamne kategorije " + str);
			}catch(UnsupportedAudioFileException ioe){
				ioe.printStackTrace(System.out);
				PlayerWin.getErrDlg().showError("Desila se greška tokom čitanja kategorije " + str);
			}
			schTypes.add(type);
		}
		return true;
	}

	private long calculateDuration(CatWithList cwl){
		long ret = 0;
		for(int i=0,len=cwl.list.size();i<len;i++)
			ret += cwl.list.get(i).duration / 1000;
		if(cwl.cat.crossfade)
			ret -= (cwl.list.size() - 1) * 3000;
		if(cwl.startDzinglLen != -1) ret += cwl.startDzinglLen;
		if(cwl.endDzinglLen != -1) ret += cwl.endDzinglLen;
		return ret;
	}

	private long calculateDuration(ScheduledItemsType type, List<ScheduledItem> list){
		long ret = 0;
		for(int i=0,len=list.size();i<len;i++)
			ret += list.get(i).duration / 1000;
		if(type.najava != null) ret += type.najava.duration / 1000;
		if(type.odjava != null) ret += type.odjava.duration / 1000;
		return ret;
	}

	private ProgSection makeNewSection(long startTime, CatWithList cwl){
		ProgSection sect = new ProgSection();
		sect.startTime = cal.getTimeInMillis();
		if(cwl.cat.trajanje == 0){
			sect.duration = calculateDuration(cwl);
		}else if(cwl.cat.trajanje > 0){
			sect.duration = cwl.cat.trajanje * 60000;
		}
		//sect.duration = (cwl.cat.trajanje==-1)?calculateDuration(cwl):cwl.cat.trajanje * 60000;
		sect.catName = cwl.cat.ime;
		sect.startToon = cwl.cat.najavnaSpica != null;
		sect.endToon = cwl.cat.odjavnaSpica != null;
		sect.pomeriti = cwl.cat.cuvajSadrzaj;
		sect.popunitiDoKraja = cwl.cat.trajanje > 0;
		sect.crossfade = cwl.cat.crossfade;
		sect.scheduledTime = startTime;
		sect.sectionType = ProgSectionType.TERMIN;
		sect.prioritet = (byte)cwl.cat.prioritet;
		sect.pisiUIzvestaj = cwl.cat.pisiUIzvestaj;
		if(!sect.popunitiDoKraja){
			if(sect.startToon){
				try{
					sect.songs.add(new ListJItem(cwl.cat.najavnaSpica));
				}catch(Exception ex){ex.printStackTrace(System.out);}
			}
			if(cwl.cat.trajanje < 0){
				for(int i=-cwl.cat.trajanje;i>0;i--)
					sect.songs.add(cwl.list.get(rand.nextInt(cwl.list.size())));
				sect.duration = 0;
				for(int i=0,len=sect.songs.size();i<len;i++)
					sect.duration += sect.songs.get(i).duration / 1000;
			}else{
				if(!cwl.cat.postujRedosled){
					ListJItem item;
					int index;
					for(int i=0,len=cwl.list.size();i<len;i++){
						index = rand.nextInt(cwl.list.size());
						item = cwl.list.get(index);
						cwl.list.set(index, cwl.list.get(i));
						cwl.list.set(i, item);
					}
				}
				for(int i=0,len=cwl.list.size();i<len;i++)
					sect.songs.add(cwl.list.get(i));
				if(sect.endToon){
					try{
						sect.songs.add(new ListJItem(cwl.cat.odjavnaSpica));
					}catch(Exception ex){ex.printStackTrace(System.out);}
				}
			}
		}
		return sect;
	}

	/*
	private void sortedSectionAdd(ProgSection sec){
		int i, len = sections.size();
		for(i=0;i<len;i++){
			if(sec.startTime < sections.get(i).startTime) break;
		}
		sections.add(i, sec);
	}
	*/

	private int addSectionToPL(ProgSection sec){
		int i, len = sections.size();
		ProgSection temp;
		long endTime = sec.startTime + sec.duration;
		int state = ACCEPTED;
		for(i=0;i<len;i++){
			if((sec.startTime + sec.duration < plStart) || (plEnd < sec.startTime)){
				break;
			}
			/*
			 * Ako >>> cuvajSadrzaj == true <<< onda pomeri termin kasnije,
			 * ako dodje do kraja dana onda ignorisi
			 * Ako >>> cuvajSadrzaj == false <<< onda krajcaj. Ako je zauzeto parce u sredini
			 * onda se termin deli na dva s tim da u prvom vazi >>> playEndToon = false <<<
			 * a u drugom vazi >>> playStartToon = false <<<
			 */
			temp = sections.get(i);
			if(sec.pomeriti){
				if(((sec.startTime >= temp.startTime) && (sec.startTime < temp.startTime + temp.duration)) || 
						((endTime > temp.startTime) && (endTime <= temp.startTime + temp.duration))){
					sec.startTime = temp.startTime + temp.duration;
					endTime = sec.startTime + sec.duration;
					//state = addSectionToPL(sec);
					continue;
				}
			}
			// Provera da li treba skratiti termin s kraja
			if(!sec.pomeriti && (sec.startTime < temp.startTime) && (endTime > temp.startTime) && 
					(endTime <= temp.startTime + temp.duration)){
				endTime = temp.startTime;
				sec.duration = endTime - sec.startTime;
				endTime = sec.startTime + sec.duration;
				continue;
			}
			// Provera da li treba skratiti termin s pocetka
			if(!sec.pomeriti && (endTime > temp.startTime + temp.duration) && 
					(sec.startTime >= temp.startTime) && 
					(sec.startTime < temp.startTime + temp.duration)){
				sec.startTime = temp.startTime + temp.duration;
				continue;
			}
			// Provera da li termin obuhvata prethodni termin;
			if((sec.startTime < temp.startTime) && (endTime > temp.startTime + temp.duration)){
				if(sec.pomeriti){
					sec.startTime = temp.startTime + temp.duration;
					state = addSectionToPL(sec);
					break;
				}else{
					if(sec.popunitiDoKraja){
						ProgSection nextSec = sec.clone();
						nextSec.duration = sec.startTime + sec.duration - temp.startTime - temp.duration;
						nextSec.startTime = temp.startTime + temp.duration;
						nextSec.startToon = false;
						sec.endToon = false;
						sec.duration = temp.startTime - sec.startTime;
						state = addSectionToPL(sec);
						state = addSectionToPL(nextSec);
						break;
					}else{
						// Rasparcati sadrzaj
						ProgSection nextSec = sec.clone();
						nextSec.startTime = temp.startTime + temp.duration;
						nextSec.duration = 0;
						ListJItem item;
						while(sec.duration > temp.startTime - sec.startTime){
							item = sec.songs.remove(sec.songs.size() - 1);
							nextSec.songs.add(0, item);
							nextSec.duration += item.duration / 1000;
							sec.duration -= item.duration / 1000;
						}
						sec.startTime = temp.startTime - sec.duration;
						nextSec.startToon = false;
						sec.endToon = false;
						sec.duration = temp.startTime - sec.startTime;
						state = addSectionToPL(sec);
						state = addSectionToPL(nextSec);
						break;
					}
				}
			}
			// Provera da li je termin obuhvacen od strane drugog termina (odbacuje se)
			if(!sec.pomeriti && (sec.startTime >= temp.startTime) && 
					(sec.startTime + sec.duration <= temp.startTime + temp.duration)){
				state = REJECTED;
				break;
			}
			// Ako je termin kraci od jednog minuta, rad prestaje prevremeno
			if(sec.popunitiDoKraja && (sec.duration < 90000)){
				state = REJECTED;
				break;
			}
		}
		/*
		if((sec.sectionType == ProgSectionType.REKLAME) && (state == REJECTED)){
			Calendar newCal = new GregorianCalendar();
			newCal.setTimeInMillis(sec.scheduledTime);
			System.out.println("Reklame u " + ((new SimpleDateFormat("HH:mm dd.MM.yyyy.")).format(newCal.getTime())) 
					+ " odbacene.");
		}
		if((sec.sectionType == ProgSectionType.REKLAME) && (state == ACCEPTED)){
			Calendar newCal = new GregorianCalendar();
			newCal.setTimeInMillis(sec.scheduledTime);
			System.out.println("Reklame u " + ((new SimpleDateFormat("HH:mm dd.MM.yyyy.")).format(newCal.getTime())) 
					+ " prihvacene.");
		}
		*/
		if((state == REJECTED) || (state == INSERTED)) return state;
		// Ako je rad nije prestao prevremeno, "okrajcani" termin se ubacuje u vektor
		if(state == ACCEPTED){
			for(i=0;i<len;i++){
				temp = sections.get(i);
				if(sec.startTime < temp.startTime) break;
			}
			sections.add(i, sec);
			return INSERTED;
		}else return REJECTED;
	}

	private void checkSectionForTime(long startTime, long endTime, CatWithList cwl){
		List<Integer> times = getReservedTimes(cwl.cat);
		ProgSection sect;
		int time;
		long curr;
		for(int i=0,len=times.size();i<len;i++){
			time = times.get(i).intValue();
			cal.setTimeInMillis(startTime);
			cal.set(Calendar.HOUR_OF_DAY, time / 60);
			cal.set(Calendar.MINUTE, time % 60);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			curr = cal.getTimeInMillis();
			while(curr <= endTime){
				if(((curr >= startTime) && (curr <= endTime)) || 
						((curr + (cwl.cat.trajanje * 60000) >= startTime) && (curr + (cwl.cat.trajanje * 60000) <= endTime))){
					// Proveri da li odgovara dan u nedelji
					switch(cal.get(Calendar.DAY_OF_WEEK)){
					case Calendar.MONDAY:
						if(cwl.cat.ponedeljak){
							sect = makeNewSection(cal.getTimeInMillis(), cwl);
							addSectionToPL(sect);
						}
						break;
					case Calendar.TUESDAY:
						if(cwl.cat.utorak){
							sect = makeNewSection(cal.getTimeInMillis(), cwl);
							addSectionToPL(sect);
						}
						break;
					case Calendar.WEDNESDAY:
						if(cwl.cat.sreda){
							sect = makeNewSection(cal.getTimeInMillis(), cwl);
							addSectionToPL(sect);
						}
						break;
					case Calendar.THURSDAY:
						if(cwl.cat.cetvrtak){
							sect = makeNewSection(cal.getTimeInMillis(), cwl);
							addSectionToPL(sect);
						}
						break;
					case Calendar.FRIDAY:
						if(cwl.cat.petak){
							sect = makeNewSection(cal.getTimeInMillis(), cwl);
							addSectionToPL(sect);
						}
						break;
					case Calendar.SATURDAY:
						if(cwl.cat.subota){
							sect = makeNewSection(cal.getTimeInMillis(), cwl);
							addSectionToPL(sect);
						}
						break;
					default:
						if(cwl.cat.nedelja){
							sect = makeNewSection(cal.getTimeInMillis(), cwl);
							addSectionToPL(sect);
						}
					}
				}
				// Predji u isto vreme sutradan
				cal.setTimeInMillis(cal.getTimeInMillis() + 86400000L);
				curr = cal.getTimeInMillis();
			}
		}
	}

	private void checkCommercialsForTime(long startTime, long endTime, ScheduledItemsType type){
		List<Integer> times = type.getAllTimes();
		// Times tester
		/*
		String strTime;
		for(int i=0,len=times.size();i<len;i++){
			strTime = String.valueOf(times.get(i) % 60);
			if(strTime.length() == 1) strTime = "0" + strTime;
			strTime = String.valueOf(times.get(i) / 60) + ":" + strTime;
			if(strTime.length() == 4) strTime = "0" + strTime;
			System.out.println(strTime);
		}*/
		ProgSection sect;
		int time, temp;
		long currTime = 0;
		for(int i=0,len=times.size();i<len;i++){
			time = times.get(i).intValue();
			cal.setTimeInMillis(startTime);
			cal.set(Calendar.HOUR_OF_DAY, time / 60);
			cal.set(Calendar.MINUTE, time % 60);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			currTime = cal.getTimeInMillis();
			while(currTime <= endTime){
				if((currTime >= startTime) && (currTime <= endTime)){
					List<ScheduledItem> block = type.generateBlock(currTime);
					temp = block.size();
					if(temp > 0){
						sect = new ProgSection();
						sect.catName = type.name;
						sect.duration = calculateDuration(type, block);
						sect.startToon = type.najava != null;
						sect.endToon = type.odjava != null;
						sect.pomeriti = true;
						sect.popunitiDoKraja = false;
						sect.startTime = currTime;
						sect.scheduledTime = currTime;
						sect.prioritet = type.prioritet;
						sect.sectionType = ProgSectionType.REKLAME;
						sect.pisiUIzvestaj = false;
						if(sect.startToon)
							sect.songs.add(type.najava);
						for(int j=0,jLen=block.size();j<jLen;j++)
							sect.songs.add(block.get(j));
						if(sect.endToon)
							sect.songs.add(type.odjava);
						addSectionToPL(sect);
					}
				}
				currTime += 86400000L;
				//cal.setTimeInMillis(cal.getTimeInMillis() + 86400000L);
				//currTime = cal.getTimeInMillis();
			}
		}
	}

	public void createSections(long startTime, long endTime){
		loadCategories();
		loadScheduledTypes();
		for(int prioritet=1;prioritet<7;prioritet++){
			for(int i=0,len=schTypes.size();i<len;i++){
				if(schTypes.get(i).prioritet == prioritet)
					checkCommercialsForTime(startTime, endTime, schTypes.get(i));
			}
			for(int i=0,len=cats.size();i<len;i++){
				if(cats.get(i).cat.prioritet == prioritet)
					checkSectionForTime(startTime, endTime, cats.get(i));
			}
		}
	}

	private void connectSections(){
		for(int i=0,len=sections.size();i<len;i++){
			if((i < len - 1) && (sections.get(i).popunitiDoKraja)){
				sections.get(i).duration = sections.get(i + 1).startTime - sections.get(i).startTime;
			}
			if((i < len - 1) && (!sections.get(i).popunitiDoKraja)){
				sections.get(i + 1).startTime = sections.get(i).startTime + sections.get(i).duration;
			}
		}
	}

	public List<ListJItem> loadSongs(CatWithList cwl){
		List<ListJItem> ret = new ArrayList<ListJItem>();
		ListJItem item;
		for(int i=0,len=cwl.list.size();i<len;i++){
			item = cwl.list.get(i);
			for(int j=0;j<item.rang;j++)
				ret.add(item);
		}
		return ret;
	}

	private long fillSection(List<ListJItem> list, long start, ProgSection sec, ListJItem najava, ListJItem odjava){
		long end = sec.startTime + sec.duration;
		long curr = start;
		sec.startTime = start;
		if(najava != null){
			sec.songs.add(najava);
			curr += najava.duration / 1000;
		}
		long actualEnd = (odjava == null)? end : end-(odjava.duration/1000);
		ListJItem item;
		boolean first = true;
		int i;
		while(curr < end){
			item = list.get(rand.nextInt(list.size()));
			while(true){
				i = sec.songs.size();
				if((i > 2) && (sec.songs.get(i-1).fileName.equals(item.fileName) || 
						sec.songs.get(i-2).fileName.equals(item.fileName) || 
						sec.songs.get(i-3).fileName.equals(item.fileName)))
					item = list.get(rand.nextInt(list.size()));
				else break;
			}
			if(Math.abs(actualEnd - curr + (item.duration / 1000)) > Math.abs(actualEnd - curr)){
				sec.songs.add(item);
				if(first)
					first = false;
				else{
					curr += item.duration / 1000;
					if(sec.crossfade) curr -= 3000;
				}
			}else break;
		}
		if(odjava != null){
			sec.songs.add(odjava);
			curr += odjava.duration / 1000;
		}
		return curr;
	}

	private CatWithList getCatByName(String name){
		for (int i = 0, len = cats.size(); i < len; i++) {
			if(cats.get(i).cat.ime.equals(name))
				return cats.get(i);
		}
		return null;
	}

	public void fillSections(long curr){
		List<ListJItem> songPool;
		ProgSection sec;
		CatWithList cat;
		ListJItem najava, odjava;
		for(int i=0,len=sections.size();i<len;i++){
			sec = sections.get(i);
			if(!sec.popunitiDoKraja){
				curr += sec.duration;
				continue;
			} 
			cat = getCatByName(sec.catName);
			songPool = loadSongs(cat);
			try{
				najava = (sec.startToon)?new ListJItem(cat.cat.najavnaSpica):null;
			}catch(Exception ex){
				ex.printStackTrace(System.out);
				najava = null;
			}
			try{
				odjava = (sec.endToon)?new ListJItem(cat.cat.odjavnaSpica):null;
			}catch(Exception ex){
				ex.printStackTrace(System.out);
				odjava = null;
			}
			curr = fillSection(songPool, curr, sec, najava, odjava);
		}
	}

	private long calculateTime(ProgSection sec, long start){
		sec.startTime = start;
		long time = start;
		for(int i=0,len=sec.songs.size();i<len;i++){
			time += sec.songs.get(i).duration / 1000;
		}
		if(sec.crossfade){
			int decreaceBy = 1;
			if(sec.startToon) decreaceBy++;
			if(sec.endToon) decreaceBy++;
			time -= (sec.songs.size() - decreaceBy) * 3000;
		}
		return time;
	}

	private long correctSection(ProgSection sec, long start, long end){
		int endIndex = sec.songs.size() - 1;
		if(sec.endToon) endIndex--;
		long time = calculateTime(sec, start);
		if(end - time > 120000L){
			System.out.println("Ima manjka pesama.");
			// Ako ima manjka pesama
			try {
				CatWithList cwl = getCatByName(sec.catName);
				long diffTime = end - time;
				boolean found = true;
				ListJItem item;
				int i, len, tries = 20;
				while(diffTime > 0){
					found = true;
					while(found){
						found = false;
						item = cwl.list.get(rand.nextInt(cwl.list.size()));
						len = (endIndex - 3 < 0)? 0 : endIndex - 3;
						for(i=endIndex;i>=len && !found;i--){
							if(sec.songs.get(i).fullPath.equals(item.fullPath))
								found = true;
						}
						if((Math.abs(diffTime - (item.duration / 1000)) > diffTime))
							found = true;
						// Ako nije nadjen onda dodaj
						if(!found){
							sec.songs.add(endIndex, item);
							endIndex++;
							diffTime -= item.duration / 1000;
						}else{
							tries--;
							break;
						}
					}
					if(tries == 0) break;
				}
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}else if(time - end > 120000L){
			System.out.println("Ima viska pesama.");
			// Ako ima viska pesama
			long diffTime = time - end;
			ListJItem item;
			while(true){
				item = sec.songs.get(endIndex);
				if(Math.abs(diffTime - (item.duration / 1000)) < diffTime){
					sec.songs.remove(endIndex);
					endIndex--;
					diffTime -= item.duration / 1000;
					if(endIndex == -1) break;
					continue;
				}else break;
			}
		}
		return calculateTime(sec, start);
	}

	private void correctSections(){
		ProgSection sec, next;
		long time;
		for(int i=0,iLen=sections.size()-1;i<iLen;i++){
			sec = sections.get(i);
			while(sec.songs.size() == 0){
				iLen--;
				sections.remove(i);
				if(i >= iLen) return;
				sec = sections.get(i);
			}
			time = sec.startTime;
			next = sections.get(i + 1);
			if(sec.popunitiDoKraja){
				time = correctSection(sec, time, next.startTime);
				next.startTime = time;
			}else{
				time = calculateTime(sec, time);
				next.startTime = time;
			}
		}
	}

	public void generate(long begin, long end){
		sections.clear();
		loadCategories();
		loadScheduledTypes();
		begin -= 7200000L;
		plStart = begin;
		plEnd = end;
		createSections(begin,end);
		connectSections();
		fillSections(begin);
		correctSections();
	}

}
