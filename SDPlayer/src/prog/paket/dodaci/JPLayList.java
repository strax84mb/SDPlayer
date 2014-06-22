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
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import prog.paket.baza.struct.menutree.TraitChange;
import prog.paket.playlist.generator.WaitInfoDialog;

public class JPLayList extends JList<ListJItem> implements DragGestureListener {

	private static final long serialVersionUID = -5748753533700247499L;

	private List<ContentListener> contentListeners = new ArrayList<ContentListener>();

	private String pLName;

	public String getpLName() {
		return pLName;
	}

	public JPLayList getThis(){
		return this;
	}

	public JPLayList(String pLName){
		this(new DefaultListModel<ListJItem>(), pLName);
	}

	public JPLayList(DefaultListModel<ListJItem> model, String pLName){
		super(model);
		this.pLName = pLName;
		setDragEnabled(true);
		setDropMode(DropMode.INSERT);
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		DragSource ds = new DragSource();
		ds.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);
		setTransferHandler(new PlayListHandler());
		addKeyListener(new PlayListKeyListener());
		setSelectionModel(new PlayListSelectionModel());
	}

	public DefaultListModel<ListJItem> getListModel(){
		return (DefaultListModel<ListJItem>)getModel();
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent event) {
		event.startDrag(null, new TransferableListJItem(
				new PLItemsPackage(getSelectedIndices(), this)));
	}

	public void removeSelectedItems(){
		int inds[] = getSelectedIndices();
		DefaultListModel<ListJItem> model = getListModel();
		for(int i=inds.length-1;i>=0;i--)
			model.removeElementAt(inds[i]);
		fireContentEvent(new ContentEvent(this, TraitChange.REMOVED));
	}

	public void transferFromAnotherPL(JPLayList list, int indicies[], int dropIndex){
		int index = dropIndex;
		DefaultListModel<ListJItem> model = getListModel();
		int len = indicies.length;
		if(this.equals(list)){
			ArrayList<ListJItem> items = new ArrayList<ListJItem>();
			for(int i=len-1;i>=0;i--){
				if(indicies[i] < index) index--;
				items.add(model.remove(indicies[i]));
			}
			for(int i=0;i<len;i++)
				model.add(index + i, items.get(i));
		}else{
			for(int i=0;i<len;i++){
				model.add(index + i, list.getModel().getElementAt(indicies[i]).cloneItem());
			}
		}
		getSelectionModel().setSelectionInterval(index, index + len - 1);
		fireContentEvent(new ContentEvent(getThis(), TraitChange.ADDED));
	}

	private class PlayListHandler extends TransferHandler {

		private static final long serialVersionUID = -5409446322150194140L;

		@Override
		public boolean canImport(TransferSupport support) {
			if(!support.isDrop()) return false;
			return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || 
					support.isDataFlavorSupported(TransferableListJItem.jItemListFlavor);
		}

		private void transferFiles(TransferSupport support) throws UnsupportedFlavorException, IOException{
			@SuppressWarnings("unchecked")
			List<File> files = (List<File>)support.getTransferable().getTransferData(
					DataFlavor.javaFileListFlavor);
			int index = ((JList.DropLocation)support.getDropLocation()).getIndex();
			WaitInfoDialog.getInstance().setAddSongsToPlayList(getListModel(), files, index);
			WaitInfoDialog.getInstance().setVisible(true);
			getSelectionModel().setSelectionInterval(index, index + files.size() - 1);
			fireContentEvent(new ContentEvent(getThis(), TraitChange.ADDED));
		}

		private void transferListJItems(TransferSupport support) 
				throws UnsupportedFlavorException, IOException{
			int dropIndex = ((JList.DropLocation)support.getDropLocation()).getIndex();
			PLItemsPackage pack = (PLItemsPackage)support.getTransferable().getTransferData(
					TransferableListJItem.jItemListFlavor);
			transferFromAnotherPL(pack.getjPList(), pack.getIndicies(), dropIndex);
		}

		@Override
		public boolean importData(TransferSupport support) {
			if(!canImport(support)) return false;
			try{
				if(support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
					transferFiles(support);
				}else if(support.isDataFlavorSupported(TransferableListJItem.jItemListFlavor)){
					transferListJItems(support);
				}
				return true;
			}catch(Exception e){
				e.printStackTrace(System.out);
				return false;
			}
		}
		
	}

	private class PlayListKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent ke) {
			if((ke.getKeyCode() == KeyEvent.VK_DELETE) && (ke.getModifiers() == 0)){
				int sels[] = getSelectedIndices();
				DefaultListModel<ListJItem> model = getListModel();
				for(int i=sels.length-1;i>=0;i--)
					model.remove(sels[i]);
				fireContentEvent(new ContentEvent(getThis(), TraitChange.REMOVED));
			}
		}
	}

	private class PlayListSelectionModel extends DefaultListSelectionModel {

		private static final long serialVersionUID = -5087453212285098193L;

		@Override
		public void addSelectionInterval(int index0, int index1) {
			if(index0 == index1){
				if(getSelectedIndices().length == getListModel().getSize()) clearSelection();
				if(isSelectedIndex(index0)) return;
			}
			super.addSelectionInterval(index0, index1);
		}

		@Override
		public void setSelectionInterval(int index0, int index1) {
			if(index0 == index1){
				if(getSelectedIndices().length == getListModel().getSize()) clearSelection();
				if(isSelectedIndex(index0)) return;
			}
			super.setSelectionInterval(index0, index1);
		}

	}

	public void addContentListener(ContentListener listener){
		contentListeners.add(listener);
	}

	public void fireContentEvent(ContentEvent event){
		for(int i=0,len=contentListeners.size();i<len;i++)
			contentListeners.get(i).contentChanged(event);
	}

	@Override
	public boolean equals(Object obj) {
		if((obj == null) || !(obj instanceof JPLayList)) return false;
		return pLName.equals(((JPLayList)obj).getpLName());
	}

}
