package prog.paket.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;

import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;

import prog.paket.dodaci.ListJItem;
import prog.paket.playlist.generator.PlayerWin;
import prog.paket.playlist.generator.struct.CatWithList;
import prog.paket.playlist.generator.struct.MusicCategory;
import prog.paket.playlist.generator.struct.PesFileFilter;

public class CWLUpdater {

	public static CatWithList old_CWL_Load(File file){
		try{
			CatWithList ret = new CatWithList();
			MusicCategory cat = new MusicCategory();
			String str;
			int len, i;
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
			File songFile;
			MpegAudioFileReader mp3fr = new MpegAudioFileReader();
			for(i=0;i<len;i++){
				try{
					songFile = new File(ois.readUTF());
					item = new ListJItem();
					AudioFileFormat baseFormat = mp3fr.getAudioFileFormat(songFile);
					item.duration = (Long)baseFormat.properties().get("duration");
					item.frameCount = (Integer)baseFormat.properties().get("mp3.length.frames");
					item.fileName = songFile.getName();
					item.fullPath = songFile.getAbsolutePath();
					item.fileName = item.fileName.substring(0, item.fileName.lastIndexOf("."));
					//crossfade = false;
					item.crossfade = true;
					item.rang = 1;
					// Citanje ranga
					item.rang = ois.readByte();
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

	public static void main(String[] args) {
		try{
			File dir = new File("mcats");
			File files[] = dir.listFiles(new PesFileFilter());
			CatWithList cwls[] = new CatWithList[files.length];
			for(int i=0,len=files.length;i<len;i++)
				cwls[i] = CWLUpdater.old_CWL_Load(files[i]);
			for(int i=0,len=cwls.length;i<len;i++)
				if(cwls[i] != null) cwls[i].save();
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
	}

}
