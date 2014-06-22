package prog.paket.forme.pregled;

import javax.swing.JList;
import javax.swing.TransferHandler;

import prog.paket.dodaci.ListJItem;
import prog.paket.dodaci.TransferableListJItem;
import prog.paket.playlist.generator.struct.ProgSection;

public class ListaTerminaTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 5687102408993065045L;

	@Override
	public boolean canImport(TransferSupport support) {
		if(!support.isDrop()) return false;
		return support.isDataFlavorSupported(TransferableProgSection.progSectionFlavor) || 
				support.isDataFlavorSupported(TransferableListJItem.jItemListFlavor);
	}

	@Override
	public boolean importData(TransferSupport support) {
		if(!canImport(support)) return false;
		ProgSectionList listaTermina = (ProgSectionList)support.getComponent();
		try{
			
			ProgSection sec = (ProgSection)support.getTransferable().getTransferData(
					TransferableProgSection.progSectionFlavor);
			int index = ((JList.DropLocation)support.getDropLocation()).getIndex();
			int oldIndex = listaTermina.getIndexOfSection(sec);
			if(oldIndex < index) index--;
			listaTermina.getListModel().removeElementAt(oldIndex);
			listaTermina.getListModel().insertElementAt(sec, index);
			listaTermina.correctStartTimes();
			listaTermina.setSelectedIndex(index);
			return true;
		}catch(Exception e){
			System.out.println("Dropped data is not ProgSection!");
			//e.printStackTrace(System.out);
		}
		try{
			int indicies[] = (int [])support.getTransferable().getTransferData(
					TransferableListJItem.jItemListFlavor);
			int index = ((JList.DropLocation)support.getDropLocation()).getIndex();
			ProgSection oldSec = listaTermina.getSelectedValue();
			ProgSection newSec = listaTermina.getModel().getElementAt(index);
			ListJItem item;
			int addIndex = newSec.songs.size();
			for(int i=indicies.length-1;i>=0;i--){
				item = oldSec.songs.remove(indicies[i]);
				newSec.songs.add(addIndex, item);
			}
			listaTermina.setSelectedIndex(index);
			return true;
		}catch(Exception e){
			e.printStackTrace(System.out);
			return false;
		}
	}

}
