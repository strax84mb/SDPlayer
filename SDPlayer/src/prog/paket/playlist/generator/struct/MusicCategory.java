package prog.paket.playlist.generator.struct;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MusicCategory {

	public String ime = "";
	public boolean ponedeljak = false; // 0x00000001
	public boolean utorak = false; // 0x00000002
	public boolean sreda = false; // 0x00000004
	public boolean cetvrtak = false; // 0x00000008
	public boolean petak = false; // 0x00000010
	public boolean subota = false; // 0x00000020
	public boolean nedelja = false; // 0x00000040
	public long begin = -1;
	public long end = -1;
	public boolean crossfade = false; // 0x00000200
	public boolean cuvajSadrzaj = false; // 0x00000400
	public boolean pisiUIzvestaj = false; // 0x00000800
	public boolean postujRedosled = false;
	// trajanje = -1; oznacava da trajanje emitovanja nije ograniceno (u minutima)
	public int trajanje = -1;
	public int prioritet = 2;
	// Oznacava da li je sadrzaj podeljen po vaznosti
	public boolean periodicno = false;
	public int prvo = -1;
	public int zadnje = -1;
	public int na_svakih = -1;
	public boolean terminsko = false;
	public int termin1 = -1;
	public int termin2 = -1;
	public int termin3 = -1;
	public int termin4 = -1;
	public int termin5 = -1;
	public int termin6 = -1;
	public int termin7 = -1;
	public int termin8 = -1;
	public int termin9 = -1;
	public int termin10 = -1;
	public String najavnaSpica = null;
	public String odjavnaSpica = null;

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MusicCategory)
			return ime.equals(((MusicCategory) obj).ime);
		else return false;
	}

	public static ArrayList<String> getSongsFromCatPath(String path){
		ArrayList<String> ret = new ArrayList<String>();
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(path), "UTF-8"));
			int len = Integer.parseInt(reader.readLine());
			for(int i=0;i<len;i++)
				ret.add(reader.readLine());
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return ret;
	}

	@Override
	public String toString() {
		return ime;
	}

}
