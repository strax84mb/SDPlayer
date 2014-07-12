package prog.paket.recstreamer.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.RandomAccessFile;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class PortSelectorDlg extends JDialog {

	private final JPanel contentPanel = new JPanel();
	public JTextField tfURL;
	public JComboBox<Mixer.Info> cbInput;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			PortSelectorDlg dialog = new PortSelectorDlg();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadAllPorts(){
		Mixer.Info[] infos = AudioSystem.getMixerInfo();
		for(int i=0,len=infos.length;i<len;i++){
			cbInput.addItem(infos[i]);
		}
		cbInput.setSelectedIndex(-1);
		try{
			RandomAccessFile raf = new RandomAccessFile("sound.conf", "rw");
			raf.seek(0L);
			byte index = raf.readByte();
			cbInput.setSelectedIndex(index);
			// TODO More functions
			raf.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void saveSettings(){
		try{
			RandomAccessFile raf = new RandomAccessFile("sound.conf", "rw");
			raf.seek(0L);
			raf.writeByte(cbInput.getSelectedIndex());
			// TODO More functions
			raf.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public PortSelectorDlg() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		{
			JLabel lblInput = new JLabel("Veza sa miksetom:");
			contentPanel.add(lblInput);
		}
		{
			cbInput = new JComboBox<Mixer.Info>();
			cbInput.setMaximumSize(new Dimension(32767, 25));
			cbInput.setAlignmentX(Component.LEFT_ALIGNMENT);
			contentPanel.add(cbInput);
		}
		{
			Component verticalStrut = Box.createVerticalStrut(20);
			contentPanel.add(verticalStrut);
		}
		{
			JLabel lblURL = new JLabel("URL za striming:");
			contentPanel.add(lblURL);
		}
		{
			tfURL = new JTextField();
			tfURL.setMaximumSize(new Dimension(2147483647, 25));
			tfURL.setAlignmentX(Component.LEFT_ALIGNMENT);
			contentPanel.add(tfURL);
			tfURL.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnOk = new JButton("OK");
				btnOk.addActionListener(new BtnOkActionListener());
				btnOk.setActionCommand("OK");
				buttonPane.add(btnOk);
				getRootPane().setDefaultButton(btnOk);
			}
			{
				JButton btnCancel = new JButton("Cancel");
				btnCancel.addActionListener(new BtnCancelActionListener());
				btnCancel.setActionCommand("Cancel");
				buttonPane.add(btnCancel);
			}
		}
		loadAllPorts();
	}

	private void closeThis(){
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	private class BtnCancelActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			closeThis();
		}
	}
	private class BtnOkActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			saveSettings();
			closeThis();
		}
	}
}
