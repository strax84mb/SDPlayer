package prog.paket.baza.struct;

import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import prog.paket.baza.BazaProzor;
import prog.paket.dodaci.ListJItem;
import prog.paket.playlist.generator.ErrorInfoDialog;

public class SongBaseTable extends JTable implements DragGestureListener {

	private static final long serialVersionUID = 355298105751735420L;

	private BazaProzor dlg;

	public SongBaseTable(BazaProzor dlg){
		super();
		this.dlg = dlg;
		setModel(new SongBaseTableModelImpl());
		setCellSelectionEnabled(false);
		setRowSelectionAllowed(true);
		setSelectionModel(new SongBaseTableSelectionModel());
		getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		getColumnModel().getColumn(1).setPreferredWidth(150);
		DragSource ds = new DragSource();
		ds.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
		setDragEnabled(true);
		setTransferHandler(new SongBaseTableTransferHandler());
		setShowGrid(false);
		setAutoscrolls(false);
		addKeyListener(new SongBaseTableKeyListener());
		setAutoCreateRowSorter(true);
	}

	public List<SongEntry> getSelectedEntries(){
		int[] rows = getSelectedRows();
		ArrayList<SongEntry> ret = new ArrayList<SongEntry>();
		for(int i=0,len=rows.length;i<len;i++){
			ret.add(getTableModel().getEntryAt(rows[i]));
		}
		return ret;
	}

	public SongBaseTableModel getTableModel(){
		return (SongBaseTableModel)getModel();
	}

	private class SongBaseTableModelImpl extends AbstractTableModel implements SongBaseTableModel {

		private static final long serialVersionUID = 3783376390138205346L;

		private String[] columnNames = new String[]{"Naziv", "Rang", "Osobine", "Vrsta"};
		private ArrayList<Object[]> rawData = new ArrayList<Object[]>();
		private int firstSelIndex, lastSelIndex;
		private boolean shouldClearSelection;

