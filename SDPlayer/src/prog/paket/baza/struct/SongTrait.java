package prog.paket.baza.struct;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public interface SongTrait {

	static public Icon includeIcon = new ImageIcon(SongTrait.class.getResource(
			"/prog/paket/baza/struct/add-icon.png"));
	static public Icon excludeIcon = new ImageIcon(SongTrait.class.getResource(
			"/prog/paket/baza/struct/close-icon.png"));
	static public Icon noStateIcon = new ImageIcon(SongTrait.class.getResource(
			"/prog/paket/baza/struct/no-state-icon.png"));

	public int getID();
	public String getTraitName();
	public String getAbrev();
	public SearchState getState();
	public void setState(SearchState state);
	public int getSubTraitCount();
	public SongTrait getSubTraitAt(int index);
	public SongTrait getTraitParent();
	public void setTraitParent(SongTrait traitParent);
	public void add(SongTrait newTrait);
	public void rename(String text, String abrev);
	public void remove();
	public SongTrait getRootTrait();

}
