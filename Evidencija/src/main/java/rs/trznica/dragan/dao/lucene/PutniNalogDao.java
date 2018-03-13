package rs.trznica.dragan.dao.lucene;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import rs.trznica.dragan.entities.putninalog.PutniNalog;

@Repository
public class PutniNalogDao extends GenericLuceneDao<PutniNalog> {

	private static final String FIELD_PN_REDNI_BROJ = "redniBroj";
	private static final String FIELD_PN_NAMENA = "namenaVozila";
	private static final String FIELD_PN_TIP = "tipVozila";
	private static final String FIELD_PN_MARKA = "markaVozila";
	private static final String FIELD_PN_RO = "regOznaka";
	private static final String FIELD_PN_SNAGA_MOTORA = "snagaMotora";
	private static final String FIELD_PN_BROJ_SEDISTA = "brojSedista";
	private static final String FIELD_PN_TEZINA = "tezina";
	private static final String FIELD_PN_NOSIVOST = "nosivost";
	private static final String FIELD_PN_VOZAC = "vozac";
	private static final String FIELD_PN_RELACIJA = "relacija";
	private static final String FIELD_PN_DATUM = "datum";
	private static final String FILED_PN_VRSTA_PREVOZA = "vrstaPrevoza";
	private static final String FIELD_PN_KORISNIK = "korisnik";
	private static final String FIELD_PN_POSADA = "posada";
	private static final String FIELD_PN_ADRESA = "adresaGaraze";
	private static final String FIELD_PN_MESTO = "mesto";
	
	@Autowired
	public PutniNalogDao(@Value("${index.dir}") String indexDir) throws IOException {
		super(indexDir, PutniNalog.class);
	}
	
	@Override
	protected PutniNalog docToEntity(Document doc) {
		PutniNalog nalog = new PutniNalog();
		nalog.setId(Long.valueOf(doc.get(FIELD_ID)));
		nalog.setRedniBroj(Long.valueOf(doc.get(FIELD_PN_REDNI_BROJ)));
		nalog.setNamenaVozila(doc.get(FIELD_PN_NAMENA));
		nalog.setTipVozila(doc.get(FIELD_PN_TIP));
		nalog.setMarkaVozila(doc.get(FIELD_PN_MARKA));
		nalog.setRegOznaka(doc.get(FIELD_PN_RO));
		nalog.setSnagaMotora(Integer.valueOf(doc.get(FIELD_PN_SNAGA_MOTORA)));
		nalog.setBrojSedista(Integer.valueOf(doc.get(FIELD_PN_BROJ_SEDISTA)));
		nalog.setTezina(doc.get(FIELD_PN_TEZINA));
		nalog.setNosivost(doc.get(FIELD_PN_NOSIVOST));
		nalog.setVozac(doc.get(FIELD_PN_VOZAC));
		nalog.setRelacija(doc.get(FIELD_PN_RELACIJA));
		nalog.setDatum(doc.get(FIELD_PN_DATUM));
		nalog.setVrstaPrevoza(doc.get(FILED_PN_VRSTA_PREVOZA));
		nalog.setKorisnik(doc.get(FIELD_PN_KORISNIK));
		nalog.setPosada(doc.get(FIELD_PN_POSADA));
		nalog.setAdresaGaraze(doc.get(FIELD_PN_ADRESA));
		nalog.setMesto(doc.get(FIELD_PN_MESTO));
		return nalog;
	}

	@Override
	protected Document entityToDoc(PutniNalog entity) {
		Document doc = new Document();
		doc.add(new LongField(FIELD_ID, entity.getId(), ID_LONG_TYPE));
		doc.add(new StringField(FIELD_ID_TEXT, entity.getId().toString(), Store.YES));
		doc.add(new StringField(FIELD_PN_REDNI_BROJ, entity.getRedniBroj().toString(), Store.YES));
		doc.add(new StringField(FIELD_PN_NAMENA, entity.getNamenaVozila(), Store.YES));
		doc.add(new StringField(FIELD_PN_TIP, entity.getTipVozila(), Store.YES));
		doc.add(new StringField(FIELD_PN_MARKA, entity.getMarkaVozila(), Store.YES));
		doc.add(new StringField(FIELD_PN_RO, entity.getRegOznaka(), Store.YES));
		doc.add(new StringField(FIELD_PN_VOZAC, entity.getVozac(), Store.YES));
		doc.add(new StringField(FIELD_PN_RELACIJA, entity.getRelacija(), Store.YES));
		doc.add(new StringField(FIELD_PN_DATUM, entity.getDatum(), Store.YES));
		doc.add(new StringField(FILED_PN_VRSTA_PREVOZA, entity.getVrstaPrevoza(), Store.YES));
		doc.add(new StringField(FIELD_PN_ADRESA, entity.getAdresaGaraze(), Store.YES));
		doc.add(new StringField(FIELD_PN_MESTO, entity.getMesto(), Store.YES));
		if (PutniNalog.PUTNICKI.equals(entity.getNamenaVozila())) {
			doc.add(new LongField(FIELD_PN_SNAGA_MOTORA, entity.getSnagaMotora(), ID_LONG_TYPE));
			doc.add(new LongField(FIELD_PN_BROJ_SEDISTA, entity.getBrojSedista(), ID_LONG_TYPE));
			doc.add(new StringField(FIELD_PN_KORISNIK, entity.getKorisnik(), Store.YES));
		}
		if (PutniNalog.TERETNI.equals(entity.getNamenaVozila())) {
			doc.add(new StringField(FIELD_PN_TEZINA, entity.getTezina(), Store.YES));
			doc.add(new StringField(FIELD_PN_NOSIVOST, entity.getNosivost(), Store.YES));
			doc.add(new StringField(FIELD_PN_POSADA, entity.getPosada(), Store.YES));
		}
		return doc;
	}

