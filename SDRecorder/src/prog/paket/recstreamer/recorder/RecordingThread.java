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

import org.jma.MetaData;
import org.jma.encoder.IEncoder;
import org.jma.encoder.audio.IAudioEncoder;
import org.jma.encoder.video.IVideoEncoder;
import org.red5.server.stream.codec.AudioCodec;

import com.sswf.io.encoder.Encoder;
import com.sswf.rtmp.ClientManager;
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
		Encoder enc = new Encoder();
		enc.add(AudioCodec.MP3, audioFormat);
		consumer = new ClientManager();
		consumer.setEncoder(enc);
		//encoder = consumer.getEncoder().getAudioEncoder();
		//encoder.setSourceFormat(audioFormat);
		input = new byte[encoder.getInputBufferSize()];
		output = new byte[encoder.getOutputBufferSize()];
	}

	public void closeThread(){
		running = false;
	}

	private String createFilename(){
		Calendar cal = new GregorianCalendar();
		tempString = String.valueOf(cal.get(Calendar.YEAR));
		i = cal.get(Calendar.MONTH);
		tempString += "-" + ((i < 10)? "0" + String.valueOf(i) : String.valueOf(i));
		i = cal.get(Calendar.DAY_OF_MONTH);
		tempString += "-" + ((i < 10)? "0" + String.valueOf(i) : String.valueOf(i));
		i = cal.get(Calendar.HOUR_OF_DAY);
		tempString += "-----" + ((i < 10)? "0" + String.valueOf(i) : String.valueOf(i));
		i = cal.get(Calendar.MINUTE);
		tempString += "-" + ((i < 10)? "0" + String.valueOf(i) : String.valueOf(i));
		i = cal.get(Calendar.SECOND);
		tempString += "-" + ((i < 10)? "0" + String.valueOf(i) : String.valueOf(i));
		return tempString;
	}

	@Override
	public void run() {
		try{
			line.start();
			lock.lock();
			int inSize = encoder.getInputBufferSize();
			File file = new File("snimci/" + createFilename() + ".mp3");
			file.createNewFile();
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
						outputCount = 0;
						if(file.length() > 314572800L){
							outputCount = encoder.encodeFinish(output);
							stream.write(output);
							stream.close();
							file = new File("snimci/" + createFilename() + ".mp3");
							file.createNewFile();
							stream = new FileOutputStream(file, true);
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			outputCount = encoder.encodeFinish(output);
			stream.write(output);
			if(stream != null)
				stream.close();
			lock.unlock();
			line.stop();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
