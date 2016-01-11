package rs.trznica.dragan.forms;

import javax.swing.JDialog;

import rs.trznica.dragan.forms.support.ModalResult;

public abstract class GenericDialog<T> extends JDialog {

	private static final long serialVersionUID = -2517857100125835510L;

	protected ModalResult modalResult = ModalResult.CANCEL;

	public ModalResult getModalResult() {
		return modalResult;
	}
	
	private T returnValue = null;
	
	public T getReturnValue() {
		return returnValue;
	}
	
	public abstract void editObject(T object);
	
	protected JDialog getThisForm() {
		return this;
	}
}

