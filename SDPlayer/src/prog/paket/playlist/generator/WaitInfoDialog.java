package prog.paket.playlist.generator;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;

import java.awt.Font;
import java.io.File;
import java.util.List;

import prog.paket.baza.struct.SongEntry;
import prog.paket.dodaci.JPLayList;
import prog.paket.dodaci.ListJItem;
import prog.paket.dodaci.PlayListTable;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class WaitInfoDialog extends JDialog {

	private static final long serialVersionUID = 7246002299805142245L;

	/*
	 * 0: addSongsToLibByDrop
	 */
	private int operation;
	private JPLayList songList;
	private DefaultListModel<ListJItem> model;
	private PlayListTable plTable;
	private List<File> fileList;
	private List<ListJItem> list;
	private boolean checkIfAlreadyThere;
	private boolean repopulateList;
	private int index;
	private byte rank;

	static private WaitInfoDialog instance;

	static public WaitInfoDialog getInstance(){
		return instance;
	}

	public JLabel lblNewLabel;

	private boolean isSongInList(String song, DefaultListModel<ListJItem> model){
		int len = model.getSize();
		for(int i=0;i<len;i++)
			if(model.get(i).fullPath.equals(song)) return true;
		return false;
	}

	private ListJItem getSongFromList(String fullPath){
		for(int i=0,len=list.size();i<len;i++){
			if(list.get(i).fullPath.equals(fullPath))
				return list.get(i);
		}
		return null;
	}

	public void setAddSongsToLibByDrop(List<ListJItem> list, List<File> fileList, boolean checkIfAlreadyThere, byte rank){
		this.list = list;
		this.fileList = fileList;
		this.checkIfAlreadyThere = checkIfAlreadyThere;
		this.rank = rank;
		model = songList.getListModel();
		operation = 0;
	}

	private void processAndAddToSongList(List<ListJItem> model, ListJItem item){
		int i, result, len = list.size();
		for(i=0;i<len;i++){
			if(item.fullPath.compareTo(list.get(i).fullPath) == 0) return;
			result = item.fullPath.toLowerCase().compareTo(list.get(i).fullPath.toLowerCase());
			if(result < 0) break;
		}
		list.add(i, item);
	}

	private void addSongsToLibByDrop(){
		try{
			int len = fileList.size();
			File file;
			for(int i=0;i<len;i++){
				file = fileList.get(i);
				if(checkIfAlreadyThere){
					if(!file.isDirectory() && !isSongInList(file.getAbsolutePath(), model)){
						try{
							ListJItem item = new ListJItem(file);
							if(rank != -1) item.rang = rank;
							processAndAddToSongList(list, item);
						}catch(Exception e){
							e.printStackTrace(System.out);
						}
					}
				}else{
					try{
						ListJItem item = new ListJItem(file);
						if(rank != -1) item.rang = rank;
						list.add(item);
					}catch(Exception e){
						e.printStackTrace(System.out);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		setVisible(false);
	}

	public void setAddSongsToPlayList(DefaultListModel<ListJItem> model, List<File> fileList, int index){
		this.model = model;
		this.fileList = fileList;
		this.index = index;
		operation = 1;
	}

	private void addSongsToPlayList(){
		int len = fileList.size();
		for(int i=index,count=0;count<len;count++){
			try{
				ListJItem item = new ListJItem(fileList.get(count));
				model.add(i, item);
			}catch(Exception e){}
			i++;
		}
		setVisible(false);
	}

	public void setAddSongsToTree(List<ListJItem> list, List<File> fileList, JPLayList songList, 
			boolean repopulateList, byte rank){
		this.list = list;
		this.fileList = fileList;
		this.songList = songList;
		this.repopulateList = repopulateList;
		this.rank = rank;
		operation = 2;
	}

	private void sortedInsertInList(ListJItem item){
		int i, result, len = list.size();
		for(i=0;i<len;i++){
			if(item.fullPath.compareTo(list.get(i).fullPath) == 0){
				list.get(i).rang = rank;
				return;
			}
			result = item.fullPath.toLowerCase().compareTo(list.get(i).fullPath.toLowerCase());
			if(result < 0) break;
		}
		list.add(i, item);
	}

	private void addSongsToTree(){
		int len = fileList.size();
		for(int i=index,count=0;i<index+len;i++,count++){
			try{
				ListJItem item = new ListJItem(fileList.get(count));
				item.rang = rank;
				sortedInsertInList(item);
			}catch(Exception e){}
		}
		if(repopulateList){
			len = list.size();
			songList.getListModel().clear();
			for(int i=0;i<len;i++){
				songList.getListModel().addElement(list.get(i));
			}
		}
		setVisible(false);
	}

	public void setAddSongsToPlayListTable(PlayListTable plTable, List<File> fileList, int index){
		this.index = index;
		this.fileList = fileList;
		this.plTable = plTable;
		operation = 3;
	}

	private void addSongsToPlayListTable(){
		ListJItem[] items = new ListJItem[fileList.size()];
		int temp = 0;
		for(int i=0,len=fileList.size();i<len;i++){
			try{
				items[temp] = new ListJItem(fileList.get(i));
				items[temp].droppedToPL = true;
				if(items[temp].duration < 70000000L) items[temp].crossfade = false;
				temp++;
			}catch(Exception e){}
		}
		plTable.insertRows(items, index, temp);
		setVisible(false);
	}

	private SongEntry[] entries;

	/*
	 * Set operation = 4;
	 */
	public void prepareCopyingEntriesToMusicTree(List<ListJItem> list, byte rank, SongEntry[] entries){
		this.list = list;
		this.rank = rank;
		this.entries = entries;
		operation = 4;
	}

	private void addEntriesToMusicTree(){
		ListJItem item;
		for(int i=0,len=entries.length;i<len;i++){
			try{
				item = getSongFromList(entries[i].getFullPath());
				if(item == null){
					item = new ListJItem(entries[i]);
					if(rank > 0) item.rang = rank;
					sortedInsertInList(item);
				}else{
					if(rank > 0) item.rang = rank;
					item.cats = entries[i].getCats();
				}
			}catch(Exception e){
				e.printStackTrace(System.out);
			}
		}
	}

	/* 
	 * Set operation = 5;
	 */
	public void prepareCopyingEntriesToJPlayList(DefaultListModel<ListJItem> model, int index, 
			SongEntry[] entries){
		this.model = model;
		this.index = index;
		this.entries = entries;
		operation = 5;
	}

	private void addEntriesToJPlayList(){
		ListJItem item;
		for(int i=0,len=entries.length;i<len;i++){
			try{
				item = new ListJItem(entries[i]);
				model.insertElementAt(item, index);
				index++;
			}catch(Exception e){
				e.printStackTrace(System.out);
			}
		}
	}

	/**
	 * Create the dialog.
	 */
	public WaitInfoDialog() {
		addComponentListener(new ThisComponentListener());
		setModal(true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 573, 255);
		getContentPane().setLayout(null);
		
		lblNewLabel = new JLabel("<html><p align='center'>Program obavlja radnju koju ste mu zadali.<br/>S po\u0161tovanjem vas molimo da sa\u010Dekate.</p><</html>");
		lblNewLabel.setBounds(35, 68, 506, 62);
		lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 26));
		getContentPane().add(lblNewLabel);
		instance = this;
	}

	private class ThisComponentListener extends ComponentAdapter {
		
		@Override
		public void componentShown(ComponentEvent arg0) {
			arg0.getComponent().update(arg0.getComponent().getGraphics());
			switch(operation){
			case 0: addSongsToLibByDrop(); break;
			case 1: addSongsToPlayList(); break;
			case 2: addSongsToTree(); break;
			case 3: addSongsToPlayListTable(); break;
			// Add database entries to JMusicTree
			case 4: addEntriesToMusicTree(); break;
			// Add database entries to JPlayList
			case 5: addEntriesToJPlayList(); break;
			}
		}
	}

}
