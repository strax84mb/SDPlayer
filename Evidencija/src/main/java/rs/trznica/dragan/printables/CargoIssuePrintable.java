package rs.trznica.dragan.printables;

import org.springframework.util.StringUtils;
import rs.trznica.dragan.entities.putninalog.PutniNalogSql;
import rs.trznica.dragan.forms.support.DateUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class CargoIssuePrintable implements Printable {

	private PutniNalogSql putniNalog;
	private BufferedImage img;
	
	public CargoIssuePrintable(PutniNalogSql putniNalog, String resourceDir) throws IOException {
		this.putniNalog = putniNalog;
		this.img = ImageIO.read(new File(resourceDir + "/Teretni1.bmp"));
	}
	
	@Override
	public int print(Graphics g, PageFormat pf, int pageIndex)
			throws PrinterException {
		if(pageIndex >= 2) return NO_SUCH_PAGE;
		Graphics2D g2d = (Graphics2D)g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		double pageWidth = pf.getImageableWidth();
		double pageHeight = pf.getImageableHeight();
		double imageWidth = 1620;
		double imageHeight = 1081;
		double scaleX = pageWidth / imageWidth;
		double scaleY = pageHeight / imageHeight;
		double scaleFactor = Math.min(scaleX, scaleY);
		g2d.scale(scaleFactor, scaleFactor);
		g2d.drawImage(img, 1, 50, null);
		g2d.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		g2d.setColor(Color.BLACK);
		FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
		// Stampanje
		String temp = putniNalog.getVrstaPrevoza();
		int width = fm.stringWidth(temp);
		g2d.drawString(temp, 1410 - (width / 2), 88);
		g2d.drawString(putniNalog.getRelacija(), 927, 492);
		temp = putniNalog.getMarkaVozila();
		width = fm.stringWidth(temp);
		g2d.drawString(temp, 970 - (width / 2), 665);
		temp = putniNalog.getTipVozila();
		width = fm.stringWidth(temp);
		g2d.drawString(temp, 970 - (width / 2), 700);
		temp = putniNalog.getNosivost().toString();
		width = fm.stringWidth(temp);
		g2d.drawString(temp, 1145 - (width / 2), 700);
		temp = putniNalog.getTezina().toString();
		width = fm.stringWidth(temp);
		g2d.drawString(temp, 1234 - (width / 2), 700);
		temp = "SU";
		width = fm.stringWidth(temp);
		g2d.drawString(temp, 1540 - (width / 2), 700);
		g2d.setFont(new Font("Times New Roman", Font.PLAIN, 40));
		g2d.drawString(putniNalog.getRedniBroj().toString(), 1430, 320);
		g2d.setFont(new Font("Times New Roman", Font.BOLD, 30));
		temp = putniNalog.getKorisnik();
		width = fm.stringWidth(temp);
		g2d.drawString(temp, 965, 146);
		g2d.setFont(new Font("Times New Roman", Font.BOLD, 26));
		g2d.drawString(putniNalog.getVozac(), 1027, 374);
		if (!StringUtils.isEmpty(putniNalog.getPosada())) {
			int index = putniNalog.getPosada().indexOf(',');
			if (index != -1) {
				g2d.drawString(putniNalog.getPosada().substring(0, index), 1172, 415);
				g2d.drawString(putniNalog.getPosada().substring(index+1), 847, 454);
			} else {
				g2d.drawString(putniNalog.getPosada(), 1172, 415);
			}
		} 
		fm = g2d.getFontMetrics(g2d.getFont());
		temp = putniNalog.getRegOznaka();
		width = fm.stringWidth(temp);
		g2d.drawString(temp, 1380 - (width / 2), 702);
		g2d.drawString(putniNalog.getMesto() + ",", 1010, 201);
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtils.toDate(putniNalog.getDatum()));
		temp = String.valueOf(cal.get(Calendar.MONTH) + 1);
		if(temp.length() == 1) temp = "0" + temp;
		temp = String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) + 
				"." + temp + "." + String.valueOf(cal.get(Calendar.YEAR)) + ".";
		g2d.drawString(temp, 1348, 201);
		return 0;
	}

}
