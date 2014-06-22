package prog.paket.forme.pregled;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import prog.paket.dodaci.JPLayList;
import prog.paket.dodaci.ListJItem;
import prog.paket.playlist.generator.SoundPreviewDlg;

public class ActPreviewAction extends AbstractAction {

	private static final long serialVersionUID = -5394798405760000895L;

	private JPLayList listaPesama;

	public ActPreviewAction(JPLayList listaPesama){
		this.listaPesama = listaPesama;
		putValue(NAME, "Preslušaj");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		ListJItem item = listaPesama.getSelectedValue();
		if(item == null) return;
		new SoundPreviewDlg(item);
	}

}
