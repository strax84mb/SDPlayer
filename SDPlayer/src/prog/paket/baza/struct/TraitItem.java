package prog.paket.baza.struct;

import javax.swing.Action;
import javax.swing.JMenuItem;

public class TraitItem extends JMenuItem implements SongTrait{

	private static final long serialVersionUID = -3890918925364742368L;

	private SongTrait parentTrait;

	public TraitItem(Action action, SongTrait parentTrait) {
		super(action);
		this.parentTrait = parentTrait;
		setIcon(SongTrait.noStateIcon);
	}

	public JMenuItem getThis(){
		return this;
	}

	@Override
	public int getID() {
		return -1;
	}

	@Override
	public String getAbrev() {
		return null;
	}

	@Override
	public String getTraitName() {
		return null;
	}

	@Override
	public SearchState getState() {
		return SearchState.NO_STATE;
	}

	@Override
	public void setState(SearchState state) {}

	@Override
	public int getSubTraitCount() {
		return 0;
	}

	@Override
	public SongTrait getSubTraitAt(int index) {
		return null;
	}

	@Override
	public SongTrait getTraitParent() {
		return parentTrait;
	}

	@Override
	public void add(SongTrait newTrait) {}

	@Override
	public void rename(String text, String abrev) {}

	@Override
	public void remove() {}

	@Override
	public void setTraitParent(SongTrait traitParent) {
		this.parentTrait = traitParent;
	}

	@Override
	public SongTrait getRootTrait() {
		return null;
	}

}
