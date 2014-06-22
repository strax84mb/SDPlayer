package prog.paket.test;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateWriter {

	public static void main(String[] args) {
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(1395049718387L);
		System.out.println(cal.getTime());
	}

}
