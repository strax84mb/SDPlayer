package rs.trznica.dragan.forms;

import org.eclipse.wb.swing.FocusTraversalOnArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import rs.trznica.dragan.dao.BrojiloRepository;
import rs.trznica.dragan.entities.struja.BrojiloSql;
import rs.trznica.dragan.entities.struja.VrstaBrojila;
import rs.trznica.dragan.forms.support.ModalResult;
import rs.trznica.dragan.validator.exceptions.ChangeNotAcceptedException;
import rs.trznica.dragan.validator.struja.BrojiloValidator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BrojiloForm extends GenericDialog<BrojiloSql> {

	private static final long serialVersionUID = -5269728175774257655L;

	private ApplicationContext ctx;
	private BrojiloRepository brojiloRepository;

	private JTextField tfBroj;
	private JTextField tfED;
	private JTextField tfOpis;
	private JCheckBox chckbxUFunkciji;
	private JComboBox<VrstaBrojila> cbVrstaBrojila;
	
	private Long entityId = null;

	private ModalResult modalResult = ModalResult.CANCEL;
	private BrojiloSql returnValue = null;

	@Autowired
	public BrojiloForm(ApplicationContext ctx) {
		this.ctx = ctx;
		brojiloRepository = this.ctx.getBean(BrojiloRepository.class);
		setModal(true);
		setTitle("Merno mesto");
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 309);
		setResizable(false);
		
		JPanel panelTop = new JPanel();
		getContentPane().add(panelTop, BorderLayout.NORTH);
		
		JLabel lblTitle = new JLabel("Merno mesto");
		lblTitle.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		panelTop.add(lblTitle);
		
		JPanel panelBottom = new JPanel();
		panelBottom.setBorder(new EmptyBorder(0, 0, 5, 0));
		getContentPane().add(panelBottom, BorderLayout.SOUTH);
		panelBottom.setLayout(new BoxLayout(panelBottom, BoxLayout.X_AXIS));
		
		panelBottom.add(Box.createHorizontalGlue());
		JButton btnOk = makeButton("Snimi", new BtnOkActionListener());
		panelBottom.add(btnOk);
		panelBottom.add(Box.createHorizontalStrut(20));
		JButton btnCancel = makeButton("Otka\u017Ei", new BtnCancelActionListener());
		panelBottom.add(btnCancel);
		panelBottom.add(Box.createHorizontalGlue());
		
		JPanel panelCenter = makeCenterPanel(100, 250);
		
		tfED = makeTextField(panelCenter, 0, new JLabel("ED broj"), 12);
		tfBroj = makeTextField(panelCenter, 1, new JLabel("Broj brojila"), 12);
		tfOpis = makeTextField(panelCenter, 2, new JLabel("Opis"), null);
		chckbxUFunkciji = makeCheckBox(panelCenter, 3, "U funkciji");
		chckbxUFunkciji.setSelected(true);
		
		cbVrstaBrojila = new JComboBox<VrstaBrojila>();
		for (VrstaBrojila vrsta : VrstaBrojila.values()) {
			cbVrstaBrojila.addItem(vrsta);
		}
		cbVrstaBrojila.setSelectedIndex(-1);
		addComponent(panelCenter, 4, new JLabel("Vrsta brojila"), cbVrstaBrojila, true);
		
		setFocusTraversalPolicy(new FocusTraversalOnArray(new java.awt.Component[] {tfED, tfBroj, tfOpis, 
				chckbxUFunkciji, cbVrstaBrojila, btnOk, btnCancel}));
		traverseByEnter(tfED);
		traverseByEnter(tfBroj);
		traverseByEnter(tfOpis);
		traverseByEnter(chckbxUFunkciji);
		traverseByEnter(cbVrstaBrojila);
	}

	@Override
	public void editObject(BrojiloSql object) {
		entityId = object.getId();
		tfBroj.setText(object.getBroj());
		tfED.setText(object.getEd());
		tfOpis.setText(object.getOpis());
		chckbxUFunkciji.setSelected(object.getUFunkciji());
		for (int i = 0; i < cbVrstaBrojila.getItemCount(); i++) {
			if (cbVrstaBrojila.getItemAt(i).equals(object.getVrstaBrojila())) {
				cbVrstaBrojila.setSelectedIndex(i);
				break;
			}
		}
		cbVrstaBrojila.setEditable(false);
	}

	public ModalResult getModalResult() {
		return modalResult;
	}
	
	public BrojiloSql getReturnValue() {
		return returnValue;
	}
	
	private class BtnOkActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			BrojiloSql brojilo = BrojiloSql.builder()
					.broj(tfBroj.getText())
					.ed(tfED.getText())
					.opis(tfOpis.getText())
					.uFunkciji(chckbxUFunkciji.isSelected())
					.vrstaBrojila((VrstaBrojila) cbVrstaBrojila.getSelectedItem())
					.build();
			brojilo.setVrstaBrojila((VrstaBrojila) cbVrstaBrojila.getSelectedItem());
			
			BindingResult result = new DataBinder(brojilo).getBindingResult();
			new BrojiloValidator().validate(brojilo, result);
			if (result.getErrorCount() > 0) {
				ErrorDialog dialog = new ErrorDialog();
				dialog.showErrors(result);
			} else {
				try {
					if (entityId == null) {
						brojilo = brojiloRepository.save(brojilo);
						System.out.println(brojilo.getId());
					} else {
						YesNoDialog dlg = new YesNoDialog("Sigurno \u017Eelite snimiti une\u0161ene izmene?");
						dlg.showDialogInCenter(getThisForm());
						if (!ModalResult.YES.equals(dlg.getModalResult())) {
							throw new ChangeNotAcceptedException();
						}
						brojilo.setId(entityId);
						brojiloRepository.save(brojilo);
					}
					returnValue = brojilo;
					modalResult = ModalResult.OK;
					setVisible(false);
				} catch (ChangeNotAcceptedException e1) {
					returnValue = null;
					modalResult = ModalResult.CANCEL;
				} catch (Exception e1) {
					e1.printStackTrace();
					ErrorDialog dialog = new ErrorDialog();
					dialog.showError("Desila se gre\u0161ka prilikom snimanja.\n" + e1.getMessage());
				}
			}
		}
		
	}
	
	private class BtnCancelActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			modalResult = ModalResult.CANCEL;
			setVisible(false);
		}
	}
}
