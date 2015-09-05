package rs.trznica.dragan.poi;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

public class ReportGenerationTest {

	@Test
	public void testReportGeneration() throws FileNotFoundException, IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		sheet.createRow(0);
		sheet.getRow(0).createCell(0);
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
		sheet.createRow(1);
		sheet.getRow(1).setHeightInPoints(18);
		
		Font font = workbook.createFont();
		font.setFontHeightInPoints(Integer.valueOf(16).shortValue());
		font.setFontName("Times New Roman");
		font.setColor(Font.COLOR_RED);
		
		XSSFCell cell = sheet.getRow(1).createCell(1, Cell.CELL_TYPE_NUMERIC);
		cell.getCellStyle().setFont(font);
		cell.getCellStyle().setBottomBorderColor(new XSSFColor(Color.RED));
		cell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
		cell.getCellStyle().setBorderBottom(BorderStyle.MEDIUM_DASHED);
		cell.getCellStyle().setBorderLeft(BorderStyle.THIN);
		cell.getCellStyle().setBorderTop(BorderStyle.MEDIUM);
		cell.getCellStyle().setBorderRight(BorderStyle.THICK);
		cell.setCellValue(12.594);
		
		XSSFCellStyle style = cell.getCellStyle();
		style.setFont(font);
		style.setBottomBorderColor(new XSSFColor(Color.RED));
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setBorderBottom(BorderStyle.MEDIUM_DASHED);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.THICK);
		cell.setCellStyle(style);
		
		sheet.setColumnWidth(1, 255*12);
		
		sheet.getRow(0).getCell(0).setCellValue("TEXT");
		
		workbook.write(new FileOutputStream("f:/prog/test.xlsx"));
		workbook.close();
	}
}
