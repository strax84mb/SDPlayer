package prog.paket.baza.struct.menutree;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import prog.paket.baza.TraitDlg;
import prog.paket.baza.struct.menutree.TreeCoord.PosValue;
import prog.paket.playlist.generator.YesNoForm;

public class TraitPanel extends JPanel {

	private static final long serialVersionUID = -6470123626624213129L;

	private List<TraitListener> traitListeners = new ArrayList<TraitListener>();

	private TraitNode rootNode = new TraitNode(0, "Root", null);

	private boolean enteredDrag = false;
	private TraitNode draggedNode = null;
	private int dragX, dragY;
	private TreeCoord dragTC = null;
	private TraitNode clickedNode = null;
	public int middleColID = Integer.MIN_VALUE;
	public int defaultID = Integer.MIN_VALUE;

	private JPopupMenu popupMenu;

	public TraitNode getByID(TraitNode tn, int id){
		TraitNode temp, ret = null;
		for(int i=0,len=tn.getChildCount();i<len;i++){
			temp = tn.getChildAt(i);
			if(temp.getId() == id) return temp;
			if(temp.getChildCount() > 0){
				ret = getByID(temp, id);
				if(ret != null) return ret;
			}
		}
		return null;
	}

	private TraitPanel getThis(){
		return this;
	}

	public TraitNode getRootNode(){
		return rootNode;
	}

