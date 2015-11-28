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
import org.apache.poi.xssf.usermodel.XSSFCell;
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

	private int maxConsumers = 8;
	private int monthColumns = 2;

	public SumarySheetGenerator(int months, int consumersNum) {
		this(months, consumersNum, 8, 2);
	}

	public SumarySheetGenerator(int months, int consumersNum, int maxConsumers, int monthColumns) {
		this.consumersNum = consumersNum;
		this.maxConsumers = maxConsumers;
		this.monthColumns = monthColumns;
		wb = new XSSFWorkbook();
		generateFont();
		generateStyles();
		rowHeight = (short)365;
		
		int sheetNum = 0;
		if (consumersNum <= maxConsumers) {
			sheetNum = months / (monthColumns * 2);
			if (months % (monthColumns * 2) > 0) {
				sheetNum++;
			}
		} else {
			sheetNum = months / monthColumns;
			if (months % monthColumns > 0) {
				sheetNum++;
			}
		}
		for (int i = 0; i < sheetNum; i++) {
			createSheet();
		}
		addSummarySheet();
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
		for (int i = 0; i < monthColumns; i++) {
			sheet.setColumnWidth(i * 5 + 1, 2236);
			sheet.setColumnWidth(i * 5 + 2, 2236);
			sheet.setColumnWidth(i * 5 + 3, 2600);
			sheet.setColumnWidth(i * 5 + 4, 2600);
			sheet.setColumnWidth(i * 5 + 5, 2600);
		}
		sheet.setColumnWidth(monthColumns * 5 + 1, 2459);
		
		int rowIndex = 0;
		if (consumersNum <= maxConsumers) {
			rowIndex = drawTitle(sheet, rowIndex);
			for (int i = 0; i < consumersNum; i++) {
				rowIndex = drawEntryRow(sheet, rowIndex);
			}
			rowIndex = drawTitle(sheet, rowIndex);
			for (int i = 0; i < consumersNum; i++) {
				rowIndex = drawEntryRow(sheet, rowIndex);
			}
			rowIndex = drawFooterRows(sheet, rowIndex);
		} else {
			rowIndex = drawTitle(sheet, rowIndex);
			for (int i = 0; i < consumersNum; i++) {
				rowIndex = drawEntryRow(sheet, rowIndex);
			}
			rowIndex = drawFooterRows(sheet, rowIndex);
		}
		
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
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 2, monthColumns * 5 + 1, monthColumns * 5 + 1));
		
		for (int i = 0; i < monthColumns; i++) {
			// Add month name field
			sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, i * 5 + 1, (i + 1) * 5));
			// Add km label
			sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, i * 5 + 1, i * 5 + 2));
			// Add liter label
			sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, i * 5 + 3, i * 5 + 4));
			// Add price label
			sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 2, i * 5 + 5, i * 5 + 5));
		}
		
		// Create month name cells
		XSSFRow row = sheet.getRow(rowNum);
		for (int i = 0; i < monthColumns; i++) {
			for (int j = 0; j < 5; j++) {
				row.createCell(i * 5 + 1 + j).setCellStyle(styles.get(CellStyleEnum.TITLE));
			}
		}
		
		row = sheet.getRow(rowNum + 1);
		row.createCell(0).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(0).setCellValue("Reg. Oznaka");
		// Create first row titles
		for (int i = 0; i < monthColumns; i++) {
			for (int j = 0; j < 5; j++) {
				row.createCell(i * 5 + 1 + j).setCellStyle(styles.get(CellStyleEnum.TITLE));
			}
			row.getCell(i * 5 + 1).setCellValue("km");
			row.getCell(i * 5 + 3).setCellValue("Litara");
			row.getCell(i * 5 + 5).setCellValue("Ukupno bez PDV");
		}
		row.createCell(monthColumns * 5 + 1).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(monthColumns * 5 + 1).setCellValue("Reg. Oznaka");
		// Create second row titles
		row = sheet.getRow(rowNum + 2);
		row.createCell(0).setCellStyle(styles.get(CellStyleEnum.TITLE));
		for (int i = 0; i < monthColumns; i++) {
			for (int j = 0; j < 5; j++) {
				row.createCell(i * 5 + 1 + j).setCellStyle(styles.get(CellStyleEnum.TITLE));
			}
			row.getCell(i * 5).setCellValue("BMB");
			row.getCell(i * 5 + 1).setCellValue("ED");
			row.getCell(i * 5 + 2).setCellValue("BMB");
			row.getCell(i * 5 + 3).setCellValue("ED");
		}
		row.createCell(monthColumns * 5 + 1).setCellStyle(styles.get(CellStyleEnum.TITLE));
		
		return rowNum + 3;
	}
	
	public int getTitleSpan() {
		return 3;
	}
	
	private int  drawEntryRow(XSSFSheet sheet, int rowNum) {
		sheet.createRow(rowNum);
		sheet.getRow(rowNum).setHeight(rowHeight);
		XSSFRow row = sheet.getRow(rowNum);
		XSSFCell cell;
		row.createCell(0).setCellStyle(styles.get(CellStyleEnum.NAME));
		for (int i = 0; i < monthColumns; i++) {
			for (int j = 0; j < 5; j++) {
				cell = row.createCell(i * 5 + 1 + j);
				if (j < 2) {
					cell.setCellStyle(styles.get(CellStyleEnum.KM));
				} else {
					cell.setCellStyle(styles.get(CellStyleEnum.NORMAL));
				}
			}
		}
		row.createCell(monthColumns * 5 + 1).setCellStyle(styles.get(CellStyleEnum.NAME));
		return rowNum + 1;
	}
	
	private int drawFooterRows(XSSFSheet sheet, int rowNum) {
		sheet.createRow(rowNum);
		sheet.getRow(rowNum).setHeight(rowHeight);
		sheet.createRow(rowNum + 1);
		sheet.getRow(rowNum + 1).setHeight(rowHeight);

		// Add regions for summary
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 1, 0, 0));
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 1, monthColumns * 5 + 1, monthColumns * 5 + 1));
		for (int i = 0; i < monthColumns; i++) {
			// km summary
			sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, i * 5 + 1, i * 5 + 2));
			// Liter summary
			sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, i * 5 + 3, i * 5 + 4));
			// Money summary
			sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 1, i * 5 + 5, i * 5 + 5));
		}
		
		XSSFRow row = sheet.getRow(rowNum);
		row.createCell(0).setCellStyle(styles.get(CellStyleEnum.NAME));
		row.getCell(0).setCellValue("Ukupno:");
		for (int i = 0; i < monthColumns; i++) {
			row.createCell(i * 5 + 1).setCellStyle(styles.get(CellStyleEnum.KM));
			row.createCell(i * 5 + 2).setCellStyle(styles.get(CellStyleEnum.KM));
			row.createCell(i * 5 + 3).setCellStyle(styles.get(CellStyleEnum.NORMAL));
			row.createCell(i * 5 + 4).setCellStyle(styles.get(CellStyleEnum.NORMAL));
			row.createCell(i * 5 + 5).setCellStyle(styles.get(CellStyleEnum.NORMAL));
		}
		row.createCell(monthColumns * 5 + 1).setCellStyle(styles.get(CellStyleEnum.NAME));
		row.getCell(monthColumns * 5 + 1).setCellValue("Ukupno:");
		
		row = sheet.getRow(rowNum + 1);
		row.createCell(0).setCellStyle(styles.get(CellStyleEnum.NAME));
		for (int i = 0; i < monthColumns; i++) {
			row.createCell(i * 5 + 1).setCellStyle(styles.get(CellStyleEnum.KM));
			row.createCell(i * 5 + 2).setCellStyle(styles.get(CellStyleEnum.KM));
			row.createCell(i * 5 + 3).setCellStyle(styles.get(CellStyleEnum.NORMAL));
			row.createCell(i * 5 + 4).setCellStyle(styles.get(CellStyleEnum.NORMAL));
			row.createCell(i * 5 + 5).setCellStyle(styles.get(CellStyleEnum.NORMAL));
		}
		row.createCell(monthColumns * 5 + 1).setCellStyle(styles.get(CellStyleEnum.NAME));
		
		return rowNum + 2;
	}
	
	public int getFooterSpan() {
		return 2;
	}
	
	private void addSummarySheet() {
		XSSFSheet sheet = wb.createSheet();
		sheet.setColumnWidth(0, 3000);
		sheet.setColumnWidth(1, 2459);
		sheet.setColumnWidth(2, 2459);
		sheet.setColumnWidth(3, 3200);
		sheet.setColumnWidth(4, 3200);
		sheet.setColumnWidth(5, 3200);
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
		sheet.addMergedRegion(new CellRangeAddress(consumersNum + 3, consumersNum + 4, 0, 0));
		sheet.addMergedRegion(new CellRangeAddress(consumersNum + 3, consumersNum + 4, 5, 5));
		sheet.addMergedRegion(new CellRangeAddress(consumersNum + 4, consumersNum + 4, 1, 2));
		sheet.addMergedRegion(new CellRangeAddress(consumersNum + 4, consumersNum + 4, 3, 4));
		
		XSSFRow row = sheet.createRow(0);
		row.createCell(0).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(0).setCellValue("Zbir");
		
		row = sheet.createRow(1);
		row.setHeight((short)(rowHeight * 2));
		row.createCell(0);
		row.getCell(0).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(0).setCellValue("Reg. Oznaka");
		row.createCell(1);
		row.getCell(1).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(1).setCellValue("km BMB");
		row.createCell(2);
		row.getCell(2).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(2).setCellValue("km ED");
		row.createCell(3);
		row.getCell(3).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(3).setCellValue("Litara BMB");
		row.createCell(4);
		row.getCell(4).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(4).setCellValue("Litara ED");
		row.createCell(5);
		row.getCell(5).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(5).setCellValue("Ukupno bez PDV");
		
		for (int i = 0; i < consumersNum; i++) {
			row = sheet.createRow(i + 2);
			row.createCell(0).setCellStyle(styles.get(CellStyleEnum.NAME));
			row.createCell(1).setCellStyle(styles.get(CellStyleEnum.KM));
			for (int j = 2; j <= 5; j++) {
				row.createCell(j).setCellStyle(styles.get(CellStyleEnum.NORMAL));
			}
		}
		
		row = sheet.createRow(consumersNum + 2);
		row = sheet.createRow(consumersNum + 3);
		row.createCell(0).setCellStyle(styles.get(CellStyleEnum.NAME));
		row.getCell(0).setCellValue("Ukupno:");
		row.createCell(1).setCellStyle(styles.get(CellStyleEnum.KM));
		row.createCell(2).setCellStyle(styles.get(CellStyleEnum.KM));
		for (int j = 3; j <= 5; j++) {
			row.createCell(j).setCellStyle(styles.get(CellStyleEnum.NORMAL)); // row number = consumersNum + 3
		}
		row = sheet.createRow(consumersNum + 4);
		row.createCell(0).setCellStyle(styles.get(CellStyleEnum.NAME));
		row.createCell(1).setCellStyle(styles.get(CellStyleEnum.KM));
		row.createCell(2).setCellStyle(styles.get(CellStyleEnum.KM));
		for (int j = 3; j <= 5; j++) {
			row.createCell(j).setCellStyle(styles.get(CellStyleEnum.NORMAL)); // row number = consumersNum + 4
		}
	}
	
	private enum CellStyleEnum {
		TITLE,
		NAME,
		KM,
		NORMAL
	}
}
