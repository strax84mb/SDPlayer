package rs.trznica.dragan.forms.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;

import org.springframework.context.ApplicationContext;

public class NewDialogAction extends AbstractAction {

	private static final long serialVersionUID = -789796312351640098L;

	private Class<? extends JDialog> clazz;
	private ApplicationContext ctx;
	
	public NewDialogAction(ApplicationContext ctx, Class<? extends JDialog> clazz, String name) {
		this.ctx = ctx;
		this.clazz = clazz;
		putValue(Action.NAME, name);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JDialog dialog = (JDialog) ctx.getBean(clazz);
		dialog.setVisible(true);
		dialog.dispose();
	}
}
