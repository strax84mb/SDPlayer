package prog.paket.forme.emisije;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

import prog.paket.baza.struct.SongEntry;
import prog.paket.baza.struct.TransferableSongEntry;
import prog.paket.dodaci.JPLayList;
import prog.paket.dodaci.ListJItem;
import prog.paket.dodaci.PLItemsPackage;
import prog.paket.dodaci.TransferableListJItem;
import prog.paket.playlist.generator.WaitInfoDialog;

public class MusicTreeTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 2944897999010426298L;

	private JPLayList songList;

	private PLGeneratorWindow dlg;

	public MusicTreeTransferHandler(PLGeneratorWindow dlg){
		this.dlg = dlg;
		this.songList = dlg.list;
	}

	@Override
	public boolean canImport(TransferSupport support) {
		if(!support.isDrop()) return false;
		return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || 
				support.isDataFlavorSupported(TransferableListJItem.jItemListFlavor) || 
				support.isDataFlavorSupported(TransferableSongEntry.songEntryFlavor);
	}

	private void sortedInsert(List<ListJItem> list, ListJItem item){
		int i, result, len = list.size();
		for(i=0;i<len;i++){
			if(item.fullPath.compareTo(list.get(i).fullPath) == 0) return;
			result = item.fullPath.toLowerCase().compareTo(list.get(i).fullPath.toLowerCase());
			if(result < 0) break;
		}
		list.add(i, item);
	}

	private void transferFiles(TransferSupport support, CategoryTreeNode node, 
			RankedTreeNode rtn, byte rank) throws UnsupportedFlavorException, IOException{
		@SuppressWarnings("unchecked")
		List<File> files = (List<File>)support.getTransferable().getTransferData(
				DataFlavor.javaFileListFlavor);
		WaitInfoDialog.getInstance().setAddSongsToTree(node.getSongList(), files, songList, false, 
				(rank == -1)?1:rank);
		WaitInfoDialog.getInstance().setVisible(true);
		// Doradi listu
		DefaultListModel<ListJItem> model = songList.getListModel();
		int len;
		if(rank == -1){
			model.removeAllElements();
			len = node.getSongList().size();
			for(int i=0;i<len;i++)
				model.addElement(node.getSongList().get(i));
		}else{
			model.removeAllElements();
			List<ListJItem> list = rtn.getSongList();
			len = list.size();
			for(int i=0;i<len;i++)
				model.addElement(list.get(i));
		}
		dlg.repopulateSongList();
	}

	private void transferItems(TransferSupport support, CategoryTreeNode node, byte rank) 
			throws UnsupportedFlavorException, IOException{
		PLItemsPackage pack = (PLItemsPackage)support.getTransferable().getTransferData(
				TransferableListJItem.jItemListFlavor);
		int[] indicies = pack.getIndicies();
		int len = indicies.length;
		if(pack.getjPList().equals(songList)){
			// Preuzmi stavke liste
			DefaultListModel<ListJItem> model = songList.getListModel();
			for(int i=0;i<len;i++){
				if(!node.isSongInList(model.get(i)))
					sortedInsert(node.getSongList(), model.get(i));
				model.get(i).rang = rank;
			}
			dlg.repopulateSongList();
		}else{
			DefaultListModel<ListJItem> model = pack.getjPList().getListModel();
			ListJItem item;
			for(int i=0;i<len;i++){
				item = model.get(i).cloneItem();
				if(!node.isSongInList(model.get(i))){
					sortedInsert(node.getSongList(), item);
					item.rang = rank;
				}else{
					for(int j=0,jLen=node.getSongList().size();j<jLen;j++){
						if(node.getSongList().get(j).equals(item)){
							node.getSongList().get(j).rang = rank;
							break;
						}
					}
				}
			}
			dlg.repopulateSongList();
		}
	}

	private void transferEntries(TransferSupport support, CategoryTreeNode node, 
			RankedTreeNode rtn, byte rank) throws UnsupportedFlavorException, IOException{
		SongEntry[] entries = (SongEntry[])support.getTransferable().getTransferData(
				TransferableSongEntry.songEntryFlavor);
		WaitInfoDialog.getInstance().prepareCopyingEntriesToMusicTree(node.getSongList(), rank, entries);
		WaitInfoDialog.getInstance().setVisible(true);
		
		// Preuzmi stavke liste
		DefaultListModel<ListJItem> model = songList.getListModel();
		int len;
		if(rank == -1){
			model.removeAllElements();
			len = node.getSongList().size();
			for(int i=0;i<len;i++)
				model.addElement(node.getSongList().get(i));
		}else{
			model.removeAllElements();
			List<ListJItem> list = rtn.getSongList();
			len = list.size();
			for(int i=0;i<len;i++)
				model.addElement(list.get(i));
		}
		dlg.repopulateSongList();
	}

	@Override
	public boolean importData(TransferSupport support) {
		if(!canImport(support)) return false;
		TreePath path = ((JTree.DropLocation)support.getDropLocation()).getPath();
		if(path == null) return false;
		byte rank = -1;
		// Nalaz kategorije
		CategoryTreeNode node = null;
		RankedTreeNode rtn = null;
		if(path.getLastPathComponent() instanceof CategoryTreeNode){
			node = (CategoryTreeNode)path.getLastPathComponent();
			rank = -1;
		}else if(path.getLastPathComponent() instanceof RankedTreeNode){
			rtn = (RankedTreeNode)path.getLastPathComponent();
			node = (CategoryTreeNode)rtn.getParent();
			String str = (String)rtn.getUserObject();
			if(str.equals("Sve"))
				rank = -1;
			else{
				try{
					rank = Byte.parseByte(str);
				}catch(NumberFormatException nfe){
					nfe.printStackTrace(System.out);
					return false;
				}
			}
		}else return false;
		try{
			if(support.isDataFlavorSupported(TransferableSongEntry.songEntryFlavor)){
				// Prebacaj iz baze
				transferEntries(support, node, rtn, rank);
				return true;
			}else if(support.isDataFlavorSupported(TransferableListJItem.jItemListFlavor)){
				// Prebacaj iz liste
				transferItems(support, node, rank);
				return true;
			}else if(support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
				// Prebacaj fajlova
				transferFiles(support, node, rtn, rank);
				return true;
			}
			return false;
		}catch(Exception e){
			e.printStackTrace(System.out);
			return false;
		}
	}
	
}
