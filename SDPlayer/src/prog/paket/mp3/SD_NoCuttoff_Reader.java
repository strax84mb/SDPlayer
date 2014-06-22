package prog.paket.mp3;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.spi.mpeg.sampled.convert.DecodedMpegAudioInputStream;
import prog.paket.playlist.generator.PlayerWin;

public class SD_NoCuttoff_Reader extends SD_MP3_Reader {

	public SD_NoCuttoff_Reader(String name){
		super(name);
	}

	//@Override
	public void openStream(String filename, int secStart)
			throws UnsupportedAudioFileException, IOException, URISyntaxException {
		in = PlayerWin.getInstance().mp3fr.getAudioInputStream(new BufferedInputStream(
				new FileInputStream(filename), 4096));
		din = null;
		AudioFormat baseFormat = in.getFormat();
		decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
		                                            baseFormat.getSampleRate(),
		                                            16,
		                                            baseFormat.getChannels(),
		                                            baseFormat.getChannels() * 2,
		                                            baseFormat.getSampleRate(),
		                                            false);
		din = (DecodedMpegAudioInputStream)PlayerWin.getInstance().mp3formater.getAudioInputStream(
				decodedFormat, in);
		//playing = true;
		inputStep = 1 / baseFormat.getSampleRate();
		normalStep = 1 / 48000.0f;
		sampleOffset = 0.0f;
		inputSampleRate = baseFormat.getSampleRate();
		streamPos = 0;
		stereo = (baseFormat.getChannels() == 1)?false:true;
		inLen = 0;
		ending = false;
		normCount = 0;
		normSquares = 0;
		reachedEnd = false;
		bufferUpByteArray();
		initiateNormalGain();
		if(reachedEnd) checkForTrailingSilence();
		loaded = true;
	}

	@Override
	public int resample(int[] resampled, int offset, int len) {
		if(!reachedEnd){
			//System.out.println("qwerty");
			bufferUpByteArray();
			if(reachedEnd){
				checkForTrailingSilence();
				ending = true;
			}
		}
		if(inputStep > normalStep){
			if(stereo) temp = resampleWhenFLTNStereo(resampled, offset, len);
			else temp = resampleWhenFLTNMono(resampled, offset, len);
			if(temp == -1) playing = false;
			return temp;
		}else if(inputStep == normalStep){
			if(stereo) temp = resampleWhenFEStereo(resampled, offset, len);
			else temp = resampleWhenFEMono(resampled, offset, len);
			if(temp == -1) playing = false;
			return temp;
		}else{
		}
			// TODO Add more functionality later
			return -1;
	}

	@Override
	protected void checkForTrailingSilence(){
		try{
			if(stereo){
				pos = inLen - 4;
				while(pos >= 0){
					if((sample[pos+1] != 0) || (sample[pos+3] != 0)) break;
					if((sample[pos] < -5) || (sample[pos] > 5)) break;
					if((sample[pos+2] < -5) || (sample[pos+2] > 5)) break;
					pos -= 4;
				}
				inLen = pos + 4;
			}else{
				pos = inLen - 2;
				while(pos >= 0){
					if((sample[pos+1] != 0) || (sample[pos] < -5) || (sample[pos] > 5)) break;
					pos -= 2;
				}
				inLen = pos + 2;
			}
			// Na 1000 zadnjih uzoraka (ako ih jos ima) postaviti postepeno pojacanje 
			// da bi se neutralisalo sustanje usled odsecanja
			if(stereo){
				if(inLen - 2000 < 0)
					pos = 0;
				else pos = inLen - 2000;
				for(count=499;pos<inLen;pos+=4,count--){
					// Levi kanal
					block = (byte)(sample[pos+1]);
					block <<= 8;
					block |= sample[pos]&0xFF;
					block *= farFade[count];
					sample[pos] = (byte)(block);
					block >>= 8;
					sample[pos+1] = (byte)(block);
					// Desni kanal
					block = (byte)(sample[pos+3]);
					block <<= 8;
					block |= sample[pos+2]&0xFF;
					block *= farFade[count];
					sample[pos+2] = (byte)(block);
					block >>= 8;
					sample[pos+3] = (byte)(block);
				}
			}else{
				if(inLen - 1000 < 0)
					pos = 0;
				else pos = inLen - 1000;
				for(count=499;pos<1000;pos+=2,count--){
					// Levi kanal
					block = (byte)(sample[pos+1]);
					block <<= 8;
					block |= sample[pos]&0xFF;
					block *= farFade[count];
					sample[pos] = (byte)(block);
					block >>= 8;
					sample[pos+1] = (byte)(block);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void bufferUpByteArray(){
		while(true){
			try {
				if(inLen == 1048576) break;
				temp = 1048576 - inLen;
				if(temp > 4096) temp = 4096;
				count = din.read(sample, inLen, temp);
				if(count == -1){
					reachedEnd = true;
					break;
				}
				inLen += count;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
