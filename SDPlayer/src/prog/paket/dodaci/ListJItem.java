package prog.paket.dodaci;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.spi.mpeg.sampled.convert.DecodedMpegAudioInputStream;

import prog.paket.baza.struct.SongEntry;
import prog.paket.playlist.generator.PlayerWin;

public class ListJItem {

	/**
	 * Ime datoteke, bez ekstenzije.
	 * <br/><br/>
	 * File name with no extension (file type).
	 */
	public String fileName;

	/**
	 * Apsolutna putanja do datoteke.
	 * <br/><br/>
	 * Absolute path to file.
	 */
	public String fullPath;

	/**
	 * Dozvola postepenog prelaza.
	 * <br/><br/>
	 * Allow crossfade.
	 */
	public boolean crossfade;

	/**
	 * Trajanje u mikrosekundama.
	 * <br/><br/>
	 * Duration in microseconds.
	 */
	public long duration;

	/**
	 * Broj frejmova.
	 * <br/><br/>
	 * Song frame length.
	 */
	public long frameCount;

	/**
	 * Koliko puta u toku dana treba puštati ovu pesmu.
	 * <br/><br/>
	 * Times a day this song should be played.
	 */
	public byte rang;

	/**
	 * Označava da je pesma ručno prevučena u plej listu i samim tim ima prednost nad 
	 * pesmama koje su ubačene automatski, a pripadaju terminima kategorije 3 ili više 
	 * (manje važnijim terminima).
	 * <br/><br/>
	 * Indicates that item has been dropped to playlist and therefore takes precedance 
	 * over automaticaly inserted items from sections with category 3 and above.
	 */
	public boolean droppedToPL = false;

	public boolean pisiUIzvestaj = true;

	/**
	 * Format pesme (brza, spora, narodna itd.)
	 * <br/><br/>
	 * Song characteristics (fast, slow, folklore etc.)
	 */
	public List<Integer> cats = new ArrayList<Integer>();

	/**
	 * Konstruktor.
	 * <br/><br/>
	 * Constructor.
	 */
	public ListJItem(){
		fileName = null;
		fullPath = null;
		duration = -1;
		//crossfade = false;
		crossfade = true;
		rang = 1;
	}

