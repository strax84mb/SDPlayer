package rs.trznica.dragan.forms;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
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
import javax.swing.border.EmptyBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ApplicationFrame extends JFrame {

	private static final long serialVersionUID = 2278856270790456663L;

	private JPanel contentPane;
	private final Action closeAction = new CloseAction();

	@Autowired
	private ApplicationContext ctx;
	private final Action newConsumerAction = new NewConsumerAction();
	private final Action newFillupAction = new NewFillupAction();
	private final Action listConsumersAction = new ListConsumersAction();
	private JDesktopPane desktopPane;
	private final Action listFillUpsAction = new ListFillUpsAction();

	/**
	 * Create the frame.
	 */
	public ApplicationFrame() {
		setFont(new Font("Times New Roman", Font.PLAIN, 16));
		setTitle("Evidencija vozila");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 843, 483);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		setJMenuBar(menuBar);
		
		JMenu mnConsumers = new JMenu("Potro\u0161a\u010Di");
		mnConsumers.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		menuBar.add(mnConsumers);
		
		JMenuItem mntmListConsumers = new JMenuItem("Lista potro\u0161a\u010Da");
		mntmListConsumers.setAction(listConsumersAction);
		mntmListConsumers.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		mnConsumers.add(mntmListConsumers);
		
		JMenuItem mntmNewConsumer = new JMenuItem("Dodaj potro\u0161a\u010Da");
		mntmNewConsumer.setAction(newConsumerAction);
		mntmNewConsumer.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		mnConsumers.add(mntmNewConsumer);
		
		JMenu mnFillups = new JMenu("Tankovanja");
		mnFillups.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		menuBar.add(mnFillups);
		
		JMenuItem mntmNewFillup = new JMenuItem("Unesi tankovanje");
		mntmNewFillup.setAction(newFillupAction);
		mntmNewFillup.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		mnFillups.add(mntmNewFillup);
		
		JMenuItem mntmListFillups = new JMenuItem("Lista tankovanja");
		mntmListFillups.setAction(listFillUpsAction);
		mntmListFillups.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		mnFillups.add(mntmListFillups);
		
		java.awt.Component horizontalGlue = Box.createHorizontalGlue();
		menuBar.add(horizontalGlue);
		
		JMenu mnSystem = new JMenu("Komande");
		mnSystem.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		menuBar.add(mnSystem);
		
		JMenuItem mntmClose = new JMenuItem("Kraj rada");
		mntmClose.setAction(closeAction);
		mntmClose.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		mnSystem.add(mntmClose);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane);
		
		desktopPane = new JDesktopPane();
		scrollPane.setViewportView(desktopPane);
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
		public void actionPerformed(ActionEvent e) {
			getThisFrame().dispatchEvent(new WindowEvent(getThisFrame(), WindowEvent.WINDOW_CLOSING));
		}
	}
	private class NewConsumerAction extends AbstractAction {
		private static final long serialVersionUID = -5308483703384459993L;
		public NewConsumerAction() {
			putValue(Action.NAME, "Dodaj potro\u0161a\u010Da");
		}
		public void actionPerformed(ActionEvent e) {
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
		public void actionPerformed(ActionEvent e) {
			VoziloListaForm form = ctx.getBean(VoziloListaForm.class);
			form.setVisible(true);
			desktopPane.add(form);
			try {
				form.setMaximum(true);
			} catch (PropertyVetoException e1) {
				e1.printStackTrace();
			}
		}
	}
	private class NewFillupAction extends AbstractAction {
		private static final long serialVersionUID = 5087940145405045218L;
		public NewFillupAction() {
			putValue(NAME, "Unesi tankovanje");
		}
		public void actionPerformed(ActionEvent e) {
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
		public void actionPerformed(ActionEvent e) {
			TankovanjeListaForm form = ctx.getBean(TankovanjeListaForm.class);
			form.setVisible(true);
			desktopPane.add(form);
			try {
				form.setMaximum(true);
			} catch (PropertyVetoException e1) {
				e1.printStackTrace();
			}
		}
	}
}
