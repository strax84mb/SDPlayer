package prog.paket.mp3;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class SD_Mic_Reader {

	protected AudioFormat audioFormat = null;
	protected TargetDataLine line = null;

	protected byte[] data = new byte[8192];
	protected int temp, length, pos, count;
	protected int block;
	protected boolean active;

	public SD_Mic_Reader(int targetIndex) throws LineUnavailableException{
		audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
                48000.0f, 16, 2, 4, 48000.0f, false);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat, 4096);
		line = (TargetDataLine)AudioSystem.getMixer(AudioSystem.getMixerInfo()[targetIndex]).getLine(info);
		line.open(audioFormat);
	}

	public boolean activateMic(){
		try{
			line.start();
			length = 0;
			active = true;
			return true;
		}catch(Exception e){
			return false;
		}
	}

	public boolean deactivateMic(){
		try{
			line.stop();
			line.flush();
			length = 0;
			active = false;
			return true;
		}catch(Exception e){
			return false;
		}
	}

	public boolean isActive(){
		return active;
	}

	public void destroy(){
		try{
			line.close();
		}catch(Exception e){}
	}

	public int readFromMic(int[] output, int offset, int outLen){
		try {
			if(active){
				line.start();
				temp = line.read(data, length, 8192);
				if(temp == 0) return 0;
				length += temp;
				temp = output.length - offset;
				if(temp > length / 2) temp = length / 2;
				for(count=0,pos=offset;count<temp;count+=2,pos++){
					block = (byte)(data[count+1]);
					block <<= 8;
					block |= data[count]&0xFF;
					output[pos] = block;
				}
			}
			if(length < 8){
				length = 0;
				return -1;
			}
			block = temp;
			temp = length - (count * 2);
			length -= count * 2;
			for(pos=0,count*=2;pos<temp;pos++,count++)
				data[pos] = data[count];
			return block;
		} catch (Exception e) {
			return -1;
		}
	}

}
