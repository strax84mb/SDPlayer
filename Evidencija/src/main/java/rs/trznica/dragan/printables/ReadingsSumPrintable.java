package rs.trznica.dragan.printables;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import rs.trznica.dragan.forms.support.DecimalFormater;

public class ReadingsSumPrintable implements Printable {

	private String startMonth;
	private String endMonth;
	private String ntKw;
	private String vtKw;
	private String stKw;
	private String ntCena;
	private String vtCena;
	private String stCena;
	private String rktvnKw;
	private String rktvnCena;
	private String pristup;
	private String podsticaj;
	private String aktivnaKw;
	private String aktivnaCena;
	private String ukupno;
	
	private FontMetrics fm;
	private Graphics2D g2d;
	private Stroke dashedStroke;
	private BasicStroke fullStroke;
	
	private int firstTab = 550;
	private int secondTab = 1050;
	private int thirdTab = 1650;
	
	public ReadingsSumPrintable(String startMonth, String endMonth,
			String ntKw, String vtKw, String stKw, String ntCena,
			String vtCena, String stCena, String rktvnKw, String rktvnCena,
			String pristup, String podsticaj) {
		super();
		this.startMonth = startMonth;
		this.endMonth = endMonth;
		this.ntKw = ntKw;
		this.vtKw = vtKw;
		this.stKw = stKw;
		this.ntCena = ntCena;
		this.vtCena = vtCena;
		this.stCena = stCena;
		this.rktvnKw = rktvnKw;
		this.rktvnCena = rktvnCena;
		this.pristup = pristup;
		this.podsticaj = podsticaj;
		
		Long value = DecimalFormater.parseToLongSep(ntKw);
		value += DecimalFormater.parseToLongSep(vtKw);
		value += DecimalFormater.parseToLongSep(stKw);
		aktivnaKw = DecimalFormater.formatFromLongSep(value, 0);
		
		value = DecimalFormater.parseToLongSep(ntCena);
		value += DecimalFormater.parseToLongSep(vtCena);
		value += DecimalFormater.parseToLongSep(stCena);
		aktivnaCena = DecimalFormater.formatFromLongSep(value, 2);
		
		value = DecimalFormater.parseToLongSep(aktivnaCena);
		value += DecimalFormater.parseToLongSep(pristup);
		value += DecimalFormater.parseToLongSep(podsticaj);
		ukupno = DecimalFormater.formatFromLongSep(value, 2);
	}

	@Override
	public int print(Graphics graphics, PageFormat pf, int pageIndex)
			throws PrinterException {
		if (pageIndex != 0) {
			return NO_SUCH_PAGE;
		}
		g2d = (Graphics2D) graphics;
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		double pageWidth = pf.getImageableWidth();
		double pageHeight = pf.getImageableHeight();
		double imageWidth = 2016;
		double imageHeight = 3085;
		double scaleX = pageWidth / imageWidth;
		double scaleY = pageHeight / imageHeight;
		double scaleFactor = Math.min(scaleX, scaleY);
		g2d.scale(scaleFactor, scaleFactor);
		Font font = new Font("Times New Roman", Font.PLAIN, 50);
		Font titleFont = new Font("Times New Roman", Font.BOLD, 100);
		g2d.setFont(titleFont);
		g2d.setColor(Color.BLACK);
		fm = g2d.getFontMetrics(g2d.getFont());
		fullStroke = new BasicStroke(1);
		g2d.setStroke(fullStroke);
		g2d.drawRect(5, 75, 2005, 2900);
		String text = String.format("Struja od %s do %s", startMonth, endMonth);
		int width = fm.stringWidth(text);
		g2d.drawString(text, (int) ((imageWidth / 2) - (width / 2)), 350);
		g2d.setFont(font);
		fm = g2d.getFontMetrics(g2d.getFont());
		
		dashedStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);

		writeRightString("kW", secondTab, 650);
		writeRightString("Iznos", thirdTab, 650);
		
		writeRightString("NT", firstTab, 800);
		writeRightString("VT", firstTab, 900);
		writeRightString("ST", firstTab, 1000);
		writeRightString("Reaktivna", firstTab, 1100);
		writeRightString("Ukupna aktivna", firstTab, 1300);
		writeRightString("Pristup", firstTab, 1400);
		writeRightString("Podsticaj", firstTab, 1500);
		
		writeRightString(ntKw, secondTab, 800);
		writeRightString(vtKw, secondTab, 900);
		writeRightString(stKw, secondTab, 1000);
		writeRightString(rktvnKw, secondTab, 1100);
		writeRightString(aktivnaKw, secondTab, 1300);
		
		writeRightString(ntCena, thirdTab, 800);
		writeRightString(vtCena, thirdTab, 900);
		writeRightString(stCena, thirdTab, 1000);
		writeRightString(rktvnCena, thirdTab, 1100);
		writeRightString(aktivnaCena, thirdTab, 1300);
		writeRightString(pristup, thirdTab, 1400);
		writeRightString(podsticaj, thirdTab, 1500);
		
		g2d.setStroke(fullStroke);
		
		text = "Ukupan iznos:";
		width = fm.stringWidth(text);
		writeRightString(text, secondTab, 1700);
		writeRightString(ukupno, thirdTab, 1700);
		g2d.setStroke(dashedStroke);
		g2d.drawLine(secondTab - width, 1730, thirdTab, 1730);
		g2d.setStroke(fullStroke);
		
		text = "Ref. teh. i op. poslova";
		g2d.drawString(text, 1580 - (fm.stringWidth(text) / 2), 2500);
		text = "Dragan Dobrijevi\u0107";
		g2d.drawString(text, 1580 - (fm.stringWidth(text) / 2), 2570);
		g2d.drawLine(1580 - 250, 2750, 1580 + 250, 2750);
		
		return PAGE_EXISTS;
	}

	private void writeRightString(String text, int tabPos, int vertPos) {
		g2d.setStroke(fullStroke);
		g2d.drawString(text, tabPos - fm.stringWidth(text), vertPos);
		if (tabPos == firstTab) {
			g2d.setStroke(dashedStroke);
			g2d.drawLine(tabPos - fm.stringWidth(text), vertPos + 30, thirdTab, vertPos + 30);
		}
	}
}
