package rs.trznica.dragan.forms;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.eclipse.wb.swing.FocusTraversalOnArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import rs.trznica.dragan.dao.lucene.BrojiloDao;
import rs.trznica.dragan.entities.struja.Brojilo;
import rs.trznica.dragan.entities.struja.VrstaBrojila;
import rs.trznica.dragan.forms.support.ModalResult;
import rs.trznica.dragan.validator.exceptions.ChangeNotAcceptedException;
import rs.trznica.dragan.validator.struja.BrojiloValidator;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BrojiloForm extends GenericDialog<Brojilo> {

	private static final long serialVersionUID = -5269728175774257655L;

	private ApplicationContext ctx;
	private BrojiloDao brojiloDao;
	
	private Font defaultFont = new Font("Times New Roman", Font.PLAIN, 18);
	private JTextField tfBroj;
	private JTextField tfED;
	private JTextField tfOpis;
	private JCheckBox chckbxUFunkciji;
	private JComboBox<VrstaBrojila> cbVrstaBrojila;
	
	private Long entityId = null;

	private ModalResult modalResult = ModalResult.CANCEL;
	private Brojilo returnValue = null;

	@Autowired
	public BrojiloForm(ApplicationContext ctx) {
		this.ctx = ctx;
		brojiloDao = this.ctx.getBean(BrojiloDao.class);
		setModal(true);
		setTitle("Brojilo");
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 309);
		
		JPanel panelTop = new JPanel();
		getContentPane().add(panelTop, BorderLayout.NORTH);
		
		JLabel lblTitle = new JLabel("Potro\u0161a\u010D");
		lblTitle.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		panelTop.add(lblTitle);
		
		JPanel panelBottom = new JPanel();
		panelBottom.setBorder(new EmptyBorder(0, 0, 5, 0));
		getContentPane().add(panelBottom, BorderLayout.SOUTH);
		panelBottom.setLayout(new BoxLayout(panelBottom, BoxLayout.X_AXIS));
		
		panelBottom.add(Box.createHorizontalGlue());
		JButton btnOk = makeButton("Snimi", new BtnOkActionListener());
		panelBottom.add(btnOk);
		panelBottom.add(Box.createHorizontalStrut(20));
		JButton btnCancel = makeButton("Otka\u017Ei", new BtnCancelActionListener());
		panelBottom.add(btnCancel);
		panelBottom.add(Box.createHorizontalGlue());
		
		JPanel panelCenter = new JPanel();
		panelCenter.setBorder(new EmptyBorder(0, 10, 0, 10));
		getContentPane().add(panelCenter, BorderLayout.CENTER);
		GridBagLayout gbl_panelCenter = new GridBagLayout();
		gbl_panelCenter.columnWidths = new int[] {173, 251};
		gbl_panelCenter.rowHeights = new int[] {0, 0, 0, 0, 0};
		gbl_panelCenter.columnWeights = new double[]{0.0, 1.0};
		gbl_panelCenter.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		panelCenter.setLayout(gbl_panelCenter);
		
		tfBroj = makeTextField(panelCenter, 0, "Broj", 15);
		tfED = makeTextField(panelCenter, 1, "ED broj", 15);
		tfOpis = makeTextField(panelCenter, 2, "Opis", null);
		chckbxUFunkciji = makeCheckBox(panelCenter, 3, "U funkciji");
		chckbxUFunkciji.setSelected(true);
		
		cbVrstaBrojila = new JComboBox<VrstaBrojila>();
		for (VrstaBrojila vrsta : VrstaBrojila.values()) {
			cbVrstaBrojila.addItem(vrsta);
		}
		cbVrstaBrojila.setSelectedIndex(-1);
		addComponent(panelCenter, 4, "Vrsta brojila", cbVrstaBrojila);
		
		setFocusTraversalPolicy(new FocusTraversalOnArray(new java.awt.Component[] {tfBroj, tfED, tfOpis, 
				chckbxUFunkciji, cbVrstaBrojila, btnOk, btnCancel}));
	}
	
	@Override
	public void editObject(Brojilo object) {
		entityId = object.getId();
		tfBroj.setText(object.getBroj());
		tfED.setText(object.getEd());
		tfOpis.setText(object.getOpis());
		chckbxUFunkciji.setSelected(object.getuFunkciji());
		for (int i = 0; i < cbVrstaBrojila.getItemCount(); i++) {
			if (cbVrstaBrojila.getItemAt(i).equals(object.getVrstaBrojila())) {
				cbVrstaBrojila.setSelectedIndex(i);
				break;
			}
		}
	}

	public ModalResult getModalResult() {
		return modalResult;
	}
	
	public Brojilo getReturnValue() {
		return returnValue;
	}
	
	private JButton makeButton(String title, ActionListener listener) {
		JButton button = new JButton(title);
		button.addActionListener(listener);
		button.setFont(defaultFont);
		return button;
	}
	
	private JTextField makeTextField(JPanel panel, int row, String label, Integer columns) {
		JTextField textField = new JTextField();
		if (columns != null) {
			textField.setColumns(columns);
		}
		addComponent(panel, row, label, textField);
		return textField;
	}
	
	private JCheckBox makeCheckBox(JPanel panel, int row, String label) {
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
	
	private void addComponent(JPanel panel, int row, String label, java.awt.Component component) {
		JLabel jLabel = new JLabel(label);
		jLabel.setFont(defaultFont);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(0, 0, 5, 5);
		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 0;
		constraints.gridy = row;
		panel.add(jLabel, constraints);

		component.setFont(defaultFont);
		
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(0, 0, 5, 0);
		constraints.gridx = 1;
		constraints.gridy = row;
		panel.add(component, constraints);
	}
	
	private class BtnOkActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Brojilo brojilo = new Brojilo();
			brojilo.setBroj(tfBroj.getText());
			brojilo.setEd(tfED.getText());
			brojilo.setOpis(tfOpis.getText());
			brojilo.setuFunkciji(chckbxUFunkciji.isSelected());
			brojilo.setVrstaBrojila((VrstaBrojila) cbVrstaBrojila.getSelectedItem());
			
			BindingResult result = new DataBinder(brojilo).getBindingResult();
			new BrojiloValidator().validate(brojilo, result);
			if (result.getErrorCount() > 0) {
				ErrorDialog dialog = new ErrorDialog();
				dialog.showErrors(result);
			} else {
				try {
					if (entityId == null) {
						brojilo = brojiloDao.save(brojilo);
						System.out.println(brojilo.getId());
					} else {
						YesNoDialog dlg = new YesNoDialog("Sigurno želite snimiti unešene izmene?");
						dlg.showDialogInCenter(getThisForm());
						if (!ModalResult.YES.equals(dlg.getModalResult())) {
							throw new ChangeNotAcceptedException();
						}
						brojilo.setId(entityId);
						brojiloDao.update(brojilo);
					}
					returnValue = brojilo;
					modalResult = ModalResult.OK;
					setVisible(false);
				} catch (ChangeNotAcceptedException e1) {
					returnValue = null;
					modalResult = ModalResult.CANCEL;
				} catch (Exception e1) {
					e1.printStackTrace();
					ErrorDialog dialog = new ErrorDialog();
					dialog.showError("Desila se greška prilikom snimanja.\n" + e1.getMessage());
				}
			}
		}
		
	}
	
	private class BtnCancelActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			modalResult = ModalResult.CANCEL;
			setVisible(false);
		}
	}
}
