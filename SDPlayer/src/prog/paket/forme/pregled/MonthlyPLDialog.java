package prog.paket.forme.pregled;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JPopupMenu;
import javax.swing.ListModel;
import javax.swing.SpringLayout;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JButton;

import prog.paket.dodaci.ContentEvent;
import prog.paket.dodaci.ContentListener;
import prog.paket.dodaci.JPLayList;
import prog.paket.dodaci.ListJItem;
import prog.paket.forme.reklame.ScheduledItem;
import prog.paket.forme.reklame.ScheduledItemsType;
import prog.paket.playlist.generator.PlayerWin;
import prog.paket.playlist.generator.struct.PLGenerator;
import prog.paket.playlist.generator.struct.ProgSection;
import prog.paket.playlist.generator.struct.ProgSectionType;
import prog.paket.playlist.generator.struct.RekFileFilter;
import prog.paket.playlist.generator.struct.SecFileFilter;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.ListSelectionModel;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JComboBox;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class MonthlyPLDialog extends JFrame {

	private static final long serialVersionUID = -5830597288706181280L;

	private JPanel contentPane;
	public JScrollPane spDani;
	public JLabel lblTermini;
	public ProgSectionList listaTermina;
	public JScrollPane spLista;
	public JPLayList listaPesama;
	public JButton btnGeneratePL;

	private boolean canProccessItems = true, canProccessCatList = true;
	public JButton btnSnimiIzmene;
	public JLabel lblStart;
	public JTextField tfPocDatum;
	public JTextField tfPocVreme;
	public JLabel lblEnd;
	public JTextField tfKrajDatum;
	public JTextField tfKrajVreme;
	public JSeparator separator;

	private TraitMap traitMap;

	public void loadPlayList(){
		canProccessCatList = false;
		cbPrikaz.removeAllItems();
		cbPrikaz.addItem(new TraitInfo(-2, "Prikazati normalno", "", Integer.MIN_VALUE));
		cbPrikaz.addItem(new TraitInfo(-1, "Prikazati trajanje", "", Integer.MIN_VALUE));
		cbPrikaz.addItem(new TraitInfo(0, "Svim podkategorijama", "", Integer.MIN_VALUE));
		traitMap = new TraitMap();
		Set<Integer> keys = traitMap.keySet();
		for(Iterator<Integer> iter=keys.iterator();iter.hasNext();)
			cbPrikaz.addItem(traitMap.get(iter.next()));
		cbPrikaz.setSelectedIndex(0);
		canProccessCatList = true;
		// Termini
		canProccessItems = false;
		try{
			DefaultListModel<ProgSection> model = (DefaultListModel<ProgSection>)listaTermina.getModel();
			model.removeAllElements();
			File dir = new File("plists");
			File files[] = dir.listFiles(new SecFileFilter());
			ProgSection sec;
			HashMap<String, ListJItem> map = new HashMap<String, ListJItem>();
			for(int i=0,len=files.length;i<len;i++){
				sec = ProgSection.load(files[i], map);
				model.addElement(sec);
			}
			listaTermina.setSelectedIndex(-1);
			((DefaultListModel<ListJItem>)listaPesama.getModel()).removeAllElements();
		}catch(Exception e){
			e.printStackTrace(System.out);
			PlayerWin.getErrDlg().showError("Desila se greška prilikom učitavanja generisane plej liste.");
		}
		canProccessItems = true;
	}

	private ScheduledItemsType[] getScheduledTypes(){
		try{
			File dir = new File("mcats");
			File files[] = dir.listFiles(new RekFileFilter());
			ScheduledItemsType ret[] = new ScheduledItemsType[files.length];
			for(int i=0,len=files.length;i<len;i++)
				ret[i] = ScheduledItemsType.load(files[i]);
			return ret;
		}catch(Exception e){
			e.printStackTrace(System.out);
			return null;
		}
	}

	private void refreshCommercials(){
		ProgSection sec;
		ScheduledItemsType types[] = getScheduledTypes(), type;
		if(types == null){
			PlayerWin.getErrDlg().showError("Desila se greška tokom čitanja kategorija reklama/oglasa.");
			return;
		}
		List<ScheduledItem> list;
		for(int i=0,len=types.length;i<len;i++){
			type = types[i];
			for(int j=listaTermina.getModel().getSize()-1;j>=0;j--){
				sec = listaTermina.getModel().getElementAt(j);
				if((sec.sectionType == ProgSectionType.REKLAME) && (sec.catName.equals(type.name))){
					list = type.generateBlock(sec.scheduledTime);
					if(list.size() == 0){
						DefaultListModel<ProgSection> model = 
								(DefaultListModel<ProgSection>)listaTermina.getModel();
						model.removeElementAt(j);
						continue;
					}
					sec.songs.clear();
					if(type.najava != null)
						sec.songs.add(type.najava);
					for(int q=0,qLen=list.size();q<qLen;q++)
						sec.songs.add(list.get(q));
					if(type.odjava != null)
						sec.songs.add(type.odjava);
				}
			}
		}
	}

	/**
	 * Create the frame.
	 */
	public MonthlyPLDialog() {
		setBounds(100, 100, 731, 419);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		lblTermini = new JLabel("Termini:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblTermini, 2, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblTermini, 0, SpringLayout.NORTH, contentPane);
		contentPane.add(lblTermini);
		
		spDani = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, spDani, 0, SpringLayout.SOUTH, lblTermini);
		sl_contentPane.putConstraint(SpringLayout.WEST, spDani, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, spDani, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, spDani, 250, SpringLayout.WEST, contentPane);
		contentPane.add(spDani);
		
		listaTermina = new ProgSectionList();
		listaTermina.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listaTermina.addListSelectionListener(new ListaTerminaListSelectionListener());
		listaTermina.setTransferHandler(new ListaTerminaTransferHandler());
		spDani.setViewportView(listaTermina);
		
		spLista = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, spLista, 0, SpringLayout.NORTH, spDani);
		sl_contentPane.putConstraint(SpringLayout.WEST, spLista, 0, SpringLayout.EAST, spDani);
		contentPane.add(spLista);
		
		listaPesama = new JPLayList("sectionSongList");
		listaPesama.addMouseMotionListener(new ListaPesamaMouseMotionListener());
		listaPesama.setTransferHandler(new ListaPesamaTransferHandler());
		listaPesama.addMouseListener(new ItemsListmouseListener());
		listaPesama.addContentListener(new ListaPesamaContentListener());
		spLista.setViewportView(listaPesama);
		
		btnGeneratePL = new JButton("Generiši plej listu");
		sl_contentPane.putConstraint(SpringLayout.EAST, spLista, -5, SpringLayout.WEST, btnGeneratePL);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnGeneratePL, -160, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnGeneratePL, -5, SpringLayout.EAST, contentPane);
		btnGeneratePL.addActionListener(new BtnGenerateActionListener());
		contentPane.add(btnGeneratePL);
		
		btnSnimiIzmene = new JButton("Snimi plej listu");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnSnimiIzmene, 0, SpringLayout.WEST, btnGeneratePL);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnSnimiIzmene, 0, SpringLayout.EAST, btnGeneratePL);
		btnSnimiIzmene.addActionListener(new BtnSnimiIzmeneActionListener());
		contentPane.add(btnSnimiIzmene);
		
		lblStart = new JLabel("Početak:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblStart, 0, SpringLayout.NORTH, lblTermini);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblStart, 0, SpringLayout.WEST, btnGeneratePL);
		contentPane.add(lblStart);
		
		tfPocDatum = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.WEST, tfPocDatum, 0, SpringLayout.WEST, btnGeneratePL);
		DatumFocusListener datumFocusListener = new DatumFocusListener();
		VremeFocusListener vremeFocusListener = new VremeFocusListener();
		tfPocDatum.addFocusListener(datumFocusListener);
		sl_contentPane.putConstraint(SpringLayout.NORTH, tfPocDatum, 0, SpringLayout.NORTH, spDani);
		contentPane.add(tfPocDatum);
		tfPocDatum.setColumns(7);
		
		tfPocVreme = new JTextField();
		tfPocVreme.addFocusListener(vremeFocusListener);
		sl_contentPane.putConstraint(SpringLayout.NORTH, tfPocVreme, -1, SpringLayout.NORTH, spDani);
		sl_contentPane.putConstraint(SpringLayout.WEST, tfPocVreme, 6, SpringLayout.EAST, tfPocDatum);
		contentPane.add(tfPocVreme);
		tfPocVreme.setColumns(4);
		
		lblEnd = new JLabel("Kraj:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblEnd, 5, SpringLayout.SOUTH, tfPocDatum);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblEnd, 6, SpringLayout.EAST, spLista);
		contentPane.add(lblEnd);
		
		tfKrajDatum = new JTextField();
		tfKrajDatum.addFocusListener(datumFocusListener);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnGeneratePL, 5, SpringLayout.SOUTH, tfKrajDatum);
		sl_contentPane.putConstraint(SpringLayout.NORTH, tfKrajDatum, 0, SpringLayout.SOUTH, lblEnd);
		sl_contentPane.putConstraint(SpringLayout.EAST, tfKrajDatum, 0, SpringLayout.EAST, tfPocDatum);
		tfKrajDatum.setColumns(7);
		contentPane.add(tfKrajDatum);
		
		tfKrajVreme = new JTextField();
		tfKrajVreme.addFocusListener(vremeFocusListener);
		sl_contentPane.putConstraint(SpringLayout.WEST, tfKrajVreme, 0, SpringLayout.WEST, tfPocVreme);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, tfKrajVreme, 0, SpringLayout.SOUTH, tfKrajDatum);
		tfKrajVreme.setColumns(4);
		contentPane.add(tfKrajVreme);
		
		separator = new JSeparator();
		sl_contentPane.putConstraint(SpringLayout.NORTH, separator, 10, SpringLayout.SOUTH, btnGeneratePL);
		sl_contentPane.putConstraint(SpringLayout.WEST, separator, 0, SpringLayout.WEST, btnGeneratePL);
		sl_contentPane.putConstraint(SpringLayout.EAST, separator, 0, SpringLayout.EAST, btnGeneratePL);
		contentPane.add(separator);
		
		btnRefreshComm = new JButton("Osveži reklame");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRefreshComm, 10, SpringLayout.SOUTH, separator);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnRefreshComm, 0, SpringLayout.WEST, btnGeneratePL);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRefreshComm, 0, SpringLayout.EAST, btnGeneratePL);
		btnRefreshComm.addActionListener(new BtnRefreshCommActionListener());
		contentPane.add(btnRefreshComm);
		
		btnAdjustTimes = new JButton("Koriguj vremena");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnAdjustTimes, 0, SpringLayout.WEST, btnGeneratePL);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnAdjustTimes, 0, SpringLayout.EAST, btnGeneratePL);
		btnAdjustTimes.addActionListener(new BtnAdjustTimesActionListener());
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnAdjustTimes, 6, SpringLayout.SOUTH, btnRefreshComm);
		contentPane.add(btnAdjustTimes);
		
		separator_1 = new JSeparator();
		sl_contentPane.putConstraint(SpringLayout.WEST, separator_1, 0, SpringLayout.WEST, btnGeneratePL);
		sl_contentPane.putConstraint(SpringLayout.EAST, separator_1, 0, SpringLayout.EAST, btnGeneratePL);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnSnimiIzmene, 10, SpringLayout.SOUTH, separator_1);
		sl_contentPane.putConstraint(SpringLayout.NORTH, separator_1, 10, SpringLayout.SOUTH, btnAdjustTimes);
		contentPane.add(separator_1);
		
		cbPrikaz = new JComboBox<TraitInfo>();
		sl_contentPane.putConstraint(SpringLayout.SOUTH, spLista, -5, SpringLayout.NORTH, cbPrikaz);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, cbPrikaz, 0, SpringLayout.SOUTH, spDani);
		sl_contentPane.putConstraint(SpringLayout.EAST, cbPrikaz, 0, SpringLayout.EAST, btnGeneratePL);
		cbPrikaz.setMaximumRowCount(20);
		cbPrikaz.addItemListener(new CbPrikazItemListener());
		contentPane.add(cbPrikaz);
		
		lblPrikaz = new JLabel("Pikaži sadržane podkategorije od:");
		sl_contentPane.putConstraint(SpringLayout.WEST, cbPrikaz, 6, SpringLayout.EAST, lblPrikaz);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblPrikaz, -5, SpringLayout.SOUTH, spDani);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblPrikaz, 6, SpringLayout.EAST, spDani);
		contentPane.add(lblPrikaz);
		
		lblSadrzaj = new JLabel("Sadržaj");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblSadrzaj, 0, SpringLayout.NORTH, lblTermini);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblSadrzaj, 0, SpringLayout.WEST, spLista);
		contentPane.add(lblSadrzaj);
		
		JPopupMenu listaPesamaPopupMenu = new JPopupMenu();
		listaPesamaPopupMenu.add(new ActMakeReportAction(listaTermina, listaPesama));
		listaPesamaPopupMenu.add(new ActPreviewAction(listaPesama));
		listaPesamaPopupMenu.addSeparator();
		listaPesamaPopupMenu.add(new ActSplitSectionNoInOutAction(listaTermina, listaPesama));
		listaPesamaPopupMenu.add(new ActSplitSectionWithInOutAction(listaTermina, listaPesama));
		listaPesamaPopupMenu.addSeparator();
		listaPesamaPopupMenu.add(new ActRemoveSongsAction(listaTermina, listaPesama));
		listaPesama.setComponentPopupMenu(listaPesamaPopupMenu);
		
		JPopupMenu listaTerminaPopupMenu = new JPopupMenu();
		listaTerminaPopupMenu.add(new ActProvideSectionInOutAction(listaTermina, listaPesama));
		listaTerminaPopupMenu.add(new ActMergeSectionsAction(listaTermina, listaPesama));
		listaTerminaPopupMenu.addSeparator();
		listaTerminaPopupMenu.add(new ActDeleteSectionsAction(listaTermina));
		listaTermina.setComponentPopupMenu(listaTerminaPopupMenu);
		
	}
	private class ListaTerminaListSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent lse) {
			if(!lse.getValueIsAdjusting()){
				canProccessItems = false;
				if(listaTermina.getSelectedIndex() == -1){
					((DefaultListModel<ListJItem>)listaPesama.getModel()).clear();
					return;
				}
				ProgSection sec = listaTermina.getSelectedValue();
				DefaultListModel<ListJItem> model = (DefaultListModel<ListJItem>)listaPesama.getModel();
				model.removeAllElements();
				for(int i=0,len=sec.songs.size();i<len;i++)
					model.addElement(sec.songs.get(i));
				changeItemsText(((TraitInfo)cbPrikaz.getSelectedItem()).getId());
				canProccessItems = true;
			}
		}
	}
	private void clearSavedSections(){
		try{
			File dir = new File("plists");
			File files[] = dir.listFiles(new SecFileFilter());
			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
	}
	private class BtnSnimiIzmeneActionListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			DefaultListModel<ProgSection> model = (DefaultListModel<ProgSection>)listaTermina.getModel();
			try{
				clearSavedSections();
				ProgSection sec;
				for(int i=0,len=model.getSize();i<len;i++){
					sec = model.getElementAt(i);
					ProgSection.save(sec);
				}
				PlayerWin.getErrDlg().showError("Snimanje uspešno završeno.", "Obaveštenje");
			}catch(Exception e){
				e.printStackTrace();
				PlayerWin.getErrDlg().showError("Desila se greška prilikom<br/>snimanja generisane plej liste.");
			}
		}
	}
	private SimpleDateFormat sdf = new SimpleDateFormat("d.M.yyyy");
	public JButton btnRefreshComm;
	public JButton btnAdjustTimes;
	public JSeparator separator_1;
	public JComboBox<TraitInfo> cbPrikaz;
	public JLabel lblPrikaz;
	public JLabel lblSadrzaj;
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
	private class BtnGenerateActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			Calendar cal = new GregorianCalendar();
			Date date;
			try{
				date = sdf.parse(tfPocDatum.getText());
				cal.setTime(date);
			}catch(ParseException e){
				PlayerWin.getErrDlg().showError("Pogrešno unešen početni datum.");
				return;
			}
			int time = parseTime(tfPocVreme.getText());
			if(time == -1){
				PlayerWin.getErrDlg().showError("Pogrešno unešeno početno vreme.");
				return;
			}
			cal.set(Calendar.HOUR_OF_DAY, time / 60);
			cal.set(Calendar.MINUTE, time % 60);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			long begin, end;
			begin = cal.getTimeInMillis();
			try{
				date = sdf.parse(tfKrajDatum.getText());
				cal.setTime(date);
			}catch(ParseException e){
				PlayerWin.getErrDlg().showError("Pogrešno unešen završni datum.");
				return;
			}
			time = parseTime(tfKrajVreme.getText());
			if(time == -1){
				PlayerWin.getErrDlg().showError("Pogrešno unešeno završno vreme.");
				return;
			}
			cal.set(Calendar.HOUR_OF_DAY, time / 60);
			cal.set(Calendar.MINUTE, time % 60);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			end = cal.getTimeInMillis();
			// Generacija liste
			try{
				PLGenerator generator = new PLGenerator();
				generator.generate(begin, end);
				DefaultListModel<ProgSection> sections = (DefaultListModel<ProgSection>)listaTermina.getModel();
				sections.removeAllElements();
				for(int i=0,len=generator.sections.size();i<len;i++)
					sections.addElement(generator.sections.get(i));
				listaTermina.setSelectedIndex(-1);
			}catch(Exception e){
				try {
					e.printStackTrace(System.out);
					FileOutputStream fos = new FileOutputStream("greske.txt", true);
					PrintWriter writer = new PrintWriter(fos);
					writer.print("Time in milis: ");
					writer.println(System.currentTimeMillis());
					e.printStackTrace(writer);
					writer.close();
				} catch (Exception e1) {
					e1.printStackTrace(System.out);
				}
			}
		}
	}
	private class BtnAdjustTimesActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			listaTermina.correctStartTimes();
			listaTermina.repaint();
		}
	}
	private class BtnRefreshCommActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			refreshCommercials();
			listaTermina.correctStartTimes();
		}
	}
	private class ListaPesamaContentListener implements ContentListener {
		@Override
		public void contentChanged(ContentEvent event) {
			if(canProccessItems){
				if(listaTermina.getSelectedIndex() == -1) return;
				ProgSection sec = listaTermina.getSelectedValue();
				DefaultListModel<ListJItem> model = listaPesama.getListModel();
				sec.songs.clear();
				for(int i=0,len=model.getSize();i<len;i++)
					sec.songs.add(model.getElementAt(i));
			}
		}
	}
	private class ItemsListmouseListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			if(e.getButton() == MouseEvent.BUTTON3)
				listaPesama.setSelectedIndex(listaPesama.locationToIndex(e.getPoint()));
		}
	}
	private class ListaPesamaMouseMotionListener extends MouseMotionAdapter {
		@Override
		public void mouseDragged(MouseEvent event) {
			if(listaTermina.getDropMode() != DropMode.ON)
				listaTermina.setDropMode(DropMode.ON);
		}
	}
	private String formatDuration(long duration){
		int temp = (int)(duration / 1000000L);
		String ret = String.valueOf(temp % 60);
		if(ret.length() == 1)
			ret += "0";
		ret = String.valueOf(temp / 60) + ":" + ret;
		return ret;
	}
	private void changeItemsText(Integer id){
		ListJItem item;
		ListModel<ListJItem> model = listaPesama.getModel();
		File file;
		String text;
		for(int i=0,len=model.getSize();i<len;i++){
			item = model.getElementAt(i);
			file = new File(item.fullPath);
			text = file.getName();
			text = text.substring(0, text.length() - 4);
			item.fileName = text;
		}
		/*
		 * TraitInfo(-2, "Prikazati normalno", "", Integer.MIN_VALUE);
		 * TraitInfo(-1, "Prikazati trajanje", "", Integer.MIN_VALUE);
		 * TraitInfo(0, "Svim podkategorijama", "", Integer.MIN_VALUE);
		 */
		switch(id.intValue()){
		case -2:
			break;
		case -1:
			for(int i=0,len=model.getSize();i<len;i++){
				item = model.getElementAt(i);
				item.fileName += " - " + formatDuration(item.duration);
			}
			break;
		default:
			for(int i=0,len=model.getSize();i<len;i++){
				item = model.getElementAt(i);
				item.fileName += " - " + traitMap.getAllAbrevs(item.cats, id);
			}
		}
		listaPesama.repaint();
	}
	private class CbPrikazItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent event) {
			if(canProccessCatList && (event.getStateChange() == ItemEvent.SELECTED)){
				TraitInfo info = (TraitInfo)event.getItem();
				changeItemsText(info.getId());
			}
		}
	}
}
