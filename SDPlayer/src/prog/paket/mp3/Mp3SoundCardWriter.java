package prog.paket.mp3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Mp3SoundCardWriter extends Thread {

	protected short[] inData = new short[2048];
	protected short block;
	protected int inLength, inIndex;

	protected byte[] data = new byte[4096];
	protected int length, index, counter;

	protected float gain;

	protected boolean thisShouldRun = true;
	protected boolean isThereError = false;
	protected int errorCount = 0;

	protected SourceDataLine line = null;
	protected AudioFormat audioFormat = null;
	protected SDBuffer buffer;

	protected final Lock lock = new ReentrantLock();
	protected final Condition lineReady = lock.newCondition();

	public Mp3SoundCardWriter(SDBuffer buffer) throws LineUnavailableException{
		this.buffer = buffer;
		audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
                48000.0f, 16, 2, 4, 48000.0f, false);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat, 4096);
		line = (SourceDataLine)AudioSystem.getMixer(null).getLine(info);
	}

	public void setGain(float gain){
		this.gain = gain; 
	}

	public void close(){
		thisShouldRun = false;
	}

	protected void convertToBytes(){
		index = line.write(data, 0, length);
		length -= index;
		// index vec drzi broj baytova koji su uspesno poslati kartici
		for(counter=0;counter<length;counter++,index++)
			data[counter] = data[index];
		counter = (4096 - length) / 2;
		if(counter > inLength) counter = inLength;
		for(index=0,inIndex=0;inIndex<counter;length+=2,inLength--){
			block = inData[inIndex++];
			block *= gain;
			data[index++] = (byte)(block);
			block >>= 8;
			data[index++] = (byte)(block);
		}
		for(counter=0;counter<inLength;){
			inData[counter++] = inData[inIndex++];
		}
	}

	@Override
	public void run() {
		lock.lock();
		try {
			line.open(audioFormat);
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
			isThereError = true;
			return;
		}
		line.start();
		while(thisShouldRun){
			try{
				if(inLength > 0){
					if(length < 3072)
						convertToBytes();
					else lineReady.awaitNanos(100);
				}else{
					if(length < 3072){
						inIndex = buffer.getOutLength();
						inIndex -= inIndex % 2;
						counter = 2048 - inLength;
						counter -= counter % 2;
						if(counter > inIndex) counter = inIndex;
						buffer.pop(inData, inLength, counter);
						inLength += counter;
						convertToBytes();
					}else lineReady.awaitNanos(100);
				}
			}catch(Exception e){
				e.printStackTrace();
				errorCount++;
				if(errorCount > 4){
					thisShouldRun = false;
					isThereError = true;
				}
			}
		}
		try{
			line.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		lock.unlock();
	}

}