		public SongBaseTableModelImpl(){
			addTableModelListener(new TableModelListener() {
				@Override
				public void tableChanged(TableModelEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							if(shouldClearSelection) clearSelection();
							int rc = getRowCount();
							if((firstSelIndex != -1) && (lastSelIndex != -1) && 
									(firstSelIndex < rc) && (lastSelIndex < rc))
								addRowSelectionInterval(firstSelIndex, lastSelIndex);
							else clearSelection();
						}
					});
				}
			});
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch(columnIndex){
			case 0: return SongEntry.class;
			case 1: return Byte.class;
			case 2: return String.class;
			default: return String.class;
			}
		}

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return columnNames[columnIndex];
		}

		@Override
		public int getRowCount() {
			return rawData.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return rawData.get(rowIndex)[columnIndex];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			rawData.get(rowIndex)[columnIndex] = aValue;
			fireTableDataChanged();
		}

		@Override
		public SongEntry removeRow(int rowIndex) {
			SongEntry ret = (SongEntry)(rawData.remove(rowIndex)[0]);
			firstSelIndex = rowIndex;
			lastSelIndex = rowIndex;
			shouldClearSelection = false;
			fireTableRowsDeleted(rowIndex, rowIndex);
			return ret;
		}

		@Override
		public void removeRows(int[] rows) {
			int i, len = rows.length;
			for(i=len-1;i>=0;i--){
				rawData.remove(rows[i]);
			}
			firstSelIndex = -1;
			lastSelIndex = -1;
			fireTableDataChanged();
		}

		@Override
		public void addRow(Object[] rowData) {
			rawData.add(rowData);
			firstSelIndex = getRowCount() - 1;
			lastSelIndex = getRowCount() - 1;
			shouldClearSelection = false;
			fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
		}

		@Override
		public void insertRow(int rowIndex, Object[] rowData) {
			rawData.add(rowIndex, rowData);
			firstSelIndex = rowIndex;
			lastSelIndex = rowIndex;
			shouldClearSelection = false;
			fireTableRowsInserted(rowIndex, rowIndex);
		}

		@Override
		public void insertRows(Object[][] list, int index, int len) {
			int i;
			for(i=0;i<len;i++)
				rawData.add(list[i]);
			firstSelIndex = index;
			lastSelIndex = index + len - 1;
			fireTableRowsInserted(firstSelIndex, lastSelIndex);
		}

		@Override
		public int moveRows(int[] rows, int index) {
			Object[][] objs = new Object[rows.length][];
			int i, len = rows.length, temp = 0;
			for(i=len-1;i>=0;i--){
				objs[i] = rawData.remove(rows[i]);
				if(rows[i] < index) temp++;
			}
			index -= temp;
			for(i=0;i<len;i++)
				rawData.add(index + i, objs[i]);
			getSelectionModel().clearSelection();
			firstSelIndex = index;
			lastSelIndex = index + len - 1;
			shouldClearSelection = true;
			fireTableDataChanged();
			return index;
		}

		@Override
		public void cutOffFromRow(int fromRow) {
			if(fromRow < 0) return;
			if(fromRow >= rawData.size()) return;
			for(int i=fromRow,len=rawData.size();i<len;i++)
				rawData.remove(fromRow);
			firstSelIndex = -1;
			lastSelIndex = -1;
			shouldClearSelection = true;
			fireTableDataChanged();
		}

		@Override
		public void clear() {
			rawData.clear();
			firstSelIndex = -1;
			lastSelIndex = -1;
			shouldClearSelection = true;
			fireTableDataChanged();
		}

		@Override
		public SongEntry getEntryAt(int row) {
			return (SongEntry)rawData.get(row)[0];
		}

		@Override
		public Byte getRankAt(int row){
			return (Byte)rawData.get(row)[1];
		}

		@Override
		public String getDescTraitsAt(int row) {
			return (String)rawData.get(row)[2];
		}

		@Override
		public String getTraitsAt(int row) {
			return (String)rawData.get(row)[3];
		}

		@Override
		public boolean isEmpty() {
			return rawData.size() == 0;
		}

		public void removeByID(int id){
			for(int i=0,len=rawData.size();i<len;i++){
				if(((SongEntry)(rawData.get(i)[0])).getId() == id){
					removeRow(i);
					break;
				}
			}
		}

		@Override
		public int getIndexOfItem(int id) {
			for(int i=0,len=rawData.size();i<len;i++)
				if(((SongEntry)rawData.get(i)[0]).getId() == id)
					return i;
			return -1;
		}

	}

	private class SongBaseTableKeyListener extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent ke) {
			if(ke.isControlDown()){
				if((ke.getKeyCode() == KeyEvent.VK_A) 
						&& (getRowCount() > 0)){
					System.out.println("Ctrl+A");
					//((SongBaseTableSelectionModel)getSelectionModel()).
					getSelectionModel().setSelectionInterval(0, getRowCount() - 1);
				}
			}else{
				switch(ke.getKeyCode()){
				case KeyEvent.VK_ESCAPE:
					clearSelection();
					break;
				case KeyEvent.VK_DELETE:
					getTableModel().removeRows(getSelectedRows());
					break;
				}
			}
		}

	}

	private class SongBaseTableSelectionModel extends DefaultListSelectionModel {

		private static final long serialVersionUID = -498076983986341098L;

		@Override
		public void addSelectionInterval(int index0, int index1) {
			/*
			if(index0 == index1){
				if(getSelectedRows().length == getTableModel().getRowCount()) clearSelection();
				if(isSelectedIndex(index0)) return;
			}*/
			super.addSelectionInterval(index0, index1);
		}

		private boolean inIntArray(int[] rows, int index){
			for(int i=0,len=rows.length;i<len;i++)
				if(rows[i] == index) return true;
			return false;
		}

		@Override
		public void setSelectionInterval(int index0, int index1) {
			int[] rows = getSelectedRows();
			if(inIntArray(rows, index0) && inIntArray(rows, index1)) return;
			if(index0 == index1){
				if(getSelectedRows().length == getTableModel().getRowCount()) clearSelection();
				if(isRowSelected(index0)) return;
			}
			super.setSelectionInterval(index0, index1);
		}

	}

	public Object[] createTableRow(SongEntry entry){
		String c2 = null, c3 = null;
		SongTrait trait;
		int middle = dlg.menuBar.getPrimaryTraitID();
		for(int j=0,size=entry.getCats().size();j<size;j++){
			trait = dlg.menuBar.getByID(entry.getCats().get(j));
			if(trait.getTraitParent().getID() == middle){
				if(c2 == null)
					c2 = trait.getAbrev();
				else c2 += ", " + trait.getAbrev();
			}else{
				if(c3 == null)
					c3 = trait.getAbrev();
				else c3 += ", " + trait.getAbrev();
			}
		}
		return new Object[]{entry, new Byte(entry.getRank()), c2, c3};
	}

	public void refreshTableRow(int row){
		String c2 = null, c3 = null;
		SongTrait trait;
		SongEntry entry = getTableModel().getEntryAt(row);
		int middle = dlg.menuBar.getPrimaryTraitID();
		for(int j=0,size=entry.getCats().size();j<size;j++){
			trait = dlg.menuBar.getByID(entry.getCats().get(j));
			if(trait.getTraitParent().getID() == middle){
				if(c2 == null)
					c2 = trait.getAbrev();
				else c2 += ", " + trait.getAbrev();
			}else{
				if(c3 == null)
					c3 = trait.getAbrev();
				else c3 += ", " + trait.getAbrev();
			}
		}
		getTableModel().setValueAt(c2, row, 2);
		getTableModel().setValueAt(c3, row, 3);
	}

	private class SongBaseTableTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 3388994193052967392L;

		@Override
		public boolean canImport(TransferSupport support) {
			if(!support.isDrop()) return false;
			return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
		}

		@Override
		public boolean importData(TransferSupport support) {
			if(!canImport(support)) return false;
			dlg.lblPoruka.setText("Molimo sačekajte");
			try{
				@SuppressWarnings("unchecked")
				List<File> files = (List<File>)support.getTransferable().getTransferData(
						DataFlavor.javaFileListFlavor);
				int count = dlg.songDB.getNextID();
				SongEntry entry;
				ArrayList<Integer> addList = new ArrayList<Integer>();
				ArrayList<Integer> subList = new ArrayList<Integer>();
				dlg.menuBar.addIncludeExcludeCats(addList, subList);
				if(addList.size() == 0){
					ErrorInfoDialog info = new ErrorInfoDialog();
					info.showError("Morate izabrati kategorije koje<br/>će biti dodeljene pesmama.");
					info.dispose();
					dlg.lblPoruka.setText("Formatna baza");
					return false;
				}
				for(int i=0,len=files.size();i<len;i++){
					try{
						new ListJItem(files.get(i));
						if(dlg.songDB.isPathEntered(files.get(i).getAbsolutePath()))
							continue;
						entry = new SongEntry();
						entry.setId(count);
						entry.setRank((byte)1);
						entry.setFullPath(files.get(i).getAbsolutePath());
						entry.setCats(addList);
						dlg.songDB.addEntry(entry);
						count++;
						dlg.table.getTableModel().addRow(createTableRow(entry));
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
				getSelectionModel().clearSelection();
			}catch(Exception e){
				dlg.lblPoruka.setText("Formatna baza");
				return false;
			}
			dlg.lblPoruka.setText("Formatna baza");
			return true;
		}

	}

	@Override
	public void dragGestureRecognized(DragGestureEvent event) {
		int rows[] = getSelectedRows();
		if((rows == null) || (rows.length == 0)) return;
		SongEntry entries[] = new SongEntry[rows.length];
		for(int i=0,len=rows.length;i<len;i++)
			entries[i] = (SongEntry)getValueAt(rows[i], 0);
		event.startDrag(null, new TransferableSongEntry(entries));
	}

}
