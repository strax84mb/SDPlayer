package prog.paket.mp3;

public class ShortBuffer extends SDBufferAdapter{

	protected short[] data = new short[2048];
	protected int counter, length, index;

	public ShortBuffer(){
		length = 0;
	}

	@Override
	public boolean inputAvailable() {
		return length < 2048;
	}

	@Override
	public boolean outputAvailable() {
		return length < 2048;
	}

	@Override
	public int inputHasAvailable() {
		return 2048 - length;
	}

	@Override
	public int outputHasAvailable() {
		return 2048 - length;
	}

	@Override
	public int getInLength() {
		return length;
	}

	@Override
	public int getOutLength() {
		return length;
	}

	@Override
	public boolean isInEmpty() {
		return length == 0;
	}

	@Override
	public boolean isOutEmpty() {
		return length == 0;
	}

	@Override
	public void push(short[] nums, int len) {
		for(counter=0;counter<len;)
			data[length++] = nums[counter++];
	}

	@Override
	public void pop(short[] nums, int offset, int len) {
		for(index=0,counter=offset;index<len;)
			nums[counter++] = data[index++];
		length -= len;
		for(index=0,counter=len;index<length;)
			data[index] = data[counter];
	}

}