	public TraitPanel(){
		popupMenu = new JPopupMenu();
		popupMenu.add(new AddTraitAction());
		popupMenu.add(new RenameTraitAction());
		popupMenu.addSeparator();
		popupMenu.add(new DeleteTraitAction());
		popupMenu.addSeparator();
		popupMenu.add(new SetPrimaryTraitAction());
		popupMenu.add(new SetDefaultTraitAction());
		popupMenu.setInvoker(this);
		setBackground(Color.WHITE);
		addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				dragX = e.getX();
				dragY = e.getY();
				//prevDragTC = dragTC;
				dragTC = checkDragHoverFromFirstRow(dragX, dragY);
				//if(dragTC != null) System.out.println(dragTC.node.getId() + " : " + dragTC.node.getName());
				/*
				if((prevDragTC == null) && (dragTC != null)) repaint();
				else if((prevDragTC != null) && (dragTC == null)) repaint();
				else if((prevDragTC != null) && (dragTC != null) && prevDragTC.node.equals(dragTC.node))
					repaint();
					*/
				repaint();
				//dragTC = prevDragTC;
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				hitNode = checkMouseHover(rootNode, e.getX(), e.getY());
				if(hitNode != null){
					if(hitNode.equals(selectedNode)) return;
				}else if(selectedNode != null){
					if(selectedNode.equals(selectedNode)) return;
				}else return;
				selectedNode = hitNode;
				repaint();
				super.mouseMoved(e);
			}
			
		});
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if((draggedNode != null) && (dragTC != null)){
					switch(dragTC.pos){
					case BEFORE:
						if(draggedNode.equals(dragTC.node)) break;
						if(draggedNode.getParent().equals(dragTC.node.getParent())){
							int curr = draggedNode.getParent().getIndex(draggedNode);
							int index = dragTC.node.getParent().getIndex(dragTC.node);
							if(curr < index) index--;
							draggedNode.getParent().remove(draggedNode);
							dragTC.node.getParent().add(index, draggedNode);
						}else{
							draggedNode.getParent().remove(draggedNode);
							int index = dragTC.node.getParent().getIndex(dragTC.node);
							dragTC.node.getParent().add(index, draggedNode);
						}
						break;
					case AFTER:
						if(draggedNode.equals(dragTC.node)) break;
						if(draggedNode.getParent().equals(dragTC.node.getParent())){
							int curr = draggedNode.getParent().getIndex(draggedNode);
							int index = dragTC.node.getParent().getIndex(dragTC.node) + 1;
							if(curr < index) index--;
							draggedNode.getParent().remove(draggedNode);
							dragTC.node.getParent().add(index, draggedNode);
						}else{
							draggedNode.getParent().remove(draggedNode);
							int index = dragTC.node.getParent().getIndex(dragTC.node) + 1;
							dragTC.node.getParent().add(index, draggedNode);
						}
						break;
					case INTO:
						if(draggedNode.equals(dragTC.node)) break;
						if(draggedNode.getParent().equals(dragTC.node)) break;
						draggedNode.getParent().remove(draggedNode);
						dragTC.node.add(draggedNode);
						break;
					}
				}
				enteredDrag = false;
				draggedNode = null;
				dragTC = null;
				setCursor(Cursor.getDefaultCursor());
				repaint();
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if((hitNode != null) && (e.getButton() == MouseEvent.BUTTON1)){
					enteredDrag = true;
					draggedNode = hitNode;
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					return;
				}
				if((hitNode != null) && (e.getButton() == MouseEvent.BUTTON3)){
					clickedNode = hitNode;
					popupMenu.getComponent(0).setEnabled(true);
					popupMenu.getComponent(1).setEnabled(true);
					popupMenu.getComponent(3).setEnabled(true);
					popupMenu.getComponent(5).setEnabled(clickedNode.getId() != middleColID);
					popupMenu.getComponent(6).setEnabled(clickedNode.getId() != defaultID);
					popupMenu.setLocation(e.getLocationOnScreen());
					popupMenu.setVisible(true);
					return;
				}
				if((hitNode == null) && (e.getButton() == MouseEvent.BUTTON3)){
					clickedNode = null;
					popupMenu.getComponent(0).setEnabled(true);
					popupMenu.getComponent(1).setEnabled(false);
					popupMenu.getComponent(3).setEnabled(false);
					popupMenu.getComponent(5).setEnabled(false);
					popupMenu.getComponent(6).setEnabled(false);
					popupMenu.setLocation(e.getLocationOnScreen());
					popupMenu.setVisible(true);
					return;
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				if(draggedNode != null){
					enteredDrag = false;
					draggedNode = null;
					setCursor(Cursor.getDefaultCursor());
				}
			}
			
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if((hitNode != null) && (e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)){
					if(hitNode.getChildCount() > 0){
						if(hitNode.getChildAt(0).visible)
							hitNode.hideChildren();
						else hitNode.showChildren();
						repaint();
					}
				}
			}
		});
	}

	private TraitNode checkMouseHover(TraitNode tn, int x, int y){
		TraitNode temp;
		for(int i=0,len=tn.getChildCount();i<len;i++){
			temp = tn.getChildAt(i);
			if(temp.visible && (x >= temp.x) && (x <= temp.x + temp.width) && 
					(y >= temp.y) && (y <= temp.y + temp.height))
				return temp; 
			if((temp.getChildCount() > 0) && temp.getChildAt(0).visible){
				TraitNode ret = checkMouseHover(temp, x, y);
				if(ret != null) return ret;
			}
		}
		return null;
	}
	
	/*
	 * leva ivica [x-3, x+5]
	 * desna ivica [x-5, x+2]
	 */
	private TreeCoord checkDragHoverFromFirstRow(int x, int y){
		TraitNode temp;
		TreeCoord ret;
		for(int i=0,len=rootNode.getChildCount();i<len;i++){
			temp = rootNode.getChildAt(i);
			//if(temp.equals(draggedNode)) continue;
			if((x >= temp.x - 3) && (x <= temp.x + 5) && 
					(y >= temp.y + 2) && (y <= temp.y + temp.height - 2)){
				return new TreeCoord(temp, PosValue.BEFORE);
			}
			if((x >= temp.x + temp.width - 5) && (x <= temp.x + temp.width + 2) && 
					(y >= temp.y + 2) && (y <= temp.y + temp.height - 2)){
				return new TreeCoord(temp, PosValue.AFTER);
			}
			if((x >= temp.x + 6) && (x <= temp.x + temp.width - 6) && 
					(y >= temp.y + 2) && (y <= temp.y + temp.height - 2)){
				return new TreeCoord(temp, PosValue.INTO);
			}
			if((temp.getChildCount() > 0) && temp.getChildAt(0).visible){
				ret = checkDragHover(temp, x, y);
				if(ret != null) return ret;
			}
		}
		return null;
	}

	/*
	 * gornja ivica [y-3, y+5]
	 * donja ivica [y-5, y+2]
	 */
	private TreeCoord checkDragHover(TraitNode tn, int x, int y){
		TraitNode temp;
		TreeCoord ret;
		for(int i=0,len=tn.getChildCount();i<len;i++){
			temp = tn.getChildAt(i);
			if(temp.equals(draggedNode)) continue;
			if((x >= temp.x + 2) && (x <= temp.x + temp.width - 2) && 
					(y >= temp.y - 3) && (y <= temp.y + 5)){
				return new TreeCoord(temp, PosValue.BEFORE);
			}
			if((x >= temp.x + 2) && (x <= temp.x + temp.width - 2) && 
					(y >= temp.y + temp.height - 5) && (y <= temp.y + temp.height + 2)){
				return new TreeCoord(temp, PosValue.AFTER);
			}
			if((x >= temp.x + 2) && (x <= temp.x + temp.width - 2) && 
					(y >= temp.y + 6) && (y <= temp.y + temp.height  - 6)){
				return new TreeCoord(temp, PosValue.INTO);
			}
			if((temp.getChildCount() > 0) && temp.getChildAt(0).visible){
				ret = checkDragHover(temp, x, y);
				if(ret != null) return ret;
			}
		}
		return null;
	}

	private Font font = new Font("Times New Roman", Font.PLAIN, 14);
	private FontMetrics fm = null;
	private int tempX, tempY, height;
	private TraitNode node, selectedNode = null, hitNode = null;

	private int getMaxID(TraitNode tn, int max){
		TraitNode temp;
		for(int i=0,len=tn.getChildCount();i<len;i++){
			temp = tn.getChildAt(i);
			if(temp.getId() > max) max = temp.getId();
			if(temp.getChildCount() > 0){
				int ret = getMaxID(temp, max);
				if(ret > max) max = ret;
			}
		}
		return max;
	}

	private void calculateWidth(TraitNode tn){
		for(int i=0,length=tn.getChildCount();i<length;i++){
			tn.getChildAt(i).width = fm.stringWidth(tn.getChildAt(i).getName()) + 10;
			tn.getChildAt(i).height = height + 3;
			if(tn.getChildAt(i).getChildCount() > 0)
				calculateWidth(tn.getChildAt(i));
		}
	}

	private int getMaxTreeX(TraitNode tn, int maxX){
		if((tn.getChildCount() == 0) || !tn.getChildAt(0).visible)
			return maxX;
		TraitNode temp;
		for(int i=0,len=tn.getChildCount();i<len;i++){
			temp = tn.getChildAt(i);
			if(temp.x + temp.width > maxX)
				maxX = temp.x + temp.width;
			if((temp.getChildCount() > 0) && temp.getChildAt(0).visible){
				int ret = getMaxTreeX(temp, maxX);
				if(ret > maxX) maxX = ret;
			}
		}
		return maxX;
	}

	private void calculateFirstRowPos(){
		tempX = 5;
		tempY = 5;
		for(int i=0,len=rootNode.getChildCount();i<len;i++){
			node = rootNode.getChildAt(i);
			node.x = tempX;
			node.y = tempY;
			if((node.getChildCount() > 0) && node.getChildAt(0).visible)
				node.calculateChildNodesPos(node);
			tempX = getMaxTreeX(node, tempX + node.width);
			tempX += 5;
			node.visible = true;
		}
	}

	private void paintDropZone(Graphics2D g2d){
		g2d.setStroke(new BasicStroke(5));
		g2d.setColor(Color.RED);
		if(dragTC.node.getParent().equals(rootNode)){
			switch(dragTC.pos){
			case BEFORE:
				g2d.drawLine(dragTC.node.x - 3, dragTC.node.y - 2, 
						dragTC.node.x - 3, dragTC.node.y + dragTC.node.height + 2);
				break;
			case AFTER:
				g2d.drawLine(dragTC.node.x + dragTC.node.width + 3, dragTC.node.y - 2, 
						dragTC.node.x + dragTC.node.width + 3, dragTC.node.y + dragTC.node.height + 2);
				break;
			case INTO:
				g2d.drawRect(dragTC.node.x, dragTC.node.y, dragTC.node.width, dragTC.node.height);
				break;
			}
		}else{
			switch(dragTC.pos){
			case BEFORE:
				g2d.drawLine(dragTC.node.x - 2, dragTC.node.y - 3, 
						dragTC.node.x + dragTC.node.width + 2, dragTC.node.y -3);
				break;
			case AFTER:
				g2d.drawLine(dragTC.node.x - 2, dragTC.node.y + dragTC.node.height + 3, 
						dragTC.node.x + dragTC.node.width + 2, dragTC.node.y + dragTC.node.height + 3);
				break;
			case INTO:
				g2d.drawRect(dragTC.node.x, dragTC.node.y, dragTC.node.width, dragTC.node.height);
				break;
			}
		}
	}

	private void paintNodes(TraitNode tn, Graphics2D g2d){
		TraitNode temp;
		for(int i=0,len=tn.getChildCount();i<len;i++){
			temp = tn.getChildAt(i);
			if(temp.equals(selectedNode)){
				g2d.setColor(Color.BLUE);
				g2d.fillRect(temp.x, temp.y, temp.width, temp.height);
				g2d.setColor(Color.WHITE);
				g2d.drawString(temp.getName(), temp.x + 5, temp.y + temp.height - 5);
				g2d.setColor(Color.BLACK);
				g2d.setBackground(Color.WHITE);
			}else{
				g2d.drawRect(temp.x, temp.y, temp.width, temp.height);
				g2d.drawString(temp.getName(), temp.x + 5, temp.y + temp.height - 5);
			}
			if((temp.getChildCount() > 0) && temp.getChildAt(0).visible)
				paintNodes(temp, g2d);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		if(fm == null){
			g2d.setFont(font);
			fm = g2d.getFontMetrics(g2d.getFont());
			height = fm.getHeight();
			calculateWidth(rootNode);
		}
		g2d.setColor(Color.BLACK);
		g2d.setBackground(Color.WHITE);
		g2d.setStroke(new BasicStroke());
		calculateFirstRowPos();
		paintNodes(rootNode, g2d);
		g2d.setColor(Color.BLACK);
		if(enteredDrag){
			g2d.drawString(draggedNode.getName(), dragX, dragY);
			if(dragTC != null) paintDropZone(g2d);
		}
		setPreferredSize(new Dimension(tempX + 30, tempY + 30));
	}

	public void addTraitListener(TraitListener listener){
		traitListeners.add(listener);
	}

	public void fireTraitEvent(TraitChangeEvent event){
		for(int i=0,len=traitListeners.size();i<len;i++)
			traitListeners.get(i).traitChanged(event);
	}

	private class AddTraitAction extends AbstractAction {
		private static final long serialVersionUID = -1553658830458029699L;
		public AddTraitAction(){
			putValue(NAME, "Dodaj kategoriju");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			TraitDlg dlg = new TraitDlg();
			dlg.fillFields(null);
			dlg.setID(getMaxID(rootNode, 1) + 1);
			dlg.setVisible(true);
			if(dlg.shouldSave){
				TraitNode tn = new TraitNode(dlg.getID(), dlg.getTraitName(), dlg.getAbrev());
				tn.height = height + 3;
				tn.width = fm.stringWidth(tn.getName()) + 10;
				tn.visible = true;
				if(clickedNode == null){
					rootNode.add(tn);
				}else{
					if((clickedNode.getChildCount() > 0) && !clickedNode.getChildAt(0).visible){
						for(int i=0,len=clickedNode.getChildCount();i<len;i++)
							clickedNode.getChildAt(i).visible = true;
					}
					clickedNode.add(0, tn);
				}
				repaint();
				fireTraitEvent(new TraitChangeEvent(tn, TraitChange.ADDED));
			}
			dlg.dispose();
		}
	}
	private class RenameTraitAction extends AbstractAction {
		private static final long serialVersionUID = -2486535530086254440L;
		public RenameTraitAction(){
			putValue(NAME, "Preimenuj");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			TraitDlg dlg = new TraitDlg();
			dlg.fillFields(clickedNode);
			dlg.setVisible(true);
			if(dlg.shouldSave){
				clickedNode.setName(dlg.getTraitName());
				clickedNode.setAbrev(dlg.getAbrev());
				clickedNode.width = fm.stringWidth(clickedNode.getName()) + 10;
				repaint();
				fireTraitEvent(new TraitChangeEvent(clickedNode, TraitChange.EDITED));
			}
			dlg.dispose();
		}
	}
	private class DeleteTraitAction extends AbstractAction {
		private static final long serialVersionUID = 1328070759106396786L;
		public DeleteTraitAction(){
			putValue(NAME, "Obriši");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			YesNoForm form = new YesNoForm("Jeste li sigurni da želite obrisati kategoriju?");
			form.showDialogInCenter(getThis());
			if(form.confirmed){
				clickedNode.getParent().remove(clickedNode);
				repaint();
				fireTraitEvent(new TraitChangeEvent(clickedNode, TraitChange.REMOVED));
			}
			form.dispose();
			
		}
	}
	private class SetPrimaryTraitAction extends AbstractAction {
		private static final long serialVersionUID = -7155230955884347756L;
		public SetPrimaryTraitAction(){
			putValue(NAME, "Označi kao primarnu");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			middleColID = clickedNode.getId();
			fireTraitEvent(new TraitChangeEvent(clickedNode, TraitChange.SET_PRIMARY));			
		}
	}
	private class SetDefaultTraitAction extends AbstractAction {
		private static final long serialVersionUID = 7371291908945676006L;
		public SetDefaultTraitAction(){
			putValue(NAME, "Označi kao podrazumevanu");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			defaultID = clickedNode.getId();
			fireTraitEvent(new TraitChangeEvent(clickedNode, TraitChange.SET_DEFAULT));			
		}
	}
}
