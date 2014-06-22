package prog.paket.forme.emisije;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import prog.paket.dodaci.ListJItem;
import prog.paket.playlist.generator.struct.MusicCategory;

public class CategoryTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = -6552179837212411680L;

	protected List<ListJItem> list = new ArrayList<ListJItem>();

	private MusicCategory musicCat = new MusicCategory();

	public CategoryTreeNode(String name){
		super(name);
		this.setUserObject(name);
		musicCat.ime = name;
		add(new RankedTreeNode("Sve"));
		add(new RankedTreeNode("1"));
		add(new RankedTreeNode("2"));
		add(new RankedTreeNode("3"));
		add(new RankedTreeNode("4"));
		add(new RankedTreeNode("5"));
	}

	public MusicCategory getDesc(){
		return musicCat;
	}

	public void setDescValues(MusicCategory cat){
		cat.ime = (String)getUserObject();
		this.musicCat = cat;
	}

	public void setName(String name){
		setUserObject(name);
		musicCat.ime = name;
	}

	public void addSong(ListJItem item){
		int i = 0, len = list.size();
		for(;i<len;i++){
			if(list.get(i).fullPath.equals(item.fullPath))
				return;
			if(list.get(i).fullPath.compareTo(item.fullPath) > 0)
				break;
		}
		list.add(i, item);
	}

	public boolean isSongInList(String song){
		int len = list.size();
		for(int i=0;i<len;i++)
			if(list.get(i).fullPath.equals(song)) return true;
		return false;
	}

	public boolean isSongInList(ListJItem song){
		int len = list.size();
		for(int i=0;i<len;i++)
			if(list.get(i).fullPath.equals(song.fullPath)) return true;
		return false;
	}

	public List<ListJItem> getSongList(){
		return list;
	}

	public void setSongList(List<ListJItem> list){
		this.list = list;
	}

	public void removeSong(ListJItem item){
		for(int i=0,len=list.size();i<len;i++)
			if(list.get(i).equals(item)){
				list.remove(i);
				return;
			}
	}

}
