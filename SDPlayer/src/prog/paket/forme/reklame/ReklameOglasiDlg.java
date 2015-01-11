package prog.paket.forme.reklame;

import java.awt.BorderLayout;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.JList;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JButton;

import prog.paket.baza.struct.menutree.TraitChange;
import prog.paket.dodaci.ContentEvent;
import prog.paket.dodaci.ContentListener;
import prog.paket.dodaci.JSongField;
import prog.paket.dodaci.ListJItem;
import prog.paket.dodaci.SDModalResult;
import prog.paket.dodaci.TimeEntry;
import prog.paket.playlist.generator.CatNameDialog;
import prog.paket.playlist.generator.ErrorInfoDialog;
import prog.paket.playlist.generator.PlayerWin;
import prog.paket.playlist.generator.YesNoForm;
import prog.paket.playlist.generator.struct.RekFileFilter;

import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.BoxLayout;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.ListSelectionModel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.border.TitledBorder;
import javax.swing.JTextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Color;

public class ReklameOglasiDlg extends JFrame {

	private static final long serialVersionUID = -8175772742122569866L;

	private JPanel contentPane;
	public JScrollPane scrollPane;
	public JPanel intervalPanel;
	public ScheduledItemsList itemsList;
	public JPanel upperPanel;
	public JComboBox<ScheduledItemsType> cbLists;
	public JButton btnNew;
	public JButton btnEdit;
	public JButton btnDelete;
	public JLabel lblPocDatum;
	public JTextField tfPocDatum;
	public JLabel lblKrajDatum;
	public JTextField tfKrajDatum;
	public JLabel lblPocVreme;
	public JLabel lblKrajVreme;
	public JButton btnSnimi;
	public JTextField tfPocVreme;
	public JTextField tfKrajVreme;
	public JLabel lblNajavnapica;
	public JLabel lblOdjavnapica;
	public JSongField sfNajava;
	public JSongField sfOdjava;

	private boolean canProccessList = true;
	private boolean canProccessComboBox = true;
	private boolean canProccessTimes = true;
	private boolean canProccessIntroOutro = true;
	private SimpleDateFormat sdf = new SimpleDateFormat("d.M.yyyy");
	private VremeFocusListener vremeFocusListener = new VremeFocusListener();
	private DatumFocusListener datumFocusListener = new DatumFocusListener();
	private Calendar cal = new GregorianCalendar();

	public JPanel lowerSettingsPanel;
	public JPanel setTimesPanel;
	public JPanel giveTimesPanel;
	public JPanel timesPanel;
	public JCheckBox chckbxPon;
	public JCheckBox chckbxUto;
	public JCheckBox chckbxSre;
	public JCheckBox chckbxCet;
	public JCheckBox chckbxPet;
	public JCheckBox chckbxSub;
	public JCheckBox chckbxNed;
	public JScrollPane scrollPane_1;
	public JList<TimeEntry> timesList;
	public JLabel lblMestoUBloku;
	public JComboBox<String> cbMestoUBloku;
	public JLabel lblPrvoEmitovanje;
	public JLabel lblZadnjeEmitovanje;
	public JLabel lblNaSvakih;
	public JButton btnDodajPeriodino;
	public JLabel lblUVreme;
	public JButton btnDodajVreme;
	public JTextField tfUVreme;
	public JTextField tfPrvo;
	public JTextField tfZadnje;
	public JTextField tfNaSvakih;
	public Component horizontalStrut_8;
	public JPanel lowerPanel;
	public JPanel previewPanel;
	public JButton btnSnimiKonacno;
	public JPanel finalSavePanel;
	public JButton btnZatvori;
	public JLabel lblDatum;
	public JTextField tfPrevDate;
	public JLabel lblVreme;
	public JButton btnPrikazi;
	public JTextField tfPrevTime;
	public JComboBox<String> cbGlavniPrioritet;
	public JPanel centralPanel;
	public JTextArea prevTextArea;
	public Component horizontalStrut;
	public JScrollPane scrollPane_2;
	public JLabel lblShouldSave;

	public ReklameOglasiDlg getThis(){
		return this;
	}

	public void loadStructure(){
		canProccessComboBox = false;
		canProccessList = false;
		canProccessIntroOutro = false;
		itemsList.setCurrtime(System.currentTimeMillis());
		try{
			File dir = new File("mcats");
			File files[] = dir.listFiles(new RekFileFilter());
			cbLists.removeAllItems();
			ScheduledItemsType type;
			ScheduledItem item;
			TimeEntry entry;
			String str;
			int count_len, count, j, jLen;;
			for(int i=0,len=files.length;i<len;i++){
				str = files[i].getName();
				str = str.substring(0, str.length() - 4);
				type = new ScheduledItemsType();
				type.name = str;
				try{
					String pathStr;
					FileInputStream fis = new FileInputStream(files[i]);
					ObjectInputStream ois = new ObjectInputStream(fis);
					type.name = ois.readUTF();
					pathStr = ois.readUTF();
					try {
						type.najava = (pathStr.equals("null"))?null:new ListJItem(pathStr);
					} catch(IOException e){
						type.najava = null;
					}
					pathStr = ois.readUTF();
					try {
						type.odjava = (pathStr.equals("null"))?null:new ListJItem(pathStr);
					} catch(IOException e){
						type.odjava = null;
					}
					type.prioritet = ois.readByte();
					count_len = ois.readInt();
					for(count=0;count<count_len;count++){
						item = new ScheduledItem(ois.readUTF());
						item.begin = ois.readLong();
						item.end = ois.readLong();
						item.mestoUBloku = ois.readByte();
						item.ponedeljak = ois.readBoolean();
						item.utorak = ois.readBoolean();
						item.sreda = ois.readBoolean();
						item.cetvrtak = ois.readBoolean();
						item.petak = ois.readBoolean();
						item.subota = ois.readBoolean();
						item.nedelja = ois.readBoolean();
						jLen = ois.readInt();
						for(j=0;j<jLen;j++){
							entry = new TimeEntry();
							entry.time = ois.readInt();
							entry.rank = ois.readByte();
							item.runTimes.add(entry);
						}
						type.items.add(item);
					}
					ois.close();
					fis.close();
				}catch(IOException ioe){
					ioe.printStackTrace(System.out);
					PlayerWin.getErrDlg().showError("Desila se greška tokom čitanja kategorije " + 
							files[i].getName().substring(0, files[i].getName().lastIndexOf(".rek")));
				}catch(UnsupportedAudioFileException ioe){
					ioe.printStackTrace(System.out);
					PlayerWin.getErrDlg().showError("Desila se greška tokom čitanja kategorije " + 
							files[i].getName().substring(0, files[i].getName().lastIndexOf(".rek")));
				}
				cbLists.addItem(type);
			}
		}catch(Exception e){
			e.printStackTrace(System.out);
			PlayerWin.getErrDlg().showError("Desila se greška tokom čitanja kategorija.");
		}
		disableControls();
		((DefaultListModel<ScheduledItem>)itemsList.getModel()).removeAllElements();
		cbLists.setSelectedIndex(-1);
		lblNajavnapica.setEnabled(false);
		lblOdjavnapica.setEnabled(false);
		sfNajava.setEnabled(false);
		sfOdjava.setEnabled(false);
		sfNajava.setNullItem();
		sfOdjava.setNullItem();
		cbGlavniPrioritet.setEnabled(false);
		cbGlavniPrioritet.setSelectedIndex(-1);
		canProccessComboBox = true;
		canProccessList = true;
		canProccessIntroOutro = true;
	}

