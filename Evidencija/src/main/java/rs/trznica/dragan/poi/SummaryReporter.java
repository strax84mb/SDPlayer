package rs.trznica.dragan.poi;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
	
	private SumarySheetGenerator generator;

	@Autowired
	public SummaryReporter(ApplicationContext ctx) {
		tankovanjeDao = ctx.getBean(TankovanjeDao.class);
	}

	public void makeReport(List<Potrosac> potrosaci, String fromMonth, String tilMonth) throws IOException {
		int months = ((Integer.valueOf(tilMonth.substring(0, 4)) * 12) + Integer.valueOf(tilMonth.substring(5))) 
				- ((Integer.valueOf(fromMonth.substring(0, 4)) * 12) + Integer.valueOf(fromMonth.substring(5))) 
				+ 1;

		ConsumerStatSummary[] consumerSums = new ConsumerStatSummary[potrosaci.size()];
		ConsumerStatSummary[] monthSums = new ConsumerStatSummary[months];
		for (int i = 0; i < potrosaci.size(); i++) {
			consumerSums[i] = new ConsumerStatSummary();
		}
		for (int i = 0; i < months; i++) {
			monthSums[i] = new ConsumerStatSummary();
		}
		List<ConsumerStatSummary[]> monthlyTotals = new ArrayList<ConsumerStatSummary[]>();
		for (int i = 0; i < months; i++) {
			ConsumerStatSummary[] newSum = new ConsumerStatSummary[potrosaci.size()];
			monthlyTotals.add(newSum);
			for (int j = 0; j < potrosaci.size(); j++) {
				newSum[j] = new ConsumerStatSummary();
			}
		}

		generator = new SumarySheetGenerator(months, potrosaci.size());
		XSSFWorkbook wb = generator.getWorkbook();
		writeMonthNames(wb, months, fromMonth);
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
						if (potrosaci.get(i).getMeriKm()) {
							cell = getCell(wb, potrosaci.size(), i, monthIndex, 
									(GorivoType.BMB.equals(potrosaci.get(i).getGorivo())) ? DataType.KM_BMB : DataType.KM_ED);
						} else {
							cell = getCell(wb, potrosaci.size(), i, monthIndex, 
									(GorivoType.BMB.equals(potrosaci.get(i).getGorivo())) ? DataType.RS_BMB : DataType.RS_ED);
						}
						cell.setCellValue(getKmRsString(potrosaci.get(i).getMeriKm(), tempValue));
					}
					consumerSums[i].addKm_Rs(potrosaci.get(i).getGorivo(), potrosaci.get(i).getMeriKm(), tempValue);
					monthSums[monthIndex].addKm_Rs(potrosaci.get(i).getGorivo(), potrosaci.get(i).getMeriKm(), tempValue);
					monthlyTotals.get(monthIndex)[i].addKm_Rs(potrosaci.get(i).getGorivo(), potrosaci.get(i).getMeriKm(), 
							(GorivoType.BMB.equals(potrosaci.get(i).getGorivo())) ? consumerSums[i].getKmBmb() : consumerSums[i].getKmEd());
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
				monthlyTotals.get(monthIndex)[i].addLit(potrosaci.get(i).getGorivo(), 
						(GorivoType.BMB.equals(potrosaci.get(i).getGorivo())) ? consumerSums[i].getLitBmb() : consumerSums[i].getLitEd());
				// Fill price
				tempValue = getPriceForMonth(tankovanja, currMonth);
				if (tempValue > 0L) {
					cell = getCell(wb, potrosaci.size(), i, monthIndex, 
							((GorivoType.BMB.equals(potrosaci.get(i).getGorivo())) ? DataType.PRICE_BMB : DataType.PRICE_ED));
					cell.setCellValue(tempValue.doubleValue() / 100d);
				}
				consumerSums[i].addPrice(potrosaci.get(i).getGorivo(), tempValue);
				monthSums[monthIndex].addPrice(potrosaci.get(i).getGorivo(), tempValue);
				monthlyTotals.get(monthIndex)[i].addPrice(potrosaci.get(i).getGorivo(), 
						(GorivoType.BMB.equals(potrosaci.get(i).getGorivo())) ? consumerSums[i].getPriceBmb() : consumerSums[i].getPriceEd());
				// Increase month
				currMonth = increaseMonthString(currMonth);
			}
		}
		// Write month sums
		XSSFCell cell = null;
		for (int i = 0; i < months; i++) {
			cell = getCell(wb, potrosaci.size(), potrosaci.size(), i, DataType.KM_BMB);
			cell.setCellValue(getKmRsString(true, monthSums[i].getKmBmb()));
			cell = getCell(wb, potrosaci.size(), potrosaci.size(), i, DataType.KM_ED);
			cell.setCellValue(getKmRsString(true, monthSums[i].getKmEd()));
			cell = getCell(wb, potrosaci.size(), potrosaci.size() + 1, i, DataType.KM_BMB);
			cell.setCellValue(getKmRsString(true, monthSums[i].getKmBmb() + monthSums[i].getKmEd()));
			cell = getCell(wb, potrosaci.size(), potrosaci.size(), i, DataType.RS_BMB);
			cell.setCellValue(getKmRsString(false, monthSums[i].getRsBmb()));
			cell = getCell(wb, potrosaci.size(), potrosaci.size(), i, DataType.RS_ED);
			cell.setCellValue(getKmRsString(false, monthSums[i].getRsEd()));
			cell = getCell(wb, potrosaci.size(), potrosaci.size() + 1, i, DataType.RS_BMB);
			cell.setCellValue(getKmRsString(false, monthSums[i].getRsBmb() + monthSums[i].getRsEd()));
			cell = getCell(wb, potrosaci.size(), potrosaci.size(), i, DataType.LIT_BMB);
			cell.setCellValue(Long.valueOf(monthSums[i].getLitBmb()).doubleValue() / 100d);
			cell = getCell(wb, potrosaci.size(), potrosaci.size(), i, DataType.LIT_ED);
			cell.setCellValue(Long.valueOf(monthSums[i].getLitEd()).doubleValue() / 100d);
			cell = getCell(wb, potrosaci.size(), potrosaci.size() + 1, i, DataType.LIT_BMB);
			cell.setCellValue(Long.valueOf(monthSums[i].getLitBmb() + monthSums[i].getLitEd()).doubleValue() / 100d);
			cell = getCell(wb, potrosaci.size(), potrosaci.size(), i, DataType.PRICE_BMB);
			cell.setCellValue(Long.valueOf(monthSums[i].getPriceBmb()).doubleValue() / 100d);
			cell = getCell(wb, potrosaci.size(), potrosaci.size(), i, DataType.PRICE_ED);
			cell.setCellValue(Long.valueOf(monthSums[i].getPriceEd()).doubleValue() / 100d);
			cell = getCell(wb, potrosaci.size(), potrosaci.size() + 1, i, DataType.PRICE_BMB);
			cell.setCellValue(Long.valueOf(monthSums[i].getPriceBmb() + monthSums[i].getPriceEd()).doubleValue() / 100d);
			fillMonthlyCumulativeSum(wb.getSheetAt(i), monthlyTotals.get(i), i);
		}
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
		for (int i = 0; i < sheetsNum; i++) {
			sheet = wb.getSheetAt(i);
			for (int j = 0; j < consumers.size(); j++) {
				row = sheet.getRow(j + 3);
				row.getCell(0).setCellValue(consumers.get(j).getVozilo() ? consumers.get(j).getRegOznaka() : consumers.get(j).getTip());
			}
		}
	}

	private void writeMonthNames(XSSFWorkbook wb, int months, String startMonth) {
		int monthNum = Integer.valueOf(startMonth.substring(5));
		String currMonth = startMonth;
		for (int i = 0; i < months; i++) {
			XSSFRow row = wb.getSheetAt(i).getRow(0);
			row.getCell(1).setCellValue(getMonthString(monthNum));
			row.getCell(7).setCellValue("Zbir zaklju\u010Dno sa ovim mesecom");
			wb.setSheetName(i, currMonth.substring(0,  4) + " " + getMonthString(monthNum));
			currMonth = increaseMonthString(currMonth);
			monthNum = increaseMonthNum(monthNum);
		}
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
		return (lastKM > 0L && prevKM > 0L) ? lastKM - prevKM : 0L;
	}
	
	private XSSFCell getCell(XSSFWorkbook wb, int consumersNumber, int offset, int month, DataType type) {
		XSSFRow row;
		if (offset >= consumersNumber) {
			if (type.name().startsWith("RS_")) {
				offset = (offset - consumersNumber) * 2 + 1 + consumersNumber;
			} else {
				offset = (offset - consumersNumber) * 2 + consumersNumber;
			}
		}
		row = wb.getSheetAt(month).getRow(offset + generator.getTitleSpan());
		
		return row.getCell(type.getColumnOffset());
	}
	
	private void fillMonthlyCumulativeSum(XSSFSheet sheet, ConsumerStatSummary[] consumerSums, int monthlyIndex) {
		int firstRow = 3;
		int firstColumn = 7;
		long kmBMB = 0L;
		long kmED = 0L;
		long rsBMB = 0L;
		long rsED = 0L;
		long litBMB = 0L;
		long litED = 0L;
		long priceBMB = 0L;
		long priceED = 0L;
		XSSFRow row = null;
		for (int i = 0; i < consumerSums.length; i++) {
			row = sheet.getRow(firstRow + i);
			if (consumerSums[i].getKmBmb() > 0L) {
				row.getCell(firstColumn).setCellValue(getKmRsString(true, consumerSums[i].getKmBmb()));
				kmBMB += consumerSums[i].getKmBmb();
			} else if (consumerSums[i].getRsBmb() > 0L) {
				row.getCell(firstColumn).setCellValue(getKmRsString(false, consumerSums[i].getRsBmb()));
				rsBMB += consumerSums[i].getRsBmb();
			} else {
				row.getCell(firstColumn).setCellValue("0");
			}
			if (consumerSums[i].getKmEd() > 0L) {
				row.getCell(firstColumn + 1).setCellValue(getKmRsString(true, consumerSums[i].getKmEd()));
				kmED += consumerSums[i].getKmBmb();
			} else if (consumerSums[i].getRsEd() > 0L) {
				row.getCell(firstColumn + 1).setCellValue(getKmRsString(false, consumerSums[i].getRsEd()));
				rsED += consumerSums[i].getRsEd();
			} else {
				row.getCell(firstColumn + 1).setCellValue("0");
			}
			row.getCell(firstColumn + 2).setCellValue(Long.valueOf(consumerSums[i].getLitBmb()).doubleValue() / 100d);
			litBMB += consumerSums[i].getLitBmb();
			row.getCell(firstColumn + 3).setCellValue(Long.valueOf(consumerSums[i].getLitEd()).doubleValue() / 100d);
			litED += consumerSums[i].getLitEd();
			row.getCell(firstColumn + 4).setCellValue(Long.valueOf(consumerSums[i].getPriceBmb()).doubleValue() / 100d);
			priceBMB += consumerSums[i].getPriceBmb();
			row.getCell(firstColumn + 5).setCellValue(Long.valueOf(consumerSums[i].getPriceEd()).doubleValue() / 100d);
			priceED += consumerSums[i].getPriceEd();
		}
		row = sheet.getRow(firstRow + consumerSums.length);
		row.getCell(firstColumn).setCellValue(getKmRsString(true, kmBMB));
		row.getCell(firstColumn + 1).setCellValue(getKmRsString(true, kmED));
		row.getCell(firstColumn + 2).setCellValue(Long.valueOf(litBMB).doubleValue() / 100d);
		row.getCell(firstColumn + 3).setCellValue(Long.valueOf(litED).doubleValue() / 100d);
		row.getCell(firstColumn + 4).setCellValue(Long.valueOf(priceBMB).doubleValue() / 100d);
		row.getCell(firstColumn + 5).setCellValue(Long.valueOf(priceED).doubleValue() / 100d);
		row = sheet.getRow(firstRow + consumerSums.length + 1);
		row.getCell(firstColumn).setCellValue(getKmRsString(false, rsBMB));
		row.getCell(firstColumn + 1).setCellValue(getKmRsString(false, rsED));
		row = sheet.getRow(firstRow + consumerSums.length + 2);
		row.getCell(firstColumn).setCellValue(getKmRsString(true, kmBMB + kmED));
		row.getCell(firstColumn + 2).setCellValue(Long.valueOf(litBMB + litED).doubleValue() / 100d);
		row.getCell(firstColumn + 4).setCellValue(Long.valueOf(priceBMB + priceED).doubleValue() / 100d);
		row = sheet.getRow(firstRow + consumerSums.length + 3);
		row.getCell(firstColumn).setCellValue(getKmRsString(false, rsBMB + rsED));
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

	private String getKmRsString(boolean km, long value) {
		StringBuilder builder = new StringBuilder(Long.valueOf(value).toString());
		int length = builder.length();
		int dots = length / 3;
		int offset = length % 3;
		if (offset == 0) {
			dots--;
			offset = 3;
		}
		if (dots > 0) {
			for (int i = 0; i < dots; i++) {
				builder.insert(offset + i + (i * 3), '.');
			}
		}
		return builder.append((km) ? " k" : " r").toString();
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
		NAME(0),
		KM_BMB(1),
		KM_ED(2),
		RS_BMB(1),
		RS_ED(2),
		LIT_BMB(3),
		LIT_ED(4),
		PRICE_BMB(5),
		PRICE_ED(6);
		
		private int columnOffset = -1;
		
		DataType(int columnOffset) {
			this.columnOffset = columnOffset;
		}
		
		public int getColumnOffset() {
			return columnOffset; 
		}
	}
}
