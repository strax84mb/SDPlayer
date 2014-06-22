package prog.paket.forme.emisije;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import java.awt.Font;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;

import prog.paket.baza.struct.menutree.TraitChange;
import prog.paket.dodaci.ContentEvent;
import prog.paket.dodaci.ContentListener;
import prog.paket.dodaci.JPLayList;
import prog.paket.dodaci.ListJItem;
import prog.paket.dodaci.SDModalResult;
import prog.paket.forme.pregled.ActPreviewAction;
import prog.paket.forme.pregled.TraitInfo;
import prog.paket.forme.pregled.TraitMap;
import prog.paket.playlist.generator.CatNameDialog;
import prog.paket.playlist.generator.ErrorInfoDialog;
import prog.paket.playlist.generator.PlayerWin;
import prog.paket.playlist.generator.ScheadulingWindow;
import prog.paket.playlist.generator.YesNoForm;
import prog.paket.playlist.generator.struct.CatWithList;
import prog.paket.playlist.generator.struct.PesFileFilter;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class PLGeneratorWindow extends JDialog{

	private static final long serialVersionUID = -8868150933244227837L;

	private CatNameDialog catNameDlg = new CatNameDialog();
	// ErrorInfoDialog treba sprovesti iz glavnog prozora
	private ScheadulingWindow schWin = null;

	private JPanel contentPane;
	public JButton btnDodaj;
	public JButton btnPreimenuj;
	public JButton btnObrisi;
	public JButton btnPodesi;
	public JScrollPane scrollPane;
	public JMusicCategoryTree catTree;
	public JScrollPane scrollPane_1;
	public JButton btnZatvori;
	public JPopupMenu catTreePopupMenu;
	public JPopupMenu songListPopupMenu;
	public JMenuItem mntmDodaj;
	public JMenuItem mntmPodesi;
	public JMenuItem mntmPreimenuj;
	public JMenuItem mntmObrii;
	public JPLayList list;

	public String loadedMCat;
	public JLabel lblPrikaz;
	public JComboBox<TraitInfo> cbPrikaz;
	public JSeparator separator;

	private TraitMap traitMap;
	private boolean canProccessTraitCB = true;

	public void saveCategoryPool(String month){
		try{
			File dir = new File("mcats");
			File files[] = dir.listFiles(new PesFileFilter());
			for(int i=0,len=files.length;i<len;i++)
				files[i].delete();
			CategoryTreeNode node;
			for(int nodeInd=0,nodeLen=catTree.getCategoryCount();nodeInd<nodeLen;nodeInd++){
				node = catTree.getCategoryNodeAt(nodeInd);
				CatWithList obj = new CatWithList(node.getDesc(), node.getSongList());
				if(!obj.save()){
					PlayerWin.getErrDlg().showError("Snimanje kategorije " + 
							((String)node.getUserObject()) + " nije uspelo.");
					return;
				}
			}
			PlayerWin.getErrDlg().showError("Snimanje uspešno završeno.", "Obaveštenje");
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
	}

	public void loadCategoryPool(){
		try{
			canProccessTraitCB = false;
			cbPrikaz.removeAllItems();
			cbPrikaz.addItem(new TraitInfo(-2, "Prikazati normalno", "", Integer.MIN_VALUE));
			cbPrikaz.addItem(new TraitInfo(-1, "Prikazati trajanje", "", Integer.MIN_VALUE));
			cbPrikaz.addItem(new TraitInfo(0, "Svim podkategorijama", "", Integer.MIN_VALUE));
			traitMap = new TraitMap();
			Set<Integer> keys = traitMap.keySet();
			for(Iterator<Integer> iter=keys.iterator();iter.hasNext();)
				cbPrikaz.addItem(traitMap.get(iter.next()));
			cbPrikaz.setSelectedIndex(0);
			canProccessTraitCB = true;
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
		try{
			File dir = new File("mcats");
			File files[] = dir.listFiles(new PesFileFilter());
			CatWithList cl;
			CategoryTreeNode node;
			String name;
			catTree.removeAllCategories();
			for(int i=0,len=files.length;i<len;i++){
				name = files[i].getName();
				name = name.substring(0, name.length() - 4);
				cl = CatWithList.load(name);
				node = catTree.addCategory(cl.cat.ime);
				if((cl == null) || (node == null)){
					PlayerWin.getErrDlg().showError("Učitavanje kategorije " + name + " nije uspešno.");
					continue;
				}
				node.setDescValues(cl.cat);
				node.setSongList(cl.list);
			}
			catTree.reload(null);
		}catch(Exception e){
			e.printStackTrace(System.out);
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
		ListModel<ListJItem> model = list.getModel();
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
		list.repaint();
	}

	public void repopulateSongList(){
		if(catTree.getSelectionPath() == null){
			list.setEnabled(false);
			return;
		}
		list.setEnabled(true);
		Object obj = catTree.getSelectionPath().getLastPathComponent();
		List<ListJItem> songList = null;
		if(obj instanceof RankedTreeNode){
			songList = ((RankedTreeNode)obj).getSongList();
		}else{
			songList = ((CategoryTreeNode)obj).getSongList();
		}
		DefaultListModel<ListJItem> model = list.getListModel();
		model.removeAllElements();
		for(int i=0,len=songList.size();i<len;i++)
			model.addElement(songList.get(i));
		changeItemsText(((TraitInfo)cbPrikaz.getSelectedItem()).getId());
	}

	/**
	 * Create the frame.
	 */
	public PLGeneratorWindow() {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 607, 551);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		btnDodaj = new JButton("Dodaj");
		btnDodaj.addActionListener(new BtnDodajActionListener());
		
		btnPreimenuj = new JButton("Preimenuj");
		btnPreimenuj.addActionListener(new BtnPreimenujActionListener());
		
		btnObrisi = new JButton("Obri\u0161i");
		btnObrisi.addActionListener(new BtnObrisiActionListener());
		
		btnPodesi = new JButton("Podesi");
		btnPodesi.addActionListener(new BtnPodesiActionListener());
		
		scrollPane = new JScrollPane();
		
		scrollPane_1 = new JScrollPane();
		
		btnZatvori = new JButton("Snimi");
		btnZatvori.addActionListener(new BtnZatvoriActionListener());
		
		list = new JPLayList("programSongList");
		list.setEnabled(false);
		list.addKeyListener(new SongListKeyListener());
		list.addContentListener(new SongListContentListener());
		scrollPane_1.setViewportView(list);
		
		catTree = new JMusicCategoryTree();
		catTree.setRootVisible(false);
		catTree.setFont(new Font("SansSerif", Font.PLAIN, 14));
		catTree.addTreeSelectionListener(new CatTreeSelectionListener());
		catTree.setTransferHandler(new MusicTreeTransferHandler(this));
		scrollPane.setViewportView(catTree);
		
		catTreePopupMenu = new JPopupMenu();
		addPopup(catTree, catTreePopupMenu);
		
		songListPopupMenu = new JPopupMenu();
		songListPopupMenu.add(new SongListPopupRemoveAction());
		songListPopupMenu.addSeparator();
		songListPopupMenu.add(new ActPreviewAction(list));
		addPopup(list, songListPopupMenu);
		
		mntmDodaj = new JMenuItem("Dodaj");
		mntmDodaj.addActionListener(new MntmDodajActionListener());
		catTreePopupMenu.add(mntmDodaj);
		
		mntmPodesi = new JMenuItem("Podesi");
		mntmPodesi.addActionListener(new MntmPodesiActionListener());
		catTreePopupMenu.add(mntmPodesi);
		
		mntmPreimenuj = new JMenuItem("Preimenuj");
		mntmPreimenuj.addActionListener(new MntmPreimenujActionListener());
		catTreePopupMenu.add(mntmPreimenuj);
		
		mntmObrii = new JMenuItem("Obri\u0161i");
		mntmObrii.addActionListener(new MntmObriiActionListener());
		catTreePopupMenu.add(mntmObrii);
		SpringLayout sl_contentPane = new SpringLayout();
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane_1, 0, SpringLayout.NORTH, btnPodesi);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnZatvori, -5, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 146, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 5, SpringLayout.WEST, btnPodesi);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -6, SpringLayout.NORTH, btnZatvori);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, 0, SpringLayout.EAST, btnDodaj);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnZatvori, 0, SpringLayout.WEST, btnDodaj);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnZatvori, 0, SpringLayout.EAST, btnDodaj);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane_1, 0, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane_1, 5, SpringLayout.EAST, btnPodesi);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnObrisi, 0, SpringLayout.WEST, btnPodesi);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnObrisi, 0, SpringLayout.EAST, btnPodesi);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnPreimenuj, 0, SpringLayout.WEST, btnPodesi);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnPreimenuj, 0, SpringLayout.EAST, btnPodesi);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnDodaj, 0, SpringLayout.WEST, btnPodesi);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnDodaj, 0, SpringLayout.EAST, btnPodesi);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnPodesi, 180, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnPodesi, 5, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnPodesi, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnObrisi, 107, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnPreimenuj, 73, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnDodaj, 39, SpringLayout.NORTH, contentPane);
		contentPane.setLayout(sl_contentPane);
		contentPane.add(btnDodaj);
		contentPane.add(btnPreimenuj);
		contentPane.add(btnObrisi);
		contentPane.add(btnPodesi);
		contentPane.add(btnZatvori);
		contentPane.add(scrollPane);
		contentPane.add(scrollPane_1);
		
		lblPrikaz = new JLabel("Pikaži sadržane podkategorije od:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblPrikaz, 10, SpringLayout.WEST, scrollPane_1);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane_1, 0, SpringLayout.NORTH, lblPrikaz);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblPrikaz, 0, SpringLayout.SOUTH, scrollPane);
		contentPane.add(lblPrikaz);
		
		cbPrikaz = new JComboBox<TraitInfo>();
		cbPrikaz.addItemListener(new CbPrikazItemListener());
		sl_contentPane.putConstraint(SpringLayout.NORTH, cbPrikaz, 0, SpringLayout.NORTH, btnZatvori);
		sl_contentPane.putConstraint(SpringLayout.WEST, cbPrikaz, 0, SpringLayout.WEST, lblPrikaz);
		sl_contentPane.putConstraint(SpringLayout.EAST, cbPrikaz, 0, SpringLayout.EAST, contentPane);
		contentPane.add(cbPrikaz);
		
		separator = new JSeparator();
		sl_contentPane.putConstraint(SpringLayout.NORTH, separator, 0, SpringLayout.NORTH, lblPrikaz);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, separator, 0, SpringLayout.SOUTH, contentPane);
		separator.setOrientation(SwingConstants.VERTICAL);
		sl_contentPane.putConstraint(SpringLayout.WEST, separator, 6, SpringLayout.EAST, scrollPane);
		contentPane.add(separator);
		
		if(schWin == null)
			schWin = new ScheadulingWindow(new ErrorInfoDialog());
	}
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					if(e.getComponent() instanceof JMusicCategoryTree){
						JMusicCategoryTree mct = (JMusicCategoryTree)e.getComponent();
						TreePath path = mct.getPathForLocation(e.getX(), e.getY());
						mct.setSelectionPath(path);
					}
					showMenu(e);
					System.out.println(e.getComponent().getName());
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
	private class BtnZatvoriActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			saveCategoryPool(loadedMCat);
		}
	}
	private class SongListPopupRemoveAction extends AbstractAction {
		private static final long serialVersionUID = 5784825411960858880L;
		public SongListPopupRemoveAction(){
			putValue(NAME, "Obriši");
		}
		@Override
		public void actionPerformed(ActionEvent ae) {
			list.removeSelectedItems();
		}
	}
	private class BtnDodajActionListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			catNameDlg.tfNaziv.setText("");
			catNameDlg.setLocation(btnDodaj.getLocationOnScreen().x + 10 + btnDodaj.getWidth(), 
					btnDodaj.getLocationOnScreen().y);
			catNameDlg.tfNaziv.requestFocusInWindow();
			catNameDlg.setVisible(true);
			if((catNameDlg.modalResult == SDModalResult.OK) && !catNameDlg.tfNaziv.getText().isEmpty()){
				String str = catNameDlg.tfNaziv.getText().trim();
				if(str.isEmpty()){
					PlayerWin.getErrDlg().showError("Niste uneli ime kategorije.");
					return;
				}
				if((str.indexOf(":") != -1) || (str.indexOf("/") != -1) || 
						(str.indexOf("?") != -1) || (str.indexOf("\\") != -1)){
					PlayerWin.getErrDlg().showError("Ime kategorije ne može da sadrži slova \' : , ? , / , \\ \'");
					return;
				}
				try{
					catTree.addCategory(str);
					catTree.reload(null);
				}catch(Exception e){
					e.printStackTrace(System.out);
					PlayerWin.getErrDlg().showError("Dodavanje nove kategorija nije uspelo.");
					return;
				}
			}
		}
	}
	private class BtnPreimenujActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			CategoryTreeNode node = catTree.getSelectedCategory();
			if(node == null) return;
			catNameDlg.tfNaziv.setText((String)node.getUserObject());
			catNameDlg.setLocation(btnPreimenuj.getLocationOnScreen().x + 10 + btnPreimenuj.getWidth(), 
					btnPreimenuj.getLocationOnScreen().y);
			catNameDlg.tfNaziv.requestFocusInWindow();
			catNameDlg.setVisible(true);
			if((catNameDlg.modalResult == SDModalResult.OK) && !catNameDlg.tfNaziv.getText().isEmpty()){
				String str = catNameDlg.tfNaziv.getText().trim();
				if(str.isEmpty()){
					PlayerWin.getErrDlg().showError("Niste uneli ime kategorije.");
					return;
				}
				if(str.equals((String)node.getUserObject())) return;
				if((str.indexOf(":") != -1) || (str.indexOf("/") != -1) || 
						(str.indexOf("?") != -1) || (str.indexOf("\\") != -1)){
					PlayerWin.getErrDlg().showError("Ime kategorije ne može da sadrži slova \' : , ? , / , \\ \'");
					return;
				}
				try{
					catTree.editCategoryName((String)node.getUserObject(), str);
					catTree.reloadByNode(node);
				}catch(Exception e){
					e.printStackTrace(System.out);
					PlayerWin.getErrDlg().showError("Preimenovanje kategorija nije uspelo.");
					return;
				}
			}
		}
	}
	private class BtnPodesiActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			CategoryTreeNode node = catTree.getSelectedCategory();
			if(node == null) return;
			schWin.popuniPolja(node.getDesc());
			schWin.shouldSave = false;
			schWin.setVisible(true);
			if(!schWin.shouldSave) return;
			node.setDescValues(schWin.cat);
			catTree.setSelectionPath(catTree.getSelectionCatPath(node));
			catTree.reload(null);
		}
	}
	private class BtnObrisiActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent event) {
			YesNoForm form = new YesNoForm("Jeste li sigurni da želite obrisati kategoriju?");
			form.showDialog(btnObrisi);
			if(!form.confirmed){
				form.dispose();
				return;
			}
			form.dispose();
			CategoryTreeNode node = catTree.getSelectedCategory();
			if(node == null) return;
			try{
				catTree.remove((String)node.getUserObject());
				catTree.reload(null);
			}catch(Exception e){
				e.printStackTrace(System.out);
				PlayerWin.getErrDlg().showError("Brisanje kategorija nije uspelo.");
			}
		}
	}
	private class SongListKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent ke) {
			if((ke.getKeyCode() == KeyEvent.VK_DELETE) && (ke.getModifiers() == 0)){
				TreePath path = catTree.getSelectionPath();
				if(path == null) return;
				CategoryTreeNode node = null;
				if(path.getLastPathComponent() instanceof CategoryTreeNode){
					node = (CategoryTreeNode)path.getLastPathComponent();
				}else if(path.getLastPathComponent() instanceof RankedTreeNode){
					node = (CategoryTreeNode)((RankedTreeNode)path.getLastPathComponent()).getParent();
				}else return;
				int rows[] = list.getSelectedIndices();
				DefaultListModel<ListJItem> model = list.getListModel();
				for(int i=rows.length-1;i>=0;i--){
					node.removeSong(model.remove(rows[i]));
				}
			}
		}
	}
	private class CatTreeSelectionListener implements TreeSelectionListener {
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			repopulateSongList();
		}
	}
	private class MntmDodajActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			btnDodaj.doClick();
		}
	}
	private class MntmPodesiActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			btnPodesi.doClick();
		}
	}
	private class MntmPreimenujActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			btnPreimenuj.doClick();
		}
	}
	private class MntmObriiActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			btnObrisi.doClick();
		}
	}
	private class CbPrikazItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent event) {
			if((event.getStateChange() == ItemEvent.SELECTED) && canProccessTraitCB){
				if(event.getItem() == null) return;
				changeItemsText(((TraitInfo)event.getItem()).getId());
			}
		}
	}
	private class SongListContentListener implements ContentListener {
		@Override
		public void contentChanged(ContentEvent event) {
			if(event.getChange() == TraitChange.ADDED){
				Object obj = catTree.getSelectionPath().getLastPathComponent();
				CategoryTreeNode node = null;
				ListModel<ListJItem> model = list.getModel();
				if(obj instanceof RankedTreeNode){
					node = (CategoryTreeNode)(((RankedTreeNode)obj).getParent());
				}else{
					node = (CategoryTreeNode)obj;
				}
				ListJItem item;
				for(int i=0,iLen=model.getSize();i<iLen;i++){
					item = model.getElementAt(i);
					if(!node.isSongInList(item))
						node.addSong(item);
				}
				repopulateSongList();
			}else if(event.getChange() == TraitChange.REMOVED){
				boolean found;
				Object obj = catTree.getSelectionPath().getLastPathComponent();
				List<ListJItem> songList = null;
				CategoryTreeNode node = null;
				ListModel<ListJItem> model = list.getModel();
				if(obj instanceof RankedTreeNode){
					node = (CategoryTreeNode)(((RankedTreeNode)obj).getParent());
					songList = ((RankedTreeNode)obj).getSongList();
				}else{
					node = (CategoryTreeNode)obj;
					songList = node.getSongList();
				}
				ListJItem item;
				for(int i=songList.size()-1;i>=0;i--){
					found = false;
					item = songList.get(i);
					for(int j=model.getSize()-1;j>=0 && !found;j--){
						if(model.getElementAt(j).equals(item))
							found = true;
					}
					if(found) node.removeSong(item);
				}
			}
		}
	}
}
