package prog.paket.dodaci;

public class PLItemsPackage {

	private int indicies[] = null;

	private JPLayList jPList = null;

	public int[] getIndicies() {
		return indicies;
	}

	public void setIndicies(int[] indicies) {
		this.indicies = indicies;
	}

	public JPLayList getjPList() {
		return jPList;
	}

	public void setjPList(JPLayList jPList) {
		this.jPList = jPList;
	}

	public PLItemsPackage() {}

	public PLItemsPackage(int[] indicies, JPLayList jPList) {
		this.indicies = indicies;
		this.jPList = jPList;
	}

}
