package rs.trznica.dragan.dao.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import rs.trznica.dragan.entities.struja.Ocitavanje;
import rs.trznica.dragan.entities.struja.VrstaBrojila;
import rs.trznica.dragan.entities.support.OcitavanjeComparator;

@Repository
public class OcitavanjeDao extends GenericLuceneDao<Ocitavanje> {

	private static final String FIELD_BROJILO_ID = "brojiloId";
	private static final String FIELD_BROJILO_VRSTA = "brojiloVrsta";
	private static final String FIELD_BROJILO_BROJ = "brojiloBroj";
	private static final String FIELD_BROJILO_ED = "brojiloED";
	private static final String FIELD_MESEC = "mesec";
	private static final String FIELD_KW_NT = "kwNT";
	private static final String FIELD_KW_VT = "kwVT";
	private static final String FIELD_CENA_NT = "cenaNT";
	private static final String FIELD_CENA_VT = "cenaVT";
	private static final String FIELD_PRISTUP = "pristup";
	private static final String FIELD_PODSTICAJ = "podsticaj";
	private static final String FIELD_KW_REAKT = "kwReakt";
	private static final String FIELD_CENA_KW = "cenaKW";

	@Autowired
	public OcitavanjeDao(@Value("${index.dir}") String indexDir) throws IOException {
		super(indexDir, Ocitavanje.class);
	}
	
	@Override
	protected Ocitavanje docToEntity(Document doc) {
		Ocitavanje ocitavanje = new Ocitavanje();
		ocitavanje.setId(Long.valueOf(doc.get(FIELD_ID)));
		ocitavanje.setBrojiloId(Long.valueOf(doc.get(FIELD_BROJILO_ID)));
		ocitavanje.setBrojiloVrsta(VrstaBrojila.getForName(doc.get(FIELD_BROJILO_VRSTA)));
		ocitavanje.setBrojiloBroj(doc.get(FIELD_BROJILO_BROJ));
		ocitavanje.setBrojiloED(doc.get(FIELD_BROJILO_ED));
		ocitavanje.setMesec(doc.get(FIELD_MESEC));
		ocitavanje.setKwNT(Long.valueOf(doc.get(FIELD_KW_NT)));
		ocitavanje.setCenaNT(Long.valueOf(doc.get(FIELD_CENA_NT)));
		if (VrstaBrojila.SIR_POT_DVO.equals(ocitavanje.getBrojiloVrsta())) {
			ocitavanje.setKwVT(Long.valueOf(doc.get(FIELD_KW_VT)));
			ocitavanje.setCenaVT(Long.valueOf(doc.get(FIELD_CENA_VT)));
		}
		ocitavanje.setPristup(Long.valueOf(doc.get(FIELD_PRISTUP)));
		ocitavanje.setPodsticaj(Long.valueOf(doc.get(FIELD_PODSTICAJ)));
		if (VrstaBrojila.MAXIGRAF.equals(ocitavanje.getBrojiloVrsta())) {
			ocitavanje.setKwReaktivna(Long.valueOf(doc.get(FIELD_KW_REAKT)));
			ocitavanje.setCenaKW(Long.valueOf(doc.get(FIELD_CENA_KW)));
		}
		return ocitavanje;
	}

	@Override
	protected Document entityToDoc(Ocitavanje entity) {
		Document doc = new Document();
		doc.add(new LongField(FIELD_ID, entity.getId(), ID_LONG_TYPE));
		doc.add(new StringField(FIELD_ID_TEXT, entity.getId().toString(), Store.YES));
		doc.add(new StringField(FIELD_BROJILO_ID, entity.getBrojiloId().toString(), Store.YES));
		doc.add(new StringField(FIELD_BROJILO_VRSTA, entity.getBrojiloVrsta().name(), Store.YES));
		doc.add(new StringField(FIELD_BROJILO_BROJ, entity.getBrojiloBroj(), Store.YES));
		doc.add(new StringField(FIELD_BROJILO_ED, entity.getBrojiloED(), Store.YES));
		doc.add(new StringField(FIELD_MESEC, entity.getMesec(), Store.YES));
		doc.add(new LongField(FIELD_KW_NT, entity.getKwNT(), ID_LONG_TYPE));
		doc.add(new LongField(FIELD_CENA_NT, entity.getCenaNT(), ID_LONG_TYPE));
		if (VrstaBrojila.SIR_POT_DVO.equals(entity.getBrojiloVrsta())) {
			doc.add(new LongField(FIELD_KW_VT, entity.getKwVT(), ID_LONG_TYPE));
			doc.add(new LongField(FIELD_CENA_VT, entity.getCenaVT(), ID_LONG_TYPE));
		}
		doc.add(new LongField(FIELD_PRISTUP, entity.getPristup(), ID_LONG_TYPE));
		doc.add(new LongField(FIELD_PODSTICAJ, entity.getPodsticaj(), ID_LONG_TYPE));
		if (VrstaBrojila.MAXIGRAF.equals(entity.getBrojiloVrsta())) {
			doc.add(new LongField(FIELD_KW_REAKT, entity.getKwReaktivna(), ID_LONG_TYPE));
			doc.add(new LongField(FIELD_CENA_KW, entity.getCenaKW(), ID_LONG_TYPE));
		}
		return doc;
	}

	public int countReadingsForCounter(Long counterId) throws IOException {
		IndexSearcher searcher = getSearcher();
		if (searcher == null) {
			return 0;
		}
		TopDocs docs = searcher.search(new TermQuery(new Term(FIELD_BROJILO_ID, counterId.toString())), Integer.MAX_VALUE);
		int ret = docs.totalHits;
		searcher.getIndexReader().close();
		return ret;
	}
	
	public List<Ocitavanje> findInInterval(List<Long> counterIds, String fromMonth, String toMonth) throws IOException {
		IndexSearcher searcher = getSearcher();
		Query monthQuery = TermRangeQuery.newStringRange(FIELD_MESEC, fromMonth, toMonth, true, true);
		BooleanQuery.Builder inclCountersBuilder = new BooleanQuery.Builder();
		for (Long counterId : counterIds) {
			inclCountersBuilder.add(new TermQuery(new Term(FIELD_BROJILO_ID, counterId.toString())), Occur.SHOULD);
		}
		BooleanQuery mainQuery = new BooleanQuery.Builder()
				.add(monthQuery, Occur.MUST)
				.add(inclCountersBuilder.build(), Occur.MUST)
				.build();
		TopDocs docs = searcher.search(mainQuery, Integer.MAX_VALUE);
		List<Ocitavanje> ret = new ArrayList<Ocitavanje>();
		for (ScoreDoc scoreDoc : docs.scoreDocs) {
			ret.add(docToEntity(searcher.doc(scoreDoc.doc)));
		}
		searcher.getIndexReader().close();
		ret.sort(new OcitavanjeComparator());
		return ret;
	} 
}
