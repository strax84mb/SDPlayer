package prog.paket.mp3;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import prog.paket.playlist.generator.PlayerWin;

import javazoom.spi.mpeg.sampled.convert.DecodedMpegAudioInputStream;
import javazoom.spi.mpeg.sampled.convert.MpegFormatConversionProvider;

public class SD_MP3_Reader {

	protected AudioInputStream in = null;
	//private AudioInputStream din = null;
	protected DecodedMpegAudioInputStream din = null;
	protected AudioFormat decodedFormat = null;
	//private RandomAccessFile raf;

	//private byte[] sample = new byte[4096];
	protected byte[] sample = new byte[1048576];
	protected int inLen, count, pos, outPos, temp;
	protected int block;
	protected long tempSquares;
	protected long normSquares, normCount;
	protected double normalGain, inputSampleRate;
	protected int y0, y1, y2, y3;
	protected double inputStep, normalStep, sampleOffset;
	private double mu, mu2, a0, a1, a2, a3;
	protected int streamPos;

	protected boolean playing = false, stereo, startCutoff, trailCutoff, ending, loaded = false;
	protected boolean reachedEnd, loading = false;
	public boolean reportIt;
	public int durationInSeconds;

	public String songPath = null;

	protected double[] farFade = new double[500];

	public String name;

	public SD_MP3_Reader(String name){
		this.name = name;
		for(int i=0;i<500;i++)
			farFade[i] = i / 500.0;
	}

	public boolean isPlayingPath(String path){
		if(songPath == null) return false;
		return songPath.equals(path);
	}

	public boolean isEnding(){
		return ending;
	}

	public boolean isLoading(){
		return loading;
	}

	public void markLoading(){
		loading = true;
	}

	public boolean isPlaying(){
		return playing;
	}

	public void startTrailCutoff(){
		trailCutoff = true;
	}

	public void play(){
		playing = true;
	}

	public void stopPlay(){
		playing = false;
		loaded = false;
	}

	public boolean isLoaded(){
		return loaded;
	}

