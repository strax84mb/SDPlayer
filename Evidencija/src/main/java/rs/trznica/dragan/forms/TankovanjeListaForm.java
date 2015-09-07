package rs.trznica.dragan.forms;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;

import rs.trznica.dragan.dao.PotrosacDao;
import rs.trznica.dragan.dao.TankovanjeDao;
import rs.trznica.dragan.entities.tankovanje.Tankovanje;
import rs.trznica.dragan.forms.support.ConsumerCheckBox;
import rs.trznica.dragan.forms.support.DecimalFormater;
import rs.trznica.dragan.poi.ConsumerReporter;

import javax.swing.border.EmptyBorder;
import javax.swing.ListSelectionModel;

@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TankovanjeListaForm extends JInternalFrame {

	private static final long serialVersionUID = 3419061457204688936L;
	private JTable table;
	private JTextField tfStartFrom;
	private JTextField tfEndsWith;
	private JCheckBox chckbxAll;

	private List<ConsumerCheckBox> consumers = new ArrayList<ConsumerCheckBox>();

	private PotrosacDao potrosacDao;
	private TankovanjeDao tankovanjeDao;
	private ConsumerReporter reporter;

	private void populateAutowiredFields(ApplicationContext ctx) {
		potrosacDao = ctx.getBean(PotrosacDao.class);
		tankovanjeDao = ctx.getBean(TankovanjeDao.class);
		reporter = ctx.getBean(ConsumerReporter.class);
	}

	private void populateConsumers(JPanel panel) {
		ChckbxUniversalActionListener itemListener = new ChckbxUniversalActionListener();

		potrosacDao.findAll().forEach(x -> {
			ConsumerCheckBox cBox = new ConsumerCheckBox(x);
			cBox.addActionListener(itemListener);
			consumers.add(cBox);
			panel.add(cBox);
		});
	}

	/**
	 * Create the frame.
	 * @throws PropertyVetoException 
	 */
	@Autowired
	public TankovanjeListaForm(ApplicationContext ctx) {
		populateAutowiredFields(ctx);
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		setBounds(100, 100, 654, 417);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Datum", "Potrošač", "Za mesec", "Količina", "Cena litre", "Ukupna cena"
			}
		) {
			private static final long serialVersionUID = -4076593013718122690L;
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class, String.class, String.class, String.class
			};
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}
		});
		DefaultTableCellRenderer rightSideRend = new DefaultTableCellRenderer();
		rightSideRend.setHorizontalAlignment(JLabel.RIGHT);
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(0).setPreferredWidth(110);
		table.getColumnModel().getColumn(0).setMinWidth(110);
		table.getColumnModel().getColumn(0).setMaxWidth(110);
		table.getColumnModel().getColumn(2).setResizable(false);
		table.getColumnModel().getColumn(2).setPreferredWidth(75);
		table.getColumnModel().getColumn(2).setMinWidth(75);
		table.getColumnModel().getColumn(2).setMaxWidth(75);
		table.getColumnModel().getColumn(3).setCellRenderer(rightSideRend);
		table.getColumnModel().getColumn(4).setCellRenderer(rightSideRend);
		table.getColumnModel().getColumn(5).setCellRenderer(rightSideRend);
		scrollPane.setViewportView(table);
		
		JPanel topPanel = new JPanel();
		getContentPane().add(topPanel, BorderLayout.WEST);
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
		
		JPanel paramsPanel = new JPanel();
		topPanel.add(paramsPanel);
		paramsPanel.setLayout(new BoxLayout(paramsPanel, BoxLayout.Y_AXIS));
		
		JPanel startFromPanel = new JPanel();
		startFromPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		startFromPanel.setMaximumSize(new Dimension(32767, 35));
		FlowLayout flowLayout = (FlowLayout) startFromPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		paramsPanel.add(startFromPanel);
		
		JLabel lblStartFrom = new JLabel("Od meseca: ");
		lblStartFrom.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		startFromPanel.add(lblStartFrom);
		
		tfStartFrom = new JTextField();
		tfStartFrom.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		startFromPanel.add(tfStartFrom);
		tfStartFrom.setColumns(8);
		
		JPanel endWithPanel = new JPanel();
		endWithPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		endWithPanel.setMaximumSize(new Dimension(32767, 35));
		FlowLayout fl_endWithPanel = (FlowLayout) endWithPanel.getLayout();
		fl_endWithPanel.setAlignment(FlowLayout.LEFT);
		paramsPanel.add(endWithPanel);
		
		JLabel lblEndWith = new JLabel("Do meseca: ");
		lblEndWith.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		endWithPanel.add(lblEndWith);
		
		tfEndsWith = new JTextField();
		tfEndsWith.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		endWithPanel.add(tfEndsWith);
		tfEndsWith.setColumns(8);
		
		chckbxAll = new JCheckBox("Sve");
		chckbxAll.addActionListener(new ChckbxAllActionListener());
		chckbxAll.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		chckbxAll.setHorizontalAlignment(SwingConstants.LEFT);
		paramsPanel.add(chckbxAll);
		populateConsumers(paramsPanel);
		
		Component verticalGlue = Box.createVerticalGlue();
		paramsPanel.add(verticalGlue);
		
		JCheckBox chckbxHideObsolete = new JCheckBox("Sakrij zastarele");
		chckbxHideObsolete.addItemListener(new ChckbxHideObsoleteItemListener());
		chckbxHideObsolete.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		paramsPanel.add(chckbxHideObsolete);
		
		JButton btnSearch = new JButton("Prika\u017Ei");
		btnSearch.addActionListener(new BtnSearchActionListener());
		btnSearch.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		paramsPanel.add(btnSearch);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		
		Component horizontalGlue = Box.createHorizontalGlue();
		bottomPanel.add(horizontalGlue);
		
		JButton btnReport = new JButton("Izveštaj");
		btnReport.addActionListener(new BtnReportActionListener());
		btnReport.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		bottomPanel.add(btnReport);

	}

	protected JCheckBox getChckbxAll() {
		return chckbxAll;
	}

	private ConsumerCheckBox getCheckBoxById(Long id) {
		return consumers.stream()
				.filter(x -> x.getConsumer().getId().equals(id))
				.findFirst()
				.get();
	}

	private List<Long> getChosenIds() {
		List<Long> ids = new ArrayList<Long>();
		consumers.stream()
				.filter(x -> x.isSelected())
				.forEach(x -> ids.add(x.getConsumer().getId()));
		return ids;
	}

	private void populateTableWithData(Iterable<Tankovanje> fillUps) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		model.setRowCount(0);
		fillUps.forEach(x -> {
			model.addRow(new Object[] {sdf.format(x.getDatum()), x.getPotrosac().toString(), x.getMesec(), 
					DecimalFormater.formatFromLong(x.getKolicina(), 2), 
					DecimalFormater.formatFromLong(x.getJedCena(), 2), 
					DecimalFormater.formatFromLong(x.getJedCena() * x.getKolicina() / 100L, 2)});
		});
	}

	private class ChckbxHideObsoleteItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent ev) {
			// TODO dodaj sakivanje zastarelih vozila
		}
	}
	private class ChckbxAllActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ev) {
			if (chckbxAll.isSelected()) {
				consumers.stream().iterator().forEachRemaining(x -> {
					if (!x.isSelected()) {
						x.setSelected(true);
					}
				});
			}
		}
	}
	private class ChckbxUniversalActionListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			if (((ConsumerCheckBox)ev.getSource()).isSelected()) {
				if (!consumers.stream()
						.filter(x -> !x.isSelected())
						.findAny()
						.isPresent()) {
					if (!chckbxAll.isSelected()) {
						chckbxAll.setSelected(true);
					}
				}
			} else {
				chckbxAll.setSelected(false);
			}
		}
	}
	private class BtnSearchActionListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			if (chckbxAll.isSelected()) {
				if (StringUtils.isEmpty(tfStartFrom.getText()) && StringUtils.isEmpty(tfEndsWith.getText())) {
					populateTableWithData(tankovanjeDao.findAll());
				} else if (!StringUtils.isEmpty(tfStartFrom.getText()) && !StringUtils.isEmpty(tfEndsWith.getText())) {
					populateTableWithData(tankovanjeDao.listInInterval(tfStartFrom.getText(), tfEndsWith.getText()));
				} else if (!StringUtils.isEmpty(tfStartFrom.getText()) && StringUtils.isEmpty(tfEndsWith.getText())) {
					populateTableWithData(tankovanjeDao.listFromMonth(tfStartFrom.getText()));
				} else {
					populateTableWithData(tankovanjeDao.listTilMonth(tfEndsWith.getText()));
				}
			} else {
				List<Long> ids = getChosenIds();
				if (ids.size() == 1) {
					if (StringUtils.isEmpty(tfStartFrom.getText()) && StringUtils.isEmpty(tfEndsWith.getText())) {
						populateTableWithData(tankovanjeDao.listForConsumer(ids.get(0)));
					} else if (!StringUtils.isEmpty(tfStartFrom.getText()) && !StringUtils.isEmpty(tfEndsWith.getText())) {
						populateTableWithData(tankovanjeDao.listInIntervalForConsumer(ids.get(0), tfStartFrom.getText(), 
								tfEndsWith.getText()));
					} else if (!StringUtils.isEmpty(tfStartFrom.getText()) && StringUtils.isEmpty(tfEndsWith.getText())) {
						populateTableWithData(tankovanjeDao.listFromMonthForConsumer(ids.get(0), tfStartFrom.getText()));
					} else {
						populateTableWithData(tankovanjeDao.listTilMonthForConsumer(ids.get(0), tfEndsWith.getText()));
					}
				} else {
					if (StringUtils.isEmpty(tfStartFrom.getText()) && StringUtils.isEmpty(tfEndsWith.getText())) {
						populateTableWithData(tankovanjeDao.listForConsumers(ids));
					} else if (!StringUtils.isEmpty(tfStartFrom.getText()) && !StringUtils.isEmpty(tfEndsWith.getText())) {
						populateTableWithData(tankovanjeDao.listInIntervalForConsumers(ids, tfStartFrom.getText(), 
								tfEndsWith.getText()));
					} else if (!StringUtils.isEmpty(tfStartFrom.getText()) && StringUtils.isEmpty(tfEndsWith.getText())) {
						populateTableWithData(tankovanjeDao.listFromMonthForConsumers(ids, tfStartFrom.getText()));
					} else {
						populateTableWithData(tankovanjeDao.listTilMonthForConsumers(ids, tfEndsWith.getText()));
					}
				}
			}
		}
	}
	private class BtnReportActionListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			sdf.setLenient(false);
			try {
				sdf.parse(tfStartFrom.getText());
			} catch (ParseException e) {
				new ErrorDialog().showError("Moraš uneti početni mesec.");
				return;
			}
			try {
				sdf.parse(tfEndsWith.getText());
			} catch (ParseException e) {
				new ErrorDialog().showError("Moraš uneti završni mesec.");
				return;
			}
			List<Long> ids = getChosenIds();
			if (ids.size() == 0) {
				new ErrorDialog().showError("Moraš izabrati vozilo za koje će se ispisati izveštaj.");
				return;
			}
			try {
				for (Long id : ids) {
					reporter.makeReport(getCheckBoxById(id).getConsumer(), tfStartFrom.getText(), tfEndsWith.getText());
				}
				new ErrorDialog().showError("Gotovo.");
			} catch (IOException e) {
				new ErrorDialog().showError("Desila se greška tokom čitanja/pisanja datoteka.");
			} catch (ParseException e) {
				new ErrorDialog().showError("Desila se greška prilikom obrade podataka.");
			}
		}
	}
}
