package prog.paket.baza;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import prog.paket.baza.struct.PosEntry;
import prog.paket.baza.struct.SongEntry;

public class DBFile {

	/*
	 * Struktura sloga (568):
	 * id pesme (int)
	 * rang pesme (byte)
	 * broj kategorija - maksimum 15 (byte)
	 * kategorije - 15 mesta (15 int)
	 * broj karaktera - maksimum 250 (short)
	 * putanja do pesme - 250 karaktera (250 char)
	 * 
	 * Pozicioni slog (12):
	 * id pesme (int)
	 * pozicija pesme (long)
	 * 
	 * Fajl "main.sdb" cuva sve slogove
	 * Fajl "pos.sdb" cuva mesta svih slogova
	 */
	private RandomAccessFile raf;
	private RandomAccessFile posRAF;

	private byte[] filePath = new byte[500];

	private StringBuilder builder = new StringBuilder(250);

	public void open(String path, String posPath) throws IOException{
		File file = new File(path);
		File posFile = new File(posPath);
		if(!file.exists()) file.createNewFile();
		if(!posFile.exists()) posFile.createNewFile();
		raf = new RandomAccessFile(file, "rw");
		posRAF = new RandomAccessFile(posFile, "rw");
	}

	public void close() throws IOException{
		raf.close();
		posRAF.close();
	}

	public int[] readCats() throws IOException{
		int size = raf.readByte();
		int ret[] = new int[size];
		for(int i=0;i<size;i++)
			ret[i] = raf.readInt();
		size = (15 - size) * 4;
		while(size > 0)
			size -= raf.skipBytes(size);
		return ret;
	}

	public void writeEntry(SongEntry entry) throws IOException{
		// Pise ID
		raf.writeInt(entry.getId());
		// Pise rang
		raf.writeByte(entry.getRank());
		// Pise kategorije
		int len = entry.getCats().size();
		raf.writeByte(len);
		for(int i=0;i<len;i++)
			raf.writeInt(entry.getCats().get(i));
		len = 15 - len;
		while(len > 0){
			raf.writeInt(0);
			len--;
		}
		// Pise putanju
		len = entry.getFullPath().length();
		raf.writeShort(len);
		raf.writeChars(entry.getFullPath());
		len = 250 - len;
		for(int i=0;i<len;i++)
			raf.writeChar(0);
	}

	public void skipString() throws IOException{
		int size = 502;
		while(size > 0) size -= raf.skipBytes(size);
	}

	public void skipEntry() throws IOException{
		int size = 567;
		while(size > 0) size -= raf.skipBytes(size);
	}

	public void skipEntryData() throws IOException{
		// Nema potrebe za citanjem ID jer je vec procitan
		int size = 563;
		while(size > 0) size -= raf.skipBytes(size);
	}

	public SongEntry readEntry(long pos) throws IOException{
		raf.seek(pos);
		return readEntry();
	}

	private String readString() throws IOException{
		int len = raf.readShort(), temp = 0;
		builder.delete(0, builder.length());
		temp -= raf.read(filePath, temp, 500 - temp);
		ByteBuffer buffer = ByteBuffer.wrap(filePath);
		for(int i=0;i<len;i++)
			builder.append(buffer.getChar());
		return builder.toString();
	}

	public SongEntry readEntry() throws IOException{
		SongEntry ret = new SongEntry();
		ret.setId(raf.readInt());
		ret.setRank(raf.readByte());
		int size = raf.readByte();
		for(int i=0;i<size;i++)
			ret.getCats().add(raf.readInt());
		size = (15 - size) * 4;
		while(size > 0) size -= raf.skipBytes(size);
		ret.setFullPath(readString());
		ret.parseFileName();
		return ret;
	}

	public long getEntryPos(int cat, int id){
		try{
			// Otvara odgovarajuci pozicioni fajl
			RandomAccessFile file = new RandomAccessFile("baza/" + String.valueOf(cat) + ".pos", "r");
			// Cita broj slogova
			int len = file.readInt(), temp;
			long ret = -1;
			for(int i=0;i<len;i++){
				// Cita ID
				temp = file.readInt();
				// Ako ID odgovara trazenom, pamti poziciju
				if(temp == id){
					ret = file.readLong();
					break;
				}
				// Ignorise poziciju
				file.readLong();
			}
			file.close();
			// Vraca poziciju
			return ret;
		}catch(IOException e){
			e.printStackTrace();
			return -1;
		}
	}

