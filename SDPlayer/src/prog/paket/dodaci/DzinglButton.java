package prog.paket.dodaci;

import java.io.File;

import javax.swing.JButton;

public class DzinglButton extends JButton {

	private static final long serialVersionUID = 617610229975805063L;

	private ListJItem item = null;

	public ListJItem getSoundFile(){
		return item;
	}

	public void setSoundFile(ListJItem item){
		this.item = item;
		if(item == null)
			setText("Mesto za masku");
		else setText(item.fileName);
	}

	public void setSoundFile(File file){
		try{
			item = new ListJItem(file);
			setText(item.fileName);
		}catch(Exception e){}
	}

}
