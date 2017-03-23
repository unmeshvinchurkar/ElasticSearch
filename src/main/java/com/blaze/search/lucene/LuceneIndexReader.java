package com.blaze.search.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler.Operator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

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
	public String runQueryString(String queryString) throws IOException {

		Query query = null;

		try {
			query = queryParser.parse(queryString, SearchDocConstants.CONTENT);
		} catch (Exception e) {
			e.printStackTrace();
		}

		TopDocs hits = indexSearcher.search(query, 20);

		for (ScoreDoc sd : hits.scoreDocs) {
			Document d = indexSearcher.doc(sd.doc);
			System.out.println(d.toString());

		}

		return hits.toString();
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
