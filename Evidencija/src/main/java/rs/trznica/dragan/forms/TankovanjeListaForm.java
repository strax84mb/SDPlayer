package rs.trznica.dragan.forms;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

import rs.trznica.dragan.dao.PotrosacDao;
import rs.trznica.dragan.dao.TankovanjeDao;
import rs.trznica.dragan.forms.support.ConsumerCheckBox;

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

	private void populateAutowiredFields(ApplicationContext ctx) {
		potrosacDao = ctx.getBean(PotrosacDao.class);
		tankovanjeDao = ctx.getBean(TankovanjeDao.class);
	}

	/**
	 * Create the frame.
	 */
	@Autowired
	public TankovanjeListaForm(ApplicationContext ctx) {
		populateAutowiredFields(ctx);
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		setBounds(100, 100, 568, 417);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
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
		chckbxAll.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		chckbxAll.setHorizontalAlignment(SwingConstants.LEFT);
		paramsPanel.add(chckbxAll);
		
		Component verticalGlue = Box.createVerticalGlue();
		paramsPanel.add(verticalGlue);
		
		JCheckBox chckbxHideObsolete = new JCheckBox("Sakrij zastarele");
		chckbxHideObsolete.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		paramsPanel.add(chckbxHideObsolete);
		
		JButton btnSearch = new JButton("Prika\u017Ei");
		btnSearch.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		paramsPanel.add(btnSearch);
		
		JPanel bottomPanel = new JPanel();
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);

	}

	protected JCheckBox getChckbxAll() {
		return chckbxAll;
	}

}
