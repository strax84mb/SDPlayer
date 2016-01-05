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
import org.apache.lucene.index.DirectoryReader;
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
import org.apache.lucene.store.SimpleFSDirectory;

import rs.trznica.dragan.entities.struja.BasicEntity;

public abstract class GenericLuceneDao<T extends BasicEntity> {
	
	protected static final String FIELD_ID = "id";
	private Path indexDir;
	private Class<T> clazz;
	private Directory index;
	
	public GenericLuceneDao(String indexDir, Class<T> clazz) throws IOException {
		this.indexDir = Paths.get(indexDir);
		this.clazz = clazz;
		index = new SimpleFSDirectory(this.indexDir.resolve(Paths.get(this.clazz.getName())));
	}

	protected abstract T docToEntity(Document doc);

	protected abstract Document entityToDoc(T entity);
	
	protected IndexSearcher getSearcher() throws IOException {
		return new IndexSearcher(DirectoryReader.open(index));
	}
	
	protected IndexWriter getWriter() throws IOException {
		StandardAnalyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		return new IndexWriter(index, config);
	}
	
	public void save(T entity) throws IOException {
		if (entity.getId() == null) {
			entity.setId(getNewId());
		}
		Document doc = entityToDoc(entity);
		IndexWriter writer = getWriter();
		writer.addDocument(doc);
		writer.commit();
		writer.close();
	}
	
	public void update(T entity) throws IOException {
		Document doc = entityToDoc(entity);
		IndexWriter writer = getWriter();
		writer.updateDocument(new Term("id", doc.get("id")), doc);
		writer.commit();
		writer.close();
	}
	
	public T find(Long id) throws IOException {
		IndexSearcher searcher = getSearcher();
		TopDocs docs = searcher.search(new TermQuery(new Term(FIELD_ID, id.toString())), 1);
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
		TopDocs docs = searcher.search(new MatchAllDocsQuery(), 10);
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
		List<Query> queries = ids.stream().map(x -> new TermQuery(new Term(FIELD_ID, x.toString()))).collect(Collectors.toList());
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
		writer.deleteDocuments(new TermQuery(new Term(FIELD_ID, id.toString())));
		writer.commit();
		writer.close();
	}
	
	public Long getNewId() throws IOException {
		IndexSearcher searcher = getSearcher();
		Sort sort = new Sort(new SortField(FIELD_ID, Type.LONG));
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
