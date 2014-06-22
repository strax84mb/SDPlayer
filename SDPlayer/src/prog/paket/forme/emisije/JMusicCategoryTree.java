package prog.paket.forme.emisije;

import java.awt.Color;
import java.awt.Component;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import prog.paket.dodaci.ListJItem;
import prog.paket.playlist.generator.struct.MusicCategory;

public class JMusicCategoryTree extends JTree {

	private static final long serialVersionUID = 8215037484666209163L;

	public JMusicCategoryTree(){
		super(new DefaultTreeModel(new DefaultMutableTreeNode("JTree")));
		setRootVisible(false);
		setDropMode(DropMode.ON);
		setCellRenderer(new MusicTreeCellRenderer());
	}

	private CategoryTreeNode getNodeByName(String name){
		DefaultTreeModel model = (DefaultTreeModel)getModel();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)model.getRoot();
		int len = node.getChildCount();
		if(len == 0) return null;
		for(int i=0;i<len;i++)
			if(((String)((CategoryTreeNode)node.getChildAt(i)).getUserObject())
					.toLowerCase().equals(name.toLowerCase()))
				return (CategoryTreeNode)node.getChildAt(i);
		return null;
	}

	public MusicCategory getMusicCat(String catName){
		CategoryTreeNode node = getNodeByName(catName);
		return (node == null)?null:node.getDesc();
	}

	public List<ListJItem> getMusicList(String catName){
		CategoryTreeNode node = getNodeByName(catName);
		return (node == null)?null:node.getSongList();
	}

	public List<ListJItem> getMusicList(String catName, int importance){
		CategoryTreeNode node = getNodeByName(catName);
		return (node == null)?null:
			((RankedTreeNode)node.getChildAt(importance)).getSongList();
	}

	public void remove(String catName){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)((DefaultTreeModel)getModel()).getRoot();
		int len = node.getChildCount();
		if(len == 0) return;
		for(int i=0;i<len;i++)
			if(((String)((CategoryTreeNode)node.getChildAt(i)).getUserObject()).equals(catName))
				node.remove(i);
	}

	public void remove(CategoryTreeNode node){
		((DefaultMutableTreeNode)node.getParent()).remove(node);
	}

	public void removeCategoryAt(int index){
		((DefaultMutableTreeNode)((DefaultTreeModel)getModel()).getRoot()).remove(index);
	}

	public void removeAllCategories(){
		((DefaultMutableTreeNode)((DefaultTreeModel)getModel()).getRoot()).removeAllChildren();
	}

	public CategoryTreeNode addCategory(String name){
		if(getNodeByName(name) != null) return null;
		CategoryTreeNode node = new CategoryTreeNode(name);
		((DefaultMutableTreeNode)((DefaultTreeModel)getModel()).getRoot()).add(node);
		return node;
	}

	public boolean editCategoryName(String oldName, String newName){
		if(getNodeByName(newName) != null) return false;
		CategoryTreeNode node = getNodeByName(oldName);
		node.setName(newName);
		node.getDesc().ime = newName;
		return true;
	}

	public void reload(String catName){
		DefaultTreeModel model = (DefaultTreeModel)getModel();
		if(catName == null)
			model.reload();
		else model.reload(getNodeByName(catName));
	}

	public void reloadByNode(TreeNode node){
		((DefaultTreeModel)getModel()).reload(node);
	}

	public CategoryTreeNode getSelectedCategory(){
		TreePath path = getLeadSelectionPath();
		if(path == null) return null;
		if(path.getLastPathComponent() instanceof CategoryTreeNode)
			return (CategoryTreeNode)path.getLastPathComponent();
		else return (CategoryTreeNode)((RankedTreeNode)path.getLastPathComponent()).getParent();
	}

	public TreePath getSelectionCatPath(CategoryTreeNode node){
		return new TreePath(new Object[]{((DefaultTreeModel)getModel()).getRoot(), node});
	}

	public int getCategoryCount(){
		return ((DefaultMutableTreeNode)getModel().getRoot()).getChildCount();
	}

	public CategoryTreeNode getCategoryNodeAt(int index){
		return (CategoryTreeNode)((DefaultMutableTreeNode)getModel().getRoot()).getChildAt(index);
	}

	private class MusicTreeCellRenderer extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = 4643008111587696054L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			Component comp = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
					row, hasFocus);
			if(sel || hasFocus) return comp;
			CategoryTreeNode node = null;
			if(leaf)
				node = (CategoryTreeNode)((RankedTreeNode)value).getParent();
			else if(((DefaultMutableTreeNode)value).isRoot())
				return comp;
			else node = (CategoryTreeNode)value;
			if(node.getDesc().trajanje == -1) return comp;
			long time = 0;
			if(node.getDesc().crossfade){
				for(int i=0,len=node.getSongList().size();i<len;i++){
					time += (node.getSongList().get(i).duration / 1000) * node.getSongList().get(i).rang;
					time -= 3000;
				}
			}else{
				for(int i=0,len=node.getSongList().size();i<len;i++)
					time += (node.getSongList().get(i).duration / 1000) * node.getSongList().get(i).rang;
			}
			if(node.getDesc().trajanje * 60000 > time + 120000){
				comp.setBackground(new Color(255, 255, 153));
				return comp;
			}
			if(node.getDesc().trajanje * 60000 < time - 120000){
				comp.setBackground(new Color(255, 228, 225));
				return comp;
			}
			return comp;
		}

	}

}
