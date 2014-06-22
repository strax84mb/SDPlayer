package prog.paket.forme.reklame;

import java.awt.Color;
import java.awt.Component;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.ListSelectionModel;


public class ScheduledItemsList extends JList<ScheduledItem> implements DragGestureListener{

	private static final long serialVersionUID = -7538134956255583510L;

	private long currTime = -1;

	public ScheduledItemsList(){
		this(new DefaultListModel<ScheduledItem>());
	}

	public ScheduledItemsList(DefaultListModel<ScheduledItem> model){
		super(model);
		setDragEnabled(true);
		setDropMode(DropMode.INSERT);
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		DragSource ds = new DragSource();
		ds.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);
		setSelectionModel(new PlayListSelectionModel());
		setCellRenderer(new ScheduledListCellRenderer());
	}

	public DefaultListModel<ScheduledItem> getListModel(){
		return (DefaultListModel<ScheduledItem>)getModel();
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent event) {
		event.startDrag(null, new TransferableScheduledItem(getSelectedIndices()));
	}

	public void setCurrtime(long currTime){
		this.currTime = currTime;
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

	private class ScheduledListCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 5543354718089813089L;

		@Override
		public Component getListCellRendererComponent(JList<?> list,
				Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			Component comp = super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			ScheduledItem item = (ScheduledItem)value;
			if(currTime == -1){
				return comp;
			}else if((item.begin == -1) || (item.end == -1)){
				comp.setBackground(new Color(220, 220, 220));
			}else if((currTime < item.begin) || (item.end < currTime)){
				comp.setBackground(new Color(255, 228, 225));
			}
			return comp;
		}
		
	}

}
