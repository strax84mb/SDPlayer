package rs.trznica.dragan.forms;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.eclipse.wb.swing.FocusTraversalOnArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import rs.trznica.dragan.dao.lucene.BrojiloDao;
import rs.trznica.dragan.dao.lucene.OcitavanjeDao;
import rs.trznica.dragan.dto.struja.OcitavanjeDto;
import rs.trznica.dragan.entities.struja.Brojilo;
import rs.trznica.dragan.entities.struja.Ocitavanje;
import rs.trznica.dragan.entities.struja.VrstaBrojila;
import rs.trznica.dragan.entities.support.BrojiloComparator;
import rs.trznica.dragan.forms.support.DecimalFormater;
import rs.trznica.dragan.forms.support.ModalResult;
import rs.trznica.dragan.validator.exceptions.ChangeNotAcceptedException;
import rs.trznica.dragan.validator.struja.OcitavanjeValidator;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OcitavanjeForm extends GenericDialog<Ocitavanje> {

	private static final long serialVersionUID = 1808096789221427421L;

	private ApplicationContext ctx;
	private OcitavanjeDao ocitavanjeDao;
	private BrojiloDao brojiloDao;
	
	private Long entityId = null;
	
	private JComboBox<Brojilo> cbBrojila;
	private JTextField tfMesec;
	private JTextField tfKwVT;
	private JTextField tfKwNT;
	private JTextField tfCenaVT;
	private JTextField tfCenaNT;
	private JTextField tfPristup;
	private JTextField tfPodsticaj;
	private JTextField tfKwReatkivna;
	private JTextField tfCenaKW;
	private JTextField tfCenaReaktivna;
	private JLabel lblKwNT;
	private JLabel lblCenaNT;
	private JButton btnSave;
	private JButton btnSaveAnother;
	private JButton btnCancel;
	
	@Autowired
	public OcitavanjeForm(ApplicationContext ctx) {
		this.ctx = ctx;
		ocitavanjeDao = this.ctx.getBean(OcitavanjeDao.class);
		brojiloDao = this.ctx.getBean(BrojiloDao.class);
		setModal(true);
		setTitle("O\u010Ditavanje");
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 550);
		setResizable(false);
		
		JPanel panelTop = new JPanel();
		getContentPane().add(panelTop, BorderLayout.NORTH);
		
		JLabel lblTitle = new JLabel("O\u010Ditavanje mernog mesta");
		lblTitle.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		panelTop.add(lblTitle);
		
		JPanel panelBottom = new JPanel();
		panelBottom.setBorder(new EmptyBorder(0, 0, 5, 0));
		getContentPane().add(panelBottom, BorderLayout.SOUTH);
		panelBottom.setLayout(new BoxLayout(panelBottom, BoxLayout.X_AXIS));
		
		panelBottom.add(Box.createHorizontalGlue());
		btnSave = makeButton("Snimi i zatvori", new BtnSaveActionListener());
		panelBottom.add(btnSave);
		panelBottom.add(Box.createHorizontalStrut(15));
		btnSaveAnother = makeButton("Snimi i ne zatvori", new BtnSaveAnotherActionListener());
		panelBottom.add(btnSaveAnother);
		panelBottom.add(Box.createHorizontalStrut(15));
		btnCancel = makeButton("Otka\u017Ei", new BtnCancelActionListener());
		panelBottom.add(btnCancel);
		panelBottom.add(Box.createHorizontalGlue());
		
		JPanel panelCenter = makeCenterPanel(100, 250);
		
		DecimalsFocusListener twoDecimalsFocusListener = new DecimalsFocusListener(2);
		DecimalsFocusListener zeroDecimalsFocusListener = new DecimalsFocusListener(0);
		CalculateFocusListener zeroDigitsCalcFocusLstnr = new CalculateFocusListener(0);
		CalculateFocusListener threeDigitsCalcFocusLstnr = new CalculateFocusListener(3);
		
		cbBrojila = new JComboBox<Brojilo>();
		cbBrojila.setFont(defaultFont);
		addComponent(panelCenter, 0, new JLabel("Merno mesto:"), cbBrojila, true);
		cbBrojila.addItemListener(new CbBrojilaItemListener());
		
		tfMesec = makeTextField(panelCenter, 1, new JLabel("Mesec:"), 10);
		
		lblKwNT = new JLabel("kW niže tarife:");
		tfKwNT = makeTextField(panelCenter, 2, lblKwNT, 10);
		tfKwNT.addFocusListener(zeroDecimalsFocusListener);
		tfKwVT = makeTextField(panelCenter, 3, new JLabel("kW vi\u0161e tarife:"), 10);
		tfKwVT.addFocusListener(zeroDecimalsFocusListener);
		lblCenaNT = new JLabel("Fin. niže tarife:");
		tfCenaNT = makeTextField(panelCenter, 4, lblCenaNT, 15);
		tfCenaNT.addFocusListener(twoDecimalsFocusListener);
		tfCenaVT = makeTextField(panelCenter, 5, new JLabel("Fin. vi\u0161e tarife:"), 15);
		tfCenaVT.addFocusListener(twoDecimalsFocusListener);
		tfPristup = makeTextField(panelCenter, 6, new JLabel("Fin. pristup:"), 15);
		tfPristup.addFocusListener(twoDecimalsFocusListener);
		tfPodsticaj = makeTextField(panelCenter, 7, new JLabel("Fin. podsticaj:"), 15);
		tfPodsticaj.addFocusListener(twoDecimalsFocusListener);
		tfCenaKW = makeTextField(panelCenter, 8, new JLabel("Reakt. cena 1 kW:"), 15);
		tfCenaKW.addFocusListener(threeDigitsCalcFocusLstnr);
		tfCenaKW.setEnabled(false);
		tfKwReatkivna = makeTextField(panelCenter, 9, new JLabel("Reakt. potro\u0161eno kW:"), 10);
		tfKwReatkivna.addFocusListener(zeroDigitsCalcFocusLstnr);
		tfKwReatkivna.setEnabled(false);
		
		tfCenaReaktivna = makeTextField(panelCenter, 10, new JLabel("Reakt. ukupna cena:"), 15);
		tfCenaReaktivna.setEditable(false);

		populateCounters();
		setFocusTraversalPolicy(new FocusTraversalOnArray(new java.awt.Component[] {cbBrojila, tfMesec, 
				tfKwNT, tfKwVT, tfCenaNT, tfCenaVT, tfPristup, tfPodsticaj, tfCenaKW, tfKwReatkivna, 
				btnSave, btnSaveAnother, btnCancel}));
		traverseByEnter(cbBrojila);
		traverseByEnter(tfMesec);
		traverseByEnter(tfKwNT);
		traverseByEnter(tfKwVT);
		traverseByEnter(tfCenaNT);
		traverseByEnter(tfCenaVT);
		traverseByEnter(tfPristup);
		traverseByEnter(tfPodsticaj);
		traverseByEnter(tfCenaKW);
		traverseByEnter(tfKwReatkivna);
	}
	
	private void populateCounters() {
		try {
			cbBrojila.removeAllItems();
			brojiloDao.findAll().stream().sorted(new BrojiloComparator()).forEach(x -> cbBrojila.addItem(x));
		} catch (IOException e) {
			e.printStackTrace();
			ErrorDialog err = new ErrorDialog();
			err.showError("Desila se gre\u0161ka tokom \u010Ditanja svih mernih mesta: " + e.getMessage());
		}
		
	}
	
	@Override
	public void editObject(Ocitavanje object) {
		for (int i = 0; i < cbBrojila.getItemCount(); i++) {
			if (cbBrojila.getItemAt(i).getId().equals(object.getBrojiloId())) {
				cbBrojila.setSelectedIndex(i);
				break;
			}
		}
		cbBrojila.setEditable(false);
		tfMesec.setText(object.getMesec());
		tfKwNT.setText(DecimalFormater.formatFromLongSep(object.getKwNT(), 0));
		tfCenaNT.setText(DecimalFormater.formatFromLongSep(object.getCenaNT(), 2));
		if (!VrstaBrojila.SIR_POT_JED.equals(object.getBrojiloVrsta())) {
			tfKwVT.setText(DecimalFormater.formatFromLongSep(object.getKwVT(), 0));
			tfCenaVT.setText(DecimalFormater.formatFromLongSep(object.getCenaVT(), 2));
		} else {
			tfKwVT.setText("");
			tfCenaVT.setText("");
		}
		tfPristup.setText(DecimalFormater.formatFromLongSep(object.getPristup(), 2));
		tfPodsticaj.setText(DecimalFormater.formatFromLongSep(object.getPodsticaj(), 2));
		if (VrstaBrojila.MAXIGRAF.equals(object.getBrojiloVrsta())) {
			tfKwReatkivna.setText(DecimalFormater.formatFromLongSep(object.getKwReaktivna(), 0));
			tfCenaKW.setText(DecimalFormater.formatFromLongSep(object.getCenaKW(), 3));
			tfKwReatkivna.setEnabled(true);
			tfCenaKW.setEnabled(true);
		} else {
			tfKwReatkivna.setEnabled(false);
			tfCenaKW.setEnabled(false);
		}
		btnSaveAnother.setEnabled(false);
	}
	
	private class BtnSaveActionListener extends AbstractAction {
		private static final long serialVersionUID = -708744007672184315L;
		public BtnSaveActionListener() {
			putValue(NAME, "Snimi i zatvori");
		}
		@Override
		public void actionPerformed(ActionEvent ev) {
			OcitavanjeDto dto = new OcitavanjeDto(
					(Brojilo) cbBrojila.getSelectedItem(), 
					tfMesec.getText(), 
					tfKwVT.getText(), 
					tfKwNT.getText(), 
					tfCenaVT.getText(), 
					tfCenaNT.getText(), 
					tfPristup.getText(), 
					tfPodsticaj.getText(), 
					tfKwReatkivna.getText(), 
					tfCenaKW.getText());
			
			BindingResult result = new DataBinder(dto).getBindingResult();
			new OcitavanjeValidator().validate(dto, result);
			if (result.getErrorCount() > 0) {
				ErrorDialog dialog = new ErrorDialog();
				dialog.showErrors(result);
			} else {
				try {
					Ocitavanje ocitavanje = dto.getEntity();
					if (entityId == null) {
						ocitavanje = ocitavanjeDao.save(ocitavanje);
						System.out.println(ocitavanje.getId());
					} else {
						YesNoDialog dlg = new YesNoDialog("Sigurno \u017Eelite snimiti une\u0161ene izmene?");
						dlg.showDialogInCenter(getThisForm());
						if (!ModalResult.YES.equals(dlg.getModalResult())) {
							throw new ChangeNotAcceptedException();
						}
						ocitavanje.setId(entityId);
						ocitavanjeDao.update(ocitavanje);
					}
					setReturnValue(ocitavanje);
					modalResult = ModalResult.OK;
					setVisible(false);
				} catch (ChangeNotAcceptedException e1) {
					setReturnValue(null);
					modalResult = ModalResult.CANCEL;
				} catch (Exception e1) {
					e1.printStackTrace();
					ErrorDialog dialog = new ErrorDialog();
					dialog.showError("Desila se gre\u0161ka prilikom snimanja.\n" + e1.getMessage());
				}
			}
		}
	}
	private class BtnSaveAnotherActionListener extends AbstractAction {
		private static final long serialVersionUID = -7557344942523331144L;
		public BtnSaveAnotherActionListener() {
			putValue(NAME, "Snimi i ne zatvori");
		}
		@Override
		public void actionPerformed(ActionEvent ev) {
			OcitavanjeDto dto = new OcitavanjeDto(
					(Brojilo) cbBrojila.getSelectedItem(), 
					tfMesec.getText(), 
					tfKwVT.getText(), 
					tfKwNT.getText(), 
					tfCenaVT.getText(), 
					tfCenaNT.getText(), 
					tfPristup.getText(), 
					tfPodsticaj.getText(), 
					tfKwReatkivna.getText(), 
					tfCenaKW.getText());
			
			BindingResult result = new DataBinder(dto).getBindingResult();
			if (result.getErrorCount() > 0) {
				ErrorDialog dialog = new ErrorDialog();
				dialog.showErrors(result);
			} else {
				try {
					Ocitavanje ocitavanje = dto.getEntity();
					ocitavanje = ocitavanjeDao.save(ocitavanje);
					setReturnValue(ocitavanje);
					cbBrojila.setSelectedIndex(-1);
					tfCenaNT.setText("");
					tfCenaVT.setText("");
					tfKwNT.setText("");
					tfKwVT.setText("");
					tfPodsticaj.setText("");
					tfPristup.setText("");
					tfKwReatkivna.setText("");
					modalResult = ModalResult.CANCEL;
					cbBrojila.requestFocus();
				} catch (Exception e1) {
					e1.printStackTrace();
					ErrorDialog dialog = new ErrorDialog();
					dialog.showError("Desila se gre\u0161ka prilikom snimanja.\n" + e1.getMessage());
				}
			}
		}
	}
	private class BtnCancelActionListener extends AbstractAction {
		private static final long serialVersionUID = 6728713737378259271L;
		public BtnCancelActionListener() {
			putValue(NAME, "Otka\u017Ei");
		}
		@Override
		public void actionPerformed(ActionEvent ev) {
			setNullReturnValue();
			modalResult = ModalResult.CANCEL;
			setVisible(false);
		}
	}
	private class DecimalsFocusListener extends FocusAdapter {
		protected int decimals;
		protected DecimalsFocusListener(int decimals) {
			this.decimals = decimals;
		}
		protected void doCheck(FocusEvent ev) {
			JTextField comp = (JTextField) ev.getComponent();
			if (StringUtils.isEmpty(comp.getText())) {
				return;
			}
			try {
				Long num = DecimalFormater.parseToLong(comp.getText(), decimals);
				comp.setText(DecimalFormater.formatFromLong(num, decimals));
			} catch(Exception e) {
				e.printStackTrace();
				comp.setText("");
			}
		}
		@Override
		public void focusLost(FocusEvent ev) {
			doCheck(ev);
		}
	}
	private class CalculateFocusListener extends DecimalsFocusListener {
		protected CalculateFocusListener(int decimals) {
			super(decimals);
		}
		@Override
		public void focusLost(FocusEvent ev) {
			doCheck(ev);
			if (!StringUtils.isEmpty(tfKwReatkivna.getText()) && !StringUtils.isEmpty(tfCenaKW.getText())) {
				try {
					Long kw = DecimalFormater.parseToLong(tfKwReatkivna.getText(), 0);
					Long cena = DecimalFormater.parseToLong(tfCenaKW.getText(), 3);
					Long result = kw * cena / 10L;
					tfCenaReaktivna.setText(DecimalFormater.formatFromLong(result, 2));
				} catch (Exception e) {
					tfCenaReaktivna.setText("");
				}
			}
		}
	}
	private class CbBrojilaItemListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent ev) {
			if (ev.getStateChange() == ItemEvent.SELECTED) {
				if (cbBrojila.getSelectedIndex() == -1) {
					tfKwReatkivna.setEnabled(false);
					tfCenaKW.setEnabled(false);
					tfCenaReaktivna.setEnabled(false);
				} else {
					switch(cbBrojila.getItemAt(cbBrojila.getSelectedIndex()).getVrstaBrojila()) {
					case SIR_POT_JED:
						lblKwNT.setText("kW srednje tarife:");
						lblCenaNT.setText("Fin. srednje tarife:");
						tfKwVT.setEnabled(false);
						tfCenaVT.setEnabled(false);
						tfKwReatkivna.setEnabled(false);
						tfCenaKW.setEnabled(false);
						tfCenaReaktivna.setEnabled(false);
						break;
					case SIR_POT_DVO:
						lblKwNT.setText("kW ni\u017Ee tarife:");
						lblCenaNT.setText("Fin. ni\u017Ee tarife:");
						tfKwVT.setEnabled(true);
						tfCenaVT.setEnabled(true);
						tfKwReatkivna.setEnabled(false);
						tfCenaKW.setEnabled(false);
						tfCenaReaktivna.setEnabled(false);
						break;
					default:
						lblKwNT.setText("kW ni\u017Ee tarife:");
						lblCenaNT.setText("Fin. ni\u017Ee tarife:");
						tfKwVT.setEnabled(true);
						tfCenaVT.setEnabled(true);
						tfKwReatkivna.setEnabled(true);
						tfCenaKW.setEnabled(true);
						tfCenaReaktivna.setEnabled(true);
						break;
					}
					tfKwReatkivna.setEnabled(true);
					tfCenaKW.setEnabled(true);
					tfCenaReaktivna.setEnabled(true);
				}
			}
		}
	}
}
