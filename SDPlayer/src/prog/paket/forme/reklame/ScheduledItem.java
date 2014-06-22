package prog.paket.forme.reklame;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;

import prog.paket.baza.struct.SongEntry;
import prog.paket.dodaci.ListJItem;
import prog.paket.dodaci.TimeEntry;

/*
 * Snima po obrascu:
 * fullPath
 * begin
 * end
 * mesati
 * prioritet
 */
public class ScheduledItem extends ListJItem {

	public long begin = -1;

	public long end = -1;

	public byte mestoUBloku = 2;

	public boolean ponedeljak = false;
	public boolean utorak = false;
	public boolean sreda = false;
	public boolean cetvrtak = false;
	public boolean petak = false;
	public boolean subota = false;
	public boolean nedelja = false;

	public List<TimeEntry> runTimes = new ArrayList<TimeEntry>();

	public ScheduledItem(String path) throws UnsupportedAudioFileException, IOException{
		super(new File(path));
		crossfade = false;
	}

	public ScheduledItem(File file) throws UnsupportedAudioFileException, IOException{
		super(file);
		crossfade = false;
	}

	public ScheduledItem(SongEntry entry) throws UnsupportedAudioFileException, IOException{
		super(entry);
		crossfade = false;
	}

	@Override
	public String toString() {
		return fileName;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ListJItem)
			return fullPath.equals(((ListJItem) obj).fileName);
		else return false;
	}

}
