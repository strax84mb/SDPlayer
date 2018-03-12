package rs.trznica.dragan.forms;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import rs.trznica.dragan.forms.actions.ExportCountersActionListener;
import rs.trznica.dragan.forms.actions.FormActionFactory;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ApplicationFrame extends JFrame {

	private static final long serialVersionUID = 2278856270790456663L;

	private Font defaultFont = new Font("Times New Roman", Font.PLAIN, 16);

	private JPanel contentPane;
	private JMenuBar menuBar;

	private ApplicationContext ctx;
	private JDesktopPane desktopPane;
	private FormActionFactory formActionFactory;

	/**
	 * Create the frame.
	 */
	@Autowired
	public ApplicationFrame(ApplicationContext ctx) {
		this.ctx = ctx;
		this.formActionFactory = ctx.getBean(FormActionFactory.class);
		this.desktopPane = new JDesktopPane();
		formActionFactory.setDesktopPane(desktopPane);
		
		setFont(defaultFont);
		setTitle("Evidencija");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 850, 650);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		menuBar = new JMenuBar();
		menuBar.setFont(defaultFont);
		setJMenuBar(menuBar);
		
		JMenu mnConsumers = addMenu("Potro\u0161a\u010Di");
		formActionFactory.listFrameItem(mnConsumers, VoziloListaForm.class, "Lista potro\u0161a\u010Da", 
				KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK);
		formActionFactory.newDialogItem(mnConsumers, VoziloForm.class, "Dodaj potro\u0161a\u010Da", KeyEvent.VK_P, KeyEvent.ALT_DOWN_MASK);
		
		JMenu mnFillups = addMenu("Tankovanja");
		formActionFactory.newDialogItem(mnFillups, TankovanjeDialog.class, "Unesi tankovanje", KeyEvent.VK_T, KeyEvent.ALT_DOWN_MASK);
		formActionFactory.listFrameItem(mnFillups, TankovanjeListaForm.class, "Lista tankovanja", KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK);
		
		JMenu mnTravels = addMenu("Putni nalozi");
		formActionFactory.newDialogItem(mnTravels, JDialog.class, "Unos putnog naloga", KeyEvent.VK_N, KeyEvent.ALT_DOWN_MASK);
		formActionFactory.listFrameItem(mnTravels, JInternalFrame.class, "Lista putnih naloga", KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK);

		JMenu mnElectricity = addMenu("Struja");
		formActionFactory.newDialogItem(mnElectricity, BrojiloForm.class, "Unos mernog mesta", KeyEvent.VK_M, KeyEvent.ALT_DOWN_MASK);
		formActionFactory.listFrameItem(mnElectricity, ListaBrojilaForm.class, "Lista mernih mesta", 
				KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK);
		formActionFactory.newDialogItem(mnElectricity, OcitavanjeForm.class, "Unos o\u010Ditavanja", KeyEvent.VK_O, KeyEvent.ALT_DOWN_MASK);
		formActionFactory.listFrameItem(mnElectricity, ListaOcitavanjaForm.class, "Lista o\u010Ditavanja", 
				KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK);
		mnElectricity.addSeparator();
		addMenuItem(mnElectricity, new ExportCountersActionListener(ctx), null, null);
		
		java.awt.Component horizontalGlue = Box.createHorizontalGlue();
		menuBar.add(horizontalGlue);
		
		JMenu mnSystem = addMenu("Komande");
		formActionFactory.closeAction(mnSystem, getThisFrame());

		contentPane.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane);
		
		scrollPane.setViewportView(desktopPane);
	}

	private JMenu addMenu(String text) {
		JMenu menu = new JMenu(text);
		menu.setFont(defaultFont);
		menuBar.add(menu);
		return menu;
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
	
	private JFrame getThisFrame() {
		return this;
	}

	public JDesktopPane getDesktopPane() {
		return desktopPane;
	}
}
