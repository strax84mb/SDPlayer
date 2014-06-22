package prog.paket.playlist.generator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.Box;
import javax.swing.JLabel;
import java.awt.Component;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ErrorInfoDialog extends JDialog {

	private static final long serialVersionUID = 8714824231743603621L;

	private final JPanel contentPanel = new JPanel();
	private JLabel lblText;

	public void showError(String text){
		lblText.setText("<html>" + text + "</html>");
		lblText.setHorizontalAlignment(SwingConstants.CENTER);
		pack();
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((size.width/2)-(getSize().width/2), (size.height/2)-(getSize().height/2));
		setVisible(true);
	}

	public void showError(String text, String title){
		setTitle(title);
		lblText.setText("<html>" + text + "</html>");
		lblText.setHorizontalAlignment(SwingConstants.CENTER);
		pack();
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((size.width/2)-(getSize().width/2), (size.height/2)-(getSize().height/2));
		setVisible(true);
	}

	/**
	 * Create the dialog.
	 */
	public ErrorInfoDialog() {
		setModal(true);
		setResizable(false);
		//setBounds(100, 100, 334, 288);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			Box verticalBox = Box.createVerticalBox();
			verticalBox.setBorder(new EmptyBorder(5, 5, 5, 5));
			contentPanel.add(verticalBox);
			{
				lblText = new JLabel("<html>Greska! Broj sekundi mora<br/>biti ceo pozitivan broj.</html>");
				lblText.setAlignmentX(Component.CENTER_ALIGNMENT);
				lblText.setHorizontalTextPosition(SwingConstants.CENTER);
				lblText.setHorizontalAlignment(SwingConstants.CENTER);
				verticalBox.add(lblText);
			}
			{
				Component verticalStrut = Box.createVerticalStrut(5);
				verticalBox.add(verticalStrut);
			}
			{
				JButton btnOk = new JButton("U redu");
				btnOk.addActionListener(new BtnOkActionListener());
				btnOk.setAlignmentX(Component.CENTER_ALIGNMENT);
				verticalBox.add(btnOk);
			}
		}
	}

	public JLabel getLblText() {
		return lblText;
	}
	private class BtnOkActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			setVisible(false);
		}
	}
}
