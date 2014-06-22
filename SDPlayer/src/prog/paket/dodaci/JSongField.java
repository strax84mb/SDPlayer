package prog.paket.dodaci;

import java.awt.datatransfer.DataFlavor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.TransferHandler;

import prog.paket.baza.struct.menutree.TraitChange;

public class JSongField extends JTextField {

	private static final long serialVersionUID = 5913223150608323373L;

	private List<ContentListener> contLstnrs = new ArrayList<ContentListener>();

	private ListJItem item = null;

	public JSongField(){
		super();
		setEditable(false);
		setTransferHandler(new SongFieldTransferHandler());
		addKeyListener(new SongFieldKeyListener());
	}

	public ListJItem getItem(){
		return item;
	}

	public String getPath(){
		return (item == null)?null:item.fullPath;
	}

	public void setNullItem(){
		setText("");
		item = null;
		fireContentEvent(new ContentEvent(this, TraitChange.REMOVED));
	}

	public void setItem(ListJItem item){
		this.item = item;
		int index = -1;
		String temp = item.fullPath;
		index = temp.lastIndexOf("/");
		if(index == -1) index = temp.lastIndexOf("\\");
		if(index != -1) temp = temp.substring(index + 1);
		index = temp.lastIndexOf(".");
		temp = temp.substring(0, index);
		setText(temp);
		fireContentEvent(new ContentEvent(this, TraitChange.ADDED));
	}

	public void setItem(String path){
		try{
			item = new ListJItem(path);
			int index = -1;
			String temp = item.fullPath;
			index = temp.lastIndexOf("/");
			if(index == -1) index = temp.lastIndexOf("\\");
			if(index != -1) temp = temp.substring(index + 1);
			index = temp.lastIndexOf(".");
			temp = temp.substring(0, index);
			setText(temp);
			fireContentEvent(new ContentEvent(this, TraitChange.ADDED));
		}catch(Exception e){
			item = null;
		}
	}

	private class SongFieldKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent event) {
			if((event.getKeyCode() == KeyEvent.VK_DELETE) && (event.getModifiers() == 0))
				setNullItem();
		}
	}
	private class SongFieldTransferHandler extends TransferHandler {

		private static final long serialVersionUID = -8493320852936557471L;

		@Override
		public boolean canImport(TransferSupport support) {
			if(!support.isDrop()) return false;
			return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
		}

		@Override
		public boolean importData(TransferSupport support) {
			if(!canImport(support)) return false;
			try{
				@SuppressWarnings("unchecked")
				List<File> files = (List<File>)support.getTransferable().getTransferData(
						DataFlavor.javaFileListFlavor);
				setItem(files.get(0).getAbsolutePath());
				return true;
			}catch(Exception e){
				return false;
			}
		}
		
	}

	public void addContentListener(ContentListener listener){
		contLstnrs.add(listener);
	}

	public void fireContentEvent(ContentEvent event){
		for(int i=0,len=contLstnrs.size();i<len;i++)
			contLstnrs.get(i).contentChanged(event);
	}

}
