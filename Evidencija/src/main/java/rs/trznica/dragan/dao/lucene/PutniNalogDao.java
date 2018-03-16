package rs.trznica.dragan.dao.lucene;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.BytesRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import rs.trznica.dragan.entities.putninalog.PutniNalog;

@Repository
public class PutniNalogDao extends GenericLuceneDao<PutniNalog> {

	private static final String FIELD_PN_REDNI_BROJ = "redniBroj";
	private static final String FIELD_PN_ID_VOZILA = "idVozila";
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
	private static final String FIELD_PN_RADNA_ORG = "radnaOrganizacija";
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
		nalog.setIdVozila(Long.valueOf(doc.get(FIELD_PN_ID_VOZILA)));
		String namena = doc.get(FIELD_PN_NAMENA);
		nalog.setNamenaVozila(namena);
		nalog.setTipVozila(doc.get(FIELD_PN_TIP));
		nalog.setMarkaVozila(doc.get(FIELD_PN_MARKA));
		nalog.setRegOznaka(doc.get(FIELD_PN_RO));
		nalog.setVozac(doc.get(FIELD_PN_VOZAC));
		nalog.setRelacija(doc.get(FIELD_PN_RELACIJA));
		nalog.setDatum(doc.get(FIELD_PN_DATUM));
		nalog.setVrstaPrevoza(doc.get(FILED_PN_VRSTA_PREVOZA));
		nalog.setRadnaOrganizacija(doc.get(FIELD_PN_RADNA_ORG));
		nalog.setAdresaGaraze(doc.get(FIELD_PN_ADRESA));
		nalog.setMesto(doc.get(FIELD_PN_MESTO));
		if (PutniNalog.PUTNICKI.equals(namena)) {
			nalog.setSnagaMotora(Integer.valueOf(doc.get(FIELD_PN_SNAGA_MOTORA)));
			nalog.setBrojSedista(Integer.valueOf(doc.get(FIELD_PN_BROJ_SEDISTA)));
			nalog.setKorisnik(doc.get(FIELD_PN_KORISNIK));
		}
		if (PutniNalog.TERETNI.equals(namena)) {
			nalog.setTezina(Integer.valueOf(doc.get(FIELD_PN_TEZINA)));
			nalog.setNosivost(Integer.valueOf(doc.get(FIELD_PN_NOSIVOST)));
			nalog.setPosada(doc.get(FIELD_PN_POSADA));
		}
		return nalog;
	}

	@Override
	protected Document entityToDoc(PutniNalog entity) {
		Document doc = new Document();
		doc.add(new LongField(FIELD_ID, entity.getId(), ID_LONG_TYPE));
		doc.add(new StringField(FIELD_ID_TEXT, entity.getId().toString(), Store.YES));
		doc.add(new StringField(FIELD_PN_ID_VOZILA, entity.getIdVozila().toString(), Store.YES));
		doc.add(new StringField(FIELD_PN_REDNI_BROJ, paddZeros(entity.getRedniBroj().toString()), Store.YES));
		doc.add(new StringField(FIELD_PN_NAMENA, entity.getNamenaVozila(), Store.YES));
		doc.add(new StringField(FIELD_PN_TIP, entity.getTipVozila(), Store.YES));
		doc.add(new StringField(FIELD_PN_MARKA, entity.getMarkaVozila(), Store.YES));
		doc.add(new StringField(FIELD_PN_RO, entity.getRegOznaka(), Store.YES));
		doc.add(new StringField(FIELD_PN_VOZAC, entity.getVozac(), Store.YES));
		doc.add(new StringField(FIELD_PN_RELACIJA, entity.getRelacija(), Store.YES));
		doc.add(new StringField(FIELD_PN_DATUM, entity.getDatum(), Store.YES));
		doc.add(new SortedDocValuesField(FIELD_PN_DATUM, new BytesRef(entity.getDatum())));
		doc.add(new StringField(FILED_PN_VRSTA_PREVOZA, entity.getVrstaPrevoza(), Store.YES));
		doc.add(new StringField(FIELD_PN_RADNA_ORG, entity.getRadnaOrganizacija(), Store.YES));
		doc.add(new StringField(FIELD_PN_ADRESA, entity.getAdresaGaraze(), Store.YES));
		doc.add(new StringField(FIELD_PN_MESTO, entity.getMesto(), Store.YES));
		if (PutniNalog.PUTNICKI.equals(entity.getNamenaVozila())) {
			doc.add(new LongField(FIELD_PN_SNAGA_MOTORA, entity.getSnagaMotora(), ID_LONG_TYPE));
			doc.add(new LongField(FIELD_PN_BROJ_SEDISTA, entity.getBrojSedista(), ID_LONG_TYPE));
			doc.add(new StringField(FIELD_PN_KORISNIK, entity.getKorisnik(), Store.YES));
		}
		if (PutniNalog.TERETNI.equals(entity.getNamenaVozila())) {
			doc.add(new StringField(FIELD_PN_TEZINA, entity.getTezina().toString(), Store.YES));
			doc.add(new StringField(FIELD_PN_NOSIVOST, entity.getNosivost().toString(), Store.YES));
			doc.add(new StringField(FIELD_PN_POSADA, entity.getPosada(), Store.YES));
		}
		return doc;
	}

	@Override
	protected String getDocumentString(Document doc) {
		StringBuilder builder = new StringBuilder(4096);
		builder.append("\t").append(getField(doc, FIELD_ID_TEXT));
		builder.append("\t").append(getField(doc, FIELD_PN_REDNI_BROJ));
		builder.append("\t").append(getField(doc, FIELD_PN_ID_VOZILA));
		builder.append("\t").append(getField(doc, FIELD_PN_NAMENA));
		builder.append("\t").append(getField(doc, FIELD_PN_TIP));
		builder.append("\t").append(getField(doc, FIELD_PN_MARKA));
		builder.append("\t").append(getField(doc, FIELD_PN_RO));
		builder.append("\t").append(getField(doc, FIELD_PN_VOZAC));
		builder.append("\t").append(getField(doc, FIELD_PN_RELACIJA));
		builder.append("\t").append(getField(doc, FIELD_PN_DATUM));
		builder.append("\t").append(getField(doc, FILED_PN_VRSTA_PREVOZA));
		builder.append("\t").append(getField(doc, FIELD_PN_RADNA_ORG));
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
		doc.add(getStringField(FIELD_PN_ID_VOZILA, st.nextToken()));
		String namenaVozila = st.nextToken();
		doc.add(getStringField(FIELD_PN_NAMENA, namenaVozila));
		doc.add(getStringField(FIELD_PN_TIP, st.nextToken()));
		doc.add(getStringField(FIELD_PN_MARKA, st.nextToken()));
		doc.add(getStringField(FIELD_PN_RO, st.nextToken()));
		doc.add(getStringField(FIELD_PN_VOZAC, st.nextToken()));
		doc.add(getStringField(FIELD_PN_RELACIJA, st.nextToken()));
		doc.add(getStringField(FIELD_PN_DATUM, st.nextToken()));
		doc.add(getStringField(FILED_PN_VRSTA_PREVOZA, st.nextToken()));
		doc.add(getStringField(FIELD_PN_RADNA_ORG, st.nextToken()));
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
	
	public List<PutniNalog> getAll(Long vehicleId) throws IOException {
		IndexSearcher searcher = getSearcher();
		TopDocs docs = searcher.search(new TermQuery(new Term(FIELD_PN_ID_VOZILA, vehicleId.toString())), 
				Integer.MAX_VALUE, new Sort(new SortField(FIELD_PN_DATUM, Type.STRING, true)));
		List<PutniNalog> nalozi = new ArrayList<>();
		for (ScoreDoc scoreDoc : docs.scoreDocs) {
			nalozi.add(docToEntity(searcher.doc(scoreDoc.doc)));
		}
		searcher.getIndexReader().close();
		return nalozi;
	}

	public List<PutniNalog> getInInterval(Long vehicleId, Date start, Date end) throws IOException {
		IndexSearcher searcher = getSearcher();
		BooleanQuery.Builder mainQuery = new BooleanQuery.Builder()
				.add(new TermQuery(new Term(FIELD_PN_ID_VOZILA, vehicleId.toString())), Occur.MUST);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		if (start != null && end != null && start.getTime() == end.getTime()) {
			mainQuery.add(new TermQuery(new Term(FIELD_PN_DATUM, sdf.format(start))), Occur.MUST);
		} else {
			if (start != null) {
				mainQuery.add(TermRangeQuery.newStringRange(FIELD_PN_DATUM, sdf.format(start), "99999999", true, false), Occur.MUST);
			}
			if (end != null) {
				mainQuery.add(TermRangeQuery.newStringRange(FIELD_PN_DATUM, "00000000", sdf.format(end), false, true), Occur.MUST);
			}
		}
		TopDocs docs = searcher.search(mainQuery.build(), Integer.MAX_VALUE);
		List<PutniNalog> nalozi = new ArrayList<>();
		for (ScoreDoc scoreDoc : docs.scoreDocs) {
			nalozi.add(docToEntity(searcher.doc(scoreDoc.doc)));
		}
		searcher.getIndexReader().close();
		return nalozi;
	}
	
	private String paddZeros(String rb) {
		return String.format("%07d", Integer.parseInt(rb));
	}
	
	public Integer getLastRB(Long vehicleId) throws IOException {
		IndexSearcher searcher = getSearcher();
		if (searcher == null) {
			return null;
		}
		Query query = new TermQuery(new Term(FIELD_PN_ID_VOZILA, vehicleId.toString()));
		Sort sort = new Sort();
		sort.setSort(new SortField(FIELD_PN_POSADA, Type.STRING, true));
		TopDocs docs = searcher.search(query, 1, sort);
		String rb = null;
		if (docs.scoreDocs.length > 0) {
			rb = searcher.doc(
					docs.scoreDocs[0]
							.doc)
					.get(FIELD_PN_REDNI_BROJ);
		}
		searcher.getIndexReader().close();
		return rb == null ? 0 : Integer.parseInt(rb);
	}
}
