package prog.paket.dodaci;

public class TimeEntry {

	public int time = -1;

	public byte rank = 2;

	public TimeEntry(){}

	public TimeEntry(int time){
		this.time = time;
	}

	public TimeEntry(int time, byte rank){
		this.time = time;
		this.rank = rank;
	}

	@Override
	public boolean equals(Object obj) {
		if((obj != null) && (obj instanceof TimeEntry))
			return ((TimeEntry)obj).time == time;
		else return false;
	}

	@Override
	public String toString() {
		if(time == -1) return "";
		String ret = String.valueOf(time % 60);
		if(ret.length() == 1) ret = "0" + ret;
		ret = String.valueOf(time / 60) + ":" + ret;
		if(ret.length() == 4) ret = "0" + ret;
		return ret;
	}

}
