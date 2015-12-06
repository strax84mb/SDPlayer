package rs.trznica.dragan.poi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.PaperSize;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SumarySheetGenerator {

	private Map<CellStyleEnum, XSSFCellStyle> styles = new HashMap<CellStyleEnum, XSSFCellStyle>();
	private List<XSSFSheet> sheets = new ArrayList<XSSFSheet>();
	private short rowHeight;
	private int consumersNum;
	private XSSFFont normalFont;
	private XSSFWorkbook wb;

	public SumarySheetGenerator(int months, int consumersNum) {
		this.consumersNum = consumersNum;
		wb = new XSSFWorkbook();
		generateFont();
		generateStyles();
		rowHeight = (short)365;
		
		for (int i = 0; i < months; i++) {
			createSheet();
		}
	}
	
	public XSSFWorkbook getWorkbook() {
		return wb;
	}
	
	private void generateFont() {
		normalFont = wb.getFontAt((short)0);
		normalFont.setFontName("Calibri");
		normalFont.setBold(false);
		normalFont.setFontHeightInPoints((short)11);
	}
	
	private void generateStyles() {
		XSSFDataFormat volumeFormat = wb.createDataFormat();
		wb.createCellStyle();
		
		XSSFCellStyle normalStyle = wb.createCellStyle();
		normalStyle.setFont(normalFont);
		normalStyle.setDataFormat(volumeFormat.getFormat("#,##0.00"));
		normalStyle.setAlignment(HorizontalAlignment.RIGHT);
		normalStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		normalStyle.setBorderBottom(BorderStyle.THIN);
		normalStyle.setBorderTop(BorderStyle.THIN);
		normalStyle.setBorderLeft(BorderStyle.THIN);
		normalStyle.setBorderRight(BorderStyle.THIN);
		styles.put(CellStyleEnum.NORMAL, normalStyle);
		
		XSSFCellStyle titleStyle = wb.createCellStyle();
		titleStyle.setFont(normalFont);
		titleStyle.setAlignment(HorizontalAlignment.CENTER);
		titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		titleStyle.setWrapText(true);
		titleStyle.setBorderBottom(BorderStyle.THIN);
		titleStyle.setBorderTop(BorderStyle.THIN);
		titleStyle.setBorderLeft(BorderStyle.THIN);
		titleStyle.setBorderRight(BorderStyle.THIN);
		styles.put(CellStyleEnum.TITLE, titleStyle);

		XSSFCellStyle nameStyle = wb.createCellStyle();
		nameStyle.setFont(normalFont);
		nameStyle.setAlignment(HorizontalAlignment.LEFT);
		nameStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		nameStyle.setBorderBottom(BorderStyle.THIN);
		nameStyle.setBorderTop(BorderStyle.THIN);
		nameStyle.setBorderLeft(BorderStyle.THIN);
		nameStyle.setBorderRight(BorderStyle.THIN);
		styles.put(CellStyleEnum.NAME, nameStyle);

		XSSFCellStyle kmStyle = wb.createCellStyle();
		kmStyle.setFont(normalFont);
		kmStyle.setAlignment(HorizontalAlignment.RIGHT);
		kmStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		kmStyle.setDataFormat(wb.createDataFormat().getFormat("#,##0"));
		kmStyle.setBorderBottom(BorderStyle.THIN);
		kmStyle.setBorderTop(BorderStyle.THIN);
		kmStyle.setBorderLeft(BorderStyle.THIN);
		kmStyle.setBorderRight(BorderStyle.THIN);
		styles.put(CellStyleEnum.KM, kmStyle);
	}
	
	private XSSFSheet createSheet() {
		XSSFSheet sheet = wb.createSheet();
		sheets.add(sheet);
		sheet.getPrintSetup().setLandscape(true);
		sheet.getPrintSetup().setPaperSize(PaperSize.A4_PAPER);
		sheet.setDefaultRowHeight((short)365);
		
		sheet.setColumnWidth(0, 2459);
		for (int i = 0; i < 6; i++) {
			sheet.setColumnWidth(i + 1, (i < 2) ? 1788 : 2080);
		}
		for (int i = 0; i < 6; i++) {
			sheet.setColumnWidth(6 + i + 1, (i < 2) ? 2210 : 2880);
		}
		
		int rowIndex = 0;
		rowIndex = drawTitle(sheet, rowIndex);
		for (int i = 0; i < consumersNum; i++) {
			rowIndex = drawEntryRow(sheet, rowIndex);
		}
		rowIndex = drawFooterRows(sheet, rowIndex);
		
		return sheet;
	}
	
	private int drawTitle(XSSFSheet sheet, int rowNum) {
		sheet.createRow(rowNum);
		sheet.getRow(rowNum).setHeight(rowHeight);
		sheet.createRow(rowNum + 1);
		sheet.getRow(rowNum + 1).setHeight(rowHeight);
		sheet.createRow(rowNum + 2);
		sheet.getRow(rowNum + 2).setHeight(rowHeight);

		//Add registration fields
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 2, 0, 0));
		// Add month name field
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 1, 6));
		// Add km label
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, 1, 2));
		// Add liter label
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, 3, 4));
		// Add price label
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, 5, 6));
		int sumStartColumnIndex = 7;
		// Monthly total summary
		// Add summary sign field
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, sumStartColumnIndex, sumStartColumnIndex + 5));
		// Add km label
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, sumStartColumnIndex, sumStartColumnIndex + 1));
		// Add liter label
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, sumStartColumnIndex + 2, sumStartColumnIndex + 3));
		// Add price label
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, sumStartColumnIndex + 4, sumStartColumnIndex + 5));
		
		// Create month name cells
		XSSFRow row = sheet.getRow(rowNum);
		for (int j = 0; j < 6; j++) {
			row.createCell(j + 1).setCellStyle(styles.get(CellStyleEnum.TITLE));
		}
		// Create summary sign cell
		for (int j = 0; j < 6; j++) {
			row.createCell(sumStartColumnIndex + j).setCellStyle(styles.get(CellStyleEnum.TITLE));
		}
		
		row = sheet.getRow(rowNum + 1);
		row.createCell(0).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(0).setCellValue("Reg. Oznaka");
		// Create first row titles
		for (int j = 1; j < 7; j++) {
			row.createCell(j).setCellStyle(styles.get(CellStyleEnum.TITLE));
		}
		row.getCell(1).setCellValue("km");
		row.getCell(3).setCellValue("Litara");
		row.getCell(5).setCellValue("Ukupno bez PDV");
		// Create first row summary titles
		for (int j = sumStartColumnIndex; j < sumStartColumnIndex + 6; j++) {
			row.createCell(j).setCellStyle(styles.get(CellStyleEnum.TITLE));
		}
		row.getCell(sumStartColumnIndex).setCellValue("km");
		row.getCell(sumStartColumnIndex + 2).setCellValue("Litara");
		row.getCell(sumStartColumnIndex + 4).setCellValue("Ukupno bez PDV");
		
		// Create second row titles
		row = sheet.getRow(rowNum + 2);
		row.createCell(0).setCellStyle(styles.get(CellStyleEnum.TITLE));
		for (int j = 1; j < 7; j++) {
			row.createCell(j).setCellStyle(styles.get(CellStyleEnum.TITLE));
			row.getCell(j).setCellValue((j % 2 == 1) ? "BMB" : "ED");
		}
		// Create second row titles
		for (int j = 0; j < 6; j++) {
			row.createCell(j + sumStartColumnIndex).setCellStyle(styles.get(CellStyleEnum.TITLE));
			row.getCell(j + sumStartColumnIndex).setCellValue((j % 2 == 0) ? "BMB" : "ED");
		}
		
		return rowNum + 3;
	}
	
	public int getTitleSpan() {
		return 3;
	}
	
	private int  drawEntryRow(XSSFSheet sheet, int rowNum) {
		sheet.createRow(rowNum);
		sheet.getRow(rowNum).setHeight(rowHeight);
		XSSFRow row = sheet.getRow(rowNum);
		row.createCell(0).setCellStyle(styles.get(CellStyleEnum.NAME));
		for (int j = 1; j < 7; j++) {
			row.createCell(j).setCellStyle(styles.get((j < 3) ? CellStyleEnum.KM : CellStyleEnum.NORMAL));
		}
		// Create summary entries
		int sumStartColumnIndex = 7;
		for (int i = 0; i < 6; i++) {
			row.createCell(i + sumStartColumnIndex).setCellStyle(styles.get((i < 2) ? CellStyleEnum.KM : CellStyleEnum.NORMAL));
		}
		return rowNum + 1;
	}
	
	private int drawFooterRows(XSSFSheet sheet, int rowNum) {
		sheet.createRow(rowNum);
		sheet.getRow(rowNum).setHeight(rowHeight);
		sheet.createRow(rowNum + 1);
		sheet.getRow(rowNum + 1).setHeight(rowHeight);

		// Add regions for summary
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 1, 0, 0));
		// km summary
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, 1, 2));
		// Liter summary
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, 3, 4));
		// Money summary
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, 5, 6));
		// Add regions for total monthly summary
		int sumStartColumnIndex = 7;
		// km summary
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, sumStartColumnIndex, sumStartColumnIndex + 1));
		// Liter summary
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, sumStartColumnIndex + 2, sumStartColumnIndex + 3));
		// Money summary
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, sumStartColumnIndex + 4, sumStartColumnIndex + 5));

		XSSFRow row = sheet.getRow(rowNum);
		row.createCell(0).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(0).setCellValue("Ukupno:");
		for (int i = 1; i < 7; i++) {
			row.createCell(i).setCellStyle(styles.get((i < 3) ? CellStyleEnum.KM : CellStyleEnum.NORMAL));
		}
		for (int i = 0; i < 6; i++) {
			row.createCell(sumStartColumnIndex + i).setCellStyle(styles.get(
					(i < 2) ? CellStyleEnum.KM : CellStyleEnum.NORMAL));
		}
		
		row = sheet.getRow(rowNum + 1);
		row.createCell(0).setCellStyle(styles.get(CellStyleEnum.NAME));
		for (int i = 1; i < 7; i++) {
			row.createCell(i).setCellStyle(styles.get((i < 3) ? CellStyleEnum.KM : CellStyleEnum.NORMAL));
		}
		for (int i = 0; i < 6; i++) {
			row.createCell(sumStartColumnIndex + i).setCellStyle(styles.get(
					(i < 2) ? CellStyleEnum.KM : CellStyleEnum.NORMAL));
		}
		
		return rowNum + 2;
	}
	
	public int getFooterSpan() {
		return 2;
	}
	
	private enum CellStyleEnum {
		TITLE,
		NAME,
		KM,
		NORMAL
	}
}
