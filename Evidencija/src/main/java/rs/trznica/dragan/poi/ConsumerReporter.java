package rs.trznica.dragan.poi;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import rs.trznica.dragan.dao.TankovanjeDao;
import rs.trznica.dragan.entities.tankovanje.Potrosac;
import rs.trznica.dragan.entities.tankovanje.Tankovanje;
import rs.trznica.dragan.forms.support.DecimalFormater;

@Component
public class ConsumerReporter {

	private TankovanjeDao tankovanjeDao;

	@Value("${xls.blank.table.path}")
	private String xlsBlankTablePath;

	@Value("${xls.tables.path}")
	private String xlsTablesPath;

	@Autowired
	public ConsumerReporter(ApplicationContext ctx) {
		tankovanjeDao = ctx.getBean(TankovanjeDao.class);
	}

	public void makeReport(Potrosac potrosac, String fromMonth, String tilMonth) throws IOException, ParseException {
		XSSFWorkbook wb = null;
		try (FileInputStream fis = new FileInputStream(xlsBlankTablePath + "/blank.xlsx")) {
			wb = new XSSFWorkbook(fis);
		}
		// Create bold font and style
		Font boldFont = wb.createFont();
		boldFont.setBold(true);
		boldFont.setFontHeightInPoints(Integer.valueOf(11).shortValue());
		boldFont.setFontName("Calibri");
		boldFont.setColor(Font.COLOR_NORMAL);

		XSSFCellStyle boldStyle = wb.createCellStyle();
		boldStyle.setAlignment(HorizontalAlignment.RIGHT);
		boldStyle.setBorderBottom(BorderStyle.MEDIUM);
		boldStyle.setBorderTop(BorderStyle.MEDIUM);
		boldStyle.setBorderLeft(BorderStyle.MEDIUM);
		boldStyle.setBorderRight(BorderStyle.MEDIUM);
		boldStyle.setFont(boldFont);

		XSSFCellStyle boldStyleFirst = wb.createCellStyle();
		boldStyleFirst.setAlignment(HorizontalAlignment.LEFT);
		boldStyleFirst.setBorderBottom(BorderStyle.MEDIUM);
		boldStyleFirst.setBorderTop(BorderStyle.MEDIUM);
		boldStyleFirst.setBorderLeft(BorderStyle.MEDIUM);
		boldStyleFirst.setBorderRight(BorderStyle.MEDIUM);
		boldStyleFirst.setFont(boldFont);

		Font normalFont = wb.createFont();
		normalFont.setBold(false);
		normalFont.setFontHeightInPoints(Integer.valueOf(11).shortValue());
		normalFont.setFontName("Calibri");
		normalFont.setColor(Font.COLOR_NORMAL);

		XSSFCellStyle normalStyle = wb.createCellStyle();
		normalStyle.setAlignment(HorizontalAlignment.RIGHT);
		normalStyle.setBorderBottom(BorderStyle.THIN);
		normalStyle.setBorderTop(BorderStyle.THIN);
		normalStyle.setBorderLeft(BorderStyle.THIN);
		normalStyle.setBorderRight(BorderStyle.THIN);
		normalStyle.setFont(normalFont);

		XSSFCellStyle normalStyleFirst = wb.createCellStyle();
		normalStyleFirst.setAlignment(HorizontalAlignment.LEFT);
		normalStyleFirst.setBorderBottom(BorderStyle.THIN);
		normalStyleFirst.setBorderTop(BorderStyle.THIN);
		normalStyleFirst.setBorderLeft(BorderStyle.THIN);
		normalStyleFirst.setBorderRight(BorderStyle.THIN);
		normalStyleFirst.setFont(normalFont);

		XSSFSheet sheet = wb.getSheetAt(0);
		sheet.getRow(0).getCell(0).setCellValue(makeConsumerName(potrosac));
		if (potrosac.getVozilo()) {
			sheet.getRow(0).getCell(4).setCellValue(potrosac.getRegOznaka());
		}
		List<String> months = makeMonthList(fromMonth, tilMonth);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
		List<Tankovanje> tankovanja = StreamSupport.stream(
				tankovanjeDao.listInIntervalForConsumer(potrosac.getId(), fromMonth, tilMonth).spliterator(), false)
				.collect(Collectors.toList());
		int currRow = 6;
		long monthVolume = 0, monthSum = 0, yearVolume = 0, yearSum = 0, monthKM = 0, yearKM = 0, prevKM = 0;
		// Read prevKM
		if (potrosac.getVozilo()) {
			List<Tankovanje> lastFills = tankovanjeDao.getLastFill(potrosac.getId(), months.get(0), new PageRequest(0, 1));
			if (lastFills.size() > 0) {
				prevKM = lastFills.get(0).getKilometraza();
			} else {
				prevKM = 0L;
			}
		}
		for (String month : months) {
			Tankovanje[] fills = tankovanja.stream().filter(x -> month.equals(x.getMesec())).toArray(Tankovanje[]::new);
			monthKM = 0L;
			monthSum = 0L;
			monthVolume = 0L;
			if (fills.length > 0) {
				for (int i = 0; i < fills.length; i++) {
					Tankovanje fill = fills[i];
					sheet.getRow(currRow).getCell(0).setCellValue(sdf.format(fill.getDatum()));
					sheet.getRow(currRow).getCell(1).setCellValue(DecimalFormater.formatFromLongSep(fill.getKolicina(), 2));
					if (potrosac.getVozilo()) {
						sheet.getRow(currRow).getCell(2).setCellValue(DecimalFormater.formatFromLongSep(fill.getKilometraza(), 0));
						sheet.getRow(currRow).getCell(3).setCellValue(DecimalFormater.formatFromLongSep(
								fill.getKilometraza() - prevKM, 0));
					}
					sheet.getRow(currRow).getCell(7).setCellValue(DecimalFormater.formatFromLongSep(fill.getJedCena(), 2));
					sheet.getRow(currRow).getCell(8).setCellValue(DecimalFormater.formatFromLongSep(
							fill.getJedCena() * fill.getKolicina() / 100L, 2));
					setRowStyles(sheet.getRow(currRow), normalStyleFirst, normalStyle);
					currRow++;
					if (potrosac.getVozilo()) {
						// Add mileage
						monthKM += fill.getKilometraza() - prevKM;
						yearKM += fill.getKilometraza() - prevKM;
						prevKM = fill.getKilometraza();
					}
					// Add fuel spent
					monthVolume += fill.getKolicina();
					yearVolume += fill.getKolicina();
					// Add money spent
					monthSum += fill.getKolicina() * fill.getJedCena();
					yearSum += fill.getKolicina() * fill.getJedCena();
				}
				// Month summary
				sheet.getRow(currRow).getCell(0).setCellValue(getMonthString(month));
				sheet.getRow(currRow).getCell(1).setCellValue(DecimalFormater.formatFromLongSep(monthVolume, 2));
				if (potrosac.getVozilo()) {
					sheet.getRow(currRow).getCell(3).setCellValue(DecimalFormater.formatFromLongSep(monthKM, 0));
					sheet.getRow(currRow).getCell(9).setCellValue(DecimalFormater.formatFromLongSep(
							monthVolume / monthKM * 100L, 2));
				}
				sheet.getRow(currRow).getCell(8).setCellValue(DecimalFormater.formatFromLongSep(monthSum / 100L, 2));
				setRowStyles(sheet.getRow(currRow), boldStyleFirst, boldStyle);
				currRow++;
			}
		}
		// Yearly summary
		sheet.getRow(46).getCell(1).setCellValue(DecimalFormater.formatFromLongSep(yearVolume, 2));
		if (potrosac.getVozilo()) {
			sheet.getRow(46).getCell(3).setCellValue(DecimalFormater.formatFromLongSep(yearKM, 0));
		}
		sheet.getRow(46).getCell(8).setCellValue(DecimalFormater.formatFromLongSep(yearSum / 100L, 2));
		setRowStyles(sheet.getRow(46), boldStyleFirst, boldStyle);
		// Write XLSX file
		StringBuilder builder = new StringBuilder(xlsTablesPath);
		builder.append("/TANKOVANJE ");
		if (potrosac.getVozilo()) {
			builder.append(potrosac.getRegOznaka());
		} else {
			builder.append(potrosac.getTip());
		}
		builder.append(" ");
		builder.append(months.get(0).substring(0, 4));
		builder.append(".xlsx");
		try (FileOutputStream fos = new FileOutputStream(builder.toString())) {
			wb.write(fos);
		}
		wb.close();
	}

