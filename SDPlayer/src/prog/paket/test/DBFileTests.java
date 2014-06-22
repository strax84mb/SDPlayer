package prog.paket.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import prog.paket.baza.DBFile;
import prog.paket.baza.struct.SongEntry;

/*
 * Test case has 20 entries.
 */
@RunWith(JUnit4.class)
public class DBFileTests {

	private DBFile db = new DBFile();;

	@Before public void initTest(){
		try {
			Files.copy(Paths.get("baza", "backup", "2.pos"), Paths.get("baza", "2.pos"), 
					StandardCopyOption.REPLACE_EXISTING);
			Files.copy(Paths.get("baza", "backup", "main.sdb"), Paths.get("baza", "main.sdb"), 
					StandardCopyOption.REPLACE_EXISTING);
			db.open("baza/main.sdb", "baza/pos.sdb");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@After public void closeDB(){
		try {
			db.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test public void readFirstEntry(){
		try {
			SongEntry entry = db.readEntry();
			Assert.assertEquals(1, entry.getId());
			Assert.assertEquals("E:\\Privremeno\\Muzika\\Black Eyed Peas - Dont Phunk With My Heart.mp3", 
					entry.getFullPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test public void readThirdEntry(){
		try {
			SongEntry entry = db.readEntry(1134);
			Assert.assertEquals(3, entry.getId());
			Assert.assertEquals("E:\\Privremeno\\Muzika\\Chemical Brothers - Galvanize.mp3", 
					entry.getFullPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test public void find3rdEntryWithPosFile(){
		try {
			long pos = db.getEntryPos(2, 3);
			Assert.assertEquals(1134, pos);
			SongEntry entry = db.readEntry(pos);
			Assert.assertEquals(3, entry.getId());
			Assert.assertEquals("E:\\Privremeno\\Muzika\\Chemical Brothers - Galvanize.mp3", 
					entry.getFullPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test public void countEntryPositions(){
		Assert.assertEquals(20, db.getAllEntryPos(2).length);
	}

	@Test public void remove3rdEntry(){
		try {
			long pos = db.removePos(2, 3);
			Assert.assertEquals(1134, pos);
			Assert.assertEquals(19, db.getAllEntryPos(2).length);
			db.removeEntry(pos);
			Assert.assertEquals(11340 - 567, db.getDBLengthInBytes());
			SongEntry entry = db.readEntry(1134);
			Assert.assertEquals(20, entry.getId());
			Assert.assertEquals("E:\\Privremeno\\Muzika\\Unknown - Majko Zemljo Jedina.mp3", 
					entry.getFullPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test public void changeEntry(){
		try {
			long pos = db.removePos(2, 3);
			Assert.assertEquals(1134, pos);
			Assert.assertEquals(19, db.getAllEntryPos(2).length);
			db.removeEntry(pos);
			Assert.assertEquals(11340 - 567, db.getDBLengthInBytes());
			SongEntry entry = db.readEntry(1134);
			Assert.assertEquals(20, entry.getId());
			Assert.assertEquals("E:\\Privremeno\\Muzika\\Unknown - Majko Zemljo Jedina.mp3", 
					entry.getFullPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
