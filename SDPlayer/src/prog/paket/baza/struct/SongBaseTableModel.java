package prog.paket.baza.struct;

import javax.swing.table.TableModel;

public interface SongBaseTableModel extends TableModel{

	public SongEntry removeRow(int rowIndex);

	public void removeRows(int[] rows);

	public void addRow(Object[] rowData);

	public void insertRow(int rowIndex, Object[] rowData);

	public void insertRows(Object[][] list, int index, int len);

	public int moveRows(int[] rows, int index);

	public void cutOffFromRow(int fromRow);

	public void clear();

	public SongEntry getEntryAt(int row);

	public Byte getRankAt(int row);

	public String getDescTraitsAt(int row);

	public String getTraitsAt(int row);

	public boolean isEmpty();

	public void removeByID(int id);

	public int getIndexOfItem(int id);

}
