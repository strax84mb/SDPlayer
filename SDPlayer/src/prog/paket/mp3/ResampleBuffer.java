package prog.paket.mp3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ResampleBuffer extends SDBufferAdapter {

	protected short[] outData = new short[2048];
	protected short[] inData = new short[2048];
	protected int inStart, inLength, inIndex;
	protected int outStart, outLength, outIndex;
	protected short popValue;
	protected float sampleOffset = 0;
	protected float outSampleInterval = 0;
	protected float inSampleInterval = 0;
	protected float inSampleRate;
	protected boolean shouldDrain = false;
	protected boolean spline  = false;

	protected Lock lock = new ReentrantLock();
	protected Condition isNotPushing = lock.newCondition();
	protected Condition isNotPoping = lock.newCondition();
	protected boolean pushing;
	protected boolean poping;

	public ResampleBuffer(){}

	public void reset(float inSampleRate){
		inStart = 0;
		inLength = 0;
		outStart = 0;
		outLength = 0;
		sampleOffset = 0;
		outSampleInterval = 1 / 48000.0f;
		inSampleInterval = 1 / inSampleRate;
		this.inSampleRate = inSampleRate;
		pushing = false;
		poping = false;
		shouldDrain = false;
	}

	public void setUseSpline(boolean spline){
		this.spline = spline;
	}

	public void drain(){
		shouldDrain = true;
	}

	public boolean inputAvailable() {
		return 2048 > inLength;
	}

	public boolean outputAvailable() {
		return 2048 > outLength;
	}

	public int inputHasAvailable() {
		return 2048 - inLength;
	}

	public int outputHasAvailable() {
		return 2048 - outLength;
	}

	public int getInLength() {
		return inLength;
	}

	public int getOutLength() {
		if(outLength == 0) return 0;
		return (shouldDrain)?outLength-2:outLength;
	}

	public boolean isInEmpty() {
		return inLength == 0;
	}

	public boolean isOutEmpty() {
		return outLength == 0;
	}

	public void push(short[] nums, int len) {
		lock.lock();
		try {
			if(poping) isNotPoping.await();
			pushing = true;
			if(!shouldDrain){
				for(inIndex=0;inIndex<len;)
					inData[inLength++] = nums[inIndex];
			}
			pushing = false;
			isNotPushing.signal();
		} catch (InterruptedException e) {}
		lock.unlock();
	}

	public void pop(short[] nums, int offset, int len) {
		lock.lock();
		try {
			if(pushing) isNotPushing.await();
			poping = true;
			for(outIndex=0;outIndex<len;)
				nums[offset++] = outData[outIndex++];
			outLength -= len;
			for(outStart=0;outStart<outLength;)
				outData[outStart++] = outData[outIndex++];
			if(inLength > 0) resample();
			poping = false;
			isNotPoping.signal();
		} catch (InterruptedException e) {}
		lock.unlock();
	}

	protected void resample() {
		if(inSampleRate == 48000.0f){
			outIndex = 2048 - outLength;
			if(outLength > inLength) outIndex = inLength;
			for(inIndex=0;inIndex<outIndex;)
				outData[outLength++] = inData[inIndex];
			// Pomeranje sadrzaja na pocetak
			inLength -= outIndex;
			for(outIndex=0;outIndex<inLength;)
				inData[outIndex++] = inData[inIndex++];
		}else{
			if(spline){
				// Splajn interpolacija
				if(inSampleRate < 48000.0f){
					inIndex = 0;
					while((outLength < 2048) && (inLength > 0)){
						if((sampleOffset == outSampleInterval) || (sampleOffset == 0f)){
							outData[outLength++] = inData[inIndex++];
							outData[outLength++] = inData[inIndex++];
							sampleOffset = inSampleInterval;
							inLength -= 2;
							continue;
						}
						// TODO Implement
					}
				}else{
					// TODO Implement
				}
			}else{
				// Linearna interpolacija (trapezna metoda)
				if(inSampleRate < 48000.0f){
					inIndex = 0;
					while((outLength < 2048) && (inLength > 0)){
						if((sampleOffset == outSampleInterval) || (sampleOffset == 0f)){
							outData[outLength++] = inData[inIndex++];
							outData[outLength++] = inData[inIndex++];
							sampleOffset = inSampleInterval;
							inLength -= 2;
							continue;
						}
						if(sampleOffset > outSampleInterval){
							popValue = (short)(outData[outLength] + ((inData[inIndex] - outData[outLength]) 
									/ outSampleInterval * sampleOffset));
							outData[outLength++] = popValue;
							popValue = (short)(outData[outLength] + ((inData[inIndex+1] - outData[outLength]) 
									/ outSampleInterval * sampleOffset));
							outData[outLength++] = popValue;
							sampleOffset -= outSampleInterval;
							continue;
						}
						if(sampleOffset < outSampleInterval){
							sampleOffset += inSampleInterval;
							inIndex += 2;
							inLength -= 2;
							continue;
						}
					}
					for(outIndex=0;outIndex<inLength;)
						inData[outIndex++] = inData[inIndex++];
				}else{
					short cashA=0, cashB=0;
					inIndex = 0;
					while((outLength < 2048) && (inLength > 0)){
						if((sampleOffset == inSampleInterval) || (sampleOffset == 0f)){
							cashA = inData[inIndex++];
							outData[outLength++] = cashA;
							cashB = inData[inIndex++];
							outData[outLength++] = cashB;
							sampleOffset = inSampleInterval;
							inLength -= 2;
							continue;
						}
						if(sampleOffset > inSampleInterval){
							sampleOffset -= inSampleInterval;
							cashA = inData[inIndex++];
							cashB = inData[inIndex++];
							inLength -= 2;
							continue;
						}
						if(sampleOffset < inSampleInterval){
							popValue = (short)(cashA + ((inData[inIndex] - cashA) 
									/ inSampleInterval * sampleOffset));
							outData[outLength++] = popValue;
							popValue = (short)(cashB + ((inData[inIndex+1] - cashB) 
									/ inSampleInterval * sampleOffset));
							outData[outLength++] = popValue;
							sampleOffset += outSampleInterval;
							continue;
						}
					}
					for(outIndex=0;outIndex<inLength;)
						inData[outIndex++] = inData[inIndex++];
				}
			}
		}
	}

}
