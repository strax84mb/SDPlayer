package prog.paket.playlist.generator;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.UIManager;

import java.awt.Font;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.Dimension;
import javax.swing.BoxLayout;

import prog.paket.automation.AutoPlayThread;
import prog.paket.automation.ReaderLoader;
import prog.paket.baza.BazaProzor;
import prog.paket.dodaci.ListJItem;
import prog.paket.dodaci.ListJSection;
import prog.paket.dodaci.PLTableModel;
import prog.paket.mp3.SD_MP3_Player;
import prog.paket.report.ReportDlg;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import prog.paket.dodaci.WuMetar;
import prog.paket.forme.emisije.PLGeneratorWindow;
import prog.paket.forme.pregled.MonthlyPLDialog;
import prog.paket.forme.reklame.ReklameOglasiDlg;

import java.awt.Color;
import javax.swing.JProgressBar;

import javazoom.spi.mpeg.sampled.convert.MpegFormatConversionProvider;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Box;
import java.awt.event.WindowFocusListener;

public class PlayerWin extends JFrame {

	private static final long serialVersionUID = 4649136397330241025L;

	private Icon playedIcon = new ImageIcon(PlayerWin.class.getResource("/prog/paket/dodaci/play-pressed.png"));
	private Icon notPlayedIcon = new ImageIcon(PlayerWin.class.getResource("/prog/paket/dodaci/play.png"));
	private Icon pausedIcon = new ImageIcon(PlayerWin.class.getResource("/prog/paket/dodaci/pause-pressed.png"));
	private Icon notPausedIcon = new ImageIcon(PlayerWin.class.getResource("/prog/paket/dodaci/pause.png"));
	private Icon micIcon = new ImageIcon(PlayerWin.class.getResource("/prog/paket/dodaci/mic.png"));
	private Icon micActiveIcon = new ImageIcon(PlayerWin.class.getResource("/prog/paket/dodaci/mic-pressed.png"));

	public PLGeneratorWindow plGenWin = new PLGeneratorWindow();
	public MonthlyPLDialog monthlyPLDlg = new MonthlyPLDialog();
	public WaitInfoDialog waitDlg = new WaitInfoDialog();
	public PlaylistDialog playList = new PlaylistDialog();
	public DzinglDijalog dzinglDlg = new DzinglDijalog();
	public ReklameOglasiDlg reklOglDlg = null;
	private ErrorInfoDialog errDlg = new ErrorInfoDialog();
	public BazaProzor bazaProzor = null;
	public SD_MP3_Player player;
	public ReaderLoader loader;
	public AutoPlayThread autoPlay;

	public boolean playerPlaying = false;
	public boolean playerPaused = false;
	public boolean autoPlayOn = false;

	private JPanel contentPane;
	public JPanel panel;
	public JPanel sidePanel;
	public JTextField songNameField;
	public JLabel lblTimePassed;
	public JPanel panel_1;
	public JButton btnStop;
	public JButton btnNext;
	public JButton btnNewButton;
	public JButton btnPrikaziIstoriju;
	public JButton btnPrikaziDzinglove;
	public JButton btnGenerator;
	public JButton btnPlay;
	public JButton btnPause;

	public long mins, secs, durr, timePassed;
	// Sluzi kao zadrska padanja
	//private int wmHoldOffFall = 0;
	public JButton btnMic;
	public JSlider volumeSlider;
	public WuMetar wuMetar;

	// Automatski plejbek
	public ListJSection currSection = null;
	public ListJSection nextSection = null;
	public ListJSection nextFirstCatSec = null;

	private static PlayerWin singleton;
	public JButton btnPregledGenerisanePL;
	public JButton btnAutoPlay;
	public JProgressBar progressBar;

	public MpegAudioFileReader mp3fr = new MpegAudioFileReader();
	public MpegFormatConversionProvider mp3formater = new MpegFormatConversionProvider();
	public JButton btnBazaPesama;
	public Component verticalStrut;
	public JButton btnReklame;

