package rs.trznica.dragan.forms.support;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import rs.trznica.dragan.entities.struja.Ocitavanje;
import rs.trznica.dragan.entities.struja.VrstaBrojila;

public class ReadingsTableModel extends DefaultTableModel {

	private static final long serialVersionUID = -5417947038187039609L;
	
	private boolean hasSummary = false;

	public ReadingsTableModel() {
		super(
				new Object[][] {},
				new String[] {
						"Mesec", 
						"Brojilo", 
						"Vrsta brojila", 
						"kW NT", 
						"kW VT", 
						"Cena NT", 
						"Cena VT", 
						"Pristup", 
						"Podsticaj", 
						"kW Reakt.", 
						"Cena reakt.", 
						"ID"}
				);
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return (column < 11) ? String.class : Long.class;
	}
	
	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}
	
	public void clearAll() {
		setRowCount(0);
	}
	
	private Object[] makeRowData(Ocitavanje object) {
		return new Object[] {
				object.getMesec(), 
				object.getBrojiloBroj() + " - " + object.getBrojiloED(), 
				object.getBrojiloVrsta().getDescription(), 
				DecimalFormater.formatFromLong(object.getKwNT(), 0), 
				DecimalFormater.formatFromLong(object.getKwVT(), 0), 
				DecimalFormater.formatFromLong(object.getCenaNT(), 2), 
				DecimalFormater.formatFromLong(object.getCenaVT(), 2), 
				DecimalFormater.formatFromLong(object.getPristup(), 2), 
				DecimalFormater.formatFromLong(object.getPodsticaj(), 2), 
				(VrstaBrojila.MAXIGRAF.equals(object.getBrojiloVrsta())) ? "" : DecimalFormater.formatFromLong(object.getKwReaktivna(), 0), 
				(VrstaBrojila.MAXIGRAF.equals(object.getBrojiloVrsta())) ? "" : DecimalFormater.formatFromLong(object.getCenaReaktivna(), 2), 
				object.getId()
		};
	}
	
	public void addReading(Ocitavanje object) {
		addRow(makeRowData(object));
	}
	
	public boolean replaceReading(int rowIndex, Ocitavanje object) {
		if (hasSummary && rowIndex == getRowCount() - 1) {
			return false;
		} else {
			removeRow(rowIndex);
			insertRow(rowIndex, makeRowData(object));
			return true;
		}
	}
	
	public void addSummary(Ocitavanje object) {
		addRow(new Object[] {
				"Ukupno:", 
				"", 
				"", 
				DecimalFormater.formatFromLong(object.getKwNT(), 0), 
				DecimalFormater.formatFromLong(object.getKwVT(), 0), 
				DecimalFormater.formatFromLong(object.getCenaNT(), 2), 
				DecimalFormater.formatFromLong(object.getCenaVT(), 2), 
				DecimalFormater.formatFromLong(object.getPristup(), 2), 
				DecimalFormater.formatFromLong(object.getPodsticaj(), 2), 
				DecimalFormater.formatFromLong(object.getKwReaktivna(), 0), 
				DecimalFormater.formatFromLong(object.getCenaKW() / 10L, 2), 
				0L
		});
		hasSummary = true;
	}
	
	public void addReadings(List<Ocitavanje> objects) {
		Ocitavanje sum = new Ocitavanje();
		Ocitavanje temp;
		for (int i = 0; i < objects.size(); i++) {
			temp = objects.get(i);
			sum.setKwNT(sum.getKwNT() + temp.getKwNT());
			sum.setKwVT(sum.getKwVT() + temp.getKwVT());
			sum.setCenaNT(sum.getCenaNT() + temp.getCenaNT());
			sum.setCenaVT(sum.getCenaVT() + temp.getCenaVT());
			sum.setPristup(sum.getPristup() + temp.getPristup());
			sum.setPodsticaj(sum.getPodsticaj() + temp.getPodsticaj());
			sum.setKwReaktivna(sum.getKwReaktivna() + temp.getKwReaktivna());
			sum.setCenaKW(sum.getCenaKW() + temp.getCenaKW());
			addReading(temp);
		}
		addSummary(sum);
	}
	
	public Long getRowId(int index) {
		if (hasSummary) {
			if (index == getRowCount() - 1) {
				return null;
			} else {
				return (Long) getValueAt(index, 11);
			}
		} else {
			return (Long) getValueAt(index, 11);
		}
	}
	
	public void deleteRows(int rows[]) {
		for (int i = rows.length - 1; i >= 0; i--) {
			if (hasSummary) {
				if (rows[i] < getRowCount() - 1) {
					removeRow(rows[i]);
				}
			} else {
				removeRow(rows[i]);
			}
		}
	}
	
	public void removeSummary() {
		if (hasSummary) {
			removeRow(getRowCount() - 1);
			hasSummary = false;
		}
	}
	
	public boolean tableHasSummaryRow() {
		return hasSummary;
	}
}
