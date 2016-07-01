package rs.trznica.dragan.dto.tankovanje;

public abstract class BaseDto<T> {

	public abstract T createEntityFromData();
	
	protected String trim(String value) {
		return (value == null) ? null : value.trim();
	}
}
