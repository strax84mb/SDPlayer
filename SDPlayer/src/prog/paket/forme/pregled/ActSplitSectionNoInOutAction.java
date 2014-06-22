package prog.paket.forme.pregled;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import prog.paket.dodaci.JPLayList;
import prog.paket.playlist.generator.PlayerWin;
import prog.paket.playlist.generator.struct.ProgSection;

public class ActSplitSectionNoInOutAction extends AbstractAction {

	private static final long serialVersionUID = 4904498328350562418L;

	private ProgSectionList listaTermina;

	private JPLayList listaPesama;

	public ActSplitSectionNoInOutAction(ProgSectionList listaTermina, JPLayList listaPesama){
		this.listaTermina = listaTermina;
		this.listaPesama = listaPesama;
		putValue(NAME, "Podeli temnin");
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
					"Ovakvo deljenje bi ostavilo prvu polovinu<br/>samo sa najavnom špicom.");
			return;
		}
		if(nextSec.endToon && (songIndex == listaPesama.getModel().getSize() - 2)){
			PlayerWin.getErrDlg().showError(
					"Ovakvo deljenje bi ostavilo drugu polovinu<br/>samo sa odjavnom špicom.");
			return;
		}
		for(int i=songIndex,len=listaPesama.getModel().getSize();i<len;i++)
			nextSec.songs.add(sec.songs.remove(songIndex));
		listaTermina.getListModel().insertElementAt(nextSec, progIndex + 1);
		listaTermina.correctStartTimes();
		listaTermina.repaint();
	}

}
