package prog.paket.forme.reklame;

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

public class ScheduledItemsType {

	public String name;

	public ListJItem najava = null;

	public ListJItem odjava = null;

	public byte prioritet = 2;

	public ArrayList<ScheduledItem> items = new ArrayList<ScheduledItem>();

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ScheduledItemsType)
			return name.equals(((ScheduledItemsType) obj).name);
		else return false;
	}

	@Override
	public String toString() {
		return name;
	}

	public List<ScheduledItem> generateBlock(long startTime){
		List<ScheduledItem> ret = new ArrayList<ScheduledItem>(), start = new ArrayList<ScheduledItem>(), 
				end = new ArrayList<ScheduledItem>();
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(startTime);
		ScheduledItem item;
		int time = (cal.get(Calendar.HOUR_OF_DAY) * 60) + cal.get(Calendar.MINUTE);
		for(int i=0,len=items.size();i<len;i++){
			item = items.get(i);
			// Proverava interval
			if((item.begin > startTime) || (startTime > item.end))
				continue;
			// Proverava dan u nedelji
			switch(cal.get(Calendar.DAY_OF_WEEK)){
			case Calendar.MONDAY:
				if(!item.ponedeljak) continue;
				break;
			case Calendar.TUESDAY:
				if(!item.utorak) continue;
				break;
			case Calendar.WEDNESDAY:
				if(!item.sreda) continue;
				break;
			case Calendar.THURSDAY:
				if(!item.cetvrtak) continue;
				break;
			case Calendar.FRIDAY:
				if(!item.petak) continue;
				break;
			case Calendar.SATURDAY:
				if(!item.subota) continue;
				break;
			default:
				if(!item.nedelja) continue;
				break;
			}
			// Proveri termin
			for(int j=0,jLen=item.runTimes.size();j<jLen;j++){
				if(item.runTimes.get(j).time == time){
					switch(item.runTimes.get(j).rank){
					case 0:
						start.add(0, item);
						break;
					case 1:
						start.add(item);
						break;
					case 2:
						ret.add(item);
						break;
					case 3:
						end.add(0, item);
						break;
					default:
						end.add(item);
						break;
					}
				}
			}
		}
		// Mesa sredinu
		Random rand = new Random();
		for(int i=0,len=ret.size(),mixIndex=0;i<len;i++){
			mixIndex = rand.nextInt(len);
			item = ret.get(mixIndex);
			ret.set(mixIndex, ret.get(i));
			ret.set(i, item);
		}
		// Formiranje pocetka
		for(int i=0,len=start.size();i<len;i++)
			ret.add(i, start.get(i));
		// Formiranje kraja
		for(int i=0,len=end.size(),j=ret.size();i<len;i++)
			ret.add(j, end.get(i));
		return ret;
	}

	private void sortedInsert(List<Integer> times, int time){
		int i = 0, len = times.size();
		for(;i<len;i++){
			if(times.get(i).intValue() == time) return;
			if(time < times.get(i).intValue()) break;
		}
		times.add(i, time);
	}

	public List<Integer> getAllTimes(){
		List<Integer> ret = new ArrayList<Integer>();
		ScheduledItem item;
		for(int i=0,len=items.size();i<len;i++){
			item = items.get(i);
			for(int j=0,jLen=item.runTimes.size();j<jLen;j++)
				sortedInsert(ret, item.runTimes.get(j).time);
		}
		return ret;
	}

	static public ScheduledItemsType load(File file) throws IOException, UnsupportedAudioFileException{
		ScheduledItemsType type = new ScheduledItemsType();
		ScheduledItem item = null;
		TimeEntry entry = null;
		String pathStr;
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		type.name = ois.readUTF();
		pathStr = ois.readUTF();
		type.najava = (pathStr.equals("null"))?null:new ListJItem(pathStr);
		pathStr = ois.readUTF();
		type.odjava = (pathStr.equals("null"))?null:new ListJItem(pathStr);
		type.prioritet = ois.readByte();
		int count, count_len = ois.readInt(), j, jLen;
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
		return type;
	}

}
