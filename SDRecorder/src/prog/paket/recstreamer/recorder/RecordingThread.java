package prog.paket.recstreamer.recorder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.jma.encoder.audio.IAudioEncoder;

import com.sswf.rtmp.Consumer;

public class RecordingThread extends Thread {

	protected AudioFormat audioFormat = null;
	protected TargetDataLine line = null;
	protected IAudioEncoder encoder;
	protected Consumer consumer;
	protected FileOutputStream stream;

	private ReentrantLock lock = new ReentrantLock();
	private Condition waitUp = lock.newCondition();

	private byte[] input, output;
	private int count = 0, inputCount = 0, outputCount = 0, i;
	private String tempString;

	private boolean running = true;

	public RecordingThread(int targetIndex) throws LineUnavailableException{
		audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
                48000.0f, 16, 2, 4, 48000.0f, false);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat, 4096);
		line = (TargetDataLine)AudioSystem.getMixer(AudioSystem.getMixerInfo()[targetIndex]).getLine(info);
		line.open(audioFormat);
		encoder = consumer.getEncoder().getAudioEncoder();
		input = new byte[encoder.getInputBufferSize()];
		output = new byte[encoder.getOutputBufferSize()];
	}

	public void closeThread(){
		running = false;
	}

	@Override
	public void run() {
		try{
			line.start();
			lock.lock();
			int inSize = encoder.getInputBufferSize();
			Calendar cal = new GregorianCalendar();
			tempString = 
			File file = ;
			stream = new FileOutputStream(file, true);
			while(running){
				try{
					inputCount = line.read(input, inputCount, inSize - inputCount);
					if(inputCount > 0)
						outputCount = encoder.encodeBuffer(input, 0, inputCount, output);
					else{
						waitUp.awaitNanos(5000000L);
						continue;
					}
					while(outputCount > 0){
						stream.write(output);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			outputCount = encoder.encodeFinish(output);
			if(stream != null)
				stream.close();
			lock.unlock();
			line.stop();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
