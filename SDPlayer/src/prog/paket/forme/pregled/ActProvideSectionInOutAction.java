package prog.paket.forme.pregled;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractAction;

import prog.paket.dodaci.JPLayList;
import prog.paket.dodaci.ListJItem;
import prog.paket.forme.reklame.ScheduledItemsType;
import prog.paket.playlist.generator.PlayerWin;
import prog.paket.playlist.generator.struct.CatWithList;
import prog.paket.playlist.generator.struct.ProgSection;
import prog.paket.playlist.generator.struct.ProgSectionType;

public class ActProvideSectionInOutAction extends AbstractAction {

	private static final long serialVersionUID = -4824084489303836017L;

	private ProgSectionList listaTermina;

	private JPLayList listaPesama;

	public ActProvideSectionInOutAction(ProgSectionList listaTermina, JPLayList listaPesama) {
		this.listaTermina = listaTermina;
		this.listaPesama = listaPesama;
		putValue(NAME, "Ubaci špice");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		int index = listaTermina.getSelectedIndex();
		if(index == -1) return;
		ProgSection sec = listaTermina.getSelectedValue();
		if(sec.startToon && sec.endToon) return;
		if(sec.sectionType == ProgSectionType.REKLAME){
			try {
				ScheduledItemsType type = ScheduledItemsType.load(new File(
						"mcats/" + sec.catName.toLowerCase() + ".rek"));
				if(!sec.startToon && (type.najava != null))
					sec.songs.add(0, type.najava);
				if(!sec.endToon && (type.odjava != null))
					sec.songs.add(type.odjava);
			} catch (IOException | UnsupportedAudioFileException e) {
				e.printStackTrace(System.out);
				PlayerWin.getErrDlg().showError(
						"Desila se greška tokom čitanja<br/>kategorije zadatog termina.");
				return;
			}
		}else if(sec.sectionType == ProgSectionType.TERMIN){
			CatWithList cwl = CatWithList.load(sec.catName);
			if(cwl == null){
				PlayerWin.getErrDlg().showError(
						"Desila se greška tokom čitanja<br/>kategorije zadatog termina.");
				return;
			}
			try {
				if(!sec.startToon && (cwl.cat.najavnaSpica != null))
					sec.songs.add(0, new ListJItem(cwl.cat.najavnaSpica));
				if(!sec.endToon && (cwl.cat.odjavnaSpica != null))
					sec.songs.add(new ListJItem(cwl.cat.odjavnaSpica));
			} catch (IOException | UnsupportedAudioFileException e) {
				e.printStackTrace(System.out);
				PlayerWin.getErrDlg().showError(
						"Desila se greška tokom čitanja<br/>kategorije zadatog termina.");
				return;
			}
		}
		listaPesama.getListModel().removeAllElements();
		for(int i=0,len=sec.songs.size();i<len;i++)
			listaPesama.getListModel().addElement(sec.songs.get(i));
	}

}
