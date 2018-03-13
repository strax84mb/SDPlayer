package rs.trznica.dragan.forms.support;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DateUtils {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private static final Logger LOG = LoggerFactory.getLogger(DateUtils.class);

	public static String toTimestamp(Date date) {
		return sdf.format(date);
	}
	
	public static String toTimestamp(long millis) {
		return toTimestamp(new Date(millis));
	}
	
	public static Long toEpoch(String timestamp) {
		return toDate(timestamp).getTime();
	}
	
	public static Long toEpoch(Date date) {
		return date.getTime();
	}
	
	public static Date toDate(long millis) {
		return new Date(millis);
	}
	
	public static Date toDate(String timestamp) {
		try {
			return sdf.parse(timestamp);
		} catch (ParseException e) {
			LOG.error("Greska prilikom parsiranja vremenske oznake!", e);
			return null;
		}
	}
}
