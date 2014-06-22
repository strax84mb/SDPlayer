package prog.paket.dodaci;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.DropMode;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import prog.paket.forme.reklame.TransferableScheduledItem;
import prog.paket.playlist.generator.WaitInfoDialog;
import prog.paket.playlist.generator.struct.Duration;
import prog.paket.playlist.generator.struct.StartTime;

public class PlayListTable extends JTable implements DragGestureListener{

	private static final long serialVersionUID = 2086142344062664520L;

	public PlayListTable(){
		super();
		setModel(new PLTableModel(new Object[]{"", "", ""}, 0));
		setTableHeader(null);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setCellSelectionEnabled(false);
		setRowSelectionAllowed(true);
		setSelectionModel(new PLSelectionModel());
		getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		//getColumnModel().getColumn(0).setPreferredWidth(75);
		getColumnModel().getColumn(1).setPreferredWidth(300);
		//getColumnModel().getColumn(2).setPreferredWidth(75);
		setDragEnabled(true);
		setDropMode(DropMode.INSERT_ROWS);
		DragSource ds = new DragSource();
		ds.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
		setTransferHandler(new PlayListTableHandler());
		//setSelectionModel(new PLTableSelectionModel());
		setShowGrid(false);
		setAutoscrolls(false);
		addKeyListener(new PLKeyListener());
	}

	private PlayListTable getThis(){
		return this;
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent event) {
		event.startDrag(null, new TransferableScheduledItem(getSelectedRows()));
	}

	public void adjustWidth(int tableWidth){
		getColumnModel().getColumn(1).setPreferredWidth(tableWidth - 150);
	}

	public void addElement(ListJItem item){
		getTableModel().addRow(new Object[]{new StartTime(), item, 
				new Duration((item.duration == -1)?-1:(int)(item.duration/1000000))});
	}

	public void addElement(ListJItem item, int index){
		getTableModel().insertRow(index, new Object[]{new StartTime(), item, 
				new Duration((item.duration == -1)?-1:(int)(item.duration/1000000))});
	}

	public void removeRow(int rowIndex){
		getTableModel().removeRow(rowIndex);
	}

	public ListJItem getElement(int rowIndex){
		return (ListJItem)getTableModel().getValueAt(rowIndex, 1);
	}

	@Override
	public int getRowCount() {
		return getTableModel().getRowCount();
	}

	public void removeSelected(){
		PLTableModel model = getTableModel();
		int[] inds = getSelectedRows();
		for(int i=inds.length-1;i>=0;i--)
			model.removeRow(i);
	}

	public ListJItem getListItemAt(int index){
		return (ListJItem)getValueAt(index, 1);
	}

	public int moveRows(int[] rows, int index){
		return getTableModel().moveRows(rows, index);
	}

	public void insertRows(ListJItem[] items, int index, int len){
		getTableModel().insertRows(items, index, len);
	}

	public PLTableModel getTableModel(){
		return (PLTableModel)getModel();
	}

	private class PlayListTableHandler extends TransferHandler {
		
		private static final long serialVersionUID = 2890805420784705691L;
		
		@Override
		public boolean canImport(TransferSupport support) {
			if(!support.isDrop()) return false;
			return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || 
					support.isDataFlavorSupported(TransferableScheduledItem.scheduledItemListFlavor) || 
					support.isDataFlavorSupported(TransferableListJItem.jItemListFlavor);
		}

		private void transferFiles(TransferSupport support) 
				throws UnsupportedFlavorException, IOException{
			@SuppressWarnings("unchecked")
			List<File> files = (List<File>)support.getTransferable().getTransferData(
					DataFlavor.javaFileListFlavor);
			int index = ((JTable.DropLocation)support.getDropLocation()).getRow();
			WaitInfoDialog.getInstance().setAddSongsToPlayListTable(getThis(), files, index);
			WaitInfoDialog.getInstance().setVisible(true);
			getSelectionModel().clearSelection();
			getSelectionModel().setSelectionInterval(index, index + files.size() - 1);
		}

		private void transferItems(TransferSupport support) 
				throws UnsupportedFlavorException, IOException{
			PLItemsPackage pack = (PLItemsPackage)support.getTransferable().getTransferData(
					TransferableListJItem.jItemListFlavor);
			int index = ((JTable.DropLocation)support.getDropLocation()).getRow();
			ListJItem items[] = new ListJItem[pack.getIndicies().length];
			for(int i=0,len=items.length;i<len;i++){
				items[i] = pack.getjPList().getModel().getElementAt(pack.getIndicies()[i]);
			}
			getTableModel().insertRows(items, index, items.length);
			getSelectionModel().clearSelection();
			getSelectionModel().setSelectionInterval(index, index + items.length - 1);
		}

		private void transferPLItems(TransferSupport support) 
				throws UnsupportedFlavorException, IOException{
			int[] indicies = (int[])support.getTransferable().getTransferData(
					TransferableScheduledItem.scheduledItemListFlavor);
			int index = ((JTable.DropLocation)support.getDropLocation()).getRow();
			index = getTableModel().moveRows(indicies, index);
			getSelectionModel().clearSelection();
			getSelectionModel().addSelectionInterval(index, index + indicies.length - 1);
		}

		@Override
		public boolean importData(TransferSupport support) {
			if(!canImport(support)) return false;
			try{
				if(support.isDataFlavorSupported(TransferableScheduledItem.scheduledItemListFlavor)){
					transferPLItems(support);
				}else if(support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
					transferFiles(support);
				}else if(support.isDataFlavorSupported(TransferableListJItem.jItemListFlavor)){
					transferItems(support);
				}else return false;
				return true;
			}catch(Exception e){
				e.printStackTrace(System.out);
				return false;
			}
		}
		
	}

	private class PLSelectionModel extends DefaultListSelectionModel {
		
		private static final long serialVersionUID = 181542499991035733L;
		
		@Override
		public void addSelectionInterval(int index0, int index1) {
			//if(index1 == getRowCount() - 1) return;
			if(index0 == index1){
				if(getSelectedRows().length == getTableModel().getRowCount()) clearSelection();
				if(isSelectedIndex(index0)) return;
			}
			super.addSelectionInterval(index0, index1);
		}
		
		private boolean inIntArray(int[] rows, int index){
			for(int i=0,len=rows.length;i<len;i++)
				if(rows[i] == index) return true;
			return false;
		}
		
		@Override
		public void setSelectionInterval(int index0, int index1) {
			//if(index1 == getRowCount() - 1) return;
			int[] rows = getSelectedRows();
			if(inIntArray(rows, index0) && inIntArray(rows, index1)) return;
			if(index0 == index1){
				if(getSelectedRows().length == getTableModel().getRowCount()) clearSelection();
				if(isRowSelected(index0)) return;
			}
			super.setSelectionInterval(index0, index1);
		}
		
	}

	private class PLKeyListener extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent ke) {
			switch(ke.getKeyCode()){
			case KeyEvent.VK_ESCAPE:
				clearSelection();
				break;
			case KeyEvent.VK_DELETE:
				getTableModel().removeRows(getSelectedRows());
				break;
			case KeyEvent.VK_A:
				if((ke.getModifiers() == 2) 
						&& (getRowCount() > 0)){
					getSelectionModel().clearSelection();
					getSelectionModel().setSelectionInterval(0, getRowCount() - 1);
				}
				break;
			}
		}
		
	}

}
