package prog.paket.mp3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import prog.paket.automation.ReaderLoader;
import prog.paket.dodaci.ListJItem;
import prog.paket.playlist.generator.PlayerWin;

public class SD_MP3_Player extends Thread {

	/*
	 * 0: inactive
	 * 1: active
	 * 2: standby
	 */
	protected int action = 0;
	/*
	 *  0: no command
	 *  1: seek in current stream
	 *  2: play "item" next without fadeIn
	 *  3: play "item" next without fadeOut
	 *  4: play "item" next with crossFade
	 *  5: pause
	 *  6: stop
	 *  7: close thread
	 *  8: resume playing
	 *  9: microphone on
	 * 10: microphone off
	 * 11: load jingle
	 * 12: finished loading reader
	 * 13: finished time jump
	 */
	protected int command = 0;
	public ListJItem item;
	protected boolean orderedNext = false;
	protected int songStartInSecs;
	protected int wholeSecondCounter;
	protected int durationInSeconds;
	protected int passedInSeconds;
	protected int secondsPassed;
	protected int minutesPassed;
	protected int wmCycle = 0, wmAmp = 0, wmTemp, wmRef;
	protected double wmAdjust;

	protected SourceDataLine line = null;
	protected AudioFormat audioFormat = null;

	protected int[] sum_resampled = new int[2048];
	protected int sum_inLen;

	protected int[] resampled_1 = new int[2048];
	protected int inLen_1;
	public SD_MP3_Reader reader_1;
	protected int[] resampled_2 = new int[2048];
	protected int inLen_2;
	public SD_MP3_Reader reader_2;

	protected long timeLeft, t1 = 0, t2;

	protected int[] dzingl_resampled = new int[2048];
	protected int dzingl_inLen, dzinglPos;
	protected SD_NoCuttoff_Reader dzingl_reader;
	protected String dzinglZaPlejbek = null;

	protected int[] mic_resampled = new int[2048];
	protected int mic_inLen;
	protected SD_Mic_Reader mic_reader;
	protected boolean micActive = false;

	protected int block;
	protected int outLength, count, temp, pos, proccessedPos, nextProccessedPos;
	protected byte[] output = new byte[4096];

	public boolean keepRunning = true;
	protected double main_Gain;

	private ReaderLoader loader;

	public Lock lock = new ReentrantLock();
	public Condition waitUp = lock.newCondition();

	protected PlayerWin mainWin;

	protected double[] fadeFun = null;
	protected double[] dzinglFadeFun = null, mainSoundFadeFun = null;
	protected int fadePos_1, fadePos_2, dzinglFadePos;
	protected double dzinglVol;

	protected enum FadeMode{
		FADE_IN,
		FADE_NORMAL,
		FADE_OUT
	}
	/*
	 * 0: fade in
	 * 1: normal
	 * 2: fade out
	 */
	protected FadeMode fade_1, fade_2;

	protected boolean startReader_1_Next, startReader_2_Next, endWithFade, checkForFadeOut;
	protected boolean playReader_1 = false;
	/*
	 * 0: tisina
	 * 1: fadeOut muzike
	 * 2: emitovanje dzingla
	 * 3: fadeIn muzike
	 * 
	 * "resampled_1[]" ce biti osnova koja se prebacuje u "output[]"
	 * i nad njime ce se vrsiti transformacije u vezi dzingla
	 */
	protected int dzinglRezim;

	protected void createFadeFun(){
		fadeFun = new double[144000];
		for(int i=0;i<144000;i++){
			fadeFun[i] = i / 144000.0;
		}
		dzinglFadeFun = new double[32000];
		for(int i=0;i<32000;i++){
			dzinglFadeFun[i] = (0.7 * i / 32000);
		}
		mainSoundFadeFun = new double[32000];
		for(int i=0;i<32000;i++){
			mainSoundFadeFun[i] = (0.7 * i / 32000) + 0.3;
		}
		dzinglVol = 0.7;
	}

	public void createDzingleFadeFun(double val){
		for(int i=0;i<32000;i++){
			dzinglFadeFun[i] = (val * i / 32000);
		}
		for(int i=0;i<32000;i++){
			mainSoundFadeFun[i] = (val * i / 32000) + 1 - val;
		}
		dzinglVol = val;
	}

	public void setSongSeekPos(int secStart){
		songStartInSecs = secStart;
	}

	public void loadDzingl(String dzingl){
		dzinglZaPlejbek = dzingl;
		command = 11;
	}

