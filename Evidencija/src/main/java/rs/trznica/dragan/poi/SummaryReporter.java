package rs.trznica.dragan.poi;

import java.util.List;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import rs.trznica.dragan.dao.TankovanjeDao;
import rs.trznica.dragan.entities.tankovanje.Potrosac;

@Component
public class SummaryReporter {

	private TankovanjeDao tankovanjeDao;

	@Value("${xls.blank.table.path}")
	private String xlsBlankTablePath;

	@Value("${xls.tables.path}")
	private String xlsTablesPath;

	@Autowired
	public SummaryReporter(ApplicationContext ctx) {
		tankovanjeDao = ctx.getBean(TankovanjeDao.class);
	}

	public void makeReport(List<Potrosac> potrosaci, String fromMonth, String tilMonth) {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet();
		
		sheet.createRow(0);
		sheet.createRow(1);
		for (int i = 0; i < 16; i++) {
			sheet.setColumnWidth(i, 9*256);
		}
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 5));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 6, 10));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 11, 15));
		
		
		
		
		
	}
}
