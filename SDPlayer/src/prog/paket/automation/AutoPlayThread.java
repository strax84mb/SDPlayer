package prog.paket.automation;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import prog.paket.dodaci.ListJItem;
import prog.paket.dodaci.ListJSection;
import prog.paket.dodaci.PLTableModel;
import prog.paket.playlist.generator.PlayerWin;
import prog.paket.playlist.generator.struct.CatWithList;
import prog.paket.playlist.generator.struct.Duration;
import prog.paket.playlist.generator.struct.FirstCatFilter;
import prog.paket.playlist.generator.struct.PLGenerator;
import prog.paket.playlist.generator.struct.PlayerModule;
import prog.paket.playlist.generator.struct.ProgSection;
import prog.paket.playlist.generator.struct.ProgSectionType;
import prog.paket.playlist.generator.struct.SecFileFilter;
import prog.paket.playlist.generator.struct.StartTime;

public class AutoPlayThread extends Thread {

	/*
	 * 0: no action
	 * 1: fill playList
	 * 9: exit
	 */
	private int command = 0;
	private boolean keepRunning = true, leaveLast;
	private GregorianCalendar cal = new GregorianCalendar();
	private int pos, cutOffPriority = 3;
	private ListJItem item;
	private Random rand = new Random();
	private Map<String, ListJItem> itemsMap;

	private Lock lock = new ReentrantLock();
	private Condition hasJob = lock.newCondition();

	public void close(){
		command = 9;
	}

	public void orderPlayListCheck(){
		command = 1;
	}

	public void orderJumpToFirstCat(){
		command = 3;
	}

	public void orderStartTimesCorrection(){
		command = 4;
	}

	/**
	 * Vraća listu termina sortiranu po vremenu početka emitovanja u rastućem nizu.
	 * @return Lista termina.
	 */
	private File[] getSortedFileList(FileFilter filter){
		File dir = new File("plists");
		File files[] = dir.listFiles(filter);
		for(int i=0,iLen=files.length-1;i<iLen;i++){
			for(int j=i+1;j<iLen;j++){
				if((files[i].getName().length() > files[j].getName().length()) || 
						((files[i].getName().length() == files[j].getName().length()) && 
								(files[i].getName().compareTo(files[j].getName()) > 0))){
					dir = files[i];
					files[i] = files[j];
					files[j] = dir;
				}
			}
		}
		return files;
	}

	/**
	 * Vraća termin kojem pripada prosleđeno vreme.
	 * @param currentTime - Vreme u milisekundama za koje se vraća termin.
	 * @param files - Skup termina.
	 * @return Odgovarajući termin.
	 */
	private ProgSection findCurrentSection(long currentTime, File files[]){
		try{
			if(files == null)
				files = getSortedFileList(new SecFileFilter());
			String str;
			long time;
			// Krece od nazad da bi naslo prvo vreme koje je manje ili jednako prosledjenom vremenu
			for(int i=files.length-1;i>=0;i--){
				str = files[i].getName();
				str = str.substring(0, str.length() - 6);
				time = Long.parseLong(str);
				if(time <= currentTime)
					return ProgSection.load(files[i], null);
			}
			return null;
		}catch(Exception e){
			e.printStackTrace(System.out);
			return null;
		}
	}

	/**
	 * Vraća termin posle onog kojem pripada prosleđeno vreme.
	 * @param time - Vreme koje se prosleđuje u milisekundama.
	 * @param files - Skup termina.
	 * @return Sledeći termin.
	 */
	private ProgSection findNextSection(long time, File files[]){
		try{
			if(files == null)
				files = getSortedFileList(new SecFileFilter());
			String str;
			long temp;
			for(int i=0,len=files.length;i<len;i++){
				str = files[i].getName();
				str = str.substring(0, str.length() - 6);
				temp = Long.parseLong(str);
				if(temp > time)
					return ProgSection.load(files[i], null);
			}
			return null;
		}catch(Exception e){
			e.printStackTrace(System.out);
			return null;
		}
	}

	/**
	 * Nalazi indeks poslednje pesme (zapisa) u terminu.
	 * @param model - Model podataka plej liste.
	 * @param startIndex - Početni indeks termina.
	 * @return Indeks poslednje pesme (zapisa) u terminu.
	 */
	private int getSectionEndIndex(PLTableModel model, int startIndex){
		for(int i=startIndex,len=model.getRowCount();i<len;i++){
			if(model.getItemAt(i).isSection()){
				return i - 1;
			}
		}
		return -1;
	}

	public void removeTillIndex(boolean leaveLast, int until){
		command = 2;
		pos = until;
		this.leaveLast = leaveLast;
	}

