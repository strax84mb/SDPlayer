package rs.trznica.dragan.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import rs.trznica.dragan.dao.lucene.BrojiloDao;
import rs.trznica.dragan.dao.lucene.OcitavanjeDao;
import rs.trznica.dragan.entities.struja.Brojilo;
import rs.trznica.dragan.entities.struja.Ocitavanje;
import rs.trznica.dragan.forms.support.ModalResult;
import rs.trznica.dragan.forms.support.ReadingsTableModel;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ListaOcitavanjaForm extends JInternalFrame {
	
	private static final long serialVersionUID = -6025767172110906309L;
	
	private ApplicationContext ctx;
	private BrojiloDao brojiloDao;
	private OcitavanjeDao ocitavanjeDao;
	
	protected Font defaultFont = new Font("Times New Roman", Font.PLAIN, 18);
	
	private JTextField tfFromMonth;
	private JTextField tfToMonth;
	private JCheckBox chckbxHideUnfunctional;
	private JTable table;
	private JButton btnSearch;
	
	private List<BrojiloCheckBox> brojila = new ArrayList<BrojiloCheckBox>();
	
	@Autowired
	public ListaOcitavanjaForm(ApplicationContext ctx) {
		this.ctx = ctx;
		brojiloDao = this.ctx.getBean(BrojiloDao.class);
		ocitavanjeDao = this.ctx.getBean(OcitavanjeDao.class);
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		setBounds(100, 100, 654, 417);
		getContentPane().setLayout(new BorderLayout());
		
		Box leftBox = Box.createVerticalBox();
		getContentPane().add(leftBox, BorderLayout.WEST);
		leftBox.add(createLabel("Od meseca:"));
		tfFromMonth = createTextField(8);
		leftBox.add(tfFromMonth);
		leftBox.add(createLabel("Do meseca:"));
		tfToMonth = createTextField(8);
		leftBox.add(tfToMonth);
		leftBox.add(Box.createVerticalStrut(5));
		leftBox.add(createLabel("Brojila:"));
		leftBox.add(loadAllCounters());
		//leftBox.add(Box.createVerticalGlue());
		
		chckbxHideUnfunctional = new JCheckBox("Sakrij brojila van funkcije");
		chckbxHideUnfunctional.setAlignmentX(LEFT_ALIGNMENT);
		chckbxHideUnfunctional.setFont(defaultFont);
		chckbxHideUnfunctional.addItemListener(new ToggleHideChckbxListener());
		leftBox.add(chckbxHideUnfunctional);
		leftBox.add(Box.createVerticalStrut(10));
		
		btnSearch = new JButton("Prikaži");
		btnSearch.setFont(defaultFont);
		btnSearch.setAlignmentX(LEFT_ALIGNMENT);
		btnSearch.addActionListener(new BtnSearchActionListener());
		leftBox.add(btnSearch);
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		
		table = new JTable();
		table.setFont(defaultFont);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setModel(new ReadingsTableModel());
		table.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		resizeColumns(table.getColumnModel());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(table);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		centerPanel.add(scrollPane, BorderLayout.CENTER);
		
		Box buttonsBox = Box.createHorizontalBox();
		centerPanel.add(buttonsBox, BorderLayout.NORTH);
		
		buttonsBox.add(Box.createHorizontalGlue());
		addButtonToBar("Izmeni", new BtnEditActionListener(), buttonsBox);
		addButtonToBar("Obriši", new BtnDeleteActionListener(), buttonsBox);
	}
	
	private JButton addButtonToBar(String text, ActionListener listener, Box box) {
		JButton button = new JButton(text);
		button.setFont(defaultFont);
		button.addActionListener(listener);
		button.setAlignmentY(TOP_ALIGNMENT);
		box.add(Box.createHorizontalStrut(15));
		box.add(button);
		return button;
	}
	
	private void resizeColumns(TableColumnModel model) {
		DefaultTableCellRenderer rightSideRend = new DefaultTableCellRenderer();
		rightSideRend.setHorizontalAlignment(JLabel.RIGHT);
		for (int i = 0; i < model.getColumnCount(); i++) {
			model.getColumn(i).setPreferredWidth(75);
			model.getColumn(i).setResizable(true);
			if (i >= 3) {
				model.getColumn(i).setCellRenderer(rightSideRend);
			}
		}
	}
	
	private JScrollPane loadAllCounters() {
		JScrollPane sPane = new JScrollPane();
		sPane.setAlignmentX(LEFT_ALIGNMENT);
		try {
			Box countersBox = Box.createVerticalBox();
			sPane.setViewportView(countersBox);
			brojila.clear();
			brojiloDao.findAll().stream()
					.map(x -> new BrojiloCheckBox(x))
					.forEach(x -> {
						brojila.add(x);
						x.setVisible(x.getBrojilo().getuFunkciji());
						countersBox.add(x);
					});
		} catch (IOException e) {
			e.printStackTrace();
			ErrorDialog dlg = new ErrorDialog();
			dlg.showError("Desila se greška prilikom čitanja svih brojila.");
		}
		return sPane;
	}
	
	private JLabel createLabel(String text) {
		JLabel label = new JLabel(text);
		label.setFont(defaultFont);
		label.setAlignmentX(LEFT_ALIGNMENT);
		return label;
	}
	
	private JTextField createTextField(Integer columns) {
		JTextField field = new JTextField();
		if (columns != null) {
			field.setColumns(columns);
		}
		field.setFont(defaultFont);
		field.setAlignmentX(LEFT_ALIGNMENT);
		field.setMaximumSize(new Dimension(32767, 35));
		return field;
	}
	
	private List<Long> getSelectedCounterIds() {
		return brojila.stream()
				.filter(BrojiloCheckBox::isVisible)
				.map(BrojiloCheckBox::getBrojilo)
				.map(Brojilo::getId)
				.collect(Collectors.toList());
	}
	
	private class BrojiloCheckBox extends JCheckBox {
		private static final long serialVersionUID = -1235223700673710858L;
		private Brojilo brojilo;
		private BrojiloCheckBox(Brojilo brojilo) {
			super(brojilo.toString());
			setFont(defaultFont);
			setAlignmentX(LEFT_ALIGNMENT);
			this.brojilo = brojilo;
		}
		public Brojilo getBrojilo() {
			return brojilo;
		}
	}
	private class ToggleHideChckbxListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent ev) {
			if (ev.getStateChange() == ItemEvent.SELECTED) {
				brojila.stream().filter(x -> !x.getBrojilo().getuFunkciji()).forEach(x -> x.setVisible(false));
			} else if (ev.getStateChange() == ItemEvent.DESELECTED) {
				brojila.stream().filter(x -> !x.getBrojilo().getuFunkciji()).forEach(x -> x.setVisible(true));
			}
		}
	}
	private class BtnSearchActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ev) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			sdf.setLenient(false);
			String fromMonth = tfFromMonth.getText();
			try {
				if ("".equals(fromMonth)) {
					fromMonth = null;
				} else {
					sdf.parse(fromMonth);
				}
			} catch (Exception e) {
				e.printStackTrace();
				new ErrorDialog().showError("Početni mesec mora biti u formatu yyyy-mm ili izostavljen.");
				tfFromMonth.selectAll();
				tfFromMonth.requestFocus();
				return;
			}
			String toMonth = tfToMonth.getText();
			try {
				if ("".equals(toMonth)) {
					toMonth = null;
				} else {
					sdf.parse(toMonth);
				}
			} catch (Exception e) {
				e.printStackTrace();
				new ErrorDialog().showError("Završni mesec mora biti u formatu yyyy-mm ili izostavljen.");
				tfToMonth.selectAll();
				tfToMonth.requestFocus();
				return;
			}
			List<Long> selectedCounterIds = getSelectedCounterIds();
			if (selectedCounterIds.size() == 0) {
				new ErrorDialog().showError("Morate izabrati neko brojilo.");
				return;
			}
			try {
				List<Ocitavanje> result = ocitavanjeDao.findInInterval(selectedCounterIds, fromMonth, toMonth);
				ReadingsTableModel tableModel = (ReadingsTableModel) table.getModel();
				tableModel.clearAll();
				tableModel.addReadings(result);
			} catch (Exception e) {
				e.printStackTrace();
				new ErrorDialog().showError("Desila se greška prilikom čitanja očitavanja:<br/>" + e.getMessage());
			}
		}
	}
	private class BtnEditActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ev) {
			int rows[] = table.getSelectedRows();
			if (rows.length == 0) {
				new ErrorDialog().showError("Morate odabrati neko očitavanje.");
			} else if (rows.length == 1) {
				ReadingsTableModel model = (ReadingsTableModel) table.getModel();
				if (model.tableHasSummaryRow() && rows[0] == model.getRowCount() - 1) {
					new ErrorDialog().showError("Ne možete menjati sumu svih prikazanih očitavanja.");
				} else {
					Long id = ((ReadingsTableModel) table.getModel()).getRowId(rows[0]);
					Ocitavanje ocitavanje = null;
					try {
						ocitavanje = ocitavanjeDao.find(id);
					} catch (Exception e) {
						new ErrorDialog().showError("Desila se greška prilikom učitavanja podataka za menjanje.");
						return;
					}
					OcitavanjeForm dlg = ctx.getBean(OcitavanjeForm.class);
					dlg.editObject(ocitavanje);
					dlg.setVisible(true);
					if (ModalResult.OK.equals(dlg.getModalResult())) {
						ocitavanje = dlg.getReturnValue();
						model.replaceReading(rows[0], ocitavanje);
						model.removeSummary();
						table.clearSelection();
						table.repaint();
					}
				}
			} else {
				new ErrorDialog().showError("Ne možete menjati više očitavanja odjednom.");
			}
		}
	}
	private class BtnDeleteActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ev) {
			int rows[] = table.getSelectedRows();
			if (rows.length == 0) {
				new ErrorDialog().showError("Morate izabrati neki red iz tabele");
				return;
			}
			YesNoDialog dlg = new YesNoDialog("Sigurno želite obrisati izabrana očitavanja?");
			dlg.showDialog(table);
			if (ModalResult.NO.equals(dlg.getModalResult())) {
				return;
			}
			ReadingsTableModel model = (ReadingsTableModel) table.getModel();
			Long id = -1L;
			try {
				for (int i : rows) {
					id = model.getRowId(i);
					ocitavanjeDao.delete(id);
				}
			} catch (Exception e) {
				e.printStackTrace();
				new ErrorDialog().showError("Neuspelo brisanje očitavanja sa ID=" + id.toString());
			}
			model.deleteRows(rows);
			model.removeSummary();
			table.clearSelection();
			table.repaint();
		}
	}
}
