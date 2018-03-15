package rs.trznica.dragan.forms.support;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

public class DescriptionLabel extends JLabel {

	private static final long serialVersionUID = -607194690870304141L;

	public DescriptionLabel() {
		super();
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(false);
	}
	
	public void show(String text) {
		setText("<html>" + text.replaceAll("\n", "<br/>") + "</html>");
		setVisible(true);
	}
	
	public void hide() {
		setVisible(false);
	}
}
