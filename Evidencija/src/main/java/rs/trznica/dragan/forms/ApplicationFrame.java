package rs.trznica.dragan.forms;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
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

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ApplicationFrame extends JFrame {

	private static final long serialVersionUID = 2278856270790456663L;

	private Font defaultFont = new Font("Times New Roman", Font.PLAIN, 16);

	private JPanel contentPane;
	private JMenuBar menuBar;

	private ApplicationContext ctx;
	private JDesktopPane desktopPane;

	/**
	 * Create the frame.
	 */
	@Autowired
	public ApplicationFrame(ApplicationContext ctx) {
		this.ctx = ctx;
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
		addMenuItem(mnConsumers, new ListConsumersAction(), KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK);
		addMenuItem(mnConsumers, new NewConsumerAction(), KeyEvent.VK_P, KeyEvent.ALT_DOWN_MASK);
		
		JMenu mnFillups = addMenu("Tankovanja");
		addMenuItem(mnFillups, new NewFillupAction(), KeyEvent.VK_T, KeyEvent.ALT_DOWN_MASK);
		addMenuItem(mnFillups, new ListFillUpsAction(), KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK);
		
		JMenu mnTravels = addMenu("Putni nalozi");
		addMenuItem(mnTravels, new NewTravelAction(), KeyEvent.VK_N, KeyEvent.ALT_DOWN_MASK);
		addMenuItem(mnTravels, new ListTravelsAction(), KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK);

		JMenu mnElectricity = addMenu("Struja");
		addMenuItem(mnElectricity, new NovoBrojiloAction(), KeyEvent.VK_M, KeyEvent.ALT_DOWN_MASK);
		addMenuItem(mnElectricity, new ListajBrojilaAction(), KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK);
		addMenuItem(mnElectricity, new NovoOcitavanjeAction(), KeyEvent.VK_O, KeyEvent.ALT_DOWN_MASK);
		addMenuItem(mnElectricity, new ListaOcitavanjaAction(), KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK);
		mnElectricity.addSeparator();
		addMenuItem(mnElectricity, new ExportCountersActionListener(ctx), null, null);
		
		java.awt.Component horizontalGlue = Box.createHorizontalGlue();
		menuBar.add(horizontalGlue);
		
		JMenu mnSystem = addMenu("Komande");
		addMenuItem(mnSystem, new CloseAction(), KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK);

		contentPane.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane);
		
		desktopPane = new JDesktopPane();
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

	private class CloseAction extends AbstractAction {
		private static final long serialVersionUID = 8645385883669499085L;
		public CloseAction() {
			putValue(NAME, "Kraj rada");
			putValue(SHORT_DESCRIPTION, "Zatvori program");
		}
		public void actionPerformed(ActionEvent ev) {
			getThisFrame().dispatchEvent(new WindowEvent(getThisFrame(), WindowEvent.WINDOW_CLOSING));
		}
	}
	private class NewConsumerAction extends AbstractAction {
		private static final long serialVersionUID = -5308483703384459993L;
		public NewConsumerAction() {
			putValue(Action.NAME, "Dodaj potro\u0161a\u010Da");
		}
		public void actionPerformed(ActionEvent ev) {
			VoziloForm form = ctx.getBean(VoziloForm.class);
			form.setVisible(true);
			form.dispose();
		}
	}
	private class ListConsumersAction extends AbstractAction {
		private static final long serialVersionUID = 5106498141254209996L;
		public ListConsumersAction() {
			putValue(Action.NAME, "Lista potro\u0161a\u010Da");
		}
		public void actionPerformed(ActionEvent ev) {
			VoziloListaForm form = ctx.getBean(VoziloListaForm.class);
			form.setVisible(true);
			desktopPane.add(form);
			try {
				form.setMaximum(true);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
	}
	private class NewFillupAction extends AbstractAction {
		private static final long serialVersionUID = 5087940145405045218L;
		public NewFillupAction() {
			putValue(NAME, "Unesi tankovanje");
		}
		public void actionPerformed(ActionEvent ev) {
			TankovanjeDialog form = ctx.getBean(TankovanjeDialog.class);
			form.setVisible(true);
			form.dispose();
		}
	}
	public JDesktopPane getDesktopPane() {
		return desktopPane;
	}
	private class ListFillUpsAction extends AbstractAction {
		private static final long serialVersionUID = -135510350288315771L;
		public ListFillUpsAction() {
			putValue(NAME, "Lista tankovanja");
		}
		public void actionPerformed(ActionEvent ev) {
			TankovanjeListaForm form = ctx.getBean(TankovanjeListaForm.class);
			form.setVisible(true);
			desktopPane.add(form);
			try {
				form.setMaximum(true);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
	}
	private class NovoBrojiloAction extends AbstractAction {
		private static final long serialVersionUID = -4146707191099755001L;
		public NovoBrojiloAction() {
			putValue(NAME, "Unos mernog mesta");
		}
		public void actionPerformed(ActionEvent ev) {
			BrojiloForm form = ctx.getBean(BrojiloForm.class);
			form.setVisible(true);
			form.dispose();
		}
	}
	private class ListajBrojilaAction extends AbstractAction {
		private static final long serialVersionUID = -7010598094839937826L;
		public ListajBrojilaAction() {
			putValue(NAME, "Lista mernih mesta");
		}
		public void actionPerformed(ActionEvent ev) {
			ListaBrojilaForm form = ctx.getBean(ListaBrojilaForm.class);
			form.setVisible(true);
			desktopPane.add(form);
			try {
				form.setMaximum(true);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
	}
	private class NovoOcitavanjeAction extends AbstractAction {
		private static final long serialVersionUID = 7761291848230410090L;
		public NovoOcitavanjeAction() {
			putValue(NAME, "Unos o\u010Ditavanja");
		}
		public void actionPerformed(ActionEvent ev) {
			OcitavanjeForm form = ctx.getBean(OcitavanjeForm.class);
			form.setVisible(true);
			form.dispose();
		}
	}
	private class ListaOcitavanjaAction extends AbstractAction {
		private static final long serialVersionUID = -4920687952236705574L;
		public ListaOcitavanjaAction() {
			putValue(NAME, "Lista o\u010Ditavanja");
		}
		public void actionPerformed(ActionEvent ev) {
			ListaOcitavanjaForm form = ctx.getBean(ListaOcitavanjaForm.class);
			form.setVisible(true);
			desktopPane.add(form);
			try {
				form.setMaximum(true);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
	}
	private class NewTravelAction extends AbstractAction {
		private static final long serialVersionUID = -2074054015958500807L;
		public NewTravelAction() {
			putValue(NAME, "Unos putnog naloga");
		}
		public void actionPerformed(ActionEvent ev) {
		}
	}
	private class ListTravelsAction extends AbstractAction {
		private static final long serialVersionUID = 1150046457539236240L;
		public ListTravelsAction() {
			putValue(NAME, "Lista putnih naloga");
		}
		public void actionPerformed(ActionEvent ev) {
		}
	}
}