	public long[] getAllEntryPos(int cat){
		try{
			RandomAccessFile file = new RandomAccessFile("baza/" + String.valueOf(cat) + ".pos", "r");
			int len = file.readInt();
			long ret[] = new long[len];
			for(int i=0;i<len;i++){
				file.readInt();
				ret[i] = file.readLong();
			}
			file.close();
			return ret;
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}

	public List<SongEntry> readEntries(long[] pos) throws IOException{
		ArrayList<SongEntry> ret = new ArrayList<SongEntry>();
		for(int i=0,len=pos.length;i<len;i++)
			ret.add(readEntry(pos[i]));
		return ret;
	}

	public void updateEntryPos(int cat, int id, long pos){
		if(getEntryPos(cat, id) == -1)
			addEntryPos(cat, id, pos);
		else updatePos(cat, id, pos);
	}

	public long changeEntry(SongEntry entry) throws IOException{
		long minSize = Long.MAX_VALUE;
		int catID = -1, tempID = -1;
		File file;
		for(int i=0,len=entry.getCats().size();i<len;i++){
			catID = entry.getCats().get(i);
			file = new File("baza/" + String.valueOf(catID) + ".pos");
			if(file.exists() && file.length() < minSize){
				minSize = file.length();
				tempID = catID;
			}
		}
		catID = tempID;
		// Ako je nasao najmanji fajl
		if(minSize == Long.MAX_VALUE){
			minSize = getMainPos(entry.getId());;
		}else{
			minSize = getEntryPos(catID, entry.getId());
			if(minSize == -1){
				minSize = getMainPos(entry.getId());
			}
			raf.seek(minSize);
		}
		writeEntry(entry);
		return minSize;
	}

	public void changeEntryCats(SongEntry entry) throws IOException{
		long minSize = Long.MAX_VALUE;
		int catID = -1;
		File file;
		for(int i=0,len=entry.getCats().size();i<len;i++){
			catID = entry.getCats().get(i);
			file = new File("baza/" + String.valueOf(catID) + ".pos");
			if(file.exists() && file.length() < minSize) break;
		}
		// Ako je nasao najmanji fajl
		if(minSize == Long.MAX_VALUE){
			minSize = getMainPos(entry.getId()) + 4;
		}else{
			minSize = getEntryPos(catID, entry.getId());
			if(minSize == -1){
				minSize = getMainPos(entry.getId()) + 4;
			}else raf.seek(minSize + 4);
		}
		// Upis kategorija
		int i, len = entry.getCats().size();
		raf.writeByte(len);
		for(i=0;i<len;i++)
			raf.writeInt(entry.getCats().get(i));
		len = 15 - len;
		for(i=0;i<len;i++)
			raf.writeInt(0);
	}

	private boolean updatePos(int cat, int id, long pos){
		try{
			RandomAccessFile file = new RandomAccessFile("baza/" + String.valueOf(cat) + ".pos", "rw");
			if(file.length() == 0){
				file.writeInt(0);
				file.seek(0);
			}
			int i = 0, len = file.readInt();
			for(i=0;i<len;i++){
				if(file.readInt() == id){
					file.writeLong(pos);
					break;
				}
				file.readLong();
			}
			if(i == len){
				file.seek(file.length());
				file.writeInt(id);
				file.writeLong(pos);
				file.seek(0);
				file.writeInt(len + 1);
			}
			file.close();
			return true;
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}

	private void crossCheckCats(List<PosEntry> list, List<PosEntry> extra, boolean exclude){
		boolean found = false;
		for(int i=list.size()-1;i>=0;i--){
			if(exclude){
				found = false;
				for(int j=0,j_len=extra.size();j<j_len && !found;j++){
					if(list.get(i).id == extra.get(j).id)
						found = true;
				}
				if(found) list.remove(i);
			}else{
				found = false;
				for(int j=0,j_len=extra.size();j<j_len && !found;j++){
					if(list.get(i).id == extra.get(j).id)
						found = true;
				}
				if(!found) list.remove(i);
			}
		}
	}

	private List<PosEntry> readWholePosFile(int catID){
		ArrayList<PosEntry> ret = new ArrayList<PosEntry>();
		try{
			RandomAccessFile file = new RandomAccessFile("baza/" + String.valueOf(catID) + ".pos", "r");
			int id, len = file.readInt();
			long pos;
			for(int i=0;i<len;i++){
				id = file.readInt();
				pos = file.readLong();
				ret.add(new PosEntry(id, pos));
			}
			file.close();
		}catch(FileNotFoundException fnfe){
			System.out.println("Category ID=" + String.valueOf(catID) + " not entered.");
		}catch(IOException e){e.printStackTrace();}
		return ret;
	}

	private void sortPosList(List<PosEntry> list){
		long max;
		int index;
		PosEntry temp;
		for(int i=1,len=list.size();i<len;i++){
			max = Long.MAX_VALUE;
			index = i;
			for(int j=i;j<len;j++){
				if(list.get(j).pos < max){
					max = list.get(j).pos;
					index = j;
				}
			}
			if(list.get(i-1).pos > max){
				temp = list.get(i-1);
				list.set(i-1, list.get(index));
				list.set(index, temp);
			}
		}
	}

	public List<SongEntry> getPosByCats(int[] includes, int[] excludes) throws IOException{
		ArrayList<SongEntry> ret = new ArrayList<SongEntry>();
		// Inicijalizuje brojacke promenljive 
		int i = 0, len = includes.length;
		// Ako je "includes" niz prazan, vraca praznu listu
		if(len == 0) return ret;
		List<PosEntry> posList = null;
		// Cita ceo pozicioni fajl dokle god je lista prazna
		// Zastita od zastarelih kategorija
		/*
		while(((posList == null) || (posList.size() == 0)) && (i < len)){
			posList = readWholePosFile(includes[i]);
			i++;
		}
		*/
		posList = readWholePosFile(includes[0]);
		// Kada lista dobije elemenata, nadalje se uporedjuje presek koji se cuva
		for(i=1;i<len;i++)
			crossCheckCats(posList, readWholePosFile(includes[i]), false);
		// Proverava da li je "excludes" naveden
		if((excludes != null) && (excludes.length > 0)){
			// Uporedjuje presek koji se odseca od konacne liste
			for(i=0,len=excludes.length;i<len;i++)
				crossCheckCats(posList, readWholePosFile(excludes[i]), true);
		}
		// Ako je rezultujuca lista prazna, vraca praznu listu
		if(posList.size() == 0) return ret;
		// Sortira listu
		sortPosList(posList);
		PosEntry temp;
		SongEntry entry;
		// Odlazi na pocetak baze
		raf.seek(0);
		for(i=0,len=posList.size();i<len;i++){
			// Ucitava poziciju
			temp = posList.get(i);
			// Ako potrebna pozicija ne odgovara trenutnoj, vrsi relokaciju
			if(raf.getFilePointer() != temp.pos)
				raf.seek(temp.pos);
			// Cita slog iz baze;
			entry = readEntry();
			// Dodaje slog u povratnu listu
			ret.add(entry);
		}
		return ret;
	}

	public long removePos(int cat, int id){
		try{
			RandomAccessFile file = new RandomAccessFile("baza/" + String.valueOf(cat) + ".pos", "rw");
			int len = file.readInt(), tempID;
			long pos = -1, ret = -1, tempPos;
			// Nalazi gde je informacija o poziciji
			for(int i=0;i<len;i++){
				if(file.readInt() == id){
					// Kada je nadje, cita poziciju u bazi i pamti trenutnu poziciju manje 12
					ret = file.readLong();
					pos = file.getFilePointer() - 12;
					break;
				}
				// Preskace 8 bajtova
				file.readLong();
			}
			if(ret == -1){
				// Ako informacija nije nadjena, fajl se zatvara i vraca se -1
				file.close();
				return -1;
			}
			if(file.length() > pos + 12){
				// Ako upamcena poziciona informacija nije zadnja, zadnja se kopira na njeno mesto
				file.seek(file.length() - 12);
				tempID = file.readInt();
				tempPos = file.readLong();
				file.seek(pos);
				file.writeInt(tempID);
				file.writeLong(tempPos);
			}
			// Broj pozicionih informacija se umanjuje za 1
			file.seek(0);
			tempID = file.readInt();
			file.seek(0);
			file.writeInt(tempID - 1);
			// Duzina fajla se umanjuje za 12 bajta
			file.setLength(file.length() - 12);
			boolean shouldDelete = (file.length() <= 4);
			file.close();
			if(shouldDelete)
				Files.delete(Paths.get("baza/" + String.valueOf(cat) + ".pos"));
			return ret;
		}catch(IOException e){
			e.printStackTrace();
			return -1;
		}
	}

	public void removeEntry(int id) throws IOException{
		removeEntry(getMainPos(id));
		removeMainPos(id);
	}

	public void removeEntry(long pos) throws IOException{
		int len, i, id;
		raf.seek(pos);
		if(pos < raf.length() - 568){
			int offset = 0;
			byte data[] = new byte[568];
			raf.seek(raf.length() - 568);
			while(offset < 568) offset += raf.read(data, offset, 568 - offset);
			raf.seek(pos);
			offset = 0;
			raf.write(data, offset, 568 - offset);
			ByteBuffer buffer = ByteBuffer.wrap(data);
			id = buffer.getInt();
			buffer.get();
			len = buffer.get();
			for(i=0;i<len;i++)
				updatePos(buffer.getInt(), id, pos);
			updateMainPos(id, pos);
		}
		raf.setLength(raf.length() - 568);
	}

	public void removeEntries(SongEntry[] songs) throws IOException{
		SongEntry temp;
		long pos;
		for(int i=0,i_len=songs.length;i<i_len;i++){
			temp = songs[i];
			pos = -1;
			for(int j=0,j_len=temp.getCats().size();j<j_len;j++){
				if(pos == -1)
					pos = removePos(temp.getCats().get(j), temp.getId());
				else removePos(temp.getCats().get(j), temp.getId());
			}
			if(pos == -1)
				removeEntry(temp.getId());
			else removeEntry(pos);
		}
	}

	public boolean isPathEntered(String path){
		try{
			raf.seek(0);
			SongEntry entry;
			while(true){
				entry = readEntry();
				if(entry.getFullPath().equals(path))
					return true;
			}
		}catch(EOFException eofe){
			System.out.println("Finished reading all entered paths.");
		}catch(Exception e){e.printStackTrace();}
		return false;
	}

	public int getNextID(){
		int ret = 0;
		try{
			raf.seek(0);
			int temp;
			while(true){
				temp = raf.readInt();
				if(temp > ret) ret = temp;
				raf.seek(raf.getFilePointer() + 563);
			}
		}catch(EOFException eofe){
			System.out.println("Read all IDs for next ID.");
		}catch(Exception e){e.printStackTrace();}
		return ret + 1;
	}

	private void addEntryPos(int cat, int id, long pos){
		try{
			RandomAccessFile file = new RandomAccessFile("baza/" + String.valueOf(cat) + ".pos", "rw");
			if(file.length() == 0){
				file.writeInt(1);
			}else{
				int num = file.readInt();
				file.seek(0);
				file.writeInt(num + 1);
			}
			file.seek(file.length());
			file.writeInt(id);
			file.writeLong(pos);
			file.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void addEntry(SongEntry entry) throws IOException{
		long pos = raf.length();
		raf.seek(pos);
		for(int i=0,len=entry.getCats().size();i<len;i++)
			addEntryPos(entry.getCats().get(i), entry.getId(), pos);
		writeEntry(entry);
		addMainPos(entry.getId(), pos);
	}

	public long getDBLengthInBytes() throws IOException{
		return raf.length();
	}

	public SongEntry changeEntry(SongEntry entry, List<Integer> inclList, List<Integer> exclList, 
			int defaultID) throws IOException{
		long pos = -1;
		entry.removeCats(exclList);
		for(int i=0,len=exclList.size();i<len;i++){
			if(pos == -1)
				pos = removePos(exclList.get(i).intValue(), entry.getId());
			else removePos(exclList.get(i).intValue(), entry.getId());
		}
		if(pos == -1){
			pos = getMainPos(entry.getId());
		}
		entry.addCats(inclList);
		if((entry.getCats().size() == 0) && (defaultID != -1))
			entry.getCats().add(defaultID);
		for(int i=0,len=inclList.size();i<len;i++)
			updatePos(inclList.get(i), entry.getId(), pos);
		raf.seek(pos);
		writeEntry(entry);
		return entry;
	}

	public List<SongEntry> findByName(String searchStr) throws IOException{
		long len = raf.length(), pos = 0;
		String temp = searchStr.toLowerCase();
		ArrayList<SongEntry> list = new ArrayList<SongEntry>();
		SongEntry entry;
		raf.seek(0);
		try{
			while(pos < len){
				entry = readEntry();
				if(entry.getFileName().toLowerCase().indexOf(temp) != -1)
					list.add(entry);
			}
		}catch(EOFException eof){
			System.out.println("Finished reading whole database.");
		}
		return list;
	}

	public void addMainPos(int id, long pos) throws IOException{
		int tempID;
		long posEND = posRAF.length(), tempPOS = 0, newPOS = 0, filePOS = 0;
		posRAF.seek(0);
		// Nadji prvi veci ID
		while(filePOS < posEND){
			tempID = posRAF.readInt();
			if(tempID > id){
				newPOS = posRAF.getFilePointer() - 4;
				break;
			}
			posRAF.readLong();
			filePOS += 12;
		}
		if(filePOS < posEND){
			// Pomeri sve sledece
			filePOS = posEND - 12;
			while(filePOS >= newPOS){
				posRAF.seek(filePOS);
				tempID = posRAF.readInt();
				tempPOS = posRAF.readLong();
				posRAF.writeInt(tempID);
				posRAF.writeLong(tempPOS);
				filePOS -= 12;
			}
		}else newPOS = posEND;
		// Upisi novi
		posRAF.seek(newPOS);
		posRAF.writeInt(id);
		posRAF.writeLong(pos);
	}

	public void updateMainPos(int id, long pos) throws IOException{
		long pos1 = 0, pos2 = (posRAF.length()==0)?0:posRAF.length()-12;
		if(pos2 == 0) throw new IOException();
		int id1 = -1, id2 = -1, middleID = -1;
		long middlePOS;
		posRAF.seek(pos1);
		id1 = posRAF.readInt();
		posRAF.seek(pos2);
		id2 = posRAF.readInt();
		while(pos1 < pos2){
			if(id1 == id){
				posRAF.seek(pos1 + 4);
				posRAF.writeLong(pos);
				break;
			}
			if(id2 == id){
				posRAF.seek(pos2 + 4);
				posRAF.writeLong(pos);
				break;
			}
			middlePOS = ((pos2 - pos1) / 24) * 12 + pos1;
			posRAF.seek(middlePOS);
			middleID = posRAF.readInt();
			if(middleID < id){
				pos1 = middlePOS;
				id1 = middleID;
			}else{
				pos2 = middlePOS;
				id2 = middleID;
			}
		}
	}

	public long removeMainPos(int id) throws IOException{
		int tempID = -1;
		long tempPOS, filePOS = 0, ret = -1;
		posRAF.seek(0);
		// Nadji unos
		while(tempID != id){
			tempID = posRAF.readInt();
			ret = posRAF.readLong();
			filePOS += 12;
		}
		while(filePOS < posRAF.length()){
			posRAF.seek(filePOS);
			tempID = posRAF.readInt();
			tempPOS = posRAF.readLong();
			posRAF.seek(filePOS - 12);
			posRAF.writeInt(tempID);
			posRAF.writeLong(tempPOS);
			filePOS += 12;
		}
		posRAF.setLength(posRAF.length() - 12);
		return ret;
	}

	public long getMainPos(int id) throws IOException{
		long pos1 = 0, pos2 = (posRAF.length()==0)?0:posRAF.length()-12;
		if(pos2 == 0) return -1;
		int id1 = -1, id2 = -1, middleID = -1;
		long middlePOS;
		posRAF.seek(pos1);
		id1 = posRAF.readInt();
		posRAF.seek(pos2);
		id2 = posRAF.readInt();
		while(pos1 < pos2){
			if(id1 == id){
				posRAF.seek(pos1 + 4);
				return posRAF.readLong();
			}
			if(id2 == id){
				posRAF.seek(pos2 + 4);
				return posRAF.readLong();
			}
			middlePOS = ((pos2 - pos1) / 24) * 12 + pos1;
			posRAF.seek(middlePOS);
			middleID = posRAF.readInt();
			if(middleID < id){
				pos1 = middlePOS;
				id1 = middleID;
			}else{
				pos2 = middlePOS;
				id2 = middleID;
			}
		}
		return -1;
	}

}
