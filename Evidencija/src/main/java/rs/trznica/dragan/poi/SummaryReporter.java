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
	
	private int maxConsumers = 8;
	private int monthColumns = 2;
	private SumarySheetGenerator generator;

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
		 * Sum indexes:	0 - km bmb
		 * 				1 - km ed
		 * 				2 - lit bmb
		 * 				3 - lit ed
		 * 				4 - price
		 */
		ConsumerStatSummary[] consumerSums = new ConsumerStatSummary[potrosaci.size()];
		ConsumerStatSummary[] monthSums = new ConsumerStatSummary[months];
		for (int i = 0; i < potrosaci.size(); i++) {
			consumerSums[i] = new ConsumerStatSummary();
		}
		for (int i = 0; i < potrosaci.size(); i++) {
			monthSums[i] = new ConsumerStatSummary();
		}

		generator = new SumarySheetGenerator(months, potrosaci.size());
		XSSFWorkbook wb = generator.getWorkbook();
		writeMonthNames(wb, months, sheetsNum, potrosaci.size(), firstMonth);
		writeConsumerDesignations(wb, potrosaci);
		for (int i = 0; i < potrosaci.size(); i++) {
			List<Tankovanje> tankovanja = StreamSupport.stream(
					tankovanjeDao.listInIntervalForConsumer(potrosaci.get(i).getId(), fromMonth, tilMonth).spliterator(), false)
					.collect(Collectors.toList());
			tankovanja = insertLastPrevFillup(tankovanja, potrosaci.get(i).getId(), fromMonth);
			String currMonth = fromMonth;
			Long tempValue = 0l;
			XSSFCell cell = null;
			for (int monthIndex = 0; monthIndex < months; monthIndex++) {
				if (potrosaci.get(i).getVozilo()) {
					// Fill km
					tempValue = getKmForMonth(tankovanja, currMonth);
					if (tempValue > 0L) {
						cell = getCell(wb, potrosaci.size(), i, monthIndex, 
								(GorivoType.BMB.equals(potrosaci.get(i).getGorivo())) ? DataType.KM_BMB : DataType.KM_ED);
						cell.setCellValue(tempValue);
					}
					consumerSums[i].addKm(potrosaci.get(i).getGorivo(), tempValue);
					monthSums[monthIndex].addKm(potrosaci.get(i).getGorivo(), tempValue);
				}
				// Fill fuel volume
				tempValue = getVolumeForMonth(tankovanja, currMonth);
				if (tempValue > 0L) {
					cell = getCell(wb, potrosaci.size(), i, monthIndex, 
							((GorivoType.BMB.equals(potrosaci.get(i).getGorivo())) ? DataType.LIT_BMB : DataType.LIT_ED));
					cell.setCellValue(tempValue.doubleValue() / 100d);
				}
				consumerSums[i].addLit(potrosaci.get(i).getGorivo(), tempValue);
				monthSums[monthIndex].addLit(potrosaci.get(i).getGorivo(), tempValue);
				// Fill price
				tempValue = getPriceForMonth(tankovanja, currMonth);
				if (tempValue > 0L) {
					cell = getCell(wb, potrosaci.size(), i, monthIndex, DataType.PRICE);
					cell.setCellValue(tempValue.doubleValue() / 100d);
				}
				consumerSums[i].addPrice(tempValue);
				monthSums[monthIndex].addPrice(tempValue);
				// Increase month
				currMonth = increaseMonthString(currMonth);
			}
		}
		// Write month sums
		XSSFCell cell = null;
		for (int i = 0; i < months; i++) {
			cell = getCell(wb, potrosaci.size(), potrosaci.size(), i, DataType.KM_BMB);
			cell.setCellValue(Long.valueOf(monthSums[i].getKmBmb()));
			cell = getCell(wb, potrosaci.size(), potrosaci.size(), i, DataType.KM_ED);
			cell.setCellValue(Long.valueOf(monthSums[i].getKmEd()));
			cell = getCell(wb, potrosaci.size(), potrosaci.size() + 1, i, DataType.KM_BMB);
			cell.setCellValue(Long.valueOf(monthSums[i].getKmBmb() + monthSums[i].getKmEd()));
			cell = getCell(wb, potrosaci.size(), potrosaci.size(), i, DataType.LIT_BMB);
			cell.setCellValue(Long.valueOf(monthSums[i].getLitBmb()).doubleValue() / 100d);
			cell = getCell(wb, potrosaci.size(), potrosaci.size(), i, DataType.LIT_ED);
			cell.setCellValue(Long.valueOf(monthSums[i].getLitEd()).doubleValue() / 100d);
			cell = getCell(wb, potrosaci.size(), potrosaci.size() + 1, i, DataType.LIT_BMB);
			cell.setCellValue(Long.valueOf(monthSums[i].getLitBmb() + monthSums[i].getLitEd()).doubleValue() / 100d);
			cell = getCell(wb, potrosaci.size(), potrosaci.size(), i, DataType.PRICE);
			cell.setCellValue(Long.valueOf(monthSums[i].getPrice()).doubleValue() / 100d);
		}
		// Write consumer sums
		XSSFSheet sheet = wb.getSheetAt(wb.getNumberOfSheets() - 1);
		XSSFRow row;
		ConsumerStatSummary finalSumm = new ConsumerStatSummary();
		for (int i = 0; i < potrosaci.size(); i++) {
			row = sheet.getRow(2 + i);
			row.getCell(1).setCellValue(consumerSums[i].getKmBmb());
			finalSumm.addKmBmb(consumerSums[i].getKmBmb());
			row.getCell(2).setCellValue(consumerSums[i].getKmEd());
			finalSumm.addKmEd(consumerSums[i].getKmEd());
			row.getCell(3).setCellValue(Long.valueOf(consumerSums[i].getLitBmb()).doubleValue() / 100d);
			finalSumm.addLitBmb(consumerSums[i].getLitBmb());
			row.getCell(4).setCellValue(Long.valueOf(consumerSums[i].getLitEd()).doubleValue() / 100d);
			finalSumm.addLitEd(consumerSums[i].getLitEd());
			row.getCell(5).setCellValue(Long.valueOf(consumerSums[i].getPrice()).doubleValue() / 100d);
			finalSumm.addPrice(consumerSums[i].getPrice());
		}
		// Write total sums
		row = sheet.getRow(3 + potrosaci.size());
		row.getCell(1).setCellValue(finalSumm.getKmBmb());
		row.getCell(2).setCellValue(finalSumm.getKmEd());
		row.getCell(3).setCellValue(Long.valueOf(finalSumm.getLitBmb()).doubleValue() / 100d);
		row.getCell(4).setCellValue(Long.valueOf(finalSumm.getLitEd()).doubleValue() / 100d);
		row.getCell(5).setCellValue(Long.valueOf(finalSumm.getPrice()).doubleValue() / 100d);
		row = sheet.getRow(4 + potrosaci.size());
		row.getCell(1).setCellValue(finalSumm.getKmBmb() + finalSumm.getKmEd());
		row.getCell(3).setCellValue(Long.valueOf(finalSumm.getLitBmb() + finalSumm.getLitEd()).doubleValue() / 100d);
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
				row.getCell(monthColumns * 5 + 1).setCellValue(consumers.get(j).getVozilo() ? consumers.get(j).getRegOznaka() : consumers.get(j).getTip());
			}
			if (consumers.size() <= maxConsumers) {
				for (int j = 0; j < consumers.size(); j++) {
					row = sheet.getRow(consumers.size() + j + 7);
					row.getCell(0).setCellValue(consumers.get(j).getVozilo() ? consumers.get(j).getRegOznaka() : consumers.get(j).getTip());
					row.getCell(monthColumns * 5 + 1).setCellValue(consumers.get(j).getVozilo() ? consumers.get(j).getRegOznaka() : consumers.get(j).getTip());
				}
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
		if (consumersNum <= maxConsumers) {
			for (int i = 0; i < sheetsNum; i++) {
				XSSFRow row = wb.getSheetAt(i).getRow(0);
				for (int j = 0; j < monthColumns; j++) {
					row.getCell(j * 5 + 1).setCellValue(getMonthString(monthNum));
					monthNum = increaseMonthNum(monthNum);
				}
				// Write next row
				row = wb.getSheetAt(i).getRow(consumersNum + generator.getTitleSpan() + generator.getFooterSpan());
				for (int j = 0; j < monthColumns; j++) {
					row.getCell(j * 5 + 1).setCellValue(getMonthString(monthNum));
					monthNum = increaseMonthNum(monthNum);
				}
			}
		} else {
			for (int i = 0; i < sheetsNum; i++) {
				XSSFRow row = wb.getSheetAt(i).getRow(0);
				for (int j = 0; j < monthColumns; j++) {
					row.getCell(j * 5 + 1).setCellValue(getMonthString(monthNum));
					monthNum = increaseMonthNum(monthNum);
				}
			}
		}
	}

	private int getSheetsNum(int consumersNum, int months) {
		int sheetNum = 0;
		if (consumersNum <= maxConsumers) {
			sheetNum = months / monthColumns / 2 + 1;
		} else {
			sheetNum = months / monthColumns + 1;
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
		if (consumersNum <= maxConsumers) {
			if (month % (monthColumns * 2) < monthColumns) {
				row = wb.getSheetAt(month / (monthColumns * 2)).getRow(consumer + generator.getTitleSpan());
			} else {
				row = wb.getSheetAt(month / (monthColumns * 2)).getRow(consumer 
						+ consumersNum 
						+ (generator.getTitleSpan() * 2) 
						+ generator.getFooterSpan());
			}
		} else {
			row = wb.getSheetAt(month / monthColumns).getRow(consumer + generator.getTitleSpan());
		}
		switch (type) {
		case KM_BMB:
			return row.getCell(1 + ((month % monthColumns) * 5));
		case KM_ED:
			return row.getCell(2 + ((month % monthColumns) * 5));
		case LIT_BMB:
			return row.getCell(3 + ((month % monthColumns) * 5));
		case LIT_ED:
			return row.getCell(4 + ((month % monthColumns) * 5));
		case PRICE:
			return row.getCell(5 + ((month % monthColumns) * 5));
		case NAME:
			return row.getCell(0);
		default:
			return row.getCell(monthColumns * 5 + 1);
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
		LIT_BMB,
		LIT_ED,
		KM_BMB,
		KM_ED,
		PRICE,
		LAST_NAME
	}
}
