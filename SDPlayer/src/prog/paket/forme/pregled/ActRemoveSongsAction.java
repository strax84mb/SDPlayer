package prog.paket.forme.pregled;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import prog.paket.dodaci.JPLayList;
import prog.paket.playlist.generator.PlayerWin;
import prog.paket.playlist.generator.struct.ProgSection;

public class ActRemoveSongsAction extends AbstractAction {

	private static final long serialVersionUID = 2200110320689740866L;

	private ProgSectionList listaTermina;

	private JPLayList listaPesama;

	public ActRemoveSongsAction(ProgSectionList listaTermina, JPLayList listaPesama){
		this.listaTermina = listaTermina;
		this.listaPesama = listaPesama;
		putValue(NAME, "Skloni pesme");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		int rows[] = listaPesama.getSelectedIndices();
		if((rows == null) || (rows.length == 0)){
			PlayerWin.getErrDlg().showError("Niste selektovali nijednu pesmu.");
			return;
		}else if(rows.length == listaPesama.getModel().getSize()){
			PlayerWin.getErrDlg().showError("Ovako bi ste obrisali sve pesme u terminu.");
			return;
		}
		ProgSection sec = listaTermina.getSelectedValue();
		if(sec == null) return;
		if(rows[0] == sec.songs.size()-1) sec.endToon = false;
		if(rows[rows.length-1] == 0) sec.startToon = false;
		for(int i=rows.length-1;i>=0;i--){
			sec.songs.remove(rows[i]);
			listaPesama.getListModel().removeElementAt(rows[i]);
		}
	}

}
