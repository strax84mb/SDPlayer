package prog.paket.baza.struct.menutree;

public class TreeCoord {

	static public enum PosValue {
		BEFORE,
		INTO,
		AFTER
	};

	public TraitNode node;

	public PosValue pos;

	public TreeCoord(TraitNode node, PosValue pos){
		this.node = node;
		this.pos = pos;
	}

}
