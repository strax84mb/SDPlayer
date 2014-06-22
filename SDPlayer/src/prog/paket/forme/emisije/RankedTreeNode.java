package prog.paket.forme.emisije;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.tree.DefaultMutableTreeNode;

import prog.paket.dodaci.ListJItem;

public class RankedTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = -769416941617584790L;

	public byte rank = 0;

	public RankedTreeNode(String rankStr){
		super(rankStr);
		try{
			this.rank = Byte.parseByte(rankStr);
		}catch(Exception e){
			this.rank = 0;
		}
	}

	public List<ListJItem> getSongList(){
		List<ListJItem> list = ((CategoryTreeNode)getParent()).getSongList();
		if(rank == 0) return list;
		ArrayList<ListJItem> ret = new ArrayList<ListJItem>();
		for(int i=0,len=list.size();i<len;i++){
			if(list.get(i).rang == rank)
				ret.add(list.get(i));
		}
		return ret;
	}

	public void updateFromList(DefaultListModel<ListJItem> model){
		CategoryTreeNode node = (CategoryTreeNode)getParent();
		for(int i=0,len=model.size();i<len;i++){
			if(!node.isSongInList(model.getElementAt(i)))
				node.addSong(model.getElementAt(i));
		}
	}

}
