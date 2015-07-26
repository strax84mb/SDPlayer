package rs.trznica.dragan.forms;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.eclipse.wb.swing.FocusTraversalOnArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import rs.trznica.dragan.dao.PotrosacDao;
import rs.trznica.dragan.dao.TankovanjeDao;
import rs.trznica.dragan.entities.tankovanje.Potrosac;
import rs.trznica.dragan.forms.support.DecimalFormater;
import rs.trznica.dragan.forms.support.ModalResult;

import com.toedter.calendar.JDateChooser;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

@Component
@Configurable
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TankovanjeDialog extends GenericDialog {

	private static final long serialVersionUID = -8625818313169843072L;

	private final JPanel contentPanel = new JPanel();
	private JTextField tfMesec;
	private JTextField tfJedCena;
	private JTextField tfUkupno;
	private JTextField tfKolicina;
	private JLabel lblConsumer;
	private JComboBox<Potrosac> cbPotrosac;
	private JLabel lblGorivo;
	private JLabel lblIzabranoGorivo;
	private JLabel lblDatum;
	private JDateChooser dpDatum;
	private JLabel lblMesec;
	private JLabel lblKolicina;
	private JLabel lblCenaLitre;
	private JLabel lblUkupno;
	private JPanel buttonPane;
	private java.awt.Component horizontalGlue_1;
	private JButton btnSnimi;
	private java.awt.Component horizontalStrut;
	private JButton btnOtkazi;
	private java.awt.Component horizontalGlue;

	private Long entityId = null;

	private PotrosacDao potrosacDao;

	private TankovanjeDao tankovanjeDao;

	/**
	 * Create the dialog.
	 */
	@Autowired
	public TankovanjeDialog(ApplicationContext ctx) {
		potrosacDao = ctx.getBean(PotrosacDao.class);
		tankovanjeDao = ctx.getBean(TankovanjeDao.class);
		setModal(true);
		setTitle("Tankovanje");
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setBounds(100, 100, 396, 334);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(10, 10, 10, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			lblConsumer = new JLabel("Potro\u0161a\u010D:");
			lblConsumer.setFont(new Font("Times New Roman", Font.PLAIN, 16));
			GridBagConstraints gbc_lblConsumer = new GridBagConstraints();
			gbc_lblConsumer.anchor = GridBagConstraints.EAST;
			gbc_lblConsumer.insets = new Insets(0, 0, 5, 5);
			gbc_lblConsumer.gridx = 0;
			gbc_lblConsumer.gridy = 0;
			contentPanel.add(lblConsumer, gbc_lblConsumer);
		}
		{
			cbPotrosac = new JComboBox<Potrosac>();
			cbPotrosac.addItemListener(new CbPotrosacItemListener());
			cbPotrosac.setFont(new Font("Times New Roman", Font.PLAIN, 16));
			GridBagConstraints gbc_cbPotrosac = new GridBagConstraints();
			gbc_cbPotrosac.insets = new Insets(0, 0, 5, 0);
			gbc_cbPotrosac.fill = GridBagConstraints.HORIZONTAL;
			gbc_cbPotrosac.gridx = 1;
			gbc_cbPotrosac.gridy = 0;
			contentPanel.add(cbPotrosac, gbc_cbPotrosac);
		}
		{
			lblGorivo = new JLabel("Gorivo:");
			lblGorivo.setFont(new Font("Times New Roman", Font.PLAIN, 16));
			GridBagConstraints gbc_lblGorivo = new GridBagConstraints();
			gbc_lblGorivo.anchor = GridBagConstraints.EAST;
			gbc_lblGorivo.insets = new Insets(0, 0, 5, 5);
			gbc_lblGorivo.gridx = 0;
			gbc_lblGorivo.gridy = 1;
			contentPanel.add(lblGorivo, gbc_lblGorivo);
		}
		{
			lblIzabranoGorivo = new JLabel("BMB");
			lblIzabranoGorivo.setFont(new Font("Times New Roman", Font.PLAIN, 16));
			GridBagConstraints gbc_lblIzabranoGorivo = new GridBagConstraints();
			gbc_lblIzabranoGorivo.anchor = GridBagConstraints.WEST;
			gbc_lblIzabranoGorivo.insets = new Insets(0, 0, 5, 0);
			gbc_lblIzabranoGorivo.gridx = 1;
			gbc_lblIzabranoGorivo.gridy = 1;
			contentPanel.add(lblIzabranoGorivo, gbc_lblIzabranoGorivo);
		}
		{
			lblDatum = new JLabel("Datum:");
			lblDatum.setFont(new Font("Times New Roman", Font.PLAIN, 16));
			GridBagConstraints gbc_lblDatum = new GridBagConstraints();
			gbc_lblDatum.anchor = GridBagConstraints.EAST;
			gbc_lblDatum.insets = new Insets(0, 0, 5, 5);
			gbc_lblDatum.gridx = 0;
			gbc_lblDatum.gridy = 2;
			contentPanel.add(lblDatum, gbc_lblDatum);
		}
		{
			dpDatum = new JDateChooser();
			dpDatum.setPreferredSize(new Dimension(150, 28));
			dpDatum.setMinimumSize(new Dimension(100, 28));
			dpDatum.getDateEditor().addPropertyChangeListener(new DpDatumPropertyChangeListener());
			GridBagConstraints gbc_dpDatum = new GridBagConstraints();
			gbc_dpDatum.anchor = GridBagConstraints.WEST;
			gbc_dpDatum.insets = new Insets(0, 0, 5, 0);
			gbc_dpDatum.fill = GridBagConstraints.VERTICAL;
			gbc_dpDatum.gridx = 1;
			gbc_dpDatum.gridy = 2;
			contentPanel.add(dpDatum, gbc_dpDatum);
		}
		{
			lblMesec = new JLabel("Mesec:");
			lblMesec.setFont(new Font("Times New Roman", Font.PLAIN, 16));
			GridBagConstraints gbc_lblMesec = new GridBagConstraints();
			gbc_lblMesec.anchor = GridBagConstraints.EAST;
			gbc_lblMesec.insets = new Insets(0, 0, 5, 5);
			gbc_lblMesec.gridx = 0;
			gbc_lblMesec.gridy = 3;
			contentPanel.add(lblMesec, gbc_lblMesec);
		}
		{
			tfMesec = new JTextField();
			tfMesec.setFont(new Font("Times New Roman", Font.PLAIN, 16));
			GridBagConstraints gbc_tfMesec = new GridBagConstraints();
			gbc_tfMesec.anchor = GridBagConstraints.WEST;
			gbc_tfMesec.insets = new Insets(0, 0, 5, 0);
			gbc_tfMesec.gridx = 1;
			gbc_tfMesec.gridy = 3;
			contentPanel.add(tfMesec, gbc_tfMesec);
			tfMesec.setColumns(10);
		}
		{
			lblKolicina = new JLabel("Koli\u010Dina:");
			lblKolicina.setFont(new Font("Times New Roman", Font.PLAIN, 16));
			GridBagConstraints gbc_lblKolicina = new GridBagConstraints();
			gbc_lblKolicina.anchor = GridBagConstraints.EAST;
			gbc_lblKolicina.insets = new Insets(0, 0, 5, 5);
			gbc_lblKolicina.gridx = 0;
			gbc_lblKolicina.gridy = 4;
			contentPanel.add(lblKolicina, gbc_lblKolicina);
		}
		{
			tfKolicina = new JTextField();
			tfKolicina.addFocusListener(new TfKolicinaFocusListener());
			tfKolicina.setFont(new Font("Times New Roman", Font.PLAIN, 16));
			GridBagConstraints gbc_tfKolicina = new GridBagConstraints();
			gbc_tfKolicina.anchor = GridBagConstraints.WEST;
			gbc_tfKolicina.insets = new Insets(0, 0, 5, 0);
			gbc_tfKolicina.gridx = 1;
			gbc_tfKolicina.gridy = 4;
			contentPanel.add(tfKolicina, gbc_tfKolicina);
			tfKolicina.setColumns(10);
		}
		{
			lblCenaLitre = new JLabel("Cena litre:");
			lblCenaLitre.setFont(new Font("Times New Roman", Font.PLAIN, 16));
			GridBagConstraints gbc_lblCenaLitre = new GridBagConstraints();
			gbc_lblCenaLitre.anchor = GridBagConstraints.EAST;
			gbc_lblCenaLitre.insets = new Insets(0, 0, 5, 5);
			gbc_lblCenaLitre.gridx = 0;
			gbc_lblCenaLitre.gridy = 5;
			contentPanel.add(lblCenaLitre, gbc_lblCenaLitre);
		}
		{
			tfJedCena = new JTextField();
			tfJedCena.addFocusListener(new TfJedCenaFocusListener());
			tfJedCena.setFont(new Font("Times New Roman", Font.PLAIN, 16));
			GridBagConstraints gbc_tfJedCena = new GridBagConstraints();
			gbc_tfJedCena.anchor = GridBagConstraints.WEST;
			gbc_tfJedCena.insets = new Insets(0, 0, 5, 0);
			gbc_tfJedCena.gridx = 1;
			gbc_tfJedCena.gridy = 5;
			contentPanel.add(tfJedCena, gbc_tfJedCena);
			tfJedCena.setColumns(10);
		}
		{
			lblUkupno = new JLabel("Ukupno:");
			lblUkupno.setFont(new Font("Times New Roman", Font.PLAIN, 16));
			GridBagConstraints gbc_lblUkupno = new GridBagConstraints();
			gbc_lblUkupno.insets = new Insets(0, 0, 0, 5);
			gbc_lblUkupno.anchor = GridBagConstraints.EAST;
			gbc_lblUkupno.gridx = 0;
			gbc_lblUkupno.gridy = 6;
			contentPanel.add(lblUkupno, gbc_lblUkupno);
		}
		{
			tfUkupno = new JTextField();
			tfUkupno.setEditable(false);
			tfUkupno.setFont(new Font("Times New Roman", Font.PLAIN, 16));
			GridBagConstraints gbc_tfUkupno = new GridBagConstraints();
			gbc_tfUkupno.anchor = GridBagConstraints.WEST;
			gbc_tfUkupno.gridx = 1;
			gbc_tfUkupno.gridy = 6;
			contentPanel.add(tfUkupno, gbc_tfUkupno);
			tfUkupno.setColumns(10);
		}
		{
			buttonPane = new JPanel();
			buttonPane.setBorder(new EmptyBorder(0, 0, 10, 0));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
			{
				horizontalGlue_1 = Box.createHorizontalGlue();
				buttonPane.add(horizontalGlue_1);
			}
			{
				btnSnimi = new JButton("Snimi");
				btnSnimi.setFont(new Font("Times New Roman", Font.PLAIN, 16));
				btnSnimi.setActionCommand("OK");
				buttonPane.add(btnSnimi);
				getRootPane().setDefaultButton(btnSnimi);
			}
			{
				horizontalStrut = Box.createHorizontalStrut(20);
				buttonPane.add(horizontalStrut);
			}
			{
				btnOtkazi = new JButton("Otka\u017Ei");
				btnOtkazi.addActionListener(new BtnOtkaziActionListener());
				btnOtkazi.setFont(new Font("Times New Roman", Font.PLAIN, 16));
				btnOtkazi.setActionCommand("Cancel");
				buttonPane.add(btnOtkazi);
			}
			{
				horizontalGlue = Box.createHorizontalGlue();
				buttonPane.add(horizontalGlue);
			}
		}
		setFocusTraversalPolicy(new FocusTraversalOnArray(new java.awt.Component[]{getContentPane(), contentPanel, lblConsumer, cbPotrosac, lblGorivo, lblIzabranoGorivo, lblDatum, dpDatum, lblMesec, tfMesec, lblKolicina, tfKolicina, lblCenaLitre, tfJedCena, lblUkupno, tfUkupno, buttonPane, horizontalGlue_1, btnSnimi, horizontalStrut, btnOtkazi, horizontalGlue}));
		populateConsumers();
	}

	public void populateConsumers() {
		Iterator<Potrosac> potrosaci = potrosacDao.findAll().iterator();
		while (potrosaci.hasNext()) {
			getCbPotrosac().addItem(potrosaci.next());
		}
		getCbPotrosac().setSelectedIndex(-1);
	}

	public JComboBox<Potrosac> getCbPotrosac() {
		return cbPotrosac;
	}

	public JLabel getLblIzabranoGorivo() {
		return lblIzabranoGorivo;
	}

	public JDateChooser getDpDatum() {
		return dpDatum;
	}

	public JTextField getTfMesec() {
		return tfMesec;
	}

	public JTextField getTfKolicina() {
		return tfKolicina;
	}

	public JTextField getTfJedCena() {
		return tfJedCena;
	}

	public JTextField getTfUkupno() {
		return tfUkupno;
	}

	private TankovanjeDialog getThisDialog() {
		return this;
	}

	private class BtnOtkaziActionListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			modalResult = ModalResult.CANCEL;
			getThisDialog().setVisible(false);
		}
	}

	private class CbPotrosacItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent ev) {
			if (ev.getStateChange() == ItemEvent.SELECTED) {
				Potrosac potrosac = (Potrosac)ev.getItem();
				getLblIzabranoGorivo().setText(potrosac.getGorivo().getLabel());
			}
		}
	}
	private class TfKolicinaFocusListener extends FocusAdapter {
		@Override
		public void focusLost(FocusEvent ev) {
			try {
				Double number = DecimalFormater.parseToDouble(getTfKolicina().getText());
				getTfKolicina().setText(DecimalFormater.formatFromDouble(number, 2));
			} catch (Exception e) {
				e.printStackTrace();
				getTfKolicina().setText("");
			}
		}
	}
	private class TfJedCenaFocusListener extends FocusAdapter {
		@Override
		public void focusLost(FocusEvent ev) {
			try {
				Double number = DecimalFormater.parseToDouble(getTfJedCena().getText());
				getTfJedCena().setText(DecimalFormater.formatFromDouble(number, 2));
				getTfUkupno().setText(DecimalFormater.formatFromDouble(number * DecimalFormater.parseToDouble(getTfKolicina().getText()), 2));
			} catch (Exception e) {
				e.printStackTrace();
				getTfKolicina().setText("");
			}
		}
	}
	private class DpDatumPropertyChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent ev) {
			if ("date".equals(ev.getPropertyName())) {
				Date date = getDpDatum().getDate();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
				getTfMesec().setText(sdf.format(date));
			}
		}
	}

}
