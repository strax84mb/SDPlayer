package prog.paket.test;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AdjustDBMenuStructFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//if(1 == Integer.valueOf(1).intValue()) return;
		try{
			Files.move(Paths.get("baza", "structure", "kat_list.dat"), 
					Paths.get("baza", "structure", "temp.dat"));
			FileInputStream fis = new FileInputStream("baza/structure/temp.dat");
			ObjectInputStream ois = new ObjectInputStream(fis);
			File outFile = new File("baza/structure/kat_list.dat");
			FileOutputStream fos = new FileOutputStream(outFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeInt(Integer.MIN_VALUE);
			oos.writeInt(Integer.MIN_VALUE);
			int id, parentID;
			String name, abrev;
			try{
				while(true){
					id = ois.readInt();
					name = ois.readUTF();
					abrev = ois.readUTF();;
					parentID = ois.readInt();
					oos.writeInt(id);
					oos.writeUTF(name);
					oos.writeUTF(abrev);
					oos.writeInt(parentID);
				}
			}catch(EOFException eofe){System.out.println("Finished copying file.");}
			ois.close();
			fis.close();
			oos.close();
			fos.close();
			Files.delete(Paths.get("baza", "structure", "temp.dat"));
		}catch(Exception e){e.printStackTrace();}
	}

}