	public void setMainGain(double main_Gain){
		this.main_Gain = main_Gain;
	}

	public void setCommand(int comm, ListJItem item){
		this.command = comm;
		if(item != null) this.item = item;
	}

	public SD_MP3_Player(PlayerWin mainWin, int sourceIndex, int targetIndex, ReaderLoader loader) throws LineUnavailableException{
		audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
                48000.0f, 16, 2, 4, 48000.0f, false);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat, 4096);
		//line = (SourceDataLine)AudioSystem.getMixer(null).getLine(info);
		line = (SourceDataLine)AudioSystem.getMixer(AudioSystem.getMixerInfo()[sourceIndex]).getLine(info);
		line.open(audioFormat);
		//line.start();
		reader_1 = new SD_MP3_Reader("Reader 1");
		reader_2 = new SD_MP3_Reader("Reader 2");
		dzingl_reader = new SD_NoCuttoff_Reader("Mask Reader");
		proccessedPos = 0;
		endWithFade = false;
		this.mainWin = mainWin;
		createFadeFun();
		this.loader = loader;
	}

	protected int readLine(int[] resampled, int currentLength, SD_MP3_Reader reader){
		return reader.resample(resampled, currentLength, 2048);
	}

	public boolean loadSong(SD_MP3_Reader reader, int secStart){
		command = 0;
		try {
			while(!reader.isLoaded()){
				if(!reader.isLoading()){
					item = mainWin.playList.getNext();
					reader.markLoading();
					loader.orderLoading(reader, -1, item, false);
					System.out.println("Late order to load on: " + reader.name);
					waitUp.awaitNanos(50000);
				}
				waitUp.awaitNanos(10000);
			}
			reader.play();
			System.out.println(reader.name + " starts playing.");
			//reader.openStream(item.fullPath, secStart);
			mainWin.setWholeTime(item.duration, item.fileName);
			if((item.duration / 1000000) - songStartInSecs > 119 && PlayerWin.getInstance().autoPlayOn)
				PlayerWin.getInstance().autoPlay.orderPlayListCheck();
			wholeSecondCounter = 0;
			durationInSeconds = (int)(item.duration / 1000000L);
			passedInSeconds = 0;
			secondsPassed = 0;
			minutesPassed = 0;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	protected void setGainOnReader_1(){
		switch(fade_1){
		case FADE_IN:
			for(pos=0;pos<inLen_1 && fadePos_1<144000;pos+=2,fadePos_1++){
				resampled_1[pos] *= fadeFun[fadePos_1] * main_Gain;
				resampled_1[pos+1] *= fadeFun[fadePos_1] * main_Gain;
			}
			if(fadePos_1 == 144000){
				fade_1 = FadeMode.FADE_NORMAL;
				for(;pos<inLen_1;pos++)
					resampled_1[pos] *= main_Gain;
			}
			break;
		case FADE_NORMAL:
			timeLeft = reader_1.getTimeLeft();
			//if(timeLeft < inLen_1){
			if((timeLeft < 144000) && checkForFadeOut){
				checkForFadeOut = false;
				temp = (int)timeLeft;
				//item = mainWin.playList.getNext();
				if(item == null)
					endWithFade = false;
				else if(endWithFade && item.crossfade){
					// Pokrece "fade out"
					songStartInSecs = 0;
					playNext(false, true, -1);
					fade_1 = FadeMode.FADE_OUT;
					fadePos_1 = 143999;
					fadePos_2 = 0;
					fade_2 = FadeMode.FADE_IN;
				}
				temp = inLen_1;
			}else temp = inLen_1;
			// Primeni pojacanje
			for(pos=0;pos<temp;pos+=2){
				resampled_1[pos] *= main_Gain;
				resampled_1[pos+1] *= main_Gain;
			}
			if(timeLeft <= 16){
				// Pokreni sledecu pesmu bez stisavanja
				songStartInSecs = 0;
				playNext(false, false, -1);
				fade_1 = FadeMode.FADE_NORMAL;
			}
			// Nuliraj ostatak vektora
			if(pos < inLen_1)
				for(;pos</*inLen_1*/2048;pos++)
					resampled_1[pos] = 0;
			break;
		case FADE_OUT:
			timeLeft = reader_1.getTimeLeft();
			for(pos=0;pos<inLen_1 && fadePos_1>=0;pos+=2,fadePos_1--){
				resampled_1[pos] *= fadeFun[fadePos_1] * main_Gain;
				resampled_1[pos+1] *= fadeFun[fadePos_1] * main_Gain;
			}
			if((timeLeft < 30) || (fadePos_1 < 10)){
				for(;pos<inLen_1;pos++)
					resampled_1[pos] = 0;
				reader_1.stopPlay();
				System.out.println(reader_1.name + " has stopped.");
				fade_1 = FadeMode.FADE_NORMAL;
				fadePos_1 = 0;
			}
			break;
		}
	}

	protected void setGainOnReader_2(){
		switch(fade_2){
		case FADE_IN:
			for(pos=0;pos<inLen_2 && fadePos_2<144000;pos+=2,fadePos_2++){
				resampled_2[pos] *= fadeFun[fadePos_2] * main_Gain;
				resampled_2[pos+1] *= fadeFun[fadePos_2] * main_Gain;
			}
			if(fadePos_2 == 144000){
				fade_2 = FadeMode.FADE_NORMAL;
				for(;pos<inLen_2;pos++)
					resampled_2[pos] *= main_Gain;
			}
			break;
		case FADE_NORMAL:
			timeLeft = reader_2.getTimeLeft();
			//if(timeLeft < inLen_2){
			if((timeLeft < 144000) && checkForFadeOut){
				checkForFadeOut = false;
				temp = (int)timeLeft;
				//item = mainWin.playList.getNext();
				if(item == null)
					endWithFade = false;
				if(endWithFade && item.crossfade){
					// Pokrece "fade out"
					songStartInSecs = 0;
					playNext(true, true, -1);
					fade_2 = FadeMode.FADE_OUT;
					fadePos_2 = 143999;
					fadePos_1 = 0;
					fade_1 = FadeMode.FADE_IN;
				}
				temp = inLen_2;
			}else temp = inLen_2;
			// Primeni pojacanje
			for(pos=0;pos<temp;pos+=2){
				resampled_2[pos] *= main_Gain;
				resampled_2[pos+1] *= main_Gain;
			}
			if(timeLeft <= 16){
				// Pokreni sledecu pesmu bez stisavanja
				songStartInSecs = 0;
				playNext(true, false, -1);
				fade_2 = FadeMode.FADE_NORMAL;
			}
			// Nuliraj ostatak vektora
			if(pos < inLen_2)
				for(;pos</*inLen_2*/2048;pos++)
					resampled_2[pos] = 0;
			break;
		case FADE_OUT:
			timeLeft = reader_2.getTimeLeft();
			for(pos=0;pos<inLen_2 && fadePos_2>=0;pos+=2,fadePos_2--){
				resampled_2[pos] *= fadeFun[fadePos_2] * main_Gain;
				resampled_2[pos+1] *= fadeFun[fadePos_2] * main_Gain;
			}
			if((timeLeft < 30) || (fadePos_2 < 10)){
				for(;pos<inLen_2;pos++)
					resampled_2[pos] = 0;
				reader_2.stopPlay();
				System.out.println(reader_2.name + " has stopped.");
				fade_2 = FadeMode.FADE_NORMAL;
				fadePos_2 = 0;
			}
			break;
		}
	}

	protected void stickJinglOnData(){
		switch(dzinglRezim){
		case 0:
			return;
		case 1:
			// Ucitaj dzingl
			if(dzingl_reader.isPlaying() && (dzingl_inLen < 2048)){
				//pos = readLine(dzingl_resampled, dzingl_inLen, dzingl_reader);
				pos = dzingl_reader.resample(dzingl_resampled, dzingl_inLen, 2048 - dzingl_inLen);
				if(pos == -1){
					dzingl_reader.closeStream();
				}else dzingl_inLen += pos;
			}
			timeLeft = dzingl_reader.getTimeLeft();
			// Obara zvuk u intervalu od pola sekunde
			for(pos=0;pos<sum_inLen && pos<dzingl_inLen && dzinglFadePos>=0;pos+=2,dzinglFadePos--){
				/*
				sum_resampled[pos] *= dzinglFadeFun[dzinglFadePos];
				sum_resampled[pos+1] *= dzinglFadeFun[dzinglFadePos];
				*/
				sum_resampled[pos] = (int)((sum_resampled[pos] * mainSoundFadeFun[dzinglFadePos]) + 
						(dzingl_resampled[pos] * main_Gain * dzinglFadeFun[31999-dzinglFadePos]));
				sum_resampled[pos+1] = (int)((sum_resampled[pos+1] * mainSoundFadeFun[dzinglFadePos]) + 
						(dzingl_resampled[pos+1] * main_Gain * dzinglFadeFun[31999-dzinglFadePos]));
			}
			for(count=0;pos<dzingl_inLen;pos++,count++)
				dzingl_resampled[count] = dzingl_resampled[pos];
			dzingl_inLen = count;
			// Ako se nije stiglo do kraja vektora (obaranje zavrseno) onda
			// se prelazi u rezim emitovanja dzingla
			if(dzinglFadePos <= 2){
			//if(pos < sum_inLen){
				dzinglRezim = 2;
				// Ucitaj dzingl
				if(dzingl_reader.isPlaying()){
					//pos = readLine(dzingl_resampled, dzingl_inLen, dzingl_reader);
					pos = dzingl_reader.resample(dzingl_resampled, dzingl_inLen, 2048 - dzingl_inLen);
					if(pos == -1)
						dzingl_reader.closeStream();
					else dzingl_inLen += pos;
				}
				for(count=0;pos<sum_inLen && count<dzingl_inLen;pos+=2,count+=2){
					sum_resampled[pos] = (int)((sum_resampled[pos] * mainSoundFadeFun[0]) + 
							(dzingl_resampled[count] * main_Gain * dzinglVol));
					sum_resampled[pos+1] = (int)((sum_resampled[pos+1] * mainSoundFadeFun[0]) + 
							(dzingl_resampled[count+1] * main_Gain * dzinglVol));
				}
				for(count=0;pos<dzingl_inLen;pos++,count++)
					dzingl_resampled[count] = dzingl_resampled[pos];
				dzingl_inLen = count;
			}
			break;
		case 2:
			// Ucitaj dzingl
			if(dzingl_reader.isPlaying() && (dzingl_inLen < 2048)){
				//pos = readLine(dzingl_resampled, dzingl_inLen, dzingl_reader);
				pos = dzingl_reader.resample(dzingl_resampled, dzingl_inLen, 2048 - dzingl_inLen);
				if(pos == -1){
					dzingl_reader.closeStream();
				}else dzingl_inLen += pos;
			}
			timeLeft = dzingl_reader.getTimeLeft();
			for(pos=0;pos<sum_inLen && pos<dzingl_inLen && (timeLeft>Long.MAX_VALUE-32000 || 
					(timeLeft<Long.MAX_VALUE-32000 && timeLeft+((dzingl_inLen-pos)/2)>32000L));pos+=2){
				sum_resampled[pos] = (int)((sum_resampled[pos] * mainSoundFadeFun[0]) + 
						(dzingl_resampled[pos] * main_Gain * dzinglVol));
				sum_resampled[pos+1] = (int)((sum_resampled[pos+1] * mainSoundFadeFun[0]) + 
						(dzingl_resampled[pos+1] * main_Gain * dzinglVol));
			}
			// Privremeno smestanje vrednosti iz "pos"
			temp = pos;
			for(count=0;temp<dzingl_inLen;temp++,count++)
				dzingl_resampled[count] = dzingl_resampled[temp];
			dzingl_inLen = count;
			if(timeLeft <= 32000){
				dzinglRezim = 3;
				dzinglFadePos = 0;
				for(pos=0;pos<sum_inLen && dzinglFadePos<32000;pos+=2,dzinglFadePos++){
					sum_resampled[pos] = (int)((sum_resampled[pos] * mainSoundFadeFun[dzinglFadePos]) + 
							(dzingl_resampled[pos] * main_Gain * dzinglFadeFun[31999 - dzinglFadePos]));
					sum_resampled[pos+1] = (int)((sum_resampled[pos+1] * mainSoundFadeFun[dzinglFadePos]) + 
							(dzingl_resampled[pos+1] * main_Gain * dzinglFadeFun[31999 - dzinglFadePos]));
				}
				for(count=0;pos<dzingl_inLen;pos++,count++)
					dzingl_resampled[count] = dzingl_resampled[pos];
				dzingl_inLen = count;
			}
			/*
			if((dzingl_inLen == 0) && !dzingl_reader.isPlaying()){
				dzinglRezim = 3;
				dzinglFadePos = 0;
				for(;pos<sum_inLen;pos++){
					sum_resampled[pos] *= dzinglFadeFun[0];
				}
			}
			*/
			break;
		case 3:
			// Ucitaj dzingl
			if(dzingl_reader.isPlaying() && (dzingl_inLen < 2048)){
				//pos = readLine(dzingl_resampled, dzingl_inLen, dzingl_reader);
				pos = dzingl_reader.resample(dzingl_resampled, dzingl_inLen, 2048 - dzingl_inLen);
				if(pos == -1){
					dzingl_reader.closeStream();
				}else dzingl_inLen += pos;
			}
			for(pos=0;pos<sum_inLen && dzinglFadePos<32000;pos+=2,dzinglFadePos++){
				sum_resampled[pos] = (int)((sum_resampled[pos] * mainSoundFadeFun[dzinglFadePos]) + 
						(dzingl_resampled[pos] * main_Gain * dzinglFadeFun[31999 - dzinglFadePos]));
				sum_resampled[pos+1] = (int)((sum_resampled[pos+1] * mainSoundFadeFun[dzinglFadePos]) + 
						(dzingl_resampled[pos+1] * main_Gain * dzinglFadeFun[31999 - dzinglFadePos]));
				/*
				sum_resampled[pos] *= dzinglFadeInFun[dzinglFadePos];
				sum_resampled[pos+1] *= dzinglFadeInFun[dzinglFadePos];
				*/
			}
			for(count=0;pos<dzingl_inLen;pos++,count++)
				dzingl_resampled[count] = dzingl_resampled[pos];
			dzingl_inLen = count;
			timeLeft = dzingl_reader.getTimeLeft();
			if((dzinglFadePos >= 31999) || ((dzingl_inLen == 0) && (timeLeft <= 52))){
				dzinglRezim = 0;
				dzingl_reader.stopPlay();
			}
			break;
		}
	}

	protected boolean transferOut() throws InterruptedException{
		if(sum_inLen == 0) return false;
		while(true){
			temp = line.available();
			if(temp > 4096) break;
			//System.out.println("Waiting");
			waitUp.awaitNanos(5000000);
		}
		if(temp > sum_inLen*2) temp = sum_inLen;
			else temp /= 2;
		temp -= temp % 2;
		// Test
		// Test
		/*
		double newGain = PlayerWin.getInstance().volumeSlider.getValue() / 100.0;
		int maxResampled = 0;
		for(count=0;count<sum_inLen;count++){
			block = sum_resampled[count];
			if(Math.abs(block) > wmAmp) maxResampled = Math.abs(block);
		}
		*/
		// Test
		// Test
		wmTemp = 0;
		for(count=0;count<temp;count+=2){
			block = Math.abs(sum_resampled[count]);
			if(block > wmTemp) wmTemp = block;
		}
		wmRef = 100 - PlayerWin.getInstance().volumeSlider.getValue();
		if(wmRef == 100)
			main_Gain = 0.0;
		else{
			main_Gain = wmTemp / Short.MAX_VALUE;
			main_Gain = (-36.0 + (46.0 * main_Gain / 100.0)) / 20.0;
			main_Gain = Math.pow(10, main_Gain);
			wmAdjust = wmRef;
			wmAdjust = (-36.0 + (46.0 * wmAdjust / 100.0)) / 20.0;
			wmAdjust = Math.pow(10, wmAdjust);
			main_Gain = main_Gain / wmAdjust;
		}
		for(count=0;count<temp;count+=2){
			block = sum_resampled[count];
			if(Math.abs(block) > wmAmp) wmAmp = Math.abs(block);
			if(block > Short.MAX_VALUE){
				//System.out.println(block);
				block = Short.MAX_VALUE;
			}
			if(block < Short.MIN_VALUE){
				//System.out.println(block);
				block = Short.MIN_VALUE;
			}
			output[outLength++] = (byte)(block);
			block >>= 8;
			output[outLength++] = (byte)(block);
			block = sum_resampled[count+1];
			if(Math.abs(block) > wmAmp) wmAmp = Math.abs(block);
			if(block > Short.MAX_VALUE){
				//System.out.println(block);
				block = Short.MAX_VALUE;
			}
			if(block < Short.MIN_VALUE){
				//System.out.println(block);
				block = Short.MIN_VALUE;
			}
			output[outLength++] = (byte)(block);
			block >>= 8;
			output[outLength++] = (byte)(block);
			if((++wmCycle) >= 24000){
				wmCycle -= 24000;
				//mainWin.adjustWM(wmAmp * 100 / Short.MAX_VALUE);
				mainWin.adjustWM(wmAmp);
				wmAmp = 0;
			}
			if((++wholeSecondCounter) >= 48000){
				wholeSecondCounter -= 48000;
				adjustPlayTime();
				if(!orderedNext && (durationInSeconds - passedInSeconds < 10)){
					if(playReader_1){
						item = mainWin.playList.getNext();
						loader.orderLoading(reader_2, -1, item, false);
						System.out.println(reader_2.name + " is ordered to load.");
						reader_1.startTrailCutoff();
					}else{
						item = mainWin.playList.getNext();
						loader.orderLoading(reader_1, -1, item, false);
						System.out.println(reader_1.name + " is ordered to load.");
						reader_2.startTrailCutoff();
					}
					orderedNext = true;
				}
			}
		}
		//line.start();
		//line.drain();
		outLength -= line.write(output, 0, outLength);
		//line.stop();
		// Shifting input streams to begining
		for(count=0,pos=temp;pos<sum_inLen;count++,pos++)
			sum_resampled[count] = resampled_1[pos];
		sum_inLen = count;
		return true;
	}

	protected void adjustPlayTime(){
		passedInSeconds++;
		if(secondsPassed < 59)
			secondsPassed++;
		else{
			secondsPassed = 0;
			minutesPassed++;
		}
		mainWin.adjustTimePassed(minutesPassed, secondsPassed);
	}

	public void writeSong(SD_MP3_Reader reader){
		Calendar cal = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		try{
			File file = new File("reports/" + sdf.format(cal.getTime()) + ".report");
			if(!file.exists())
				file.createNewFile();
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(raf.length());
			raf.writeLong(cal.getTimeInMillis());
			raf.writeBoolean(reader.reportIt);
			raf.writeInt(reader.durationInSeconds);
			raf.writeUTF(reader.songPath);
			raf.close();
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
		try{
			File file = new File("reports/" + sdf.format(cal.getTime()) + ".surplus");
			if(!file.exists())
				file.createNewFile();
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(raf.length());
			raf.writeLong(cal.getTimeInMillis());
			raf.writeBoolean(reader.reportIt);
			raf.writeInt(reader.durationInSeconds);
			raf.writeUTF(reader.songPath);
			raf.close();
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
	}

	/*
	 * Proveri da li je potreban fade i ako nije otvara novu pesmu na istoj liniji
	 */
	protected void playNext(boolean load_reader_1, boolean doFadeIn, int secStart){
		try{
			orderedNext = false;
			playReader_1 = load_reader_1;
			if(load_reader_1){
				if(doFadeIn){
					fade_1 = FadeMode.FADE_IN;
					fade_2 = FadeMode.FADE_OUT;
					fadePos_1 = 0;
					fadePos_2 = 143999;
				}else{
					fade_1 = FadeMode.FADE_NORMAL;
					fade_2 = FadeMode.FADE_NORMAL;
					try{
						reader_2.stopPlay();
						System.out.println(reader_2.name + " has stopped.");
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				if(item != null){
					loadSong(reader_1, secStart);
					writeSong(reader_1);
					endWithFade = item.crossfade;
					checkForFadeOut = true;
				}else{
					loader.cancelLoading();
					mainWin.btnStop.doClick();
				}
			}else{
				if(doFadeIn){
					fade_2 = FadeMode.FADE_IN;
					fade_1 = FadeMode.FADE_OUT;
					fadePos_2 = 0;
					fadePos_1 = 143999;
				}else{
					fade_1 = FadeMode.FADE_NORMAL;
					fade_2 = FadeMode.FADE_NORMAL;
					try{
						reader_1.stopPlay();
						System.out.println(reader_1.name + " has stopped.");
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				if(item != null){
					loadSong(reader_2, secStart);
					writeSong(reader_2);
					endWithFade = item.crossfade;
					checkForFadeOut = true;
				}else{
					loader.cancelLoading();
					mainWin.btnStop.doClick();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	protected boolean getPCMData(){
		if((inLen_1 > 0) || (inLen_2 > 0)) return false;
		if(!reader_1.isPlaying() && !reader_2.isPlaying() && PlayerWin.getInstance().playerPlaying && 
				!PlayerWin.getInstance().playerPaused){
			if(playReader_1){
				try {
					if(!reader_1.isLoaded()){
						if(!reader_1.isLoading()){
							System.out.println("Out of turn loading of Reader 1.");
							item = mainWin.playList.getNext();
							reader_1.markLoading();
							loader.orderLoading(reader_1, -1, item, true);
							//playReader_1 = false;
						}
						System.out.println("Waiting for Reader 1 to load.");
					}
					while(reader_1.isLoading() && !reader_1.isLoaded())
						waitUp.awaitNanos(1000000L);
				} catch (InterruptedException e) {
					System.out.println("Error while waiting for load of Reader 1.");
					e.printStackTrace(System.out);
				}
				reader_1.play();
				System.out.println("Reader 1 is starting playback.");
			}else{
				try {
					if(!reader_2.isLoaded()){
						if(!reader_2.isLoading()){
							System.out.println("Out of turn loading of Reader 2.");
							item = mainWin.playList.getNext();
							reader_2.markLoading();
							loader.orderLoading(reader_2, -1, item, true);
							//playReader_1 = true;
						}
						System.out.println("Waiting for Reader 2 to load.");
					}
					while(reader_2.isLoading() && !reader_2.isLoaded())
						waitUp.awaitNanos(1000000L);
				} catch (InterruptedException e) {
					System.out.println("Error while waiting for load of Reader 2.");
					e.printStackTrace(System.out);
				}
				reader_2.play();
				System.out.println("Reader 2 is starting playback.");
			}
		}
		// Ucitaj i pojacaj
		if(reader_1.isPlaying()){
			pos = readLine(resampled_1, inLen_1, reader_1);
			if(pos == -1){
				reader_1.stopPlay();
				//reader_1.closeStream();
				System.out.println(reader_1.name + " has stopped.");
			}else inLen_1 += pos;
			setGainOnReader_1();
		}else{
			if(inLen_1 == -1) inLen_1 = 0;
			for(pos=inLen_1;pos<2048;pos++)
				resampled_1[pos] = 0;
		}
		// Ucitaj i pojacaj
		if(reader_2.isPlaying()){
			pos = readLine(resampled_2, inLen_2, reader_2);
			if(pos == -1){
				reader_2.stopPlay();
				//reader_2.closeStream();
				System.out.println(reader_2.name + " has stopped.");
			}else inLen_2 += pos;
			setGainOnReader_2();
		}else{
			if(inLen_2 == -1) inLen_2 = 0;
			for(pos=inLen_2;pos<2048;pos++)
				resampled_2[pos] = 0;
		}
		/*
		// Ucitaj dzingl
		if(dzingl_reader.isPlaying()){
			pos = readLine(dzingl_resampled, dzingl_inLen, dzingl_reader);
			if(pos == -1)
				dzingl_reader.closeStream();
			else dzingl_inLen += pos;
		}
		*/
		return true;
	}

	protected void signalSuperposition(){
		if(mainWin.playerPlaying && !mainWin.playerPaused){
			// Nuliraj do kraja
			if(inLen_1 == -1) inLen_1 = 0;
			for(count=inLen_1;count<2048;count++)
				resampled_1[count] = 0;
			// Nuliraj do kraja
			if(inLen_2 == -1) inLen_2 = 0;
			for(count=inLen_2;count<2048;count++)
				resampled_2[count] = 0;
			// Saberi signale
			temp = (inLen_1>inLen_2)?inLen_1:inLen_2;
			for(count=sum_inLen,pos=0;count<2048 && pos<temp;count++,pos++)
				sum_resampled[count] = resampled_1[pos] + resampled_2[pos];
			sum_inLen = count;
			// Pomeranje zaostalog zapisa na pocetak
			for(count=0,temp=pos;temp<inLen_1;temp++,count++)
				resampled_1[count] = resampled_1[temp];
			inLen_1 = count;
			for(count=0,temp=pos;temp<inLen_2;temp++,count++)
				resampled_2[count] = resampled_2[temp];
			inLen_2 = count;
		}
		// Nalepi signal sa mikrofona
		if((mic_reader != null) && mic_reader.isActive()){
			pos = mic_reader.readFromMic(mic_resampled, mic_inLen, 2048);
			mic_inLen += pos;
			for(count=sum_inLen,pos=0;count<2048 && pos<mic_inLen;count++,pos++){
				sum_resampled[count] = mic_resampled[pos];
			}
			for(count=0;pos<mic_inLen;count++,pos++){
				mic_resampled[count] = mic_resampled[pos];
			}
			mic_inLen = count;
		}
		// Nalepi dzingl
		stickJinglOnData();
	}

	@Override
	public void run() {
		lock.lock();
		line.start();
		while(keepRunning){
			try{
				switch(command){
				case 0:
					switch(action){
					case 0:
						waitUp.awaitNanos(30000000);
						break;
					case 1:
						getPCMData();
						signalSuperposition();
						if(!transferOut()) waitUp.awaitNanos(3000000);
						break;
					case 2:
						signalSuperposition();
						if(!transferOut()) waitUp.awaitNanos(3000000);
					}
					//waitUp.awaitNanos(300);
					break;
				case 1:
					if(item != null){
						if(reader_1.isPlayingPath(item.fullPath)){
							if(item.duration / 1000000 - songStartInSecs < 5)
								songStartInSecs = (int)((item.duration / 1000000) - 5);
							reader_1.openStream(item.fullPath, (int)(item.frameCount * 
									(((double)songStartInSecs) / (item.duration / 1000000))), 
									item.pisiUIzvestaj);
							//reader_1.openStream(item.fullPath, songStartInSecs);
							playNext(true, false, songStartInSecs);
						}else{
							if(item.duration / 1000000 - songStartInSecs < 5)
								songStartInSecs = (int)((item.duration / 1000000) - 5);
							reader_2.openStream(item.fullPath, (int)(item.frameCount * 
									(((double)songStartInSecs) / (item.duration / 1000000))), 
									item.pisiUIzvestaj);
							//reader_2.openStream(item.fullPath, songStartInSecs);
							playNext(false, false, songStartInSecs);
						}
						passedInSeconds = songStartInSecs;
						minutesPassed = songStartInSecs / 60;
						secondsPassed = songStartInSecs % 60;
					}
					action = 1;
					command = 0;
					break;
					/*
					if(item != null){
						if(!reader_1.isPlayingPath(item.fullPath)){
							loader.orderSeek(reader_1, songStartInSecs, item);
						}else{
							loader.orderSeek(reader_2, songStartInSecs, item);
						}
					}
					action = 1;
					command = 0;
					break;
					*/
				case 2:
				case 3:
				case 4:
					if(!reader_1.isPlaying()){
						loader.orderLoading(reader_1, -1, item, true);
						System.out.println(reader_1.name + " is ordered to load.");
					}else{
						loader.orderLoading(reader_2, -1, item, true);
						System.out.println(reader_2.name + " is ordered to load.");
					}
					action = 0;
					command = 0;
					break;
				case 5:
					action = 2;
					command = 0;
					break;
				case 6:
					if(reader_1.isPlaying()){
						reader_1.stopPlay();
						System.out.println(reader_1.name + " has stopped.");
					}
					if(reader_2.isPlaying()){
						reader_2.stopPlay();
						System.out.println(reader_2.name + " has stopped.");
					}
					line.flush();
					action = 0;
					command = 0;
					minutesPassed = 0;
					secondsPassed = 0;
					break;
				case 7:
					try{
						if(reader_1.isPlaying()) reader_1.closeStream();
					}catch(Exception e){}
					try{
						if(reader_2.isPlaying()) reader_2.closeStream();
					}catch(Exception e){}
					keepRunning = false;
					break;
				case 8:
					command = 0;
					action = 1;
					break;
				case 9:
					command = 0;
					if(mic_reader.activateMic()){
						micActive = true;
						mainWin.setMicActiveIcon();
					}
					break;
				case 10:
					command = 0;
					if(mic_reader.activateMic()){
						micActive = false;
						mainWin.setMicPassiveIcon();
					}
					break;
				case 11:
					command = 0;
					dzingl_reader.openStream(dzinglZaPlejbek, -1);
					dzingl_reader.play();
					dzinglRezim = 1;
					dzinglFadePos = 23999;
					break;
				case 12:
					if(!reader_1.isPlaying()){
						if(reader_2.isPlaying() && item.crossfade && endWithFade) fade_2 = FadeMode.FADE_OUT;
						playNext(true, item.crossfade && endWithFade, -1);
					}else{
						if(reader_1.isPlaying() && item.crossfade && endWithFade) fade_1 = FadeMode.FADE_OUT;
						playNext(false, item.crossfade && endWithFade, -1);
					}
					action = 1;
					command = 0;
					break;
				case 13:
					if(item != null){
						if(!reader_1.isPlayingPath(item.fullPath)){
							playNext(true, false, songStartInSecs);
						}else{
							playNext(false, false, songStartInSecs);
						}
						passedInSeconds = songStartInSecs;
						minutesPassed = songStartInSecs / 60;
						secondsPassed = songStartInSecs % 60;
					}
					action = 1;
					command = 0;
					break;
				}
			}catch(Exception e){
				try {
					e.printStackTrace(System.out);
					FileOutputStream fos = new FileOutputStream("greske.txt", true);
					PrintWriter writer = new PrintWriter(fos);
					writer.print("Time in milis: ");
					writer.println(System.currentTimeMillis());
					e.printStackTrace(writer);
					writer.close();
				} catch (Exception e1) {
					e1.printStackTrace(System.out);
				}
			}
		}
		line.close();
		lock.unlock();
	}

}
