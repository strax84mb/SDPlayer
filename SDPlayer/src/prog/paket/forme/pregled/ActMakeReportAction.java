package prog.paket.forme.pregled;

import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.AbstractAction;

import prog.paket.dodaci.JPLayList;
import prog.paket.dodaci.ListJItem;
import prog.paket.playlist.generator.GenerationReportDlg;
import prog.paket.playlist.generator.struct.ProgSection;

public class ActMakeReportAction extends AbstractAction {

	private static final long serialVersionUID = -8135881812771518854L;

	private ProgSectionList listaTermina;

	private JPLayList listaPesama;

	public ActMakeReportAction(ProgSectionList listaTermina, JPLayList listaPesama){
		this.listaTermina = listaTermina;
		this.listaPesama = listaPesama;
		putValue(NAME, "Prikaži izveštaj");
	}

	private List<Long> getTimes(String path){
		List<Long> ret = new ArrayList<Long>();
		ProgSection sec;
		ListJItem item;
		long time;
		for(int i=0,len=listaTermina.getModel().getSize();i<len;i++){
			sec = listaTermina.getModel().getElementAt(i);
			time = sec.startTime;
			for(int j=0,jLen=sec.songs.size();j<jLen;j++){
				item = sec.songs.get(j);
				if(item.fullPath.equals(path))
					ret.add(Long.valueOf(time));
				time += item.duration / 1000;
				if(sec.crossfade && (j == jLen - 1) && !sec.endToon){
					time -= 3000;
					continue;
				}
				if(sec.crossfade && (j > 0) && !sec.startToon)
					time -= 3000;
			}
		}
		return ret;
	}

	private String formatStartTime(int time){
		if(time == -1) return "";
		String ret = String.valueOf(time % 60);
		if(ret.length() == 1) ret = "0" + ret;
		time /= 60;
		ret = String.valueOf(time % 60) + ":" + ret;
		if(time / 60 == 0) return ret;
		if(ret.length() == 4) ret = "0" + ret;
		ret = String.valueOf(time / 60) + ":" + ret;
		return ret;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(listaPesama.getSelectedIndex() == -1) return;
		ListJItem selItem = listaPesama.getSelectedValue();
		List<Long> times = getTimes(selItem.fullPath);
		Calendar cal = new GregorianCalendar();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy.");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		long sum = 0, day = 0;
		int count =0;
		boolean first = false;
		String str = "<html><table border='2'><tr><th colspan='3'>Zastupljenost zvučnog zapisa:<br/>" + 
				selItem.fileName + 
				"</th></tr><tr><th>Datum</th><th>Vremena</th><th>Prosečan interval</th></tr>";
		for(int i=0,len=times.size();i<len;i++){
			if(times.get(i).longValue() > day){
				if(day > 0){
					str += "</td><td align='right'>";
					if(count > 0) str += formatStartTime((int)(sum / 1000 / count));
					str += "</td></tr>";
				}
				cal.setTimeInMillis(times.get(i));
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				day = cal.getTimeInMillis() + 86400000L;
				first = true;
				sum = 0;
				count = 0;
				str += "<tr><td>" + dateFormat.format(cal.getTime()) + "</td><td align='right'>";
			}
			if(!first){
				count++;
				sum += times.get(i) - times.get(i-1);
				str += "<br/>";
			}else first = false;
			cal.setTimeInMillis(times.get(i));
			str += timeFormat.format(cal.getTime());
		}
		if(day > 0){
			str += "</td><td align='right'>";
			if(count > 0) str += formatStartTime((int)(sum / 1000 / count));
			str += "</td></tr></table></html>";
		}
		new GenerationReportDlg(str);
	}

}
