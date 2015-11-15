package rs.trznica.dragan.poi;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

public class SumaryReportTest {

	@Test
	public void testGeneration() throws IOException {
		SumarySheetGenerator generator = new SumarySheetGenerator(12, 12);
		XSSFWorkbook wb = generator.getWorkbook();
		try (FileOutputStream fos = new FileOutputStream("f:/Prog/summary.xlsx")) {
			wb.write(fos);
		}
	}
}
