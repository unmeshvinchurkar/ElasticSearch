package com.blaze.search.lucene;

import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import com.blaze.search.IIndexWriter;

import net.sf.json.JSONObject;

public class LuceneIndexWriter implements IIndexWriter {

	private IndexWriter indexWriter = null;
	private Directory directory = null;

	public LuceneIndexWriter(IndexWriter indexWriter, Directory directory) {

		this.indexWriter = indexWriter;
		this.directory = directory;
	}

	@Override
	public void addDocument(JSONObject json) throws IOException {

		Document doc = new Document();
		Iterator iter = json.keys();

		while (iter.hasNext()) {
			String key = (String) iter.next();
			String value = json.getString(key);
			Field field = new TextField(key, value, Store.YES);

			doc.add(field);
		}
		indexWriter.addDocument(doc);
		indexWriter.updateDocument(new Term("id", doc.get("id")), doc);

	}

	@Override
	public void close() {
		try {

			indexWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			if (!(directory instanceof RAMDirectory)) {
				directory.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void commit() {
		try {
			indexWriter.prepareCommit();
			indexWriter.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void unlock() {

	}

}
