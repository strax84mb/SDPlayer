package rs.trznica.dragan.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.jdatepicker.JDateComponentFactory;
import org.jdatepicker.impl.JDatePickerImpl;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import rs.trznica.dragan.entities.tankovanje.Potrosac;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TankovanjeForm extends JInternalFrame {

	/** Serial version UID */
	private static final long serialVersionUID = 2342322762132148412L;

	private Font defaultFont = new Font("Times New Roman", Font.PLAIN, 18);

	private JDatePickerImpl dpDatum;
	private JTextField tfMesec;
	private JTextField tfKolicina;
	private JTextField tfJedCena;
	private JComboBox<Potrosac> cbPotrosac;
	private JLabel lblPrikazUkupnog;
	private JLabel lblGorivoPotrosaca;

	/**
	 * Create the frame.
	 */
	public TankovanjeForm() {
		setClosable(true);
		setIconifiable(true);
		setTitle("Tankovanje");
		setBounds(100, 100, 450, 328);
		
		JPanel panelCenter = new JPanel();
		getContentPane().add(panelCenter, BorderLayout.CENTER);
		GridBagLayout gbl_panelCenter = new GridBagLayout();
		gbl_panelCenter.columnWidths = new int[]{0, 0, 0};
		gbl_panelCenter.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_panelCenter.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panelCenter.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		panelCenter.setLayout(gbl_panelCenter);
		
		JLabel lblDatum = new JLabel("Datum:");
		lblDatum.setFont(defaultFont);
		GridBagConstraints gbc_lblDatum = new GridBagConstraints();
		gbc_lblDatum.anchor = GridBagConstraints.WEST;
		gbc_lblDatum.insets = new Insets(0, 0, 5, 5);
		gbc_lblDatum.gridx = 0;
		gbc_lblDatum.gridy = 0;
		panelCenter.add(lblDatum, gbc_lblDatum);
		
		
		dpDatum = (JDatePickerImpl)new JDateComponentFactory().createJDatePicker();
		dpDatum.getJFormattedTextField().setFont(defaultFont);
		GridBagConstraints gbc_dpDatum = new GridBagConstraints();
		gbc_dpDatum.insets = new Insets(0, 0, 5, 0);
		gbc_dpDatum.fill = GridBagConstraints.WEST;
		gbc_dpDatum.gridx = 1;
		gbc_dpDatum.gridy = 0;
		panelCenter.add(dpDatum, gbc_dpDatum);
		
		JLabel lblMesec = new JLabel("Mesec:");
		lblMesec.setFont(defaultFont);
		GridBagConstraints gbc_lblMesec = new GridBagConstraints();
		gbc_lblMesec.anchor = GridBagConstraints.WEST;
		gbc_lblMesec.insets = new Insets(0, 0, 5, 5);
		gbc_lblMesec.gridx = 0;
		gbc_lblMesec.gridy = 1;
		panelCenter.add(lblMesec, gbc_lblMesec);
		
		tfMesec = new JTextField();
		tfMesec.setMinimumSize(new Dimension(100, 28));
		tfMesec.setPreferredSize(new Dimension(100, 28));
		tfMesec.setFont(defaultFont);
		tfMesec.setColumns(10);
		GridBagConstraints gbc_tfMesec = new GridBagConstraints();
		gbc_tfMesec.anchor = GridBagConstraints.WEST;
		gbc_tfMesec.insets = new Insets(0, 0, 5, 0);
		gbc_tfMesec.gridx = 1;
		gbc_tfMesec.gridy = 1;
		panelCenter.add(tfMesec, gbc_tfMesec);
		
		JLabel lblPotrosac = new JLabel("Potro\u0161a\u010D:");
		lblPotrosac.setFont(defaultFont);
		GridBagConstraints gbc_lblPotrosac = new GridBagConstraints();
		gbc_lblPotrosac.anchor = GridBagConstraints.WEST;
		gbc_lblPotrosac.insets = new Insets(0, 0, 5, 5);
		gbc_lblPotrosac.gridx = 0;
		gbc_lblPotrosac.gridy = 2;
		panelCenter.add(lblPotrosac, gbc_lblPotrosac);
		
		cbPotrosac = new JComboBox<Potrosac>();
		cbPotrosac.setFont(defaultFont);
		GridBagConstraints gbc_cbPotrosac = new GridBagConstraints();
		gbc_cbPotrosac.insets = new Insets(0, 0, 5, 0);
		gbc_cbPotrosac.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbPotrosac.gridx = 1;
		gbc_cbPotrosac.gridy = 2;
		panelCenter.add(cbPotrosac, gbc_cbPotrosac);
		
		JLabel lblGorivo = new JLabel("Gorivo:");
		lblGorivo.setFont(defaultFont);
		GridBagConstraints gbc_lblGorivo = new GridBagConstraints();
		gbc_lblGorivo.anchor = GridBagConstraints.WEST;
		gbc_lblGorivo.insets = new Insets(0, 0, 5, 5);
		gbc_lblGorivo.gridx = 0;
		gbc_lblGorivo.gridy = 3;
		panelCenter.add(lblGorivo, gbc_lblGorivo);
		
		lblGorivoPotrosaca = new JLabel("");
		lblGorivoPotrosaca.setFont(defaultFont);
		GridBagConstraints gbc_lblGorivoPotrosaca = new GridBagConstraints();
		gbc_lblGorivoPotrosaca.anchor = GridBagConstraints.WEST;
		gbc_lblGorivoPotrosaca.insets = new Insets(0, 0, 5, 0);
		gbc_lblGorivoPotrosaca.gridx = 1;
		gbc_lblGorivoPotrosaca.gridy = 3;
		panelCenter.add(lblGorivoPotrosaca, gbc_lblGorivoPotrosaca);
		
		JLabel lblKolicina = new JLabel("Koli\u010Dina:");
		lblKolicina.setFont(defaultFont);
		GridBagConstraints gbc_lblKolicina = new GridBagConstraints();
		gbc_lblKolicina.anchor = GridBagConstraints.WEST;
		gbc_lblKolicina.insets = new Insets(0, 0, 5, 5);
		gbc_lblKolicina.gridx = 0;
		gbc_lblKolicina.gridy = 4;
		panelCenter.add(lblKolicina, gbc_lblKolicina);
		
		tfKolicina = new JTextField();
		tfKolicina.setFont(defaultFont);
		GridBagConstraints gbc_tfKolicina = new GridBagConstraints();
		gbc_tfKolicina.insets = new Insets(0, 0, 5, 0);
		gbc_tfKolicina.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfKolicina.gridx = 1;
		gbc_tfKolicina.gridy = 4;
		panelCenter.add(tfKolicina, gbc_tfKolicina);
		tfKolicina.setColumns(10);
		
		JLabel lblJedCena = new JLabel("Cena litre:");
		lblJedCena.setFont(defaultFont);
		GridBagConstraints gbc_lblJedCena = new GridBagConstraints();
		gbc_lblJedCena.anchor = GridBagConstraints.WEST;
		gbc_lblJedCena.insets = new Insets(0, 0, 5, 5);
		gbc_lblJedCena.gridx = 0;
		gbc_lblJedCena.gridy = 5;
		panelCenter.add(lblJedCena, gbc_lblJedCena);
		
		tfJedCena = new JTextField();
		tfJedCena.setFont(defaultFont);
		GridBagConstraints gbc_tfJedCena = new GridBagConstraints();
		gbc_tfJedCena.insets = new Insets(0, 0, 5, 0);
		gbc_tfJedCena.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfJedCena.gridx = 1;
		gbc_tfJedCena.gridy = 5;
		panelCenter.add(tfJedCena, gbc_tfJedCena);
		tfJedCena.setColumns(10);
		
		JLabel lblUkupno = new JLabel("Ukupno:");
		lblUkupno.setFont(defaultFont);
		GridBagConstraints gbc_lblUkupno = new GridBagConstraints();
		gbc_lblUkupno.anchor = GridBagConstraints.WEST;
		gbc_lblUkupno.insets = new Insets(0, 0, 0, 5);
		gbc_lblUkupno.gridx = 0;
		gbc_lblUkupno.gridy = 6;
		panelCenter.add(lblUkupno, gbc_lblUkupno);
		
		lblPrikazUkupnog = new JLabel("");
		lblPrikazUkupnog.setFont(defaultFont);
		GridBagConstraints gbc_lblPrikazUkupnog = new GridBagConstraints();
		gbc_lblPrikazUkupnog.anchor = GridBagConstraints.WEST;
		gbc_lblPrikazUkupnog.gridx = 1;
		gbc_lblPrikazUkupnog.gridy = 6;
		panelCenter.add(lblPrikazUkupnog, gbc_lblPrikazUkupnog);
		
		JPanel panelButtons = new JPanel();
		panelButtons.setBorder(new EmptyBorder(0, 0, 5, 0));
		getContentPane().add(panelButtons, BorderLayout.SOUTH);
		panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.X_AXIS));
		
		java.awt.Component horizontalGlue = Box.createHorizontalGlue();
		panelButtons.add(horizontalGlue);
		
		JButton btnOk = new JButton("Snimi");
		btnOk.setFont(defaultFont);
		panelButtons.add(btnOk);
		
		java.awt.Component horizontalStrut = Box.createHorizontalStrut(20);
		panelButtons.add(horizontalStrut);
		
		JButton btnCancel = new JButton("Otka\u017Ei");
		btnCancel.setFont(defaultFont);
		panelButtons.add(btnCancel);
		
		java.awt.Component horizontalGlue_1 = Box.createHorizontalGlue();
		panelButtons.add(horizontalGlue_1);

	}

	protected JLabel getLblPrikazUkupnog() {
		return lblPrikazUkupnog;
	}
	public JLabel getLblGorivoPotrosaca() {
		return lblGorivoPotrosaca;
	}
}
