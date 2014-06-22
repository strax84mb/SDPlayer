package prog.paket.dodaci;

import javax.swing.table.DefaultTableModel;

import prog.paket.playlist.generator.struct.Duration;
import prog.paket.playlist.generator.struct.StartTime;

public class PLTableModel extends DefaultTableModel {

	private static final long serialVersionUID = -2485630786050227421L;

	public PLTableModel(Object[] columnNames, int rowCount) {
		super(columnNames, rowCount);
	}

	public void removeRows(int[] rows){
		for(int i=rows.length-1;i>=0;i--)
			removeRow(rows[i]);
		fireTableDataChanged();
	}

	public void addRow(ListJItem item){
		addRow(new Object[]{new StartTime(), item, new Duration(
				(item instanceof ListJSection)?-1:item.duration / 1000000)});
	}

	public void insertRow(int index, ListJItem item){
		insertRow(index, new Object[]{new StartTime(), item, new Duration(
				(item instanceof ListJSection)?-1:item.duration / 1000000)});
	}

	public void insertRows(ListJItem[] items, int index, int len){
		int i;
		for(i=0;i<len;i++)
			insertRow(i + index, items[i]);
	}

	public int moveRows(int[] rows, int index){
		Object[][] objs = new Object[rows.length][];
		int i, len = rows.length, temp = 0;
		for(i=len-1;i>=0;i--){
			if(getItemAt(i).duration != -1){
				objs[i] = new Object[3];
				objs[i][0] = getStartTimeAt(rows[i]);
				objs[i][1] = getItemAt(rows[i]);
				objs[i][2] = getDurationAt(rows[i]);
				removeRow(rows[i]);
				if(rows[i] < index) temp++;
			}
		}
		index -= temp;
		for(i=0;i<len;i++)
			insertRow(index + i, new Object[]{objs[i][0], objs[i][1], objs[i][2]});
		/*
		getSelectionModel().clearSelection();
		firstSelIndex = index;
		lastSelIndex = index + len - 1;
		shouldClearSelection = true;
		fireTableDataChanged();
		*/
		return index;
	}

	public void clear(){
		setRowCount(0);
		fireTableDataChanged();
	}

	public ListJItem getItemAt(int row){
		return (ListJItem)getValueAt(row, 1);
	}

	public Duration getDurationAt(int row){
		return (Duration)getValueAt(row, 2);
	}

	public StartTime getStartTimeAt(int row){
		return (StartTime)getValueAt(row, 0);
	}

	public boolean isEmpty(){
		return getRowCount() == 0;
	}

}
