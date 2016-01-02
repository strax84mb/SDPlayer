package rs.trznica.dragan.forms.support;

import org.junit.Assert;
import org.junit.Test;

public class KmLongToStringFormatTest {

	@Test
	public void test_1() {
		Assert.assertEquals("1", getString(1L));
	}

	@Test
	public void test_2() {
		Assert.assertEquals("12", getString(12L));
	}

	@Test
	public void test_3() {
		Assert.assertEquals("123", getString(123L));
	}

	@Test
	public void test_4() {
		Assert.assertEquals("1.234", getString(1234L));
	}
	
	@Test
	public void test_5() {
		Assert.assertEquals("12.345", getString(12345L));
	}
	
	@Test
	public void test_6() {
		Assert.assertEquals("123.456", getString(123456L));
	}
	
	@Test
	public void test_7() {
		Assert.assertEquals("1.234.567", getString(1234567L));
	}
	
	@Test
	public void test_8() {
		Assert.assertEquals("12.345.678", getString(12345678L));
	}
	
	@Test
	public void test_9() {
		Assert.assertEquals("123.456.789", getString(123456789L));
	}
	
	@Test
	public void test_10() {
		Assert.assertEquals("1.234.567.890", getString(1234567890L));
	}
	
	private String getString(long value) {
		StringBuilder builder = new StringBuilder(Long.valueOf(value).toString());
		int length = builder.length();
		int dots = length / 3;
		int offset = length % 3;
		if (offset == 0) {
			dots--;
			offset = 3;
		}
		if (dots > 0) {
			for (int i = 0; i < dots; i++) {
				builder.insert(offset + i + (i * 3), '.');
			}
		}
		return builder.toString();
	}
}
