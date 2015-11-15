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
		
		int sheetNum = 0;
		if (consumersNum < 9) {
			sheetNum = months / 6;
			if (months % 6 > 0) {
				sheetNum++;
			}
		} else {
			sheetNum = months / 3;
			if (months % 3 > 0) {
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
		nameStyle.setBorderBottom(BorderStyle.THIN);
		nameStyle.setBorderTop(BorderStyle.THIN);
		nameStyle.setBorderLeft(BorderStyle.THIN);
		nameStyle.setBorderRight(BorderStyle.THIN);
		styles.put(CellStyleEnum.NAME, nameStyle);

		XSSFCellStyle kmStyle = wb.createCellStyle();
		kmStyle.setFont(normalFont);
		kmStyle.setAlignment(HorizontalAlignment.RIGHT);
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
		for (int i = 1; i < 13; i++) {
			if (i % 4 == 0) {
				sheet.setColumnWidth(i, 2600);
			} else {
				sheet.setColumnWidth(i, 2236);
			}
		}
		sheet.setColumnWidth(13, 2459);
		
		if (consumersNum < 9) {
			drawTitle(sheet, 0);
			for (int i = 0; i < consumersNum + 1; i++) {
				drawEntryRow(sheet, i + 3);
			}
			drawTitle(sheet, consumersNum + 4);
			for (int i = 0; i < consumersNum + 1; i++) {
				drawEntryRow(sheet, consumersNum + i + 7);
			}
		} else {
			drawTitle(sheet, 0);
			for (int i = 0; i < consumersNum + 1; i++) {
				drawEntryRow(sheet, i + 3);
			}
		}
		
		return sheet;
	}
	
	private void drawTitle(XSSFSheet sheet, int rowNum) {
		sheet.createRow(rowNum);
		sheet.getRow(rowNum).setHeight(rowHeight);
		sheet.createRow(rowNum + 1);
		sheet.getRow(rowNum + 1).setHeight(rowHeight);
		sheet.createRow(rowNum + 2);
		sheet.getRow(rowNum + 2).setHeight(rowHeight);
		
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 1, 4)); // 0
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 5, 8)); // 1
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 9, 12)); // 2
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 2, 0, 0)); // 3
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 2, 1, 1)); // 4
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, 2, 3)); // 5
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 2, 4, 4)); // 6
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 2, 5, 5)); // 7
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, 6, 7)); // 8
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 2, 8, 8)); // 9
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 2, 9, 9)); // 10
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, 10, 11)); // 11
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 2, 12, 12)); // 12
		sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 2, 13, 13)); // 13
		
		XSSFRow row = sheet.getRow(rowNum);
		row.createCell(1).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.createCell(5).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.createCell(9).setCellStyle(styles.get(CellStyleEnum.TITLE));
		
		row = sheet.getRow(rowNum + 1);
		row.createCell(0);
		row.getCell(0).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(0).setCellValue("Reg. Oznaka");
		row.createCell(1);
		row.getCell(1).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(1).setCellValue("km");
		row.createCell(2);
		row.getCell(2).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(2).setCellValue("Litara");
		row.createCell(4);
		row.getCell(4).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(4).setCellValue("Ukupno bez PDV");
		row.createCell(5);
		row.getCell(5).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(5).setCellValue("km");
		row.createCell(6);
		row.getCell(6).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(6).setCellValue("Litara");
		row.createCell(8);
		row.getCell(8).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(8).setCellValue("Ukupno bez PDV");
		row.createCell(9);
		row.getCell(9).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(9).setCellValue("km");
		row.createCell(10);
		row.getCell(10).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(10).setCellValue("Litara");
		row.createCell(12);
		row.getCell(12).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(12).setCellValue("Ukupno bez PDV");
		row.createCell(13);
		row.getCell(13).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(13).setCellValue("Reg. Oznaka");
		
		row = sheet.getRow(rowNum + 2);
		row.createCell(0).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.createCell(1).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.createCell(2);
		row.getCell(2).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(2).setCellValue("BMB");
		row.createCell(3);
		row.getCell(3).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(3).setCellValue("ED");
		row.createCell(4).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.createCell(5).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.createCell(6);
		row.getCell(6).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(6).setCellValue("BMB");
		row.createCell(7);
		row.getCell(7).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(7).setCellValue("ED");
		row.createCell(8).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.createCell(9).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.createCell(10);
		row.getCell(10).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(10).setCellValue("BMB");
		row.createCell(11);
		row.getCell(11).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(11).setCellValue("ED");
		row.createCell(12).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.createCell(13).setCellStyle(styles.get(CellStyleEnum.TITLE));
	}
	
	private void drawEntryRow(XSSFSheet sheet, int rowNum) {
		sheet.createRow(rowNum);
		sheet.getRow(rowNum).setHeight(rowHeight);
		XSSFRow row = sheet.getRow(rowNum);
		row.createCell(0).setCellStyle(styles.get(CellStyleEnum.NAME));
		for (int i = 1; i < 13; i++) {
			row.createCell(i).setCellStyle(styles.get(CellStyleEnum.NORMAL));
		}
		row.createCell(1).setCellStyle(styles.get(CellStyleEnum.KM));
		row.createCell(5).setCellStyle(styles.get(CellStyleEnum.KM));
		row.createCell(9).setCellStyle(styles.get(CellStyleEnum.KM));
		row.createCell(13).setCellStyle(styles.get(CellStyleEnum.NAME));
	}
	
	private void addSummarySheet() {
		XSSFSheet sheet = wb.createSheet();
		sheet.setColumnWidth(0, 3000);
		sheet.setColumnWidth(1, 2459);
		sheet.setColumnWidth(2, 2459);
		sheet.setColumnWidth(3, 2459);
		sheet.setColumnWidth(4, 3200);
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
		
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
		row.getCell(1).setCellValue("km");
		row.createCell(2);
		row.getCell(2).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(2).setCellValue("Litara BMB");
		row.createCell(3);
		row.getCell(3).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(3).setCellValue("Litara ED");
		row.createCell(4);
		row.getCell(4).setCellStyle(styles.get(CellStyleEnum.TITLE));
		row.getCell(4).setCellValue("Ukupno bez PDV");
		
		for (int i = 0; i < consumersNum; i++) {
			row = sheet.createRow(i + 2);
			row.createCell(0).setCellStyle(styles.get(CellStyleEnum.NAME));
			row.createCell(1).setCellStyle(styles.get(CellStyleEnum.KM));
			for (int j = 2; j < 5; j++) {
				row.createCell(j).setCellStyle(styles.get(CellStyleEnum.NORMAL));
			}
		}
		
		row = sheet.createRow(consumersNum + 2);
		row = sheet.createRow(consumersNum + 3);
		row.createCell(0).setCellStyle(styles.get(CellStyleEnum.NAME));
		row.getCell(0).setCellValue("Ukupno:");
		row.createCell(1).setCellStyle(styles.get(CellStyleEnum.KM));
		for (int j = 2; j < 5; j++) {
			row.createCell(j).setCellStyle(styles.get(CellStyleEnum.NORMAL)); // row number = consumersNum + 3
		}
	}
	
	private enum CellStyleEnum {
		TITLE,
		NAME,
		KM,
		NORMAL
	}
}
