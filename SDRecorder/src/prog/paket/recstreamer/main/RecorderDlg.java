package prog.paket.recstreamer.main;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;
import java.io.RandomAccessFile;

import javax.swing.SwingConstants;

import prog.paket.recstreamer.recorder.RecordingThread;

public class RecorderDlg extends JFrame {

	private RecordingThread recThread;

	private JPanel contentPane;
	public JButton btnQuit;
	public JLabel lblTitle;

	private byte loadInputLine() {
		byte index = -1;
		try{
			RandomAccessFile raf = new RandomAccessFile("sound.conf", "rw");
			raf.seek(0L);
			index = raf.readByte();
			raf.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return index;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RecorderDlg frame = new RecorderDlg();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public RecorderDlg() {
		setTitle("Snima\u010D");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 308, 114);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		btnQuit = new JButton("Prekini program");
		contentPane.add(btnQuit, BorderLayout.SOUTH);
		
		lblTitle = new JLabel("Snimanje u toku.");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("Times New Roman", Font.BOLD, 21));
		contentPane.add(lblTitle, BorderLayout.CENTER);
		
		try {
			recThread = new RecordingThread(loadInputLine());
		} catch (LineUnavailableException e) {
			e.printStackTrace(System.out);
		}
	}

}
