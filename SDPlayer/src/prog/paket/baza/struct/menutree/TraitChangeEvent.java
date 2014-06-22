package prog.paket.baza.struct.menutree;

import java.util.EventObject;

public class TraitChangeEvent extends EventObject {

	private static final long serialVersionUID = -3062775767310859429L;

	protected TraitChange change;

	protected TraitChangeEvent(Object source){
		super(source);
	}

	public TraitChangeEvent(TraitNode trait, TraitChange change){
		this(trait);
		this.change = change;
	}

	public TraitNode getTrait(){
		return (TraitNode)getSource();
	}

	public TraitChange getChangeType(){
		return change;
	}

	public void setChangeType(TraitChange change){
		this.change = change;
	}

}
