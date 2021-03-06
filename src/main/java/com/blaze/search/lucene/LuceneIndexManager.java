package com.blaze.search.lucene;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import com.blaze.search.IIndexReader;
import com.blaze.search.IIndexWriter;
import com.blaze.search.IndexManager;
import com.blaze.search.SearchFactory;

public class LuceneIndexManager implements IndexManager {

	private Locale locale = Locale.US;

	public LuceneIndexManager(Locale locale) {

		if (locale != null) {
			this.locale = locale;
		}

	}

	public IIndexReader getIndexReader(String nameOrPath) throws IOException {

		Path path = Paths.get(nameOrPath);
		Directory index = FSDirectory.open(path);
		IndexReader reader = DirectoryReader.open(index);
		return new LuceneIndexReader(reader, index, SearchFactory.getAnalyzer(locale));
	}

	public IIndexWriter getIndexWriter(String nameOrPath) throws IOException {

		Directory directory = null;
		IndexWriter indexWriter = null;
		Analyzer analyzer = SearchFactory.getAnalyzer(locale);

		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setCommitOnClose(true);
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

		Path path = Paths.get(nameOrPath);

		directory = FSDirectory.open(path);
		indexWriter = new IndexWriter(directory, config);

		return new LuceneIndexWriter(indexWriter, directory);
	}

	@Override
	public void createIndex(String nameOrPath) {

		createIndex(nameOrPath, false);
	}

	@Override
	public void createIndex(String nameOrPath, boolean inMemory) {

		Directory directory = null;
		IndexWriter indexWriter = null;
		Analyzer analyzer = SearchFactory.getAnalyzer(locale);

		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setCommitOnClose(true);
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

		Path path = Paths.get(nameOrPath);

		try {
			if (inMemory) {
				if (!LuceneInMemoryIndexCache.exists(nameOrPath)) {
					directory = new RAMDirectory();
					LuceneInMemoryIndexCache.addIndexMemory(nameOrPath, (RAMDirectory) directory);
				} else {
					directory = LuceneInMemoryIndexCache.getIndexMemory(nameOrPath);
				}
			} else {
				directory = FSDirectory.open(path);
			}

			indexWriter = new IndexWriter(directory, config);
			indexWriter.prepareCommit();
			indexWriter.commit();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				indexWriter.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				directory.close();
			} catch (IOException e) {
			}

			try {
				analyzer.close();
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void deleteIndex(String nameOrPath) {

		if (!exists(nameOrPath)) {
			return;
		}

		if (LuceneInMemoryIndexCache.exists(nameOrPath)) {
			LuceneInMemoryIndexCache.deleteIndexMemory(nameOrPath);
			return;

		}

		Directory directory = null;
		IndexWriter indexWriter = null;
		Analyzer analyzer = SearchFactory.getAnalyzer(locale);

		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setCommitOnClose(true);
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		Path path = Paths.get(nameOrPath);

		try {
			directory = FSDirectory.open(path);
			indexWriter = new IndexWriter(directory, config);
			indexWriter.deleteAll();
			indexWriter.prepareCommit();
			indexWriter.commit();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				indexWriter.close();
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

	@Override
	public boolean exists(String nameOrPath) {

		if (LuceneInMemoryIndexCache.exists(nameOrPath)) {
			return true;
		}

		Directory directory = null;
		IndexReader reader = null;
		try {
			Path path = Paths.get(nameOrPath);
			directory = FSDirectory.open(path);
			reader = DirectoryReader.open(directory);

			reader.numDocs();
		} catch (Exception e) {
			return false;
		} finally {

			try {
				reader.close();
			} catch (Exception e) {
			}
			try {
				directory.close();
			} catch (IOException e) {
			}

		}
		return true;
	}

}
