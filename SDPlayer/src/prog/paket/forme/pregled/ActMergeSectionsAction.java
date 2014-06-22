package prog.paket.forme.pregled;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import prog.paket.dodaci.JPLayList;
import prog.paket.dodaci.ListJItem;
import prog.paket.playlist.generator.struct.ProgSection;

public class ActMergeSectionsAction extends AbstractAction {

	private static final long serialVersionUID = -3693361913199581819L;

	private ProgSectionList listaTermina;

	private JPLayList listaPesama;

	public ActMergeSectionsAction(ProgSectionList listaTermina, JPLayList listaPesama){
		this.listaTermina = listaTermina;
		this.listaPesama = listaPesama;
		putValue(NAME, "Spoj sa sledećim terminom");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		int index = listaTermina.getSelectedIndex();
		if((index == -1) || (index == listaTermina.getModel().getSize() - 1)) return;
		ProgSection sec = listaTermina.getSelectedValue();
		ProgSection nextSec = listaTermina.getModel().getElementAt(index + 1);
		ListJItem item = (sec.endToon)?sec.songs.remove(sec.songs.size()-1):null;
		if(nextSec.startToon)
			nextSec.songs.remove(0);
		if(nextSec.endToon)
			nextSec.songs.remove(nextSec.songs.size() - 1);
		for(int i=0,len=nextSec.songs.size();i<len;i++)
			sec.songs.add(nextSec.songs.get(i));
		if(item != null)
			sec.songs.add(item);
		listaTermina.getListModel().removeElementAt(index + 1);
		listaPesama.getListModel().removeAllElements();
		for(int i=0,len=sec.songs.size();i<len;i++)
			listaPesama.getListModel().addElement(sec.songs.get(i));
	}

}
