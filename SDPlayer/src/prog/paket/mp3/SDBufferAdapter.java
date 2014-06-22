package prog.paket.mp3;

public class SDBufferAdapter implements SDBuffer {

	@Override
	public void reset(float inSampleRate) {}

	@Override
	public void drain() {}

	@Override
	public boolean inputAvailable() {
		return false;
	}

	@Override
	public boolean outputAvailable() {
		return false;
	}

	@Override
	public int inputHasAvailable() {
		return 0;
	}

	@Override
	public int outputHasAvailable() {
		return 0;
	}

	@Override
	public int getInLength() {
		return 0;
	}

	@Override
	public int getOutLength() {
		return 0;
	}

	@Override
	public boolean isInEmpty() {
		return false;
	}

	@Override
	public boolean isOutEmpty() {
		return false;
	}

	@Override
	public void push(short[] nums, int len) {}

	@Override
	public void pop(short[] nums, int offset, int len) {}

}
