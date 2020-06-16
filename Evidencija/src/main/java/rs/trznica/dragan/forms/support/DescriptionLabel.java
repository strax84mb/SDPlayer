package rs.trznica.dragan.forms.support;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import rs.trznica.dragan.entities.putninalog.PutniNalog;
import rs.trznica.dragan.entities.putninalog.PutniNalogSql;

public class DescriptionLabel extends JLabel {

	private static final long serialVersionUID = -607194690870304141L;

	public DescriptionLabel() {
		super();
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(false);
	}
	
	public void showText(String text) {
		setText("<html>" + text.replaceAll("\n", "<br/>") + "</html>");
		setVisible(true);
	}
	
	public void hideText() {
		setVisible(false);
	}
	
	public void showText(PutniNalogSql nalog) {
		StringBuilder builder = new StringBuilder(4096);
		builder.append("<html><table border=\"0\" cellpadding=\"2\" cellspacing=\"0\">");
		builder.append("<tr><td>").append("<b>ID  naloga:</b>").append("</td><td>").append(nalog.getId()).append("</td></tr>");
		builder.append("<tr><td>").append("<b>Redni broj:</b>").append("</td><td>").append(nalog.getRedniBroj()).append("</td></tr>");
		builder.append("<tr><td>").append("<b>Reg oznaka:</b>").append("</td><td>").append(nalog.getRegOznaka()).append("</td></tr>");
		builder.append("<tr><td>").append("<b>Marka/Tip:</b>").append("</td><td>")
				.append(nalog.getMarkaVozila() + " " + nalog.getTipVozila()).append("</td></tr>");
		builder.append("<tr><td>").append("<b>Vozac:</b>").append("</td><td>").append(nalog.getVozac()).append("</td></tr>");
		if (PutniNalog.PUTNICKI.equals(nalog.getNamenaVozila())) {
			builder.append("<tr><td>").append("<b>Korisnik:</b>").append("</td><td>").append(nalog.getKorisnik()).append("</td></tr>");
			builder.append("<tr><td>").append("<b>Snaga motora:</b>").append("</td><td>").append(nalog.getSnagaMotora()).append("</td></tr>");
			builder.append("<tr><td>").append("<b>Broj sedi\u0161ta:</b>").append("</td><td>")
					.append(nalog.getBrojSedista()).append("</td></tr>");
			
		}
		if (PutniNalog.TERETNI.equals(nalog.getNamenaVozila())) {
			builder.append("<tr><td>").append("<b>Posada:</b>").append("</td><td>").append(nalog.getPosada()).append("</td></tr>");
			builder.append("<tr><td>").append("<b>Te\u017Eina:</b>").append("</td><td>")
					.append(DecimalFormater.formatFromLong(nalog.getTezina().longValue(), 3)).append("</td></tr>");
			builder.append("<tr><td>").append("<b>Nosivost:</b>").append("</td><td>")
					.append(DecimalFormater.formatFromLong(nalog.getNosivost().longValue(), 3)).append("</td></tr>");
			
		}
		builder.append("<tr><td>").append("<b>Relacija:</b>").append("</td><td>").append(nalog.getRelacija()).append("</td></tr>");
		builder.append("<tr><td>").append("<b>Datum:</b>").append("</td><td>")
				.append(DateUtils.getReadableDate(nalog.getDatum())).append("</td></tr>");
		builder.append("<tr><td>").append("<b>Vrsta prevoza:</b>").append("</td><td>").append(nalog.getVrstaPrevoza()).append("</td></tr>");
		builder.append("<tr><td>").append("<b>Radna org.:</b>").append("</td><td>").append(nalog.getRadnaOrganizacija()).append("</td></tr>");
		builder.append("<tr><td>").append("<b>Adresa gara\u017Ee:</b>").append("</td><td>")
				.append(nalog.getAdresaGaraze()).append("</td></tr>");
		builder.append("<tr><td>").append("<b>Mesto:</b>").append("</td><td>").append(nalog.getMesto()).append("</td></tr>");
		builder.append("</html>");
		setText(builder.toString());
		setVisible(true);
	}
}
