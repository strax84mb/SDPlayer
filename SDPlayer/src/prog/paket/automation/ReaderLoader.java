package prog.paket.automation;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import prog.paket.dodaci.ListJItem;
import prog.paket.mp3.SD_MP3_Player;
import prog.paket.mp3.SD_MP3_Reader;

public class ReaderLoader extends Thread {

	private SD_MP3_Player player;
	private SD_MP3_Reader reader;
	private boolean playRightAway;
	private int secStart;
	private ListJItem item;
	/*
	 * 0: nothing
	 * 1: load reader
	 * 9: exit
	 */
	private int command = 0;

	public Lock lock = new ReentrantLock();
	public Condition goAhead = lock.newCondition();

	public ReaderLoader(){
		command = 0;
	}

	public void setPlayer(SD_MP3_Player player){
		this.player = player;
	}

	public void orderLoading(SD_MP3_Reader reader, int secStart, ListJItem item, boolean playRightAway){
		this.reader = reader;
		this.secStart = secStart;
		this.item = item;
		this.playRightAway = playRightAway;
		this.command = 1;
	}

	public void orderSeek(SD_MP3_Reader reader, int secStart, ListJItem item){
		this.reader = reader;
		this.secStart = secStart;
		this.item = item;
		this.command = 2;
	}

	public void close(){
		command = 9;
	}

	public void cancelLoading(){
		command = 0;
	}

	@Override
	public void run() {
		boolean keepRunning = true;
		lock.lock();
		int count = 0;
		while(keepRunning){
			try{
				switch(command){
				case 0:
					goAhead.awaitNanos(50000000);
					break;
				case 1:
					command = 0;
					for(count=0;count<3;count++){
						try{
							reader.openStream(item.fullPath, secStart, item.pisiUIzvestaj);
							break;
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
					if(playRightAway) player.setCommand(12, null);
					break;
				case 2:
					command = 0;
					if(reader.isPlaying()) reader.stopPlay();
					if(item.duration / 1000000 - secStart < 5)
						secStart = (int)((item.duration / 1000000) - 5);
					reader.openStream(item.fullPath, (int)(item.frameCount * 
							((double)secStart / (item.duration / 1000000))), item.pisiUIzvestaj);
					player.setCommand(13, null);
					break;
				case 9:
					keepRunning = false;
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
					e1.printStackTrace();
				}
			}
		}
		lock.unlock();
	}

}
