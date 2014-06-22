package prog.paket.baza.struct;

import java.util.ArrayList;
import java.util.List;

public class SongEntry {

	private int id;

	private byte rank;

	private String fullPath;

	private String fileName;

	private ArrayList<Integer> cats = new ArrayList<Integer>();

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public byte getRank() {
		return rank;
	}

	public void setRank(byte rank) {
		this.rank = rank;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public void parseFileName(){
		int i = fullPath.lastIndexOf("\\");
		if(i == -1) i = fullPath.lastIndexOf("/");
		fileName = fullPath.substring(i + 1, fullPath.lastIndexOf("."));
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public ArrayList<Integer> getCats() {
		return cats;
	}

	public void setCats(ArrayList<Integer> cats) {
		this.cats = cats;
	}

	public boolean hasCat(int catID){
		for(int i=0,len=cats.size();i<len;i++){
			if(cats.get(i).intValue() == catID) return true;
		}
		return false;
	}

	public void addCats(List<Integer> list){
		for(int i=0,len=list.size();i<len;i++){
			if(!hasCat(list.get(i)))
				cats.add(list.get(i).intValue());
		}
	}

	public void removeCats(List<Integer> list){
		for(int i=0,len=list.size();i<len;i++){
			if(hasCat(list.get(i))){
				for(int j=cats.size()-1;j>=0;j--){
					if(cats.get(j).equals(list.get(i))){
						cats.remove(j);
						break;
					}
				}
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj instanceof SongEntry){
			return ((SongEntry)obj).fullPath.equals(this.fullPath);
		}else return false;
	}

	@Override
	public String toString() {
		if((fileName != null) && !fileName.equals(""))
			return fileName;
		int i = fullPath.lastIndexOf("\\");
		if(i == -1) i = fullPath.lastIndexOf("/");
		return fullPath.substring(i + 1, fullPath.lastIndexOf("."));
	}

}
