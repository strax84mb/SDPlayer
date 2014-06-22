package prog.paket.playlist.generator;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import prog.paket.dodaci.SDModalResult;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CatNameDialog extends JDialog {

	private static final long serialVersionUID = 6663353843233457792L;

	public SDModalResult modalResult = SDModalResult.CANCEL;

	private final JPanel contentPanel = new JPanel();
	public JLabel lblNazivKategorije;
	public JTextField tfNaziv;

	/**
	 * Create the dialog.
	 */
	public CatNameDialog() {
		addWindowListener(new ThisWindowListener());
		setModal(true);
		setBounds(100, 100, 199, 129);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		lblNazivKategorije = new JLabel("Naziv:");
		
		tfNaziv = new JTextField();
		tfNaziv.setBackground(Color.WHITE);
		tfNaziv.setColumns(10);
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(tfNaziv, GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
						.addComponent(lblNazivKategorije))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNazivKategorije)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tfNaziv, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(67, Short.MAX_VALUE))
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Zapamti");
				okButton.addActionListener(new OkButtonActionListener());
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Otka\u017Ei");
				cancelButton.addActionListener(new CancelButtonActionListener());
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	private class CancelButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			modalResult = SDModalResult.CANCEL;
			setVisible(false);
		}
	}
	private class OkButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			tfNaziv.setText(tfNaziv.getText().trim());
			modalResult = SDModalResult.OK;
			setVisible(false);
		}
	}
	private class ThisWindowListener extends WindowAdapter {
		@Override
		public void windowOpened(WindowEvent arg0) {
			tfNaziv.requestFocusInWindow();
		}
	}
}
