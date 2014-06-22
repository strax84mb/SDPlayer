package prog.paket.test;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import javazoom.spi.mpeg.sampled.convert.DecodedMpegAudioInputStream;
import javazoom.spi.mpeg.sampled.convert.MpegFormatConversionProvider;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;

public class DataAccessComparisons {

	//private String songPath = "D:/AZEMINA GRBIC - Rastasmo se u Proljece.mp3";
	private String songPath = "d:/Muzika Arhiva/Novo/Alisa - Sanja.mp3";

	private long tb/* time before */, ta/* time after */, count;

	private byte data[] = new byte[4096];
	private int dataLen = 4096, dataRead;

	public void checkFileAccessTime(){
		// FileInputStream
		tb = System.currentTimeMillis();
		count = 0;
		try{
			FileInputStream fis = new FileInputStream(songPath);
			while(true){
				try{
					dataRead = fis.read(data, 0, dataLen);
					if(dataRead == -1) break;
					else count += dataRead;
				}catch(EOFException eofe){
					break;
				}
			}
			fis.close();
		}catch(Exception e){e.printStackTrace();}
		ta = System.currentTimeMillis();
		System.out.println("File Access (FileInputStream): " + String.valueOf(ta - tb) + 
				" ms. Bytes read: " + String.valueOf(count) + " bytes.");
		// Java NIO
		tb = System.currentTimeMillis();
		count = 0;
		try{
			InputStream fis = Files.newInputStream(Paths.get(songPath));
			while(true){
				try{
					dataRead = fis.read(data, 0, dataLen);
					if(dataRead == -1) break;
					else count += dataRead;
				}catch(EOFException eofe){
					break;
				}
			}
			fis.close();
		}catch(Exception e){e.printStackTrace();}
		ta = System.currentTimeMillis();
		System.out.println("File Access (Files.newInputStream()): " + String.valueOf(ta - tb) + 
				" ms. Bytes read: " + String.valueOf(count) + " bytes.");
		// BufferedInputStream
		tb = System.currentTimeMillis();
		count = 0;
		try{
			InputStream fis = new BufferedInputStream(new FileInputStream(songPath), 2048);
			while(true){
				try{
					dataRead = fis.read(data, 0, dataLen);
					if(dataRead == -1) break;
					else count += dataRead;
				}catch(EOFException eofe){
					break;
				}
			}
			fis.close();
		}catch(Exception e){e.printStackTrace();}
		ta = System.currentTimeMillis();
		System.out.println("File Access (BufferedInputStream): " + String.valueOf(ta - tb) + 
				" ms. Bytes read: " + String.valueOf(count) + " bytes.");
	} 

	public void checkAudioISReadTime(InputStream is, String description){
		try{
			tb = System.currentTimeMillis();
			count = 0;
			//AudioInputStream in = AudioSystem.getAudioInputStream(is);
			AudioInputStream in = new MpegAudioFileReader().getAudioInputStream(new File(songPath));
			AudioFormat baseFormat = in.getFormat();
			AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
			                                            baseFormat.getSampleRate(),
			                                            16,
			                                            baseFormat.getChannels(),
			                                            baseFormat.getChannels() * 2,
			                                            baseFormat.getSampleRate(),
			                                            false);
			//AudioInputStream din = AudioSystem.getAudioInputStream(decodedFormat, in);
			DecodedMpegAudioInputStream din = (DecodedMpegAudioInputStream)
					(new MpegFormatConversionProvider()).getAudioInputStream(decodedFormat, in);
			synchronized(din){
				@SuppressWarnings("unchecked")
				Map<Object, Object> map = din.properties();
				int frames = 0;//(Integer)map.get("mp3.length.frames");
				//while(din.skipFrames(1) != 0) frames++;
				System.out.println(frames);
				if(frames == 0){
					System.out.println(din.skipFrames(9000));
					while(true){
						try{
							dataRead = din.read(data, 0, dataLen);
							if(dataRead == -1){
								System.out.println("EOF");
								break;
							}
							else count += dataRead;
						}catch(EOFException eofe){
							eofe.printStackTrace();
							break;
						}
					}
				}
			}
			ta = System.currentTimeMillis();
			din.close();
			in.close();
			is.close();
			System.out.println("File Access (" + description + "): " + String.valueOf(ta - tb) + 
					" ms. Bytes read: " + String.valueOf(count) + " bytes.");
		}catch(Exception e){e.printStackTrace();}
	}

	public void compareAudioReadWithVariousIS(){
		try{
			//checkAudioISReadTime(new FileInputStream(songPath), "FileInputStream");
			//checkAudioISReadTime(Files.newInputStream(Paths.get(songPath)), "Files.newInputStream()");
			checkAudioISReadTime(new BufferedInputStream(new FileInputStream(songPath), 1048576), 
					"BufferedInputStream");
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DataAccessComparisons comp = new DataAccessComparisons();
		comp.compareAudioReadWithVariousIS();
	}

}
