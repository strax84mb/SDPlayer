package rs.trznica.dragan.forms.actions;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

public class AltF4CloseAction extends AbstractAction {
	
	private static final long serialVersionUID = 3665163516712039485L;
	
	private JFrame frame;
	
	public AltF4CloseAction(JFrame frame) {
		this.frame = frame;
		putValue(NAME, "Kraj rada");
		putValue(SHORT_DESCRIPTION, "Zatvori program");
	}

	public void actionPerformed(ActionEvent ev) {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
}
