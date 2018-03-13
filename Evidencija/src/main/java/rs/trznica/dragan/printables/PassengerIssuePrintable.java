package rs.trznica.dragan.printables;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import javax.imageio.ImageIO;

import rs.trznica.dragan.entities.putninalog.PutniNalog;
import rs.trznica.dragan.forms.support.DateUtils;

public class PassengerIssuePrintable implements Printable {

	private PutniNalog putniNalog;
	private BufferedImage img;
	
	public PassengerIssuePrintable(PutniNalog putniNalog) throws IOException {
		this.putniNalog = putniNalog;
		this.img = ImageIO.read(new File("Putnicki1.bmp"));
	}
	
	@Override
	public int print(Graphics g, PageFormat pf, int pageIndex)
			throws PrinterException {
		if(pageIndex >= 2) return NO_SUCH_PAGE;
		Graphics2D g2d = (Graphics2D)g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		double pageWidth = pf.getImageableWidth();
		double pageHeight = pf.getImageableHeight();
		double imageWidth = 1061;
		double imageHeight = 1538;
		double scaleX = pageWidth / imageWidth;
		double scaleY = pageHeight / imageHeight;
		double scaleFactor = Math.min(scaleX, scaleY);
		g2d.scale(scaleFactor, scaleFactor);
		g2d.drawImage(img, 1, 1, null);
		g2d.setFont(new Font("Times New Roman", Font.PLAIN, 24));
		g2d.setColor(Color.BLACK);
		FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
		g2d.drawString(putniNalog.getMarkaVozila() + " " + putniNalog.getTipVozila(), 180, 440);
		g2d.drawString(putniNalog.getSnagaMotora().toString(), 195, 480);
		g2d.drawString(putniNalog.getBrojSedista().toString(), 535, 480);
		g2d.drawString(putniNalog.getRegOznaka(), 135, 523);
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtils.toDate(putniNalog.getDatum()));
		String temp = String.valueOf(cal.get(Calendar.MONTH) + 1);
		if(temp.length() == 1) temp = "0" + temp;
		temp = String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) + "." + temp + ".";
		int width = fm.stringWidth(temp);
		g2d.drawString(temp, 200, 592);
		temp = String.valueOf(cal.get(Calendar.YEAR)) + ".";
		width = fm.stringWidth(temp);
		g2d.drawString(temp, 335 - (width / 2), 592);
		g2d.setFont(new Font("Times New Roman", Font.PLAIN, 30));
		fm = g2d.getFontMetrics(g2d.getFont());
		temp = putniNalog.getVrstaPrevoza();
		width = fm.stringWidth(temp);
		g2d.drawString(temp, 858 - (width / 2), 200);
		g2d.setFont(new Font("Times New Roman", Font.PLAIN, 45));
		g2d.drawString(putniNalog.getRedniBroj().toString(), 810, 60);
		g2d.setFont(new Font("Times New Roman", Font.BOLD, 30));
		g2d.drawString(putniNalog.getRegOznaka(), 185, 63);
		g2d.setFont(new Font("Times New Roman", Font.BOLD, 24));
		g2d.drawString(putniNalog.getMesto(), 185, 110);
		g2d.drawString(putniNalog.getAdresaGaraze(), 185, 197);
		g2d.drawString(putniNalog.getVozac(), 105, 270);
		g2d.drawString(putniNalog.getKorisnik(), 105, 313);
		// Relacija
		fm = g2d.getFontMetrics(g2d.getFont());
		temp = putniNalog.getRelacija();
		width = fm.stringWidth(temp);
		if(width > 520){
			int ind = temp.length();
			String line1 = temp;
			while((width > 520) && (ind > 0)){
				ind = temp.lastIndexOf(" ", ind - 1);
				line1 = temp.substring(0, ind);
				width = fm.stringWidth(line1);
			}
			if(ind > 0){
				g2d.drawString(line1, 100, 354);
				g2d.drawString(temp.substring(ind + 1), 28, 398);
			}else{
				g2d.drawString(temp, 28, 398);
			}
		}else{
			g2d.drawString(temp, 100, 354);
		}
		return PAGE_EXISTS;
	}

}
