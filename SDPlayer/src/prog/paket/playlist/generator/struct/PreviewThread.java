package prog.paket.playlist.generator.struct;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import prog.paket.mp3.SD_MP3_Reader;
import prog.paket.playlist.generator.SoundPreviewDlg;

public class PreviewThread extends Thread {

	private SourceDataLine line;
	private AudioFormat audioFormat;
	private boolean shouldEnd = false, newLoad = false;
	private int frameStart = -1;
	private String filePath;

	private SD_MP3_Reader reader;
	private SoundPreviewDlg dlg;
	private int data[] = new int[2048], len = 0, wholeSecondCounter, outLen = 0;
	private byte output[] = new byte[4096];
	private double gain = 1;

	public Lock lock = new ReentrantLock();
	public Condition waitUp = lock.newCondition();

	public PreviewThread(int sourceIndex, String filePath, SoundPreviewDlg dlg) 
			throws LineUnavailableException{
		this.filePath = filePath;
		this.dlg = dlg;
		audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
                48000.0f, 16, 2, 4, 48000.0f, false);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat, 4096);
		line = (SourceDataLine)AudioSystem.getMixer(AudioSystem.getMixerInfo()[sourceIndex]).getLine(info);
		if(!line.isOpen())
			line.open(audioFormat);
		reader = new SD_MP3_Reader("Preslusavanje");
	}

	public void setGain(double gain){
		this.gain = gain;
	}

	public void startPlayFrom(int frameStart) 
			throws UnsupportedAudioFileException, IOException, URISyntaxException{
		this.frameStart = frameStart;
		newLoad = true;
	}

	public void endThread(){
		shouldEnd = true;
	}

	@Override
	public void run() {
		line.start();
		lock.lock();
		int temp, i, block;
		boolean reachedEnd = false;
		while(!shouldEnd){
			try{
				if(newLoad){
					reader.openStream(filePath, frameStart, false);
					newLoad = false;
					len = 0;
					wholeSecondCounter = 0;
				}
				if((len < 4) && reachedEnd){
					waitUp.awaitNanos(1000000000);
					continue;
				}
				if(!reachedEnd || (len < 2048)){
					temp = reader.resample(data, len, 2048);
					if(temp == -1)
						reachedEnd = true;
					else len += temp;
				}else continue;
				temp = line.available();
				if(temp < 4){
					waitUp.awaitNanos(10000000);
					continue;
				}
				if(temp > len * 2)
					temp = len;
				else temp /= 2;
				outLen = 0;
				for(i=0;i<temp;i+=2){
					block = (int)(data[i] * gain);
					if(block > Short.MAX_VALUE){
						//System.out.println(block);
						block = Short.MAX_VALUE;
					}
					if(block < Short.MIN_VALUE){
						//System.out.println(block);
						block = Short.MIN_VALUE;
					}
					output[outLen++] = (byte)(block);
					block >>= 8;
					output[outLen++] = (byte)(block);
					block = (int)(data[i+1] * gain);
					if(block > Short.MAX_VALUE){
						//System.out.println(block);
						block = Short.MAX_VALUE;
					}
					if(block < Short.MIN_VALUE){
						//System.out.println(block);
						block = Short.MIN_VALUE;
					}
					output[outLen++] = (byte)(block);
					block >>= 8;
					output[outLen++] = (byte)(block);
					if((++wholeSecondCounter) >= 48000){
						wholeSecondCounter -= 48000;
						dlg.adjustPlayTime();
					}
				}
				line.write(output, 0, outLen);
				for(i=0;temp<len;temp++,i++)
					data[i] = data[temp];
				len = i;
				temp = 0;
			}catch(Exception e){
				e.printStackTrace(System.out);
			}
		}
		lock.unlock();
		line.stop();
		line.close();
		reader.closeStream();
	}

}
