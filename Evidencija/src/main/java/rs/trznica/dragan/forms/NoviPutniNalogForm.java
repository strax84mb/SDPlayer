package rs.trznica.dragan.forms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import rs.trznica.dragan.dao.PotrosacDao;
import rs.trznica.dragan.dao.lucene.PutniNalogDao;
import rs.trznica.dragan.dto.tankovanje.BaseDto;
import rs.trznica.dragan.dto.tankovanje.PutniNalogDto;
import rs.trznica.dragan.entities.putninalog.PutniNalog;
import rs.trznica.dragan.entities.tankovanje.Potrosac;
import rs.trznica.dragan.forms.support.DateUtils;
import rs.trznica.dragan.forms.support.ModalResult;
import rs.trznica.dragan.forms.support.StringUtils;
import rs.trznica.dragan.printables.CargoIssuePrintable;
import rs.trznica.dragan.printables.PassengerIssuePrintable;
import rs.trznica.dragan.validator.tankovanje.PutniNalogValidator;

import com.toedter.calendar.JDateChooser;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NoviPutniNalogForm extends GenericDialogV2<PutniNalog> {

	private static final long serialVersionUID = -6515551917024945896L;
	
	private PutniNalogDao putniNalogDao;
	private PotrosacDao potrosacDao;
	
	private JComboBox<Potrosac> cbVozila;
	private JTextField tfRedniBroj;
	private JComboBox<String> cbVozac;
	private JTextField tfRelacija;
	private JTextField tfVrstaPrevoza;
	private JTextField tfKorisnik;
	private JTextField tfPosada;
	private JTextField tfRO;
	private JTextField tfAdresaGaraze;
	private JTextField tfMesto;
	private JDateChooser dcDatum;
	
	private JButton btnPrint;

	@Autowired
	public NoviPutniNalogForm(ApplicationContext ctx) {
		super(ctx, 11);
	}
	
	private JComboBox<Potrosac> setUpVehiclesList() {
		JComboBox<Potrosac> comboBox = new JComboBox<Potrosac>();
		comboBox.setFont(defaultFont);
		potrosacDao.listVehicles().forEach(x -> comboBox.addItem(x));
		comboBox.setSelectedIndex(-1);
		return comboBox;
	}
	
	@Override
	protected void setUpCenterPanel(JPanel contentPanel) {
		cbVozila = setUpVehiclesList();
		cbVozila.addItemListener(new CbVozilaItemListener());
		addComponent(contentPanel, 0, new JLabel("Vozilo"), cbVozila, true);
		
		cbVozac = new JComboBox<String>();
		cbVozac.setFont(defaultFont);
		cbVozac.setEditable(true);
		addComponent(contentPanel, 1, new JLabel("Voza\u010D"), cbVozac, true);
		
		tfRedniBroj = makeTextField(contentPanel, 2, new JLabel("Redni broj"), null);
		
		dcDatum = new JDateChooser();
		dcDatum.setDateFormatString("dd.MM.yyyy");
		dcDatum.setFont(defaultFont);
		addComponent(contentPanel, 3, new JLabel("Datum"), dcDatum, true);
		
		tfRelacija = makeTextField(contentPanel, 4, new JLabel("Relacija"), null);
		tfVrstaPrevoza = makeTextField(contentPanel, 5, new JLabel("Vrsta prevoza"), null);
		tfVrstaPrevoza.setText("Sopstvene potrebe");
		tfKorisnik = makeTextField(contentPanel, 6, new JLabel("Korisnik"), null);
		tfKorisnik.setText("A. D. \"Tr\u017Enica\"");
		tfPosada = makeTextField(contentPanel, 7, new JLabel("Posada"), null);
		tfRO = makeTextField(contentPanel, 8, new JLabel("Radna org."), null);
		tfRO.setText("A. D. \"Tr\u017Enica\"");
		tfAdresaGaraze = makeTextField(contentPanel, 9, new JLabel("Adresa gara\u017Ee"), null);
		tfAdresaGaraze.setText("Matije Gupca 50");
		tfMesto = makeTextField(contentPanel, 10, new JLabel("Mesto"), null);
		tfMesto.setText("Subotica");
	}

	@Override
	protected void autowireFields(ApplicationContext ctx) {
		putniNalogDao = getContext().getBean(PutniNalogDao.class);
		potrosacDao = getContext().getBean(PotrosacDao.class);
	}

	@Override
	protected BaseDto<PutniNalog> makeDto() {
		return new PutniNalogDto(tfRedniBroj.getText(), 
				(Potrosac) cbVozila.getSelectedItem(), 
				(String) cbVozac.getSelectedItem(), 
				tfRelacija.getText(), 
				dcDatum.getDate(), 
				tfVrstaPrevoza.getText(), 
				tfKorisnik.getText(), 
				tfPosada.getText(), 
				tfRO.getText(), 
				tfAdresaGaraze.getText(), 
				tfMesto.getText());
	}

	@Override
	protected void saveNewEntity(PutniNalog newEntity) {
		if (getEntityId() != null) {
			newEntity.setId(getEntityId());
		}
		try {
			PutniNalog nalog = putniNalogDao.save(newEntity);
			setReturnValue(nalog);
			modalResult = ModalResult.OK;
		} catch (Exception e) {
			new ErrorDialog().showError("Gre\u0161ka: " + e.getMessage());
		}
	}

	@Override
	public void editObject(PutniNalog nalog) {
		setEntityId(nalog.getId());
		setReturnValue(nalog);
		for (int i = 0; i < cbVozila.getItemCount(); i++) {
			if (nalog.getId().equals(cbVozila.getItemAt(i).getId())) {
				cbVozila.setSelectedIndex(i);
				repopulateDrivers(cbVozila.getItemAt(i).getVozaci());
				break;
			}
		}
		cbVozac.setSelectedItem(nalog.getVozac());
		tfRedniBroj.setText(nalog.getRedniBroj().toString());
		tfRelacija.setText(nalog.getRelacija());
		tfVrstaPrevoza.setText(nalog.getVrstaPrevoza());
		tfKorisnik.setText(nalog.getKorisnik());
		tfPosada.setText(nalog.getPosada());
		tfRO.setText(nalog.getRegOznaka());
		tfAdresaGaraze.setText(nalog.getAdresaGaraze());
		tfMesto.setText(nalog.getMesto());
		dcDatum.setDate(DateUtils.toDate(nalog.getDatum()));
	}

	@Override
	protected Map<String, Object> setProperties() {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("xPos", 100);
		properties.put("yPos", 100);
		properties.put("width", 450);
		properties.put("height", 530);
		return properties;
	}

	@Override
	protected JButton[] makeAdditionalButtons() {
		btnPrint = new JButton("Od\u0161tampaj");
		btnPrint.addActionListener(new BtnPrintActionListener());
		
		return new JButton[] {btnPrint};
	}
	
	private void repopulateDrivers(String vozaciString) {
		cbVozac.removeAllItems();
		if (StringUtils.isNotEmpty(vozaciString)) {
			String vozaci[] = vozaciString.split("\n");
			for (String vozac : vozaci) {
				cbVozac.addItem(vozac);
			}
		}
		cbVozac.setSelectedItem(null);
	}
	
	private void printIssue(PutniNalog nalog) throws IOException, PrintException {
		if (nalog == null) {
			PutniNalogDto dto = (PutniNalogDto) makeDto();
			BindingResult result = new DataBinder(dto).getBindingResult();
			makeValidator().validate(dto, result);
			if (result.getErrorCount() == 0) {
				nalog = dto.createEntityFromData();
			} else {
				modalResult = ModalResult.CANCEL;
				ErrorDialog dialog = new ErrorDialog();
				dialog.showErrors(result);
				return;
			}
		}
		PrinterChooserDialog dlg = new PrinterChooserDialog();
		dlg.setVisible(true);
		if (dlg.getReturnValue() != null) {
			if (PutniNalog.TERETNI.equals(nalog.getNamenaVozila())) {
				PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
				attrs.add(new MediaPrintableArea(1f, 0.5f, 
						MediaSize.ISO.A4.getX(MediaSize.INCH)-0.5f, 
						MediaSize.ISO.A4.getY(MediaSize.INCH)-0.5f, 
						MediaSize.INCH));
				attrs.add(OrientationRequested.LANDSCAPE);
				DocPrintJob job = dlg.getReturnValue().createPrintJob();
				SimpleDoc doc = new SimpleDoc(new CargoIssuePrintable(nalog), 
						DocFlavor.SERVICE_FORMATTED.PRINTABLE, null);
				job.print(doc, attrs);
			} else {
				PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
				attrs.add(new MediaPrintableArea(1f, 0.5f, 
						MediaSize.ISO.A4.getX(MediaSize.INCH)-0.5f, 
						MediaSize.ISO.A4.getY(MediaSize.INCH)-0.5f, 
						MediaSize.INCH));
				attrs.add(OrientationRequested.PORTRAIT);
				DocPrintJob job = dlg.getReturnValue().createPrintJob();
				SimpleDoc doc = new SimpleDoc(new PassengerIssuePrintable(nalog), 
						DocFlavor.SERVICE_FORMATTED.PRINTABLE, null);
				job.print(doc, attrs);
			}
		}
	}
	
	public class BtnPrintActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ev) {
			try {
				printIssue(null);
			} catch (Exception e) {
				new ErrorDialog().showError(e.getMessage());
			}
		}
	}

	@Override
	protected Validator makeValidator() {
		return new PutniNalogValidator();
	}
	
	public class CbVozilaItemListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent ev) {
			if (ev.getStateChange() == ItemEvent.SELECTED) {
				Potrosac vozilo = (Potrosac) ev.getItem();
				if (vozilo != null) {
					repopulateDrivers(vozilo.getVozaci());
					tfRedniBroj.setText(String.valueOf(vozilo.getrBNaloga() + 1));
					if (vozilo.getTeretnjak()) {
						if (StringUtils.isEmpty(tfRelacija.getText())) {
							tfRelacija.setText("Lokal");
						}
						tfPosada.setEnabled(true);
						tfKorisnik.setEnabled(false);
					} else {
						tfPosada.setEnabled(false);
						tfKorisnik.setEnabled(true);
					}
				}
			}
		}
		
	}
}
