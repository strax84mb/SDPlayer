package rs.trznica.dragan.forms;

import javax.swing.JDialog;

import rs.trznica.dragan.forms.support.ModalResult;

public class GenericDialog extends JDialog {

	private static final long serialVersionUID = -2517857100125835510L;

	protected ModalResult modalResult = ModalResult.CANCEL;

	public ModalResult getModalResult() {
		return modalResult;
	}
}