	@Override
	public void run() {
		itemsMap = new HashMap<String, ListJItem>();
		lock.lock();
		while(keepRunning){
			try{
				switch(command){
				case 0:
					hasJob.awaitNanos(2000000000);
					break;
				case 1:
					while (!PlayerWin.getInstance().claimPlayList(PlayerModule.AUTOMATION)) {
						hasJob.awaitNanos(20000);
					}
					adjustPLStartTimes();
					adjustSection(0);
					addSectionsToPL(null, null);
					adjustPLStartTimes();
					getNextFirstCat();
					command = 0;
					PlayerWin.getInstance().releasePlayList();
					//generateMore();
					break;
				case 2:
					while (!PlayerWin.getInstance().claimPlayList(PlayerModule.AUTOMATION)) {
						hasJob.awaitNanos(20000);
					}
					PLTableModel model = PlayerWin.getInstance().playList.getModel();
					if(leaveLast) pos--;
					command = 0;
					if(pos < 1){
						break;
					}
					int rows[] = new int[pos];
					for(int i=0;i<pos;i++)
						rows[i] = i;
					model.removeRows(rows);
					PlayerWin.getInstance().releasePlayList();
					break;
				case 3:
					while (!PlayerWin.getInstance().claimPlayList(PlayerModule.AUTOMATION)) {
						hasJob.awaitNanos(20000);
					}
					jumpToFirstCat();
					command = 0;
					PlayerWin.getInstance().releasePlayList();
					break;
				case 4:
					while (!PlayerWin.getInstance().claimPlayList(PlayerModule.AUTOMATION)) {
						hasJob.awaitNanos(20000);
					}
					adjustPLStartTimes();
					PlayerWin.getInstance().releasePlayList();
					command = 0;
				case 9:
					keepRunning = false;
					break;
				}
			}catch(Exception e){
				if (!PlayerWin.getInstance().playListIsFree && 
						PlayerWin.getInstance().currentModule == PlayerModule.AUTOMATION) {
					PlayerWin.getInstance().releasePlayList();
				}
				e.printStackTrace(System.out);
			}
		}
		lock.unlock();
	}

	private void autoGenerateMore() throws IOException{
		File[] files = getSortedFileList(new SecFileFilter());
		PLGenerator generator = new PLGenerator();
		Date now = new Date();
		generator.generate(now.getTime(), now.getTime() + 189200000L);
		// Delete all previos sections
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
		ProgSection sec;
		for(int i=0,len=generator.sections.size();i<len;i++){
			sec = generator.sections.get(i);
			ProgSection.save(sec);
		}
	}

