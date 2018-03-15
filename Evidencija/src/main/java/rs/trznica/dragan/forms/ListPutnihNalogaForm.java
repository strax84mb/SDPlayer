package rs.trznica.dragan.forms;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import rs.trznica.dragan.dao.PotrosacDao;
import rs.trznica.dragan.dao.lucene.PutniNalogDao;
import rs.trznica.dragan.entities.putninalog.PutniNalog;
import rs.trznica.dragan.entities.tankovanje.Potrosac;
import rs.trznica.dragan.forms.support.DescriptionLabel;

import com.toedter.calendar.JDateChooser;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ListPutnihNalogaForm extends JInternalFrame {
	
	private static final long serialVersionUID = -6508367169171685256L;

	private static final Logger LOG = LoggerFactory.getLogger(ListPutnihNalogaForm.class);
	private final Font defaultFont = new Font("Times New Roman", Font.PLAIN, 18);

	private ApplicationContext ctx;
	private PotrosacDao potrosacDao;
	private PutniNalogDao putniNalogDao;
	
	private JComboBox<Potrosac> vozila;
	private JDateChooser dcStart;
	private JDateChooser dcEnd;
	private JButton btnShow;
	private JButton btnPrint;
	private JButton btnEdit;
	private JButton btnDelete;
	
	private JPanel upperPanel;
	private DescriptionLabel descriptionLabel;
	private JTable table;
	
	@Autowired
	public ListPutnihNalogaForm(ApplicationContext ctx) {
		// Autowire fields
		this.ctx = ctx;
		potrosacDao = ctx.getBean(PotrosacDao.class);
		putniNalogDao = ctx.getBean(PutniNalogDao.class);
		// Setup dialog
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		setBounds(100, 100, 654, 417);
		setTitle("Lista putnih naloga");
		getContentPane().setLayout(new BorderLayout());
		// Setup upper components
		upperPanel = new JPanel(new BorderLayout());
		vozila = listVehicles();
		Box vozilaBox = createBox(false, "Vozilo:", 5, vozila);
		vozilaBox.setBorder(new EmptyBorder(5, 5, 0, 5));
		upperPanel.add(vozilaBox, BorderLayout.NORTH);
		dcStart = new JDateChooser();
		dcStart.setFont(defaultFont);
		dcEnd = new JDateChooser();
		dcEnd.setFont(defaultFont);
		Box datesBox = createBox(false, "Od:", 5, dcStart, 15, "Do:", 5, dcEnd);
		datesBox.setBorder(new EmptyBorder(5, 15, 5, 5));
		upperPanel.add(datesBox, BorderLayout.CENTER);
		btnShow = new JButton(new ShowAction());
		btnShow.setFont(defaultFont);
		Box searchBox = createBox(false, 10, btnShow, 5);
		searchBox.setBorder(new EmptyBorder(5, 5, 5, 0));
		upperPanel.add(searchBox, BorderLayout.EAST);
		getContentPane().add(upperPanel, BorderLayout.NORTH);
		// Setup center components
		table = new JTable();
		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		// Setup bottom panel
		btnPrint = new JButton(new PrintAction());
		btnPrint.setFont(defaultFont);
		btnEdit = new JButton(new EditAction());
		btnEdit.setFont(defaultFont);
		btnDelete = new JButton(new DeleteAction());
		btnDelete.setFont(defaultFont);
		Box bottomBox = createBox(true, btnPrint, 15, btnEdit, 15, btnDelete);
		bottomBox.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(bottomBox, BorderLayout.SOUTH);
		// Setup description panel
		descriptionLabel = new DescriptionLabel();
		descriptionLabel.setFont(defaultFont);
		getContentPane().add(descriptionLabel, BorderLayout.EAST);
	}
	
	private JComboBox<Potrosac> listVehicles() {
		JComboBox<Potrosac> vozila = new JComboBox<Potrosac>();
		vozila.setFont(defaultFont);
		potrosacDao.listVehicles().forEach(vehicle -> vozila.addItem(vehicle));
		return vozila;
	}
	
	private Box createBox(boolean compact, Object... components) {
		Box box = Box.createHorizontalBox();
		if (compact) {
			box.add(Box.createHorizontalGlue());
		}
		for (Object component : components) {
			if (component.getClass().isAssignableFrom(int.class)) {
				box.add(Box.createHorizontalStrut((int) component));
			} else if (component.getClass().isAssignableFrom(Integer.class)) {
				box.add(Box.createHorizontalStrut((Integer) component));
			} else if (component.getClass().isAssignableFrom(String.class)) {
				box.add(createLabel((String) component));
			} else {
				box.add((java.awt.Component) component);
			}
		}
		if (compact) {
			box.add(Box.createHorizontalGlue());
		}
		return box;
	}
	
	private JLabel createLabel(String text) {
		JLabel label = new JLabel();
		label.setFont(defaultFont);
		label.setText(text);
		return label;
	}
	
	private class ShowAction extends AbstractAction {
		
		private static final long serialVersionUID = 802887950094988144L;

		public ShowAction() {
			putValue(Action.NAME, "Prika\u017Ei");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (vozila.getSelectedIndex() == -1) {
				new ErrorDialog().showError("Mora\u0161 izabrati neko vozilo.");
				return;
			}
			if (startAfterEnd()) {
				new ErrorDialog().showError("Po\u010Detni datum ne mo\u017Ee biti posle zavr\u0161nog.");
				return;
			}
			Potrosac vozilo = (Potrosac) vozila.getSelectedItem();
			List<PutniNalog> nalozi;
			if (dcStart.getDate() == null && dcEnd.getDate() == null) {
				try {
					nalozi = putniNalogDao.getAll(vozilo.getId());
				} catch (IOException e1) {
					LOG.error("Greska prilikom citanja!", e1);
					new ErrorDialog().showError("Gre\u0161ka prilikom \u010Ditanja!");
					return;
				}
			} else {
				try {
					nalozi = putniNalogDao.getInInterval(vozilo.getId(), dcStart.getDate(), dcEnd.getDate());
				} catch (IOException e1) {
					LOG.error("Greska prilikom citanja!", e1);
					new ErrorDialog().showError("Gre\u0161ka prilikom \u010Ditanja!");
					return;
				}
			}
			// TODO Fill table
		}
		
		private boolean startAfterEnd() {
			Date start = dcStart.getDate();
			Date end = dcStart.getDate();
			if (start == null || end == null) {
				return false;
			} else {
				return start.getTime() > end.getTime();
			}
		}
		
	}
	
	private class PrintAction extends AbstractAction {
		
		private static final long serialVersionUID = 1963364585093374909L;

		public PrintAction() {
			putValue(Action.NAME, "Od\u0161tampaj");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
		}
	}
	
	private class EditAction extends AbstractAction {
		
		private static final long serialVersionUID = 7881154047543244763L;

		public EditAction() {
			putValue(Action.NAME, "Izmeni");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
		}
	}
	
	private class DeleteAction extends AbstractAction {
		
		private static final long serialVersionUID = 6033670577564638279L;

		public DeleteAction() {
			putValue(Action.NAME, "Obri\u0161i");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
		}
	}
}