	private void removeTime(int index){
		DefaultListModel<TimeEntry> model = (DefaultListModel<TimeEntry>)(timesList.getModel());
		model.removeElementAt(index);
		itemsList.getSelectedValue().runTimes.remove(index);
	}

	private void addTime(int time){
		DefaultListModel<TimeEntry> model = (DefaultListModel<TimeEntry>)(timesList.getModel());
		int i = 0, len = model.size();
		for(;i<len;i++){
			if(model.get(i).time == time) return;
			if(model.get(i).time > time) break;
		}
		TimeEntry entry = new TimeEntry();
		entry.time = time;
		model.add(i, entry);
		ScheduledItem item = itemsList.getSelectedValue();
		item.runTimes.add(i, entry);
	}

	public void enableControls(){
		lblPrvoEmitovanje.setEnabled(true);
		lblZadnjeEmitovanje.setEnabled(true);
		lblNaSvakih.setEnabled(true);
		lblUVreme.setEnabled(true);
		btnDodajPeriodino.setEnabled(true);
		btnDodajVreme.setEnabled(true);
		tfPrvo.setEnabled(true);
		tfZadnje.setEnabled(true);
		tfNaSvakih.setEnabled(true);
		tfUVreme.setEnabled(true);
		timesList.setEnabled(true);
		chckbxPon.setEnabled(true);
		chckbxUto.setEnabled(true);
		chckbxSre.setEnabled(true);
		chckbxCet.setEnabled(true);
		chckbxPet.setEnabled(true);
		chckbxSub.setEnabled(true);
		chckbxNed.setEnabled(true);
		cbGlavniPrioritet.setEnabled(true);
		lblPocDatum.setEnabled(true);
		lblPocVreme.setEnabled(true);
		lblKrajDatum.setEnabled(true);
		lblKrajVreme.setEnabled(true);
		lblMestoUBloku.setEnabled(true);
		tfPocDatum.setEnabled(true);
		tfPocVreme.setEnabled(true);
		tfKrajDatum.setEnabled(true);
		tfKrajVreme.setEnabled(true);
		btnSnimi.setEnabled(true);
	}

	public void clearControls(){
		tfPrvo.setText("");
		tfZadnje.setText("");
		tfNaSvakih.setText("");
		tfUVreme.setText("");
		((DefaultListModel<TimeEntry>)timesList.getModel()).removeAllElements();
		cbGlavniPrioritet.setSelectedIndex( -1);
		tfPocDatum.setText("");
		tfPocVreme.setText("");
		tfKrajDatum.setText("");
		tfKrajVreme.setText("");
		cbMestoUBloku.setSelectedIndex(-1);
	}

	public void disableControls(){
		lblPrvoEmitovanje.setEnabled(false);
		lblZadnjeEmitovanje.setEnabled(false);
		lblNaSvakih.setEnabled(false);
		lblUVreme.setEnabled(false);
		btnDodajPeriodino.setEnabled(false);
		btnDodajVreme.setEnabled(false);
		tfPrvo.setText("");
		tfZadnje.setText("");
		tfNaSvakih.setText("");
		tfUVreme.setText("");
		((DefaultListModel<TimeEntry>)timesList.getModel()).removeAllElements();
		tfPrvo.setEnabled(false);
		tfZadnje.setEnabled(false);
		tfNaSvakih.setEnabled(false);
		tfUVreme.setEnabled(false);
		timesList.setEnabled(false);
		chckbxPon.setSelected(false);
		chckbxUto.setSelected(false);
		chckbxSre.setSelected(false);
		chckbxCet.setSelected(false);
		chckbxPet.setSelected(false);
		chckbxSub.setSelected(false);
		chckbxNed.setSelected(false);
		chckbxPon.setEnabled(false);
		chckbxUto.setEnabled(false);
		chckbxSre.setEnabled(false);
		chckbxCet.setEnabled(false);
		chckbxPet.setEnabled(false);
		chckbxSub.setEnabled(false);
		chckbxNed.setEnabled(false);
		cbGlavniPrioritet.setSelectedIndex( -1);
		cbGlavniPrioritet.setEnabled(true);
		lblPocDatum.setEnabled(false);
		lblPocVreme.setEnabled(false);
		lblKrajDatum.setEnabled(false);
		lblKrajVreme.setEnabled(false);
		lblMestoUBloku.setEnabled(false);
		tfPocDatum.setText("");
		tfPocVreme.setText("");
		tfKrajDatum.setText("");
		tfKrajVreme.setText("");
		cbMestoUBloku.setSelectedIndex(-1);
		tfPocDatum.setEnabled(false);
		tfPocVreme.setEnabled(false);
		tfKrajDatum.setEnabled(false);
		tfKrajVreme.setEnabled(false);
		cbMestoUBloku.setEnabled(false);
		btnSnimi.setEnabled(false);
		lblShouldSave.setVisible(false);
	}

