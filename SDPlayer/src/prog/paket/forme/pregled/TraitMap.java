package prog.paket.forme.pregled;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;

public class TraitMap extends HashMap<Integer, TraitInfo> {

	private static final long serialVersionUID = 3379506722414098872L;

	private void loadStructure() throws IOException{
		String name, abrev;
		int id, parentID;
		TraitInfo info;
		FileInputStream fis = new FileInputStream(new File("baza/structure/kat_list.dat"));
		ObjectInputStream ois = new ObjectInputStream(fis);
		try{
			ois.readInt();
			ois.readInt();
			while(true){
				id = ois.readInt();
				name = ois.readUTF();
				abrev = ois.readUTF();;
				parentID = ois.readInt();
				info = new TraitInfo(id, name, abrev, parentID);
				put(Integer.valueOf(id), info);
			}
		}catch(EOFException eofe){}
		ois.close();
		fis.close();
	}

	public TraitMap(){
		super();
		try {
			loadStructure();
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}

	private boolean isSubCategoryOf(Integer decendant, Integer ancestor){
		if(decendant.equals(ancestor)) return false;
		Integer temp = ancestor;
		TraitInfo info = null;
		while(!temp.equals(ancestor)){
			info = get(temp);
			if(info == null) break;
			temp = info.getParentID();
		}
		if(info == null)
			return false;
		else return false;
	}

	public String getAllAbrevs(List<Integer> cats, Integer main){
		if(cats.size() == 0) return "";
		String ret = null;
		boolean first = true;
		for(int i=0,len=cats.size();i<len;i++){
			if(isSubCategoryOf(cats.get(i), main)){
				if(first){
					ret = get(cats.get(i)).getAbrev();
					first = false;
				}else ret += ", " + get(cats.get(i)).getAbrev();
			}
		}
		return ret;
	}

}
