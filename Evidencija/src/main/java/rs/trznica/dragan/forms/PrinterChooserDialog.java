package rs.trznica.dragan.forms;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

public class PrinterChooserDialog extends JDialog {

	private static final long serialVersionUID = -6392671137546951697L;
	
	private PrintService returnValue = null;

	private JComboBox<PrintService> cbPrinters = new JComboBox<PrintService>();
	private JButton btnPrint;
	private JButton btnCancel;
	private Font defaultFont = new Font("Times New Roman", Font.PLAIN, 16);
	
	public PrinterChooserDialog() {
		setResizable(false);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		
		Box box = Box.createHorizontalBox();
		box.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		JLabel label = new JLabel("\u0160tampa\u010Di");
		label.setFont(defaultFont);
		box.add(label);
		box.add(Box.createHorizontalStrut(10));
		
		btnPrint = new JButton("Od\u0161tampaj");
		btnPrint.setFont(defaultFont);
		btnPrint.addActionListener(new ChooseListener());
		btnPrint.setEnabled(false);
		btnCancel = new JButton("Otka\u017Ei");
		btnCancel.setFont(defaultFont);
		btnCancel.addActionListener(new CancelListener());
		
		cbPrinters = new JComboBox<PrintService>();
		cbPrinters.setFont(defaultFont);
		loadPrinters();
		box.add(cbPrinters);
		
		getContentPane().add(box, BorderLayout.NORTH);
		
		box = Box.createHorizontalBox();
		box.setBorder(new EmptyBorder(5, 5, 5, 5));
		box.add(Box.createHorizontalGlue());
		box.add(btnPrint);
		box.add(Box.createHorizontalStrut(15));
		box.add(btnCancel);
		box.add(Box.createHorizontalGlue());
		
		getContentPane().add(box, BorderLayout.SOUTH);
		
		pack();
		int x = (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - (getWidth() / 2);
		int y = (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - (getHeight() / 2);
		setLocation(x, y);
	}
	
	public PrintService getReturnValue() {
		return returnValue;
	}
	
	private void loadPrinters() {
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, pras);
		for (PrintService service : services) {
			cbPrinters.addItem(service);
		}
		PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
		for (int i = 0; i < cbPrinters.getItemCount(); i++) {
			if (cbPrinters.getItemAt(i).getName().equals(defaultService.getName())) {
				cbPrinters.setSelectedIndex(i);
				btnPrint.setEnabled(true);
				break;
			}
		}
	}
	
	private class ChooseListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ev) {
			returnValue = (PrintService) cbPrinters.getSelectedItem();
			setVisible(false);
		}
	}
	
	private class CancelListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ev) {
			returnValue = null;
			setVisible(false);
		}
	}
}
