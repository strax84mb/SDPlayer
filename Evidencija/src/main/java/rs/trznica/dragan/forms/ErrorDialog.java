package rs.trznica.dragan.forms;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.springframework.validation.BindingResult;
import javax.swing.border.EmptyBorder;

public class ErrorDialog extends JDialog {

	/** Serial version UID */
	private static final long serialVersionUID = -6965190318056642344L;

	private JLabel lblText;

	private JDialog getThisForm() {
		return this;
	}

	public void showError(String error) {
		lblText.setText(error);
		pack();
		setVisible(true);
	}

	public void showErrors(BindingResult result) {
		StringBuilder builder = new StringBuilder();
		result.getGlobalErrors().stream().forEach(x -> {
			builder.append(x.getCode()).append("<br/>");
		});
		lblText.setText(builder.toString());
		pack();
		setVisible(true);
	}

	/**
	 * Create the dialog.
	 */
	public ErrorDialog() {
		setModal(true);
		setTitle("Gre\u0161ka");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
			{
				Component horizontalGlue = Box.createHorizontalGlue();
				buttonPane.add(horizontalGlue);
			}
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new OkButtonActionListener());
				okButton.setFont(new Font("Times New Roman", Font.PLAIN, 18));
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				Component horizontalGlue = Box.createHorizontalGlue();
				buttonPane.add(horizontalGlue);
			}
		}
		{
			lblText = new JLabel("");
			lblText.setBorder(new EmptyBorder(5, 5, 5, 5));
			lblText.setFont(new Font("Times New Roman", Font.PLAIN, 18));
			getContentPane().add(lblText, BorderLayout.CENTER);
		}
	}

	private class OkButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			getThisForm().setVisible(false);
			getThisForm().dispose();
		}
	}
}
