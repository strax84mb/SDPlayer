package prog.paket.report;

import java.io.File;

public class Song {

	private File file;

	private long startTime = -1;

	private int durationInSeconds = -1;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public int getDurationInSeconds() {
		return durationInSeconds;
	}

	public void setDurationInSeconds(int durationInSeconds) {
		this.durationInSeconds = durationInSeconds;
	}

	public Song(){}

	public Song(File file, long startTime, int durationInSeconds){
		this.file = file;
		this.startTime = startTime;
		this.durationInSeconds = durationInSeconds;
	}

	@Override
	public String toString() {
		String temp = file.getName();
		return temp.substring(0, temp.length() - 4);
	}

}
