package prog.paket.forme.pregled;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.TransferHandler;

import prog.paket.baza.struct.SongEntry;
import prog.paket.baza.struct.TransferableSongEntry;
import prog.paket.baza.struct.menutree.TraitChange;
import prog.paket.dodaci.ContentEvent;
import prog.paket.dodaci.JPLayList;
import prog.paket.dodaci.ListJItem;
import prog.paket.dodaci.TransferableListJItem;
import prog.paket.playlist.generator.WaitInfoDialog;

public class ListaPesamaTransferHandler extends TransferHandler {

	private static final long serialVersionUID = -8608211772430086776L;

	@Override
	public boolean canImport(TransferSupport support) {
		if(!support.isDrop()) return false;
		return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || 
				support.isDataFlavorSupported(TransferableListJItem.jItemListFlavor) || 
				support.isDataFlavorSupported(TransferableSongEntry.songEntryFlavor);
	}

	@SuppressWarnings("unchecked")
	private void transferFiles(TransferSupport support) 
			throws UnsupportedFlavorException, IOException{
		List<File> files = (List<File>)support.getTransferable().getTransferData(
				DataFlavor.javaFileListFlavor);
		int index = ((JList.DropLocation)support.getDropLocation()).getIndex();
		JPLayList listaPesama = (JPLayList)support.getComponent();
		WaitInfoDialog.getInstance().setAddSongsToPlayList(listaPesama.getListModel(), files, index);
		WaitInfoDialog.getInstance().setVisible(true);
		listaPesama.getSelectionModel().setSelectionInterval(index, index + files.size() - 1);
		listaPesama.fireContentEvent(new ContentEvent(listaPesama, TraitChange.ADDED));
	}

	private void transferListJItems(TransferSupport support) 
			throws UnsupportedFlavorException, IOException{
		ArrayList<ListJItem> items = new ArrayList<ListJItem>();
		int[] indicies = (int[])support.getTransferable().getTransferData(
				TransferableListJItem.jItemListFlavor);
		int index = ((JList.DropLocation)support.getDropLocation()).getIndex();
		JPLayList listaPesama = (JPLayList)support.getComponent();
		DefaultListModel<ListJItem> model = listaPesama.getListModel();
		int len = indicies.length;
		for(int i=len-1;i>=0;i--){
			if(indicies[i] < index) index--;
			items.add(model.remove(indicies[i]));
		}
		for(int i=0;i<len;i++)
			model.add(index + i, items.get(i));
		listaPesama.getSelectionModel().setSelectionInterval(index, index + len - 1);
		listaPesama.fireContentEvent(new ContentEvent(listaPesama, TraitChange.ADDED));
	}

	private void transferSongEntries(TransferSupport support) 
			throws UnsupportedFlavorException, IOException{
		SongEntry entries[] = (SongEntry[])support.getTransferable().getTransferData(
				TransferableSongEntry.songEntryFlavor);
		int index = ((JList.DropLocation)support.getDropLocation()).getIndex();
		JPLayList listaPesama = (JPLayList)support.getComponent();
		WaitInfoDialog.getInstance().prepareCopyingEntriesToJPlayList(
				listaPesama.getListModel(), index, entries);
		WaitInfoDialog.getInstance().setVisible(true);
		listaPesama.clearSelection();
		listaPesama.fireContentEvent(new ContentEvent(listaPesama, TraitChange.ADDED));
	}

	@Override
	public boolean importData(TransferSupport support) {
		if(!canImport(support)) return false;
		try{
			if(support.isDataFlavorSupported(TransferableListJItem.jItemListFlavor)){
				transferListJItems(support);
				return true;
			}else if(support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
				transferFiles(support);
				return true;
			}else if(support.isDataFlavorSupported(TransferableSongEntry.songEntryFlavor)){
				transferSongEntries(support);
				return true;
			}else return false;
		}catch(Exception e){
			e.printStackTrace(System.out);
			return false;
		}
	}

}
