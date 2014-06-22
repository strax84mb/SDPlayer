package prog.paket.report;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ReportTable extends JTable {

	private static final long serialVersionUID = 8939317743552746918L;

	public ReportTable(){
		DefaultTableModel model = new DefaultTableModel(new Object[]{"Vreme", "Naziv datoteke", 
				"Za izveštaj", "Izvođač", "Naziv", "Napomena"}, 0);
		setModel(model);
		setCellSelectionEnabled(false);
		setRowSelectionAllowed(true);
		getColumnModel().getColumn(0).setPreferredWidth(100);
		getColumnModel().getColumn(1).setPreferredWidth(300);
		getColumnModel().getColumn(2).setPreferredWidth(70);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if(column == 2)
			return true;
		else return false;
	}

	@Override
	public Class<?> getColumnClass(int column) {
		switch(column){
		case 0:
			return String.class;
		case 1:
			return Song.class;
		case 2:
			return Boolean.class;
		case 3:
			return String.class;
		case 4:
			return String.class;
		default:
			return String.class;
		}
	}

	public String getStartString(long startTime){
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(startTime);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy.");
		return sdf.format(cal.getTime());
	}

	public DefaultTableModel getTableModel(){
		return (DefaultTableModel)getModel();
	}

}
