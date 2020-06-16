package rs.trznica.dragan.forms.actions;

import org.springframework.context.ApplicationContext;
import rs.trznica.dragan.dao.BrojiloRepository;
import rs.trznica.dragan.dao.OcitavanjeRepository;
import rs.trznica.dragan.dao.PutniNalogRepository;
import rs.trznica.dragan.dao.lucene.BrojiloDao;
import rs.trznica.dragan.dao.lucene.OcitavanjeDao;
import rs.trznica.dragan.dao.lucene.PutniNalogDao;
import rs.trznica.dragan.entities.putninalog.PutniNalogSql;
import rs.trznica.dragan.entities.struja.Brojilo;
import rs.trznica.dragan.entities.struja.BrojiloSql;
import rs.trznica.dragan.entities.struja.OcitavanjeSql;
import rs.trznica.dragan.forms.ErrorDialog;

import javax.persistence.Column;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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
			BrojiloRepository brojiloRepository = ctx.getBean(BrojiloRepository.class);
			List<Brojilo> brojila = ucitajBrojila();
			List<BrojiloSql> sqlBrojila = migrirajBrojila(brojiloRepository, brojila);
			migrirajOcitavanja(brojila, sqlBrojila);
			migrirajPutneNaloge();
			//ctx.getBean(BrojiloDao.class).exportAllToCvs("/home/sdobrijevic/temp.csv");
			new ErrorDialog().showError("Izvoza je uspe\u0161no obavljen.");
		} catch (Exception e) {
			e.printStackTrace();
			new ErrorDialog().showError("Desila se gre\u0161ka prilikom izvoza: " + e.getMessage());
		}
	}

	private List<Brojilo> ucitajBrojila() throws IOException {
		return ctx.getBean(BrojiloDao.class).findAll();
	}

	private List<BrojiloSql> migrirajBrojila(BrojiloRepository brojiloRepository, List<Brojilo> list) {
		return list.stream().map(br -> BrojiloSql.builder()
				.broj(br.getBroj())
				.ed(br.getEd())
				.opis(br.getOpis())
				.uFunkciji(br.getuFunkciji())
				.vrstaBrojila(br.getVrstaBrojila())
				.build()
		).map(br -> {
			Optional<BrojiloSql> brojilo = brojiloRepository.findByBrojAndEd(br.getBroj(), br.getEd());
			if (brojilo.isPresent()) {
				return brojilo.get();
			} else {
				return brojiloRepository.save(br);
			}
		}).collect(Collectors.toList());
	}

	private void migrirajOcitavanja(List<Brojilo> brojila, List<BrojiloSql> sqlBrojila) throws IOException {
		OcitavanjeRepository repo = ctx.getBean(OcitavanjeRepository.class);
		System.out.println("Currently saved: " + repo.count());
		AtomicLong al = new AtomicLong(0L);
		ctx.getBean(OcitavanjeDao.class).findAll().stream()
				.map(oc -> {
					OcitavanjeSql.OcitavanjeSqlBuilder builder = OcitavanjeSql.builder();
					builder.brojiloId(oc.getBrojiloId())
							.brojiloVrsta(oc.getBrojiloVrsta())
							.brojiloBroj(oc.getBrojiloBroj())
							.brojiloED(oc.getBrojiloED())
							.mesec(oc.getMesec());
					builder.kwVT(oc.getKwVT())
							.kwNT(oc.getKwNT())
							.cenaVT(oc.getCenaVT())
							.cenaNT(oc.getCenaNT());
					builder.pristup(oc.getPristup())
							.podsticaj(oc.getPodsticaj())
							.kwReaktivna(oc.getKwReaktivna())
							.cenaReaktivna(oc.getCenaKW());
					return builder.build();
				}).filter(oc -> !repo.findByBrojiloEDAndMesec(
							oc.getBrojiloED(),
							oc.getMesec()).isPresent())
				.forEach(oc -> {
					repo.save(oc);
					al.incrementAndGet();
				});
		System.out.println("Transfer count: " + al.get());
	}

	private void migrirajPutneNaloge() throws IOException {
		PutniNalogDao dao = ctx.getBean(PutniNalogDao.class);
		PutniNalogRepository repo = ctx.getBean(PutniNalogRepository.class);
		dao.findAll().stream()
				.map(pn -> {
					PutniNalogSql.PutniNalogSqlBuilder builder = PutniNalogSql.builder();
					builder.id(pn.getId())
							.redniBroj(pn.getRedniBroj())
							.idVozila(pn.getIdVozila())
							.namenaVozila(pn.getNamenaVozila())
							.tipVozila(pn.getTipVozila())
							.markaVozila(pn.getMarkaVozila())
							.regOznaka(pn.getRegOznaka());
					builder.snagaMotora(pn.getSnagaMotora())
							.brojSedista(pn.getBrojSedista())
							.tezina(pn.getTezina())
							.nosivost(pn.getNosivost())
							.vozac(pn.getVozac())
							.relacija(pn.getRelacija());
					builder.datum(pn.getDatum())
							.vrstaPrevoza(pn.getVrstaPrevoza())
							.korisnik(pn.getKorisnik())
							.posada(pn.getPosada())
							.radnaOrganizacija(pn.getRadnaOrganizacija())
							.adresaGaraze(pn.getAdresaGaraze())
							.mesto(pn.getMesto());
					return builder.build();
				}).filter(pn -> !repo.findByIdVozilaAndRedniBroj(pn.getIdVozila(), pn.getRedniBroj()).isPresent())
				.forEach(repo::save);
	}
}