	@Override
	protected String getDocumentString(Document doc) {
		StringBuilder builder = new StringBuilder(4096);
		builder.append("\t").append(getField(doc, FIELD_ID_TEXT));
		builder.append("\t").append(getField(doc, FIELD_PN_REDNI_BROJ));
		builder.append("\t").append(getField(doc, FIELD_PN_NAMENA));
		builder.append("\t").append(getField(doc, FIELD_PN_TIP));
		builder.append("\t").append(getField(doc, FIELD_PN_MARKA));
		builder.append("\t").append(getField(doc, FIELD_PN_RO));
		builder.append("\t").append(getField(doc, FIELD_PN_VOZAC));
		builder.append("\t").append(getField(doc, FIELD_PN_RELACIJA));
		builder.append("\t").append(getField(doc, FIELD_PN_DATUM));
		builder.append("\t").append(getField(doc, FILED_PN_VRSTA_PREVOZA));
		builder.append("\t").append(getField(doc, FIELD_PN_ADRESA));
		builder.append("\t").append(getField(doc, FIELD_PN_MESTO));
		if (PutniNalog.PUTNICKI.equals(getField(doc, FIELD_PN_NAMENA))) {
			builder.append("\t").append(getField(doc, FIELD_PN_SNAGA_MOTORA));
			builder.append("\t").append(getField(doc, FIELD_PN_BROJ_SEDISTA));
			builder.append("\t").append(getField(doc, FIELD_PN_KORISNIK));
		}
		if (PutniNalog.TERETNI.equals(getField(doc, FIELD_PN_NAMENA))) {
			builder.append("\t").append(getField(doc, FIELD_PN_TEZINA));
			builder.append("\t").append(getField(doc, FIELD_PN_NOSIVOST));
			builder.append("\t").append(getField(doc, FIELD_PN_POSADA));
		}
		return builder.toString();
	}

	@Override
	protected Document getDocumentFromString(String line) {
		StringTokenizer st = new StringTokenizer(line, "\t");
		String value = st.nextToken();
		Document doc = new Document();
		doc.add(getLongField(FIELD_ID, value));
		doc.add(getStringField(FIELD_ID_TEXT, value));
		doc.add(getStringField(FIELD_PN_REDNI_BROJ, st.nextToken()));
		String namenaVozila = st.nextToken();
		doc.add(getStringField(FIELD_PN_NAMENA, namenaVozila));
		doc.add(getStringField(FIELD_PN_TIP, st.nextToken()));
		doc.add(getStringField(FIELD_PN_MARKA, st.nextToken()));
		doc.add(getStringField(FIELD_PN_RO, st.nextToken()));
		doc.add(getStringField(FIELD_PN_VOZAC, st.nextToken()));
		doc.add(getStringField(FIELD_PN_RELACIJA, st.nextToken()));
		doc.add(getStringField(FIELD_PN_DATUM, st.nextToken()));
		doc.add(getStringField(FILED_PN_VRSTA_PREVOZA, st.nextToken()));
		doc.add(getStringField(FIELD_PN_ADRESA, st.nextToken()));
		doc.add(getStringField(FIELD_PN_MESTO, st.nextToken()));
		if (PutniNalog.PUTNICKI.equals(namenaVozila)) {
			doc.add(getLongField(FIELD_PN_SNAGA_MOTORA, st.nextToken()));
			doc.add(getLongField(FIELD_PN_BROJ_SEDISTA, st.nextToken()));
			doc.add(getStringField(FIELD_PN_KORISNIK, st.nextToken()));
		}
		if (PutniNalog.TERETNI.equals(namenaVozila)) {
			doc.add(getStringField(FIELD_PN_TEZINA, st.nextToken()));
			doc.add(getStringField(FIELD_PN_NOSIVOST, st.nextToken()));
			doc.add(getStringField(FIELD_PN_POSADA, st.nextToken()));
		}
		return doc;
	}

}
