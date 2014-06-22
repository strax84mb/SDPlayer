package prog.paket.mp3;

public interface SDBuffer {

	public void reset(float inSampleRate);
	public void drain();
	public boolean inputAvailable();
	public boolean outputAvailable();
	public int inputHasAvailable();
	public int outputHasAvailable();
	public int getInLength();
	public int getOutLength();
	public boolean isInEmpty();
	public boolean isOutEmpty();
	public void push(short[] nums, int len);
	public void pop(short[] nums, int offset, int len);

}
