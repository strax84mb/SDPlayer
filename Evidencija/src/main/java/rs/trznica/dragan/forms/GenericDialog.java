package rs.trznica.dragan.forms;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import rs.trznica.dragan.forms.support.ModalResult;

public abstract class GenericDialog<T> extends JDialog {

	private static final long serialVersionUID = -2517857100125835510L;

	protected final Font defaultFont = new Font("Times New Roman", Font.PLAIN, 18);
	
	protected static final String ENTER_PRESSED_KEY = "EnterPressed";
	protected static final String ESC_PRESSED_KEY = "EscapePressed";
	
	//private Long entityId = null;

	protected Action traversalByEnterAction = new TraversalByEnterAction();
	protected Action traversalByEscapeAction = new TraversalByEscapeAction();
	
	//private JButton btnSave;
	//private JButton btnCancel;

	protected ModalResult modalResult = ModalResult.CANCEL;

	public ModalResult getModalResult() {
		return modalResult;
	}
	
	private T returnValue = null;
	
	public T getReturnValue() {
		return returnValue;
	}
	
	public void setReturnValue(T returnValue) {
		this.returnValue = returnValue;
	}

	public void setNullReturnValue() {
		this.returnValue = null;
	}

	public abstract void editObject(T object);
	
	protected JDialog getThisForm() {
		return this;
	}
	
	protected void traverseByEnter(JComponent components[]) {
		for (JComponent comp : components) {
			traverseByEnter(comp);
		}
	}
	
	protected void traverseByEnter(JComponent from) {
		from.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), ENTER_PRESSED_KEY);
		from.getActionMap().put(ENTER_PRESSED_KEY, traversalByEnterAction);
	}
	
	protected void traverseBackByEscape(JComponent from) {
		from.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), ESC_PRESSED_KEY);
		from.getActionMap().put(ESC_PRESSED_KEY, traversalByEscapeAction);
	}
	
	protected void traverseBackByEscape(JComponent components[]) {
		for (JComponent comp : components) {
			traverseBackByEscape(comp);
		}
	}
	
	protected JPanel makeCenterPanel(int firstRowWidth, int secondRowWidth) {
		JPanel panelCenter = new JPanel();
		panelCenter.setBorder(new EmptyBorder(0, 10, 0, 10));
		getContentPane().add(panelCenter, BorderLayout.CENTER);
		GridBagLayout gbl_panelCenter = new GridBagLayout();
		gbl_panelCenter.columnWidths = new int[] {firstRowWidth, secondRowWidth};
		gbl_panelCenter.rowHeights = new int[] {0, 0, 0, 0, 0};
		gbl_panelCenter.columnWeights = new double[]{0.0, 1.0};
		gbl_panelCenter.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		panelCenter.setLayout(gbl_panelCenter);
		return panelCenter;
	}
	/*
	protected JPanel makeButtonPanel(String saveLabel, JButton buttons[], String cancelLabel) {
		JPanel panelBottom = new JPanel();
		panelBottom.setBorder(new EmptyBorder(0, 0, 5, 0));
		panelBottom.setLayout(new BoxLayout(panelBottom, BoxLayout.X_AXIS));
		
		panelBottom.add(Box.createHorizontalGlue());
		JButton btnOk = makeButton("Snimi", new BtnOkActionListener());
		panelBottom.add(btnOk);
		if (buttons != null && buttons.length > 0) {
			
			
			
			
			
		}
		
		
		
		panelBottom.add(Box.createHorizontalStrut(20));
		JButton btnCancel = makeButton("Otka\u017Ei", new BtnCancelActionListener());
		panelBottom.add(btnCancel);
		panelBottom.add(Box.createHorizontalGlue());
		
		
		
		
		
		
		
	}
	*/
	protected JCheckBox makeCheckBox(JPanel panel, int row, String label) {
		JCheckBox checkBox = new JCheckBox(label);
		checkBox.setFont(defaultFont);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(0, 0, 5, 0);
		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 1;
		constraints.gridy = row;
		panel.add(checkBox, constraints);
		return checkBox;
	}
	
	protected JButton makeButton(String title, ActionListener listener) {
		JButton button = new JButton(title);
		button.addActionListener(listener);
		button.setFont(defaultFont);
		return button;
	}
	
	protected JTextField makeTextField(JPanel panel, int row, JLabel label, Integer columns) {
		JTextField textField = new JTextField();
		if (columns != null) {
			textField.setColumns(columns);
		}
		addComponent(panel, row, label, textField, columns == null);
		return textField;
	}
	
	protected void addComponent(JPanel panel, int row, JLabel label, java.awt.Component component, boolean stretchComponent) {
		label.setFont(defaultFont);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(0, 0, 5, 5);
		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 0;
		constraints.gridy = row;
		panel.add(label, constraints);

		component.setFont(defaultFont);
		
		constraints = new GridBagConstraints();
		if (stretchComponent) {
			constraints.fill = GridBagConstraints.HORIZONTAL;
		} else {
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.WEST;
		}
		constraints.insets = new Insets(0, 0, 5, 0);
		constraints.gridx = 1;
		constraints.gridy = row;
		panel.add(component, constraints);
	}
	
	protected class TraversalByEnterAction extends AbstractAction {

		private static final long serialVersionUID = 6015706579962226369L;

		@Override
		public void actionPerformed(ActionEvent e) {
			getFocusTraversalPolicy().getComponentAfter(getThisForm(), (Component) e.getSource()).requestFocus();
		}
	} 

	protected class TraversalByEscapeAction extends AbstractAction {

		private static final long serialVersionUID = 5544340206950110088L;

		@Override
		public void actionPerformed(ActionEvent e) {
			getFocusTraversalPolicy().getComponentAfter(getThisForm(), (Component) e.getSource()).requestFocus();
		}
	} 
}

