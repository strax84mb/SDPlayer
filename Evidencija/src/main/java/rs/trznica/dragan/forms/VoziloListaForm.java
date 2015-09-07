package rs.trznica.dragan.forms;

import java.awt.Font;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SpringLayout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import rs.trznica.dragan.dao.PotrosacDao;
import rs.trznica.dragan.entities.tankovanje.Potrosac;
import rs.trznica.dragan.forms.support.ModalResult;

import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class VoziloListaForm extends JInternalFrame {

	private static final long serialVersionUID = -5835779850596456492L;

	private JList<Potrosac> potrosaci;
	private JLabel lblText;
	private JButton btnEdit;
	private JButton btnDelete;
	private JButton btnClose;

	private PotrosacDao potrosacDao;
	private ApplicationContext ctx;

	private final Action closeAction = new CloseAction();
	private final Action deleteAction = new DeleteAction();
	private final Action editAction = new EditAction();

	private void autowireFields(ApplicationContext ctx) {
		this.ctx = ctx;
		potrosacDao = ctx.getBean(PotrosacDao.class);
	}

	/**
	 * Create the frame.
	 */
	@Autowired
	public VoziloListaForm(ApplicationContext ctx) {
		setMaximizable(true);
		autowireFields(ctx);
		
		setClosable(true);
		setIconifiable(true);
		setBounds(100, 100, 765, 458);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		JSeparator separator = new JSeparator();
		springLayout.putConstraint(SpringLayout.NORTH, separator, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, separator, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, separator, 0, SpringLayout.HORIZONTAL_CENTER, getContentPane());
		getContentPane().add(separator);

		potrosaci = new JList<Potrosac>(new DefaultListModel<Potrosac>());
		potrosaci.addListSelectionListener(new PotrosaciListSelectionListener());
		potrosaci.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(potrosaci);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -5, SpringLayout.WEST, separator);
		getContentPane().add(scrollPane);
		
		
		JPanel panelUpper = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, panelUpper, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, panelUpper, 5, SpringLayout.EAST, separator);
		springLayout.putConstraint(SpringLayout.SOUTH, panelUpper, 45, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, panelUpper, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(panelUpper);
		
		lblText = new JLabel("");
		lblText.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		springLayout.putConstraint(SpringLayout.NORTH, lblText, 10, SpringLayout.SOUTH, panelUpper);
		panelUpper.setLayout(new BoxLayout(panelUpper, BoxLayout.X_AXIS));
		
		btnEdit = new JButton("Izmeni");
		btnEdit.setAction(editAction);
		btnEdit.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		panelUpper.add(btnEdit);
		
		java.awt.Component horizontalGlue_3 = Box.createHorizontalGlue();
		panelUpper.add(horizontalGlue_3);
		
		btnDelete = new JButton("Obri\u0161i");
		btnDelete.setAction(deleteAction);
		btnDelete.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		panelUpper.add(btnDelete);
		springLayout.putConstraint(SpringLayout.WEST, lblText, 5, SpringLayout.EAST, separator);
		springLayout.putConstraint(SpringLayout.EAST, lblText, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(lblText);
		
		JPanel panelLower = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, panelLower, -45, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, lblText, -10, SpringLayout.NORTH, panelLower);
		springLayout.putConstraint(SpringLayout.WEST, panelLower, 5, SpringLayout.EAST, separator);
		springLayout.putConstraint(SpringLayout.SOUTH, panelLower, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, panelLower, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(panelLower);
		
		java.awt.Component horizontalGlue = Box.createHorizontalGlue();
		panelLower.add(horizontalGlue);
		
		btnClose = new JButton("Zatvori");
		btnClose.setAction(closeAction);
		btnClose.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		panelLower.add(btnClose);
		
		java.awt.Component horizontalGlue_1 = Box.createHorizontalGlue();
		panelLower.add(horizontalGlue_1);
		
		listajVozila();
	}

	public JButton getBtnEdit() {
		return btnEdit;
	}

	public JButton getBtnDelete() {
		return btnDelete;
	}

	public JButton getBtnClose() {
		return btnClose;
	}

	private JInternalFrame getThisFrame() {
		return this;
	}

	private void showConsumerText(Potrosac potrosac) {
		if (potrosac == null) {
			lblText.setText("");
			return;
		}
		StringBuilder builder = new StringBuilder("<html><p>");
		if (potrosac.getVozilo()) {
			if (potrosac.getTeretnjak()) {
				builder.append("Teretno vozilo<br/><br/>");
			} else {
				builder.append("Putničko vozilo<br/><br/>");
			}
			builder.append("Reg. oznaka: ").append(potrosac.getRegOznaka()).append("<br/>");
			builder.append("Marka: ").append(potrosac.getMarka()).append("<br/>");
		}
		builder.append(potrosac.getTip()).append("<br/>");
		builder.append("Gorivo: ").append(potrosac.getGorivo().getLabel());
		builder.append("</p></html>");
		lblText.setText(builder.toString());
	}

	private void listajVozila() {
		DefaultListModel<Potrosac> model = (DefaultListModel<Potrosac>)(potrosaci.getModel());
		Iterator<Potrosac> podaci = potrosacDao.findAll().iterator();
		model.setSize(0);
		while (podaci.hasNext()) {
			model.addElement(podaci.next());
		}
	}
	private class CloseAction extends AbstractAction {
		private static final long serialVersionUID = -8999062640049689323L;
		public CloseAction() {
			putValue(NAME, "Zatvori");
		}
		public void actionPerformed(ActionEvent e) {
			getThisFrame().dispose();
		}
	}
	private class PotrosaciListSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent ev) {
			if (!ev.getValueIsAdjusting()) {
				showConsumerText(potrosaci.getSelectedValue());
			}
		}
	}
	private class DeleteAction extends AbstractAction {
		private static final long serialVersionUID = 8258985896707083273L;
		public DeleteAction() {
			putValue(NAME, "Obri\u0161i");
		}
		public void actionPerformed(ActionEvent e) {
			if (potrosaci.getSelectedValue() != null) {
				YesNoDialog dialog = new YesNoDialog("Jeste li sigurni da želite obrisati potrošača?");
				dialog.showDialogInCenter(getThisFrame());
				if (dialog.getModalResult() == ModalResult.YES) {
					potrosacDao.delete(potrosaci.getSelectedValue());
					listajVozila();
					showConsumerText(null);
					potrosaci.clearSelection();
				}
			}
		}
	}
	private class EditAction extends AbstractAction {
		private static final long serialVersionUID = -5951179355927152537L;
		public EditAction() {
			putValue(NAME, "Izmeni");
		}
		public void actionPerformed(ActionEvent ev) {
			if (potrosaci.getSelectedValue() != null) {
				VoziloForm form = ctx.getBean(VoziloForm.class);
				form.editConsumer(potrosaci.getSelectedValue());
				form.setVisible(true);
				if (form.getModalResult() == ModalResult.OK) {
					potrosaci.clearSelection();
					listajVozila();
				}
				form.dispose();
			}
		}
	}
}
