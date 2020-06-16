package rs.trznica.dragan.forms.support;

import rs.trznica.dragan.entities.struja.OcitavanjeSql;
import rs.trznica.dragan.entities.support.OcitavanjeSuma;

import javax.swing.table.DefaultTableModel;
import java.util.List;

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
						"kW ST", 
						"Cena NT", 
						"Cena VT", 
						"Cena ST", 
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

	private String brojiloToString(OcitavanjeSql oc) {
		return new StringBuilder(oc.getBrojiloED())
				.append(" - ")
				.append(oc.getBrojiloBroj())
				.append(" - ")
				.append(oc.getBrojiloVrsta().getAbrev())
				.toString();
	}

	private Object[] makeRowData(OcitavanjeSql object) {
		switch (object.getBrojiloVrsta()) {
		case SIR_POT_JED:
			return new Object[] {
					object.getMesec(), 
					brojiloToString(object),
					object.getBrojiloVrsta().getAbrev(), 
					"", 
					"", 
					DecimalFormater.formatFromLongSep(object.getKwNT(), 0), 
					"", 
					"", 
					DecimalFormater.formatFromLongSep(object.getCenaNT(), 2), 
					DecimalFormater.formatFromLongSep(object.getPristup(), 2), 
					DecimalFormater.formatFromLongSep(object.getPodsticaj(), 2), 
					"", 
					"", 
					object.getId()
			};
		case SIR_POT_DVO:
			return new Object[] {
					object.getMesec(),
					brojiloToString(object),
					object.getBrojiloVrsta().getAbrev(), 
					DecimalFormater.formatFromLongSep(object.getKwNT(), 0), 
					DecimalFormater.formatFromLongSep(object.getKwVT(), 0), 
					"", 
					DecimalFormater.formatFromLongSep(object.getCenaNT(), 2), 
					DecimalFormater.formatFromLongSep(object.getCenaVT(), 2), 
					"", 
					DecimalFormater.formatFromLongSep(object.getPristup(), 2), 
					DecimalFormater.formatFromLongSep(object.getPodsticaj(), 2), 
					"", 
					"", 
					object.getId()
			};
		default:
			return new Object[] {
					object.getMesec(),
					brojiloToString(object),
					object.getBrojiloVrsta().getAbrev(), 
					DecimalFormater.formatFromLongSep(object.getKwNT(), 0), 
					DecimalFormater.formatFromLongSep(object.getKwVT(), 0), 
					"", 
					DecimalFormater.formatFromLongSep(object.getCenaNT(), 2), 
					DecimalFormater.formatFromLongSep(object.getCenaVT(), 2), 
					"", 
					DecimalFormater.formatFromLongSep(object.getPristup(), 2), 
					DecimalFormater.formatFromLongSep(object.getPodsticaj(), 2), 
					DecimalFormater.formatFromLongSep(object.getKwReaktivna(), 0), 
					DecimalFormater.formatFromLongSep(object.getCenaReaktivna(), 2), 
					object.getId()
			};
		}
	}
	
	public void addReading(OcitavanjeSql object) {
		addRow(makeRowData(object));
	}
	
	public boolean replaceReading(int rowIndex, OcitavanjeSql object) {
		if (hasSummary && rowIndex == getRowCount() - 1) {
			return false;
		} else {
			removeRow(rowIndex);
			insertRow(rowIndex, makeRowData(object));
			return true;
		}
	}
	
	public void addSummary(OcitavanjeSuma object) {
		addRow(new Object[] {
				"Ukupno:", 
				"", 
				"", 
				DecimalFormater.formatFromLongSep(object.getKwNT(), 0), 
				DecimalFormater.formatFromLongSep(object.getKwVT(), 0), 
				DecimalFormater.formatFromLongSep(object.getKwST(), 0), 
				DecimalFormater.formatFromLongSep(object.getCenaNT(), 2), 
				DecimalFormater.formatFromLongSep(object.getCenaVT(), 2), 
				DecimalFormater.formatFromLongSep(object.getCenaST(), 2), 
				DecimalFormater.formatFromLongSep(object.getPristup(), 2), 
				DecimalFormater.formatFromLongSep(object.getPodsticaj(), 2), 
				DecimalFormater.formatFromLongSep(object.getKwReaktivna(), 0), 
				DecimalFormater.formatFromLongSep(object.getCenaReaktivna(), 2), 
				0L
		});
		hasSummary = true;
	}
	
	public void addReadings(List<OcitavanjeSql> objects) {
		OcitavanjeSuma suma = new OcitavanjeSuma();
		OcitavanjeSql temp;
		for (int i = 0; i < objects.size(); i++) {
			temp = objects.get(i);
			suma.addReading(temp);
			addReading(temp);
		}
		addSummary(suma);
	}
	
	public Long getRowId(int index) {
		if (hasSummary) {
			if (index == getRowCount() - 1) {
				return null;
			} else {
				return (Long) getValueAt(index, getColumnCount() - 1);
			}
		} else {
			return (Long) getValueAt(index, getColumnCount() - 1);
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
