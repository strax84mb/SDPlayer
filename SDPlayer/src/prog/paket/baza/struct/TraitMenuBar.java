package prog.paket.baza.struct;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

import prog.paket.baza.BazaProzor;
import prog.paket.baza.TreeConfig;

public class TraitMenuBar extends JMenuBar implements SongTrait{

	private static final long serialVersionUID = -8748039038184418214L;

	private int primaryTrait = Integer.MIN_VALUE;
	private int defaultTrait = Integer.MIN_VALUE;

	SongTrait markedTrait = null;

	public BazaProzor dlg;

	public JTextField searchStr = new JTextField();
	public JComboBox<String> rankBox = new JComboBox<String>();

	public TraitMenuBar(BazaProzor dlg){
		this.dlg = dlg;
		rankBox.addItem("Sve");
		rankBox.addItem("1");
		rankBox.addItem("2");
		rankBox.addItem("3");
		rankBox.addItem("4");
		rankBox.addItem("5");
		rankBox.setSelectedIndex(0);
		add(searchStr);
		add(rankBox);
		JMenuItem menuItem = (JMenuItem)(new TraitItem(new ShowTraitsAction(), this));
		add(menuItem);
		add((JMenuItem)(new TraitItem(new ChangeTraitsAction(), this)));
		searchStr.addKeyListener(new TraitMenuTextKeyListener(menuItem));
		loadMenu();
	}

	public int getChoosenRank(){
		return (rankBox.getSelectedIndex() < 0)?0:rankBox.getSelectedIndex();
	}

	private class TraitMenuTextKeyListener extends KeyAdapter {
		private JMenuItem menuItem;
		public TraitMenuTextKeyListener(JMenuItem menuItem){
			this.menuItem = menuItem;
		}
		@Override
		public void keyPressed(KeyEvent e) {
			if((e.getKeyCode() == KeyEvent.VK_ENTER) && (e.getModifiers() == 0))
				menuItem.doClick();
		}
	}

	protected SongTrait findTraitByID(int id, SongTrait trait){
		SongTrait temp = null;
		for(int i=0,len=trait.getSubTraitCount();i<len;i++){
			temp = trait.getSubTraitAt(i);
			if(temp.getID() == id) return temp;
			if(temp.getSubTraitCount() > 0){
				temp = findTraitByID(id, temp);
				if(temp != null) return temp;
			}
		}
		return null;
	}

	public SongTrait getByID(int id){
		if(id == 0) return this;
		return findTraitByID(id, this);
	}

	protected SongTrait findParentOfID(int id, SongTrait trait){
		SongTrait temp = null;
		for(int i=0,len=trait.getSubTraitCount();i<len;i++){
			temp = trait.getSubTraitAt(i);
			if(temp.getID() == id) return trait;
			if(temp.getSubTraitCount() > 0){
				temp = findParentOfID(id, temp);
				if(temp != null) return temp;
			}
		}
		return null;
	}

	public SongTrait getParentOf(int id){
		return findParentOfID(id, this);
	}

	protected int getMaxTraitID(int maxID, SongTrait trait){
		SongTrait temp = null;
		int ret = 0;
		for(int i=0,len=trait.getSubTraitCount();i<len;i++){
			temp = trait.getSubTraitAt(i);
			if(temp.getID() > maxID) maxID = temp.getID();
			if(temp.getSubTraitCount() > 0){
				ret = getMaxTraitID(maxID, temp);
				if(ret > maxID) maxID = ret;
			}
		}
		return maxID;
	}

	public int getNextMaxID(){
		return getMaxTraitID(0, this) + 1;
	}

	@Override
	public int getID() {
		return 0;
	}

	@Override
	public String getTraitName() {
		return "";
	}

	@Override
	public String getAbrev() {
		return "";
	}

	@Override
	public SearchState getState() {
		return SearchState.NO_STATE;
	}

	@Override
	public void setState(SearchState state) {}

	@Override
	public int getSubTraitCount() {
		return getMenuCount() - 4;
	}

	@Override
	public SongTrait getSubTraitAt(int index) {
		return (SongTrait)getSubElements()[index];
	}

	@Override
	public SongTrait getTraitParent() {
		return null;
	}

	@Override
	public SongTrait getRootTrait(){
		return this;
	}

