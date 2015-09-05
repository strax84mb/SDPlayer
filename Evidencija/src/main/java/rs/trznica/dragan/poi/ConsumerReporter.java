package rs.trznica.dragan.poi;

import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import rs.trznica.dragan.dao.TankovanjeDao;

@Component
public class ConsumerReporter {

	private TankovanjeDao tankovanjeDao;

	@Autowired
	public ConsumerReporter(ApplicationContext ctx) {
		tankovanjeDao = ctx.getBean(TankovanjeDao.class);
	}

	public final static void makeReport() throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook();
		
		
		
		
		
		
		wb.close();
	}
}
