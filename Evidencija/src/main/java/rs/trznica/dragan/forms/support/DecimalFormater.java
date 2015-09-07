package rs.trznica.dragan.forms.support;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;

public abstract class DecimalFormater {

	public static String formatFromLong(Long value, int decimals) {
		if (decimals == 0) {
			return value.toString();
		}
		String str = value.toString();
		if (str.length() < decimals) {
			char[] zeros = new char[decimals - str.length()];
			Arrays.fill(zeros, '0');
			return new StringBuilder("0,").append(new String(zeros)).append(str).toString();
		} else if (str .length() == decimals) {
			return new StringBuilder("0,").append(str).toString();
		} else {
			return new StringBuilder(str).insert(str.length() - decimals, ',').toString();
		}
	}

	public static String formatFromLongSep(Long value, int decimals) {
		if (decimals == 0) {
			String str = value.toString();
			StringBuilder builder = new StringBuilder(str);
			for (int i = 1; i * 3 < str.length(); i++) {
				builder.insert(str.length() - (i * 3), '.');
			}
			return builder.toString();
		}
		String str = value.toString();
		if (str.length() < decimals) {
			char[] zeros = new char[decimals - str.length()];
			Arrays.fill(zeros, '0');
			return new StringBuilder("0,").append(new String(zeros)).append(str).toString();
		} else if (str .length() == decimals) {
			return new StringBuilder("0,").append(str).toString();
		} else {
			StringBuilder builder = new StringBuilder(str);
			builder.insert(str.length() - decimals, ',');
			for (int i = 1; i * 3 < str.length() - decimals; i++) {
				builder.insert(str.length() - decimals - (i * 3), '.');
			}
			return builder.toString();
		}
	}

	public static String formatFromDouble(Double value, int decimals) {
		if (decimals == 0) {
			return String.valueOf(value.longValue());
		}
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator(',');
		char[] zeros = new char[decimals];
		Arrays.fill(zeros, '0');
		DecimalFormat df = new DecimalFormat("##0." + new String(zeros), dfs);
		return df.format(value);
	}

	public static Long parseToLong(String value, int decimal) {
		return Double.valueOf(parseToDouble(value) * Math.pow(10d, decimal)).longValue();
	}

	public static Double parseToDouble(String value) {
		return Double.valueOf(value.replaceAll(",", "."));
	}
}
