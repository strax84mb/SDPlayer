package prog.paket.baza;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Component;
import javax.swing.Box;

import prog.paket.baza.struct.menutree.TraitNode;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class TraitDlg extends JDialog {

	private static final long serialVersionUID = 956784183470934708L;

	private final JPanel contentPanel = new JPanel();
	public JLabel lblNaziv;
	public JTextField tfNaziv;
	public JLabel lblSkrac;
	public JTextField tfSkrac;
	public JLabel lblID;
	public Component horizontalStrut;

	private int id = -1;

	public boolean shouldSave = false;

	public void fillFields(TraitNode node){
		if(node == null){
			this.id = -1;
			lblID.setText("ID broj kategorije:");
			tfNaziv.setText("");
			tfSkrac.setText("");
			return;
		}
		this.id = node.getId();
		lblID.setText("ID broj kategorije: " + String.valueOf(node.getId()));
		tfNaziv.setText(node.getName());
		tfSkrac.setText(node.getAbrev());
	}

	public void setID(int id){
		this.id= id;
		lblID.setText("ID broj kategorije: " + String.valueOf(id));
	}

	public int getID(){
		return id;
	}

	public String getTraitName(){
		return tfNaziv.getText().trim();
	}

	public String getAbrev(){
		return tfSkrac.getText().trim();
	}

	/**
	 * Create the dialog.
	 */
	public TraitDlg() {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setBounds(100, 100, 276, 232);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);
		
		lblNaziv = new JLabel("Naziv kategorije");
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblNaziv, 10, SpringLayout.WEST, contentPanel);
		contentPanel.add(lblNaziv);
		
		tfNaziv = new JTextField();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, tfNaziv, 5, SpringLayout.SOUTH, lblNaziv);
		sl_contentPanel.putConstraint(SpringLayout.WEST, tfNaziv, 5, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, tfNaziv, -5, SpringLayout.EAST, contentPanel);
		contentPanel.add(tfNaziv);
		tfNaziv.setColumns(10);
		
		lblSkrac = new JLabel("Skra\u0107enica");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblSkrac, 20, SpringLayout.SOUTH, tfNaziv);
		contentPanel.add(lblSkrac);
		
		tfSkrac = new JTextField();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, tfSkrac, -5, SpringLayout.NORTH, lblSkrac);
		sl_contentPanel.putConstraint(SpringLayout.WEST, tfSkrac, 10, SpringLayout.EAST, lblSkrac);
		sl_contentPanel.putConstraint(SpringLayout.EAST, tfSkrac, -5, SpringLayout.EAST, contentPanel);
		contentPanel.add(tfSkrac);
		tfSkrac.setColumns(10);
		
		lblID = new JLabel("ID broj kategorije:");
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblSkrac, 0, SpringLayout.WEST, lblID);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblNaziv, 20, SpringLayout.SOUTH, lblID);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblID, 10, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblID, 10, SpringLayout.NORTH, contentPanel);
		contentPanel.add(lblID);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnOK = new JButton("Snimi");
				btnOK.addActionListener(new BtnOKActionListener());
				btnOK.setActionCommand("OK");
				buttonPane.add(btnOK);
				getRootPane().setDefaultButton(btnOK);
			}
			
			horizontalStrut = Box.createHorizontalStrut(20);
			buttonPane.add(horizontalStrut);
			{
				JButton btnCancel = new JButton("Otka\u017Ei");
				btnCancel.addActionListener(new BtnCancelActionListener());
				btnCancel.setActionCommand("Cancel");
				buttonPane.add(btnCancel);
			}
		}
	}
	private class BtnCancelActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			setVisible(false);
		}
	}
	private class BtnOKActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			shouldSave = true;
			setVisible(false);
		}
	}
}
