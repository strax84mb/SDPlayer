package prog.paket.playlist.generator.struct;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import prog.paket.dodaci.ListJItem;
import prog.paket.dodaci.ListJSection;

public class ProgSection {

	public List<ListJItem> songs = new ArrayList<ListJItem>();

	public long startTime = -1;
	public long duration = -1; // In seconds
	public String catName = null;
	public boolean startToon = false;
	public boolean endToon = false;
	public boolean pomeriti = false;
	public boolean popunitiDoKraja = false;
	public boolean crossfade = false;
	public long scheduledTime = -1;
	public byte prioritet = 6;
	public boolean pisiUIzvestaj = true;
	public ProgSectionType sectionType = ProgSectionType.TERMIN;

	public ProgSection(){}

	public ProgSection clone(){
		ProgSection ret = new ProgSection();
		ret.startTime = startTime;
		ret.duration = duration;
		ret.catName = catName;
		ret.startToon = startToon;
		ret.endToon = endToon;
		ret.pomeriti = pomeriti;
		ret.popunitiDoKraja = popunitiDoKraja;
		ret.crossfade = crossfade;
		ret.scheduledTime = scheduledTime;
		ret.sectionType = sectionType;
		return ret;
	}

	public ListJSection generateListJSection(){
		ListJSection ret = new ListJSection();
		ret.catName = catName;
		ret.startTime = startTime;
		ret.scheduledTime = scheduledTime;
		ret.duration = duration;
		ret.startToon = startToon;
		ret.endToon = endToon;
		ret.crossfade = crossfade;
		ret.popunitiDoKraja = popunitiDoKraja;
		ret.fileName = ">> " + catName + " <<";
		ret.prioritet = prioritet;
		return ret;
	}

	@Override
	public String toString() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTimeInMillis(startTime);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss d.M.yyyy");
		return sdf.format(cal.getTime()) + " - " + catName;
	}

	@Override
	public boolean equals(Object obj) {
		if((obj != null) && (obj instanceof ProgSection))
			return (((ProgSection)obj).startTime == startTime) && 
					(((ProgSection)obj).catName.equals(catName));
		else return false;
	}

	static public ProgSection load(File file, HashMap<String, ListJItem> map) throws IOException{
		ProgSection sec = new ProgSection();
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		sec.startTime = ois.readLong();
		sec.duration = ois.readLong();
		sec.catName = ois.readUTF();
		sec.startToon = ois.readBoolean();
		sec.endToon = ois.readBoolean();
		sec.pomeriti = ois.readBoolean();
		sec.popunitiDoKraja = ois.readBoolean();
		sec.crossfade = ois.readBoolean();
		sec.scheduledTime = ois.readLong();
		sec.prioritet = ois.readByte();
		sec.pisiUIzvestaj = ois.readBoolean();
		byte sectionType = ois.readByte();
		switch(sectionType){
		case 1:
			sec.sectionType = ProgSectionType.TERMIN;
			break;
		case 2:
			sec.sectionType = ProgSectionType.REKLAME;
			break;
		default:
			sec.sectionType = ProgSectionType.ID_DZINGL;
		}
		ListJItem item;
		String path;
		int len;
		for(int count=0,songLen=ois.readInt();count<songLen;count++){
			try{
				path = ois.readUTF();
				if((map != null) && (count > 0) && (count < songLen - 1)){
					item = map.get(path);
					if(item == null){
						item = new ListJItem(path);
						map.put(path, item);
					}
				}else{
					item = new ListJItem(path);
				}
				if(item.duration > 30000000L)
					item.crossfade = sec.crossfade;
				else item.crossfade = false;
				sec.songs.add(item);
				if(sec.startToon && (count == 0)){
					item.crossfade = false;
					item.pisiUIzvestaj = false;
				}else if(sec.endToon && (count == songLen - 1)){
					item.crossfade = false;
					item.pisiUIzvestaj = false;
				}else{
					item.pisiUIzvestaj = sec.pisiUIzvestaj;
				}
				len = ois.readInt();
				for(int i=0;i<len;i++)
					item.cats.add(new Integer(ois.readInt()));
			}catch(Exception e){
				e.printStackTrace(System.out);
			}
		}
		ois.close();
		fis.close();
		return sec;
	}

	static public void save(ProgSection sec) throws IOException{
		FileOutputStream fos = new FileOutputStream("plists/" + 
				String.valueOf(sec.startTime) + "-" + String.valueOf(sec.prioritet) + ".sec");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeLong(sec.startTime);
		oos.writeLong(sec.duration);
		oos.writeUTF(sec.catName);
		oos.writeBoolean(sec.startToon);
		oos.writeBoolean(sec.endToon);
		oos.writeBoolean(sec.pomeriti);
		oos.writeBoolean(sec.popunitiDoKraja);
		oos.writeBoolean(sec.crossfade);
		oos.writeLong(sec.scheduledTime);
		oos.writeByte(sec.prioritet);
		oos.writeBoolean(sec.pisiUIzvestaj);
		switch(sec.sectionType){
		case TERMIN:
			oos.writeByte(1);
			break;
		case REKLAME:
			oos.writeByte(2);
			break;
		default:
			oos.writeByte(0);
			break;
		}
		oos.writeInt(sec.songs.size());
		ListJItem item;
		for(int j=0,jLen=sec.songs.size();j<jLen;j++){
			item = sec.songs.get(j);
			oos.writeUTF(item.fullPath);
			oos.writeInt(item.cats.size());
			for(int i=0,iLen=item.cats.size();i<iLen;i++)
				oos.writeInt(item.cats.get(i));
		}
		oos.close();
		fos.close();
	}

}
