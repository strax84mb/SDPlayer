package rs.trznica.dragan.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;

import rs.trznica.dragan.dto.tankovanje.VoziloDodatnoDto;
import rs.trznica.dragan.forms.support.ModalResult;

@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class VoziloDodatnoForm extends GenericDialog<VoziloDodatnoDto> {

	private static final long serialVersionUID = -5235983373687659163L;
	
	private JTextField tfPodrucje;
	private JTextField tfBrojSedista;
	private JTextField tfSnagaMotora;
	private JTextField tfTezina;
	private JTextField tfNosivost;
	private JTextField tfRBNaloga;
	private JTextArea taVozaci;
	
	private JButton btnSave;
	private JButton btnCancel;
	
	@Override
	public void editObject(VoziloDodatnoDto object) {
	}
	
	@Autowired
	public VoziloDodatnoForm(ApplicationContext ctx) {
		setModal(true);
		setTitle("Dodatni podaci o vozilu");
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 500);
		setResizable(true);
		
		JPanel panelTop = new JPanel();
		getContentPane().add(panelTop, BorderLayout.NORTH);
		
		JLabel lblTitle = new JLabel("Dodatni podaci o vozilu");
		lblTitle.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		panelTop.add(lblTitle);
		
		JPanel panelCenter = makeCenterPanel(100, 250);
		
		tfPodrucje = makeTextField(panelCenter, 0, new JLabel("Podru\u010Dje"), 4);
		tfBrojSedista = makeTextField(panelCenter, 1, new JLabel("Broj sedi\u0161ta"), 4);
		tfSnagaMotora = makeTextField(panelCenter, 2, new JLabel("Snaga motora"), 4);
		tfTezina = makeTextField(panelCenter, 3, new JLabel("Te\u017Eina u kg"), 10);
		tfNosivost = makeTextField(panelCenter, 4, new JLabel("Nosivost u kg"), 10);
		tfRBNaloga = makeTextField(panelCenter, 5, new JLabel("Redni broj naloga"), 10);
		taVozaci = new JTextArea(0, 0);
		taVozaci.setFont(defaultFont);
		JScrollPane scPane = new JScrollPane();
		scPane.setViewportView(taVozaci);
		scPane.setPreferredSize(new Dimension(250, 100));
		addComponent(panelCenter, 6, new JLabel("Voza\u010Di"), scPane, true);
		
		JPanel panelBottom = new JPanel();
		panelBottom.setBorder(new EmptyBorder(0, 0, 5, 0));
		getContentPane().add(panelBottom, BorderLayout.SOUTH);
		panelBottom.setLayout(new BoxLayout(panelBottom, BoxLayout.X_AXIS));
		
		panelBottom.add(Box.createHorizontalGlue());
		btnSave = makeButton("Gotovo", new BtnOkActionListener());
		panelBottom.add(btnSave);
		panelBottom.add(Box.createHorizontalStrut(20));
		btnCancel = makeButton("Otka\u017Ei", new BtnCancelActionListener());
		panelBottom.add(btnCancel);
		panelBottom.add(Box.createHorizontalGlue());
	}
	
	private class BtnOkActionListener implements ActionListener {
		
		private Integer checkNumericValue(String value, String errorMsg) throws Exception {
			try {
				Integer numValue = Integer.valueOf(value.trim());
				if (numValue < 0) {
					throw new Exception(errorMsg);
				}
				return numValue;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				throw new Exception(errorMsg);
			}
		}
		
		@Override
		public void actionPerformed(ActionEvent ev) {
			try {
				VoziloDodatnoDto dto = new VoziloDodatnoDto();
				if (StringUtils.isEmpty(tfPodrucje.getText().trim())) {
					throw new Exception("Podru\u010Dje mora biti navedeno.");
				}
				dto.setPodrucje(tfPodrucje.getText().trim());
				dto.setBrojSedista(checkNumericValue(tfBrojSedista.getText(), "Broj sedi\u0161ta mora biti ceo pozitivan broj ili nula."));
				dto.setSnagaMotora(checkNumericValue(tfSnagaMotora.getText(), "Snaga motora mora biti ceo pozitivan broj ili nula."));
				dto.setTezina(checkNumericValue(tfTezina.getText(), "Te\u017Eina u kg mora biti ceo pozitivan broj ili nula."));
				dto.setNosivost(checkNumericValue(tfNosivost.getText(), "Nosivost u kg mora biti ceo pozitivan broj ili nula."));
				dto.setrBNaloga(checkNumericValue(tfRBNaloga.getText(), "Redni broj poslednjeg naloga mora biti ceo pozitivan broj ili nula."));
				dto.setVozaci(taVozaci.getText().trim());
				
				setReturnValue(dto);
				modalResult = ModalResult.OK;
				
				setVisible(false);
			} catch (Exception e) {
				e.printStackTrace();
				new ErrorDialog().showError(e.getMessage());
			}
		}
	}

	private class BtnCancelActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			modalResult = ModalResult.CANCEL;
			getThisForm().setVisible(false);
		}
	}
}
