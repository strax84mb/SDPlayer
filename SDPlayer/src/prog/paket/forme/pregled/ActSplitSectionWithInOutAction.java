package prog.paket.forme.pregled;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import prog.paket.dodaci.JPLayList;
import prog.paket.playlist.generator.PlayerWin;
import prog.paket.playlist.generator.struct.ProgSection;

public class ActSplitSectionWithInOutAction extends AbstractAction {

	private static final long serialVersionUID = -5276059076413055653L;

	private ProgSectionList listaTermina;

	private JPLayList listaPesama;

	public ActSplitSectionWithInOutAction(ProgSectionList listaTermina, JPLayList listaPesama){
		this.listaTermina = listaTermina;
		this.listaPesama = listaPesama;
		putValue(NAME, "Podeli temnin (poštujući špice)");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		int progIndex = listaTermina.getSelectedIndex();
		if(progIndex == -1) return;
		int songIndex = listaPesama.getSelectedIndex();
		if(songIndex < 1){
			PlayerWin.getErrDlg().showError(
					"Ovakvo deljenje bi ostavilo prvu polovinu<br/>bez ijednog zvučnog zapisa.");
			return;
		}
		ProgSection sec = listaTermina.getSelectedValue();
		ProgSection nextSec = sec.clone();
		sec.endToon = false;
		nextSec.startToon = false;
		if(sec.startToon && (songIndex == 1)){
			PlayerWin.getErrDlg().showError(
					"Ovakvo deljenje bi ostavilo prvu polovinu<br/>samo sa najavnom i odjavnom špicom.");
			return;
		}
		if(nextSec.endToon && (songIndex == listaPesama.getModel().getSize() - 2)){
			PlayerWin.getErrDlg().showError(
					"Ovakvo deljenje bi ostavilo drugu polovinu<br/>samo sa najavnom i odjavnom špicom.");
			return;
		}
		for(int i=songIndex,len=listaPesama.getModel().getSize();i<len;i++)
			nextSec.songs.add(sec.songs.remove(songIndex));
		if(sec.endToon)
			sec.songs.add(nextSec.songs.get(nextSec.songs.size() - 1));
		if(nextSec.startToon)
			nextSec.songs.add(sec.songs.get(0));
		listaTermina.getListModel().insertElementAt(nextSec, progIndex + 1);
		listaTermina.correctStartTimes();
		listaTermina.repaint();
	}

}
