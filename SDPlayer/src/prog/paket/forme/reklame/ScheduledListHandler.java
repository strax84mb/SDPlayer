package prog.paket.forme.reklame;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.TransferHandler;

import prog.paket.baza.struct.SongEntry;
import prog.paket.baza.struct.TransferableSongEntry;

public class ScheduledListHandler extends TransferHandler {

	private static final long serialVersionUID = -4034290126074324345L;

	private JComboBox<ScheduledItemsType> cbLists;

	private ScheduledItemsList itemsList;

	public ScheduledListHandler(JComboBox<ScheduledItemsType> cbLists, ScheduledItemsList itemsList){
		this.cbLists = cbLists;
		this.itemsList = itemsList;
	}

	@Override
	public boolean canImport(TransferSupport support) {
		if(!support.isDrop()) return false;
		return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || 
				support.isDataFlavorSupported(TransferableScheduledItem.scheduledItemListFlavor) || 
				support.isDataFlavorSupported(TransferableSongEntry.songEntryFlavor);
	}

	private void transferFiles(TransferSupport support) 
			throws UnsupportedFlavorException, IOException{
		@SuppressWarnings("unchecked")
		List<File> files = (List<File>)support.getTransferable().getTransferData(
				DataFlavor.javaFileListFlavor);
		DefaultListModel<ScheduledItem> model = itemsList.getListModel();
		ScheduledItemsType type = (ScheduledItemsType)cbLists.getSelectedItem();
		int index = ((JList.DropLocation)support.getDropLocation()).getIndex();
		for(int i=0,len=files.size();i<len;i++){
			try{
				model.add(index + i, new ScheduledItem(files.get(i)));
			}catch(Exception e){
				System.out.println("Dodavanje fajla " + files.get(i).getName() + " neuspešno.");
			}
		}
		itemsList.getSelectionModel().setSelectionInterval(index, index + files.size() - 1);
		type.items.clear();
		for(int i=0,len=model.size();i<len;i++)
			type.items.add(model.elementAt(i));
	}

	private void transferScheduledItems(TransferSupport support) 
			throws UnsupportedFlavorException, IOException{
		ArrayList<ScheduledItem> items = new ArrayList<ScheduledItem>();
		ScheduledItemsType type = (ScheduledItemsType)cbLists.getSelectedItem();
		DefaultListModel<ScheduledItem> model = itemsList.getListModel();
		int[] indicies = (int[])support.getTransferable().getTransferData(
				TransferableScheduledItem.scheduledItemListFlavor);
		int index = ((JList.DropLocation)support.getDropLocation()).getIndex();
		int len = indicies.length;
		for(int i=len-1;i>=0;i--){
			if(indicies[i] < index) index--;
			items.add(model.remove(indicies[i]));
		}
		for(int i=0;i<len;i++)
			model.add(index + i, items.get(i));
		itemsList.getSelectionModel().setSelectionInterval(index, index + len - 1);
		type.items.clear();
		len = model.size();
		for(int i=0;i<len;i++)
			type.items.add(model.elementAt(i));
	}

	private void transferEntries(TransferSupport support) 
			throws UnsupportedFlavorException, IOException{
		SongEntry entries[] = (SongEntry[])support.getTransferable().getTransferData(
				TransferableSongEntry.songEntryFlavor);
		DefaultListModel<ScheduledItem> model = itemsList.getListModel();
		ScheduledItemsType type = (ScheduledItemsType)cbLists.getSelectedItem();
		int index = ((JList.DropLocation)support.getDropLocation()).getIndex();
		for(int i=0,len=entries.length;i<len;i++){
			try{
				model.add(index + i, new ScheduledItem(entries[i]));
			}catch(Exception e){
				System.out.println("Dodavanje stavke \"" + entries[i].getFileName() + 
						"\" iz baze je neuspešno.");
			}
		}
		itemsList.getSelectionModel().setSelectionInterval(index, index + entries.length - 1);
		type.items.clear();
		for(int i=0,len=model.size();i<len;i++)
			type.items.add(model.elementAt(i));
	}

	@Override
	public boolean importData(TransferSupport support) {
		if(!canImport(support)) return false;
		//Transferable trans = support.getTransferable();
		if(cbLists.getSelectedIndex() == -1) return false;
		try{
			if(support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
				transferFiles(support);
			}else if(support.isDataFlavorSupported(TransferableScheduledItem.scheduledItemListFlavor)){
				transferScheduledItems(support);
			}else if(support.isDataFlavorSupported(TransferableSongEntry.songEntryFlavor)){
				transferEntries(support);
			}else return false;
			itemsList.repaint();
			return true;
		}catch(Exception e){
			e.printStackTrace(System.out);
			return false;
		}
	}

}