	public void jumpToFirstCat(){
		ProgSection nextFirstCatSec = PlayerWin.getInstance().nextFirstCatSec;
		PlayerWin.getInstance().nextFirstCatSec = null;
		try{
			PrintWriter writer = new PrintWriter("log.txt", "UTF-8");
			Date date = new Date();
			writer.println("LOG: Time is " + date.toString());
			writer.println("\tMoving onto first category section: " + nextFirstCatSec.catName);
			date.setTime(nextFirstCatSec.startTime);
			writer.println("\tstarting in " + date.toString());
			writer.close();
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
		PLTableModel model = PlayerWin.getPlayListModel();
		List<ListJItem> items = new ArrayList<ListJItem>();
		ListJItem temp;
		for(int i=0;i<model.getRowCount();i++){
			temp = model.getItemAt(i);
			if(temp.droppedToPL)
				items.add(temp);
		}
		model.setRowCount(0);
		File[] files = getSortedFileList(new FirstCatFilter());
		ProgSection section = findNextSection(System.currentTimeMillis() - 90000, files);
		model.addRow(section.generateListJSection());
		for(int i=0;i<section.songs.size();i++)
			model.addRow(section.songs.get(i));
		for(int i=0;i<items.size();i++)
			model.addRow(items.get(i));
	}

	private void getNextFirstCat() throws IOException {
		if(PlayerWin.getInstance().currSection == null) return;
		if(PlayerWin.getInstance().currSection.prioritet == 1) return;
		if(PlayerWin.getInstance().secondsToEnd() < 30) return;
		File files[] = getSortedFileList(new FirstCatFilter());
		String max = files[files.length-1].getName();
		max = max.substring(0, max.length() - 6);
		if(Long.valueOf(max) - System.currentTimeMillis() < 7200000L){
			autoGenerateMore();
			command = 0;
			return;
		}
		ProgSection firstCatSec = findNextSection(System.currentTimeMillis() + 120000L, files);
		PlayerWin.getInstance().nextFirstCatSec = firstCatSec;
	}

	private void respectDroppedToPLSongs(int upperIndex, PLTableModel model){
		
	}

	/**
	 * Dodaje termin na kraj plej liste.
	 * @param model - Model podataka plej liste.
	 * @param sec - Termin koji se dodaje.
	 */
	public void addSection(PLTableModel model, ProgSection sec){
		ListJSection lsec = sec.generateListJSection();
		model.addRow(new Object[]{new StartTime(), lsec, null});
		for(int i=0,len=sec.songs.size();i<len;i++)
			model.addRow(sec.songs.get(i));
	}

	private void loadCurrentSection(PLTableModel model, long currentTime, File files[]){
		try{
			if(files == null)
				files = getSortedFileList(new SecFileFilter());
			ProgSection sec = findCurrentSection(currentTime, files);
			model.addRow(new Object[]{new StartTime(), sec.generateListJSection(), null});
			long time = sec.startTime, nextTime;
			for(int i=0,len=sec.songs.size();i<len;i++){
				nextTime = (sec.songs.get(i).duration / 1000) + time;
				if(sec.crossfade && sec.startToon && (i > 1)) nextTime -= 3000;
				else if(sec.crossfade && sec.endToon && (i < len - 1)) nextTime -= 3000;
				if((time <= currentTime) && (currentTime <= nextTime)){
					if(currentTime - time > nextTime - currentTime) i++;
					model.addRow(sec.songs.get(i));
					i++;
					while(i < len){
						model.addRow(sec.songs.get(i));
						i++;
					}
					return;
				}
				time = nextTime;
			}
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
	}

	public void initiatePlaylist(){
		long startTime = System.currentTimeMillis();
		PLTableModel model = PlayerWin.getPlayListModel();
		File files[] = getSortedFileList(new SecFileFilter());
		loadCurrentSection(model, startTime, files);
		if(model.getRowCount() >= 50) return;
		ProgSection sec;
		while(model.getRowCount() < 50){
			sec = findNextSection(startTime, files);
			addSection(model, sec);
			startTime = sec.scheduledTime;
		}
	}

	public void adjustPLStartTimes(){
		long time = System.currentTimeMillis();
		time += PlayerWin.getInstance().durr * 1000;
		time -= (PlayerWin.getInstance().timePassed * 1000);
		PLTableModel model = PlayerWin.getPlayListModel();
		ListJItem item;
		ListJSection sec;
		int nextSecIndex = -1;
		for(int i=0,len=model.getRowCount();i<len;i++){
			item = model.getItemAt(i);
			if(item.isSection()){
				sec = (ListJSection)item;
				sec.startTime = time;
				continue;
			}
			if(nextSecIndex == -1){
				
			}else nextSecIndex = getSectionEndIndex(model, i);
			if(model.getItemAt(i).isItem()){
				StartTime st = model.getStartTimeAt(i);
				st.time = time;
				st.time2Text(cal);
				time += model.getItemAt(i).duration / 1000;
			}
		}
		PlayerWin.getInstance().playList.playListPane.repaintContent();
	}

	public void adjustSection(int startIndex){
		ListJSection sec = PlayerWin.getInstance().currSection;
		if(sec.sectionType != ProgSectionType.TERMIN) return;
		if(!sec.popunitiDoKraja) return;
		PLTableModel model = PlayerWin.getPlayListModel();
		if(model.getItemAt(0).isSection())
			return;
		int endIndex = getSectionEndIndex(model, startIndex);
		if(endIndex == -1) endIndex = model.getRowCount() - 1;
		if(!sec.endToon && (endIndex < 0)) return;
		if(sec.endToon && (endIndex < 1)) return;
		if(model.getRowCount() == endIndex + 1) return;
		ListJSection nextSec = (ListJSection)model.getItemAt(endIndex + 1);
		if(sec.endToon) endIndex--;
		item = model.getItemAt(endIndex);
		while(item.droppedToPL){
			endIndex--;
			if(endIndex < startIndex) return;
			item = model.getItemAt(endIndex);
		}
		long curr = System.currentTimeMillis();
		if(nextSec.scheduledTime - curr > 300000L && nextSec.scheduledTime - nextSec.startTime > 120000L){
			System.out.println("PL PROBLEM: Lack of songs.");
			// Ako ima manjka pesama
			try {
				CatWithList cwl = CatWithList.load(sec.catName);
				if(cwl == null) throw new IOException("ERROR: Failed reading song category: " + sec.catName);
				long diffTime = nextSec.scheduledTime - nextSec.startTime;
				boolean found = true;
				int i, len, tries = 10;
				// Ako razlika nije pozitivna prekidaj (misija uspela)
				while(diffTime > 0){
					found = true;
					while(found){
						found = false;
						item = cwl.list.get(rand.nextInt(cwl.list.size()));
						len = (endIndex - 3 < startIndex)? startIndex : endIndex - 3;
						for(i=endIndex;i>=len && !found;i--){
							if(model.getItemAt(i).fullPath.equals(item.fullPath))
								found = true;
						}
						if((diffTime > 30000L && nextSec.prioritet == 1) || 
								(nextSec.prioritet != 1 && Math.abs(diffTime - (item.duration / 1000)) < diffTime)){
							// Ako nije nadjen onda dodaj
							if(!found){
								endIndex++;
								model.insertRow(endIndex, new Object[]{new StartTime(), item, 
									new Duration(item.duration / 1000000)});
								diffTime -= item.duration / 1000;
							}
						}else{
							tries--;
							break;
						}
					}
					if(tries == 0){
						System.out.println("OUTCOME: Song lottery depleted.");
						break;
					}
					System.out.println("OUTCOME: No more lack of songs.");
				}
			} catch (IOException e) {
				e.printStackTrace(System.out);
			}
		}else if(nextSec.scheduledTime - curr > 300000L && nextSec.startTime - nextSec.scheduledTime > 120000L){
			System.out.println("PL PROBLEM: Too many songs.");
			// Ako ima viska pesama
			long diffTime = nextSec.startTime - nextSec.scheduledTime;
			while(true){
				item = model.getItemAt(endIndex);
				while(item.droppedToPL){
					endIndex--;
					if(endIndex < startIndex){
						insertOrRemoveSections(model);
						return;
					}
					item = model.getItemAt(endIndex);
				}
				if((nextSec.prioritet == 1 && diffTime > item.duration / 1000) || 
						(nextSec.prioritet != 1 && Math.abs(diffTime - (item.duration / 1000)) > item.duration / 1000)){
					model.removeRow(endIndex);
					System.out.println("PL ACTION: One song removed.");
					endIndex--;
					if(endIndex < startIndex) return;
					diffTime -= item.duration / 1000;
					if((endIndex < startIndex) || (diffTime <= 0)) return;
					if(endIndex == -1) break;
					continue;
				}else break;
			}
		}
		// TODO Ubacivanje prve i druge kategorije
	}

	private int findPlace(PLTableModel model, File file){
		String temp = file.getName();
		temp = temp.substring(temp.indexOf("-") + 1, temp.indexOf("."));
		if(Byte.valueOf(temp) <= cutOffPriority) return -1;
		temp = file.getName();
		temp = temp.substring(0, temp.indexOf("-"));
		long lowestTime = model.getStartTimeAt(1).time, time = Long.parseLong(temp);
		if(time < lowestTime) return -1;
		StartTime st;
		for(int i=2,len=model.getRowCount();i<len;i++){
			st = model.getStartTimeAt(i);
			if(time < st.time) return i + 1;
		}
		return -1;
	}

	private void insertOrRemoveSections(PLTableModel model){
		File files[] = null;
		try{
			files = getSortedFileList(new SecFileFilter());
		}catch(Exception e){
			e.printStackTrace(System.out);
			return;
		}
		int i, len, modelIndex;
		for(i=0,len=files.length;i<len;i++){
			modelIndex = findPlace(model, files[i]);
			if(modelIndex == -1) continue;
			insertSectionIntoPL(model, files[i], modelIndex);
		}
		// TODO Provera unetih termina
		// TODO Provera neunetih termina
		// TODO 
		// TODO 
		// TODO Implement
	}

	private void insertSectionIntoPL(PLTableModel model, File file, int index){
		try {
			ProgSection sec = ProgSection.load(file, null);
			model.insertRow(index++, sec.generateListJSection());
			for(int i=0,len=sec.songs.size();i<len;i++){
				model.insertRow(index++, sec.songs.get(i));
			}
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}

	private void addSectionsToPL(PLTableModel model, File files[]){
		if(model == null)
			model = PlayerWin.getPlayListModel();
		if(model.getRowCount() > 25) return;
		ListJSection last = null;
		for(int i=model.getRowCount()-1;i>=0;i--){
			if(model.getItemAt(i).isSection()){
				last = (ListJSection)model.getItemAt(i);
				break;
			}
		}
		if(last == null) last = PlayerWin.getInstance().currSection;
		if(files == null)
			files = getSortedFileList(new SecFileFilter());
		long startTime = last.scheduledTime;
		ProgSection sec;
		while(model.getRowCount() < 50){
			sec = findNextSection(startTime, files);
			addSection(model, sec);
			startTime = sec.scheduledTime;
		}
	}

	private void generateMore(){
		try{
			PLTableModel model = PlayerWin.getPlayListModel();
			if(model.getRowCount() > 20) return;
			PLGenerator generator = new PLGenerator();
			long begin = System.currentTimeMillis();
			generator.generate(begin, begin + 25200000L);
			// Pocisti snimljene termine
			File dir = new File("plists");
			File files[] = dir.listFiles(new SecFileFilter());
			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
			// Snimanje nove liste
			ProgSection sec;
			for(int i=0,len=generator.sections.size();i<len;i++){
				sec = generator.sections.get(i);
				ProgSection.save(sec);
			}
			System.out.println("NOTIFICATION: New 7 hours of program generated");
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
	}

}
