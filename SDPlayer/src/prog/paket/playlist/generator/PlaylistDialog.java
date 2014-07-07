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

	public ListJItem getNext(){
		if(PlayerWin.getInstance().autoPlayOn){
			((PlayListTable)playListPane.getViewport().getView()).clearSelection();
			if(playListPane.getPlayList().getRowCount() == 0){
				PlayerWin.getInstance().autoPlay.initiatePlaylist();
			}
			ListJItem item = playListPane.getPlayList().getListItemAt(0);
			playListPane.getPlayList().getTableModel().removeRow(0);
			while(!item.isItem()){
				PlayerWin.getInstance().currSection = (ListJSection)item;
				item = playListPane.getPlayList().getListItemAt(0);
				if(!item.isItem() && (PlayerWin.getInstance().nextFirstCatSec != null) && 
						(PlayerWin.getInstance().nextFirstCatSec.equals((ListJSection)item))){
					PlayerWin.getInstance().nextFirstCatSec = null;
				}else{
					ListJItem item1 = null;
					for(int i=0,len=playListPane.getPlayList().getTableModel().getRowCount();i<len;i++){
						item1 = playListPane.getPlayList().getListItemAt(i);
						if(!item1.isItem()){
							if(((ListJSection)item1).prioritet == 1){
								PlayerWin.getInstance().nextFirstCatSec = (ListJSection)item1;
								break;
							}
						}
					}
				}
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
