package prog.paket.dodaci;

import java.util.EventObject;

import prog.paket.baza.struct.menutree.TraitChange;

public class ContentEvent extends EventObject {

	private static final long serialVersionUID = -8556099922567773166L;

	private TraitChange change;

	protected ContentEvent(Object source){
		super(source);
	}

	public ContentEvent(Object source, TraitChange change){
		this(source);
		this.change = change;
	}

	public TraitChange getChange(){
		return change;
	}

}
