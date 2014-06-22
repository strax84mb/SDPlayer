package prog.paket.test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RecoverSongDB {

	private RandomAccessFile raf;

	public RecoverSongDB() throws IOException{
		cleanUp();
		raf = new RandomAccessFile("baza/main.sdb.backup", "rw");
		long pos = 0, fileLen = raf.length();
		int id = 0, len, cats[], i;
		while(pos < fileLen){
			id++;
			raf.writeInt(id);
			raf.seek(pos + 5);
			len = raf.readByte();
			cats = new int[len];
			for(i=0;i<len;i++)
				cats[i] = raf.readInt();
			for(i=0;i<len;i++)
				addPos2Cat(cats[i], id, pos);
			addMainPos(id, pos);
			pos += 568;
			raf.seek(pos);
		}
		raf.close();
		updateCatFilesLengths();
	}

	private void cleanUp(){
		File dir = new File("baza");
		File[] files = dir.listFiles();
		for(int i=files.length-1;i>=0;i--){
			if(files[i].getName().endsWith(".pos")){
				files[i].delete();
			}else if(files[i].getName().equals("pos.sdb")){
				files[i].delete();
			}
		}
	}

	private void addPos2Cat(int cat, int id, long pos) throws IOException{
		File file = new File("baza/" + String.valueOf(cat) + ".pos");
		if(!file.exists()) file.createNewFile();
		RandomAccessFile posRAF = new RandomAccessFile(file, "rw");
		if(posRAF.length() == 0) posRAF.writeInt(0);
		posRAF.seek(posRAF.length());
		posRAF.writeInt(id);
		posRAF.writeLong(pos);
		posRAF.close();
	}

	private void addMainPos(int id, long pos) throws IOException{
		File file = new File("baza/pos.sdb");
		if(!file.exists()) file.createNewFile();
		RandomAccessFile posRAF = new RandomAccessFile(file, "rw");
		posRAF.seek(posRAF.length());
		posRAF.writeInt(id);
		posRAF.writeLong(pos);
		posRAF.close();
	}

	private void updateCatFilesLengths() throws IOException{
		File dir = new File("baza");
		File[] files = dir.listFiles();
		for(int i=files.length-1;i>=0;i--){
			if(files[i].getName().endsWith(".pos")){
				RandomAccessFile posRAF = new RandomAccessFile(files[i], "rw");
				int len = (int)((posRAF.length() - 4L) / 12L);
				posRAF.seek(0);
				posRAF.writeInt(len);
				posRAF.close();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new RecoverSongDB();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
