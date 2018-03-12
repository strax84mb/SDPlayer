package rs.trznica.dragan.forms.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import org.springframework.context.ApplicationContext;

public class ListFrameAction extends AbstractAction {
	
	private static final long serialVersionUID = -2655958603193420830L;

	private Class<? extends JInternalFrame> clazz;
	private ApplicationContext ctx;
	private JDesktopPane desktopPane;

	public ListFrameAction(Class<? extends JInternalFrame> clazz,
			ApplicationContext ctx, JDesktopPane desktopPane, String name) {
		this.clazz = clazz;
		this.ctx = ctx;
		this.desktopPane = desktopPane;
		putValue(Action.NAME, name);
	}

	@Override
	public void actionPerformed(ActionEvent aEvent) {
		JInternalFrame frame = (JInternalFrame) ctx.getBean(clazz);
		frame.setVisible(true);
		desktopPane.add(frame);
		try {
			frame.setMaximum(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}

}
