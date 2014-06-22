package prog.paket.dodaci;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;

import prog.paket.forme.reklame.TransferableScheduledItem;
import prog.paket.playlist.generator.WaitInfoDialog;

public class PlayListPane extends JScrollPane {

	private static final long serialVersionUID = 1424066924040321794L;

	private PlayListTable table;

	public PlayListPane(){
		this(new PlayListTable());
	}

	public PlayListPane(PlayListTable table){
		this.table = table;
		setViewportView(table);
		setTransferHandler(new PlayListPaneHandler());
	}

	public PlayListTable getPlayList(){
		return table;
	}

	public PLTableModel getModel(){
		return (PLTableModel)table.getTableModel();
	}

	private class PlayListPaneHandler extends TransferHandler {

		private static final long serialVersionUID = -5035703579397011729L;

		@Override
		public boolean canImport(TransferSupport support) {
			if(!support.isDrop()) return false;
			return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || 
					support.isDataFlavorSupported(TransferableListJItem.jItemListFlavor);
		}

		private void transferFiles(TransferSupport support) 
				throws UnsupportedFlavorException, IOException{
			@SuppressWarnings("unchecked")
			List<File> files = (List<File>)support.getTransferable().getTransferData(
					DataFlavor.javaFileListFlavor);
			int index = table.getRowCount();
			WaitInfoDialog.getInstance().setAddSongsToPlayListTable(table, files, index);
			WaitInfoDialog.getInstance().setVisible(true);
			table.getSelectionModel().clearSelection();
			table.getSelectionModel().setSelectionInterval(index, index + files.size() - 1);
		}

		private void transferItems(TransferSupport support) 
				throws UnsupportedFlavorException, IOException{
			PLItemsPackage pack = (PLItemsPackage)support.getTransferable().getTransferData(
					TransferableListJItem.jItemListFlavor);
			int index = ((JTable.DropLocation)support.getDropLocation()).getRow();
			ListJItem item;
			for(int i=0,len=pack.getIndicies().length;i<len;i++){
				item = pack.getjPList().getModel().getElementAt(pack.getIndicies()[i]);
				table.getTableModel().addRow(item);
			}
			table.getSelectionModel().clearSelection();
			table.getSelectionModel().setSelectionInterval(
					index, index + pack.getIndicies().length - 1);
		}

		private void transferPLItems(TransferSupport support) 
				throws UnsupportedFlavorException, IOException{
			int[] indicies = (int[])support.getTransferable().getTransferData(
					TransferableScheduledItem.scheduledItemListFlavor);
			table.moveRows(indicies, table.getRowCount());
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

	public void repaintContent(){
		table.repaint();
	}

}
