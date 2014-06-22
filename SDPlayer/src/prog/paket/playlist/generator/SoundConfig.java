package prog.paket.playlist.generator;

import java.awt.EventQueue;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.UIManager;

import java.awt.Component;
import javax.swing.Box;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.awt.Dimension;

public class SoundConfig extends JFrame {

	private static final long serialVersionUID = -2338312187945328285L;

	private JPanel contentPane;
	public JLabel lblZvucnici;
	public JComboBox<Mixer.Info> cbSpeakers;
	public JLabel lblPreslusavanje;
	public JComboBox<Mixer.Info> cbPreslusavanje;
	public JPanel panel;
	public Component horizontalGlue;
	public JButton btnOk;
	public Component horizontalStrut;
	public JButton btnCancel;
	public Component horizontalGlue_1;
	public Component verticalStrut;
	public Component verticalStrut_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					SoundConfig frame = new SoundConfig();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void loadMixers(){
		Mixer.Info[] infos = AudioSystem.getMixerInfo();
		for(int i=0,len=infos.length;i<len;i++){
			cbSpeakers.addItem(infos[i]);
			cbPreslusavanje.addItem(infos[i]);
		}
		try{
			FileInputStream fis = new FileInputStream("sound.conf");
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader reader = new BufferedReader(isr);
			cbSpeakers.setSelectedIndex(Integer.parseInt(reader.readLine()));
			int mic = Integer.parseInt(reader.readLine());
			cbPreslusavanje.setSelectedIndex(mic);
			reader.close();
			isr.close();
			fis.close();
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * Create the frame.
	 */
	public SoundConfig() {
		setTitle("Konfiguracija kanala");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 166);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		lblZvucnici = new JLabel("Zvu\u010Dnici");
		contentPane.add(lblZvucnici);
		
		cbSpeakers = new JComboBox<Mixer.Info>();
		cbSpeakers.setMaximumSize(new Dimension(32767, 20));
		cbSpeakers.setAlignmentX(Component.LEFT_ALIGNMENT);
		contentPane.add(cbSpeakers);
		
		verticalStrut = Box.createVerticalStrut(10);
		contentPane.add(verticalStrut);
		
		lblPreslusavanje = new JLabel("Izlaz za preslu\u0161avanje");
		contentPane.add(lblPreslusavanje);
		
		cbPreslusavanje = new JComboBox<Mixer.Info>();
		cbPreslusavanje.setMaximumSize(new Dimension(32767, 20));
		cbPreslusavanje.setAlignmentX(Component.LEFT_ALIGNMENT);
		contentPane.add(cbPreslusavanje);
		
		verticalStrut_1 = Box.createVerticalStrut(10);
		contentPane.add(verticalStrut_1);
		
		panel = new JPanel();
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		contentPane.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		horizontalGlue = Box.createHorizontalGlue();
		panel.add(horizontalGlue);
		
		btnOk = new JButton("Zapamti");
		btnOk.addActionListener(new BtnOkActionListener());
		panel.add(btnOk);
		
		horizontalStrut = Box.createHorizontalStrut(20);
		panel.add(horizontalStrut);
		
		btnCancel = new JButton("Otka\u017Ei");
		btnCancel.addActionListener(new BtnCancelActionListener());
		panel.add(btnCancel);
		
		horizontalGlue_1 = Box.createHorizontalGlue();
		panel.add(horizontalGlue_1);
		loadMixers();
	}

	private void closeWindow(){
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	private class BtnCancelActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			closeWindow();
		}
	}
	private class BtnOkActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			try{
				PrintWriter writer = new PrintWriter("sound.conf", "UTF-8");
				writer.println(cbSpeakers.getSelectedIndex());
				writer.println(cbPreslusavanje.getSelectedIndex());
				writer.close();
			}catch(Exception e){e.printStackTrace();}
			closeWindow();
		}
	}
}
