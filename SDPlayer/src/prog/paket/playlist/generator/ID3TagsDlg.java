package prog.paket.playlist.generator;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;
import javax.swing.JButton;

import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import java.awt.Dimension;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ID3TagsDlg extends JDialog {

	private static final long serialVersionUID = -6389439206233029570L;

	public JTextField textField;
	public JPanel southPanel;
	public JPanel centerPanel;
	public JPanel editID3v1Panel;
	public JPanel editID3v24Panel;
	public JLabel lblIzvodjacV1;
	public JLabel lblNazivV1;
	public JLabel lblAlbumV1;
	public JLabel lblKomentarV1;
	public JLabel lblRBv1;
	public JLabel lblGodinaV1;
	public JTextField tfIzvodjacV1;
	public JTextField tfNazivV1;
	public JTextField tfKomentarV1;
	public JTextField tfAlbumV1;
	public JTextField tfRBv1;
	public JTextField tfGodinaV1;
	public JCheckBox chckbxAddID3v1;
	public JLabel lblIzvodjacV24;
	public JLabel lblNazivV24;
	public JLabel lblKomentarV24;
	public JLabel lblAlbumV24;
	public JLabel lblRBv24;
	public JLabel lblGodinaV24;
	public JLabel lblKompozitorV24;
	public JLabel lblOriginalniIzvodjacV24;
	public JLabel lblVlasnikPravaV24;
	public JTextField tfIzvodjacV24;
	public JTextField tfNazivV24;
	public JTextField tfKomentarV24;
	public JTextField tfAlbumV24;
	public JTextField tfRBv24;
	public JTextField tfGodinaV24;
	public JTextField tfKompozitorV24;
	public JTextField tfOriginalV24;
	public JTextField tfVlasnikPravaV24;
	public JCheckBox chckbxAddID3v24;
	public JButton btnOk;
	public JButton btnCancel;

	private String fileName = null;
	private boolean saved = false;

	public boolean isSaved(){
		return saved;
	}

	private void readID3Tags(){
		try {
			File file = new File(fileName);
			MP3File mp3file = new MP3File(file, MP3File.LOAD_ALL);
			ID3v1Tag v1tag = mp3file.getID3v1Tag();
			String temp = null;
			if(v1tag == null){
				chckbxAddID3v1.setSelected(false);
			}else{
				chckbxAddID3v1.setSelected(true);
				temp = v1tag.getFirst(FieldKey.ARTIST);
				if((temp != null) && !temp.isEmpty())
					tfIzvodjacV1.setText(temp);
				temp = v1tag.getFirst(FieldKey.TITLE);
				if((temp != null) && !temp.isEmpty())
					tfNazivV1.setText(temp);
				temp = v1tag.getFirst(FieldKey.COMMENT);
				if((temp != null) && !temp.isEmpty())
					tfKomentarV1.setText(temp);
				temp = v1tag.getFirst(FieldKey.ALBUM);
				if((temp != null) && !temp.isEmpty())
					tfAlbumV1.setText(temp);
				temp = v1tag.getFirst(FieldKey.TRACK);
				if((temp != null) && !temp.isEmpty())
					tfRBv1.setText(temp);
				temp = v1tag.getFirst(FieldKey.YEAR);
				if((temp != null) && !temp.isEmpty())
					tfGodinaV1.setText(temp);
			}
			AbstractID3v2Tag v2tag = mp3file.getID3v2Tag();
			if(v2tag == null){
				chckbxAddID3v24.setSelected(false);
			}else{
				chckbxAddID3v24.setSelected(true);
				temp = v2tag.getFirst(FieldKey.ARTIST);
				if((temp != null) && !temp.isEmpty())
					tfIzvodjacV24.setText(temp);
				temp = v2tag.getFirst(FieldKey.TITLE);
				if((temp != null) && !temp.isEmpty())
					tfNazivV24.setText(temp);
				temp = v2tag.getFirst(FieldKey.COMMENT);
				if((temp != null) && !temp.isEmpty())
					tfKomentarV24.setText(temp);
				temp = v2tag.getFirst(FieldKey.ALBUM);
				if((temp != null) && !temp.isEmpty())
					tfAlbumV24.setText(temp);
				temp = v2tag.getFirst(FieldKey.TRACK);
				if((temp != null) && !temp.isEmpty())
					tfRBv24.setText(temp);
				temp = v2tag.getFirst(FieldKey.YEAR);
				if((temp != null) && !temp.isEmpty())
					tfGodinaV24.setText(temp);
				temp = v2tag.getFirst(FieldKey.COMPOSER);
				if((temp != null) && !temp.isEmpty())
					tfKompozitorV24.setText(temp);
				temp = v2tag.getFirst(FieldKey.ORIGINAL_ARTIST);
				if((temp != null) && !temp.isEmpty())
					tfOriginalV24.setText(temp);
				temp = v2tag.getFirst(FieldKey.PRODUCER);
				if((temp != null) && !temp.isEmpty())
					tfVlasnikPravaV24.setText(temp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TagException e) {
			e.printStackTrace();
		} catch (ReadOnlyFileException e) {
			e.printStackTrace();
		} catch (InvalidAudioFrameException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ID3TagsDlg(String fileName) {
		setModal(true);
		
		String title = fileName;
		int index = title.lastIndexOf("/");
		if(index != -1) title = title.substring(index);
		index = title.lastIndexOf("\\");
		if(index != -1) title = title.substring(index);
		setTitle(title);
		
		setMinimumSize(new Dimension(680, 430));
		setBounds(100, 100, 730, 434);
		
		centerPanel = new JPanel();
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
		
		editID3v1Panel = new JPanel();
		editID3v1Panel.setBorder(new TitledBorder(null, "ID3v1", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		centerPanel.add(editID3v1Panel);
		GridBagLayout gbl_editID3v1Panel = new GridBagLayout();
		gbl_editID3v1Panel.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 30, 0};
		gbl_editID3v1Panel.columnWidths = new int[]{0, 0, 0};
		gbl_editID3v1Panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_editID3v1Panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		editID3v1Panel.setLayout(gbl_editID3v1Panel);
		
		chckbxAddID3v1 = new JCheckBox("Snimi ID3v1 oznaku");
		chckbxAddID3v1.addItemListener(new ChckbxAddID3v1ItemListener());
		GridBagConstraints gbc_chckbxAddID3v1 = new GridBagConstraints();
		gbc_chckbxAddID3v1.anchor = GridBagConstraints.WEST;
		gbc_chckbxAddID3v1.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxAddID3v1.gridx = 1;
		gbc_chckbxAddID3v1.gridy = 0;
		editID3v1Panel.add(chckbxAddID3v1, gbc_chckbxAddID3v1);
		
		lblIzvodjacV1 = new JLabel("Izvo\u0111a\u010D");
		GridBagConstraints gbc_lblIzvodjacV1 = new GridBagConstraints();
		gbc_lblIzvodjacV1.anchor = GridBagConstraints.EAST;
		gbc_lblIzvodjacV1.insets = new Insets(0, 0, 5, 5);
		gbc_lblIzvodjacV1.gridx = 0;
		gbc_lblIzvodjacV1.gridy = 1;
		editID3v1Panel.add(lblIzvodjacV1, gbc_lblIzvodjacV1);
		
		tfIzvodjacV1 = new JTextField();
		tfIzvodjacV1.setEnabled(false);
		GridBagConstraints gbc_tfIzvodjacV1 = new GridBagConstraints();
		gbc_tfIzvodjacV1.insets = new Insets(0, 0, 5, 0);
		gbc_tfIzvodjacV1.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfIzvodjacV1.gridx = 1;
		gbc_tfIzvodjacV1.gridy = 1;
		editID3v1Panel.add(tfIzvodjacV1, gbc_tfIzvodjacV1);
		tfIzvodjacV1.setColumns(10);
		
		lblNazivV1 = new JLabel("Naziv");
		GridBagConstraints gbc_lblNazivV1 = new GridBagConstraints();
		gbc_lblNazivV1.anchor = GridBagConstraints.EAST;
		gbc_lblNazivV1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNazivV1.gridx = 0;
		gbc_lblNazivV1.gridy = 2;
		editID3v1Panel.add(lblNazivV1, gbc_lblNazivV1);
		
		tfNazivV1 = new JTextField();
		tfNazivV1.setEnabled(false);
		GridBagConstraints gbc_tfNazivV1 = new GridBagConstraints();
		gbc_tfNazivV1.insets = new Insets(0, 0, 5, 0);
		gbc_tfNazivV1.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfNazivV1.gridx = 1;
		gbc_tfNazivV1.gridy = 2;
		editID3v1Panel.add(tfNazivV1, gbc_tfNazivV1);
		tfNazivV1.setColumns(10);
		
		lblKomentarV1 = new JLabel("Komentar");
		GridBagConstraints gbc_lblKomentarV1 = new GridBagConstraints();
		gbc_lblKomentarV1.anchor = GridBagConstraints.EAST;
		gbc_lblKomentarV1.insets = new Insets(0, 0, 5, 5);
		gbc_lblKomentarV1.gridx = 0;
		gbc_lblKomentarV1.gridy = 3;
		editID3v1Panel.add(lblKomentarV1, gbc_lblKomentarV1);
		
		tfKomentarV1 = new JTextField();
		tfKomentarV1.setEnabled(false);
		GridBagConstraints gbc_tfKomentarV1 = new GridBagConstraints();
		gbc_tfKomentarV1.insets = new Insets(0, 0, 5, 0);
		gbc_tfKomentarV1.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfKomentarV1.gridx = 1;
		gbc_tfKomentarV1.gridy = 3;
		editID3v1Panel.add(tfKomentarV1, gbc_tfKomentarV1);
		tfKomentarV1.setColumns(10);
		
		lblAlbumV1 = new JLabel("Album");
		GridBagConstraints gbc_lblAlbumV1 = new GridBagConstraints();
		gbc_lblAlbumV1.anchor = GridBagConstraints.EAST;
		gbc_lblAlbumV1.insets = new Insets(0, 0, 5, 5);
		gbc_lblAlbumV1.gridx = 0;
		gbc_lblAlbumV1.gridy = 4;
		editID3v1Panel.add(lblAlbumV1, gbc_lblAlbumV1);
		
		tfAlbumV1 = new JTextField();
		tfAlbumV1.setEnabled(false);
		GridBagConstraints gbc_tfAlbumV1 = new GridBagConstraints();
		gbc_tfAlbumV1.insets = new Insets(0, 0, 5, 0);
		gbc_tfAlbumV1.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfAlbumV1.gridx = 1;
		gbc_tfAlbumV1.gridy = 4;
		editID3v1Panel.add(tfAlbumV1, gbc_tfAlbumV1);
		tfAlbumV1.setColumns(10);
		
		lblRBv1 = new JLabel("Redni broj");
		GridBagConstraints gbc_lblRBv1 = new GridBagConstraints();
		gbc_lblRBv1.anchor = GridBagConstraints.EAST;
		gbc_lblRBv1.insets = new Insets(0, 0, 5, 5);
		gbc_lblRBv1.gridx = 0;
		gbc_lblRBv1.gridy = 5;
		editID3v1Panel.add(lblRBv1, gbc_lblRBv1);
		
		tfRBv1 = new JTextField();
		tfRBv1.setEnabled(false);
		GridBagConstraints gbc_tfRBv1 = new GridBagConstraints();
		gbc_tfRBv1.anchor = GridBagConstraints.WEST;
		gbc_tfRBv1.insets = new Insets(0, 0, 5, 0);
		gbc_tfRBv1.gridx = 1;
		gbc_tfRBv1.gridy = 5;
		editID3v1Panel.add(tfRBv1, gbc_tfRBv1);
		tfRBv1.setColumns(4);
		
		lblGodinaV1 = new JLabel("Godina");
		GridBagConstraints gbc_lblGodinaV1 = new GridBagConstraints();
		gbc_lblGodinaV1.anchor = GridBagConstraints.EAST;
		gbc_lblGodinaV1.insets = new Insets(0, 0, 5, 5);
		gbc_lblGodinaV1.gridx = 0;
		gbc_lblGodinaV1.gridy = 6;
		editID3v1Panel.add(lblGodinaV1, gbc_lblGodinaV1);
		
		tfGodinaV1 = new JTextField();
		tfGodinaV1.setEnabled(false);
		GridBagConstraints gbc_tfGodinaV1 = new GridBagConstraints();
		gbc_tfGodinaV1.anchor = GridBagConstraints.WEST;
		gbc_tfGodinaV1.insets = new Insets(0, 0, 5, 0);
		gbc_tfGodinaV1.gridx = 1;
		gbc_tfGodinaV1.gridy = 6;
		editID3v1Panel.add(tfGodinaV1, gbc_tfGodinaV1);
		tfGodinaV1.setColumns(4);
		
		editID3v24Panel = new JPanel();
		editID3v24Panel.setBorder(new TitledBorder(null, "ID3v2.4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		centerPanel.add(editID3v24Panel);
		GridBagLayout gbl_editID3v24Panel = new GridBagLayout();
		gbl_editID3v24Panel.columnWidths = new int[]{0, 0, 0};
		gbl_editID3v24Panel.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_editID3v24Panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_editID3v24Panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		editID3v24Panel.setLayout(gbl_editID3v24Panel);
		
		chckbxAddID3v24 = new JCheckBox("Snimi ID3v2.4 oznaku");
		chckbxAddID3v24.addItemListener(new ChckbxAddID3v24ItemListener());
		GridBagConstraints gbc_chckbxAddID3v24 = new GridBagConstraints();
		gbc_chckbxAddID3v24.anchor = GridBagConstraints.WEST;
		gbc_chckbxAddID3v24.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxAddID3v24.gridx = 1;
		gbc_chckbxAddID3v24.gridy = 0;
		editID3v24Panel.add(chckbxAddID3v24, gbc_chckbxAddID3v24);
		
		lblIzvodjacV24 = new JLabel("Izvo\u0111a\u010D");
		GridBagConstraints gbc_lblIzvodjacV24 = new GridBagConstraints();
		gbc_lblIzvodjacV24.anchor = GridBagConstraints.EAST;
		gbc_lblIzvodjacV24.insets = new Insets(0, 0, 5, 5);
		gbc_lblIzvodjacV24.gridx = 0;
		gbc_lblIzvodjacV24.gridy = 1;
		editID3v24Panel.add(lblIzvodjacV24, gbc_lblIzvodjacV24);
		
		tfIzvodjacV24 = new JTextField();
		tfIzvodjacV24.setEnabled(false);
		GridBagConstraints gbc_tfIzvodjacV24 = new GridBagConstraints();
		gbc_tfIzvodjacV24.insets = new Insets(0, 0, 5, 0);
		gbc_tfIzvodjacV24.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfIzvodjacV24.gridx = 1;
		gbc_tfIzvodjacV24.gridy = 1;
		editID3v24Panel.add(tfIzvodjacV24, gbc_tfIzvodjacV24);
		tfIzvodjacV24.setColumns(10);
		
		lblNazivV24 = new JLabel("Naziv");
		GridBagConstraints gbc_lblNazivV24 = new GridBagConstraints();
		gbc_lblNazivV24.anchor = GridBagConstraints.EAST;
		gbc_lblNazivV24.insets = new Insets(0, 0, 5, 5);
		gbc_lblNazivV24.gridx = 0;
		gbc_lblNazivV24.gridy = 2;
		editID3v24Panel.add(lblNazivV24, gbc_lblNazivV24);
		
		tfNazivV24 = new JTextField();
		tfNazivV24.setEnabled(false);
		GridBagConstraints gbc_tfNazivV24 = new GridBagConstraints();
		gbc_tfNazivV24.insets = new Insets(0, 0, 5, 0);
		gbc_tfNazivV24.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfNazivV24.gridx = 1;
		gbc_tfNazivV24.gridy = 2;
		editID3v24Panel.add(tfNazivV24, gbc_tfNazivV24);
		tfNazivV24.setColumns(10);
		
		lblKomentarV24 = new JLabel("Komentar");
		GridBagConstraints gbc_lblKomentarV24 = new GridBagConstraints();
		gbc_lblKomentarV24.anchor = GridBagConstraints.EAST;
		gbc_lblKomentarV24.insets = new Insets(0, 0, 5, 5);
		gbc_lblKomentarV24.gridx = 0;
		gbc_lblKomentarV24.gridy = 3;
		editID3v24Panel.add(lblKomentarV24, gbc_lblKomentarV24);
		
		tfKomentarV24 = new JTextField();
		tfKomentarV24.setEnabled(false);
		GridBagConstraints gbc_tfKomentarV24 = new GridBagConstraints();
		gbc_tfKomentarV24.insets = new Insets(0, 0, 5, 0);
		gbc_tfKomentarV24.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfKomentarV24.gridx = 1;
		gbc_tfKomentarV24.gridy = 3;
		editID3v24Panel.add(tfKomentarV24, gbc_tfKomentarV24);
		tfKomentarV24.setColumns(10);
		
		lblAlbumV24 = new JLabel("Album");
		GridBagConstraints gbc_lblAlbumV24 = new GridBagConstraints();
		gbc_lblAlbumV24.anchor = GridBagConstraints.EAST;
		gbc_lblAlbumV24.insets = new Insets(0, 0, 5, 5);
		gbc_lblAlbumV24.gridx = 0;
		gbc_lblAlbumV24.gridy = 4;
		editID3v24Panel.add(lblAlbumV24, gbc_lblAlbumV24);
		
		tfAlbumV24 = new JTextField();
		tfAlbumV24.setEnabled(false);
		GridBagConstraints gbc_tfAlbumV24 = new GridBagConstraints();
		gbc_tfAlbumV24.insets = new Insets(0, 0, 5, 0);
		gbc_tfAlbumV24.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfAlbumV24.gridx = 1;
		gbc_tfAlbumV24.gridy = 4;
		editID3v24Panel.add(tfAlbumV24, gbc_tfAlbumV24);
		tfAlbumV24.setColumns(10);
		
		lblRBv24 = new JLabel("Redni broj");
		GridBagConstraints gbc_lblRBv24 = new GridBagConstraints();
		gbc_lblRBv24.anchor = GridBagConstraints.EAST;
		gbc_lblRBv24.insets = new Insets(0, 0, 5, 5);
		gbc_lblRBv24.gridx = 0;
		gbc_lblRBv24.gridy = 5;
		editID3v24Panel.add(lblRBv24, gbc_lblRBv24);
		
		tfRBv24 = new JTextField();
		tfRBv24.setEnabled(false);
		GridBagConstraints gbc_tfRBv24 = new GridBagConstraints();
		gbc_tfRBv24.anchor = GridBagConstraints.WEST;
		gbc_tfRBv24.insets = new Insets(0, 0, 5, 0);
		gbc_tfRBv24.gridx = 1;
		gbc_tfRBv24.gridy = 5;
		editID3v24Panel.add(tfRBv24, gbc_tfRBv24);
		tfRBv24.setColumns(4);
		
		lblGodinaV24 = new JLabel("Godina");
		GridBagConstraints gbc_lblGodinaV24 = new GridBagConstraints();
		gbc_lblGodinaV24.anchor = GridBagConstraints.EAST;
		gbc_lblGodinaV24.insets = new Insets(0, 0, 5, 5);
		gbc_lblGodinaV24.gridx = 0;
		gbc_lblGodinaV24.gridy = 6;
		editID3v24Panel.add(lblGodinaV24, gbc_lblGodinaV24);
		
		tfGodinaV24 = new JTextField();
		tfGodinaV24.setEnabled(false);
		GridBagConstraints gbc_tfGodinaV24 = new GridBagConstraints();
		gbc_tfGodinaV24.anchor = GridBagConstraints.WEST;
		gbc_tfGodinaV24.insets = new Insets(0, 0, 5, 0);
		gbc_tfGodinaV24.gridx = 1;
		gbc_tfGodinaV24.gridy = 6;
		editID3v24Panel.add(tfGodinaV24, gbc_tfGodinaV24);
		tfGodinaV24.setColumns(4);
		
		lblKompozitorV24 = new JLabel("Kompozitor");
		GridBagConstraints gbc_lblKompozitorV24 = new GridBagConstraints();
		gbc_lblKompozitorV24.anchor = GridBagConstraints.EAST;
		gbc_lblKompozitorV24.insets = new Insets(0, 0, 5, 5);
		gbc_lblKompozitorV24.gridx = 0;
		gbc_lblKompozitorV24.gridy = 7;
		editID3v24Panel.add(lblKompozitorV24, gbc_lblKompozitorV24);
		
		tfKompozitorV24 = new JTextField();
		tfKompozitorV24.setEnabled(false);
		GridBagConstraints gbc_tfKompozitorV24 = new GridBagConstraints();
		gbc_tfKompozitorV24.insets = new Insets(0, 0, 5, 0);
		gbc_tfKompozitorV24.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfKompozitorV24.gridx = 1;
		gbc_tfKompozitorV24.gridy = 7;
		editID3v24Panel.add(tfKompozitorV24, gbc_tfKompozitorV24);
		tfKompozitorV24.setColumns(10);
		
		lblOriginalniIzvodjacV24 = new JLabel("Originalni izvo\u0111a\u010D");
		GridBagConstraints gbc_lblOriginalniIzvodjacV24 = new GridBagConstraints();
		gbc_lblOriginalniIzvodjacV24.anchor = GridBagConstraints.EAST;
		gbc_lblOriginalniIzvodjacV24.insets = new Insets(0, 0, 5, 5);
		gbc_lblOriginalniIzvodjacV24.gridx = 0;
		gbc_lblOriginalniIzvodjacV24.gridy = 8;
		editID3v24Panel.add(lblOriginalniIzvodjacV24, gbc_lblOriginalniIzvodjacV24);
		
		tfOriginalV24 = new JTextField();
		tfOriginalV24.setEnabled(false);
		GridBagConstraints gbc_tfOriginalV24 = new GridBagConstraints();
		gbc_tfOriginalV24.insets = new Insets(0, 0, 5, 0);
		gbc_tfOriginalV24.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfOriginalV24.gridx = 1;
		gbc_tfOriginalV24.gridy = 8;
		editID3v24Panel.add(tfOriginalV24, gbc_tfOriginalV24);
		tfOriginalV24.setColumns(10);
		
		lblVlasnikPravaV24 = new JLabel("Vlasnik prava");
		GridBagConstraints gbc_lblVlasnikPravaV24 = new GridBagConstraints();
		gbc_lblVlasnikPravaV24.insets = new Insets(0, 0, 5, 5);
		gbc_lblVlasnikPravaV24.anchor = GridBagConstraints.EAST;
		gbc_lblVlasnikPravaV24.gridx = 0;
		gbc_lblVlasnikPravaV24.gridy = 9;
		editID3v24Panel.add(lblVlasnikPravaV24, gbc_lblVlasnikPravaV24);
		
		tfVlasnikPravaV24 = new JTextField();
		tfVlasnikPravaV24.setEnabled(false);
		GridBagConstraints gbc_tfVlasnikPravaV24 = new GridBagConstraints();
		gbc_tfVlasnikPravaV24.insets = new Insets(0, 0, 5, 0);
		gbc_tfVlasnikPravaV24.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfVlasnikPravaV24.gridx = 1;
		gbc_tfVlasnikPravaV24.gridy = 9;
		editID3v24Panel.add(tfVlasnikPravaV24, gbc_tfVlasnikPravaV24);
		tfVlasnikPravaV24.setColumns(10);
		
		southPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) southPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		getContentPane().add(southPanel, BorderLayout.SOUTH);
		
		btnOk = new JButton("Snimi");
		btnOk.addActionListener(new BtnOkActionListener());
		southPanel.add(btnOk);
		
		btnCancel = new JButton("Otka\u017Ei");
		btnCancel.addActionListener(new BtnCancelActionListener());
		southPanel.add(btnCancel);
		
		this.fileName = fileName;
		
		readID3Tags();
	}
	private class ChckbxAddID3v1ItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent event) {
			boolean state = false;
			if(event.getStateChange() == ItemEvent.SELECTED)
				state = true;
			else if(event.getStateChange() == ItemEvent.DESELECTED)
					state = false;
			else return;
			tfIzvodjacV1.setEnabled(state);
			tfNazivV1.setEnabled(state);
			tfKomentarV1.setEnabled(state);
			tfAlbumV1.setEnabled(state);
			tfRBv1.setEnabled(state);
			tfGodinaV1.setEnabled(state);
		}
	}
	private class ChckbxAddID3v24ItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent event) {
			boolean state = false;
			if(event.getStateChange() == ItemEvent.SELECTED)
				state = true;
			else if(event.getStateChange() == ItemEvent.DESELECTED)
					state = false;
			else return;
			tfIzvodjacV24.setEnabled(state);
			tfNazivV24.setEnabled(state);
			tfKomentarV24.setEnabled(state);
			tfAlbumV24.setEnabled(state);
			tfRBv24.setEnabled(state);
			tfGodinaV24.setEnabled(state);
			tfKompozitorV24.setEnabled(state);
			tfOriginalV24.setEnabled(state);
			tfVlasnikPravaV24.setEnabled(state);
		}
	}
	private void closeThis(){
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
	private class BtnCancelActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			closeThis();
		}
	}
	private class BtnOkActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try{
				File file = new File(fileName);
				MP3File mp3file = new MP3File(file);
				if(chckbxAddID3v1.isSelected()){
					ID3v1Tag tag = mp3file.getID3v1Tag();
					if(tag != null)
						mp3file.delete(tag);
					tag = new ID3v1Tag();
					tag.setField(FieldKey.ARTIST, tfIzvodjacV1.getText());
					tag.setField(FieldKey.TITLE, tfNazivV1.getText());
					tag.setField(FieldKey.COMMENT, tfKomentarV1.getText());
					tag.setField(FieldKey.ALBUM, tfAlbumV1.getText());
					tag.setField(FieldKey.TRACK, tfRBv1.getText());
					tag.setField(FieldKey.YEAR, tfGodinaV1.getText());
					mp3file.setID3v1Tag(tag);
				}else{
					ID3v1Tag tag = mp3file.getID3v1Tag();
					if(tag != null)
						mp3file.delete(tag);
				}
				if(chckbxAddID3v24.isSelected()){
					AbstractID3v2Tag tag = mp3file.getID3v2Tag();
					if(tag != null)
						mp3file.delete(tag);
					tag = new ID3v24Tag();
					tag.setField(FieldKey.ARTIST, tfIzvodjacV24.getText());
					tag.setField(FieldKey.TITLE, tfNazivV24.getText());
					tag.setField(FieldKey.COMMENT, tfKomentarV24.getText());
					tag.setField(FieldKey.ALBUM, tfAlbumV24.getText());
					tag.setField(FieldKey.TRACK, tfRBv24.getText());
					tag.setField(FieldKey.YEAR, tfGodinaV24.getText());
					tag.setField(FieldKey.COMPOSER, tfKompozitorV24.getText());
					tag.setField(FieldKey.ORIGINAL_ARTIST, tfOriginalV24.getText());
					tag.setField(FieldKey.PRODUCER, tfVlasnikPravaV24.getText());
					mp3file.setID3v2Tag(tag);
				}else{
					AbstractID3v2Tag tag = mp3file.getID3v2Tag();
					if(tag != null)
						mp3file.delete(tag);
				}
				mp3file.save();
				saved = true;
				closeThis();
			}catch(Exception e){
				e.printStackTrace(System.out);
			}
		}
	}
}
