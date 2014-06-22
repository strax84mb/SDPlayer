package prog.paket.baza.struct;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.MenuSelectionManager;
import javax.swing.plaf.synth.SynthMenuUI;

public class TraitMenu extends JMenu implements SongTrait, DragGestureListener{

	private static final long serialVersionUID = -4178736154456489510L;

	private SearchState state = SearchState.NO_STATE;

	private int id;

	private String abrev;

	private SongTrait parentTrait;

	public TraitMenu(int id, String text, String abrev, SongTrait parentTrait){
		super(text);
		this.id = id;
		this.abrev = abrev;
		this.parentTrait = parentTrait;
		setUI(new SynthMenuUI(){
			@Override
			protected void doClick(MenuSelectionManager msm) {
				getThis().doClick(0);
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getPoint().x < 28)
					cycleState();
			}
		});
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if((e.getKeyCode() == KeyEvent.VK_SPACE) && (e.getModifiers() == 0))
					cycleState();
			}
		});
		setIcon(SongTrait.noStateIcon);
		DragSource ds = new DragSource();
		ds.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
		setDropTarget(new DropTarget());
	}

	public void cycleState(){
		switch(state){
		case INCLUDE:
			state = SearchState.EXCLUDE;
			setIcon(SongTrait.excludeIcon);
			break;
		case EXCLUDE:
			state = SearchState.NO_STATE;
			setIcon(SongTrait.noStateIcon);
			break;
		case NO_STATE:
			state = SearchState.INCLUDE;
			setIcon(SongTrait.includeIcon);
			break;
		}
		((TraitMenuBar)getRootTrait()).dlg.writeOutAbrevs();
	}

	public JMenuItem getThis(){
		return this;
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public String getTraitName() {
		return getText();
	}

	@Override
	public String getAbrev() {
		return abrev;
	}

	@Override
	public SearchState getState() {
		return state;
	}

	@Override
	public void setState(SearchState state) {
		this.state = state;
		switch(state){
		case INCLUDE:
			setIcon(SongTrait.includeIcon);
			break;
		case EXCLUDE:
			setIcon(SongTrait.excludeIcon);
			break;
		case NO_STATE:
			setIcon(SongTrait.noStateIcon);
			break;
		}
	}

	@Override
	public int getSubTraitCount() {
		return getItemCount();
	}

	@Override
	public SongTrait getSubTraitAt(int index) {
		return (SongTrait)getItem(index);
	}

	@Override
	public SongTrait getTraitParent() {
		return parentTrait;
	}

	@Override
	public void add(SongTrait newTrait) {
		add((TraitMenu)newTrait);
		newTrait.setTraitParent(this);
	}

	public void add(TraitMenu menu){
		add(menu, getSubTraitCount());
		menu.setTraitParent(this);
	}

	public void add(TraitItem item){
		add((JMenuItem)item);
		item.setTraitParent(this);
	}

	@Override
	public void rename(String text, String abrev) {
		setText(text);
		this.abrev = abrev;
	}

	@Override
	public void remove() {
		if(parentTrait instanceof TraitMenuBar){
			TraitMenuBar bar = (TraitMenuBar)parentTrait;
			bar.remove(this);
			parentTrait = null;
		}else{
			TraitMenu menu = (TraitMenu)parentTrait;
			menu.remove(this);
			parentTrait = null;
		}
	}

	@Override
	public void setTraitParent(SongTrait traitParent) {
		this.parentTrait = traitParent;
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		dge.startDrag(null, new TransferableTrait(this));
	}

	@Override
	public SongTrait getRootTrait() {
		SongTrait temp = this;
		while(temp.getID() != 0){
			temp = temp.getTraitParent();
		}
		return temp;
	}

}
