package rs.trznica.dragan.dao.lucene;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.FieldType.NumericType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import rs.trznica.dragan.entities.struja.BasicEntity;

public abstract class GenericLuceneDao<T extends BasicEntity> {
	
	protected static final String FIELD_ID = "id";
	protected static final String FIELD_ID_TEXT = "idText";
	protected static final FieldType ID_LONG_TYPE = new FieldType();
	static {
		ID_LONG_TYPE.setStored(true);
		ID_LONG_TYPE.setDocValuesType(DocValuesType.NUMERIC);
		ID_LONG_TYPE.setNumericType(NumericType.LONG);
		ID_LONG_TYPE.setIndexOptions(IndexOptions.DOCS);
		ID_LONG_TYPE.setTokenized(true);
		ID_LONG_TYPE.setOmitNorms(true);
		ID_LONG_TYPE.freeze();
	}
	private Path indexDir;
	private Class<T> clazz;
	private Directory index;
	
	public GenericLuceneDao(String indexDir, Class<T> clazz) throws IOException {
		this.indexDir = Paths.get(indexDir);
		this.clazz = clazz;
		index = FSDirectory.open(this.indexDir.resolve(Paths.get(this.clazz.getName())));
	}

	protected abstract T docToEntity(Document doc);

	protected abstract Document entityToDoc(T entity);
	
	protected IndexSearcher getSearcher() throws IOException {
		return (DirectoryReader.indexExists(index)) ?  new IndexSearcher(DirectoryReader.open(index)) : null;
	}
	
	protected IndexWriter getWriter() throws IOException {
		StandardAnalyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		return new IndexWriter(index, config);
	}
	
	public T save(T entity) throws IOException {
		if (entity.getId() == null) {
			entity.setId(getNewId());
		}
		Document doc = entityToDoc(entity);
		IndexWriter writer = getWriter();
		writer.addDocument(doc);
		writer.commit();
		writer.close();
		return entity;
	}
	
	public void update(T entity) throws IOException {
		Document doc = entityToDoc(entity);
		IndexWriter writer = getWriter();
		writer.updateDocument(new Term(FIELD_ID_TEXT, entity.getId().toString()), doc);
		writer.commit();
		writer.close();
	}
	
	public T find(Long id) throws IOException {
		IndexSearcher searcher = getSearcher();
		if (searcher == null) {
			return null;
		}
		TopDocs docs = searcher.search(new TermQuery(new Term(FIELD_ID_TEXT, id.toString())), 1);
		if (docs.totalHits == 0) {
			searcher.getIndexReader().close();
			return null;
		}
		Document doc = searcher.doc(docs.scoreDocs[0].doc);
		searcher.getIndexReader().close();
		return docToEntity(doc);
	}
	
	public List<T> findAll() throws IOException {
		IndexSearcher searcher = getSearcher();
		if (searcher == null) {
			return new ArrayList<T>();
		}
		TopDocs docs = searcher.search(new MatchAllDocsQuery(), Integer.MAX_VALUE);
		List<T> ret = new ArrayList<T>();
		for (ScoreDoc sDoc : docs.scoreDocs) {
			Document doc = searcher.doc(sDoc.doc);
			ret.add(docToEntity(doc));
		}
		searcher.getIndexReader().close();
		return ret;
	}
	
	public List<T> find(List<Long> ids) throws IOException {
		IndexSearcher searcher = getSearcher();
		if (searcher == null) {
			return new ArrayList<T>();
		}
		List<Query> queries = ids.stream().map(x -> new TermQuery(new Term(FIELD_ID_TEXT, x.toString()))).collect(Collectors.toList());
		TopDocs docs = searcher.search(new DisjunctionMaxQuery(queries, 1f), ids.size());
		List<T> ret = new ArrayList<T>();
		for (ScoreDoc sDoc : docs.scoreDocs) {
			Document doc = searcher.doc(sDoc.doc);
			ret.add(docToEntity(doc));
		}
		searcher.getIndexReader().close();
		return ret;
	}
	
	public void deleteAll() throws IOException {
		IndexWriter writer = getWriter();
		writer.deleteAll();
		writer.commit();
		writer.close();
	}
	
	public void delete(Long id) throws IOException {
		IndexWriter writer = getWriter();
		writer.deleteDocuments(new TermQuery(new Term(FIELD_ID_TEXT, id.toString())));
		writer.commit();
		writer.close();
	}
	
	public Long getNewId() throws IOException {
		IndexSearcher searcher = getSearcher();
		if (searcher == null) {
			return 1L;
		}
		Sort sort = new Sort(new SortField(FIELD_ID, Type.LONG, true));
		TopDocs docs = searcher.search(new MatchAllDocsQuery(), 1, sort);
		if (docs.totalHits == 0) {
			searcher.getIndexReader().close();
			return 1L;
		}
		Set<String> fieldsToLoad = new HashSet<String>();
		fieldsToLoad.add(FIELD_ID);
		Document doc = searcher.doc(docs.scoreDocs[0].doc, fieldsToLoad);
		Long maxID = Long.valueOf(doc.get(FIELD_ID));
		return maxID + 1;
		
	}
	
	protected String storeBoolean(Boolean value) {
		return (value) ? "1" : "0";
	}
	
	protected Boolean getBoolean(String value) {
		return "1".equals(value);
	}
}
