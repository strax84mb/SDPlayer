package prog.paket.forme.pregled;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import prog.paket.playlist.generator.struct.ProgSection;

public class ProgSectionList extends JList<ProgSection> implements DragGestureListener {

	private static final long serialVersionUID = 2671915669267158280L;

	public ProgSectionList(){
		this(new DefaultListModel<ProgSection>());
	}

	private ProgSectionList(DefaultListModel<ProgSection> model){
		super(model);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setDragEnabled(true);
		setDropMode(DropMode.ON_OR_INSERT);
		DragSource ds = new DragSource();
		ds.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
		setTransferHandler(new ProgSectionMoveHandler());
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent event) {
		if(getSelectedIndex() == -1) return;
		setDropMode(DropMode.INSERT);
		event.startDrag(null, new TransferableProgSection(getSelectedValue()));
	}

	public DefaultListModel<ProgSection> getListModel(){
		return (DefaultListModel<ProgSection>)getModel();
	}

	public void correctStartTimes(){
		if(getModel().getSize() == 0) return;
		long time = getModel().getElementAt(0).startTime, duration = 0;
		ProgSection sec;
		int count, decreaceBy;
		for(int i=0,len=getModel().getSize();i<len;i++){
			sec = getModel().getElementAt(i);
			duration = 0;
			sec.startTime = time;
			count = sec.songs.size();
			decreaceBy = 1;
			for(int j=0;j<count;j++)
				duration += sec.songs.get(j).duration / 1000;
			if(sec.crossfade){
				if(sec.startToon) decreaceBy++;
				if(sec.endToon) decreaceBy++;
				duration -= 3000L * (count - ((decreaceBy > count)?count:decreaceBy));
			}
			time += duration;
		}
	}

	public int getIndexOfSection(ProgSection sec){
		for(int i=0,len=getModel().getSize();i<len;i++)
			if(getModel().getElementAt(i).equals(sec))
				return i;
		return -1;
	}

	private class ProgSectionMoveHandler extends TransferHandler {

		private static final long serialVersionUID = 8813822532646035441L;

		@Override
		public boolean canImport(TransferSupport support) {
			if(!support.isDrop()) return false;
			return support.isDataFlavorSupported(TransferableProgSection.progSectionFlavor);
		}

		@Override
		public boolean importData(TransferSupport support) {
			if(!canImport(support)) return false;
			try{
				ProgSection sec = (ProgSection)support.getTransferable().getTransferData(
						TransferableProgSection.progSectionFlavor);
				int index = ((JList.DropLocation)support.getDropLocation()).getIndex();
				int oldIndex = getIndexOfSection(sec);
				if(oldIndex < index) index--;
				getListModel().removeElementAt(oldIndex);
				getListModel().insertElementAt(sec, index);
				correctStartTimes();
				return true;
			}catch(Exception e){
				e.printStackTrace(System.out);
				return false;
			}
		}

	}

}
