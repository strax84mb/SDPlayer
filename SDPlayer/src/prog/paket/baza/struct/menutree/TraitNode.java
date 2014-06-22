package prog.paket.baza.struct.menutree;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

public class TraitNode implements TreeNode{

	private int id;

	private String name;

	private String abrev;

	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	private ArrayList<TraitNode> children = new ArrayList<TraitNode>();

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj instanceof TraitNode){
			return ((TraitNode) obj).getId() == getId();
		}else return false;
	}

	@Override
	public String toString() {
		return getName();
	}

	private TraitNode parent;

	@Override
	public TraitNode getParent() {
		return parent;
	}

	public void setParent(TraitNode parent){
		this.parent = parent;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Enumeration children() {
		return null;
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public TraitNode getChildAt(int childIndex) {
		return children.get(childIndex);
	}

	@Override
	public int getChildCount() {
		return children.size();
	}

	@Override
	public int getIndex(TreeNode node) {
		if(node instanceof TraitNode){
			for(int i=0,len=children.size();i<len;i++){
				if(((TraitNode)node).getId() == children.get(i).getId())
					return i;
			}
			return -1;
		}else return -1;
	}

	@Override
	public boolean isLeaf() {
		return children.size() == 0;
	}

	public TraitNode(int id, String name, String abrev) {
		super();
		this.id = id;
		this.name = name;
		this.abrev = abrev;
	}

	public void add(TraitNode node){
		children.add(node);
		node.setParent(this);
	}

	public void add(int index, TraitNode node){
		children.add(index, node);
		node.setParent(this);
	}

	public TraitNode remove(int index){
		TraitNode ret = children.remove(index);
		ret.setParent(null);
		return ret;
	}

	public void remove(TraitNode node){
		int i = 0, len = children.size();
		for(;i<len;i++){
			if(((TraitNode)node).getId() == children.get(i).getId())
				break;
		}
		if(i < len){
			TraitNode ret = children.remove(i);
			ret.setParent(null);
		}
	}

	public int width;

	public int height;

	public boolean visible = false;

	public int calculateChildNodesPos(TraitNode tn){
		TraitNode temp = tn.getChildAt(0);
		int nextY = tn.y + 5 + height;
		for(int i=0,len=tn.getChildCount();i<len;i++){
			temp = tn.getChildAt(i);
			//temp.visible = true;
			temp.x = tn.x + 25;
			temp.y = nextY;
			if((temp.getChildCount() > 0) && temp.getChildAt(0).visible){
				nextY = calculateChildNodesPos(temp);
			}else nextY = tn.getChildAt(i).y + 5 + height;
		}
		return nextY;
	}

	public void showParentRows(TraitNode par, TraitNode child){
		TraitNode temp;
		for(int i=0,len=par.getChildCount();i<len;i++){
			temp = par.getChildAt(i);
			temp.visible = true;
			if((temp.getChildCount() > 0) && temp.getChildAt(0).visible && !temp.equals(child))
				temp.hideChildren();
		}
	}

	public void showChildren(){
		for(int i=0,len=getChildCount();i<len;i++){
			getChildAt(i).visible = true;
		}
		TraitNode temp = this;
		while(temp.getParent().getId() != 0)
			temp = temp.getParent();
		if(temp.getChildCount() > 0)
			calculateChildNodesPos(temp);
		//showParentRows(getParent(), this);
	}

	private void hideChildNodes(TraitNode tn){
		for(int i=0,len=tn.getChildCount();i<len;i++){
			tn.getChildAt(i).visible = false;
			if(tn.getChildAt(i).getChildCount() > 0)
				hideChildNodes(tn.getChildAt(i));
		}
	}

	public void hideChildren(){
		hideChildNodes(this);
		TraitNode temp = this;
		while(temp.getParent().getId() != 0)
			temp = temp.getParent();
		if(temp.getChildCount() > 0)
			calculateChildNodesPos(temp);
	}

	public int x;

	public int y;

}
