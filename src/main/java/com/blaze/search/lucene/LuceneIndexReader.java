package com.blaze.search.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler.Operator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

import com.blaze.SearchDocConstants;
import com.blaze.search.IIndexReader;

public class LuceneIndexReader implements IIndexReader {

	private Directory directory = null;
	private IndexReader indexReader = null;
	private IndexSearcher indexSearcher = null;
	private Analyzer analyzer = new StandardAnalyzer();
	private StandardQueryParser queryParser = null;

	public LuceneIndexReader(IndexReader indexReader, Directory directory) {
		this.indexReader = indexReader;
		this.indexSearcher = new IndexSearcher(indexReader);
		this.queryParser = new StandardQueryParser(analyzer);
		this.directory = directory;

		queryParser.setDefaultOperator(Operator.OR);
		queryParser.setAllowLeadingWildcard(true);
	}

	@Override
	public List runQueryString(String queryString) throws IOException {

		Query query = null;
		List resultDocs = new ArrayList();

		try {
			query = queryParser.parse(queryString, SearchDocConstants.CONTENT);
		} catch (Exception e) {
			e.printStackTrace();
		}

		TopDocs hits = indexSearcher.search(query, 20);

		for (ScoreDoc sd : hits.scoreDocs) {
			Document doc = indexSearcher.doc(sd.doc);
			Map docMap = new HashMap();

			List fields = doc.getFields();

			docMap.put("score", String.valueOf(sd.score));
			docMap.put("id", sd.doc);

			for (int i = 0; i < fields.size(); i++) {
				IndexableField field = (IndexableField) fields.get(i);
				docMap.put(field.name(), field.stringValue());
			}
			resultDocs.add(docMap);
			System.out.println(doc.toString());
		}

		return resultDocs;
	}

	@Override
	public void close() {
		try {
			indexReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			directory.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
