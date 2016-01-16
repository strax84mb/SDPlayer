package rs.trznica.dragan.forms;

import java.io.IOException;

import javax.swing.DefaultListModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import rs.trznica.dragan.dao.lucene.BrojiloDao;
import rs.trznica.dragan.dao.lucene.OcitavanjeDao;
import rs.trznica.dragan.entities.struja.Brojilo;
import rs.trznica.dragan.entities.support.BrojiloComparator;
import rs.trznica.dragan.forms.support.ModalResult;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ListaBrojilaForm extends GenericListForm<Brojilo> {

	private static final long serialVersionUID = -1223471707194373003L;

	private ApplicationContext ctx;
	private BrojiloDao brojiloDao;
	private OcitavanjeDao ocitavanjeDao;
	
	@Autowired
	public ListaBrojilaForm(ApplicationContext ctx) {
		super(ctx, "Želite li obrisati izabrano brojilo?");
	}

	@Override
	protected void autowireFields(ApplicationContext ctx) {
		this.ctx = ctx;
		brojiloDao = ctx.getBean(BrojiloDao.class);
		ocitavanjeDao = ctx.getBean(OcitavanjeDao.class);
	}

	@Override
	protected void populateList() {
		try {
			DefaultListModel<Brojilo> model = (DefaultListModel<Brojilo>)getObjectList().getModel();
			model.removeAllElements();
			brojiloDao.findAll().stream().sorted(new BrojiloComparator()).forEach(x -> model.addElement(x));
		} catch (IOException e) {
			e.printStackTrace();
			ErrorDialog dlg = new ErrorDialog();
			dlg.showError("Desila se greška prilikom čitanja svih brojila.");
		}
	}

	@Override
	protected StringBuilder objectToHtmlBody(Brojilo object) {
		StringBuilder builder = new StringBuilder("<p>");
		if (!object.getuFunkciji()) {
			builder.append("<b>Nije u funkciji</b><br/>");
		}
		builder.append("Broj: ").append(object.getBroj()).append("<br/>");
		builder.append("ED broj: ").append(object.getEd()).append("<br/>");
		if (object.getOpis() != null) {
			builder.append("Opis: ").append(object.getOpis()).append("<br/>");
		}
		builder.append("Tip brojila: ").append(object.getVrstaBrojila().getDescription());
		return builder;
	}

	@Override
	protected ModalResult performNewAction() {
		BrojiloForm form = ctx.getBean(BrojiloForm.class);
		form.setVisible(true);
		return form.getModalResult();
	}

	@Override
	protected ModalResult performEditAction() {
		BrojiloForm form = ctx.getBean(BrojiloForm.class);
		form.editObject(getObjectList().getSelectedValue());
		form.setVisible(true);
		return form.getModalResult();
	}

	@Override
	protected ModalResult performDeleteAction() {
		try {
			Long brojiloId = getObjectList().getSelectedValue().getId();
			if (ocitavanjeDao.countReadingsForCounter(brojiloId) == 0) {
				brojiloDao.delete(getObjectList().getSelectedValue().getId());
			} else {
				throw new Exception("Postoje očitavanja za izabrano brojilo.<br/>Prvo to treba obrisati.");
			}
			return ModalResult.YES;
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog dlg = new ErrorDialog();
			dlg.showError("Desila se greška prilikom brisanja: " + e.getMessage());
			return ModalResult.NO;
		}
	}
}