	private void setRowStyles(XSSFRow row, XSSFCellStyle styleFirst, XSSFCellStyle style) {
		XSSFCell cell = row.getCell(0);
		cell.setCellStyle(styleFirst);
		for (int i = 1; i <= 9; i++) {
			cell = row.getCell(i);
			cell.setCellStyle(style);
		}
	}

	private String getMonthString(String month) {
		switch (Integer.valueOf(month.substring(5))) {
		case 1:
			return "JANUAR";
		case 2:
			return "FEBRUAR";
		case 3:
			return "MART";
		case 4:
			return "APRIL";
		case 5:
			return "MAJ";
		case 6:
			return "JUN";
		case 7:
			return "JUL";
		case 8:
			return "AVGUST";
		case 9:
			return "SEPTEMBAR";
		case 10:
			return "OKTOBAR";
		case 11:
			return "NOVEMBAR";
		default:
			return "DECEMBAR";
		}
	}

	private String makeConsumerName(Potrosac potrosac) {
		StringBuilder builder = new StringBuilder();
		if (potrosac.getVozilo()) {
			builder.append(potrosac.getMarka()).append(" ");
		}
		builder.append(potrosac.getTip());
		return builder.toString();
	}

	private List<String> makeMonthList(String fromMonth, String tilMonth) throws ParseException {
		List<String> ret = new ArrayList<String>();
		ret.add(fromMonth);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		Date date = sdf.parse(fromMonth);
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		while (sdf.format(cal.getTime()).compareTo(tilMonth) < 0) {
			cal.add(Calendar.MONTH, 1);
			ret.add(sdf.format(cal.getTime()));
		}
		return ret;
	}
}
