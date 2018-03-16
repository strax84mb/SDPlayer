package rs.trznica.dragan.forms;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.eclipse.wb.swing.FocusTraversalOnArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import rs.trznica.dragan.dao.PotrosacDao;
import rs.trznica.dragan.dto.tankovanje.PotrosacDto;
import rs.trznica.dragan.dto.tankovanje.VoziloDodatnoDto;
import rs.trznica.dragan.entities.support.GorivoType;
import rs.trznica.dragan.entities.tankovanje.Potrosac;
import rs.trznica.dragan.forms.support.ModalResult;
import rs.trznica.dragan.validator.tankovanje.PotrosacValidator;

@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class VoziloForm extends JDialog {

	/** Serial version UID */
	private static final long serialVersionUID = -7441354109420247986L;

	private Font defaultFont = new Font("Times New Roman", Font.PLAIN, 18);
	private ApplicationContext ctx;

	private JCheckBox cbxVozilo;
	private JCheckBox cbxTeretnjak;
	private JCheckBox cbxUUpotrebi;
	private JCheckBox cbxKoristiKM;
	private JTextField tfTip;
	private JTextField tfMarka;
	private JTextField tfRegOznaka;
	private JComboBox<GorivoType> cbGorivo;
	private JButton btnAdditional;
	
	private Long entityId = null;
	private VoziloDodatnoDto dodatno = new VoziloDodatnoDto();

	private ModalResult modalResult = ModalResult.CANCEL;

	private PotrosacDao potrosacDao;

	public ModalResult getModalResult() {
		return modalResult;
	}

	private void setModalResult(ModalResult modalResult) {
		this.modalResult = modalResult;
	}

	public void editConsumer(Long consumerId) {
		editConsumer(potrosacDao.findOne(consumerId));
	}

	public void editConsumer(Potrosac consumer) {
		this.entityId = consumer.getId();
		cbxVozilo.setSelected(consumer.getVozilo());
		cbxTeretnjak.setSelected(consumer.getTeretnjak());
		for (int i = 0; i < cbGorivo.getItemCount(); i++) {
			if (consumer.getGorivo().equals(cbGorivo.getItemAt(i))) {
				cbGorivo.setSelectedIndex(i);
				break;
			}
		}
		cbGorivo.setEnabled(false);
		if (consumer.getMarka() != null) {
			tfMarka.setText(consumer.getMarka());
		} else {
			tfMarka.setText("");
		}
		if (consumer.getTip() != null) {
			tfTip.setText(consumer.getTip());
		} else {
			tfTip.setText("");
		}
		if (consumer.getRegOznaka() != null) {
			tfRegOznaka.setText(consumer.getRegOznaka());
		} else {
			tfRegOznaka.setText("");
		}
		cbxUUpotrebi.setSelected(consumer.getAktivan());
		cbxKoristiKM.setSelected(consumer.getMeriKm());
		dodatno.setTeretnjak(consumer.getTeretnjak());
		dodatno.setPodrucje(consumer.getPodrucje());
		dodatno.setBrojSedista(consumer.getBrojSedista());
		dodatno.setNosivost(consumer.getNosivost());
		dodatno.setSnagaMotora(consumer.getSnagaMotora());
		dodatno.setTezina(consumer.getTezina());
		dodatno.setVozaci(consumer.getVozaci());
	}

	/**
	 * Create the frame.
	 */
	@Autowired
	public VoziloForm(ApplicationContext ctx) {
		this.ctx = ctx;
		potrosacDao = ctx.getBean(PotrosacDao.class);
		setModal(true);
		setTitle("Potro\u0161a\u010D");
		getContentPane().setFont(defaultFont);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 380);
		
		JPanel panelTop = new JPanel();
		getContentPane().add(panelTop, BorderLayout.NORTH);
		
		JLabel lblTitle = new JLabel("Potro\u0161a\u010D");
		lblTitle.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		panelTop.add(lblTitle);
		
		JPanel panelBottom = new JPanel();
		panelBottom.setBorder(new EmptyBorder(0, 0, 5, 0));
		getContentPane().add(panelBottom, BorderLayout.SOUTH);
		panelBottom.setLayout(new BoxLayout(panelBottom, BoxLayout.X_AXIS));
		
		Component horizontalGlue = Box.createHorizontalGlue();
		panelBottom.add(horizontalGlue);
		
		JButton btnOk = new JButton("Snimi");
		btnOk.addActionListener(new BtnOkActionListener());
		btnOk.setFont(defaultFont);
		btnOk.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelBottom.add(btnOk);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		panelBottom.add(horizontalStrut);
		
		JButton btnCancel = new JButton("Otka\u017Ei");
		btnCancel.addActionListener(new BtnCancelActionListener());
		btnCancel.setFont(defaultFont);
		panelBottom.add(btnCancel);
		
		Component horizontalGlue_1 = Box.createHorizontalGlue();
		panelBottom.add(horizontalGlue_1);
		
		JPanel panelCenter = new JPanel();
		panelCenter.setBorder(new EmptyBorder(0, 10, 0, 10));
		getContentPane().add(panelCenter, BorderLayout.CENTER);
		GridBagLayout gbl_panelCenter = new GridBagLayout();
		gbl_panelCenter.columnWidths = new int[] {173, 251};
		gbl_panelCenter.rowHeights = new int[] {0, 0, 0, 0, 0};
		gbl_panelCenter.columnWeights = new double[]{0.0, 1.0};
		gbl_panelCenter.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		panelCenter.setLayout(gbl_panelCenter);
		
		cbxVozilo = new JCheckBox("Potro\u0161a\u010D je vozilo");
		cbxVozilo.addItemListener(new CbxVoziloItemListener());
		cbxTeretnjak = new JCheckBox("Vozilo je teretnjak");
		cbxTeretnjak.setEnabled(false);
		addComponentRow(cbxVozilo, cbxTeretnjak, panelCenter, 0);
		
		JLabel lblGorivo = new JLabel("Gorivo:");
		cbGorivo = new JComboBox<GorivoType>();
		addComponentRow(lblGorivo, cbGorivo, panelCenter, 1);
		
		JLabel lblTip = new JLabel("Tip:");
		tfTip = new JTextField();
		addComponentRow(lblTip, tfTip, panelCenter, 2);
		
		JLabel lblMarka = new JLabel("Marka:");
		tfMarka = new JTextField();
		tfMarka.setEnabled(false);
		addComponentRow(lblMarka, tfMarka, panelCenter, 3);
		tfMarka.setColumns(10);
		
		JLabel lblRegOznaka = new JLabel("Reg. oznaka:");
		tfRegOznaka = new JTextField();
		tfRegOznaka.setEnabled(false);
		addComponentRow(lblRegOznaka, tfRegOznaka, panelCenter, 4);
		tfRegOznaka.setColumns(10);

		cbxUUpotrebi = new JCheckBox("U upotrebi");
		cbxUUpotrebi.setSelected(true);
		cbxKoristiKM = new JCheckBox("Meri pre\u0111ene kilometre");
		cbxKoristiKM.setSelected(true);
		addComponentRow(cbxUUpotrebi, cbxKoristiKM, panelCenter, 5);

		btnAdditional = new JButton("Dodatni podaci");
		btnAdditional.addActionListener(new BtnAdditionalActionListener());
		btnAdditional.setEnabled(false);
		addComponentRow(null, btnAdditional, panelCenter, 6);
		
		for (GorivoType gorivo : GorivoType.values()) {
			cbGorivo.addItem(gorivo);
		}
		cbGorivo.setSelectedIndex(0);
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{cbxVozilo, cbxTeretnjak, cbGorivo, tfTip, tfMarka, tfRegOznaka, btnOk, btnCancel}));
	}

	private void addComponentRow(Component leftComp, Component rightComp, JPanel panel, int row) {
		GridBagConstraints constraints = null;
		if (leftComp != null) {
			leftComp.setFont(defaultFont);
			constraints = new GridBagConstraints();
			constraints.anchor = GridBagConstraints.WEST;
			constraints.insets = new Insets(0, 0, 5, 5);
			constraints.gridx = 0;
			constraints.gridy = row;
			panel.add(leftComp, constraints);
		}
		if (rightComp != null) {
			rightComp.setFont(defaultFont);
			constraints = new GridBagConstraints();
			constraints.insets = new Insets(0, 0, 5, 0);
			if (JTextField.class.isAssignableFrom(rightComp.getClass()) || JComboBox.class.isAssignableFrom(rightComp.getClass())) {
				constraints.fill = GridBagConstraints.HORIZONTAL;
			} else {
				constraints.anchor = GridBagConstraints.WEST;
			}
			constraints.gridx = 1;
			constraints.gridy = row;
			panel.add(rightComp, constraints);
		}
	}
	
	private class CbxVoziloItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent ev) {
			if (ev.getStateChange() == ItemEvent.SELECTED) {
				cbxTeretnjak.setEnabled(true);
				tfMarka.setEnabled(true);
				tfRegOznaka.setEnabled(true);
				btnAdditional.setEnabled(true);
			} else if (ev.getStateChange() == ItemEvent.DESELECTED) {
				cbxTeretnjak.setEnabled(false);
				cbxTeretnjak.setSelected(false);
				tfMarka.setEnabled(false);
				tfRegOznaka.setEnabled(false);
				btnAdditional.setEnabled(false);
			}
		}
	}
	private class BtnCancelActionListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			setModalResult(ModalResult.CANCEL);
			setVisible(false);
		}
	}
	private class BtnOkActionListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			PotrosacDto dto = new PotrosacDto(entityId, tfRegOznaka.getText(), tfMarka.getText(), tfTip.getText(), 
					cbxVozilo.isSelected(), cbxTeretnjak.isSelected(), cbGorivo.getItemAt(cbGorivo.getSelectedIndex()), 
					cbxUUpotrebi.isSelected(), cbxKoristiKM.isSelected());
			BindingResult result = new DataBinder(dto).getBindingResult();
			new PotrosacValidator().validate(dto, result);
			if (result.getErrorCount() == 0) {
				Potrosac consumer = dto.createNewEntity();
				consumer.setPodrucje(dodatno.getPodrucje());
				consumer.setBrojSedista(dodatno.getBrojSedista());
				consumer.setSnagaMotora(dodatno.getSnagaMotora());
				consumer.setTezina(dodatno.getTezina());
				consumer.setNosivost(dodatno.getNosivost());
				consumer.setVozaci(dodatno.getVozaci());
				// Set id
				consumer.setId(entityId);
				potrosacDao.save(consumer);
				setModalResult(ModalResult.OK);
				setVisible(false);
			} else {
				setModalResult(ModalResult.CANCEL);
				ErrorDialog dialog = new ErrorDialog();
				dialog.showErrors(result);
			}
		}
	}
	private class BtnAdditionalActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			VoziloDodatnoForm form = ctx.getBean(VoziloDodatnoForm.class);
			dodatno.setTeretnjak(cbxTeretnjak.isSelected());
			form.editObject(dodatno);
			form.setVisible(true);
			if (ModalResult.OK.equals(form.getModalResult())) {
				dodatno = form.getReturnValue();
			}
			form.dispose();
		}
	}
}
