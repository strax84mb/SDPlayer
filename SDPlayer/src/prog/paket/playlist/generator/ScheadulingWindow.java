package prog.paket.playlist.generator;

import java.awt.BorderLayout;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import java.awt.Component;
import javax.swing.Box;

import prog.paket.dodaci.JSongField;
import prog.paket.playlist.generator.struct.MusicCategory;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;

public class ScheadulingWindow extends JDialog {

	private static final long serialVersionUID = -9098340281078824855L;

	private final JPanel contentPanel = new JPanel();
	public JPanel panelDani;
	public JRadioButton rdbtnPeriodicno;
	public JLabel lblDanima;
	public JComboBox<String> cbDanima;
	public JCheckBox chckbxPon;
	public JCheckBox chckbxUto;
	public JCheckBox chckbxSre;
	public JCheckBox chckbxCet;
	public JCheckBox chckbxPet;
	public JCheckBox chckbxSub;
	public JCheckBox chckbxNed;
	public JRadioButton rdbtnTokomIntervala;
	public JLabel lblOdDana;
	public JTextField tfOdDatum;
	public JPanel terminPanel;
	public JLabel lblPrvoEmitovanje;
	public JTextField tfPrvo;
	public JTextField tfZadnje;
	public JTextField tfNaSvakih;
	public JLabel lblZadnjeEmitovanje;
	public JLabel lblNaSvakih;
	public JCheckBox chckbxTermin1;
	public JCheckBox chckbxTermin2;
	public JCheckBox chckbxTermin3;
	public JCheckBox chckbxTermin4;
	public JCheckBox chckbxTermin5;
	public JCheckBox chckbxTermin6;
	public JCheckBox chckbxTermin7;
	public JCheckBox chckbxTermin8;
	public JCheckBox chckbxTermin9;
	public JCheckBox chckbxTermin10;
	public JTextField tfTer1;
	public JTextField tfTer2;
	public JTextField tfTer3;
	public JTextField tfTer4;
	public JTextField tfTer5;
	public JTextField tfTer6;
	public JTextField tfTer7;
	public JTextField tfTer8;
	public JTextField tfTer9;
	public JTextField tfTer10;
	public JPanel trajanjePanel;
	public JRadioButton rdbtnZadatoVremeTrajanja;
	public JRadioButton rdbtnVremeTrajanjaListe;
	public JLabel lblTrajanje;
	public JTextField tfTrajanje;
	public JPanel prioritetPanel;
	public JComboBox<String> cbPrioritet;
	public JPanel titlePanel;
	public Component horizontalGlue;
	public JLabel lblTitle;
	public Component horizontalGlue_1;
	public MusicCategory cat;
	public ErrorInfoDialog errorDlg;
	public JPanel opcijePanel;
	public JCheckBox chckbxCrossfade;
	public JCheckBox chckbxCuvatiSadrajTermina;
	public JLabel lblNajavnaSpica;
	public JLabel lblOdjavnaSpica;
	public JSongField tfNajava;
	public JSongField tfOdjava;
	public JCheckBox chckbxPisiUIzvetaj;
	public JLabel lblDoDana;
	public JTextField tfDoDatum;
	public JLabel lblOdU;
	public JLabel lblDoU;
	public JTextField tfOdVreme;
	public JTextField tfDoVreme;
	private JButton okButton;
	public JCheckBox chckbxPeriodicno;
	public JCheckBox chckbxUZadatomTerminu;

	public ButtonGroup daysGroup;
	public ButtonGroup durationGroup;

	private VremeFocusListener vremeFocusListener;
	private Calendar cal = new GregorianCalendar();
	private SimpleDateFormat sdf = new SimpleDateFormat("d.M.yyyy");
	public boolean shouldSave = false;
	public JCheckBox chckbxPostujRedosled;
	public JRadioButton rdbtnOgraniceno;
	public JTextField tfBrojPesama;
	public JLabel lblPesama;

	private int parseTime(String text){
		int ret = -1, pos, num;
		text = text.replace('.', ':');
		text = text.replace(',', ':');
		try{
			ret = Integer.parseInt(text);
			if((ret >= 24) || (ret < 0)) return -1;
		}catch(NumberFormatException nfe){}
		pos = text.indexOf(":");
		if(pos == -1) return -1;
		try{
			ret = Integer.parseInt(text.substring(0, pos));
			if((ret >= 24) || (ret < 0)) return -1;
		}catch(NumberFormatException nfe){
			return -1;
		}
		ret *= 60;
		try{
			num = Integer.parseInt(text.substring(pos + 1));
			if((num >= 60) || (num < 0)) return -1;
		}catch(NumberFormatException nfe){
			return -1;
		}
		return ret + num;
	}

	private String formatTime(int time){
		if(time == -1) return "";
		String ret = String.valueOf(time % 60);
		if(ret.length() == 1) ret = "0" + ret;
		ret = String.valueOf(time / 60) + ":" + ret;
		if(ret.length() == 4) ret = "0" + ret;
		return ret;
	}

	public void popuniPolja(MusicCategory cat){
		int rem;
		lblTitle.setText("Kategorija " + cat.ime);
		setTitle(cat.ime);
		// Popuna odabira dana
		if(cat.begin == -1){
			rdbtnPeriodicno.setSelected(true);
			chckbxPon.setSelected(cat.ponedeljak);
			chckbxUto.setSelected(cat.utorak);
			chckbxSre.setSelected(cat.sreda);
			chckbxCet.setSelected(cat.cetvrtak);
			chckbxPet.setSelected(cat.petak);
			chckbxSub.setSelected(cat.subota);
			chckbxNed.setSelected(cat.nedelja);
			if(cat.ponedeljak && cat.utorak && cat.sreda && cat.cetvrtak && cat.petak 
					&& !cat.subota && !cat.nedelja){
				cbDanima.setSelectedIndex(0);
			}else if(!cat.ponedeljak && !cat.utorak && !cat.sreda && !cat.cetvrtak && !cat.petak 
					&& cat.subota && cat.nedelja){
				cbDanima.setSelectedIndex(1);
			}else if(cat.ponedeljak && cat.utorak && cat.sreda && cat.cetvrtak && cat.petak 
					&& cat.subota && cat.nedelja){
				cbDanima.setSelectedIndex(2);
			}else cbDanima.setSelectedIndex(3);
		}else{
			rdbtnTokomIntervala.setSelected(true);
			cal.setTimeInMillis(cat.begin);
			tfOdDatum.setText(sdf.format(cal.getTime()));
			rem = (cal.get(Calendar.HOUR_OF_DAY) * 60) + cal.get(Calendar.MINUTE);
			tfOdVreme.setText(formatTime(rem));
			cal.setTimeInMillis(cat.end);
			tfDoDatum.setText(sdf.format(cal.getTime()));
			rem = (cal.get(Calendar.HOUR_OF_DAY) * 60) + cal.get(Calendar.MINUTE);
			tfDoVreme.setText(formatTime(rem));
		}
		// Popuna krosfejda, cuvanja sadrzaja i pisanjau izvestaj
		chckbxCrossfade.setSelected(cat.crossfade);
		chckbxPisiUIzvetaj.setSelected(cat.pisiUIzvestaj);
		chckbxCuvatiSadrajTermina.setSelected(cat.cuvajSadrzaj);
		chckbxPostujRedosled.setSelected(cat.postujRedosled);
		// Popuna trajanja emitovanja
		if(cat.trajanje == 0){
			rdbtnVremeTrajanjaListe.setSelected(true);
		}else if(cat.trajanje < 0){
			rdbtnOgraniceno.setSelected(true);
			tfBrojPesama.setText(String.valueOf(-cat.trajanje));
		}else{
			rdbtnZadatoVremeTrajanja.setSelected(true);
			tfTrajanje.setText(formatTime(cat.trajanje));
		}
		// Popuna prioriteta
		cbPrioritet.setSelectedIndex(cat.prioritet - 1);
		// Popuna periodicnog emitovanja
		if(cat.periodicno){
			chckbxPeriodicno.setSelected(true);
			tfPrvo.setText(formatTime(cat.prvo));
			tfZadnje.setText(formatTime(cat.zadnje));
			tfNaSvakih.setText(formatTime(cat.na_svakih));
		}else{
			chckbxPeriodicno.setSelected(true);
			chckbxPeriodicno.setSelected(false);
		}
		// Popuna terminskog emitovanja
		if(cat.terminsko){
			chckbxUZadatomTerminu.setSelected(true);
			if(cat.termin1 != -1){
				chckbxTermin1.setSelected(true);
				tfTer1.setText(formatTime(cat.termin1));
			}else chckbxTermin1.setSelected(false);
			if(cat.termin2 != -1){
				chckbxTermin2.setSelected(true);
				tfTer2.setText(formatTime(cat.termin2));
			}else chckbxTermin2.setSelected(false);
			if(cat.termin3 != -1){
				chckbxTermin3.setSelected(true);
				tfTer3.setText(formatTime(cat.termin3));
			}else chckbxTermin3.setSelected(false);
			if(cat.termin4 != -1){
				chckbxTermin4.setSelected(true);
				tfTer4.setText(formatTime(cat.termin4));
			}else chckbxTermin4.setSelected(false);
			if(cat.termin5 != -1){
				chckbxTermin5.setSelected(true);
				tfTer5.setText(formatTime(cat.termin5));
			}else chckbxTermin5.setSelected(false);
			if(cat.termin6 != -1){
				chckbxTermin6.setSelected(true);
				tfTer6.setText(formatTime(cat.termin6));
			}else chckbxTermin6.setSelected(false);
			if(cat.termin7 != -1){
				chckbxTermin7.setSelected(true);
				tfTer7.setText(formatTime(cat.termin7));
			}else chckbxTermin7.setSelected(false);
			if(cat.termin8 != -1){
				chckbxTermin8.setSelected(true);
				tfTer8.setText(formatTime(cat.termin8));
			}else chckbxTermin8.setSelected(false);
			if(cat.termin9 != -1){
				chckbxTermin9.setSelected(true);
				tfTer9.setText(formatTime(cat.termin9));
			}else chckbxTermin9.setSelected(false);
			if(cat.termin10 != -1){
				chckbxTermin10.setSelected(true);
				tfTer10.setText(formatTime(cat.termin10));
			}else chckbxTermin10.setSelected(false);
		}else{
			chckbxUZadatomTerminu.setSelected(true);
			chckbxUZadatomTerminu.setSelected(false);
		}
		// Spice
		if(cat.najavnaSpica == null)
			tfNajava.setNullItem();
		else tfNajava.setItem(cat.najavnaSpica);
		if(cat.odjavnaSpica == null)
			tfOdjava.setNullItem();
		else tfOdjava.setItem(cat.odjavnaSpica);
	}

