package rs.trznica.dragan.dao.lucene;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import rs.trznica.dragan.entities.struja.Ocitavanje;
import rs.trznica.dragan.entities.struja.VrstaBrojila;

public class OcitavanjeDao extends GenericLuceneDao<Ocitavanje> {

	private static final String FIELD_BROJILO_ID = "brojiloId";
	private static final String FIELD_BROJILO_VRSTA = "brojiloVrsta";
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
	public OcitavanjeDao(@Value("index.dir") String indexDir) throws IOException {
		super(indexDir, Ocitavanje.class);
	}
	
	@Override
	protected Ocitavanje docToEntity(Document doc) {
		Ocitavanje ocitavanje = new Ocitavanje();
		ocitavanje.setId(Long.valueOf(doc.get(FIELD_ID)));
		ocitavanje.setBrojiloId(doc.get(FIELD_BROJILO_ID));
		ocitavanje.setBrojiloVrsta(VrstaBrojila.getForName(doc.get(FIELD_BROJILO_VRSTA)));
		ocitavanje.setMesec(doc.get(FIELD_MESEC));
		ocitavanje.setKwNT(Long.valueOf(doc.get(FIELD_KW_NT)));
		ocitavanje.setKwVT(Long.valueOf(doc.get(FIELD_KW_VT)));
		ocitavanje.setCenaNT(Long.valueOf(doc.get(FIELD_CENA_NT)));
		ocitavanje.setCenaVT(Long.valueOf(doc.get(FIELD_CENA_VT)));
		ocitavanje.setPristup(Long.valueOf(doc.get(FIELD_PRISTUP)));
		ocitavanje.setPodsticaj(Long.valueOf(doc.get(FIELD_PODSTICAJ)));
		if (VrstaBrojila.MAXIGRAF.equals(ocitavanje.getBrojiloId())) {
			ocitavanje.setKwReaktivna(Long.valueOf(doc.get(FIELD_KW_REAKT)));
			ocitavanje.setCenaKW(Long.valueOf(doc.get(FIELD_CENA_KW)));
		}
		return ocitavanje;
	}

	@Override
	protected Document entityToDoc(Ocitavanje entity) {
		Document doc = new Document();
		doc.add(new StringField(FIELD_ID, entity.getId().toString(), Store.YES));
		doc.add(new StringField(FIELD_BROJILO_ID, entity.getBrojiloId(), Store.YES));
		doc.add(new StringField(FIELD_BROJILO_VRSTA, entity.getBrojiloVrsta().name(), Store.YES));
		doc.add(new StringField(FIELD_MESEC, entity.getMesec(), Store.YES));
		doc.add(new LongField(FIELD_KW_NT, entity.getKwNT(), Store.YES));
		doc.add(new LongField(FIELD_KW_VT, entity.getKwVT(), Store.YES));
		doc.add(new LongField(FIELD_CENA_NT, entity.getCenaNT(), Store.YES));
		doc.add(new LongField(FIELD_CENA_VT, entity.getCenaVT(), Store.YES));
		doc.add(new LongField(FIELD_PRISTUP, entity.getPristup(), Store.YES));
		doc.add(new LongField(FIELD_PODSTICAJ, entity.getPodsticaj(), Store.YES));
		if (VrstaBrojila.MAXIGRAF.equals(entity.getBrojiloVrsta())) {
			doc.add(new LongField(FIELD_KW_REAKT, entity.getKwReaktivna(), Store.YES));
			doc.add(new LongField(FIELD_CENA_KW, entity.getCenaKW(), Store.YES));
		}
		return doc;
	}

}