	private class ShowTraitsAction extends AbstractAction{
		private static final long serialVersionUID = 8786796647887570680L;
		public ShowTraitsAction(){
			putValue(NAME, "Prikaži izabrano");
		}
		@Override
		public void actionPerformed(ActionEvent ae) {
			dlg.filterDB();
		}
	}
	private class ChangeTraitsAction extends AbstractAction{
		private static final long serialVersionUID = 6094896547735755516L;
		public ChangeTraitsAction(){
			putValue(NAME, "Izmeni kategorije");
		}
		@Override
		public void actionPerformed(ActionEvent ae) {
			TreeConfig dlg = new TreeConfig();
			dlg.setVisible(true);
			if(dlg.isSaved){
				for(int i=0,len=getSubTraitCount();i<len;i++)
					getSubTraitAt(0).remove();
				loadMenu();
				validate();
			}
			dlg.dispose();
		}
	}

	@Override
	public void setTraitParent(SongTrait traitParent) {}

	@Override
	public void add(SongTrait newTrait) {
		addTrait((TraitMenu)newTrait);
	}

	protected void addTrait(TraitMenu menu){
		add(menu, getSubTraitCount());
		menu.setTraitParent(this);
	}

	@Override
	public void rename(String text, String abrev) {}

	@Override
	public void remove() {}

	protected void saveSongTrait(ObjectOutputStream oos, SongTrait trait) throws IOException{
		SongTrait temp = null;
		for(int i=0,len=trait.getSubTraitCount();i<len;i++){
			temp = trait.getSubTraitAt(i);
			// Snimanje
			oos.writeInt(temp.getID());
			oos.writeUTF(temp.getTraitName());
			oos.writeUTF(temp.getAbrev());
			oos.writeInt(temp.getTraitParent().getID());
			// Provera postojanja podkategorija
			if(temp.getSubTraitCount() > 0){
				saveSongTrait(oos, temp);
			}
		}
	}

	public void saveMenu(){
		try{
			FileOutputStream fos = new FileOutputStream("baza/structure/kat_list.dat");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeInt(primaryTrait);
			oos.writeInt(defaultTrait);
			saveSongTrait(oos, this);
			oos.close();
			fos.close();
		}catch(Exception e){e.printStackTrace();}
	}

	public void loadMenu(){
		try{
			File file = new File("baza/structure/kat_list.dat");
			if(!file.exists()) return;
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			int id, parentID;
			String name, abrev;
			SongTrait parentTrait;
			TraitMenu menu;
			try{
				primaryTrait = ois.readInt();
				defaultTrait = ois.readInt();
				while(true){
					id = ois.readInt();
					name = ois.readUTF();
					abrev = ois.readUTF();;
					parentID = ois.readInt();
					parentTrait = getByID(parentID);
					menu = new TraitMenu(id, name, abrev, parentTrait);
					parentTrait.add(menu);
				}
			}catch(EOFException eofe){}
			ois.close();
			fis.close();
		}catch(Exception e){e.printStackTrace();}
	}

	public void setMark(SongTrait trait){
		markedTrait = trait;
	}

	public SongTrait getMark(){
		return markedTrait;
	}

	public void addIncludeExcludeCats(List<Integer> incl, List<Integer> excl){
		checkTraitStates(getRootTrait(), incl, excl);
	}

	private void checkTraitStates(SongTrait trait, List<Integer> incl, List<Integer> excl){
		SongTrait temp = null;
		for(int i=0,len=trait.getSubTraitCount();i<len;i++){
			temp = trait.getSubTraitAt(i);
			if(temp.getState() == SearchState.INCLUDE)
				incl.add(temp.getID());
			if(temp.getState() == SearchState.EXCLUDE)
				excl.add(temp.getID());
			if(temp.getSubTraitCount() > 0)
				checkTraitStates(temp, incl, excl);
		}
	}

	public String getAbrevsForState(SearchState state){
		String ret = getAbrevsForChildStates(getRootTrait(), state, null);
		return (ret == null)?"":ret;
	}

	private String getAbrevsForChildStates(SongTrait trait, SearchState state, String str){
		SongTrait temp = null;
		for(int i=0,len=trait.getSubTraitCount();i<len;i++){
			temp = trait.getSubTraitAt(i);
			if(temp.getState() == state){
				if(str == null)
					str = temp.getAbrev();
				else str += ", " + temp.getAbrev();
			}
			if(temp.getSubTraitCount() > 0)
				str = getAbrevsForChildStates(temp, state, str);
		}
		return str;
	}

	private void setChildStates(SongTrait trait, SearchState state){
		SongTrait temp = null;
		for(int i=0,len=trait.getSubTraitCount();i<len;i++){
			temp = trait.getSubTraitAt(i);
			temp.setState(state);
			if(temp.getSubTraitCount() > 0)
				setChildStates(temp, state);
		}
	}

	public void setAllStates(SearchState state){
		setChildStates(this, state);
	}

	public int getPrimaryTraitID(){
		return primaryTrait;
	}

	public int getDefaultTraitID(){
		return defaultTrait;
	}

}
