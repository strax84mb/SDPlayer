package rs.trznica.dragan.forms;

import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.springframework.context.ApplicationContext;

import rs.trznica.dragan.forms.support.ModalResult;

public abstract class GenericListForm<T> extends JInternalFrame {
	
	private static final long serialVersionUID = -7532616534717869119L;

	private JList<T> objectList;
	private JLabel lblText;
	private JButton btnNew;
	private JButton btnEdit;
	private JButton btnDelete;
	private JButton btnClose;
	
	protected Font defaultFont = new Font("Times New Roman", Font.PLAIN, 16);

	protected abstract void autowireFields(ApplicationContext ctx);
	
	protected abstract void populateList();
	
	private void reloadData() {
		populateList();
		objectList.clearSelection();
		lblText.setText("");
	}
	
	protected abstract StringBuilder objectToHtmlBody(T object);
	
	protected abstract ModalResult performNewAction();
	
	protected abstract ModalResult performEditAction();
	
	protected abstract void performDeleteAction();
	
	protected void showObjectAsText(T object) {
		if (object == null) {
			lblText.setText("");
			btnEdit.setEnabled(false);
			btnDelete.setEnabled(false);
		} else {
			StringBuilder builder = objectToHtmlBody(object);
			builder.insert(0, "<html>");
			builder.append("</html>");
			lblText.setText(builder.toString());
			btnEdit.setEnabled(true);
			btnDelete.setEnabled(true);
		}
	}
	
	public GenericListForm(ApplicationContext ctx, String confirmDeleteText) {
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

		objectList = new JList<T>(new DefaultListModel<T>());
		objectList.addListSelectionListener(new ObjectListSelectionListener());
		objectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(objectList);
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
		lblText.setFont(defaultFont);
		springLayout.putConstraint(SpringLayout.NORTH, lblText, 10, SpringLayout.SOUTH, panelUpper);
		panelUpper.setLayout(new BoxLayout(panelUpper, BoxLayout.X_AXIS));
		
		btnNew = new JButton("Dodaj");
		btnNew.setAction(new NewAction());
		btnNew.setFont(defaultFont);
		panelUpper.add(btnEdit);
		
		java.awt.Component horizontalStrut_3 = Box.createHorizontalStrut(15);
		panelUpper.add(horizontalStrut_3);
		
		btnEdit = new JButton("Izmeni");
		btnEdit.setAction(new EditAction());
		btnEdit.setFont(defaultFont);
		panelUpper.add(btnEdit);
		
		java.awt.Component horizontalGlue_3 = Box.createHorizontalGlue();
		panelUpper.add(horizontalGlue_3);
		
		btnDelete = new JButton("Obri\u0161i");
		btnDelete.setAction(new DeleteAction(confirmDeleteText));
		btnDelete.setFont(defaultFont);
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
		btnClose.setAction(new CloseAction());
		btnClose.setFont(defaultFont);
		panelLower.add(btnClose);
		
		java.awt.Component horizontalGlue_1 = Box.createHorizontalGlue();
		panelLower.add(horizontalGlue_1);

		reloadData();
		showObjectAsText(null);
	}

	public JList<T> getObjectList() {
		return objectList;
	}

	public JLabel getLblText() {
		return lblText;
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

	protected JInternalFrame getThisFrame() {
		return this;
	}
	
	private class ObjectListSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent ev) {
			if (!ev.getValueIsAdjusting()) {
				showObjectAsText(objectList.getSelectedValue());
			}
		}
	}
	private class NewAction extends AbstractAction {
		private static final long serialVersionUID = 177017573439753393L;
		public NewAction() {
			putValue(NAME, "Dodaj");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if (ModalResult.OK.equals(performNewAction())) {
				reloadData();
			}
		}
		
	}
	private class EditAction extends AbstractAction {
		private static final long serialVersionUID = 3948004467989437095L;
		public EditAction() {
			putValue(NAME, "Izmeni");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if (ModalResult.OK.equals(performEditAction())) {
				reloadData();
			}
		}
		
	}
	private class DeleteAction extends AbstractAction {
		private static final long serialVersionUID = -5746012041231886078L;
		private String confirmText;
		public DeleteAction(String confirmText) {
			putValue(NAME, "Obri\u0161i");
			this.confirmText = confirmText;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if (objectList.getSelectedValue() != null) {
				YesNoDialog dialog = new YesNoDialog(confirmText);
				dialog.showDialogInCenter(getThisFrame());
				if (dialog.getModalResult() == ModalResult.YES) {
					performDeleteAction();
				}
			}
		}
	}
	private class CloseAction extends AbstractAction {
		private static final long serialVersionUID = 7695956076052828361L;
		public CloseAction() {
			putValue(NAME, "Zatvori");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			getThisFrame().dispose();
		}
	}
}
