package rs.trznica.dragan.forms.support;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

public class DecimalTest {

	@Test
	public void testDecimalFormating() throws ParseException {
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator(',');
		DecimalFormat df = new DecimalFormat("##0.000", dfs);
		System.out.println(df.format(2432343423.23423234));
		System.out.println(df.format(2432343423.23));
		System.out.println(df.format(0.23));
		System.out.println(df.format(2356));
		Assert.assertEquals(1234567890L, DecimalFormater.parseToLong("1.234.567,89", 3).longValue());
	}
}
