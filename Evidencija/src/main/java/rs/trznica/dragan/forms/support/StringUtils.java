package rs.trznica.dragan.forms.support;

public abstract class StringUtils {
	
	public static boolean isEmpty(String value) {
		if (value == null) {
			return true;
		}
		return "".equals(value.trim());
	}

	public static boolean isNotEmpty(String value) {
		return !isEmpty(value);
	}
}
