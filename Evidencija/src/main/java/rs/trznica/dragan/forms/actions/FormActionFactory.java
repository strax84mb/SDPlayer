package rs.trznica.dragan.forms.actions;

import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class FormActionFactory {

	private Font defaultFont = new Font("Times New Roman", Font.PLAIN, 16);
	
	private JDesktopPane desktopPane;

	@Autowired
	private ApplicationContext ctx;

	public void setDesktopPane(JDesktopPane desktopPane) {
		this.desktopPane = desktopPane;
	}

	private JMenuItem addMenuItem(JMenu menu, Action action, Integer key, Integer mask) {
		JMenuItem item = new JMenuItem();
		item.setAction(action);
		item.setFont(defaultFont);
		if ((key != null) && (mask != null)) {
			item.setAccelerator(KeyStroke.getKeyStroke(key, mask));
		}
		menu.add(item);
		return item;
	}
	
	public JMenuItem closeAction(JMenu menu, JFrame frame) {
		return addMenuItem(menu, new AltF4CloseAction(frame), KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK);
	}
	
	public JMenuItem newDialogItem(JMenu menu, Class<? extends JDialog> clazz, String name, Integer key, Integer mask) {
		Action action = new NewDialogAction(ctx, clazz, name);
		return addMenuItem(menu, action, key, mask);
	}
	
	public JMenuItem listFrameItem(JMenu menu, Class<? extends JInternalFrame> clazz, String name, Integer key, Integer mask) {
		Action action = new ListFrameAction(clazz, ctx, desktopPane, name);
		return addMenuItem(menu, action, key, mask);
	}
}
