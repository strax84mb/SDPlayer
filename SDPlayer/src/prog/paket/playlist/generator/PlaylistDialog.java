package prog.paket.playlist.generator;

import javax.swing.JDialog;
import java.awt.BorderLayout;

import prog.paket.dodaci.ListJItem;
import prog.paket.dodaci.ListJSection;
import prog.paket.dodaci.PLTableModel;
import prog.paket.dodaci.PlayListPane;
import prog.paket.dodaci.PlayListTable;

public class PlaylistDialog extends JDialog {

	private static final long serialVersionUID = 8123145940650640499L;
	public PlayListPane playListPane;
	public ListJItem walker;
	public ListJSection sec;

	public ListJItem getNext(){
		if(PlayerWin.getInstance().autoPlayOn){
			((PlayListTable)playListPane.getViewport().getView()).clearSelection();
			if(playListPane.getPlayList().getRowCount() == 0){
				PlayerWin.getInstance().autoPlay.initiatePlaylist();
			}
			ListJItem item = playListPane.getPlayList().getTableModel().getItemAt(0);
			playListPane.getPlayList().getTableModel().removeRow(0);
			if(item.isSection() && ((ListJSection)item).prioritet != 1){
				PlayerWin.getInstance().nextFirstCatSec = null;
				walker = null;
			}
			while(item.isSection()){
				PlayerWin.getInstance().currSection = (ListJSection)item;
				System.out.println("Section : " + ((ListJSection)item).catName);
				if(PlayerWin.getInstance().nextFirstCatSec == null){
					walker = null;
					for(int i=0,len=playListPane.getPlayList().getTableModel().getRowCount();i<len;i++){
						walker = playListPane.getPlayList().getTableModel().getItemAt(i);
						if(!walker.isItem()){
							if(((ListJSection)walker).prioritet == 1){
								PlayerWin.getInstance().nextFirstCatSec = (ListJSection)walker;
								playListPane.getPlayList().getTableModel().removeRow(0);
								break;
							}
						}
					}
				}else{
					sec = (ListJSection)item;
					if(sec.prioritet == 1 && 
							sec.scheduledTime == PlayerWin.getInstance().nextFirstCatSec.scheduledTime){
						PlayerWin.getInstance().nextFirstCatSec = null;
						playListPane.getPlayList().getTableModel().removeRow(0);
						item = playListPane.getPlayList().getTableModel().getItemAt(0);
						playListPane.getPlayList().getTableModel().removeRow(0);
						break;
					}
				}
				item = playListPane.getPlayList().getTableModel().getItemAt(0);
				playListPane.getPlayList().getTableModel().removeRow(0);
			}
			/*
			int secInd = playListPane.getModel().getNextSectionIndex(0);
			if(secInd != -1){
				if(Math.abs(((ListJSection)playListPane.getModel().getItemAt(secInd)).startTime - 
						System.currentTimeMillis()) > 0){
					PlayerWin.getInstance().autoPlay.removeTillIndex(
							PlayerWin.getInstance().currSection.endToon, secInd);
				}
			}
			*/
			if(item.duration / 1000000 > 20)
				PlayerWin.getInstance().autoPlay.orderPlayListCheck();
			return item;
		}else{
			if(playListPane.getPlayList().getRowCount() == 0) return null;
			ListJItem item = playListPane.getPlayList().getListItemAt(0);
			playListPane.getPlayList().getTableModel().removeRow(0);
			return item;
		}
	}

	public ListJItem getElementAt(int row){
		return playListPane.getPlayList().getElement(row);
	}

	public PLTableModel getModel(){
		return playListPane.getPlayList().getTableModel();
	}

	/**
	 * Create the dialog.
	 */
	public PlaylistDialog() {
		setBounds(100, 100, 443, 420);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		playListPane = new PlayListPane();
		getContentPane().add(playListPane);

	}
}
