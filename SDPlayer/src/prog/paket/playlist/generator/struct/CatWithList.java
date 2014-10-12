package prog.paket.playlist.generator.struct;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import prog.paket.dodaci.ListJItem;

public class CatWithList {

	public long startDzinglLen = -1;
	public long endDzinglLen = -1;

	public MusicCategory cat;

	public List<ListJItem> list;

	public CatWithList(){}

	public CatWithList(MusicCategory cat, List<ListJItem> list){
		this.cat = cat;
		this.list = list;
	}

	public boolean save(){
		try{
			File file = new File("mcats/" + cat.ime.toLowerCase() + ".pes");
			if(file.exists()) return false;
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeUTF(cat.ime);
			oos.writeBoolean(cat.ponedeljak);
			oos.writeBoolean(cat.utorak);
			oos.writeBoolean(cat.sreda);
			oos.writeBoolean(cat.cetvrtak);
			oos.writeBoolean(cat.petak);
			oos.writeBoolean(cat.subota);
			oos.writeBoolean(cat.nedelja);
			oos.writeLong(cat.begin);
			oos.writeLong(cat.end);
			oos.writeBoolean(cat.crossfade);
			oos.writeBoolean(cat.cuvajSadrzaj);
			oos.writeBoolean(cat.pisiUIzvestaj);
			oos.writeBoolean(cat.postujRedosled);
			oos.writeInt(cat.trajanje);
			oos.writeInt(cat.prioritet);
			oos.writeBoolean(cat.periodicno);
			oos.writeInt(cat.prvo);
			oos.writeInt(cat.zadnje);
			oos.writeInt(cat.na_svakih);
			oos.writeBoolean(cat.terminsko);
			oos.writeInt(cat.termin1);
			oos.writeInt(cat.termin2);
			oos.writeInt(cat.termin3);
			oos.writeInt(cat.termin4);
			oos.writeInt(cat.termin5);
			oos.writeInt(cat.termin6);
			oos.writeInt(cat.termin7);
			oos.writeInt(cat.termin8);
			oos.writeInt(cat.termin9);
			oos.writeInt(cat.termin10);
			oos.writeUTF((cat.najavnaSpica == null)?"null":cat.najavnaSpica);
			oos.writeUTF((cat.odjavnaSpica == null)?"null":cat.odjavnaSpica);
			oos.writeInt(list.size());
			for(int i=0,len=list.size();i<len;i++){
				oos.writeUTF(list.get(i).fullPath);
				oos.writeByte(list.get(i).rang);
				oos.writeInt(list.get(i).cats.size());
				for(int j=0,jLen=list.get(i).cats.size();j<jLen;j++)
					oos.writeInt(list.get(i).cats.get(j));
			}
			oos.close();
			fos.close();
			return true;
		}catch(Exception e){
			e.printStackTrace(System.out);
			return false;
		}
	}

	static public CatWithList load(String name){
		try{
			CatWithList ret = new CatWithList();
			MusicCategory cat = new MusicCategory();
			String str;
			int len, i;
			File file = new File("mcats/" + name.toLowerCase() + ".pes");
			if(!file.exists()) return null;
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			cat.ime = ois.readUTF();
			cat.ponedeljak = ois.readBoolean();
			cat.utorak = ois.readBoolean();
			cat.sreda = ois.readBoolean();
			cat.cetvrtak = ois.readBoolean();
			cat.petak = ois.readBoolean();
			cat.subota = ois.readBoolean();
			cat.nedelja = ois.readBoolean();
			cat.begin = ois.readLong();
			cat.end = ois.readLong();
			cat.crossfade = ois.readBoolean();
			cat.cuvajSadrzaj = ois.readBoolean();
			cat.pisiUIzvestaj = ois.readBoolean();
			cat.postujRedosled = ois.readBoolean();
			cat.trajanje = ois.readInt();
			cat.prioritet = ois.readInt();
			cat.periodicno = ois.readBoolean();
			cat.prvo = ois.readInt();
			cat.zadnje = ois.readInt();
			cat.na_svakih = ois.readInt();
			cat.terminsko = ois.readBoolean();
			cat.termin1 = ois.readInt();
			cat.termin2 = ois.readInt();
			cat.termin3 = ois.readInt();
			cat.termin4 = ois.readInt();
			cat.termin5 = ois.readInt();
			cat.termin6 = ois.readInt();
			cat.termin7 = ois.readInt();
			cat.termin8 = ois.readInt();
			cat.termin9 = ois.readInt();
			cat.termin10 = ois.readInt();
			str = ois.readUTF();
			if(str.equals("null"))
				cat.najavnaSpica = null;
			else{
				cat.najavnaSpica = str;
				try{
					ret.startDzinglLen = (new ListJItem(str)).duration / 1000;
				}catch(Exception e){e.printStackTrace(System.out);}
			}
			str = ois.readUTF();
			if(str.equals("null"))
				cat.odjavnaSpica = null;
			else{
				cat.odjavnaSpica = str;
				try{
					ret.endDzinglLen = (new ListJItem(str)).duration / 1000;
				}catch(Exception e){e.printStackTrace(System.out);}
			}
			ret.cat = cat;
			List<ListJItem> list = new ArrayList<ListJItem>();
			len = ois.readInt();
			ListJItem item;
			int jLen;
			for(i=0;i<len;i++){
				try{
					item = new ListJItem(ois.readUTF());
					item.rang = ois.readByte();
					jLen = ois.readInt();
					for(int j=0;j<jLen;j++)
						item.cats.add(new Integer(ois.readInt()));
					item.crossfade = (cat.crossfade)? ((item.duration < 9000000L)? false : true) : false;
					list.add(item);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
				}
			}
			ret.list = list;
			ois.close();
			fis.close();
			return ret;
		}catch(Exception e){
			e.printStackTrace(System.out);
			return null;
		}
	}

}
