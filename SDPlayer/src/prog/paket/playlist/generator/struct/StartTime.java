package prog.paket.playlist.generator.struct;

import java.util.Calendar;

public class StartTime {

	public String text = null;

	public long time = -1;

	public StartTime(){
		time = -1;
		text = null;
	}

	public StartTime(long time){
		this.time = time;
		text = null;
	}

	public StartTime(long time, Calendar cal){
		this.time = time;
		time2Text(cal);
	}

	public void time2Text(Calendar cal){
		if(time == -1){
			text = null;
			return;
		}
		cal.setTimeInMillis(time);
		int temp = cal.get(Calendar.HOUR_OF_DAY);
		text = (temp < 10)?"0" + String.valueOf(temp):String.valueOf(temp);
		temp = cal.get(Calendar.MINUTE);
		text += (temp < 10)?":0" + String.valueOf(temp):":" + String.valueOf(temp);
		temp = cal.get(Calendar.SECOND);
		text += (temp < 10)?":0" + String.valueOf(temp):":" + String.valueOf(temp);
	}

	public void setTime(long time, Calendar cal){
		this.time = time;
		time2Text(cal);
	}

	public void setTime(StartTime obj){
		this.time = obj.time;
		this.text = obj.text;
	}

	@Override
	public String toString() {
		if(time == -1)
			return "";
		else return text;
	}

}
