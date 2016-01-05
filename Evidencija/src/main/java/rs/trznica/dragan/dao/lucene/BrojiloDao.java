package rs.trznica.dragan.dao.lucene;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

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
	public BrojiloDao(@Value("index.dir") String indexDir) throws IOException {
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
		doc.add(new StringField(FIELD_ID, entity.getId().toString(), Store.YES));
		doc.add(new StringField(FIELD_BROJ, entity.getBroj(), Store.YES));
		doc.add(new StringField(FIELD_ED, entity.getEd(), Store.YES));
		doc.add(new StringField(FIELD_OPIS, entity.getOpis(), Store.YES));
		doc.add(new StringField(FIELD_U_FUNKCIJI, storeBoolean(entity.getuFunkciji()), Store.YES));
		doc.add(new StringField(FIELD_VRSTA, entity.getVrstaBrojila().name(), Store.YES));
		return doc;
	}

}
