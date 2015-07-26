package rs.trznica.dragan.forms.support;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFormattedTextField.AbstractFormatter;

public class DateLabelFormatter extends AbstractFormatter {

	private static final long serialVersionUID = 8662380629034428144L;

	private static String FORMAT_STRING = "dd.MM.yyyy";

	private SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_STRING);

	@Override
	public Object stringToValue(String dateString) throws ParseException {
		return sdf.parseObject(dateString);
	}

	@Override
	public String valueToString(Object date) throws ParseException {
		if (date instanceof Date) {
			return sdf.format((Date)date);
		} else {
			throw new ParseException("Wrong type", 0);
		}
	}

}
