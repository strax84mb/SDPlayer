package rs.trznica.dragan.forms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import rs.trznica.dragan.dao.BrojiloRepository;
import rs.trznica.dragan.dao.OcitavanjeRepository;
import rs.trznica.dragan.entities.struja.BrojiloSql;
import rs.trznica.dragan.entities.support.BrojiloComparator;
import rs.trznica.dragan.forms.support.ModalResult;

import javax.swing.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ListaBrojilaForm extends GenericListForm<BrojiloSql> {

	private static final long serialVersionUID = -1223471707194373003L;

	private ApplicationContext ctx;
	private BrojiloRepository brojiloRepository;
	private OcitavanjeRepository ocitavanjeRepository;
	
	@Autowired
	public ListaBrojilaForm(ApplicationContext ctx) {
		super(ctx, "\u017Delite li obrisati izabrano merno mesto?");
	}

	@Override
	protected void autowireFields(ApplicationContext ctx) {
		this.ctx = ctx;
		brojiloRepository = ctx.getBean(BrojiloRepository.class);
		ocitavanjeRepository = ctx.getBean(OcitavanjeRepository.class);
	}

	@Override
	protected void populateList() {
		try {
			DefaultListModel<BrojiloSql> model = (DefaultListModel<BrojiloSql>)getObjectList().getModel();
			model.removeAllElements();
			brojiloRepository.findAll().stream().sorted(new BrojiloComparator()).forEach(x -> model.addElement(x));
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog dlg = new ErrorDialog();
			dlg.showError("Desila se gre\u0161ka prilikom \u010Ditanja svih mernih mesta.");
		}
	}

	@Override
	protected StringBuilder objectToHtmlBody(BrojiloSql object) {
		StringBuilder builder = new StringBuilder("<p>");
		if (!object.getUFunkciji()) {
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
			BrojiloSql selected = getObjectList().getSelectedValue();
			if (ocitavanjeRepository.countByBrojiloId(selected.getId()) == 0) {
				brojiloRepository.delete(selected.getId());
			} else {
				throw new Exception("Postoje o\u010Ditavanja za izabrano merno mesto.<br/>Prvo to treba obrisati.");
			}
			return ModalResult.YES;
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog dlg = new ErrorDialog();
			dlg.showError("Desila se gre\u0161ka prilikom brisanja: " + e.getMessage());
			return ModalResult.NO;
		}
	}
}
