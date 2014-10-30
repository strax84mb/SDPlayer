package prog.paket.report;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.table.DefaultTableModel;

import prog.paket.playlist.generator.ID3TagsDlg;
import prog.paket.playlist.generator.PlayerWin;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import javax.swing.JSeparator;

import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ReportDlg extends JDialog {

	private static final long serialVersionUID = -8838609295240964846L;

	public JTextField tfMesec;
	public ReportTable table;

	private String month = null;
	private JButton btnMesec;

	private String[] readFile(String fileName){
		String ret[] = new String[3];
		try {
			File file = new File(fileName);
			MP3File mp3file = new MP3File(file, MP3File.LOAD_ALL);
			AbstractID3v2Tag v2tag = mp3file.getID3v2Tag();
			if(v2tag != null){
				ret[0] = v2tag.getFirst(FieldKey.ARTIST);
				ret[1] = v2tag.getFirst(FieldKey.TITLE);
				ret[2] = v2tag.getFirst(FieldKey.COMMENT);
				return ret;
			}
			ID3v1Tag v1tag = mp3file.getID3v1Tag();
			if(v1tag != null){
				ret[0] = v1tag.getFirst(FieldKey.ARTIST);
				ret[1] = v1tag.getFirst(FieldKey.TITLE);
				ret[2] = v1tag.getFirst(FieldKey.COMMENT);
				return ret;
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return null;
	}

	private void refreshRow(int index){
		DefaultTableModel model = table.getTableModel();
		try {
			File file = ((Song)model.getValueAt(index, 1)).getFile();
			MP3File mp3file = new MP3File(file, MP3File.LOAD_ALL);
			AbstractID3v2Tag v2tag = mp3file.getID3v2Tag();
			if(v2tag != null){
				model.setValueAt(v2tag.getFirst(FieldKey.ARTIST), index, 3);
				model.setValueAt(v2tag.getFirst(FieldKey.TITLE), index, 4);
				model.setValueAt(v2tag.getFirst(FieldKey.COMMENT), index, 5);
				model.fireTableDataChanged();
				return;
			}
			ID3v1Tag v1tag = mp3file.getID3v1Tag();
			if(v1tag != null){
				model.setValueAt(v1tag.getFirst(FieldKey.ARTIST), index, 3);
				model.setValueAt(v1tag.getFirst(FieldKey.TITLE), index, 4);
				model.setValueAt(v1tag.getFirst(FieldKey.COMMENT), index, 5);
				model.fireTableDataChanged();
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	/**
	 * Create the dialog.
	 */
	public ReportDlg() {
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 790, 402);
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel upperPanel = new JPanel();
			getContentPane().add(upperPanel, BorderLayout.NORTH);
			{
				JLabel lblMesec = new JLabel("<html><center>Mesec u formatu godina-mesec<br/>(npr. 2014-05)</center></html>");
				lblMesec.setHorizontalAlignment(SwingConstants.CENTER);
				upperPanel.add(lblMesec);
			}
			{
				tfMesec = new JTextField();
				tfMesec.addKeyListener(new TfMesecKeyListener());
				upperPanel.add(tfMesec);
				tfMesec.setColumns(7);
			}
			{
				btnMesec = new JButton("Prika\u017Ei");
				btnMesec.addActionListener(new BtnMesecActionListener());
				upperPanel.add(btnMesec);
			}
		}
		{
			JPanel rightPanel = new JPanel();
			getContentPane().add(rightPanel, BorderLayout.EAST);
			rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
			{
				JLabel lblGenerisi = new JLabel("<html><center>Generi\u0161i<br/>izve\u0161taj za:</center></html>");
				lblGenerisi.setHorizontalAlignment(SwingConstants.CENTER);
				lblGenerisi.setAlignmentX(Component.CENTER_ALIGNMENT);
				rightPanel.add(lblGenerisi);
			}
			{
				JButton btnSOKOJ = new JButton("SOKOJ");
				btnSOKOJ.addActionListener(new BtnSOKOJActionListener());
				btnSOKOJ.setAlignmentX(Component.CENTER_ALIGNMENT);
				rightPanel.add(btnSOKOJ);
			}
			{
				JButton btnOFPS = new JButton("OFPS");
				btnOFPS.addActionListener(new BtnOFPSActionListener());
				btnOFPS.setAlignmentX(Component.CENTER_ALIGNMENT);
				rightPanel.add(btnOFPS);
			}
			{
				Component verticalStrut = Box.createVerticalStrut(5);
				rightPanel.add(verticalStrut);
			}
			{
				JSeparator separator = new JSeparator();
				separator.setMaximumSize(new Dimension(32767, 2));
				rightPanel.add(separator);
			}
			{
				Component verticalStrut = Box.createVerticalStrut(5);
				rightPanel.add(verticalStrut);
			}
			{
				JLabel lblIzmeni = new JLabel("Izmeni:");
				lblIzmeni.setAlignmentX(Component.CENTER_ALIGNMENT);
				rightPanel.add(lblIzmeni);
			}
			{
				JButton btnID3 = new JButton("ID3 Oznaku");
				btnID3.addActionListener(new BtnID3ActionListener());
				btnID3.setAlignmentX(Component.CENTER_ALIGNMENT);
				rightPanel.add(btnID3);
			}
			{
				Component verticalGlue = Box.createVerticalGlue();
				rightPanel.add(verticalGlue);
			}
			{
				JButton btnClose = new JButton("Zatvori");
				btnClose.addActionListener(new BtnCloseActionListener());
				btnClose.setAlignmentX(Component.CENTER_ALIGNMENT);
				rightPanel.add(btnClose);
			}
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			getContentPane().add(scrollPane, BorderLayout.CENTER);
			{
				table = new ReportTable();
				scrollPane.setViewportView(table);
			}
		}
	}

	private class BtnMesecActionListener implements ActionListener {
		private int checkIfSongEntered(String path){
			DefaultTableModel model = table.getTableModel();
			Song song;
			for(int i=0,len=model.getRowCount();i<len;i++){
				song = (Song)model.getValueAt(i, 1);
				if(song.getFile().getAbsolutePath().equals(path))
					return i;
			}
			return -1;
		}
		private String parseMonthString(String str){
			String temp = str.replace('.', '-').replace(',', '-'), ret;;
			int index = temp.indexOf('-'), count;
			count = temp.length() - index - 1;
			if(count == 4){
				ret = temp.substring(0, index);
				// Ako broj meseca nije dvocifren;
				if(ret.length() == 1) ret = "0" + ret;
				temp = temp.substring(index + 1) + "-" + ret;
			}else{
				ret = temp.substring(index + 1);
				if(ret.length() == 1){
					ret = "0" + ret;
					temp = temp.substring(0, index + 1) + ret;
				}
			}
			return temp;
		}
		public void actionPerformed(ActionEvent event) {
			String temp = null;
			File file = null;
			try{
				temp = parseMonthString(tfMesec.getText().trim());
				file = new File("reports/" + temp + ".report");
				if(!file.exists()) throw new Exception();
			}catch(Exception e){
				e.printStackTrace(System.out);
				PlayerWin.getErrDlg().showError("Pogrešno unešen mesec koji treba prikazati.<br/>Dozvoljeni formati su:<br/>dan.mesec<br/>dan,mesec<br/>dan-mesec<br/>mesec.dan<br/>mesec,dan<br/>mesec-dan");
				return;
			}
			month = temp;
			try{
				RandomAccessFile raf = new RandomAccessFile(file, "r");
				long startTime;
				boolean reportIt;
				int durInSec;
				String filePath;
				DefaultTableModel model = table.getTableModel();
				model.setRowCount(0);
				raf.seek(0);
				Object[] row = null;
				String[] id3;
				int rowIndex;
				try{
					while(true){
						startTime = raf.readLong();
						reportIt = raf.readBoolean();
						durInSec = raf.readInt();
						filePath = raf.readUTF();
						row = new Object[6];
						row[0] = table.getStartString(startTime);
						row[1] = new Song(new File(filePath), startTime, durInSec);
						row[2] = reportIt;
						rowIndex = checkIfSongEntered(filePath);
						if(rowIndex == -1){
							id3 = readFile(filePath);
							if(id3 == null){
								row[3] = "";
								row[4] = "";
								row[5] = "";
							}else{
								row[3] = id3[0];
								row[4] = id3[1];
								row[5] = id3[2];
							}
						}else{
							row[3] = model.getValueAt(rowIndex, 3);
							row[4] = model.getValueAt(rowIndex, 4);
							row[5] = model.getValueAt(rowIndex, 5);
						}
						model.addRow(row);
					}
				}catch(EOFException e){
					System.out.println("Reached end of file: reports/" + temp + ".report");
				}
				raf.close();
			}catch(Exception e){
				e.printStackTrace(System.out);
				PlayerWin.getErrDlg().showError("Desila se greška prilikom čitanja izveštaja.");
			}
		}
	}
	private class BtnSOKOJActionListener implements ActionListener {
		private String parseTime(int time){
			String ret = String.valueOf(time % 60);
			if(ret.length() == 1) ret = "0" + ret;
			time /= 60;
			ret = String.valueOf(time % 60) + ":" + ret;
			if(ret.length() == 4) ret = "0" + ret;
			ret = String.valueOf(time / 60) + ":" + ret;
			if(ret.length() == 7) ret = "0" + ret;
			return ret;
		}
		public void actionPerformed(ActionEvent event) {
			try{
				File file = new File(month + "-SOKOJ.txt");
				if(!file.exists())
					file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file, false);
				PrintWriter writer = new PrintWriter(fos);
				writer.println("Datum\tVreme emitovanja\tIzvođač\tNaziv dela\tTrajanje dela\tNapomena");
				DefaultTableModel model = table.getTableModel();
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
				String line, name;
				int dashPos;
				Boolean reportIt;
				Song song;
				Calendar cal = new GregorianCalendar();
				for(int i=0,len=model.getRowCount();i<len;i++){
					reportIt = (Boolean)model.getValueAt(i, 2);
					if(reportIt.booleanValue()){
						song = (Song)model.getValueAt(i, 1);
						cal.setTimeInMillis(song.getStartTime());
						line = dateFormat.format(cal.getTime()) + "\t";
						name = song.toString();
						dashPos = name.indexOf('-');
						line += timeFormat.format(cal.getTime()) + "\t" + 
								(String)model.getValueAt(i, 3) + "\t" + 
								(String)model.getValueAt(i, 4) + "\t" + 
								parseTime(song.getDurationInSeconds());
						writer.println(line);
					}
				}
				writer.close();
				fos.close();
				PlayerWin.getErrDlg().showError("Snimanje izveštaja za SOKOJ za mesec " + 
						month + "<br/>je uspešno obavljeno.", "Obaveštenje");
			}catch(Exception e){
				e.printStackTrace(System.out);
			}
		}
	}
	private void closeThis(){
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
	private class BtnCloseActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			closeThis();
		}
	}
	private class BtnOFPSActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try{
				File file = new File("OFPS.xls");
				Workbook workbook = Workbook.getWorkbook(file);
				WritableWorkbook copy = Workbook.createWorkbook(new File(month + "-OFPS.xls"), workbook);
				WritableSheet sheet = copy.getSheet(0);
				Label artistCell, songCell;
				WritableCellFormat dateFormat, timeFormat, durationFormat;
				dateFormat = new WritableCellFormat(new DateFormat("dd/MM/yyyy"));
				dateFormat.setAlignment(Alignment.LEFT);
				//System.out.println(dateFormat.getPattern().toString());
				DateFormat tf = new DateFormat("HH:mm:ss");
				tf.getDateFormat().setTimeZone(TimeZone.getTimeZone("GMT+2:00"));
				timeFormat = new WritableCellFormat(tf);
				timeFormat.setAlignment(Alignment.LEFT);
				durationFormat = new WritableCellFormat(new DateFormat("HH:mm:ss"));
				durationFormat.setAlignment(Alignment.LEFT);
				DateTime dateCell, timeCell, durationCell;
				DefaultTableModel model = table.getTableModel();
				String name;
				Boolean reportIt;
				Song song;
				Calendar cal = new GregorianCalendar();
				Date time = null;
				int row = 1, hrs, mins, secs;
				for(int i=0,len=model.getRowCount();i<len;i++){
					reportIt = (Boolean)model.getValueAt(i, 2);
					if(reportIt.booleanValue()){
						song = (Song)model.getValueAt(i, 1);
						cal.setTimeInMillis(song.getStartTime());
						cal.set(Calendar.MILLISECOND, 0);
						time = cal.getTime();
						cal.set(Calendar.HOUR_OF_DAY, 0);
						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.SECOND, 0);
						dateCell = new DateTime(0, row, cal.getTime());
						dateCell.setCellFormat(dateFormat);
						sheet.addCell(dateCell);
						timeCell = new DateTime(1, row, time, timeFormat, true);
						sheet.addCell(timeCell);
						name = song.toString();
						artistCell = new Label(2, row, (String)model.getValueAt(i, 3));
						sheet.addCell(artistCell);
						songCell = new Label(3, row, (String)model.getValueAt(i, 4));
						sheet.addCell(songCell);
						secs = song.getDurationInSeconds();
						hrs = secs / 3600;
						secs %= 3600;
						mins = secs / 60;
						secs %= 60;
						cal.set(Calendar.HOUR_OF_DAY, hrs);
						cal.set(Calendar.MINUTE, mins);
						cal.set(Calendar.SECOND, secs);
						cal.set(Calendar.MILLISECOND, 0);
						cal.setTimeInMillis(cal.getTimeInMillis() + 7200000L);
						durationCell = new DateTime(4, row, cal.getTime(), timeFormat, true);
						sheet.addCell(durationCell);
						row++;
					}
				}
				//CellView cell = new CellView();
				//cell.setFormat(new WritableCellFormat(new DateFormat("dd/MM/yyyy")));
				//sheet.setColumnView(0, cell);
				copy.write();
				copy.close();
				workbook.close();
				PlayerWin.getErrDlg().showError("Snimanje izveštaja za OFPS za mesec " + 
						month + "<br/>je uspešno obavljeno.", "Obaveštenje");
			}catch(Exception e){
				e.printStackTrace(System.out);
			}
		}
	}
	private class BtnID3ActionListener implements ActionListener {
		public void changeInstances(String path, String strs[], int row){
			DefaultTableModel model = table.getTableModel();
			Song song;
			for(int i=0,len=model.getRowCount();i<len;i++){
				if(i == row) continue;
				song = (Song)model.getValueAt(i, 1);
				if(song.getFile().getAbsolutePath().equals(path)){
					model.setValueAt(strs[0], i, 3);
					model.setValueAt(strs[1], i, 4);
					model.setValueAt(strs[2], i, 5);
				}
			}
		}
		public void actionPerformed(ActionEvent event) {
			int row = table.getSelectedRow();
			if(row == -1) return;
			Song song = (Song)table.getTableModel().getValueAt(row, 1);
			ID3TagsDlg dlg = new ID3TagsDlg(song.getFile().getAbsolutePath());
			dlg.setVisible(true);
			if(dlg.isSaved()){
				refreshRow(row);
				String strs[] = new String[3];
				strs[0] = (String)table.getValueAt(row, 3);
				strs[1] = (String)table.getValueAt(row, 4);
				strs[2] = (String)table.getValueAt(row, 5);
				changeInstances(song.getFile().getAbsolutePath(), strs, row);
			}
			dlg.dispose();
		}
	}
	private class TfMesecKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent event) {
			if((event.getKeyCode() == KeyEvent.VK_ENTER) && (event.getModifiers() == 0))
				getBtnMesec().doClick();
		}
	}
	protected JButton getBtnMesec() {
		return btnMesec;
	}
}
