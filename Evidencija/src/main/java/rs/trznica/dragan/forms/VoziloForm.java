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

	private JCheckBox cbxVozilo;
	private JCheckBox cbxTeretnjak;
	private JCheckBox cbxUUpotrebi;
	private JCheckBox cbxKoristiKM;
	private JTextField tfTip;
	private JTextField tfMarka;
	private JTextField tfRegOznaka;
	private JComboBox<GorivoType> cbGorivo;

	private Long entityId = null;

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
	}

	/**
	 * Create the frame.
	 */
	@Autowired
	public VoziloForm(ApplicationContext ctx) {
		potrosacDao = ctx.getBean(PotrosacDao.class);
		setModal(true);
		setTitle("Potro\u0161a\u010D");
		getContentPane().setFont(defaultFont);
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
		cbxVozilo.setFont(defaultFont);
		GridBagConstraints gbc_cbxVozilo = new GridBagConstraints();
		gbc_cbxVozilo.anchor = GridBagConstraints.WEST;
		gbc_cbxVozilo.insets = new Insets(0, 0, 5, 5);
		gbc_cbxVozilo.gridx = 0;
		gbc_cbxVozilo.gridy = 0;
		panelCenter.add(cbxVozilo, gbc_cbxVozilo);
		
		cbxTeretnjak = new JCheckBox("Vozilo je teretnjak");
		cbxTeretnjak.setFont(defaultFont);
		cbxTeretnjak.setEnabled(false);
		GridBagConstraints gbc_cbxTeretnjak = new GridBagConstraints();
		gbc_cbxTeretnjak.insets = new Insets(0, 0, 5, 0);
		gbc_cbxTeretnjak.anchor = GridBagConstraints.WEST;
		gbc_cbxTeretnjak.gridx = 1;
		gbc_cbxTeretnjak.gridy = 0;
		panelCenter.add(cbxTeretnjak, gbc_cbxTeretnjak);
		
		JLabel lblGorivo = new JLabel("Gorivo:");
		lblGorivo.setFont(defaultFont);
		GridBagConstraints gbc_lblGorivo = new GridBagConstraints();
		gbc_lblGorivo.anchor = GridBagConstraints.WEST;
		gbc_lblGorivo.insets = new Insets(0, 0, 5, 5);
		gbc_lblGorivo.gridx = 0;
		gbc_lblGorivo.gridy = 1;
		panelCenter.add(lblGorivo, gbc_lblGorivo);
		
		cbGorivo = new JComboBox<GorivoType>();
		cbGorivo.setFont(defaultFont);
		GridBagConstraints gbc_cbGorivo = new GridBagConstraints();
		gbc_cbGorivo.insets = new Insets(0, 0, 5, 0);
		gbc_cbGorivo.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbGorivo.gridx = 1;
		gbc_cbGorivo.gridy = 1;
		panelCenter.add(cbGorivo, gbc_cbGorivo);
		
		JLabel lblTip = new JLabel("Tip:");
		lblTip.setFont(defaultFont);
		GridBagConstraints gbc_lblTip = new GridBagConstraints();
		gbc_lblTip.anchor = GridBagConstraints.WEST;
		gbc_lblTip.insets = new Insets(0, 0, 5, 5);
		gbc_lblTip.gridx = 0;
		gbc_lblTip.gridy = 2;
		panelCenter.add(lblTip, gbc_lblTip);
		
		tfTip = new JTextField();
		tfTip.setFont(defaultFont);
		GridBagConstraints gbc_tfTip = new GridBagConstraints();
		gbc_tfTip.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfTip.insets = new Insets(0, 0, 5, 0);
		gbc_tfTip.gridx = 1;
		gbc_tfTip.gridy = 2;
		panelCenter.add(tfTip, gbc_tfTip);
		tfTip.setColumns(10);
		
		JLabel lblMarka = new JLabel("Marka:");
		lblMarka.setFont(defaultFont);
		GridBagConstraints gbc_lblMarka = new GridBagConstraints();
		gbc_lblMarka.anchor = GridBagConstraints.WEST;
		gbc_lblMarka.insets = new Insets(0, 0, 5, 5);
		gbc_lblMarka.gridx = 0;
		gbc_lblMarka.gridy = 3;
		panelCenter.add(lblMarka, gbc_lblMarka);
		
		tfMarka = new JTextField();
		tfMarka.setEnabled(false);
		tfMarka.setFont(defaultFont);
		GridBagConstraints gbc_tfMarka = new GridBagConstraints();
		gbc_tfMarka.insets = new Insets(0, 0, 5, 0);
		gbc_tfMarka.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfMarka.gridx = 1;
		gbc_tfMarka.gridy = 3;
		panelCenter.add(tfMarka, gbc_tfMarka);
		tfMarka.setColumns(10);
		
		JLabel lblRegOznaka = new JLabel("Reg. oznaka:");
		lblRegOznaka.setFont(defaultFont);
		GridBagConstraints gbc_lblRegOznaka = new GridBagConstraints();
		gbc_lblRegOznaka.anchor = GridBagConstraints.WEST;
		gbc_lblRegOznaka.insets = new Insets(0, 0, 5, 5);
		gbc_lblRegOznaka.gridx = 0;
		gbc_lblRegOznaka.gridy = 4;
		panelCenter.add(lblRegOznaka, gbc_lblRegOznaka);
		
		tfRegOznaka = new JTextField();
		tfRegOznaka.setEnabled(false);
		tfRegOznaka.setFont(defaultFont);
		GridBagConstraints gbc_tfRegOznaka = new GridBagConstraints();
		gbc_tfRegOznaka.insets = new Insets(0, 0, 5, 0);
		gbc_tfRegOznaka.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfRegOznaka.gridx = 1;
		gbc_tfRegOznaka.gridy = 4;
		panelCenter.add(tfRegOznaka, gbc_tfRegOznaka);
		tfRegOznaka.setColumns(10);

		cbxUUpotrebi = new JCheckBox("U upotrebi");
		cbxUUpotrebi.setSelected(true);
		cbxUUpotrebi.setFont(defaultFont);
		GridBagConstraints gbc_cbxUUpotrebi = new GridBagConstraints();
		gbc_cbxUUpotrebi.anchor = GridBagConstraints.WEST;
		gbc_cbxUUpotrebi.insets = new Insets(0, 0, 5, 5);
		gbc_cbxUUpotrebi.gridx = 0;
		gbc_cbxUUpotrebi.gridy = 5;
		panelCenter.add(cbxUUpotrebi, gbc_cbxUUpotrebi);

		cbxKoristiKM = new JCheckBox("Meri pređene kilometre");
		cbxKoristiKM.setSelected(true);
		cbxKoristiKM.setFont(defaultFont);
		GridBagConstraints gbc_cbxKoristiKM = new GridBagConstraints();
		gbc_cbxKoristiKM.insets = new Insets(0, 0, 5, 0);
		gbc_cbxKoristiKM.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbxKoristiKM.gridx = 1;
		gbc_cbxKoristiKM.gridy = 5;
		panelCenter.add(cbxKoristiKM, gbc_cbxKoristiKM);

		for (GorivoType gorivo : GorivoType.values()) {
			cbGorivo.addItem(gorivo);
		}
		cbGorivo.setSelectedIndex(0);
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{cbxVozilo, cbxTeretnjak, cbGorivo, tfTip, tfMarka, tfRegOznaka, btnOk, btnCancel}));
	}

	private class CbxVoziloItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent ev) {
			if (ev.getStateChange() == ItemEvent.SELECTED) {
				cbxTeretnjak.setEnabled(true);
				tfMarka.setEnabled(true);
				tfRegOznaka.setEnabled(true);
			} else if (ev.getStateChange() == ItemEvent.DESELECTED) {
				cbxTeretnjak.setEnabled(false);
				cbxTeretnjak.setSelected(false);
				tfMarka.setEnabled(false);
				tfRegOznaka.setEnabled(false);
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
}
