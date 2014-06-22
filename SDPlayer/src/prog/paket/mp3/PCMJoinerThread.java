package prog.paket.mp3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PCMJoinerThread extends Thread {

	protected float[] fadeIn = new float[480000];
	protected float[] fadeOut = new float[480000];
	protected int fadeSpan;

	/*
	 * Fade modes:
	 * 0 - No fade
	 * 1 - Fade in
	 * 2 - Fade out
	 */
	protected short[] inputA = new short[2048];
	protected int aIndex, aLength, aFadePos, aFadeMode = 0;
	protected short[] inputB = new short[2048];
	protected int bIndex, bLength, bFadePos, bFadeMode = 0;
	protected short[] output = new short[2048];
	protected int outIndex, outLength;

	protected int counter, pos;

	protected boolean thisShouldRun = true;

	protected SDBuffer inputBufferA, inputBufferB, outputBuffer;

	protected final Lock lock = new ReentrantLock();
	protected final Condition lineReady = lock.newCondition();

	public PCMJoinerThread(SDBuffer inputBufferA, SDBuffer inputBufferB, SDBuffer outputBuffer){
		this.inputBufferA = inputBufferA;
		this.inputBufferB = inputBufferB;
		this.outputBuffer = outputBuffer;
		aFadeMode = 0;
		bFadeMode = 0;
		aFadePos = 0;
		bFadePos = 0;
	}

	protected void startCrossFade(boolean leftFadeOut){
		aFadePos = 0;
		bFadePos = 0;
		if(leftFadeOut){
			aFadeMode = 2;
			bFadeMode = 1;
		}else{
			aFadeMode = 1;
			bFadeMode = 2;
		}
	}

	protected void startFade(boolean left, boolean out){
		if(left){
			aFadePos = 0;
			if(out) aFadeMode = 2;
				else aFadeMode = 1;
		}else{
			bFadePos = 0;
			if(out) bFadeMode = 2;
				else bFadeMode = 1;
		}
	}

	protected void endFade(){
		aFadeMode = 0;
		bFadeMode = 0;
	}

	protected int readChannelA(){
		if(aLength == 2048) return 0;
		aIndex = inputBufferA.getOutLength();
		pos = 2048 - aLength;
		if(pos > aIndex) pos = aIndex;
		inputBufferA.pop(inputA, aLength, pos);
		switch(aFadeMode){
		case 0:
			aLength += pos;
			break;
		case 1:
			for(counter=0;counter<pos;counter+=2){
				inputA[aLength++] *= fadeIn[aFadePos];
				inputA[aLength++] *= fadeIn[aFadePos++];
			}
			break;
		case 2:
			for(counter=0;counter<pos;counter+=2){
				inputA[aLength++] *= fadeOut[aFadePos];
				inputA[aLength++] *= fadeOut[aFadePos++];
			}
			break;
		}
		return pos;
	}

	protected int readChannelB(){
		if(bLength == 2048) return 0;
		bIndex = inputBufferB.getOutLength();
		pos = 2048 - bLength;
		if(pos > bIndex) pos = bIndex;
		inputBufferB.pop(inputB, bLength, pos);
		switch(bFadeMode){
		case 0:
			bLength += pos;
			break;
		case 1:
			for(counter=0;counter<pos;counter+=2){
				inputB[bLength++] *= fadeIn[bFadePos];
				inputB[bLength++] *= fadeIn[bFadePos++];
			}
			break;
		case 2:
			for(counter=0;counter<pos;counter+=2){
				inputB[bLength++] *= fadeOut[bFadePos];
				inputB[bLength++] *= fadeOut[bFadePos++];
			}
			break;
		}
		return pos;
		
	}

	protected boolean joinAndWriteOut(){
		// sabiranje signala
		pos = 2048 - outLength;
		if(pos == 0) return false;
		if(pos > aLength) pos = aLength;
		if(pos > bLength) pos = bLength;
		aIndex = Short.MAX_VALUE;
		bIndex = Short.MIN_VALUE;
		for(counter=0;counter<pos;counter++){
			outIndex = inputA[counter] + inputB[counter];
			if(outIndex > aIndex) outIndex = aIndex;
			if(outIndex < bIndex) outIndex = bIndex;
			output[outLength++] = (short)outIndex;
		}
		aLength -= pos;
		bLength -= pos;
		for(counter=0,aIndex=pos;counter<aLength;)
			inputA[counter++] = inputA[aIndex++];
		for(counter=0,bIndex=pos;counter<bLength;counter++)
			inputB[counter++] = inputB[bIndex++];
		// Slanje podataka u izlaz
		counter = outputBuffer.inputHasAvailable();
		if(counter == 0) return false;
		pos = (outLength > counter)?counter:outLength;
		if(pos > counter) pos = counter;
		outputBuffer.push(output, pos);
		outLength -= pos;
		for(counter=0,outIndex=pos;counter<outLength;)
			output[counter++] = output[outIndex++];
		return true;
	}

	public void close(){
		thisShouldRun = false;
	}

	@Override
	public void run() {
		lock.lock();
		try{
			while(thisShouldRun){
				if(aLength < 2048) aIndex = readChannelA();
				if(bLength < 2048) bIndex = readChannelB();
				if(!joinAndWriteOut()) lineReady.awaitNanos(100);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		lock.unlock();
	}

}
