package rs.trznica.dragan.forms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import rs.trznica.dragan.dao.BrojiloRepository;
import rs.trznica.dragan.dao.OcitavanjeRepository;
import rs.trznica.dragan.entities.struja.BrojiloSql;
import rs.trznica.dragan.entities.struja.OcitavanjeSql;
import rs.trznica.dragan.entities.support.BrojiloComparator;
import rs.trznica.dragan.entities.support.OcitavanjeComparator;
import rs.trznica.dragan.forms.support.ModalResult;
import rs.trznica.dragan.forms.support.ReadingsTableModel;
import rs.trznica.dragan.printables.ReadingsSumPrintable;

import javax.print.PrintService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ListaOcitavanjaForm extends JInternalFrame {
	
	private static final long serialVersionUID = -6025767172110906309L;
	
	private ApplicationContext ctx;
	private BrojiloRepository brojiloRepository;
	private OcitavanjeRepository ocitavanjeRepository;
	
	protected Font defaultFont = new Font("Times New Roman", Font.PLAIN, 18);
	
	private JTextField tfFromMonth;
	private JTextField tfToMonth;
	private JCheckBox chckbxSelectAll;
	private JCheckBox chckbxHideUnfunctional;
	private JTable table;
	private JButton btnSearch;
	private JButton btnPrintSum;
	
	private List<BrojiloCheckBox> brojila = new ArrayList<BrojiloCheckBox>();
	
	@Autowired
	public ListaOcitavanjaForm(ApplicationContext ctx) {
		this.ctx = ctx;
		brojiloRepository = this.ctx.getBean(BrojiloRepository.class);
		ocitavanjeRepository = this.ctx.getBean(OcitavanjeRepository.class);
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
		
		chckbxSelectAll = new JCheckBox("Izaberi sve");
		chckbxSelectAll.setAlignmentX(LEFT_ALIGNMENT);
		chckbxSelectAll.setFont(defaultFont);
		chckbxSelectAll.addActionListener(new SelectAllListener());
		leftBox.add(chckbxSelectAll);
		
		leftBox.add(loadAllCounters());
		
		chckbxHideUnfunctional = new JCheckBox("Sakrij brojila van funkcije");
		chckbxHideUnfunctional.setAlignmentX(LEFT_ALIGNMENT);
		chckbxHideUnfunctional.setFont(defaultFont);
		chckbxHideUnfunctional.addItemListener(new ToggleHideChckbxListener());
		leftBox.add(chckbxHideUnfunctional);
		leftBox.add(Box.createVerticalStrut(10));
		
		leftBox.add(makeLeftButtonBar());
		
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
		addButtonToBar("Obri\u0161i", new BtnDeleteActionListener(), buttonsBox);
	}
	
	private Box makeLeftButtonBar() {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(LEFT_ALIGNMENT);
		
		btnSearch = new JButton("Prika\u017Ei");
		btnSearch.setFont(defaultFont);
		btnSearch.setAlignmentX(LEFT_ALIGNMENT);
		btnSearch.addActionListener(new BtnSearchActionListener());
		box.add(btnSearch);
		
		box.add(Box.createHorizontalStrut(10));
		
		btnPrintSum = new JButton("Od\u0161tampaj");
		btnPrintSum.setFont(defaultFont);
		btnPrintSum.setAlignmentX(LEFT_ALIGNMENT);
		btnPrintSum.addActionListener(new PrintSumListener());
		box.add(btnPrintSum);
		
		return box;
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
			switch (i) {
			case 1:
				model.getColumn(i).setPreferredWidth(230);
				break;
			case 2:
				model.getColumn(i).setPreferredWidth(50);
				break;
			default:
				model.getColumn(i).setPreferredWidth(100);
			}
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
			ActionListener listener = new CheckIfAllSelectedListener();
			Box countersBox = Box.createVerticalBox();
			sPane.setViewportView(countersBox);
			brojila.clear();
			brojiloRepository.findAll().stream()
					.sorted(new BrojiloComparator())
					.map(x -> new BrojiloCheckBox(x))
					.forEach(x -> {
						brojila.add(x);
						x.setVisible(x.getBrojilo().getUFunkciji());
						x.addActionListener(listener);
						countersBox.add(x);
					});
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog dlg = new ErrorDialog();
			dlg.showError("Desila se gre\u0161ka prilikom \u010Ditanja svih brojila.");
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
				.filter(BrojiloCheckBox::isSelected)
				.map(BrojiloCheckBox::getBrojilo)
				.map(BrojiloSql::getId)
				.collect(Collectors.toList());
	}
	
	private class BrojiloCheckBox extends JCheckBox {
		private static final long serialVersionUID = -1235223700673710858L;
		private BrojiloSql brojilo;
		private BrojiloCheckBox(BrojiloSql brojilo) {
			super(brojilo.toString());
			setFont(defaultFont);
			setAlignmentX(LEFT_ALIGNMENT);
			this.brojilo = brojilo;
		}
		public BrojiloSql getBrojilo() {
			return brojilo;
		}
	}
	private class ToggleHideChckbxListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent ev) {
			if (ev.getStateChange() == ItemEvent.SELECTED) {
				brojila.stream().filter(x -> !x.getBrojilo().getUFunkciji()).forEach(x -> x.setVisible(false));
			} else if (ev.getStateChange() == ItemEvent.DESELECTED) {
				brojila.stream().filter(x -> !x.getBrojilo().getUFunkciji()).forEach(x -> x.setVisible(true));
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
				new ErrorDialog().showError("Po\u010Detni mesec mora biti u formatu yyyy-mm ili izostavljen.");
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
				new ErrorDialog().showError("Zavr\u0161ni mesec mora biti u formatu yyyy-mm ili izostavljen.");
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
				List<OcitavanjeSql> result = ocitavanjeRepository.findByBrojiloIdInAndMesecBetween(selectedCounterIds, fromMonth, toMonth);
				result.sort(new OcitavanjeComparator());
				ReadingsTableModel tableModel = (ReadingsTableModel) table.getModel();
				tableModel.clearAll();
				tableModel.addReadings(result);
			} catch (Exception e) {
				e.printStackTrace();
				new ErrorDialog().showError("Desila se gre\u0161ka prilikom \u010Ditanja o\u010Ditavanja:<br/>" + e.getMessage());
			}
		}
	}
	private class BtnEditActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ev) {
			int rows[] = table.getSelectedRows();
			if (rows.length == 0) {
				new ErrorDialog().showError("Morate odabrati neko o\u010Ditavanje.");
			} else if (rows.length == 1) {
				ReadingsTableModel model = (ReadingsTableModel) table.getModel();
				if (model.tableHasSummaryRow() && rows[0] == model.getRowCount() - 1) {
					new ErrorDialog().showError("Ne mo\u017Eete menjati sumu svih prikazanih o\u010Ditavanja.");
				} else {
					Long id = ((ReadingsTableModel) table.getModel()).getRowId(rows[0]);
					OcitavanjeSql ocitavanje = null;
					try {
						ocitavanje = ocitavanjeRepository.findOne(id);
					} catch (Exception e) {
						new ErrorDialog().showError("Desila se gre\u0161ka prilikom u\u010Ditavanja podataka za menjanje.");
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
				new ErrorDialog().showError("Ne mo\u017Eete menjati vi\u0161e o\u010Ditavanja odjednom.");
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
			YesNoDialog dlg = new YesNoDialog("Sigurno \u017Eelite obrisati izabrana o\u010Ditavanja?");
			dlg.showDialogInCenter();
			if (ModalResult.NO.equals(dlg.getModalResult())) {
				return;
			}
			ReadingsTableModel model = (ReadingsTableModel) table.getModel();
			Long id = -1L;
			try {
				for (int i : rows) {
					id = model.getRowId(i);
					ocitavanjeRepository.delete(id);
				}
			} catch (Exception e) {
				e.printStackTrace();
				new ErrorDialog().showError("Neuspelo brisanje o\u010Ditavanja sa ID=" + id.toString());
			}
			model.deleteRows(rows);
			model.removeSummary();
			table.clearSelection();
			table.repaint();
		}
	}
	private class SelectAllListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ev) {
			brojila.stream().forEach(x -> x.setSelected(chckbxSelectAll.isSelected()));
		}
	}
	private class CheckIfAllSelectedListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ev) {
			boolean allSelected = brojila.stream()
					.filter(BrojiloCheckBox::isVisible)
					.allMatch(BrojiloCheckBox::isSelected);
			chckbxSelectAll.setSelected(allSelected);
		}
	}
	private class PrintSumListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ev) {
			ReadingsTableModel model = (ReadingsTableModel) table.getModel();
			if (!"Ukupno:".equals((String) model.getValueAt(model.getRowCount() - 1, 0))) {
				new ErrorDialog().showError("Nema ukupnog stanja. Ponovi pretragu.");
				return;
			}
			if (StringUtils.isEmpty(tfFromMonth.getText()) || StringUtils.isEmpty(tfToMonth.getText())) {
				new ErrorDialog().showError("Obavezno je uneti po\u010Detni i zavr\u0161ni mesec.");
				return;
			}
			
			PrinterChooserDialog dlg = new PrinterChooserDialog();
			dlg.setVisible(true);
			PrintService service = dlg.getReturnValue();
			dlg.dispose();
			if (service == null) {
				return;
			}
			
			PrinterJob job = PrinterJob.getPrinterJob();
			job.setPrintable(new ReadingsSumPrintable(
					tfFromMonth.getText(), 
					tfToMonth.getText(), 
					(String) model.getValueAt(model.getRowCount() - 1, 3), 
					(String) model.getValueAt(model.getRowCount() - 1, 4), 
					(String) model.getValueAt(model.getRowCount() - 1, 5), 
					(String) model.getValueAt(model.getRowCount() - 1, 6), 
					(String) model.getValueAt(model.getRowCount() - 1, 7), 
					(String) model.getValueAt(model.getRowCount() - 1, 8), 
					(String) model.getValueAt(model.getRowCount() - 1, 11), 
					(String) model.getValueAt(model.getRowCount() - 1, 12), 
					(String) model.getValueAt(model.getRowCount() - 1, 9), 
					(String) model.getValueAt(model.getRowCount() - 1, 10)));
			
			try {
				job.setPrintService(service);
				job.print();
			} catch (PrinterException e) {
				e.printStackTrace();
				new ErrorDialog().showError("Desila se gre\u0161ka tokom \u0161tampanja:<br/>" + e.getMessage());
			}
		}
	}
}
