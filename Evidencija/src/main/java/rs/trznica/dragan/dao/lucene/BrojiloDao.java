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
import org.springframework.util.StringUtils;

import rs.trznica.dragan.entities.struja.Brojilo;
import rs.trznica.dragan.entities.struja.VrstaBrojila;

@Repository
public class BrojiloDao extends GenericLuceneDao<Brojilo>{
	
	private static final String FIELD_BROJ = "broj";
	private static final String FIELD_ED = "ed";
	private static final String FIELD_OPIS = "opis";
	private static final String FIELD_U_FUNKCIJI = "uFunkciji";
	private static final String FIELD_VRSTA = "vrsta";
	
	@Autowired
	public BrojiloDao(@Value("${index.dir}") String indexDir) throws IOException {
		super(indexDir, Brojilo.class);
	}

	@Override
	protected Brojilo docToEntity(Document doc) {
		Brojilo brojilo = new Brojilo();
		brojilo.setId(Long.valueOf(doc.get(FIELD_ID)));
		brojilo.setBroj(doc.get(FIELD_BROJ));
		brojilo.setEd(doc.get(FIELD_ED));
		brojilo.setOpis(doc.get(FIELD_OPIS));
		brojilo.setuFunkciji(getBoolean(doc.get(FIELD_U_FUNKCIJI)));
		brojilo.setVrstaBrojila(VrstaBrojila.getForName(doc.get(FIELD_VRSTA)));
		return brojilo;
	}
	@Override
	protected Document entityToDoc(Brojilo entity) {
		Document doc = new Document();
		doc.add(new LongField(FIELD_ID, entity.getId(), ID_LONG_TYPE));
		doc.add(new StringField(FIELD_ID_TEXT, entity.getId().toString(), Store.YES));
		doc.add(new StringField(FIELD_BROJ, entity.getBroj(), Store.YES));
		doc.add(new StringField(FIELD_ED, entity.getEd(), Store.YES));
		if (!StringUtils.isEmpty(entity.getOpis())) {
			doc.add(new StringField(FIELD_OPIS, entity.getOpis(), Store.YES));
		}
		doc.add(new StringField(FIELD_U_FUNKCIJI, storeBoolean(entity.getuFunkciji()), Store.YES));
		doc.add(new StringField(FIELD_VRSTA, entity.getVrstaBrojila().name(), Store.YES));
		return doc;
	}

	@Override
	protected String getDocumentString(Document doc) {
		StringBuilder builder = new StringBuilder(getField(doc, FIELD_ID_TEXT));
		builder.append("\t").append(getField(doc, FIELD_BROJ));
		builder.append("\t").append(getField(doc, FIELD_ED));
		builder.append("\t").append(getField(doc, FIELD_OPIS));
		builder.append("\t").append(getField(doc, FIELD_U_FUNKCIJI));
		builder.append("\t").append(getField(doc, FIELD_VRSTA));
		return builder.toString();
	}

	@Override
	protected Document getDocumentFromString(String line) {
		StringTokenizer st = new StringTokenizer(line, "\t");
		String value = st.nextToken();
		Document doc = new Document();
		doc.add(getLongField(FIELD_ID, value));
		doc.add(getStringField(FIELD_ID_TEXT, value));
		doc.add(getStringField(FIELD_BROJ, st.nextToken()));
		doc.add(getStringField(FIELD_ED, st.nextToken()));
		value = st.nextToken();
		if (!StringUtils.isEmpty(value)) {
			doc.add(getStringField(FIELD_OPIS, value));
		}
		doc.add(getStringField(FIELD_U_FUNKCIJI, st.nextToken()));
		doc.add(getStringField(FIELD_VRSTA, st.nextToken()));
		return doc;
	}
}
