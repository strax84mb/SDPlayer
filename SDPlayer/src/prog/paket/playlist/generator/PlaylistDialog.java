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
			while(item instanceof ListJSection){
				PlayerWin.getInstance().currSection = (ListJSection)item;
				item = playListPane.getPlayList().getListItemAt(0);
				if((item instanceof ListJSection) && (item == PlayerWin.getInstance().nextFirstCatSec))
					PlayerWin.getInstance().nextFirstCatSec = null;
				playListPane.getPlayList().getTableModel().removeRow(0);
				for(int i=0,len=playListPane.getPlayList().getTableModel().getRowCount();i<len;i++){
					item = playListPane.getPlayList().getListItemAt(i);
					if(item instanceof ListJSection){
						if(((ListJSection)item).prioritet == 1){
							PlayerWin.getInstance().nextFirstCatSec = (ListJSection)item;
							break;
						}
					}
				}
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
