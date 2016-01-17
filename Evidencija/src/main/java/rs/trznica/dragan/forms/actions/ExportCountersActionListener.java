package rs.trznica.dragan.forms.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import rs.trznica.dragan.forms.ErrorDialog;

public class ExportCountersActionListener extends AbstractAction {

	private static final long serialVersionUID = 3492345869267924986L;

	public ExportCountersActionListener() {
		putValue(Action.NAME, "Izvezi sva merna mesta");
	}
	
	@Override
	public void actionPerformed(ActionEvent ev) {
		try {
			
			
			
			
			
		} catch (Exception e) {
			new ErrorDialog().showError("Desila se greška prilikom izvoza.");
		}
		// TODO Auto-generated method stub
		
	}

}