	/**
	 * Create the frame.
	 */
	public ReklameOglasiDlg() {
		addWindowListener(new ThisWindowListener());
		setMinimumSize(new Dimension(900, 490));
		setBounds(100, 100, 903, 490);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		centralPanel = new JPanel();
		contentPane.add(centralPanel, BorderLayout.CENTER);
		centralPanel.setLayout(new BorderLayout(0, 0));
		
		upperPanel = new JPanel();
		centralPanel.add(upperPanel, BorderLayout.NORTH);
		GridBagLayout gbl_upperPanel = new GridBagLayout();
		gbl_upperPanel.columnWidths = new int[] {0, 0, 0, 0, 0};
		gbl_upperPanel.rowHeights = new int[] {26};
		gbl_upperPanel.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_upperPanel.rowWeights = new double[]{0.0};
		upperPanel.setLayout(gbl_upperPanel);
		
		cbLists = new JComboBox<ScheduledItemsType>();
		cbLists.addItemListener(new CbListsItemListener());
		GridBagConstraints gbc_cbLists = new GridBagConstraints();
		gbc_cbLists.insets = new Insets(0, 0, 0, 5);
		gbc_cbLists.fill = GridBagConstraints.BOTH;
		gbc_cbLists.gridx = 0;
		gbc_cbLists.gridy = 0;
		upperPanel.add(cbLists, gbc_cbLists);
		
		btnNew = new JButton("Novo");
		btnNew.addActionListener(new BtnNewActionListener());
		GridBagConstraints gbc_btnNew = new GridBagConstraints();
		gbc_btnNew.insets = new Insets(0, 0, 0, 5);
		gbc_btnNew.gridx = 1;
		gbc_btnNew.gridy = 0;
		upperPanel.add(btnNew, gbc_btnNew);
		
		btnEdit = new JButton("Izmeni");
		btnEdit.addActionListener(new BtnEditActionListener());
		GridBagConstraints gbc_btnEdit = new GridBagConstraints();
		gbc_btnEdit.insets = new Insets(0, 0, 0, 5);
		gbc_btnEdit.gridx = 2;
		gbc_btnEdit.gridy = 0;
		upperPanel.add(btnEdit, gbc_btnEdit);
		
		btnDelete = new JButton("Obri\u0161i");
		btnDelete.addActionListener(new BtnDeleteActionListener());
		GridBagConstraints gbc_btnDelete = new GridBagConstraints();
		gbc_btnDelete.gridx = 3;
		gbc_btnDelete.gridy = 0;
		upperPanel.add(btnDelete, gbc_btnDelete);
		
		scrollPane = new JScrollPane();
		centralPanel.add(scrollPane, BorderLayout.CENTER);
		
		itemsList = new ScheduledItemsList();
		itemsList.addKeyListener(new ItemsListKeyListener());
		itemsList.addListSelectionListener(new ItemsListListSelectionListener());
		itemsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		itemsList.setTransferHandler(new ScheduledListHandler(cbLists, itemsList));
		scrollPane.setViewportView(itemsList);
		
		setTimesPanel = new JPanel();
		setTimesPanel.setBorder(new TitledBorder(null, "Pode\u0161avanje termina blokova", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		centralPanel.add(setTimesPanel, BorderLayout.WEST);
		setTimesPanel.setPreferredSize(new Dimension(265, 10));
		setTimesPanel.setLayout(new BoxLayout(setTimesPanel, BoxLayout.X_AXIS));
		
		giveTimesPanel = new JPanel();
		giveTimesPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
		setTimesPanel.add(giveTimesPanel);
		GridBagLayout gbl_giveTimesPanel = new GridBagLayout();
		gbl_giveTimesPanel.columnWidths = new int[] {113, 51};
		gbl_giveTimesPanel.rowHeights = new int[]{16, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_giveTimesPanel.columnWeights = new double[]{1.0, 1.0};
		gbl_giveTimesPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		giveTimesPanel.setLayout(gbl_giveTimesPanel);
		
		lblMestoUBloku = new JLabel("Mesto u bloku");
		GridBagConstraints gbc_lblMestoUBloku = new GridBagConstraints();
		gbc_lblMestoUBloku.anchor = GridBagConstraints.WEST;
		gbc_lblMestoUBloku.insets = new Insets(0, 7, 0, 5);
		gbc_lblMestoUBloku.gridx = 0;
		gbc_lblMestoUBloku.gridy = 0;
		giveTimesPanel.add(lblMestoUBloku, gbc_lblMestoUBloku);
		
		cbMestoUBloku = new JComboBox<String>();
		cbMestoUBloku.addItemListener(new CbMestoUBlokuItemListener());
		GridBagConstraints gbc_cbMestoUBloku = new GridBagConstraints();
		gbc_cbMestoUBloku.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbMestoUBloku.gridwidth = 2;
		gbc_cbMestoUBloku.insets = new Insets(0, 0, 10, 0);
		gbc_cbMestoUBloku.gridx = 0;
		gbc_cbMestoUBloku.gridy = 1;
		giveTimesPanel.add(cbMestoUBloku, gbc_cbMestoUBloku);
		cbMestoUBloku.setModel(new DefaultComboBoxModel<String>(new String[] {"Prvo", "Drugo", "Između", "Pretposlednje", "Poslednje"}));
		//cbMestoUBloku.setSelectedIndex(2);
		
		lblPrvoEmitovanje = new JLabel("Prvo emitovanje");
		GridBagConstraints gbc_lblPrvoEmitovanje = new GridBagConstraints();
		gbc_lblPrvoEmitovanje.insets = new Insets(0, 0, 1, 5);
		gbc_lblPrvoEmitovanje.anchor = GridBagConstraints.EAST;
		gbc_lblPrvoEmitovanje.gridx = 0;
		gbc_lblPrvoEmitovanje.gridy = 4;
		giveTimesPanel.add(lblPrvoEmitovanje, gbc_lblPrvoEmitovanje);
		
		tfPrvo = new JTextField();
		GridBagConstraints gbc_tfPrvo = new GridBagConstraints();
		gbc_tfPrvo.insets = new Insets(0, 0, 1, 0);
		gbc_tfPrvo.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfPrvo.gridx = 1;
		gbc_tfPrvo.gridy = 4;
		giveTimesPanel.add(tfPrvo, gbc_tfPrvo);
		tfPrvo.addFocusListener(vremeFocusListener);
		tfPrvo.setColumns(10);
		
		lblZadnjeEmitovanje = new JLabel("Zadnje emitovanje");
		GridBagConstraints gbc_lblZadnjeEmitovanje = new GridBagConstraints();
		gbc_lblZadnjeEmitovanje.anchor = GridBagConstraints.EAST;
		gbc_lblZadnjeEmitovanje.insets = new Insets(0, 0, 1, 5);
		gbc_lblZadnjeEmitovanje.gridx = 0;
		gbc_lblZadnjeEmitovanje.gridy = 5;
		giveTimesPanel.add(lblZadnjeEmitovanje, gbc_lblZadnjeEmitovanje);
		
		tfZadnje = new JTextField();
		GridBagConstraints gbc_tfZadnje = new GridBagConstraints();
		gbc_tfZadnje.insets = new Insets(0, 0, 1, 0);
		gbc_tfZadnje.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfZadnje.gridx = 1;
		gbc_tfZadnje.gridy = 5;
		giveTimesPanel.add(tfZadnje, gbc_tfZadnje);
		tfZadnje.addFocusListener(vremeFocusListener);
		tfZadnje.setColumns(10);
		
		lblNaSvakih = new JLabel("Na svakih");
		lblNaSvakih.setHorizontalTextPosition(SwingConstants.LEADING);
		GridBagConstraints gbc_lblNaSvakih = new GridBagConstraints();
		gbc_lblNaSvakih.anchor = GridBagConstraints.EAST;
		gbc_lblNaSvakih.insets = new Insets(0, 0, 1, 5);
		gbc_lblNaSvakih.gridx = 0;
		gbc_lblNaSvakih.gridy = 6;
		giveTimesPanel.add(lblNaSvakih, gbc_lblNaSvakih);
		
		tfNaSvakih = new JTextField();
		GridBagConstraints gbc_tfNaSvakih = new GridBagConstraints();
		gbc_tfNaSvakih.insets = new Insets(0, 0, 1, 0);
		gbc_tfNaSvakih.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfNaSvakih.gridx = 1;
		gbc_tfNaSvakih.gridy = 6;
		giveTimesPanel.add(tfNaSvakih, gbc_tfNaSvakih);
		tfNaSvakih.addFocusListener(vremeFocusListener);
		tfNaSvakih.setColumns(10);
		
		btnDodajPeriodino = new JButton("Dodaj periodi\u010Dno");
		btnDodajPeriodino.addActionListener(new BtnDodajPeriodinoActionListener());
		GridBagConstraints gbc_btnDodajPeriodino = new GridBagConstraints();
		gbc_btnDodajPeriodino.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnDodajPeriodino.gridwidth = 2;
		gbc_btnDodajPeriodino.gridx = 0;
		gbc_btnDodajPeriodino.gridy = 7;
		giveTimesPanel.add(btnDodajPeriodino, gbc_btnDodajPeriodino);
		
		lblUVreme = new JLabel("U vreme");
		GridBagConstraints gbc_lblUVreme = new GridBagConstraints();
		gbc_lblUVreme.anchor = GridBagConstraints.EAST;
		gbc_lblUVreme.insets = new Insets(0, 0, 1, 5);
		gbc_lblUVreme.gridx = 0;
		gbc_lblUVreme.gridy = 2;
		giveTimesPanel.add(lblUVreme, gbc_lblUVreme);
		
		tfUVreme = new JTextField();
		tfUVreme.addFocusListener(vremeFocusListener);
		GridBagConstraints gbc_tfUVreme = new GridBagConstraints();
		gbc_tfUVreme.insets = new Insets(0, 0, 1, 0);
		gbc_tfUVreme.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfUVreme.gridx = 1;
		gbc_tfUVreme.gridy = 2;
		giveTimesPanel.add(tfUVreme, gbc_tfUVreme);
		tfUVreme.setColumns(10);
		
		btnDodajVreme = new JButton("Dodaj vreme");
		btnDodajVreme.addActionListener(new BtnDodajVremeActionListener());
		GridBagConstraints gbc_btnDodajVreme = new GridBagConstraints();
		gbc_btnDodajVreme.insets = new Insets(0, 0, 20, 0);
		gbc_btnDodajVreme.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnDodajVreme.gridwidth = 2;
		gbc_btnDodajVreme.gridx = 0;
		gbc_btnDodajVreme.gridy = 3;
		giveTimesPanel.add(btnDodajVreme, gbc_btnDodajVreme);
		
		horizontalStrut_8 = Box.createHorizontalStrut(5);
		setTimesPanel.add(horizontalStrut_8);
		
		timesPanel = new JPanel();
		timesPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
		timesPanel.setPreferredSize(new Dimension(120, 10));
		timesPanel.setMaximumSize(new Dimension(120, 32767));
		setTimesPanel.add(timesPanel);
		timesPanel.setLayout(new BoxLayout(timesPanel, BoxLayout.Y_AXIS));
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setMinimumSize(new Dimension(70, 25));
		scrollPane_1.setPreferredSize(new Dimension(70, 6));
		scrollPane_1.setMaximumSize(new Dimension(70, 32767));
		scrollPane_1.setAlignmentX(Component.LEFT_ALIGNMENT);
		timesPanel.add(scrollPane_1);
		
		timesList = new JList<TimeEntry>(new DefaultListModel<TimeEntry>());
		timesList.addListSelectionListener(new TimesListListSelectionListener());
		timesList.addKeyListener(new TimesListKeyListener());
		scrollPane_1.setViewportView(timesList);
		
		intervalPanel = new JPanel();
		centralPanel.add(intervalPanel, BorderLayout.EAST);
		intervalPanel.setBorder(new TitledBorder(null, "Dani emitovanja", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		intervalPanel.setPreferredSize(new Dimension(200, 10));
		GridBagLayout gbl_intervalPanel = new GridBagLayout();
		gbl_intervalPanel.columnWidths = new int[] {0, 0};
		gbl_intervalPanel.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 30};
		gbl_intervalPanel.columnWeights = new double[]{0.0, 1.0};
		gbl_intervalPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		intervalPanel.setLayout(gbl_intervalPanel);
		
		lblPocDatum = new JLabel("Po\u010Detni datum");
		GridBagConstraints gbc_lblPocDatum = new GridBagConstraints();
		gbc_lblPocDatum.anchor = GridBagConstraints.EAST;
		gbc_lblPocDatum.insets = new Insets(5, 0, 5, 5);
		gbc_lblPocDatum.gridx = 0;
		gbc_lblPocDatum.gridy = 0;
		intervalPanel.add(lblPocDatum, gbc_lblPocDatum);
		
		tfPocDatum = new JTextField();
		GridBagConstraints gbc_tfPocDatum = new GridBagConstraints();
		gbc_tfPocDatum.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfPocDatum.insets = new Insets(5, 0, 5, 0);
		gbc_tfPocDatum.gridx = 1;
		gbc_tfPocDatum.gridy = 0;
		intervalPanel.add(tfPocDatum, gbc_tfPocDatum);
		tfPocDatum.addFocusListener(datumFocusListener);
		tfPocDatum.setMaximumSize(new Dimension(2147483647, 28));
		tfPocDatum.setAlignmentX(Component.LEFT_ALIGNMENT);
		tfPocDatum.setColumns(10);
		
		lblPocVreme = new JLabel("Po\u010Detno vreme");
		GridBagConstraints gbc_lblPocVreme = new GridBagConstraints();
		gbc_lblPocVreme.anchor = GridBagConstraints.EAST;
		gbc_lblPocVreme.insets = new Insets(0, 0, 5, 5);
		gbc_lblPocVreme.gridx = 0;
		gbc_lblPocVreme.gridy = 1;
		intervalPanel.add(lblPocVreme, gbc_lblPocVreme);
		
		tfPocVreme = new JTextField();
		GridBagConstraints gbc_tfPocVreme = new GridBagConstraints();
		gbc_tfPocVreme.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfPocVreme.insets = new Insets(0, 0, 5, 0);
		gbc_tfPocVreme.gridx = 1;
		gbc_tfPocVreme.gridy = 1;
		intervalPanel.add(tfPocVreme, gbc_tfPocVreme);
		tfPocVreme.addFocusListener(vremeFocusListener);
		tfPocVreme.setColumns(10);
		
		lblKrajDatum = new JLabel("Krajnji datum");
		GridBagConstraints gbc_lblKrajDatum = new GridBagConstraints();
		gbc_lblKrajDatum.anchor = GridBagConstraints.EAST;
		gbc_lblKrajDatum.insets = new Insets(0, 0, 5, 5);
		gbc_lblKrajDatum.gridx = 0;
		gbc_lblKrajDatum.gridy = 2;
		intervalPanel.add(lblKrajDatum, gbc_lblKrajDatum);
		
		tfKrajDatum = new JTextField();
		GridBagConstraints gbc_tfKrajDatum = new GridBagConstraints();
		gbc_tfKrajDatum.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfKrajDatum.insets = new Insets(0, 0, 5, 0);
		gbc_tfKrajDatum.gridx = 1;
		gbc_tfKrajDatum.gridy = 2;
		intervalPanel.add(tfKrajDatum, gbc_tfKrajDatum);
		tfKrajDatum.addFocusListener(datumFocusListener);
		tfKrajDatum.setMaximumSize(new Dimension(2147483647, 28));
		tfKrajDatum.setAlignmentX(Component.LEFT_ALIGNMENT);
		tfKrajDatum.setColumns(10);
		
		lblKrajVreme = new JLabel("Zavr\u0161no vreme");
		GridBagConstraints gbc_lblKrajVreme = new GridBagConstraints();
		gbc_lblKrajVreme.anchor = GridBagConstraints.EAST;
		gbc_lblKrajVreme.insets = new Insets(0, 0, 5, 5);
		gbc_lblKrajVreme.gridx = 0;
		gbc_lblKrajVreme.gridy = 3;
		intervalPanel.add(lblKrajVreme, gbc_lblKrajVreme);
		
		tfKrajVreme = new JTextField();
		GridBagConstraints gbc_tfKrajVreme = new GridBagConstraints();
		gbc_tfKrajVreme.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfKrajVreme.insets = new Insets(0, 0, 5, 0);
		gbc_tfKrajVreme.gridx = 1;
		gbc_tfKrajVreme.gridy = 3;
		intervalPanel.add(tfKrajVreme, gbc_tfKrajVreme);
		tfKrajVreme.addFocusListener(vremeFocusListener);
		tfKrajVreme.setColumns(10);
		
		chckbxPon = new JCheckBox("Pon");
		GridBagConstraints gbc_chckbxPon = new GridBagConstraints();
		gbc_chckbxPon.insets = new Insets(10, 0, 5, 5);
		gbc_chckbxPon.gridx = 0;
		gbc_chckbxPon.gridy = 4;
		intervalPanel.add(chckbxPon, gbc_chckbxPon);
		
		chckbxUto = new JCheckBox("Uto");
		GridBagConstraints gbc_chckbxUto = new GridBagConstraints();
		gbc_chckbxUto.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxUto.gridx = 0;
		gbc_chckbxUto.gridy = 5;
		intervalPanel.add(chckbxUto, gbc_chckbxUto);
		
		chckbxPet = new JCheckBox("Pet");
		GridBagConstraints gbc_chckbxPet = new GridBagConstraints();
		gbc_chckbxPet.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxPet.gridx = 1;
		gbc_chckbxPet.gridy = 5;
		intervalPanel.add(chckbxPet, gbc_chckbxPet);
		
		chckbxSre = new JCheckBox("Sre");
		GridBagConstraints gbc_chckbxSre = new GridBagConstraints();
		gbc_chckbxSre.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxSre.gridx = 0;
		gbc_chckbxSre.gridy = 6;
		intervalPanel.add(chckbxSre, gbc_chckbxSre);
		
		chckbxSub = new JCheckBox("Sub");
		GridBagConstraints gbc_chckbxSub = new GridBagConstraints();
		gbc_chckbxSub.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxSub.gridx = 1;
		gbc_chckbxSub.gridy = 6;
		intervalPanel.add(chckbxSub, gbc_chckbxSub);
		
		chckbxCet = new JCheckBox("\u010Cet");
		GridBagConstraints gbc_chckbxCet = new GridBagConstraints();
		gbc_chckbxCet.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxCet.gridx = 0;
		gbc_chckbxCet.gridy = 7;
		intervalPanel.add(chckbxCet, gbc_chckbxCet);
		
		chckbxNed = new JCheckBox("Ned");
		GridBagConstraints gbc_chckbxNed = new GridBagConstraints();
		gbc_chckbxNed.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxNed.gridx = 1;
		gbc_chckbxNed.gridy = 7;
		intervalPanel.add(chckbxNed, gbc_chckbxNed);
		
		btnSnimi = new JButton("Snimi");
		btnSnimi.addActionListener(new BtnSnimiActionListener());
		
		lblShouldSave = new JLabel("Nije sve unešeno");
		lblShouldSave.setForeground(Color.RED);
		GridBagConstraints gbc_lblShouldSave = new GridBagConstraints();
		gbc_lblShouldSave.gridwidth = 2;
		gbc_lblShouldSave.insets = new Insets(0, 0, 5, 5);
		gbc_lblShouldSave.gridx = 0;
		gbc_lblShouldSave.gridy = 8;
		intervalPanel.add(lblShouldSave, gbc_lblShouldSave);
		GridBagConstraints gbc_btnSnimi = new GridBagConstraints();
		gbc_btnSnimi.insets = new Insets(2, 0, 0, 0);
		gbc_btnSnimi.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSnimi.gridwidth = 2;
		gbc_btnSnimi.gridx = 0;
		gbc_btnSnimi.gridy = 9;
		intervalPanel.add(btnSnimi, gbc_btnSnimi);
		btnSnimi.setMaximumSize(new Dimension(2147483647, 28));
		
		lowerPanel = new JPanel();
		centralPanel.add(lowerPanel, BorderLayout.SOUTH);
		lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.Y_AXIS));
		
		lowerSettingsPanel = new JPanel();
		lowerSettingsPanel.setBorder(new TitledBorder(null, "Pode\u0161avanje najavne i odjavne \u0161pice (neobavezno)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		lowerPanel.add(lowerSettingsPanel);
		lowerSettingsPanel.setLayout(new BoxLayout(lowerSettingsPanel, BoxLayout.X_AXIS));
		
		lblNajavnapica = new JLabel("Najavna \u0161pica");
		lowerSettingsPanel.add(lblNajavnapica);
		
		sfNajava = new JSongField();
		sfNajava.addContentListener(new IntroContentListener());
		lowerSettingsPanel.add(sfNajava);
		sfNajava.setColumns(10);
		
		horizontalStrut = Box.createHorizontalStrut(30);
		lowerSettingsPanel.add(horizontalStrut);
		
		lblOdjavnapica = new JLabel("Odjavna \u0161pica");
		lowerSettingsPanel.add(lblOdjavnapica);
		
		sfOdjava = new JSongField();
		sfOdjava.addContentListener(new OutroContentListener());
		lowerSettingsPanel.add(sfOdjava);
		sfOdjava.setColumns(10);
		
		finalSavePanel = new JPanel();
		lowerPanel.add(finalSavePanel);
		GridBagLayout gbl_finalSavePanel = new GridBagLayout();
		gbl_finalSavePanel.columnWidths = new int[] {0, 0, 0};
		gbl_finalSavePanel.rowHeights = new int[] {28};
		gbl_finalSavePanel.columnWeights = new double[]{0.0, 0.0, 0.0};
		gbl_finalSavePanel.rowWeights = new double[]{0.0};
		finalSavePanel.setLayout(gbl_finalSavePanel);
		
		btnSnimiKonacno = new JButton("Snimi sve izmene za izabrani tipa oglasa/reklama");
		btnSnimiKonacno.addActionListener(new BtnSnimiKonacnoActionListener());
		
		cbGlavniPrioritet = new JComboBox<String>();
		cbGlavniPrioritet.addItemListener(new CbGlavniPrioritetItemListener());
		cbGlavniPrioritet.setPreferredSize(new Dimension(150, 26));
		GridBagConstraints gbc_cbGlavniPrioritet = new GridBagConstraints();
		gbc_cbGlavniPrioritet.insets = new Insets(0, 0, 0, 20);
		gbc_cbGlavniPrioritet.gridx = 0;
		gbc_cbGlavniPrioritet.gridy = 0;
		finalSavePanel.add(cbGlavniPrioritet, gbc_cbGlavniPrioritet);
		cbGlavniPrioritet.setModel(new DefaultComboBoxModel<String>(new String[] {"Prioritet emitovanja 1", 
				"Prioritet emitovanja 2", "Prioritet emitovanja 3", "Prioritet emitovanja 4", 
				"Prioritet emitovanja 5", "Prioritet emitovanja 6"}));
		GridBagConstraints gbc_btnSnimiKonacno = new GridBagConstraints();
		gbc_btnSnimiKonacno.weightx = 1.0;
		gbc_btnSnimiKonacno.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSnimiKonacno.gridx = 1;
		gbc_btnSnimiKonacno.gridy = 0;
		finalSavePanel.add(btnSnimiKonacno, gbc_btnSnimiKonacno);
		
		btnZatvori = new JButton("Zatvori");
		btnZatvori.addActionListener(new BtnZatvoriActionListener());
		GridBagConstraints gbc_btnZatvori = new GridBagConstraints();
		gbc_btnZatvori.insets = new Insets(0, 20, 0, 0);
		gbc_btnZatvori.gridx = 2;
		gbc_btnZatvori.gridy = 0;
		finalSavePanel.add(btnZatvori, gbc_btnZatvori);
		
		previewPanel = new JPanel();
		previewPanel.setPreferredSize(new Dimension(210, 10));
		previewPanel.setBorder(new TitledBorder(null, "Pregled blokova", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(previewPanel, BorderLayout.WEST);
		previewPanel.setLayout(new BoxLayout(previewPanel, BoxLayout.Y_AXIS));
		
		lblDatum = new JLabel("Datum");
		previewPanel.add(lblDatum);
		
		tfPrevDate = new JTextField();
		tfPrevDate.setAlignmentX(Component.LEFT_ALIGNMENT);
		tfPrevDate.setMaximumSize(new Dimension(100, 28));
		previewPanel.add(tfPrevDate);
		tfPrevDate.addFocusListener(datumFocusListener);
		tfPrevDate.setColumns(7);
		
		lblVreme = new JLabel("Vreme");
		previewPanel.add(lblVreme);
		
		tfPrevTime = new JTextField();
		tfPrevTime.setAlignmentX(Component.LEFT_ALIGNMENT);
		tfPrevTime.setMaximumSize(new Dimension(80, 28));
		previewPanel.add(tfPrevTime);
		tfPrevTime.addFocusListener(vremeFocusListener);
		tfPrevTime.setColumns(4);
		
		btnPrikazi = new JButton("Prika\u017Ei");
		btnPrikazi.addActionListener(new BtnPrikaziActionListener());
		previewPanel.add(btnPrikazi);
		
		scrollPane_2 = new JScrollPane();
		scrollPane_2.setAlignmentX(Component.LEFT_ALIGNMENT);
		previewPanel.add(scrollPane_2);
		
		prevTextArea = new JTextArea();
		prevTextArea.setEditable(false);
		scrollPane_2.setViewportView(prevTextArea);
		prevTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		sdf.setLenient(false);
	}
	private class BtnNewActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			CatNameDialog dlg = new CatNameDialog();
			dlg.setLocation(btnNew.getLocationOnScreen().x + btnNew.getWidth() - dlg.getWidth(), 
					btnNew.getLocationOnScreen().y + btnNew.getHeight());
			dlg.setVisible(true);
			if((dlg.modalResult == SDModalResult.CANCEL) || dlg.tfNaziv.getText().isEmpty()){
				dlg.dispose();
				return;
			}
			ErrorInfoDialog err = PlayerWin.getErrDlg();
			if((dlg.tfNaziv.getText().indexOf(":") != -1) || (dlg.tfNaziv.getText().indexOf("/:") != -1) 
					|| (dlg.tfNaziv.getText().indexOf("?") != -1) 
					|| (dlg.tfNaziv.getText().indexOf("\\") != -1)){
				err.showError("Znakove / , : , ? , \\ nije dozvoljeno unositi u imena kategorije.");
				return;
			}
			ScheduledItemsType schList = new ScheduledItemsType();
			schList.name = dlg.tfNaziv.getText();
			dlg.dispose();
			File file = new File("mcats/" + schList.name.toLowerCase() + ".rek");
			try {
				if(file.exists()){
					err.showError("Kategorija sa tim nazivom već postoji.<br/>Izaberite drugo ime.");
					return;
				}
				file.createNewFile();
				int i = 0, len;
				for(len=cbLists.getItemCount();i<len;i++)
					if(cbLists.getItemAt(i).name.compareTo(schList.name) > 0)
						break;
				cbLists.insertItemAt(schList, i);
				cbLists.setSelectedIndex(i);
			} catch (IOException e) {
				e.printStackTrace(System.out);
				err.showError("Desila se greška tokom kreiranja kategorije.");
			}
		}
	}
	private class BtnEditActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if(cbLists.getSelectedIndex() == -1) return;
			ScheduledItemsType schList = (ScheduledItemsType)cbLists.getSelectedItem();
			CatNameDialog dlg = new CatNameDialog();
			dlg.tfNaziv.setText(schList.name);
			dlg.setLocation(btnEdit.getLocationOnScreen().x + btnEdit.getWidth() - dlg.getWidth(), 
					btnEdit.getLocationOnScreen().y + btnEdit.getHeight());
			dlg.setVisible(true);
			if((dlg.modalResult == SDModalResult.CANCEL) || dlg.tfNaziv.getText().isEmpty()){
				dlg.dispose();
				return;
			}
			String newName = dlg.tfNaziv.getText();
			ErrorInfoDialog err = PlayerWin.getErrDlg();
			if((newName.indexOf(":") != -1) || (newName.indexOf("/:") != -1) 
					|| (newName.indexOf("?") != -1) || (newName.indexOf("\\") != -1)){
				err.showError("Znakove / , : , ? , \\ nije dozvoljeno unositi u imena kategorije.");
				return;
			}
			dlg.dispose();
			err.setLocation(btnNew.getLocationOnScreen());
			try{
				if(Files.exists(Paths.get("mcats", newName.toLowerCase() + ".rek"))){
					err.showError("Kategorija sa tim nazivom već postoji.<br/>Izaberite drugo ime.");
					return;
				}
				Files.move(Paths.get("mcats", schList.name + ".rek"), Paths.get("mcats", newName + ".rek"));
				schList.name = newName;
			}catch(Exception e){
				e.printStackTrace(System.out);
				err.showError("Desila se greška tokom preimenovanja kategorije.");
			}
		}
	}
	private class BtnDeleteActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if(cbLists.getSelectedIndex() == -1) return;
			YesNoForm form = new YesNoForm("Jeste li sigurni da želite obrisati izabranu kategoriju.");
			form.setLocation(btnDelete.getLocationOnScreen().x + btnDelete.getWidth() - form.getWidth(), 
					btnDelete.getLocationOnScreen().y + btnDelete.getHeight());
			form.setVisible(true);
			if(!form.confirmed){
				form.dispose();
				return;
			}
			form.dispose();
			ErrorInfoDialog err = PlayerWin.getErrDlg();
			err.setLocation(btnEdit.getLocationOnScreen());
			try{
				String name = ((ScheduledItemsType)(cbLists.getSelectedItem())).name;
				Files.delete(Paths.get("mcats", name.toLowerCase() + ".rek"));
				cbLists.removeItemAt(cbLists.getSelectedIndex());
				if(cbLists.getItemCount() == 0){
					canProccessComboBox = false;
					canProccessList = false;
					disableControls();
					((DefaultListModel<ScheduledItem>)itemsList.getModel()).removeAllElements();
					lblNajavnapica.setEnabled(false);
					lblOdjavnapica.setEnabled(false);
					sfNajava.setEnabled(false);
					sfOdjava.setEnabled(false);
					sfNajava.setNullItem();
					sfOdjava.setNullItem();
					cbGlavniPrioritet.setEnabled(false);
					cbGlavniPrioritet.setSelectedIndex(-1);
					canProccessComboBox = true;
					canProccessList = true;
				}
			}catch(Exception e){
				e.printStackTrace(System.out);
				err.showError("Desila se greška tokom brisanja kategorije.");
			}
		}
	}
	private class CbListsItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent event) {
			if(canProccessComboBox && (event.getStateChange() == ItemEvent.SELECTED)){
				canProccessList = false;
				canProccessIntroOutro = false;
				disableControls();
				if(event.getItem() == null){
					((DefaultListModel<ScheduledItem>)itemsList.getModel()).removeAllElements();
					lblNajavnapica.setEnabled(false);
					lblOdjavnapica.setEnabled(false);
					sfNajava.setEnabled(false);
					sfOdjava.setEnabled(false);
					sfNajava.setNullItem();
					sfOdjava.setNullItem();
					cbGlavniPrioritet.setEnabled(false);
					cbGlavniPrioritet.setSelectedIndex(-1);
				}else{
					ScheduledItemsType type = (ScheduledItemsType)cbLists.getSelectedItem();
					clearControls();
					cbGlavniPrioritet.setSelectedIndex(type.prioritet - 1);
					lblNajavnapica.setEnabled(true);
					lblOdjavnapica.setEnabled(true);
					sfNajava.setEnabled(true);
					sfOdjava.setEnabled(true);
					if(type.najava == null)
						sfNajava.setNullItem();
					else sfNajava.setItem(type.najava);
					if(type.odjava == null)
						sfOdjava.setNullItem();
					else sfOdjava.setItem(type.odjava);
					DefaultListModel<TimeEntry> timesModel = (DefaultListModel<TimeEntry>)timesList.getModel();
					timesModel.removeAllElements();
					DefaultListModel<ScheduledItem> model = 
							(DefaultListModel<ScheduledItem>)itemsList.getModel();
					model.removeAllElements();
					for(int i=0,len=type.items.size();i<len;i++)
						model.addElement(type.items.get(i));
				}
				canProccessList = true;
				canProccessIntroOutro = true;
			}
		}
	}
	private class DatumFocusListener extends FocusAdapter {
		@Override
		public void focusLost(FocusEvent event) {
			JTextField source = (JTextField)event.getSource();
			String str = source.getText().trim();
			str = str.replace('/', '.');
			str = str.replace(',', '.');
			source.setText(str);
			try {
				sdf.parse(source.getText());
			} catch (ParseException e1) {
				//e1.printStackTrace();
				source.setText("");
			}
		}
	}
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
	private class VremeFocusListener extends FocusAdapter {
		@Override
		public void focusLost(FocusEvent e) {
			JTextField source = (JTextField)e.getSource();
			int time = parseTime(source.getText().trim());
			source.setText(formatTime(time));
		}
	}
	private class BtnDodajVremeActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			int time = parseTime(tfUVreme.getText());
			if(time == -1){
				ErrorInfoDialog dlg = PlayerWin.getErrDlg();
				dlg.setLocation(btnDodajVreme.getLocationOnScreen());
				dlg.showError("Pogrešno unešeno vreme!");
				return;
			}
			addTime(time);
		}
	}
	private class BtnDodajPeriodinoActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			int prvo = parseTime(tfPrvo.getText());
			if(prvo == -1){
				ErrorInfoDialog dlg = PlayerWin.getErrDlg();
				dlg.setLocation(btnDodajVreme.getLocationOnScreen());
				dlg.showError("Pogrešno unešeno vreme prvog dnevnog emitovanja!");
				return;
			}
			int zadnje = parseTime(tfZadnje.getText());
			if(zadnje == -1){
				ErrorInfoDialog dlg = PlayerWin.getErrDlg();
				dlg.setLocation(btnDodajVreme.getLocationOnScreen());
				dlg.showError("Pogrešno unešeno vreme poslednjeg dnevnog emitovanja!");
				return;
			}
			int korak = parseTime(tfNaSvakih.getText());
			if(korak == -1){
				ErrorInfoDialog dlg = PlayerWin.getErrDlg();
				dlg.setLocation(btnDodajVreme.getLocationOnScreen());
				dlg.showError("Pogrešno unešeno trajanje koraka!");
				return;
			}
			if(prvo >= zadnje){
				ErrorInfoDialog dlg = PlayerWin.getErrDlg();
				dlg.setLocation(btnDodajVreme.getLocationOnScreen());
				dlg.showError("Prvog dnevno emitovanja se mora desiti pre poslednjeg");
				return;
			}
			int step = prvo;
			while(step <= zadnje){
				addTime(step);
				step += korak;
			}
		}
	}
	private class TimesListKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent ke) {
			if((ke.getKeyCode() == KeyEvent.VK_DELETE) && (ke.getModifiers() == 0)){
				int times[] = timesList.getSelectedIndices();
				for(int i=times.length-1;i>=0;i--)
					removeTime(times[i]);
			}
		}
	}
	private class BtnSnimiActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if(itemsList.getSelectedIndex() == -1) return;
			ErrorInfoDialog dlg = PlayerWin.getErrDlg();
			ScheduledItem item = itemsList.getSelectedValue();
			dlg.setLocation(itemsList.getLocationOnScreen());
			try{
				String str = tfPocDatum.getText().trim();
				str = str.replace('/', '.');
				str = str.replace(',', '.');
				cal.setTime(sdf.parse(str));
			}catch(ParseException e){
				dlg.showError("Pogrešno unešen datum početka emitovanja!");
				return;
			}
			int time = parseTime(tfPocVreme.getText().trim());
			if(time == -1){
				dlg.showError("Pogrešno unešeno vreme početka emitovanja!");
				return;
			}
			cal.set(Calendar.HOUR_OF_DAY, time / 60);
			cal.set(Calendar.MINUTE, time % 60);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			long begin = cal.getTimeInMillis(), end;
			try{
				String str = tfKrajDatum.getText().trim();
				str = str.replace('/', '.');
				str = str.replace(',', '.');
				cal.setTime(sdf.parse(str));
			}catch(ParseException e){
				dlg.showError("Pogrešno unešen datum zarvšetka emitovanja!");
				return;
			}
			time = parseTime(tfKrajVreme.getText().trim());
			if(time == -1){
				dlg.showError("Pogrešno unešeno vreme zarvšetka emitovanja!");
				return;
			}
			cal.set(Calendar.HOUR_OF_DAY, time / 60);
			cal.set(Calendar.MINUTE, time % 60);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			end = cal.getTimeInMillis();
			if(begin >= end){
				dlg.showError("Početak emitovanja se mora desiti pre kraja emitovanja!");
				return;
			}
			item.begin = begin;
			item.end = end;
			item.ponedeljak = chckbxPon.isSelected();
			item.utorak = chckbxUto.isSelected();
			item.sreda = chckbxSre.isSelected();
			item.cetvrtak = chckbxCet.isSelected();
			item.petak = chckbxPet.isSelected();
			item.subota = chckbxSub.isSelected();
			item.nedelja = chckbxNed.isSelected();
			dlg.showError("Uspešno snimljeno.", "Obaveštenje");
		}
	}
	private class BtnZatvoriActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			dispatchEvent(new WindowEvent(getThis(), WindowEvent.WINDOW_CLOSING));
		}
	}
	private class ItemsListListSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if(canProccessList && !event.getValueIsAdjusting()){
				if(itemsList.getSelectedIndex() == -1){
					disableControls();
					return;
				}else enableControls();
				ScheduledItem item = itemsList.getSelectedValue();
				int time = -1;
				if(item.begin != -1){
					cal.setTimeInMillis(item.begin);
					tfPocDatum.setText(sdf.format(cal.getTime()));
					time = (cal.get(Calendar.HOUR_OF_DAY) * 60) + cal.get(Calendar.MINUTE);
					tfPocVreme.setText(formatTime(time));
				}else{
					tfPocDatum.setText("");
					tfPocVreme.setText("");
				}
				if(item.end != -1){
					cal.setTimeInMillis(item.end);
					tfKrajDatum.setText(sdf.format(cal.getTime()));
					time = (cal.get(Calendar.HOUR_OF_DAY) * 60) + cal.get(Calendar.MINUTE);
					tfKrajVreme.setText(formatTime(time));
				}else{
					tfKrajDatum.setText("");
					tfKrajVreme.setText("");
				}
				// Popuna dana u nedelji
				chckbxPon.setSelected(item.ponedeljak);
				chckbxUto.setSelected(item.utorak);
				chckbxSre.setSelected(item.sreda);
				chckbxCet.setSelected(item.cetvrtak);
				chckbxPet.setSelected(item.petak);
				chckbxSub.setSelected(item.subota);
				chckbxNed.setSelected(item.nedelja);
				// Provera popunjenosti
				if((!item.ponedeljak && !item.utorak && !item.sreda && !item.cetvrtak && 
						!item.petak && !item.subota && !item.nedelja) || (item.begin == -1) || 
						(item.end == -1))
					lblShouldSave.setVisible(true);
				else lblShouldSave.setVisible(false);
				// Popuna vremena
				DefaultListModel<TimeEntry> model = (DefaultListModel<TimeEntry>)timesList.getModel();
				model.removeAllElements();
				for(int i=0,len=item.runTimes.size();i<len;i++)
					model.addElement(item.runTimes.get(i));
				cbMestoUBloku.setSelectedIndex(-1);
			}
		}
	}
	private class BtnSnimiKonacnoActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if(cbLists.getSelectedIndex() == -1) return;
			try{
				ScheduledItemsType type = cbLists.getItemAt(cbLists.getSelectedIndex());
				Path path = Paths.get("mcats", type.name.toLowerCase() + ".rek_temp");
				TimeEntry entry;
				OutputStream os = Files.newOutputStream(path, StandardOpenOption.CREATE_NEW, 
						StandardOpenOption.WRITE);
				ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeUTF(type.name);
				String filePath = (type.najava == null)?"null":type.najava.fullPath;
				oos.writeUTF(filePath);
				filePath = (type.odjava == null)?"null":type.odjava.fullPath;
				oos.writeUTF(filePath);
				oos.writeByte(type.prioritet);
				oos.writeInt(type.items.size());
				ScheduledItem item;
				for(int i=0,len=type.items.size();i<len;i++){
					item = type.items.get(i);
					oos.writeUTF(item.fullPath);
					oos.writeLong(item.begin);
					oos.writeLong(item.end);
					oos.writeByte(item.mestoUBloku);
					oos.writeBoolean(item.ponedeljak);
					oos.writeBoolean(item.utorak);
					oos.writeBoolean(item.sreda);
					oos.writeBoolean(item.cetvrtak);
					oos.writeBoolean(item.petak);
					oos.writeBoolean(item.subota);
					oos.writeBoolean(item.nedelja);
					int jLen = item.runTimes.size();
					oos.writeInt(jLen);
					for(int j=0;j<jLen;j++){
						entry = item.runTimes.get(j);
						oos.writeInt(entry.time);
						oos.writeByte(entry.rank);
					}
				}
				oos.close();
				os.close();
				Files.delete(Paths.get("mcats", type.name + ".rek"));
				Files.move(path, Paths.get("mcats", type.name + ".rek"), 
						StandardCopyOption.REPLACE_EXISTING);
				PlayerWin.getErrDlg().showError("Snimanje kategorije uspešno.", "Obaveštenje");
			}catch(Exception e){
				e.printStackTrace(System.out);
				PlayerWin.getErrDlg().showError("Desila se greška tokom snimanja kategorije.");
			}
		}
	}
	private class BtnPrikaziActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if(cbLists.getSelectedIndex() == -1) return;
			try{
				String str = tfPrevDate.getText().trim();
				str = str.replace('/', '.');
				str = str.replace(',', '.');
				cal.setTime(sdf.parse(str));
			}catch(ParseException e){
				e.printStackTrace(System.out);
				prevTextArea.setText("");
				PlayerWin.getErrDlg().showError("Pogrešno unešen datum pregleda.");
				return;
			}
			int time = parseTime(tfPrevTime.getText().trim());
			if(time == -1){
				prevTextArea.setText("");
				PlayerWin.getErrDlg().showError("Pogrešno unešeno vreme pregleda.");
				return;
			}
			cal.set(Calendar.HOUR_OF_DAY, time / 60);
			cal.set(Calendar.MINUTE, time % 60);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			ScheduledItemsType type = (ScheduledItemsType)(cbLists.getSelectedItem());
			List<ScheduledItem> list = type.generateBlock(cal.getTimeInMillis());
			String str = (list.size() == 0)?"":list.get(0).fileName;
			for(int i=1,len=list.size();i<len;i++)
				str += "\n" + list.get(i).fileName;
			prevTextArea.setText(str);
		}
	}
	private class ItemsListKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent ke) {
			if(cbLists.getSelectedIndex() == -1) return;
			if((ke.getKeyCode() == KeyEvent.VK_DELETE) && (ke.getModifiers() == 0)){
				int rows[] = itemsList.getSelectedIndices();
				DefaultListModel<ScheduledItem> model = itemsList.getListModel();
				ScheduledItemsType type = (ScheduledItemsType)cbLists.getSelectedItem();
				for(int i=rows.length-1;i>=0;i--){
					model.removeElementAt(rows[i]);
					type.items.remove(rows[i]);
				}
			}
		}
	}
	private class TimesListListSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if(canProccessTimes && !event.getValueIsAdjusting()){
				if(timesList.getSelectedIndex() == -1){
					cbMestoUBloku.setEnabled(false);
					return;
				}
				TimeEntry entry = timesList.getSelectedValue();
				cbMestoUBloku.setEnabled(true);
				cbMestoUBloku.setSelectedIndex(entry.rank);
				tfPrevTime.setText(entry.toString());
			}
		}
	}
	private class ThisWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent event) {
			canProccessList = false;
			canProccessTimes = false;
			disableControls();
			cbLists.removeAllItems();
			tfPrevDate.setText("");
			tfPrevTime.setText("");
			prevTextArea.setText("");
			((DefaultListModel<ScheduledItem>)itemsList.getModel()).removeAllElements();
			canProccessList = true;
			canProccessTimes = true;
		}
	}
	private class CbMestoUBlokuItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent event) {
			if(canProccessTimes && (event.getStateChange() == ItemEvent.SELECTED) && 
					(cbMestoUBloku.getSelectedIndex() != -1)){
				TimeEntry entry = timesList.getSelectedValue();
				if(entry == null) return;
				entry.rank = (byte)(cbMestoUBloku.getSelectedIndex());
			}
		}
	}
	private class CbGlavniPrioritetItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent event) {
			if(event.getStateChange() == ItemEvent.SELECTED){
				if(cbLists.getSelectedIndex() == -1) return;
				ScheduledItemsType type = (ScheduledItemsType)cbLists.getSelectedItem();
				type.prioritet = (byte)(cbGlavniPrioritet.getSelectedIndex() + 1);
			}
		}
	}
	private class IntroContentListener implements ContentListener {
		@Override
		public void contentChanged(ContentEvent event) {
			if (canProccessIntroOutro) {
				ScheduledItemsType type = (ScheduledItemsType)cbLists.getSelectedItem();
				if(type == null) return;
				if(event.getChange() == TraitChange.REMOVED){
					type.najava = null;
				}
				if((event.getChange() == TraitChange.ADDED) || (event.getChange() == TraitChange.EDITED)){
					type.najava = ((JSongField)event.getSource()).getItem();
				}
			}
		}
	}
	private class OutroContentListener implements ContentListener {
		@Override
		public void contentChanged(ContentEvent event) {
			if (canProccessIntroOutro) {
				ScheduledItemsType type = (ScheduledItemsType)cbLists.getSelectedItem();
				if(type == null) return;
				if(event.getChange() == TraitChange.REMOVED){
					type.odjava = null;
				}
				if((event.getChange() == TraitChange.ADDED) || (event.getChange() == TraitChange.EDITED)){
					type.odjava = ((JSongField)event.getSource()).getItem();
				}
			}
		}
	}
}
