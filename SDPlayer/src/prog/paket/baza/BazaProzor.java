package prog.paket.baza;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.TransferHandler;


import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import prog.paket.baza.struct.SearchState;
import prog.paket.baza.struct.SongBaseTable;
import prog.paket.baza.struct.SongBaseTableModel;
import prog.paket.baza.struct.SongEntry;
import prog.paket.baza.struct.TraitMenuBar;
import prog.paket.dodaci.ListJItem;
import prog.paket.playlist.generator.ErrorInfoDialog;
import prog.paket.playlist.generator.PlayerWin;
import prog.paket.playlist.generator.YesNoForm;

import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class BazaProzor extends JDialog {

	private static final long serialVersionUID = 4907081729786298530L;

	public JScrollPane scrollPane;
	public SongBaseTable table;
	public JButton btnPodesi;
	public TraitMenuBar menuBar;
	public JLabel lblPoruka;
	public JButton btnPocisti;

	public DBFile songDB;
	public JLabel lblInclude;
	public JLabel lblExclude;
	public JButton btnObrii;

	/**
	 * Create the dialog.
	 */
	public BazaProzor() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1096, 423);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		lblPoruka = new JLabel("Formatna baza");
		springLayout.putConstraint(SpringLayout.NORTH, lblPoruka, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, lblPoruka, 0, SpringLayout.HORIZONTAL_CENTER, getContentPane());
		lblPoruka.setFont(new Font("SansSerif", Font.BOLD, 20));
		getContentPane().add(lblPoruka);
		
		lblInclude = new JLabel("Uključi: ");
		springLayout.putConstraint(SpringLayout.NORTH, lblInclude, 10, SpringLayout.SOUTH, lblPoruka);
		springLayout.putConstraint(SpringLayout.WEST, lblInclude, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(lblInclude);
		
		lblExclude = new JLabel("Isključi: ");
		springLayout.putConstraint(SpringLayout.NORTH, lblExclude, 7, SpringLayout.SOUTH, lblInclude);
		springLayout.putConstraint(SpringLayout.WEST, lblExclude, 0, SpringLayout.WEST, lblInclude);
		getContentPane().add(lblExclude);
		
		scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 10, SpringLayout.SOUTH, lblExclude);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, 0, SpringLayout.EAST, getContentPane());
		scrollPane.setTransferHandler(new SongBaseTransferHandler());
		getContentPane().add(scrollPane);
		
		table = new SongBaseTable(this);
		table.getColumnModel().getColumn(1).setPreferredWidth(70);
		table.getColumnModel().getColumn(1).setMinWidth(70);
		table.getColumnModel().getColumn(1).setMaxWidth(70);
		scrollPane.setViewportView(table);
		
		btnPodesi = new JButton("Snimi novi format");
		springLayout.putConstraint(SpringLayout.SOUTH, btnPodesi, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, btnPodesi, 0, SpringLayout.HORIZONTAL_CENTER, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.NORTH, btnPodesi);
		btnPodesi.addActionListener(new BtnPodesiActionListener());
		getContentPane().add(btnPodesi);
		
		btnPocisti = new JButton("Formatiraj sledeće");
		springLayout.putConstraint(SpringLayout.NORTH, btnPocisti, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btnPocisti, -10, SpringLayout.EAST, getContentPane());
		btnPocisti.addActionListener(new BtnPocistiActionListener());
		getContentPane().add(btnPocisti);
		
		btnObrii = new JButton("Obriši");
		btnObrii.addActionListener(new BtnObriiActionListener());
		springLayout.putConstraint(SpringLayout.WEST, btnObrii, 0, SpringLayout.WEST, btnPocisti);
		springLayout.putConstraint(SpringLayout.SOUTH, btnObrii, -5, SpringLayout.NORTH, scrollPane);
		springLayout.putConstraint(SpringLayout.EAST, btnObrii, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(btnObrii);
		
		menuBar = new TraitMenuBar(this);
		setJMenuBar(menuBar);
		
		songDB = new DBFile();
		try {
			songDB.open("baza/main.sdb", "baza/pos.sdb");
		} catch (IOException e) {
			e.printStackTrace();
			lblPoruka.setText("Problem u otvaranju baze!");
		}
		addWindowListener(new ThisWindowListener());
	}
	private class BtnPodesiActionListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			int[] rows = table.getSelectedRows();
			if((rows == null) || (rows.length == 0)) return;
			int index, ids[] = new int[rows.length];
			SongBaseTableModel model = table.getTableModel();
			for(int i=0,len=rows.length;i<len;i++)
				ids[i] = ((SongEntry)table.getValueAt(rows[i], 0)).getId();
			List<Integer> inclList = new ArrayList<Integer>();
			List<Integer> exclList = new ArrayList<Integer>();
			menuBar.addIncludeExcludeCats(inclList, exclList);
			if(inclList.size() + exclList.size() == 0) return;
			SongEntry entry;
			for(int i=ids.length-1;i>=0;i--){
				index = model.getIndexOfItem(ids[i]);
				entry = model.getEntryAt(index);
				try {
					if(entry.getCats().size() == 0)
						entry.getCats().add(menuBar.getDefaultTraitID());
					if(menuBar.getChoosenRank() != 0)
						entry.setRank((byte)menuBar.getChoosenRank());
					entry = songDB.changeEntry(entry, inclList, exclList, menuBar.getDefaultTraitID());
					model.setValueAt(entry, index, 0);
					model.setValueAt(new Byte(entry.getRank()), index, 1);
					table.refreshTableRow(index);
				} catch (IOException e) {
					e.printStackTrace(System.out);
				}
			}
			PlayerWin.getErrDlg().showError("Podešavanje izvršeno", "Obaveštenje");
		}
	}
	private class BtnPocistiActionListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			menuBar.setAllStates(SearchState.NO_STATE);
			lblInclude.setText("Uključi:");
			lblExclude.setText("Isljuči:");
		}
	}
	private class ThisWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent event) {
			try {
				songDB.close();
				PlayerWin.getInstance().bazaProzor = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private class BtnObriiActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			YesNoForm form = new YesNoForm("Jeste li sigurni da želite obrisati pesme iz baze.");
			form.setLocation(btnObrii.getLocationOnScreen().x + (btnObrii.getWidth() / 2) - 
					(form.getWidth()), btnObrii.getLocationOnScreen().y);
			form.setVisible(true);
			if(!form.confirmed){
				form.dispose();
				return;
			}
			form.dispose();
			int[] rows = table.getSelectedRows();
			if((rows == null) || (rows.length == 0)) return;
			int index, ids[] = new int[rows.length];
			SongBaseTableModel model = table.getTableModel();
			for(int i=0,len=rows.length;i<len;i++)
				ids[i] = ((SongEntry)table.getValueAt(rows[i], 0)).getId();
			List<Integer> inclList = new ArrayList<Integer>();
			SongEntry entry;
			for(int i=ids.length-1;i>=0;i--){
				index = model.getIndexOfItem(ids[i]);
				entry = model.getEntryAt(index);
				try {
					@SuppressWarnings("unchecked")
					ArrayList<Integer> exclList = (ArrayList<Integer>)entry.getCats().clone();
					entry = songDB.changeEntry(entry, inclList, exclList, -1);
					songDB.removeEntry(entry.getId());
					table.getTableModel().removeRow(index);
					System.out.println("Deleted entry: " + entry.getFileName());
				} catch (IOException e1) {
					e1.printStackTrace(System.out);
				}
			}
		}
	}

	private int[] listToArray(List<Integer> list){
		int[] ret = new int[list.size()];
		for(int i=0,len=list.size();i<len;i++)
			ret[i] = list.get(i).intValue();
		return ret;
	}

	public boolean hasCategory(List<Integer> list, int val){
		for (int i = 0, len = list.size(); i < len; i++)
			if(list.get(i).intValue() == val) return true;
		return false;
	}

	public void filterDB(){
		List<Integer> inclList = new ArrayList<Integer>();
		List<Integer> exclList = new ArrayList<Integer>();
		menuBar.addIncludeExcludeCats(inclList, exclList);
		table.getTableModel().clear();
		if((inclList.size() == 0) && menuBar.searchStr.getText().isEmpty()) return;
		if(menuBar.searchStr.getText().isEmpty()){
			if(inclList.size() == 0) return;
			try {
				List<SongEntry> entries = songDB.getPosByCats(listToArray(inclList), listToArray(exclList));
				// Ako je izabran rang onda se filtrira lista
				if(menuBar.getChoosenRank() != 0){
					for(int i=entries.size()-1;i>=0;i--)
						if(entries.get(i).getRank() != menuBar.getChoosenRank())
							entries.remove(i);
				}
				for(int i=0,len=entries.size();i<len;i++)
					table.getTableModel().addRow(table.createTableRow(entries.get(i)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			if(inclList.size() == 0){
				try {
					List<SongEntry> entries = songDB.findByName(menuBar.searchStr.getText());
					// Ukljucuje exclList u pretragu
					if(exclList.size() > 0){
						for(int i=entries.size()-1;i>=0;i--){
							for(int j=0,j_len=exclList.size();j<j_len;j++){
								if(hasCategory(entries.get(i).getCats(), exclList.get(j).intValue())){
									entries.remove(i);
									break;
								}
							}
						}
					}
					// Ukljucuje rang u pretragu
					if(menuBar.getChoosenRank() != 0){
						for(int i=entries.size()-1;i>=0;i--)
							if(entries.get(i).getRank() != menuBar.getChoosenRank())
								entries.remove(i);
					}
					// Puni tabelu
					for(int i=0,len=entries.size();i<len;i++)
						table.getTableModel().addRow(table.createTableRow(entries.get(i)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				try {
					List<SongEntry> entries = songDB.getPosByCats(listToArray(inclList), listToArray(exclList));
					String temp = menuBar.searchStr.getText().toLowerCase();
					if(menuBar.getChoosenRank() == 0){
						for(int i=entries.size()-1;i>=0;i--)
							if(entries.get(i).getFileName().toLowerCase().indexOf(temp) == -1)
								entries.remove(i);
					}else{
						for(int i=entries.size()-1;i>=0;i--)
							if((entries.get(i).getRank() != menuBar.getChoosenRank()) || 
									(entries.get(i).getFileName().toLowerCase().indexOf(temp) == -1))
								entries.remove(i);
					}
					for(int i=0,len=entries.size();i<len;i++)
						table.getTableModel().addRow(table.createTableRow(entries.get(i)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void writeOutAbrevs(){
		lblInclude.setText("Uključi: " + menuBar.getAbrevsForState(SearchState.INCLUDE));
		lblExclude.setText("Isključi: " + menuBar.getAbrevsForState(SearchState.EXCLUDE));
	}

	private class SongBaseTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 896982298488741223L;

		@Override
		public boolean canImport(TransferSupport support) {
			if(!support.isDrop()) return false;
			return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
		}

		@Override
		public boolean importData(TransferSupport support) {
			if(!canImport(support)) return false;
			lblPoruka.setText("Molimo sačekajte");
			repaint();
			try{
				@SuppressWarnings("unchecked")
				List<File> files = (List<File>)support.getTransferable().getTransferData(
						DataFlavor.javaFileListFlavor);
				int count = songDB.getNextID();
				SongEntry entry;
				ArrayList<Integer> addList = new ArrayList<Integer>();
				ArrayList<Integer> subList = new ArrayList<Integer>();
				menuBar.addIncludeExcludeCats(addList, subList);
				if(addList.size() == 0){
					ErrorInfoDialog info = new ErrorInfoDialog();
					info.showError("Morate izabrati kategorije koje<br/>će biti dodeljene pesmama.");
					info.dispose();
					lblPoruka.setText("Formatna baza");
					return false;
				}
				for(int i=0,len=files.size();i<len;i++){
					try{
						new ListJItem(files.get(i));
						if(songDB.isPathEntered(files.get(i).getAbsolutePath()))
							continue;
						entry = new SongEntry();
						entry.setId(count);
						entry.setRank((byte)1);
						entry.setFullPath(files.get(i).getAbsolutePath());
						entry.setCats(addList);
						songDB.addEntry(entry);
						count++;
						table.getTableModel().addRow(table.createTableRow(entry));
					}catch(Exception e){
						try {
							e.printStackTrace();
							FileOutputStream fos = new FileOutputStream("greske.txt", true);
							PrintWriter writer = new PrintWriter(fos);
							writer.print("Time in milis: ");
							writer.println(System.currentTimeMillis());
							e.printStackTrace(writer);
							writer.close();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
				table.getSelectionModel().clearSelection();
			}catch(Exception e){
				lblPoruka.setText("Formatna baza");
				return false;
			}
			lblPoruka.setText("Formatna baza");
			return true;
		}

	}
}