	/**
	 * Konstruktor.
	 * <br/><br/>
	 * Constructor.
	 * @param file - Apstraktna putanja do pesme.
	 * @param file - Abstract path to song.
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	public ListJItem(File file) throws UnsupportedAudioFileException, IOException{
		//AudioInputStream in = PlayerWin.getInstance().mp3fr.getAudioInputStream(file);
		AudioFileFormat baseFormat = PlayerWin.getInstance().mp3fr.getAudioFileFormat(file);
		duration = (Long)baseFormat.properties().get("duration");
		frameCount = (Integer)baseFormat.properties().get("mp3.length.frames");
		fileName = file.getName();
		fullPath = file.getAbsolutePath();
		fileName = fileName.substring(0, fileName.lastIndexOf("."));
		//crossfade = false;
		crossfade = true;
		rang = 1;
	}

	public ListJItem(SongEntry entry) throws UnsupportedAudioFileException, IOException{
		AudioFileFormat baseFormat = PlayerWin.getInstance().mp3fr.getAudioFileFormat(
				new File(entry.getFullPath()));
		duration = (Long)baseFormat.properties().get("duration");
		frameCount = (Integer)baseFormat.properties().get("mp3.length.frames");
		fileName = entry.getFileName();
		fullPath = entry.getFullPath();
		fileName = fileName.substring(0, fileName.lastIndexOf("."));
		crossfade = true;
		rang = 1;
		cats = entry.getCats();
	}

	/**
	 * Provera stvarne dužine pesme radi odsecanja tišine. Ovaj posao je dodeljen klasi MP3_Reader.
	 * <br/><br/>
	 * Check for leading trailing silence. This task has been relegated to class MP3_Reader.
	 * @param data - Sadržaj pesme.
	 * @param data - Content of song.
	 * @throws FileNotFoundException
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	@Deprecated
	public void checkRealLength(byte[] data) throws FileNotFoundException, UnsupportedAudioFileException, IOException{
		// Pretpostavlja se da je data duzine 1048576
		AudioInputStream in = PlayerWin.getInstance().mp3fr.getAudioInputStream(
				new BufferedInputStream(new FileInputStream(fullPath), 4096));
		AudioFileFormat fileFormat = PlayerWin.getInstance().mp3fr.getAudioFileFormat(
				new File(fullPath));
		AudioFormat baseFormat = fileFormat.getFormat();
		AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
		                                            baseFormat.getSampleRate(),
		                                            16,
		                                            baseFormat.getChannels(),
		                                            baseFormat.getChannels() * 2,
		                                            baseFormat.getSampleRate(),
		                                            false);
		DecodedMpegAudioInputStream din = (DecodedMpegAudioInputStream)
				PlayerWin.getInstance().mp3formater.getAudioInputStream(decodedFormat, in);
		int pos, len = 0, step = baseFormat.getChannels() * 2, temp = 0;
		long songBegin = 0, songEnd = duration / 1000000;
		// Proverava pocetnu tisinu
		boolean finish = false, reachedEnd = false;
		while(!finish){
			// Puni bafer
			while(len < 1048576){
				pos = 1048576 - len;
				if(pos > 4096) pos = 4096;
				pos = din.read(data, len, pos);
				if(pos == -1){
					reachedEnd = true;
					break;
				}
				len += pos;
			}
			int start = 0/*, end*/;
			if(step == 4){
				pos = 0;
				while(pos < len){
					if((data[pos+1] > 0) || (data[pos+3] > 0))
						break;
					if((data[pos] > 120) || (data[pos+2] > 120))
						break;
					pos += 4;
				}
				start = pos;
			}else{
				pos = 0;
				while(pos < len){
					if((data[pos+1] > 0) || (data[pos] > 120))
						break;
					pos += 2;
				}
				start = pos;
			}
			temp += start;
			if(start < len){
				finish = true;
				songBegin = temp;
			}
		}
		// Prelazi na 9 sekundi do kraja
		if(!reachedEnd){
			temp = (int)(songBegin / step / baseFormat.getSampleRate());
			songEnd = (duration / 1000000) - 9 - temp;
			double koef = (double)songEnd * 1000000 / duration;
			din.skipFrames((long)(frameCount * koef));
			len = 0;
			if(frameCount * koef > 0){
				while(len < 1048576){
					pos = 1048576 - len;
					if(pos > 4096) pos = 4096;
					pos = din.read(data, len, pos);
					if(pos == -1){
						reachedEnd = true;
						break;
					}
					len += pos;
				}
			}
		}
		// Zatvara datoteku
		din.close();
		in.close();
		// Provera zavrsne tisine
		if(step == 4){
			pos = len - 1;
			while(pos >= 0){
				if((data[pos+1] > 0) || (data[pos+3] > 0))
					break;
				if((data[pos] > 120) || (data[pos+2] > 120))
					break;
				pos += 4;
			}
		}else{
			pos = len - 1;
			while(pos >= 0){
				if((data[pos+1] > 0) || (data[pos] > 120))
					break;
				pos += 2;
			}
		}
		len = pos + 1;
		songEnd -= (long)(len / step / baseFormat.getSampleRate());
		duration = songEnd - songBegin;
	}

	/**
	 * Konstruktor.
	 * <br/><br/>
	 * Constructor.
	 * @param file - Apstraktna putanja do pesme.
	 * @param file - Abstract path to song.
	 * @param crossfade - Da li je dozvoljeno koristiti postepeni prelaz na pesmi.
	 * @param crossfade - Is it allowed to use crossfade on song.
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	public ListJItem(File file, boolean crossfade) throws UnsupportedAudioFileException, IOException{
		this(file);
		this.crossfade = crossfade;
	}

	/**
	 * Konstruktor.
	 * <br/><br/>
	 * Constructor.
	 * @param filePath - Putanja do pesme.
	 * @param filePath - Path to song.
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	public ListJItem(String filePath) throws UnsupportedAudioFileException, IOException{
		this(new File(filePath));
	}

	/**
	 * Vraća trajanje zvučnog zapisa u sekundama.
	 * <br/><br/>
	 * Returns length in seconds.
	 */
	public long getDurationInSeconds(){
		return duration / 1000000;
	}

	/**
	 * Redefinisano da prikazuje ime datoteke.
	 * <br/><br/>
	 * Overriden to show field fileName.
	 */
	@Override
	public String toString() {
		return fileName;
	}

	/**
	 * Redefinisano da upoređuje putanju do datoteke.
	 * <br/><br/>
	 * Overriden to compare field <b>fullPath</b>.
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj instanceof ListJItem)
			return fullPath.equals(((ListJItem) obj).fullPath);
		else return false;
	}

	public ListJItem cloneItem(){
		ListJItem ret = new ListJItem();
		ret.fullPath = fullPath;
		ret.fileName = fullPath;
		int index = ret.fileName.indexOf("/");
		if(index != -1)
			ret.fileName = ret.fileName.substring(index + 1);
		index = ret.fileName.indexOf("\\");
		if(index != -1)
			ret.fileName = ret.fileName.substring(index + 1);
		index = ret.fileName.lastIndexOf(".");
		if(index != -1)
			ret.fileName = ret.fileName.substring(0, index);
		ret.crossfade = crossfade;
		ret.duration = duration;
		ret.frameCount = frameCount;
		ret.rang = rang;
		ret.droppedToPL = droppedToPL;
		ret.pisiUIzvestaj = pisiUIzvestaj;
		return ret;
	}

}
