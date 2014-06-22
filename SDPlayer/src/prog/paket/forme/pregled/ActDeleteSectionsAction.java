package prog.paket.forme.pregled;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;


public class ActDeleteSectionsAction extends AbstractAction {

	private static final long serialVersionUID = -1434900462440403274L;

	private ProgSectionList listaTermina;

	public ActDeleteSectionsAction(ProgSectionList listaTermina){
		this.listaTermina = listaTermina;
		putValue(NAME, "Obriši termin");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		int index = listaTermina.getSelectedIndex();
		if(index == -1) return;
		listaTermina.getListModel().removeElementAt(index);
	}

}
