package rs.trznica.dragan.forms;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.springframework.context.ApplicationContext;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import rs.trznica.dragan.dto.tankovanje.BaseDto;
import rs.trznica.dragan.forms.support.ModalResult;

public abstract class GenericDialogV2<T> extends GenericDialog<T> {

	private static final long serialVersionUID = 2746828131664760447L;
	
	private ApplicationContext ctx;
	private Long entityId = null;
	
	private JPanel contentPanel;
	private JButton btnOk;
	private JButton btnCancel;
	private JPanel panelBottom;

	protected ApplicationContext getContext() {
		return ctx;
	}
	
	protected Long getEntityId() {
		return entityId;
	}

	protected void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public JPanel getContentPanel() {
		return contentPanel;
	}

	public JButton getOkButton() {
		return btnOk;
	}

	public JButton getCancelButton() {
		return btnCancel;
	}

	public JPanel getPanelBottom() {
		return panelBottom;
	}

	public GenericDialogV2() {
		super();
	}
	
	protected abstract Map<String, Object> setProperties();
	
	/**
	 * 
	 * @param ctx
	 * @param properties - Properties list:<ul>
	 * <li>titleText (String)</li>
	 * <li>xPos (int)</li>
	 * <li>yPos (int)</li>
	 * <li>width (int)</li>
	 * <li>height (int)</li>
	 * </ul>
	 */
	public GenericDialogV2(ApplicationContext ctx, int contentRows) {
		super();
		this.ctx = ctx;
		autowireFields(ctx);
		
		setModal(true);
		getContentPane().setFont(defaultFont);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		Map<String, Object> properties = setProperties();
		setBounds(
				(int) properties.get("xPos"), 
				(int) properties.get("yPos"), 
				(int) properties.get("width"), 
				(int) properties.get("height"));

		if (properties.get("titleText") != null) {
			setTitle((String) properties.get("titleText"));
			JPanel panelTop = new JPanel();
			getContentPane().add(panelTop, BorderLayout.NORTH);
			
			JLabel lblTitle = new JLabel((String) properties.get("titleText"));
			lblTitle.setFont(new Font("Times New Roman", Font.PLAIN, 20));
			panelTop.add(lblTitle);
		}
		
		setUpButtonsPanel();
		
		contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_panelCenter = new GridBagLayout();
		gbl_panelCenter.columnWidths = new int[] {173, 251};
		gbl_panelCenter.rowHeights = new int[contentRows];
		gbl_panelCenter.columnWeights = new double[]{0.0, 1.0};
		gbl_panelCenter.rowWeights = new double[contentRows];
		for (int i = 0; i < gbl_panelCenter.rowWeights.length; i++) {
			gbl_panelCenter.rowHeights[i] = 0;
			gbl_panelCenter.rowWeights[i] = 0.0;
		}
		contentPanel.setLayout(gbl_panelCenter);

		setUpCenterPanel(contentPanel);
	}
	
	protected JButton[] makeAdditionalButtons() {
		return null;
	}
	
	private void setUpButtonsPanel() {
		JPanel panelBottom = new JPanel();
		panelBottom.setBorder(new EmptyBorder(0, 0, 5, 0));
		getContentPane().add(panelBottom, BorderLayout.SOUTH);
		panelBottom.setLayout(new BoxLayout(panelBottom, BoxLayout.X_AXIS));
		
		Component horizontalGlue = Box.createHorizontalGlue();
		panelBottom.add(horizontalGlue);
		
		btnOk = new JButton("Snimi");
		btnOk.addActionListener(new BtnOkActionListener());
		btnOk.setFont(defaultFont);
		btnOk.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelBottom.add(btnOk);
		
		JButton[] additionalButtons = makeAdditionalButtons();
		if (additionalButtons != null && additionalButtons.length > 0) {
			for (JButton button : additionalButtons) {
				panelBottom.add(Box.createHorizontalStrut(20));
				
				button.setFont(defaultFont);
				button.setAlignmentX(Component.CENTER_ALIGNMENT);
				panelBottom.add(button);
			}
		}
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		panelBottom.add(horizontalStrut);
		
		btnCancel = new JButton("Otka\u017Ei");
		btnCancel.addActionListener(new BtnCancelActionListener());
		btnCancel.setFont(defaultFont);
		panelBottom.add(btnCancel);
		
		Component horizontalGlue_1 = Box.createHorizontalGlue();
		panelBottom.add(horizontalGlue_1);
	}
	
	protected abstract void setUpCenterPanel(JPanel contentPanel);
	
	protected abstract void autowireFields(ApplicationContext ctx);
	
	protected abstract BaseDto<T> makeDto();
	
	protected abstract void saveNewEntity(T newEntity);
	
	protected abstract Validator makeValidator();
	
	private class BtnOkActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			BaseDto<T> dto = makeDto();
			BindingResult result = new DataBinder(dto).getBindingResult();
			makeValidator().validate(dto, result);
			if (result.getErrorCount() == 0) {
				T entity = dto.createEntityFromData();
				saveNewEntity(entity);
				modalResult = ModalResult.OK;
				setVisible(false);
			} else {
				modalResult = ModalResult.CANCEL;
				ErrorDialog dialog = new ErrorDialog();
				dialog.showErrors(result);
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
