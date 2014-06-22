package prog.paket.forme.pregled;

public class TraitInfo {

	private Integer id;

	private String name;

	private String abrev;

	private Integer parentID;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbrev() {
		return abrev;
	}

	public void setAbrev(String abrev) {
		this.abrev = abrev;
	}

	public Integer getParentID() {
		return parentID;
	}

	public void setParentID(Integer parentID) {
		this.parentID = parentID;
	}

	public TraitInfo(Integer id, String name, String abrev, Integer parentID) {
		this.id = id;
		this.name = name;
		this.abrev = abrev;
		this.parentID = parentID;
	}

	@Override
	public boolean equals(Object obj) {
		if((obj != null) && (obj instanceof TraitInfo)){
			return ((TraitInfo)obj).getId().equals(id);
		}else return false;
	}

	@Override
	public String toString() {
		if(id.intValue() < 1)
			return name;
		else return name + " - " + abrev + ", ID: " + id.toString();
	}

}
