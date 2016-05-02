package rs.trznica.dragan.forms.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.springframework.context.ApplicationContext;

import rs.trznica.dragan.dao.lucene.BrojiloDao;
import rs.trznica.dragan.forms.ErrorDialog;

public class ExportCountersActionListener extends AbstractAction {

	private static final long serialVersionUID = 3492345869267924986L;
	
	private ApplicationContext ctx;

	public ExportCountersActionListener(ApplicationContext ctx) {
		putValue(Action.NAME, "Izvezi sva merna mesta");
		this.ctx = ctx;
	}
	
	@Override
	public void actionPerformed(ActionEvent ev) {
		try {
			ctx.getBean(BrojiloDao.class).exportAllToCvs("F:/Prog/temp.csv");
			new ErrorDialog().showError("Izvoza je uspe\u0161no obavljen.");
		} catch (Exception e) {
			e.printStackTrace();
			new ErrorDialog().showError("Desila se gre\u0161ka prilikom izvoza: " + e.getMessage());
		}
	}

}
