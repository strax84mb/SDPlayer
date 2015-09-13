package rs.trznica.dragan.poi;

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

	public static XSSFWorkbook createWorkbook() {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet();
		sheet.getPrintSetup().setLandscape(true);
		sheet.getPrintSetup().setPaperSize(PaperSize.A4_PAPER);
		
		XSSFFont normalFont = wb.getFontAt((short)0);
		normalFont.setFontName("Calibri");
		normalFont.setBold(false);
		normalFont.setFontHeightInPoints((short)11);
		
		XSSFDataFormat volumeFormat = wb.createDataFormat();
		
		XSSFCellStyle normalStyle = wb.createCellStyle();
		normalStyle.setFont(normalFont);
		normalStyle.setDataFormat(volumeFormat.getFormat("#,##0.00"));
		normalStyle.setAlignment(HorizontalAlignment.RIGHT);
		
		XSSFCellStyle titleStyle = wb.createCellStyle();
		titleStyle.setFont(normalFont);
		titleStyle.setAlignment(HorizontalAlignment.CENTER);
		titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		titleStyle.setWrapText(true);
		titleStyle.setBorderBottom(BorderStyle.THIN);
		titleStyle.setBorderTop(BorderStyle.THIN);
		titleStyle.setBorderLeft(BorderStyle.THIN);
		titleStyle.setBorderRight(BorderStyle.THIN);

		XSSFCellStyle nameStyle = wb.createCellStyle();
		nameStyle.setFont(normalFont);
		nameStyle.setAlignment(HorizontalAlignment.LEFT);

		sheet.setDefaultRowHeight((short)365);
		
		sheet.setColumnWidth(0, 2459);
		sheet.setDefaultColumnStyle(0, normalStyle);
		for (int i = 1; i < 13; i++) {
			if (i % 4 == 0) {
				sheet.setColumnWidth(i, 2600);
			} else {
				sheet.setColumnWidth(i, 2236);
			}
			sheet.setDefaultColumnStyle(i, normalStyle);
		}
		sheet.setColumnWidth(13, 2459);
		sheet.setDefaultColumnStyle(13, normalStyle);
		
		sheet.createRow(0);
		sheet.getRow(0).setHeight((short)365);
		sheet.createRow(1);
		sheet.getRow(1).setHeight((short)365);
		sheet.createRow(2);
		sheet.getRow(2).setHeight((short)365);
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 4)); // 0
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 5, 8)); // 1
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 9, 12)); // 2
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, 0)); // 3
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 1)); // 4
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 3)); // 5
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 4, 4)); // 6
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 5, 5)); // 7
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 6, 7)); // 8
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 8, 8)); // 9
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 9, 9)); // 10
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 10, 11)); // 11
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 12, 12)); // 12
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 13, 13)); // 13
		
		XSSFRow row = sheet.getRow(1);
		row.createCell(0);
		row.getCell(0).setCellStyle(titleStyle);
		row.getCell(0).setCellValue("Reg. Oznaka");
		row.createCell(1);
		row.getCell(1).setCellStyle(titleStyle);
		row.getCell(1).setCellValue("km");
		row.createCell(2);
		row.getCell(2).setCellStyle(titleStyle);
		row.getCell(2).setCellValue("Litara");
		row.createCell(4);
		row.getCell(4).setCellStyle(titleStyle);
		row.getCell(4).setCellValue("Ukupno bez PDV");
		row.createCell(5);
		row.getCell(5).setCellStyle(titleStyle);
		row.getCell(5).setCellValue("km");
		row.createCell(6);
		row.getCell(6).setCellStyle(titleStyle);
		row.getCell(6).setCellValue("Litara");
		row.createCell(8);
		row.getCell(8).setCellStyle(titleStyle);
		row.getCell(8).setCellValue("Ukupno bez PDV");
		row.createCell(9);
		row.getCell(9).setCellStyle(titleStyle);
		row.getCell(9).setCellValue("km");
		row.createCell(10);
		row.getCell(10).setCellStyle(titleStyle);
		row.getCell(10).setCellValue("Litara");
		row.createCell(12);
		row.getCell(12).setCellStyle(titleStyle);
		row.getCell(12).setCellValue("Ukupno bez PDV");
		row.createCell(13);
		row.getCell(13).setCellStyle(titleStyle);
		row.getCell(13).setCellValue("Reg. Oznaka");
		
		row = sheet.getRow(2);
		row.createCell(0).setCellStyle(titleStyle);
		row.createCell(1).setCellStyle(titleStyle);
		row.createCell(2);
		row.getCell(2).setCellStyle(titleStyle);
		row.getCell(2).setCellValue("BMB");
		row.createCell(3);
		row.getCell(3).setCellStyle(titleStyle);
		row.getCell(3).setCellValue("ED");
		row.createCell(4).setCellStyle(titleStyle);
		row.createCell(5).setCellStyle(titleStyle);
		row.createCell(6);
		row.getCell(6).setCellStyle(titleStyle);
		row.getCell(6).setCellValue("BMB");
		row.createCell(7);
		row.getCell(7).setCellStyle(titleStyle);
		row.getCell(7).setCellValue("ED");
		row.createCell(8).setCellStyle(titleStyle);
		row.createCell(9).setCellStyle(titleStyle);
		row.createCell(10);
		row.getCell(10).setCellStyle(titleStyle);
		row.getCell(10).setCellValue("BMB");
		row.createCell(11);
		row.getCell(11).setCellStyle(titleStyle);
		row.getCell(11).setCellValue("ED");
		row.createCell(12).setCellStyle(titleStyle);
		row.createCell(13).setCellStyle(titleStyle);
		
		
		
		
		
		
		
		return wb;
	}
}
