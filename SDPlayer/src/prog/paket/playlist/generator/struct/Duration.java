package prog.paket.playlist.generator.struct;

public class Duration {

	public int time = -1;

	public Duration(){}

	public Duration(long longTime){
		this.time = (int)longTime;
	}

	public Duration(int time){
		this.time = time;
	}

	@Override
	public String toString() {
		if(time == -1) return "";
		int temp = time;
		int hours = temp / 3600;
		temp %= 3600; 
		int mins = temp / 60;
		temp %= 60;
		if(hours == 0)
			return String.valueOf(mins) + ((temp<10)?":0":":") + String.valueOf(temp);
		else return String.valueOf(hours) + ((mins<10)?":0":":") + String.valueOf(mins) + 
				((temp<10)?":0":":") + String.valueOf(temp);
	}

}