	public static PlayerWin getInstance(){
		return singleton;
	}

	public static ErrorInfoDialog getErrDlg(){
		return singleton.errDlg;
	}

	public static PLTableModel getPlayListModel(){
		return singleton.playList.playListPane.getPlayList().getTableModel();
	}

	public int secondsToEnd(){
		return (int)(durr - ((mins * 60) + secs));
	}

	public void playSFX(String dzingl){
		player.loadDzingl(dzingl);
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(new NimbusLookAndFeel());
					PlayerWin frame = new PlayerWin();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void adjustWM(int val){
		val = (int)Math.abs(Math.sqrt((double)val / Short.MAX_VALUE) * 100);
		wuMetar.setValue(val);
	}

	public void setWholeTime(long time, String name){
		durr = time / 1000000;
		mins = time / 60000000L;
		secs = (time / 1000000) % 60;
		lblTimePassed.setText("0:00 / " + String.valueOf(mins) + ":" + 
				((secs < 10)?"0" + String.valueOf(secs):String.valueOf(secs)));
		songNameField.setText(name);
	}

	public void adjustTimePassed(int minutes, int seconds){
		timePassed = (minutes * 60) + seconds;
		String temp = lblTimePassed.getText();
		temp = temp.substring(temp.indexOf('/'));
		lblTimePassed.setText(String.valueOf(minutes) + ":" + 
				((seconds < 10)?"0" + String.valueOf(seconds):String.valueOf(seconds)) + " " + temp);
		progressBar.setValue(((minutes * 60) + seconds) * 250 / ((int)durr));
		/*if(autoPlayOn && (nextFirstCatSec != null) && (secondsToEnd() > 15) && 
				(System.currentTimeMillis() > nextFirstCatSec.startTime)){
			try{
				PrintWriter writer = new PrintWriter("log.txt", "UTF-8");
				Date date = new Date();
				writer.println("LOG: Time is " + date.toString());
				writer.println("\tMoving onto first category section: " + nextFirstCatSec.catName);
				date.setTime(nextFirstCatSec.startTime);
				writer.println("\tstarting in " + date.toString());
				writer.close();
			}catch(Exception e){
				e.printStackTrace(System.out);
			}
			PLTableModel model = getPlayListModel();
			int i = 0;
			ListJItem item = model.getItemAt(0);
			while(item.isItem() && (item != nextFirstCatSec) 
					&& (i < model.getRowCount()))
				i++;
			if(i < model.getRowCount()){
				i--;
				for(;i>0;i--)
					model.removeRow(0);
				
				btnNext.doClick();
			}
		}*/
	}

	public void setMicActiveIcon(){
		btnMic.setIcon(micActiveIcon);
		btnMic.setToolTipText("Isključi mikrofon");
	}

	public void setMicPassiveIcon(){
		btnMic.setIcon(micIcon);
		btnMic.setToolTipText("Uključi mikrofon");
	}

	/**
	 * Create the frame.
	 */
	public PlayerWin() {
		addWindowFocusListener(new ThisWindowFocusListener());
		singleton = this;
		addWindowListener(new ThisWindowListener());
		setLocationByPlatform(true);
		setTitle("SD Player 1.0");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 470, 245);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		
		songNameField = new JTextField();
		songNameField.setBackground(new Color(214, 217, 223));
		songNameField.setBorder(new EmptyBorder(3, 3, 3, 3));
		songNameField.setEditable(false);
		songNameField.setColumns(10);
		
		lblTimePassed = new JLabel("00:00 / 00:00");
		lblTimePassed.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		panel_1 = new JPanel();
		
		wuMetar = new WuMetar();
		wuMetar.setValue(100);
		wuMetar.setSize(new Dimension(10, 50));
		wuMetar.setMaximumSize(new Dimension(10, 50));
		wuMetar.setMinimumSize(new Dimension(10, 50));
		wuMetar.setPreferredSize(new Dimension(10, 50));
		wuMetar.setBackground(Color.BLACK);
		
		progressBar = new JProgressBar();
		progressBar.addMouseListener(new ProgressBarMouseListener());
		progressBar.setValue(100);
		progressBar.setForeground(Color.CYAN);
		progressBar.setStringPainted(true);
		progressBar.setString("");
		progressBar.setSize(new Dimension(150, 25));
		progressBar.setMinimumSize(new Dimension(150, 25));
		progressBar.setMaximumSize(new Dimension(150, 25));
		progressBar.setPreferredSize(new Dimension(150, 25));
		progressBar.setMaximum(250);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(songNameField, GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
								.addComponent(lblTimePassed)
								.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 257, GroupLayout.PREFERRED_SIZE)))
						.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(wuMetar, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
						.addComponent(wuMetar, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(songNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblTimePassed)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
							.addGap(15)
							.addComponent(progressBar, 0, 15, Short.MAX_VALUE)))
					.addContainerGap(47, Short.MAX_VALUE))
		);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
		
		btnPlay = new JButton("");
		btnPlay.addActionListener(new BtnPlayActionListener());
		btnPlay.setIcon(notPlayedIcon);
		btnPlay.setMaximumSize(new Dimension(30, 30));
		btnPlay.setMinimumSize(new Dimension(30, 30));
		btnPlay.setPreferredSize(new Dimension(30, 30));
		btnPlay.setSize(new Dimension(30, 30));
		panel_1.add(btnPlay);
		
		btnPause = new JButton("");
		btnPause.addActionListener(new BtnPauseActionListener());
		btnPause.setIcon(notPausedIcon);
		btnPause.setSize(new Dimension(30, 30));
		btnPause.setPreferredSize(new Dimension(30, 30));
		btnPause.setMinimumSize(new Dimension(30, 30));
		btnPause.setMaximumSize(new Dimension(30, 30));
		panel_1.add(btnPause);
		
		btnStop = new JButton("");
		btnStop.addActionListener(new BtnStopActionListener());
		btnStop.setIcon(new ImageIcon(PlayerWin.class.getResource("/prog/paket/dodaci/stop.png")));
		btnStop.setMaximumSize(new Dimension(30, 30));
		btnStop.setMinimumSize(new Dimension(30, 30));
		btnStop.setPreferredSize(new Dimension(30, 30));
		btnStop.setSize(new Dimension(30, 30));
		panel_1.add(btnStop);
		
		btnNext = new JButton("");
		btnNext.addActionListener(new BtnNextActionListener());
		btnNext.setSize(new Dimension(30, 30));
		btnNext.setPreferredSize(new Dimension(30, 30));
		btnNext.setMinimumSize(new Dimension(30, 30));
		btnNext.setMaximumSize(new Dimension(30, 30));
		btnNext.setIcon(new ImageIcon(PlayerWin.class.getResource("/prog/paket/dodaci/next.png")));
		panel_1.add(btnNext);
		
		volumeSlider = new JSlider(0, 100, 50);
		volumeSlider.setSize(new Dimension(75, 25));
		volumeSlider.setMinorTickSpacing(1);
		volumeSlider.setMajorTickSpacing(2);
		volumeSlider.setValue(50);
		volumeSlider.addChangeListener(new SliderChangeListener());
		volumeSlider.setPreferredSize(new Dimension(75, 25));
		volumeSlider.setMinimumSize(new Dimension(75, 25));
		volumeSlider.setMaximumSize(new Dimension(75, 25));
		panel_1.add(volumeSlider);
		
		btnMic = new JButton("");
		btnMic.setEnabled(false);
		btnMic.setMaximumSize(new Dimension(30, 30));
		btnMic.setMinimumSize(new Dimension(30, 30));
		btnMic.setPreferredSize(new Dimension(30, 30));
		btnMic.setSize(new Dimension(30, 30));
		btnMic.setIcon(micIcon);
		btnMic.setToolTipText("Uključi mikrofon");
		panel_1.add(btnMic);
		
		btnAutoPlay = new JButton("");
		btnAutoPlay.addActionListener(new BtnAutoPlayActionListener());
		btnAutoPlay.setPreferredSize(new Dimension(30, 30));
		btnAutoPlay.setMinimumSize(new Dimension(30, 30));
		btnAutoPlay.setMaximumSize(new Dimension(30, 30));
		btnAutoPlay.setSize(new Dimension(30, 30));
		btnAutoPlay.setIcon(new ImageIcon(PlayerWin.class.getResource("/prog/paket/dodaci/auto.png")));
		btnAutoPlay.setToolTipText("Uključi automatizovani plejbek");
		panel_1.add(btnAutoPlay);
		panel.setLayout(gl_panel);
		
		sidePanel = new JPanel();
		contentPane.add(sidePanel, BorderLayout.EAST);
		sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
		
		btnNewButton = new JButton("Plej lista");
		btnNewButton.addActionListener(new BtnNewButtonActionListener());
		btnNewButton.setSize(new Dimension(123, 23));
		btnNewButton.setPreferredSize(new Dimension(155, 23));
		btnNewButton.setMinimumSize(new Dimension(155, 23));
		btnNewButton.setMaximumSize(new Dimension(155, 23));
		sidePanel.add(btnNewButton);
		
		btnPrikaziIstoriju = new JButton("Prikaži izveštaje");
		btnPrikaziIstoriju.addActionListener(new BtnPrikaziIstorijuActionListener());
		btnPrikaziIstoriju.setPreferredSize(new Dimension(155, 23));
		btnPrikaziIstoriju.setMinimumSize(new Dimension(155, 23));
		btnPrikaziIstoriju.setMaximumSize(new Dimension(155, 23));
		sidePanel.add(btnPrikaziIstoriju);
		
		btnPrikaziDzinglove = new JButton("Maske i ID džinglovi");
		btnPrikaziDzinglove.addActionListener(new BtnPrikaziDzingloveActionListener());
		btnPrikaziDzinglove.setPreferredSize(new Dimension(155, 23));
		btnPrikaziDzinglove.setMinimumSize(new Dimension(155, 23));
		btnPrikaziDzinglove.setMaximumSize(new Dimension(155, 23));
		sidePanel.add(btnPrikaziDzinglove);
		
		btnGenerator = new JButton("Emisije i pesme");
		btnGenerator.setMaximumSize(new Dimension(155, 23));
		btnGenerator.setMinimumSize(new Dimension(155, 23));
		btnGenerator.setPreferredSize(new Dimension(155, 23));
		btnGenerator.addActionListener(new BtnGeneratorActionListener());
		
		btnReklame = new JButton("Reklame i oglasi");
		btnReklame.addActionListener(new BtnReklameActionListener());
		btnReklame.setMaximumSize(new Dimension(155, 23));
		btnReklame.setMinimumSize(new Dimension(155, 23));
		btnReklame.setPreferredSize(new Dimension(155, 23));
		sidePanel.add(btnReklame);
		sidePanel.add(btnGenerator);
		
		btnPregledGenerisanePL = new JButton("Pregled generisane PL");
		btnPregledGenerisanePL.addActionListener(new BtnPregledGenerisanePLActionListener());
		btnPregledGenerisanePL.setMaximumSize(new Dimension(155, 23));
		btnPregledGenerisanePL.setMinimumSize(new Dimension(155, 23));
		btnPregledGenerisanePL.setPreferredSize(new Dimension(155, 23));
		sidePanel.add(btnPregledGenerisanePL);
		
		verticalStrut = Box.createVerticalStrut(10);
		sidePanel.add(verticalStrut);
		
		btnBazaPesama = new JButton("Baza pesama");
		btnBazaPesama.addActionListener(new BtnBazaPesamaActionListener());
		btnBazaPesama.setMaximumSize(new Dimension(155, 23));
		btnBazaPesama.setMinimumSize(new Dimension(155, 23));
		btnBazaPesama.setPreferredSize(new Dimension(155, 23));
		sidePanel.add(btnBazaPesama);
		
		if(reklOglDlg == null)
			reklOglDlg = new ReklameOglasiDlg();
		
		try {
			FileInputStream fis = new FileInputStream("sound.conf");
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader reader = new BufferedReader(isr);
			int sourceIndex = Integer.parseInt(reader.readLine());
			int targetIndex = Integer.parseInt(reader.readLine());
			reader.close();
			isr.close();
			fis.close();
			autoPlay = new AutoPlayThread();
			loader = new ReaderLoader();
			player = new SD_MP3_Player(this, sourceIndex, targetIndex, loader);
			if(Thread.currentThread().getPriority() < Thread.MAX_PRIORITY)
				player.setPriority(Thread.currentThread().getPriority()+1);
			autoPlay.start();
			loader.setPlayer(player);
			loader.start();
			player.start();
		} catch (Exception e) {
			e.printStackTrace();
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
		if(volumeSlider.getModel().getValue() == 0)
			player.setMainGain(0.0);
		else{
			double temp = volumeSlider.getModel().getValue();
			temp = (-36.0 + (46.0 * temp / 100.0)) / 20.0;
			player.setMainGain(Math.pow(10, temp));
		}
	}
	private class BtnPlayActionListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			if(!playerPlaying){
				ListJItem item = playList.getNext();
				if(item != null){
					setWholeTime(item.duration, item.fileName);
					btnPlay.setIcon(playedIcon);
					player.setCommand(4, item);
					playerPlaying = true;
				}
			}else if(playerPaused){
				btnPause.setIcon(notPausedIcon);
				btnPlay.setIcon(playedIcon);
				player.setCommand(8, null);
				playerPaused = false;
			}
		}
	}
	private class BtnPauseActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(playerPlaying){
				if(!playerPaused){
					btnPause.setIcon(pausedIcon);
					btnPlay.setIcon(notPlayedIcon);
					player.setCommand(5, null);
					playerPaused = true;
				}else{
					btnPause.setIcon(notPausedIcon);
					btnPlay.setIcon(playedIcon);
					player.setCommand(8, null);
					playerPaused = false;
				}
			}
		}
	}
	private class BtnGeneratorActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			plGenWin.loadCategoryPool();
			plGenWin.setVisible(true);
		}
	}
	private class BtnNewButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			Point point = getLocation();
			playList.setSize(getSize().width, playList.getSize().height);
			playList.setLocation(point.x, point.y + getSize().height + 1);
			playList.setVisible(true);
		}
	}
	private class ThisWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent arg0) {
			try{
				player.setCommand(7, null);
				loader.close();
				autoPlay.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	private class BtnStopActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			player.setCommand(6, null);
			playerPlaying = false;
			btnPlay.setIcon(notPlayedIcon);
			playerPaused = false;
			btnPause.setIcon(notPausedIcon);
			lblTimePassed.setText("0:00 / 0:00");
		}
	}
	private class BtnNextActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			if(playerPaused){
				// TODO Ubaci "next" kod sa crossFade
			}else{
				ListJItem item = playList.getNext();
				if(item != null){
					btnPlay.setIcon(playedIcon);
					player.setCommand(4, item);
					playerPlaying = true;
				}
			}
		}
	}
	private class SliderChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent event) {
			//if(!volumeSlider.getValueIsAdjusting()){
			/*
				if(volumeSlider.getModel().getValue() == 0)
					player.setMainGain(0.0);
				else{
					double temp = volumeSlider.getModel().getValue();
					temp = (-36.0 + (46.0 * temp / 100.0)) / 20.0;
					player.setMainGain(Math.pow(10, temp));
				}
				*/
			//}
		}
	}
	private class BtnPrikaziDzingloveActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			Point point = getLocation();
			dzinglDlg.setLocation(point.x + getSize().width + 1, point.y);
			dzinglDlg.setVisible(true);
		}
	}
	private class BtnPregledGenerisanePLActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			if(monthlyPLDlg.tfPocDatum.getText().isEmpty()){
				Calendar cal = new GregorianCalendar();
				String str = String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) + "." + 
						String.valueOf(cal.get(Calendar.MONTH) + 1) + "." + 
						String.valueOf(cal.get(Calendar.YEAR));
				monthlyPLDlg.tfPocDatum.setText(str);
				str = String.valueOf(cal.get(Calendar.MINUTE));
				if(str.length() == 1) str = "0" + str;
				str = String.valueOf(cal.get(Calendar.HOUR_OF_DAY)) + ":" + str;
				if(str.length() == 4) str = "0" + str;
				monthlyPLDlg.tfPocVreme.setText(str);
				cal.setTimeInMillis(cal.getTimeInMillis() + 259200000L);
				str = String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) + "." + 
						String.valueOf(cal.get(Calendar.MONTH) + 1) + "." + 
						String.valueOf(cal.get(Calendar.YEAR));
				monthlyPLDlg.tfKrajDatum.setText(str);
				str = String.valueOf(cal.get(Calendar.MINUTE));
				if(str.length() == 1) str = "0" + str;
				str = String.valueOf(cal.get(Calendar.HOUR_OF_DAY)) + ":" + str;
				if(str.length() == 4) str = "0" + str;
				monthlyPLDlg.tfKrajVreme.setText(str);
			}
			monthlyPLDlg.loadPlayList();
			monthlyPLDlg.setVisible(true);
		}
	}
	private class ProgressBarMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent me) {
			if(playerPlaying && !playerPaused){
				int i = (int)(durr * ((double)me.getPoint().x) / progressBar.getSize().width);
				if(i <= 5) i = -1;
				player.setSongSeekPos(i);
				player.setCommand(1, null);
				//progressBar.setValue(i);
			}
		}
	}
	private class BtnAutoPlayActionListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			if(autoPlayOn){
				autoPlayOn = false;
				btnAutoPlay.setBackground(Color.LIGHT_GRAY);
			}else{
				autoPlayOn = true;
				btnAutoPlay.setBackground(Color.CYAN);
				autoPlay.initiatePlaylist();
				btnPlay.doClick();
			}
		}
	}
	private class BtnBazaPesamaActionListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			bazaProzor = new BazaProzor();
			bazaProzor.setLocation(getLocation().x, getLocation().y + 5 + getSize().height);
			bazaProzor.setVisible(true);
		}
	}
	private class ThisWindowFocusListener implements WindowFocusListener {
		public void windowGainedFocus(WindowEvent event) {
			/*
			if(playList.isVisible()) playList.setVisible(true);
			if(dzinglDlg.isVisible()) dzinglDlg.setVisible(true);
			if(plGenWin.isVisible()) plGenWin.setVisible(true);
			if(monthlyPLDlg.isVisible()) monthlyPLDlg.setVisible(true);
			if((bazaProzor != null) && bazaProzor.isVisible()) bazaProzor.setVisible(true);
			if(waitDlg.isVisible()) waitDlg.setVisible(true);
			if(errDlg.isVisible()) errDlg.setVisible(true);
			*/
		}
		public void windowLostFocus(WindowEvent arg0) {
		}
	}
	private class BtnReklameActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			reklOglDlg.disableControls();
			reklOglDlg.setLocation(getLocationOnScreen());
			reklOglDlg.loadStructure();
			reklOglDlg.setVisible(true);
		}
	}
	private class BtnPrikaziIstorijuActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			ReportDlg dlg = new ReportDlg();
			dlg.setVisible(true);
		}
	}
}