	public void openStream(String filename, int secStart, boolean reportIt) throws UnsupportedAudioFileException, IOException, URISyntaxException{
		loading = true;
		try{
			if(in != null) closeStream();
		}catch(Exception e){e.printStackTrace(System.out);}
		//openStream(new File(filename));
		//in = AudioSystem.getAudioInputStream(new File(filename));
		//raf = new RandomAccessFile(new File(filename), "r");
		System.out.println(name + ": Loading file: " + filename);
		songPath = filename;
		this.reportIt = reportIt;
		in = PlayerWin.getInstance().mp3fr.getAudioInputStream(new BufferedInputStream(
				new FileInputStream(filename), 4096));
		din = null;
		System.out.println(name + ": Decoding file...");
		AudioFormat baseFormat = in.getFormat();
		Long dur = (Long)PlayerWin.getInstance().mp3fr.getAudioFileFormat(new File(filename))
				.properties().get("duration");
		durationInSeconds = (int)(dur.longValue() / 1000000);
		decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
		                                            baseFormat.getSampleRate(),
		                                            16,
		                                            baseFormat.getChannels(),
		                                            baseFormat.getChannels() * 2,
		                                            baseFormat.getSampleRate(),
		                                            false);
		/*
		din = (DecodedMpegAudioInputStream)PlayerWin.getInstance().mp3formater.getAudioInputStream(
				decodedFormat, in);
				*/
		din = (DecodedMpegAudioInputStream)(new MpegFormatConversionProvider()).getAudioInputStream(
				decodedFormat, in);
		System.out.println("Initializing " + name + "...");
		//playing = true;
		inputStep = 1 / baseFormat.getSampleRate();
		normalStep = 1 / 48000.0f;
		sampleOffset = 0.0f;
		inputSampleRate = baseFormat.getSampleRate();
		streamPos = 0;
		stereo = (baseFormat.getChannels() == 1)?false:true;
		inLen = 0;
		startCutoff = true;
		trailCutoff = false;
		reachedEnd = false;
		ending = false;
		normCount = 0;
		normSquares = 0;
		if(secStart != -1){
			/*
			normCount = din.skipFrames(secStart);
			din.mark((int)normCount);
			normCount = 0;
			*/
			/*
			while(tempSquares > 0){
				normSquares = din.skip(tempSquares);
				if(normSquares == 0) break;
				tempSquares -= normSquares;
			}
			din.skip(tempSquares);
			normCount = 0;
			normSquares = 0;
			*/
			bufferUpByteArray(secStart);
		}else bufferUpByteArray(0);
		checkForLeadingSilence();
		initiateNormalGain();
		loading = false;
		loaded = true;
		System.out.println(name + " is loaded with file: " + filename);
	}

	public void openStream(File file, int secStart, boolean reportIt) throws UnsupportedAudioFileException, IOException, URISyntaxException{
		openStream(file.getAbsoluteFile(), secStart, reportIt);
	}

	public String getSongPath(){
		return songPath;
	}

	public void closeStream(){
		try{
			if(din != null)
				din.close();
			if(in != null)
			in.close();
			//raf.close();
			//playing = false;
			songPath = null;
			loaded = false;
		}catch(Exception e){
			e.printStackTrace();
			//e.printStackTrace();
		}
	}

	// pos-8, pos-4, pos, pos+4
	public int interpolateLeft(int inPos, double offset){
		// Set y0, y1, y2, y3
		if(inPos + 4 <= 0){
			if(inPos + 8 <= 0){
				block = (byte)(sample[inPos+9]);
				block <<= 8;
				block |= sample[inPos+8]&0xFF;
				y2 = block;
				block = (byte)(sample[inPos+13]);
				block <<= 8;
				block |= sample[inPos+12]&0xFF;
				y3 = block;
				y1 = (2 * y2) - y3;
				y0 = (2 * y1) - y2;
			}else{
				block = (byte)(sample[inPos+5]);
				block <<= 8;
				block |= sample[inPos+4]&0xFF;
				y1 = block;
				block = (byte)(sample[inPos+9]);
				block <<= 8;
				block |= sample[inPos+8]&0xFF;
				y2 = block;
				block = (byte)(sample[inPos+13]);
				block <<= 8;
				block |= sample[inPos+12]&0xFF;
				y3 = block;
				y0 = (2 * y1) - y2;
			}
		}else{
			block = (byte)(sample[inPos+1]);
			block <<= 8;
			block |= sample[inPos]&0xFF;
			//System.out.print(block + " ");
			y0 = block;
			block = (byte)(sample[inPos+5]);
			block <<= 8;
			block |= sample[inPos+4]&0xFF;
			//System.out.print(block + " ");
			y1 = block;
			block = (byte)(sample[inPos+9]);
			block <<= 8;
			block |= sample[inPos+8]&0xFF;
			//System.out.print(block + " ");
			y2 = block;
			block = (byte)(sample[inPos+13]);
			block <<= 8;
			block |= sample[inPos+12]&0xFF;
			//System.out.println(block);
			y3 = block;
		}
		// Interpolate
		mu = inputStep - sampleOffset;
		mu2 = mu * mu;
		a0 = y3 - y2 - y0 + y1;
		a1 = y0 - y1 - a0;
		a2 = y2 - y0;
		a3 = y1;
		return (int)((a0 * mu * mu2) + (a1 * mu2) + (a2 * mu) + a3);
	}

	// pos-6, pos-2, pos+2, pos+6
	public int interpolateRight(int inPos, double offset){
		// Set y0, y1, y2, y3
		if(inPos + 2 <= 0){
			if(inPos + 6 <= 0){
				block = (byte)(sample[inPos+9]);
				block <<= 8;
				block |= sample[inPos+8]&0xFF;
				y2 = block;
				block = (byte)(sample[inPos+13]);
				block <<= 8;
				block |= sample[inPos+12]&0xFF;
				y3 = block;
				y1 = (2 * y2) - y3;
				y0 = (2 * y1) - y2;
			}else{
				block = (byte)(sample[inPos+5]);
				block <<= 8;
				block |= sample[inPos+4]&0xFF;
				y1 = block;
				block = (byte)(sample[inPos+9]);
				block <<= 8;
				block |= sample[inPos+8]&0xFF;
				y2 = block;
				block = (byte)(sample[inPos+13]);
				block <<= 8;
				block |= sample[inPos+12]&0xFF;
				y3 = block;
				y0 = (2 * y1) - y2;
			}
		}else{
			block = (byte)(sample[inPos+1]);
			block <<= 8;
			block |= sample[inPos]&0xFF;
			//System.out.print(block + " ");
			y0 = block;
			block = (byte)(sample[inPos+5]);
			block <<= 8;
			block |= sample[inPos+4]&0xFF;
			//System.out.print(block + " ");
			y1 = block;
			block = (byte)(sample[inPos+9]);
			block <<= 8;
			block |= sample[inPos+8]&0xFF;
			//System.out.print(block + " ");
			y2 = block;
			block = (byte)(sample[inPos+13]);
			block <<= 8;
			block |= sample[inPos+12]&0xFF;
			//System.out.println(block);
			y3 = block;
		}
		// Interpolate
		mu = inputStep - sampleOffset;
		mu2 = mu * mu;
		a0 = y3 - y2 - y0 + y1;
		a1 = y0 - y1 - a0;
		a2 = y2 - y0;
		a3 = y1;
		return (int)((a0 * mu * mu2) + (a1 * mu2) + (a2 * mu) + a3);
	}

	// pos-4, pos-2, pos, pos+2
	public int interpolateMono(int inPos, double offset){
		// Set y0, y1, y2, y3
		if(inPos - 2 < 0){
			if(inPos - 4 < 0){
				block = (byte)(sample[inPos+5]);
				block <<= 8;
				block |= sample[inPos+4]&0xFF;
				y2 = block;
				block = (byte)(sample[inPos+7]);
				block <<= 8;
				block |= sample[inPos+6]&0xFF;
				y3 = block;
				y1 = (2 * y2) - y3;
				y0 = (2 * y1) - y2;
			}else{
				block = (byte)(sample[inPos+3]);
				block <<= 8;
				block |= sample[inPos+2]&0xFF;
				y1 = block;
				block = (byte)(sample[inPos+5]);
				block <<= 8;
				block |= sample[inPos+4]&0xFF;
				y2 = block;
				block = (byte)(sample[inPos+7]);
				block <<= 8;
				block |= sample[inPos+6]&0xFF;
				y3 = block;
				y0 = (2 * y1) - y2;
			}
		}else{
			block = (byte)(sample[inPos+1]);
			block <<= 8;
			block |= sample[inPos]&0xFF;
			y0 = block;
			block = (byte)(sample[inPos+3]);
			block <<= 8;
			block |= sample[inPos+2]&0xFF;
			y1 = block;
			block = (byte)(sample[inPos+5]);
			block <<= 8;
			block |= sample[inPos+4]&0xFF;
			y2 = block;
			block = (byte)(sample[inPos+7]);
			block <<= 8;
			block |= sample[inPos+6]&0xFF;
			y3 = block;
		}
		// Interpolate
		mu = inputStep - sampleOffset;
		mu2 = mu * mu;
		a0 = y3 - y2 - y0 + y1;
		a1 = y0 - y1 - a0;
		a2 = y2 - y0;
		a3 = y1;
		return (int)((a0 * mu * mu2) + (a1 * mu2) + (a2 * mu) + a3);
	}

	private boolean checkIfHasSound(int left, int right){
		return !((left > -15) && (left < 15) && (right > -15) && (right < 15));
	}

	protected void initiateNormalGain(){
		temp = inLen;
		temp -= temp % 2;
		for(count=0;count<temp;count+=2){
			block = (byte)(sample[count+1]);
			block <<= 8;
			block |= sample[count]&0xFF;
			if(normSquares < 9223372000000000000L){
				normSquares += block * block;
				normCount++;
			}else break;
		}
		normalGain = 8000 / Math.sqrt(normSquares / normCount);
	}

	private void checkForLeadingSilence(){
		try{
			boolean finish = false;
			while(!finish){
				bufferUpByteArray(0);
				int start = 0/*, end*/;
				if(stereo){
					pos = 0;
					while(pos < inLen){
						if((sample[pos+1] > 0) || (sample[pos+3] > 0))
							break;
						if((sample[pos] > 120) || (sample[pos+2] > 120))
							break;
						pos += 4;
					}
					start = pos;
				}else{
					pos = 0;
					while(pos < inLen){
						if((sample[pos+1] > 0) || (sample[pos] > 120))
							break;
						pos += 2;
					}
					start = pos;
				}
				if(start < inLen) finish = true;
				for(pos=0;start<inLen;pos++,start++)
					sample[pos] = sample[start];
				inLen = pos;
			}
			/*
			 * Koristi srednju kvadratnu formulu (RMS)
			while(true){
				if(reachedEnd) break;
				if(inLen == 0) bufferUpByteArray(0);
				if(stereo){
					end = (start + 256 > inLen)?inLen:start + 256;
				}else{
					end = (start + 128 > inLen)?inLen:start + 128;
				}
				tempSquares = 0;
				for(pos=start;pos<end;pos+=2){
					block = (byte)(sample[pos+1]);
					block <<= 8;
					block |= sample[pos]&0xFF;
					tempSquares += block * block;
				}
				if((Math.sqrt(tempSquares * 2 / (end - start))  ) > 500)
					break;
				else{
					if(end == inLen){
						inLen = 0;
						start = 0;
					}else start = end;
				}
			}
			for(pos=0;start<inLen;pos++,start++)
				sample[pos] = sample[start];
			inLen = pos;
			bufferUpByteArray(0);
			*/
			// Na 1000 uzoraka postaviti postepeno pojacanje 
			// da bi se neutralisalo sustanje usled odsecanja
			if(stereo){
				for(pos=0,count=0;pos<2000;pos+=4,count++){
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
				for(pos=0,count=0;pos<1000;pos+=2,count++){
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

	protected void checkForTrailingSilence(){
		try{
			if(stereo){
				pos = inLen - 4;
				while(pos >= 0){
					if((sample[pos+1] > 0) || (sample[pos+3] > 0))
						break;
					if((sample[pos] > 120) || (sample[pos+2] > 120))
						break;
					pos -= 4;
				}
				inLen = pos + 4;
			}else{
				pos = inLen - 2;
				while(pos >= 0){
					if((sample[pos+1] > 0) || (sample[pos] > 120))
						break;
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

	// Kada je frekvencija ulaza manja od standardne
	// Resample When Frequency Less Than Normal
	public int resampleWhenFLTNStereo(int[] resampled, int offset, int len){
		if(reachedEnd && (inLen < 12))
			return -1;
		if((inLen < 12) || (offset == 2048)) return 0;
		outPos = offset;
		pos = streamPos;
		while(true){
			/*
			if(sampleOffset == 0){
				block = (byte)(sample[pos+1]);
				block <<= 8;
				block |= sample[pos]&0xFF;
				resampled[outPos++] = (int)(block * normalGain);
				block = (byte)(sample[pos+3]);
				block <<= 8;
				block |= sample[pos+2]&0xFF;
				resampled[outPos++] = (int)(block * normalGain);
				pos += 4;
				sampleOffset = inputStep;
			}else*/ if(sampleOffset < normalStep){
				sampleOffset += inputStep;
				pos += 4;
			}else{
				sampleOffset -= normalStep;
				resampled[outPos++] = (int)(interpolateLeft(pos-8, inputStep - sampleOffset) * normalGain);
				resampled[outPos++] = (int)(interpolateRight(pos-6, inputStep - sampleOffset) * normalGain);
				if(startCutoff || trailCutoff){
					if(checkIfHasSound(resampled[outPos-2], resampled[outPos-1]))
						startCutoff = false;
					else outPos -= 2;
				}
				//pos += 4;
			}
			if((count == len) || (pos >= inLen - 8) || (outPos == 2048)) break;
		}
		if(normSquares < 9223372000000000000L){
			for(count=offset;count<outPos;count++){
				temp = resampled[count];
				normSquares += temp * temp;
				if(normSquares >= 9223372000000000000L){
					normCount += count - offset;
					break;
				}
			}
			if(count == outPos) normCount += outPos - offset;
			normalGain = 8000 / Math.sqrt(normSquares / normCount);
		}
		for(count=0,temp=pos-8;temp<inLen;count++,temp++)
			sample[count] = sample[temp];
		streamPos = 8;
		inLen = count;
		return outPos - offset;
	}

	// Kada je frekvencija ulaza manja od standardne
	// Resample When Frequency Less Than Normal
	public int resampleWhenFLTNMono(int[] resampled, int offset, int len){
		if(reachedEnd && (inLen < 12)) return -1;
		outPos = offset;
		pos = streamPos;
		while(true){
			/*if(sampleOffset == 0){
				block = (byte)(sample[pos+1]);
				block <<= 8;
				block |= sample[pos]&0xFF;
				resampled[outPos++] = block;
				resampled[outPos++] = block;
				pos += 2;
				sampleOffset = inputStep;
			}else*/ if(sampleOffset < normalStep){
				sampleOffset += inputStep;
				pos += 2;
			}else{
				sampleOffset -= normalStep;
				temp = interpolateMono(pos-4, inputStep - sampleOffset);
				resampled[outPos++] = temp;
				resampled[outPos++] = temp;
				if(startCutoff || trailCutoff){
					if(checkIfHasSound(resampled[outPos-2], resampled[outPos-1]))
						startCutoff = false;
					else outPos -= 2;
				}
				//pos += 2;
			}
			if((count == len) || (pos >= inLen - 4) || (outPos == 2048)) break;
			//if((count == len) || (pos >= inLen)) break;
		}
		if(normSquares < 9223372000000000000L){
			for(count=offset;count<outPos;count++){
				temp = resampled[count];
				normSquares += temp * temp;
				if(normSquares >= 9223372000000000000L){
					normCount += count - offset;
					break;
				}
			}
			if(count == outPos) normCount += outPos - offset;
			normalGain = 8000 / Math.sqrt(normSquares / normCount);
		}
		for(count=0,temp=pos-4;temp<inLen;count++,temp++)
			sample[count] = sample[temp];
		streamPos = pos;
		inLen -= pos - 4;
		return outPos - offset;
	}

	public int resampleWhenFEStereo(int[] resampled, int offset, int len){
		if(reachedEnd && (inLen < 12)) return -1;
		if((inLen < 12) || (offset == 2048)) return 0;
		outPos = offset;
		for(pos=0;pos<inLen && outPos<len;pos+=4){
			block = (byte)(sample[pos+1]);
			block <<= 8;
			block |= sample[pos]&0xFF;
			//System.out.println(block);			
			resampled[outPos++] = block;
			block = (byte)(sample[pos+3]);
			block <<= 8;
			block |= sample[pos+2]&0xFF;
			resampled[outPos++] = block;
		}
		if(normSquares < 9223372000000000000L){
			for(count=offset;count<outPos;count++){
				temp = resampled[count];
				normSquares += temp * temp;
				if(normSquares >= 9223372000000000000L){
					normCount += count - offset;
					break;
				}
			}
			if(count == outPos) normCount += outPos - offset;
			normalGain = 8000 / Math.sqrt(normSquares / normCount);
		}
		for(count=0;pos<inLen;pos++,count++)
			sample[count] = sample[pos];
		inLen = count;
		return outPos - offset;
	}

	public int resampleWhenFEMono(int[] resampled, int offset, int len){
		if(reachedEnd && (inLen < 12)) return -1;
		if((inLen < 12) || (offset == 2048)) return 0;
		outPos = offset;
		pos = streamPos;
		for(count=0;count<inLen && outPos<len;count+=2){
			block = (byte)(sample[pos+1]);
			block <<= 8;
			block |= sample[pos]&0xFF;
			//System.out.println(block);			
			resampled[outPos++] = block;
			resampled[outPos++] = block;
		}
		if(normSquares < 9223372000000000000L){
			for(count=offset;count<outPos;count++){
				temp = resampled[count];
				normSquares += temp * temp;
				if(normSquares >= 9223372000000000000L){
					normCount += count - offset;
					break;
				}
			}
			if(count == outPos) normCount += outPos - offset;
			normalGain = 8000 / Math.sqrt(normSquares / normCount);
		}
		for(count=0,temp=pos-8;temp<inLen;count++,temp++)
			sample[count] = sample[temp];
		streamPos = 8;
		inLen -= pos - 8;
		return outPos - offset;
	}

	public int resample(int[] resampled, int offset, int len){
		if(!trailCutoff)
			fillByteArray();
		else{
			if(!reachedEnd){
				bufferUpByteArray(0);
				if(reachedEnd){
					checkForTrailingSilence();
					ending = true;
				}
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

	public void bufferUpByteArray(long skipFrames){
		long skip = skipFrames;
		while(true){
			try {
				/*
				while(skipBytes > 0){
					count = din.read(sample, 0, (int)((skipBytes>=1048576)?1048576:skipBytes));
					skipBytes -= count;
				}
				*/
				if(skip > 0){
					//count = 0;
					//while(count <= 0)
						//count = din.read(sample, 0, 128);
					//raf.seek(din.skipFrames(skip));
					skip = din.skipFrames(skip);
					skip = 0;
				}
				//count = din.read(sample, inLen, (inLen > 2048)?1048576-inLen:2048);
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
		//System.out.println(inLen);
	}

	public int fillByteArray(){
		try {
			int temp = 1048576 - inLen;
			if(temp > 4096) temp = 4096;
			if(temp <= 0) return 0;
			count = din.read(sample, inLen, temp);
			if(count == -1) reachedEnd = true;
			else inLen += count;
			return count;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public long getTimeLeft(){
		if(reachedEnd)
			return (long)(inLen * 48000.0 / inputSampleRate / ((stereo)?4:2));
		else return Long.MAX_VALUE;
	}

}
