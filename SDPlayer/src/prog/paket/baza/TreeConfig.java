package prog.paket.baza;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;

import prog.paket.baza.struct.menutree.TraitChangeEvent;
import prog.paket.baza.struct.menutree.TraitListener;
import prog.paket.baza.struct.menutree.TraitNode;
import prog.paket.baza.struct.menutree.TraitPanel;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Component;
import javax.swing.Box;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.JLabel;
import javax.swing.BoxLayout;

public class TreeConfig extends JDialog {

	private static final long serialVersionUID = -1154085832413503398L;

	public boolean isSaved = false;

	private final JPanel contentPanel = new JPanel();
	private TraitPanel traitPanel = new TraitPanel();
	private String primaryStr = "";
	private String defaultStr = "";
	private JLabel lblSetting = new JLabel("<html>Podrazumevano: <br/>Srednja kolona:</html>");

	private void loadStructure(String path){
		try{
			File file = new File(path);
			if(!file.exists()) return;
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			int id, parentID;
			String name, abrev;
			TraitNode parentTrait;
			TraitNode tNode;
			try{
				traitPanel.middleColID = ois.readInt();
				traitPanel.defaultID = ois.readInt();
				while(true){
					id = ois.readInt();
					name = ois.readUTF();
					abrev = ois.readUTF();;
					parentID = ois.readInt();
					if(parentID == 0)
						parentTrait = traitPanel.getRootNode();
					else parentTrait = traitPanel.getByID(traitPanel.getRootNode(), parentID);
					tNode = new TraitNode(id, name, abrev);
					parentTrait.add(tNode);
				}
			}catch(EOFException eofe){}
			ois.close();
			fis.close();
			if(traitPanel.middleColID != Integer.MIN_VALUE){
				TraitNode tn = traitPanel.getByID(traitPanel.getRootNode(), traitPanel.middleColID);
				if(tn != null){
					primaryStr = String.valueOf(tn.getId()) + " - " + tn.getName() + " - " + tn.getAbrev();
				}
			}
			if(traitPanel.defaultID != Integer.MIN_VALUE){
				TraitNode tn = traitPanel.getByID(traitPanel.getRootNode(), traitPanel.defaultID);
				if(tn != null){
					defaultStr = String.valueOf(tn.getId()) + " - " + tn.getName() + " - " + tn.getAbrev();
				}
			}
			getLblSetting().setText("<html>Podrazumevano: " + defaultStr + "<br/>Srednja kolona: " 
					+ primaryStr + "</html>");
		}catch(Exception e){e.printStackTrace();}
	}

	protected void saveTraitNode(ObjectOutputStream oos, TraitNode tn) throws IOException{
		TraitNode temp = null;
		for(int i=0,len=tn.getChildCount();i<len;i++){
			temp = tn.getChildAt(i);
			// Snimanje
			oos.writeInt(temp.getId());
			oos.writeUTF(temp.getName());
			oos.writeUTF(temp.getAbrev());
			oos.writeInt(temp.getParent().getId());
			// Provera postojanja podkategorija
			if(temp.getChildCount() > 0){
				saveTraitNode(oos, temp);
			}
		}
	}

	private void saveStructure(String path){
		try{
			FileOutputStream fos = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeInt(traitPanel.middleColID);
			oos.writeInt(traitPanel.defaultID);
			saveTraitNode(oos, traitPanel.getRootNode());
			oos.close();
			fos.close();
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * Create the dialog.
	 */
	public TreeConfig() {
		addWindowListener(new ThisWindowListener());
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setBounds(100, 100, 783, 590);
		traitPanel.addTraitListener(new TraitChangeListener());
		loadStructure("baza/structure/kat_list.dat");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane);
			{
				scrollPane.setViewportView(traitPanel);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnOK = new JButton("Snimi");
				btnOK.addActionListener(new BtnOKActionListener());
				buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
				{
					Component horizontalStrut = Box.createHorizontalStrut(10);
					buttonPane.add(horizontalStrut);
				}
				{
					//lblSetting = new JLabel("<html>Podrazumevano: <br/>Srednja kolona:</html>");
					buttonPane.add(lblSetting);
				}
				{
					Component horizontalGlue = Box.createHorizontalGlue();
					buttonPane.add(horizontalGlue);
				}
				btnOK.setActionCommand("OK");
				buttonPane.add(btnOK);
				getRootPane().setDefaultButton(btnOK);
			}
			{
				Component horizontalStrut = Box.createHorizontalStrut(40);
				buttonPane.add(horizontalStrut);
			}
			{
				JButton btnCancel = new JButton("Otka\u017Ei");
				btnCancel.addActionListener(new BtnCancelActionListener());
				btnCancel.setActionCommand("Cancel");
				buttonPane.add(btnCancel);
			}
			{
				Component horizontalStrut = Box.createHorizontalStrut(10);
				buttonPane.add(horizontalStrut);
			}
		}
	}

	private class ThisWindowListener extends WindowAdapter {
		@Override
		public void windowActivated(WindowEvent e) {
			traitPanel.repaint();
		}
	}
	private class BtnCancelActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			setVisible(false);
		}
	}
	private class BtnOKActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			saveStructure("baza/structure/kat_list.dat");
			isSaved = true;
			setVisible(false);
		}
	}
	private class TraitChangeListener implements TraitListener {
		@Override
		public void traitChanged(TraitChangeEvent event) {
			switch(event.getChangeType()){
			case SET_DEFAULT:
				defaultStr = String.valueOf(event.getTrait().getId()) + " - " + 
						event.getTrait().getName() + " - " + event.getTrait().getAbrev();
				break;
			case SET_PRIMARY:
				primaryStr = String.valueOf(event.getTrait().getId()) + " - " + 
						event.getTrait().getName() + " - " + event.getTrait().getAbrev();
				break;
			case REMOVED:
				if(event.getTrait().getId() == traitPanel.middleColID){
					traitPanel.middleColID = Integer.MIN_VALUE;
					primaryStr = "";
				}
				if(event.getTrait().getId() == traitPanel.defaultID){
					traitPanel.defaultID = Integer.MIN_VALUE;
					defaultStr = "";
				}
				break;
			case EDITED:
				if(event.getTrait().getId() == traitPanel.middleColID){
					primaryStr = String.valueOf(event.getTrait().getId()) + " - " + 
							event.getTrait().getName() + " - " + event.getTrait().getAbrev();
				}
				if(event.getTrait().getId() == traitPanel.defaultID){
					defaultStr = String.valueOf(event.getTrait().getId()) + " - " + 
							event.getTrait().getName() + " - " + event.getTrait().getAbrev();
				}
				break;
			default:
				return;
			}
			getLblSetting().setText("<html>Podrazumevano: " + defaultStr + "<br/>Srednja kolona: " 
					+ primaryStr + "</html>");
		}
	}
	public JLabel getLblSetting() {
		return lblSetting;
	}
}
