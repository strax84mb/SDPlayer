package prog.paket.playlist.generator.struct;

import java.util.Random;

public class Bingo {

	private int[] bingoPool;
	private int len, left, ret, temp;
	private Random rand;

	public int cycle;

	public Bingo(int len){
		bingoPool = new int[len];
		reset(len);
		this.cycle = 0;
	}

	public void reset(){
		reset(len);
	}

	public void reset(int len){
		for(temp=0;temp<len;temp++)
			bingoPool[temp] = temp;
		this.len = len;
		left = len;
		rand = new Random();
		cycle++;
	}

	/*
	 * Povratna vrednost:
	 * -1 - Istrosen bubanj (treba vratiti loptice)
	 * -2 - Istrosene dopune bubnja (gotova tombola)
	 */
	public int drawNumber(){
		if(left == 0) return -1;
		ret = rand.nextInt(left);
		temp = bingoPool[ret];
		left--;
		bingoPool[ret] = bingoPool[left];
		bingoPool[left] = temp;
		return temp;
	}

}
