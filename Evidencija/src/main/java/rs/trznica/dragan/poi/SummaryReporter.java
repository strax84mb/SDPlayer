package rs.trznica.dragan.poi;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import rs.trznica.dragan.dao.TankovanjeDao;
import rs.trznica.dragan.entities.support.GorivoType;
import rs.trznica.dragan.entities.tankovanje.Potrosac;
import rs.trznica.dragan.entities.tankovanje.Tankovanje;

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

	public void makeReport(List<Potrosac> potrosaci, String fromMonth, String tilMonth) throws IOException {
		int firstMonth = Integer.valueOf(fromMonth.substring(5));
		int months = ((Integer.valueOf(tilMonth.substring(0, 4)) * 12) + Integer.valueOf(tilMonth.substring(5))) 
				- ((Integer.valueOf(fromMonth.substring(0, 4)) * 12) + Integer.valueOf(fromMonth.substring(5))) 
				+ 1;
		int sheetsNum = getSheetsNum(potrosaci.size(), months);

		/*
		 * Last is yearly sum
		 * Sum indexes:	0 - km
		 * 				1 - bmb
		 * 				2 - ed
		 * 				3 - price
		 */
		long[][] sums = new long[potrosaci.size() + 1][4];
		long[][] monthSums = new long[months][4];
		for (int i = 0; i < 4; i++) {
			sums[potrosaci.size()][i] = 0L;
		}

		SumarySheetGenerator generator = new SumarySheetGenerator(months, potrosaci.size());
		XSSFWorkbook wb = generator.getWorkbook();
		writeMonthNames(wb, months, sheetsNum, potrosaci.size(), firstMonth);
		writeConsumerDesignations(wb, potrosaci);
		for (int i = 0; i < potrosaci.size(); i++) {
			List<Tankovanje> tankovanja = StreamSupport.stream(
					tankovanjeDao.listInIntervalForConsumer(potrosaci.get(i).getId(), fromMonth, tilMonth).spliterator(), false)
					.collect(Collectors.toList());
			tankovanja = insertLastPrevFillup(tankovanja, potrosaci.get(i).getId(), fromMonth);
			for (int z = 0; z < 4; z++) {
				sums[i][z] = 0L;
			}
			String currMonth = fromMonth;
			Long tempValue = 0l;
			XSSFCell cell = null;
			for (int monthIndex = 0; monthIndex < months; monthIndex++) {
				if (potrosaci.get(i).getVozilo()) {
					// Fill km
					tempValue = getKmForMonth(tankovanja, currMonth);
					if (tempValue > 0L) {
						cell = getCell(wb, potrosaci.size(), i, monthIndex, DataType.KM);
						cell.setCellValue(tempValue);
					}
					sums[i][0] += tempValue;
					sums[potrosaci.size()][0] += tempValue;
					monthSums[monthIndex][0] += tempValue;
				}
				// Fill fuel volume
				tempValue = getVolumeForMonth(tankovanja, currMonth);
				if (tempValue > 0L) {
					cell = getCell(wb, potrosaci.size(), i, monthIndex, 
							((GorivoType.BMB.equals(potrosaci.get(i).getGorivo())) ? DataType.BMB : DataType.ED));
					cell.setCellValue(tempValue.doubleValue() / 100d);
				}
				if (GorivoType.BMB.equals(potrosaci.get(i).getGorivo())) {
					sums[i][1] += tempValue;
					sums[potrosaci.size()][1] += tempValue;
					monthSums[monthIndex][1] += tempValue;
				} else {
					sums[i][2] += tempValue;
					sums[potrosaci.size()][2] += tempValue;
					monthSums[monthIndex][2] += tempValue;
				}
				// Fill price
				tempValue = getPriceForMonth(tankovanja, currMonth);
				if (tempValue > 0L) {
					cell = getCell(wb, potrosaci.size(), i, monthIndex, DataType.PRICE);
					cell.setCellValue(tempValue.doubleValue() / 100d);
				}
				sums[i][3] += tempValue;
				sums[potrosaci.size()][3] += tempValue;
				monthSums[monthIndex][3] += tempValue;
				// Increase month
				currMonth = increaseMonthString(currMonth);
			}
		}
		// Write month sums
		XSSFCell cell = null;
		for (int i = 0; i < months; i++) {
			cell = getCell(wb, potrosaci.size(), potrosaci.size(), i, DataType.KM);
			cell.setCellValue(Long.valueOf(monthSums[i][0]));
			cell = getCell(wb, potrosaci.size(), potrosaci.size(), i, DataType.BMB);
			cell.setCellValue(Long.valueOf(monthSums[i][1]).doubleValue() / 100d);
			cell = getCell(wb, potrosaci.size(), potrosaci.size(), i, DataType.ED);
			cell.setCellValue(Long.valueOf(monthSums[i][2]).doubleValue() / 100d);
			cell = getCell(wb, potrosaci.size(), potrosaci.size(), i, DataType.PRICE);
			cell.setCellValue(Long.valueOf(monthSums[i][3]).doubleValue() / 100d);
		}
		// Write consumer sums
		XSSFSheet sheet = wb.getSheetAt(wb.getNumberOfSheets() - 1);
		XSSFRow row;
		for (int i = 0; i < potrosaci.size(); i++) {
			row = sheet.getRow(2 + i);
			row.getCell(1).setCellValue(sums[i][0]);
			row.getCell(2).setCellValue(Long.valueOf(sums[i][1]).doubleValue() / 100d);
			row.getCell(3).setCellValue(Long.valueOf(sums[i][2]).doubleValue() / 100d);
			row.getCell(4).setCellValue(Long.valueOf(sums[i][3]).doubleValue() / 100d);
		}
		// Write total sums
		row = sheet.getRow(3 + potrosaci.size());
		row.getCell(1).setCellValue(sums[potrosaci.size()][0]);
		row.getCell(2).setCellValue(Long.valueOf(sums[potrosaci.size()][1]).doubleValue() / 100d);
		row.getCell(3).setCellValue(Long.valueOf(sums[potrosaci.size()][2]).doubleValue() / 100d);
		row.getCell(4).setCellValue(Long.valueOf(sums[potrosaci.size()][3]).doubleValue() / 100d);
		// Save File
		try (FileOutputStream fos = new FileOutputStream(xlsTablesPath + "/zbirni_izvestaj.xlsx")) {
			wb.write(fos);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private List<Tankovanje> insertLastPrevFillup(List<Tankovanje> fillups, Long consumerId, String fromMonth) {
		List<Tankovanje> result = tankovanjeDao.getLastFill(consumerId, fromMonth, new PageRequest(0, 1));
		if (result != null && !result.isEmpty()) {
			fillups.add(0, result.get(0));
		}
		return fillups;
	}

	private void writeConsumerDesignations(XSSFWorkbook wb, List<Potrosac> consumers) {
		int sheetsNum = wb.getNumberOfSheets();
		XSSFSheet sheet;
		XSSFRow row;
		for (int i = 0; i < sheetsNum - 1; i++) {
			sheet = wb.getSheetAt(i);
			for (int j = 0; j < consumers.size(); j++) {
				row = sheet.getRow(j + 3);
				row.getCell(0).setCellValue(consumers.get(j).getVozilo() ? consumers.get(j).getRegOznaka() : consumers.get(j).getTip());
				row.getCell(13).setCellValue(consumers.get(j).getVozilo() ? consumers.get(j).getRegOznaka() : consumers.get(j).getTip());
			}
			sheet.getRow(consumers.size() + 3).getCell(0).setCellValue("Ukupno:");
			sheet.getRow(consumers.size() + 3).getCell(13).setCellValue("Ukupno:");
			if (consumers.size() <= 9) {
				for (int j = 0; j < consumers.size(); j++) {
					row = sheet.getRow(consumers.size() + j + 7);
					row.getCell(0).setCellValue(consumers.get(j).getVozilo() ? consumers.get(j).getRegOznaka() : consumers.get(j).getTip());
					row.getCell(13).setCellValue(consumers.get(j).getVozilo() ? consumers.get(j).getRegOznaka() : consumers.get(j).getTip());
				}
				sheet.getRow(consumers.size() * 2 + 7).getCell(0).setCellValue("Ukupno:");
				sheet.getRow(consumers.size() * 2 + 7).getCell(13).setCellValue("Ukupno:");
			}
		}
		sheet = wb.getSheetAt(wb.getNumberOfSheets() - 1);
		for (int j = 0; j < consumers.size(); j++) {
			row = sheet.getRow(j + 2);
			row.getCell(0).setCellValue(consumers.get(j).getVozilo() ? consumers.get(j).getRegOznaka() : consumers.get(j).getTip());
		}
	}

	private void writeMonthNames(XSSFWorkbook wb, int months, int sheetsNum, int consumersNum, int startMonth) {
		int monthNum = startMonth;
		if (consumersNum < 9) {
			for (int i = 0; i < sheetsNum; i++) {
				XSSFRow row = wb.getSheetAt(i).getRow(0);
				row.getCell(1).setCellValue(getMonthString(monthNum));
				monthNum = increaseMonthNum(monthNum);
				row.getCell(5).setCellValue(getMonthString(monthNum));
				monthNum = increaseMonthNum(monthNum);
				row.getCell(9).setCellValue(getMonthString(monthNum));
				monthNum = increaseMonthNum(monthNum);
				// Write next row
				row = wb.getSheetAt(i).getRow(consumersNum + 5);
				row.getCell(1).setCellValue(getMonthString(monthNum));
				monthNum = increaseMonthNum(monthNum);
				row.getCell(5).setCellValue(getMonthString(monthNum));
				monthNum = increaseMonthNum(monthNum);
				row.getCell(9).setCellValue(getMonthString(monthNum));
				monthNum = increaseMonthNum(monthNum);
			}
		} else {
			for (int i = 0; i < sheetsNum; i++) {
				XSSFRow row = wb.getSheetAt(i).getRow(0);
				row.getCell(1).setCellValue(getMonthString(monthNum));
				monthNum = increaseMonthNum(monthNum);
				row.getCell(5).setCellValue(getMonthString(monthNum));
				monthNum = increaseMonthNum(monthNum);
				row.getCell(9).setCellValue(getMonthString(monthNum));
				monthNum = increaseMonthNum(monthNum);
			}
		}
	}

	private int getSheetsNum(int consumersNum, int months) {
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
		return sheetNum;
	}

	private int increaseMonthNum(int monthNum) {
		monthNum++;
		return (monthNum > 12) ? 1 : monthNum;
	}

	private Long getVolumeForMonth(List<Tankovanje> tankovanja, String month) {
		long sum = 0L;
		for (int i = 0; i < tankovanja.size(); i++) {
			if (month.equals(tankovanja.get(i).getMesec())) {
				sum += tankovanja.get(i).getKolicina();
			}
		}
		return sum;
	}
	
	private Long getPriceForMonth(List<Tankovanje> tankovanja, String month) {
		long sum = 0L;
		for (int i = 0; i < tankovanja.size(); i++) {
			if (month.equals(tankovanja.get(i).getMesec())) {
				sum += tankovanja.get(i).getKolicina() * tankovanja.get(i).getJedCena() / 100L;
			}
		}
		return sum;
	}
	
	private Long getKmForMonth(List<Tankovanje> tankovanja, String month) {
		long prevKM = 0L;
		long lastKM = 0L;
		for (int i = 0; i < tankovanja.size(); i++) {
			if (month.equals(tankovanja.get(i).getMesec())) {
				lastKM = tankovanja.get(i).getKilometraza();
			} else if (lastKM == 0L) {
				prevKM = tankovanja.get(i).getKilometraza();
			}
		}
		return (lastKM > 0L) ? lastKM - prevKM : 0L;
	}
	
	private XSSFCell getCell(XSSFWorkbook wb, int consumersNum, int consumer, int month, DataType type) {
		XSSFRow row;
		if (consumersNum < 9) {
			if (month % 6 < 3) {
				row = wb.getSheetAt(month / 6).getRow(consumer + 3);
			} else {
				row = wb.getSheetAt(month / 6).getRow(consumer + consumersNum + 7);
			}
		} else {
			row = wb.getSheetAt(month / 3).getRow(consumer + 3);
		}
		switch (type) {
		case KM:
			return row.getCell(1 + ((month % 3) * 4));
		case BMB:
			return row.getCell(2 + ((month % 3) * 4));
		case ED:
			return row.getCell(3 + ((month % 3) * 4));
		case PRICE:
			return row.getCell(4 + ((month % 3) * 4));
		case NAME:
			return row.getCell(0);
		default:
			return row.getCell(13);
		}
	}
	
	private String increaseMonthString(String monthString) {
		int year = Integer.valueOf(monthString.substring(0, 4));
		int month = Integer.valueOf(monthString.substring(5));
		if (++month > 12) {
			year++;
			month = 1;
		}
		StringBuilder builder = new StringBuilder(String.valueOf(year));
		builder.append("-");
		if (month < 10) {
			builder.append("0");
		}
		builder.append(month);
		return builder.toString();
	}

	private String getMonthString(int month) {
		switch (month) {
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
	
	private enum DataType {
		NAME,
		BMB,
		ED,
		KM,
		PRICE,
		LAST_NAME
	}
}
