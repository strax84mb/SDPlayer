package prog.paket.playlist.generator;

import java.awt.FlowLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.swing.Box;
import javax.swing.JSlider;

import prog.paket.dodaci.ListJItem;
import prog.paket.playlist.generator.struct.PreviewThread;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SoundPreviewDlg extends JDialog {

	private static final long serialVersionUID = 3080904463330087117L;

	private JLabel lblTitle;
	private JProgressBar songProgress;
	private JLabel lblTrajanje;
	public Component verticalStrut_1;
	public JPanel volumePanel;
	public JLabel lblJainaZvuka;
	public Component horizontalStrut;
	public JSlider volumeSlider;

	private ListJItem item;

	private PreviewThread thread;

	private int durr = 0, passed = 0;

	private int getSourceindex(){
		int targetIndex = -1;
		try {
			FileInputStream fis = new FileInputStream("sound.conf");
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader reader = new BufferedReader(isr);
			targetIndex = Integer.parseInt(reader.readLine());
			targetIndex = Integer.parseInt(reader.readLine());
			reader.close();
			isr.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace(System.out);
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
		return targetIndex;
	}

	public void adjustPlayTime(){
		passed++;
		songProgress.setValue(passed * 100 / durr);
		String label = "Trajanje: ", text = String.valueOf(passed % 60);
		if(text.length() == 1) text = "0" + text;
		text = String.valueOf(passed / 60) + ":" + text;
		label += text + " / ";
		text = String.valueOf(durr % 60);
		if(text.length() == 1) text = "0" + text;
		text = String.valueOf(durr / 60) + ":" + text;
		lblTrajanje.setText(label + text);
		songProgress.setValue(passed * 250 / durr);
	}

	/**
	 * Create the dialog.
	 */
	public SoundPreviewDlg(ListJItem item) {
		this.item = item;
		addWindowListener(new ThisWindowListener());
		setResizable(false);
		setPreferredSize(new Dimension(0, 15));
		setMinimumSize(new Dimension(0, 15));
		setMaximumSize(new Dimension(2147483647, 15));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 415, 147);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		{
			JPanel panel = new JPanel();
			panel.setBorder(new EmptyBorder(10, 10, 10, 10));
			getContentPane().add(panel);
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			{
				lblTitle = new JLabel("Title");
				panel.add(lblTitle);
			}
			{
				songProgress = new JProgressBar();
				songProgress.addMouseListener(new SongProgressMouseListener());
				panel.add(songProgress);
				songProgress.setPreferredSize(new Dimension(150, 15));
				songProgress.setMinimumSize(new Dimension(10, 15));
				songProgress.setMaximumSize(new Dimension(32767, 15));
				songProgress.setMaximum(250);
				songProgress.setAlignmentX(Component.LEFT_ALIGNMENT);
			}
			{
				Component verticalStrut = Box.createVerticalStrut(10);
				panel.add(verticalStrut);
			}
			{
				lblTrajanje = new JLabel("Trajanje:");
				panel.add(lblTrajanje);
			}
			{
				verticalStrut_1 = Box.createVerticalStrut(10);
				panel.add(verticalStrut_1);
			}
			{
				volumePanel = new JPanel();
				FlowLayout flowLayout = (FlowLayout) volumePanel.getLayout();
				flowLayout.setHgap(0);
				flowLayout.setAlignment(FlowLayout.LEFT);
				volumePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
				panel.add(volumePanel);
				{
					lblJainaZvuka = new JLabel("Ja\u010Dina zvuka");
					volumePanel.add(lblJainaZvuka);
				}
				{
					horizontalStrut = Box.createHorizontalStrut(20);
					volumePanel.add(horizontalStrut);
				}
				{
					volumeSlider = new JSlider();
					volumeSlider.addChangeListener(new VolumeSliderChangeListener());
					volumeSlider.setPreferredSize(new Dimension(100, 21));
					volumePanel.add(volumeSlider);
				}
			}
		}
		durr = (int)(item.duration / 1000000L);
		passed = -1;
		adjustPlayTime();
		File file = new File(item.fullPath);
		lblTitle.setText(file.getName());
		try {
			thread = new PreviewThread(getSourceindex(), item.fullPath, this);
			thread.startPlayFrom(0);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		thread.start();
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((size.width/2)-(getSize().width/2), (size.height/2)-(getSize().height/2));
		setVisible(true);
	}
	private class VolumeSliderChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			if(volumeSlider.getModel().getValue() == 0)
				thread.setGain(0.0);
			else{
				double temp = volumeSlider.getModel().getValue();
				temp = (-36.0 + (46.0 * temp / 100.0)) / 20.0;
				thread.setGain(Math.pow(10, temp));
			}
		}
	}
	private class ThisWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent arg0) {
			thread.endThread();
		}
	}
	private class SongProgressMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent me) {
			double scale = ((double)me.getPoint().x) / songProgress.getSize().width;
			try {
				passed = (int)(durr * scale);
				songProgress.setValue((int)(250 * scale));
				thread.startPlayFrom((int)(item.frameCount * scale));
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}
	}
}