	/**
	 * Create the dialog.
	 */
	public ScheadulingWindow(ErrorInfoDialog errorDlg) {
		setResizable(false);
		setModal(true);
		cal.setLenient(false);
		sdf.setLenient(false);
		this.errorDlg = errorDlg;
		setBounds(100, 100, 701, 522);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		panelDani = new JPanel();
		panelDani.setBorder(new TitledBorder(null, "Dani emitovanja", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		terminPanel = new JPanel();
		terminPanel.setBorder(new TitledBorder(null, "Vreme emitovanja", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		trajanjePanel = new JPanel();
		trajanjePanel.setBorder(new TitledBorder(null, "Trajanje emitovanja", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		prioritetPanel = new JPanel();
		prioritetPanel.setBorder(new TitledBorder(null, "Prioritet", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		titlePanel = new JPanel();
		
		opcijePanel = new JPanel();
		opcijePanel.setBorder(new TitledBorder(null, "Dodatni izbori", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		SpringLayout sl_opcijePanel = new SpringLayout();
		opcijePanel.setLayout(sl_opcijePanel);
		
		chckbxCrossfade = new JCheckBox("Postepeni prelaz (crossfade)");
		sl_opcijePanel.putConstraint(SpringLayout.NORTH, chckbxCrossfade, 5, SpringLayout.NORTH, opcijePanel);
		sl_opcijePanel.putConstraint(SpringLayout.WEST, chckbxCrossfade, 10, SpringLayout.WEST, opcijePanel);
		chckbxCrossfade.setAlignmentX(Component.CENTER_ALIGNMENT);
		opcijePanel.add(chckbxCrossfade);
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
		
		horizontalGlue = Box.createHorizontalGlue();
		titlePanel.add(horizontalGlue);
		
		lblTitle = new JLabel("Kategorija Pop Rock");
		lblTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
		titlePanel.add(lblTitle);
		
		horizontalGlue_1 = Box.createHorizontalGlue();
		titlePanel.add(horizontalGlue_1);
		
		cbPrioritet = new JComboBox<String>();
		cbPrioritet.setModel(new DefaultComboBoxModel<String>(new String[] {
				"1 - Najveći", "2", "3", "4", "5", "6 - Najmanji"}));
		cbPrioritet.setSelectedIndex(5);
		
		durationGroup = new ButtonGroup();
		
		rdbtnZadatoVremeTrajanja = new JRadioButton("Zadato vreme trajanja");
		durationGroup.add(rdbtnZadatoVremeTrajanja);
		rdbtnZadatoVremeTrajanja.addItemListener(new RdbtnZadatoVremeTrajanjaItemListener());
		
		rdbtnOgraniceno = new JRadioButton("Trajanje");
		rdbtnOgraniceno.addItemListener(new RdbtnOgranicenoItemListener());
		durationGroup.add(rdbtnOgraniceno);
		
		rdbtnVremeTrajanjaListe = new JRadioButton("<html>Ukupno trajanje svih zvu\u010Dnih zapisa u kategoriji</html>");
		durationGroup.add(rdbtnVremeTrajanjaListe);
		rdbtnVremeTrajanjaListe.addItemListener(new RdbtnVremeTrajanjaListeItemListener());
		rdbtnVremeTrajanjaListe.setVerticalAlignment(SwingConstants.TOP);
		
		lblTrajanje = new JLabel("Trajanje");
		
		vremeFocusListener = new VremeFocusListener();
		
		tfTrajanje = new JTextField();
		tfTrajanje.addFocusListener(vremeFocusListener);
		tfTrajanje.setHorizontalAlignment(SwingConstants.CENTER);
		tfTrajanje.setColumns(4);
		tfTrajanje.setBackground(Color.WHITE);
		
		lblPrvoEmitovanje = new JLabel("Prvo emitovanje");
		
		tfPrvo = new JTextField();
		tfPrvo.addFocusListener(vremeFocusListener);
		tfPrvo.setHorizontalAlignment(SwingConstants.CENTER);
		tfPrvo.setBackground(Color.WHITE);
		tfPrvo.setColumns(4);
		
		tfZadnje = new JTextField();
		tfZadnje.addFocusListener(vremeFocusListener);
		tfZadnje.setHorizontalAlignment(SwingConstants.CENTER);
		tfZadnje.setBackground(Color.WHITE);
		tfZadnje.setColumns(4);
		
		tfNaSvakih = new JTextField();
		tfNaSvakih.addFocusListener(vremeFocusListener);
		tfNaSvakih.setHorizontalAlignment(SwingConstants.CENTER);
		tfNaSvakih.setBackground(Color.WHITE);
		tfNaSvakih.setColumns(4);
		
		lblZadnjeEmitovanje = new JLabel("Zadnje emitovanje");
		
		lblNaSvakih = new JLabel("Na svakih");
		
		tfTer1 = new JTextField();
		tfTer1.addFocusListener(vremeFocusListener);
		tfTer1.setBackground(Color.WHITE);
		tfTer1.setHorizontalAlignment(SwingConstants.CENTER);
		tfTer1.setColumns(4);
		
		tfTer2 = new JTextField();
		tfTer2.addFocusListener(vremeFocusListener);
		tfTer2.setBackground(Color.WHITE);
		tfTer2.setHorizontalAlignment(SwingConstants.CENTER);
		tfTer2.setColumns(10);
		
		tfTer3 = new JTextField();
		tfTer3.addFocusListener(vremeFocusListener);
		tfTer3.setBackground(Color.WHITE);
		tfTer3.setHorizontalAlignment(SwingConstants.CENTER);
		tfTer3.setColumns(10);
		
		tfTer4 = new JTextField();
		tfTer4.addFocusListener(vremeFocusListener);
		tfTer4.setBackground(Color.WHITE);
		tfTer4.setHorizontalAlignment(SwingConstants.CENTER);
		tfTer4.setColumns(10);
		
		tfTer5 = new JTextField();
		tfTer5.addFocusListener(vremeFocusListener);
		tfTer5.setBackground(Color.WHITE);
		tfTer5.setHorizontalAlignment(SwingConstants.CENTER);
		tfTer5.setColumns(10);
		
		tfTer6 = new JTextField();
		tfTer6.addFocusListener(vremeFocusListener);
		tfTer6.setBackground(Color.WHITE);
		tfTer6.setHorizontalAlignment(SwingConstants.CENTER);
		tfTer6.setColumns(10);
		
		tfTer7 = new JTextField();
		tfTer7.addFocusListener(vremeFocusListener);
		tfTer7.setBackground(Color.WHITE);
		tfTer7.setHorizontalAlignment(SwingConstants.CENTER);
		tfTer7.setColumns(10);
		
		tfTer8 = new JTextField();
		tfTer8.addFocusListener(vremeFocusListener);
		tfTer8.setBackground(Color.WHITE);
		tfTer8.setHorizontalAlignment(SwingConstants.CENTER);
		tfTer8.setColumns(10);
		
		tfTer9 = new JTextField();
		tfTer9.addFocusListener(vremeFocusListener);
		tfTer9.setBackground(Color.WHITE);
		tfTer9.setHorizontalAlignment(SwingConstants.CENTER);
		tfTer9.setColumns(10);
		
		tfTer10 = new JTextField();
		tfTer10.addFocusListener(vremeFocusListener);
		tfTer10.setBackground(Color.WHITE);
		tfTer10.setHorizontalAlignment(SwingConstants.CENTER);
		tfTer10.setColumns(10);
		
		chckbxTermin1 = new JCheckBox("Termin 1 u");
		chckbxTermin1.addItemListener(new ChckbxTermin1ItemListener());
		
		chckbxTermin2 = new JCheckBox("Termin 2 u");
		chckbxTermin2.addItemListener(new ChckbxTermin2ItemListener());
		
		chckbxTermin3 = new JCheckBox("Termin 3 u");
		chckbxTermin3.addItemListener(new ChckbxTermin3ItemListener());
		
		chckbxTermin4 = new JCheckBox("Termin 4 u");
		chckbxTermin4.addItemListener(new ChckbxTermin4ItemListener());
		
		chckbxTermin5 = new JCheckBox("Termin 5 u");
		chckbxTermin5.addItemListener(new ChckbxTermin5ItemListener());
		
		chckbxTermin6 = new JCheckBox("Termin 6 u");
		chckbxTermin6.addItemListener(new ChckbxTermin6ItemListener());
		
		chckbxTermin7 = new JCheckBox("Termin 7 u");
		chckbxTermin7.addItemListener(new ChckbxTermin7ItemListener());
		
		chckbxTermin8 = new JCheckBox("Termin 8 u");
		chckbxTermin8.addItemListener(new ChckbxTermin8ItemListener());
		
		chckbxTermin9 = new JCheckBox("Termin 9 u");
		chckbxTermin9.addItemListener(new ChckbxTermin9ItemListener());
		
		chckbxTermin10 = new JCheckBox("Termin 10 u");
		chckbxTermin10.addItemListener(new ChckbxTermin10ItemListener());
		
		daysGroup = new ButtonGroup();
		
		rdbtnPeriodicno = new JRadioButton("Periodi\u010Dno");
		daysGroup.add(rdbtnPeriodicno);
		rdbtnPeriodicno.addItemListener(new RdbtnPeriodicnoItemListener());
		
		lblDanima = new JLabel("Danima");
		
		cbDanima = new JComboBox<String>();
		cbDanima.addItemListener(new CbDanimaItemListener());
		cbDanima.setModel(new DefaultComboBoxModel<String>(new String[] 
				{"Radnim danima", "Vikendom", "Cele nedelje", "Odabranim danima"}));
		
		chckbxPon = new JCheckBox("Pon");
		
		chckbxUto = new JCheckBox("Uto");
		
		chckbxSre = new JCheckBox("Sre");
		
		chckbxCet = new JCheckBox("\u010Cet");
		
		chckbxPet = new JCheckBox("Pet");
		
		chckbxSub = new JCheckBox("Sub");
		
		chckbxNed = new JCheckBox("Ned");
		
		rdbtnTokomIntervala = new JRadioButton("Tokom intervala");
		daysGroup.add(rdbtnTokomIntervala);
		rdbtnTokomIntervala.addItemListener(new RdbtnJedanDanUItemListener());
		
		lblOdDana = new JLabel("Od");
		
		tfOdDatum = new JTextField();
		tfOdDatum.setBackground(Color.WHITE);
		tfOdDatum.setColumns(7);
		SpringLayout sl_contentPanel = new SpringLayout();
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, trajanjePanel, 0, SpringLayout.SOUTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, prioritetPanel, 0, SpringLayout.SOUTH, opcijePanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, prioritetPanel, 0, SpringLayout.WEST, opcijePanel);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, prioritetPanel, 65, SpringLayout.SOUTH, opcijePanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, prioritetPanel, 0, SpringLayout.EAST, opcijePanel);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, terminPanel, 0, SpringLayout.SOUTH, trajanjePanel);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, opcijePanel, 285, SpringLayout.NORTH, terminPanel);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, panelDani, 290, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, opcijePanel, 0, SpringLayout.NORTH, terminPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, opcijePanel, 0, SpringLayout.EAST, terminPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, opcijePanel, 250, SpringLayout.EAST, terminPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, terminPanel, 220, SpringLayout.EAST, panelDani);
		sl_contentPanel.putConstraint(SpringLayout.WEST, terminPanel, 0, SpringLayout.EAST, panelDani);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, trajanjePanel, 0, SpringLayout.SOUTH, panelDani);
		sl_contentPanel.putConstraint(SpringLayout.WEST, trajanjePanel, 0, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, trajanjePanel, 0, SpringLayout.EAST, panelDani);
		sl_contentPanel.putConstraint(SpringLayout.WEST, panelDani, 0, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, panelDani, 215, SpringLayout.WEST, titlePanel);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, terminPanel, 6, SpringLayout.SOUTH, titlePanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, titlePanel, 1, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, titlePanel, -1, SpringLayout.EAST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, panelDani, 42, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, titlePanel, 5, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, titlePanel, 36, SpringLayout.NORTH, contentPanel);
		contentPanel.setLayout(sl_contentPanel);
		contentPanel.add(titlePanel);
		contentPanel.add(trajanjePanel);
		SpringLayout sl_trajanjePanel = new SpringLayout();
		sl_trajanjePanel.putConstraint(SpringLayout.SOUTH, rdbtnVremeTrajanjaListe, 0, SpringLayout.SOUTH, trajanjePanel);
		sl_trajanjePanel.putConstraint(SpringLayout.NORTH, rdbtnZadatoVremeTrajanja, 5, SpringLayout.NORTH, trajanjePanel);
		sl_trajanjePanel.putConstraint(SpringLayout.NORTH, tfTrajanje, -5, SpringLayout.NORTH, lblTrajanje);
		sl_trajanjePanel.putConstraint(SpringLayout.WEST, tfTrajanje, 5, SpringLayout.EAST, lblTrajanje);
		sl_trajanjePanel.putConstraint(SpringLayout.NORTH, lblTrajanje, 5, SpringLayout.SOUTH, rdbtnZadatoVremeTrajanja);
		sl_trajanjePanel.putConstraint(SpringLayout.WEST, rdbtnVremeTrajanjaListe, 2, SpringLayout.WEST, trajanjePanel);
		sl_trajanjePanel.putConstraint(SpringLayout.EAST, rdbtnVremeTrajanjaListe, 185, SpringLayout.WEST, trajanjePanel);
		sl_trajanjePanel.putConstraint(SpringLayout.WEST, rdbtnZadatoVremeTrajanja, 2, SpringLayout.WEST, trajanjePanel);
		sl_trajanjePanel.putConstraint(SpringLayout.WEST, lblTrajanje, 23, SpringLayout.WEST, trajanjePanel);
		trajanjePanel.setLayout(sl_trajanjePanel);
		trajanjePanel.add(lblTrajanje);
		trajanjePanel.add(tfTrajanje);
		trajanjePanel.add(rdbtnZadatoVremeTrajanja);
		trajanjePanel.add(rdbtnVremeTrajanjaListe);
		
		sl_trajanjePanel.putConstraint(SpringLayout.NORTH, rdbtnVremeTrajanjaListe, 10, SpringLayout.SOUTH, rdbtnOgraniceno);
		sl_trajanjePanel.putConstraint(SpringLayout.NORTH, rdbtnOgraniceno, 10, SpringLayout.SOUTH, tfTrajanje);
		sl_trajanjePanel.putConstraint(SpringLayout.WEST, rdbtnOgraniceno, 0, SpringLayout.WEST, rdbtnZadatoVremeTrajanja);
		trajanjePanel.add(rdbtnOgraniceno);
		
		tfBrojPesama = new JTextField();
		sl_trajanjePanel.putConstraint(SpringLayout.NORTH, tfBrojPesama, -5, SpringLayout.NORTH, rdbtnOgraniceno);
		sl_trajanjePanel.putConstraint(SpringLayout.WEST, tfBrojPesama, 5, SpringLayout.EAST, rdbtnOgraniceno);
		trajanjePanel.add(tfBrojPesama);
		tfBrojPesama.setColumns(2);
		
		lblPesama = new JLabel("pesama");
		sl_trajanjePanel.putConstraint(SpringLayout.WEST, lblPesama, 5, SpringLayout.EAST, tfBrojPesama);
		sl_trajanjePanel.putConstraint(SpringLayout.SOUTH, lblPesama, 0, SpringLayout.SOUTH, rdbtnOgraniceno);
		trajanjePanel.add(lblPesama);
		contentPanel.add(panelDani);
		SpringLayout sl_panelDani = new SpringLayout();
		sl_panelDani.putConstraint(SpringLayout.NORTH, rdbtnPeriodicno, 5, SpringLayout.NORTH, panelDani);
		sl_panelDani.putConstraint(SpringLayout.WEST, tfOdDatum, 0, SpringLayout.EAST, lblOdDana);
		sl_panelDani.putConstraint(SpringLayout.WEST, lblOdDana, 0, SpringLayout.WEST, lblDanima);
		sl_panelDani.putConstraint(SpringLayout.WEST, lblDanima, 15, SpringLayout.WEST, panelDani);
		sl_panelDani.putConstraint(SpringLayout.WEST, rdbtnTokomIntervala, 2, SpringLayout.WEST, panelDani);
		sl_panelDani.putConstraint(SpringLayout.WEST, cbDanima, 1, SpringLayout.EAST, lblDanima);
		sl_panelDani.putConstraint(SpringLayout.EAST, cbDanima, -1, SpringLayout.EAST, panelDani);
		sl_panelDani.putConstraint(SpringLayout.NORTH, lblOdDana, 8, SpringLayout.SOUTH, rdbtnTokomIntervala);
		sl_panelDani.putConstraint(SpringLayout.NORTH, rdbtnTokomIntervala, 5, SpringLayout.SOUTH, chckbxCet);
		sl_panelDani.putConstraint(SpringLayout.NORTH, tfOdDatum, -6, SpringLayout.NORTH, lblOdDana);
		sl_panelDani.putConstraint(SpringLayout.NORTH, chckbxNed, 0, SpringLayout.SOUTH, chckbxSub);
		sl_panelDani.putConstraint(SpringLayout.WEST, chckbxNed, 0, SpringLayout.WEST, chckbxPet);
		sl_panelDani.putConstraint(SpringLayout.NORTH, chckbxSub, 0, SpringLayout.SOUTH, chckbxPet);
		sl_panelDani.putConstraint(SpringLayout.WEST, chckbxSub, 0, SpringLayout.WEST, chckbxPet);
		sl_panelDani.putConstraint(SpringLayout.NORTH, chckbxPet, 0, SpringLayout.NORTH, chckbxPon);
		sl_panelDani.putConstraint(SpringLayout.WEST, chckbxPet, 5, SpringLayout.EAST, chckbxPon);
		sl_panelDani.putConstraint(SpringLayout.WEST, chckbxPon, 0, SpringLayout.WEST, cbDanima);
		sl_panelDani.putConstraint(SpringLayout.WEST, chckbxSre, 0, SpringLayout.WEST, chckbxPon);
		sl_panelDani.putConstraint(SpringLayout.WEST, chckbxUto, 0, SpringLayout.WEST, chckbxPon);
		sl_panelDani.putConstraint(SpringLayout.NORTH, chckbxCet, 0, SpringLayout.SOUTH, chckbxSre);
		sl_panelDani.putConstraint(SpringLayout.WEST, chckbxCet, 0, SpringLayout.WEST, chckbxPon);
		sl_panelDani.putConstraint(SpringLayout.NORTH, chckbxSre, 0, SpringLayout.SOUTH, chckbxUto);
		sl_panelDani.putConstraint(SpringLayout.NORTH, chckbxUto, 0, SpringLayout.SOUTH, chckbxPon);
		sl_panelDani.putConstraint(SpringLayout.NORTH, chckbxPon, 5, SpringLayout.SOUTH, cbDanima);
		sl_panelDani.putConstraint(SpringLayout.NORTH, cbDanima, 3, SpringLayout.SOUTH, rdbtnPeriodicno);
		sl_panelDani.putConstraint(SpringLayout.NORTH, lblDanima, 8, SpringLayout.SOUTH, rdbtnPeriodicno);
		sl_panelDani.putConstraint(SpringLayout.WEST, rdbtnPeriodicno, 2, SpringLayout.WEST, panelDani);
		panelDani.setLayout(sl_panelDani);
		panelDani.add(rdbtnPeriodicno);
		panelDani.add(lblDanima);
		panelDani.add(cbDanima);
		panelDani.add(chckbxPon);
		panelDani.add(chckbxPet);
		panelDani.add(chckbxUto);
		panelDani.add(chckbxSre);
		panelDani.add(chckbxCet);
		panelDani.add(chckbxSub);
		panelDani.add(chckbxNed);
		panelDani.add(lblOdDana);
		panelDani.add(tfOdDatum);
		panelDani.add(rdbtnTokomIntervala);
		
		lblDoDana = new JLabel("Do");
		sl_panelDani.putConstraint(SpringLayout.WEST, lblDoDana, 0, SpringLayout.WEST, lblDanima);
		panelDani.add(lblDoDana);
		
		tfDoDatum = new JTextField();
		sl_panelDani.putConstraint(SpringLayout.NORTH, lblDoDana, 5, SpringLayout.NORTH, tfDoDatum);
		sl_panelDani.putConstraint(SpringLayout.NORTH, tfDoDatum, 0, SpringLayout.SOUTH, tfOdDatum);
		sl_panelDani.putConstraint(SpringLayout.WEST, tfDoDatum, 0, SpringLayout.WEST, tfOdDatum);
		panelDani.add(tfDoDatum);
		tfDoDatum.setColumns(7);
		
		lblOdU = new JLabel("u");
		sl_panelDani.putConstraint(SpringLayout.WEST, lblOdU, 0, SpringLayout.EAST, tfOdDatum);
		sl_panelDani.putConstraint(SpringLayout.SOUTH, lblOdU, 0, SpringLayout.SOUTH, lblOdDana);
		panelDani.add(lblOdU);
		
		lblDoU = new JLabel("u");
		sl_panelDani.putConstraint(SpringLayout.WEST, lblDoU, 0, SpringLayout.EAST, tfDoDatum);
		sl_panelDani.putConstraint(SpringLayout.SOUTH, lblDoU, 0, SpringLayout.SOUTH, lblDoDana);
		panelDani.add(lblDoU);
		
		vremeFocusListener = new VremeFocusListener();
		
		tfOdVreme = new JTextField();
		tfOdVreme.addFocusListener(vremeFocusListener);
		sl_panelDani.putConstraint(SpringLayout.NORTH, tfOdVreme, 0, SpringLayout.NORTH, tfOdDatum);
		sl_panelDani.putConstraint(SpringLayout.WEST, tfOdVreme, 0, SpringLayout.EAST, lblOdU);
		panelDani.add(tfOdVreme);
		tfOdVreme.setColumns(4);
		
		tfDoVreme = new JTextField();
		tfDoVreme.addFocusListener(vremeFocusListener);
		sl_panelDani.putConstraint(SpringLayout.NORTH, tfDoVreme, 0, SpringLayout.NORTH, tfDoDatum);
		sl_panelDani.putConstraint(SpringLayout.WEST, tfDoVreme, 0, SpringLayout.EAST, lblDoU);
		tfDoVreme.setColumns(4);
		panelDani.add(tfDoVreme);
		contentPanel.add(prioritetPanel);
		SpringLayout sl_prioritetPanel = new SpringLayout();
		sl_prioritetPanel.putConstraint(SpringLayout.NORTH, cbPrioritet, 0, SpringLayout.NORTH, prioritetPanel);
		sl_prioritetPanel.putConstraint(SpringLayout.WEST, cbPrioritet, 0, SpringLayout.WEST, prioritetPanel);
		sl_prioritetPanel.putConstraint(SpringLayout.EAST, cbPrioritet, 0, SpringLayout.EAST, prioritetPanel);
		prioritetPanel.setLayout(sl_prioritetPanel);
		prioritetPanel.add(cbPrioritet);
		contentPanel.add(terminPanel);
		SpringLayout sl_terminPanel = new SpringLayout();
		sl_terminPanel.putConstraint(SpringLayout.WEST, tfTer1, 10, SpringLayout.EAST, chckbxTermin1);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, tfTer1, -5, SpringLayout.NORTH, chckbxTermin1);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, lblNaSvakih, 6, SpringLayout.NORTH, tfNaSvakih);
		sl_terminPanel.putConstraint(SpringLayout.EAST, lblNaSvakih, -3, SpringLayout.WEST, tfNaSvakih);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, lblZadnjeEmitovanje, 6, SpringLayout.NORTH, tfZadnje);
		sl_terminPanel.putConstraint(SpringLayout.EAST, lblZadnjeEmitovanje, -3, SpringLayout.WEST, tfZadnje);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, tfNaSvakih, 0, SpringLayout.SOUTH, tfZadnje);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, tfZadnje, 0, SpringLayout.SOUTH, tfPrvo);
		sl_terminPanel.putConstraint(SpringLayout.WEST, tfNaSvakih, 0, SpringLayout.WEST, tfPrvo);
		sl_terminPanel.putConstraint(SpringLayout.WEST, tfZadnje, 0, SpringLayout.WEST, tfPrvo);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, tfPrvo, -6, SpringLayout.NORTH, lblPrvoEmitovanje);
		sl_terminPanel.putConstraint(SpringLayout.WEST, tfPrvo, 3, SpringLayout.EAST, lblPrvoEmitovanje);
		sl_terminPanel.putConstraint(SpringLayout.WEST, chckbxTermin1, 23, SpringLayout.WEST, terminPanel);
		sl_terminPanel.putConstraint(SpringLayout.WEST, lblPrvoEmitovanje, 34, SpringLayout.WEST, terminPanel);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, tfTer2, 0, SpringLayout.SOUTH, tfTer1);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, tfTer3, 0, SpringLayout.SOUTH, tfTer2);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, tfTer4, 0, SpringLayout.SOUTH, tfTer3);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, tfTer5, 0, SpringLayout.SOUTH, tfTer4);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, tfTer6, 0, SpringLayout.SOUTH, tfTer5);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, tfTer7, 0, SpringLayout.SOUTH, tfTer6);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, tfTer8, 0, SpringLayout.SOUTH, tfTer7);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, tfTer9, 0, SpringLayout.SOUTH, tfTer8);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, tfTer10, 0, SpringLayout.SOUTH, tfTer9);
		sl_terminPanel.putConstraint(SpringLayout.WEST, tfTer2, 0, SpringLayout.WEST, tfTer1);
		sl_terminPanel.putConstraint(SpringLayout.WEST, tfTer3, 0, SpringLayout.WEST, tfTer1);
		sl_terminPanel.putConstraint(SpringLayout.WEST, tfTer4, 0, SpringLayout.WEST, tfTer1);
		sl_terminPanel.putConstraint(SpringLayout.WEST, tfTer5, 0, SpringLayout.WEST, tfTer1);
		sl_terminPanel.putConstraint(SpringLayout.WEST, tfTer6, 0, SpringLayout.WEST, tfTer1);
		sl_terminPanel.putConstraint(SpringLayout.WEST, tfTer7, 0, SpringLayout.WEST, tfTer1);
		sl_terminPanel.putConstraint(SpringLayout.WEST, tfTer8, 0, SpringLayout.WEST, tfTer1);
		sl_terminPanel.putConstraint(SpringLayout.WEST, tfTer9, 0, SpringLayout.WEST, tfTer1);
		sl_terminPanel.putConstraint(SpringLayout.WEST, tfTer10, 0, SpringLayout.WEST, tfTer1);
		sl_terminPanel.putConstraint(SpringLayout.EAST, tfTer2, 0, SpringLayout.EAST, tfTer1);
		sl_terminPanel.putConstraint(SpringLayout.EAST, tfTer3, 0, SpringLayout.EAST, tfTer1);
		sl_terminPanel.putConstraint(SpringLayout.EAST, tfTer4, 0, SpringLayout.EAST, tfTer1);
		sl_terminPanel.putConstraint(SpringLayout.EAST, tfTer5, 0, SpringLayout.EAST, tfTer1);
		sl_terminPanel.putConstraint(SpringLayout.EAST, tfTer6, 0, SpringLayout.EAST, tfTer1);
		sl_terminPanel.putConstraint(SpringLayout.EAST, tfTer7, 0, SpringLayout.EAST, tfTer1);
		sl_terminPanel.putConstraint(SpringLayout.EAST, tfTer8, 0, SpringLayout.EAST, tfTer1);
		sl_terminPanel.putConstraint(SpringLayout.EAST, tfTer9, 0, SpringLayout.EAST, tfTer1);
		sl_terminPanel.putConstraint(SpringLayout.EAST, tfTer10, 0, SpringLayout.EAST, tfTer1);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, chckbxTermin2, 5, SpringLayout.NORTH, tfTer2);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, chckbxTermin3, 5, SpringLayout.NORTH, tfTer3);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, chckbxTermin4, 5, SpringLayout.NORTH, tfTer4);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, chckbxTermin5, 5, SpringLayout.NORTH, tfTer5);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, chckbxTermin6, 5, SpringLayout.NORTH, tfTer6);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, chckbxTermin7, 5, SpringLayout.NORTH, tfTer7);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, chckbxTermin8, 5, SpringLayout.NORTH, tfTer8);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, chckbxTermin9, 5, SpringLayout.NORTH, tfTer9);
		sl_terminPanel.putConstraint(SpringLayout.NORTH, chckbxTermin10, 5, SpringLayout.NORTH, tfTer10);
		sl_terminPanel.putConstraint(SpringLayout.WEST, chckbxTermin2, 0, SpringLayout.WEST, chckbxTermin1);
		sl_terminPanel.putConstraint(SpringLayout.WEST, chckbxTermin3, 0, SpringLayout.WEST, chckbxTermin1);
		sl_terminPanel.putConstraint(SpringLayout.WEST, chckbxTermin4, 0, SpringLayout.WEST, chckbxTermin1);
		sl_terminPanel.putConstraint(SpringLayout.WEST, chckbxTermin5, 0, SpringLayout.WEST, chckbxTermin1);
		sl_terminPanel.putConstraint(SpringLayout.WEST, chckbxTermin6, 0, SpringLayout.WEST, chckbxTermin1);
		sl_terminPanel.putConstraint(SpringLayout.WEST, chckbxTermin7, 0, SpringLayout.WEST, chckbxTermin1);
		sl_terminPanel.putConstraint(SpringLayout.WEST, chckbxTermin8, 0, SpringLayout.WEST, chckbxTermin1);
		sl_terminPanel.putConstraint(SpringLayout.WEST, chckbxTermin9, 0, SpringLayout.WEST, chckbxTermin1);
		sl_terminPanel.putConstraint(SpringLayout.WEST, chckbxTermin10, 0, SpringLayout.WEST, chckbxTermin1);
		terminPanel.setLayout(sl_terminPanel);
		terminPanel.add(lblZadnjeEmitovanje);
		terminPanel.add(lblPrvoEmitovanje);
		terminPanel.add(lblNaSvakih);
		terminPanel.add(tfZadnje);
		terminPanel.add(tfPrvo);
		terminPanel.add(tfNaSvakih);
		terminPanel.add(chckbxTermin1);
		terminPanel.add(chckbxTermin2);
		terminPanel.add(chckbxTermin3);
		terminPanel.add(chckbxTermin4);
		terminPanel.add(chckbxTermin5);
		terminPanel.add(chckbxTermin6);
		terminPanel.add(chckbxTermin7);
		terminPanel.add(chckbxTermin8);
		terminPanel.add(chckbxTermin9);
		terminPanel.add(chckbxTermin10);
		terminPanel.add(tfTer1);
		terminPanel.add(tfTer2);
		terminPanel.add(tfTer3);
		terminPanel.add(tfTer4);
		terminPanel.add(tfTer5);
		terminPanel.add(tfTer6);
		terminPanel.add(tfTer7);
		terminPanel.add(tfTer8);
		terminPanel.add(tfTer9);
		terminPanel.add(tfTer10);
		contentPanel.add(opcijePanel);
		
		chckbxCuvatiSadrajTermina = new JCheckBox("<html>Čuvati sadržaj termina (Ne krojiti termin već ga pomeriti u vreme kada ga je moguće emitovati u celosti)</html>");
		chckbxCuvatiSadrajTermina.setVerticalAlignment(SwingConstants.TOP);
		sl_opcijePanel.putConstraint(SpringLayout.NORTH, chckbxCuvatiSadrajTermina, 5, SpringLayout.SOUTH, chckbxCrossfade);
		sl_opcijePanel.putConstraint(SpringLayout.SOUTH, chckbxCuvatiSadrajTermina, 72, SpringLayout.SOUTH, chckbxCrossfade);
		sl_opcijePanel.putConstraint(SpringLayout.WEST, chckbxCuvatiSadrajTermina, 0, SpringLayout.WEST, chckbxCrossfade);
		sl_opcijePanel.putConstraint(SpringLayout.EAST, chckbxCuvatiSadrajTermina, -5, SpringLayout.EAST, opcijePanel);
		opcijePanel.add(chckbxCuvatiSadrajTermina);
		
		lblNajavnaSpica = new JLabel("Najavna špica");
		sl_opcijePanel.putConstraint(SpringLayout.WEST, lblNajavnaSpica, 0, SpringLayout.WEST, chckbxCrossfade);
		opcijePanel.add(lblNajavnaSpica);
		
		lblOdjavnaSpica = new JLabel("Odjavna špica");
		sl_opcijePanel.putConstraint(SpringLayout.WEST, lblOdjavnaSpica, 10, SpringLayout.WEST, opcijePanel);
		opcijePanel.add(lblOdjavnaSpica);
		
		tfNajava = new JSongField();
		tfNajava.setEditable(false);
		sl_opcijePanel.putConstraint(SpringLayout.NORTH, lblOdjavnaSpica, 10, SpringLayout.SOUTH, tfNajava);
		sl_opcijePanel.putConstraint(SpringLayout.NORTH, tfNajava, 0, SpringLayout.SOUTH, lblNajavnaSpica);
		sl_opcijePanel.putConstraint(SpringLayout.EAST, tfNajava, -10, SpringLayout.EAST, opcijePanel);
		sl_opcijePanel.putConstraint(SpringLayout.WEST, tfNajava, 0, SpringLayout.WEST, chckbxCrossfade);
		opcijePanel.add(tfNajava);
		tfNajava.setColumns(10);
		
		tfOdjava = new JSongField();
		tfOdjava.setEditable(false);
		sl_opcijePanel.putConstraint(SpringLayout.NORTH, tfOdjava, 0, SpringLayout.SOUTH, lblOdjavnaSpica);
		sl_opcijePanel.putConstraint(SpringLayout.WEST, tfOdjava, 0, SpringLayout.WEST, lblOdjavnaSpica);
		sl_opcijePanel.putConstraint(SpringLayout.EAST, tfOdjava, -10, SpringLayout.EAST, opcijePanel);
		opcijePanel.add(tfOdjava);
		tfOdjava.setColumns(10);
		
		chckbxPisiUIzvetaj = new JCheckBox("Piši u izveštaj");
		sl_opcijePanel.putConstraint(SpringLayout.NORTH, chckbxPisiUIzvetaj, 5, SpringLayout.SOUTH, chckbxCuvatiSadrajTermina);
		sl_opcijePanel.putConstraint(SpringLayout.WEST, chckbxPisiUIzvetaj, 0, SpringLayout.WEST, chckbxCrossfade);
		opcijePanel.add(chckbxPisiUIzvetaj);
		
		chckbxPostujRedosled = new JCheckBox("<html>Poštuj redosled pesama</html>");
		sl_opcijePanel.putConstraint(SpringLayout.NORTH, chckbxPostujRedosled, 5, SpringLayout.SOUTH, chckbxPisiUIzvetaj);
		sl_opcijePanel.putConstraint(SpringLayout.NORTH, lblNajavnaSpica, 10, SpringLayout.SOUTH, chckbxPostujRedosled);
		sl_opcijePanel.putConstraint(SpringLayout.WEST, chckbxPostujRedosled, 0, SpringLayout.WEST, chckbxCrossfade);
		opcijePanel.add(chckbxPostujRedosled);
		{
			okButton = new JButton("U redu");
			sl_contentPanel.putConstraint(SpringLayout.WEST, okButton, 5, SpringLayout.EAST, terminPanel);
			sl_contentPanel.putConstraint(SpringLayout.SOUTH, okButton, -5, SpringLayout.SOUTH, contentPanel);
			
			chckbxPeriodicno = new JCheckBox("Periodično emitovanje");
			chckbxPeriodicno.addItemListener(new ChckbxPeriodicnoItemListener());
			sl_terminPanel.putConstraint(SpringLayout.NORTH, chckbxPeriodicno, 0, SpringLayout.NORTH, terminPanel);
			sl_terminPanel.putConstraint(SpringLayout.NORTH, lblPrvoEmitovanje, 8, SpringLayout.SOUTH, chckbxPeriodicno);
			sl_terminPanel.putConstraint(SpringLayout.WEST, chckbxPeriodicno, 2, SpringLayout.WEST, terminPanel);
			terminPanel.add(chckbxPeriodicno);
			
			chckbxUZadatomTerminu = new JCheckBox("U zadatim terminima");
			chckbxUZadatomTerminu.addItemListener(new ChckbxUZadatomTerminuItemListener());
			sl_terminPanel.putConstraint(SpringLayout.NORTH, chckbxUZadatomTerminu, 10, SpringLayout.SOUTH, lblNaSvakih);
			sl_terminPanel.putConstraint(SpringLayout.NORTH, chckbxTermin1, 8, SpringLayout.SOUTH, chckbxUZadatomTerminu);
			sl_terminPanel.putConstraint(SpringLayout.WEST, chckbxUZadatomTerminu, 0, SpringLayout.WEST, chckbxPeriodicno);
			terminPanel.add(chckbxUZadatomTerminu);
			contentPanel.add(okButton);
			okButton.addActionListener(new OkButtonActionListener());
			okButton.setActionCommand("OK");
			getRootPane().setDefaultButton(okButton);
		}
		{
			JButton cancelButton = new JButton("Otkaži");
			sl_contentPanel.putConstraint(SpringLayout.EAST, okButton, -8, SpringLayout.WEST, cancelButton);
			sl_contentPanel.putConstraint(SpringLayout.SOUTH, cancelButton, 0, SpringLayout.SOUTH, okButton);
			sl_contentPanel.putConstraint(SpringLayout.EAST, cancelButton, 0, SpringLayout.EAST, contentPanel);
			contentPanel.add(cancelButton);
			cancelButton.addActionListener(new CancelButtonActionListener());
			cancelButton.setActionCommand("Cancel");
		}
	}
	private class CancelButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			shouldSave = false;
			setVisible(false);
		}
	}
	private class RdbtnPeriodicnoItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent ie) {
			if(ie.getStateChange() == ItemEvent.SELECTED){
				//rdbtnTokomIntervala.setSelected(false);
				lblOdDana.setEnabled(false);
				tfOdDatum.setEnabled(false);
				lblOdU.setEnabled(false);
				tfOdVreme.setEnabled(false);
				lblDoDana.setEnabled(false);
				tfDoDatum.setEnabled(false);
				lblDoU.setEnabled(false);
				tfDoVreme.setEnabled(false);
				lblDanima.setEnabled(true);
				cbDanima.setEnabled(true);
				chckbxPon.setEnabled(true);
				chckbxUto.setEnabled(true);
				chckbxSre.setEnabled(true);
				chckbxCet.setEnabled(true);
				chckbxPet.setEnabled(true);
				chckbxSub.setEnabled(true);
				chckbxNed.setEnabled(true);
				return;
			}
		}
	}
	private class RdbtnJedanDanUItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent ie) {
			if(ie.getStateChange() == ItemEvent.SELECTED){
				//rdbtnPeriodicno.setSelected(false);
				lblOdDana.setEnabled(true);
				tfOdDatum.setEnabled(true);
				lblOdU.setEnabled(true);
				tfOdVreme.setEnabled(true);
				lblDoDana.setEnabled(true);
				tfDoDatum.setEnabled(true);
				lblDoU.setEnabled(true);
				tfDoVreme.setEnabled(true);
				lblDanima.setEnabled(false);
				cbDanima.setEnabled(false);
				chckbxPon.setEnabled(false);
				chckbxUto.setEnabled(false);
				chckbxSre.setEnabled(false);
				chckbxCet.setEnabled(false);
				chckbxPet.setEnabled(false);
				chckbxSub.setEnabled(false);
				chckbxNed.setEnabled(false);
			}
		}
	}
	private class RdbtnZadatoVremeTrajanjaItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange() == ItemEvent.SELECTED){
				lblTrajanje.setEnabled(true);
				tfTrajanje.setEnabled(true);
				lblPesama.setEnabled(false);
				tfBrojPesama.setEnabled(false);
			}
		}
	}
	private class RdbtnVremeTrajanjaListeItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange() == ItemEvent.SELECTED){
				lblTrajanje.setEnabled(false);
				tfTrajanje.setEnabled(false);
				lblPesama.setEnabled(false);
				tfBrojPesama.setEnabled(false);
			}
		}
	}
	private class OkButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			int temp;
			String str;
			cat = new MusicCategory();
			cat.ime = getTitle();
			// Provera dnevnog emitovanja
			if(rdbtnPeriodicno.isSelected()){
				cat.ponedeljak = chckbxPon.isSelected();
				cat.utorak = chckbxUto.isSelected();
				cat.sreda = chckbxSre.isSelected();
				cat.cetvrtak = chckbxCet.isSelected();
				cat.petak = chckbxPet.isSelected();
				cat.subota = chckbxSub.isSelected();
				cat.nedelja = chckbxNed.isSelected();
				if(!chckbxPon.isSelected() && !chckbxUto.isSelected() && !chckbxSre.isSelected() && 
						!chckbxCet.isSelected() && !chckbxPet.isSelected() && 
						!chckbxSub.isSelected() && !chckbxNed.isSelected()){
					PlayerWin.getErrDlg().showError("Morate izabrati barem jedan<br/>dan u nedelji za emitovanje.");
					chckbxPon.requestFocusInWindow();
					return;
				}
				cat.begin = -1;
				cat.end = -1;
			}else{
				// Pocetni datum i vreme
				try{
					str = tfOdDatum.getText().trim();
					str = str.replace(',', '.');
					str = str.replace('/', '.');
					cal.setTime(sdf.parse(str));
				}catch(ParseException e){
					PlayerWin.getErrDlg().showError("Pogrešno unešen datum početka emitovanja.");
					tfOdDatum.requestFocusInWindow();
					return;
				}
				temp = parseTime(tfOdVreme.getText());
				if(temp == -1){
					PlayerWin.getErrDlg().showError("Pogrešno unešeno vreme početka emitovanja.");
					tfOdVreme.requestFocusInWindow();
					return;
				}
				cal.set(Calendar.HOUR_OF_DAY, temp / 60);
				cal.set(Calendar.MINUTE, temp % 60);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				cat.begin = cal.getTimeInMillis();
				// Krajnji datum i vreme
				try{
					str = tfDoDatum.getText().trim();
					str = str.replace(',', '.');
					str = str.replace('/', '.');
					cal.setTime(sdf.parse(str));
				}catch(ParseException e){
					PlayerWin.getErrDlg().showError("Pogrešno unešen datum završetka emitovanja.");
					tfDoDatum.requestFocusInWindow();
					return;
				}
				temp = parseTime(tfDoVreme.getText());
				if(temp == -1){
					PlayerWin.getErrDlg().showError("Pogrešno unešeno vreme završetka emitovanja.");
					tfDoVreme.requestFocusInWindow();
					return;
				}
				cal.set(Calendar.HOUR_OF_DAY, temp / 60);
				cal.set(Calendar.MINUTE, temp % 60);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				cat.end = cal.getTimeInMillis();
			}
			// Provera trajanja
			if(rdbtnZadatoVremeTrajanja.isSelected()){
				temp = parseTime(tfTrajanje.getText());
				if(temp == -1){
					PlayerWin.getErrDlg().showError("Pogrešno unešeno trajanje emitovanja.");
					tfTrajanje.requestFocusInWindow();
					return;
				}
				cat.trajanje = temp;
			}else if(rdbtnOgraniceno.isSelected()){
				try{
					temp = Integer.parseInt(tfBrojPesama.getText());
					if(temp < 1) throw new NumberFormatException();
				}catch(NumberFormatException nfe){
					PlayerWin.getErrDlg().showError("Pogrešno unešen broj pesama.");
					tfBrojPesama.requestFocusInWindow();
					return;
				}
				cat.trajanje = -temp;
			}else cat.trajanje = 0;
			// Provera periodicnog i terminskog emitovanja
			if(chckbxPeriodicno.isSelected()){
				cat.periodicno = true;
				// Prvo emitovanje
				temp = parseTime(tfPrvo.getText());
				if(temp == -1){
					PlayerWin.getErrDlg().showError("Pogrešno unešeno vreme prvog emitovanja u danu.");
					tfPrvo.requestFocusInWindow();
					return;
				}
				cat.prvo = temp;
				// Zadnje emitovanje
				temp = parseTime(tfZadnje.getText());
				if(temp == -1){
					PlayerWin.getErrDlg().showError("Pogrešno unešeno vreme poslednjeg emitovanja u danu.");
					tfZadnje.requestFocusInWindow();
					return;
				}
				cat.zadnje = temp;
				// Interval
				temp = parseTime(tfNaSvakih.getText());
				if(temp == -1){
					PlayerWin.getErrDlg().showError("Pogrešno unešeno vreme intervala emitovanja u danu.");
					tfNaSvakih.requestFocusInWindow();
					return;
				}
				cat.na_svakih = temp;
			}else{
				cat.periodicno = false;
				cat.prvo = -1;
				cat.zadnje = -1;
				cat.na_svakih = -1;
			}
			if(chckbxUZadatomTerminu.isSelected()){
				cat.terminsko = true;
				// Termin 1
				if(chckbxTermin1.isSelected()){
					temp = parseTime(tfTer1.getText());
					if(temp == -1){
						PlayerWin.getErrDlg().showError("Pogrešno unešeno vreme prvog termina.");
						tfTer1.requestFocusInWindow();
						return;
					}
					cat.termin1 = temp;
				}else cat.termin1 = -1;
				// Termin 2
				if(chckbxTermin2.isSelected()){
					temp = parseTime(tfTer2.getText());
					if(temp == -1){
						PlayerWin.getErrDlg().showError("Pogrešno unešeno vreme drugog termina.");
						tfTer2.requestFocusInWindow();
						return;
					}
					cat.termin2 = temp;
				}else cat.termin2 = -1;
				// Termin 3
				if(chckbxTermin3.isSelected()){
					temp = parseTime(tfTer3.getText());
					if(temp == -1){
						PlayerWin.getErrDlg().showError("Pogrešno unešeno vreme trećeg termina.");
						tfTer3.requestFocusInWindow();
						return;
					}
					cat.termin3 = temp;
				}else cat.termin3 = -1;
				// Termin 4
				if(chckbxTermin4.isSelected()){
					temp = parseTime(tfTer4.getText());
					if(temp == -1){
						PlayerWin.getErrDlg().showError("Pogrešno unešeno vreme četvrtog termina.");
						tfTer4.requestFocusInWindow();
						return;
					}
					cat.termin4 = temp;
				}else cat.termin4 = -1;
				// Termin 5
				if(chckbxTermin5.isSelected()){
					temp = parseTime(tfTer5.getText());
					if(temp == -1){
						PlayerWin.getErrDlg().showError("Pogrešno unešeno vreme petog termina.");
						tfTer5.requestFocusInWindow();
						return;
					}
					cat.termin5 = temp;
				}else cat.termin5 = -1;
				// Termin 6
				if(chckbxTermin6.isSelected()){
					temp = parseTime(tfTer6.getText());
					if(temp == -1){
						PlayerWin.getErrDlg().showError("Pogrešno unešeno vreme šestog termina.");
						tfTer6.requestFocusInWindow();
						return;
					}
					cat.termin6 = temp;
				}else cat.termin6 = -1;
				// Termin 7
				if(chckbxTermin7.isSelected()){
					temp = parseTime(tfTer7.getText());
					if(temp == -1){
						PlayerWin.getErrDlg().showError("Pogrešno unešeno vreme sedmog termina.");
						tfTer7.requestFocusInWindow();
						return;
					}
					cat.termin7 = temp;
				}else cat.termin7 = -1;
				// Termin 8
				if(chckbxTermin8.isSelected()){
					temp = parseTime(tfTer8.getText());
					if(temp == -1){
						PlayerWin.getErrDlg().showError("Pogrešno unešeno vreme osmog termina.");
						tfTer8.requestFocusInWindow();
						return;
					}
					cat.termin8 = temp;
				}else cat.termin8 = -1;
				// Termin 9
				if(chckbxTermin9.isSelected()){
					temp = parseTime(tfTer9.getText());
					if(temp == -1){
						PlayerWin.getErrDlg().showError("Pogrešno unešeno vreme devetog termina.");
						tfTer9.requestFocusInWindow();
						return;
					}
					cat.termin9 = temp;
				}else cat.termin9 = -1;
				// Termin 10
				if(chckbxTermin10.isSelected()){
					temp = parseTime(tfTer10.getText());
					if(temp == -1){
						PlayerWin.getErrDlg().showError("Pogrešno unešeno vreme desetog termina.");
						tfTer10.requestFocusInWindow();
						return;
					}
					cat.termin10 = temp;
				}else cat.termin10 = -1;
			}else{
				cat.termin1 = -1;
				cat.termin2 = -1;
				cat.termin3 = -1;
				cat.termin4 = -1;
				cat.termin5 = -1;
				cat.termin6 = -1;
				cat.termin7 = -1;
				cat.termin8 = -1;
				cat.termin9 = -1;
				cat.termin10 = -1;
				cat.terminsko = false;
			}
			// Poslednja provera termina
			if((cat.termin1 == -1) && (cat.termin2 == -1) && (cat.termin3 == -1) && 
					(cat.termin4 == -1) && (cat.termin5 == -1) && (cat.termin6 == -1) && 
					(cat.termin7 == -1) && (cat.termin8 == -1) && (cat.termin9 == -1) && 
					(cat.termin10 == -1) && (cat.prvo == -1) && (cat.zadnje == -1) && 
					(cat.na_svakih == -1)){
				errorDlg.showError("Morate odabrati barem jedan termin ili interval emitovanja.");
				chckbxPeriodicno.requestFocusInWindow();
				return;
			}
			// Prioritet
			if(cbPrioritet.getSelectedIndex() == -1){
				errorDlg.showError("Morate odabrati prioritet emitovanja.");
				cbPrioritet.requestFocusInWindow();
				return;
			}else cat.prioritet = cbPrioritet.getSelectedIndex() + 1;
			// Krosfejd
			cat.crossfade = chckbxCrossfade.isSelected();
			// Cuvanje celosti sadrzaja
			cat.cuvajSadrzaj = chckbxCuvatiSadrajTermina.isSelected();
			// Da li da pise u izvestaj
			cat.pisiUIzvestaj = chckbxPisiUIzvetaj.isSelected();
			// Postovanje redosleda
			cat.postujRedosled = chckbxPostujRedosled.isSelected();
			//Spice
			if(tfNajava.getItem() != null)
				cat.najavnaSpica = tfNajava.getItem().fullPath;
			else cat.najavnaSpica = null;
			if(tfOdjava.getItem() != null)
				cat.odjavnaSpica = tfOdjava.getItem().fullPath;
			else cat.odjavnaSpica = null;
			shouldSave = true;
			setVisible(false);
		}
	}
	private class CbDanimaItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent ie) {
			if(ie.getStateChange() == ItemEvent.SELECTED){
				switch(cbDanima.getSelectedIndex()){
				case 0:
					chckbxPon.setSelected(true);
					chckbxUto.setSelected(true);
					chckbxSre.setSelected(true);
					chckbxCet.setSelected(true);
					chckbxPet.setSelected(true);
					chckbxSub.setSelected(false);
					chckbxNed.setSelected(false);
					break;
				case 1:
					chckbxPon.setSelected(false);
					chckbxUto.setSelected(false);
					chckbxSre.setSelected(false);
					chckbxCet.setSelected(false);
					chckbxPet.setSelected(false);
					chckbxSub.setSelected(true);
					chckbxNed.setSelected(true);
					break;
				case 2:
					chckbxPon.setSelected(true);
					chckbxUto.setSelected(true);
					chckbxSre.setSelected(true);
					chckbxCet.setSelected(true);
					chckbxPet.setSelected(true);
					chckbxSub.setSelected(true);
					chckbxNed.setSelected(true);
					break;
				case 3:
					chckbxPon.setSelected(false);
					chckbxUto.setSelected(false);
					chckbxSre.setSelected(false);
					chckbxCet.setSelected(false);
					chckbxPet.setSelected(false);
					chckbxSub.setSelected(false);
					chckbxNed.setSelected(false);
					break;
				default:
				}
			}
		}
	}
	private class ChckbxPeriodicnoItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent event) {
			if(event.getStateChange() == ItemEvent.SELECTED){
				lblPrvoEmitovanje.setEnabled(true);
				tfPrvo.setEnabled(true);
				lblZadnjeEmitovanje.setEnabled(true);
				tfZadnje.setEnabled(true);
				lblNaSvakih.setEnabled(true);
				tfNaSvakih.setEnabled(true);
			}
			if(event.getStateChange() == ItemEvent.DESELECTED){
				lblPrvoEmitovanje.setEnabled(false);
				tfPrvo.setEnabled(false);
				lblZadnjeEmitovanje.setEnabled(false);
				tfZadnje.setEnabled(false);
				lblNaSvakih.setEnabled(false);
				tfNaSvakih.setEnabled(false);
			}
		}
	}
	private class ChckbxUZadatomTerminuItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent event) {
			if(event.getStateChange() == ItemEvent.SELECTED){
				chckbxTermin1.setEnabled(true);
				if(chckbxTermin1.isSelected())
					tfTer1.setEnabled(true);
				chckbxTermin2.setEnabled(true);
				if(chckbxTermin2.isSelected())
					tfTer2.setEnabled(true);
				chckbxTermin3.setEnabled(true);
				if(chckbxTermin3.isSelected())
					tfTer3.setEnabled(true);
				chckbxTermin4.setEnabled(true);
				if(chckbxTermin4.isSelected())
					tfTer4.setEnabled(true);
				chckbxTermin5.setEnabled(true);
				if(chckbxTermin5.isSelected())
					tfTer5.setEnabled(true);
				chckbxTermin6.setEnabled(true);
				if(chckbxTermin6.isSelected())
					tfTer6.setEnabled(true);
				chckbxTermin7.setEnabled(true);
				if(chckbxTermin7.isSelected())
					tfTer7.setEnabled(true);
				chckbxTermin8.setEnabled(true);
				if(chckbxTermin8.isSelected())
					tfTer8.setEnabled(true);
				chckbxTermin9.setEnabled(true);
				if(chckbxTermin9.isSelected())
					tfTer9.setEnabled(true);
				chckbxTermin10.setEnabled(true);
				if(chckbxTermin10.isSelected())
					tfTer10.setEnabled(true);
			}
			if(event.getStateChange() == ItemEvent.DESELECTED){
				chckbxTermin1.setEnabled(false);
				tfTer1.setEnabled(false);
				chckbxTermin2.setEnabled(false);
				tfTer2.setEnabled(false);
				chckbxTermin3.setEnabled(false);
				tfTer3.setEnabled(false);
				chckbxTermin4.setEnabled(false);
				tfTer4.setEnabled(false);
				chckbxTermin5.setEnabled(false);
				tfTer5.setEnabled(false);
				chckbxTermin6.setEnabled(false);
				tfTer6.setEnabled(false);
				chckbxTermin7.setEnabled(false);
				tfTer7.setEnabled(false);
				chckbxTermin8.setEnabled(false);
				tfTer8.setEnabled(false);
				chckbxTermin9.setEnabled(false);
				tfTer9.setEnabled(false);
				chckbxTermin10.setEnabled(false);
				tfTer10.setEnabled(false);
			}
		}
	}
	private class VremeFocusListener extends FocusAdapter {
		@Override
		public void focusLost(FocusEvent e) {
			JTextField source = (JTextField)e.getSource();
			int time = parseTime(source.getText().trim());
			source.setText(formatTime(time));
		}
	}
	private class ChckbxTermin1ItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange() == ItemEvent.SELECTED)
				tfTer1.setEnabled(true);
			if(e.getStateChange() == ItemEvent.DESELECTED)
				tfTer1.setEnabled(false);
		}
	}
	private class ChckbxTermin2ItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange() == ItemEvent.SELECTED)
				tfTer2.setEnabled(true);
			if(e.getStateChange() == ItemEvent.DESELECTED)
				tfTer2.setEnabled(false);
		}
	}
	private class ChckbxTermin3ItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange() == ItemEvent.SELECTED)
				tfTer3.setEnabled(true);
			if(e.getStateChange() == ItemEvent.DESELECTED)
				tfTer3.setEnabled(false);
		}
	}
	private class ChckbxTermin4ItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange() == ItemEvent.SELECTED)
				tfTer4.setEnabled(true);
			if(e.getStateChange() == ItemEvent.DESELECTED)
				tfTer4.setEnabled(false);
		}
	}
	private class ChckbxTermin5ItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange() == ItemEvent.SELECTED)
				tfTer5.setEnabled(true);
			if(e.getStateChange() == ItemEvent.DESELECTED)
				tfTer5.setEnabled(false);
		}
	}
	private class ChckbxTermin6ItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange() == ItemEvent.SELECTED)
				tfTer6.setEnabled(true);
			if(e.getStateChange() == ItemEvent.DESELECTED)
				tfTer6.setEnabled(false);
		}
	}
	private class ChckbxTermin7ItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange() == ItemEvent.SELECTED)
				tfTer7.setEnabled(true);
			if(e.getStateChange() == ItemEvent.DESELECTED)
				tfTer7.setEnabled(false);
		}
	}
	private class ChckbxTermin8ItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange() == ItemEvent.SELECTED)
				tfTer8.setEnabled(true);
			if(e.getStateChange() == ItemEvent.DESELECTED)
				tfTer8.setEnabled(false);
		}
	}
	private class ChckbxTermin9ItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange() == ItemEvent.SELECTED)
				tfTer9.setEnabled(true);
			if(e.getStateChange() == ItemEvent.DESELECTED)
				tfTer9.setEnabled(false);
		}
	}
	private class ChckbxTermin10ItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange() == ItemEvent.SELECTED)
				tfTer10.setEnabled(true);
			if(e.getStateChange() == ItemEvent.DESELECTED)
				tfTer10.setEnabled(false);
		}
	}
	private class RdbtnOgranicenoItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange() == ItemEvent.SELECTED){
				lblTrajanje.setEnabled(false);
				tfTrajanje.setEnabled(false);
				lblPesama.setEnabled(true);
				tfBrojPesama.setEnabled(true);
			}
		}
	}
}
