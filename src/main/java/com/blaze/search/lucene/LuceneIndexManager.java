package com.blaze.search.lucene;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.blaze.search.IIndexReader;
import com.blaze.search.IIndexWriter;
import com.blaze.search.IndexManager;

public class LuceneIndexManager implements IndexManager {

	public IIndexReader getIndexReader(String nameOrPath) throws IOException {

		Path path = Paths.get(nameOrPath);
		Directory index = FSDirectory.open(path);
		IndexReader reader = DirectoryReader.open(index);
		return new LuceneIndexReader(reader, index);
	}

	public IIndexWriter getIndexWriter(String nameOrPath) throws IOException {

		Directory directory = null;
		IndexWriter indexWriter = null;
		Analyzer analyzer = new StandardAnalyzer();

		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setCommitOnClose(true);
		config.setOpenMode( IndexWriterConfig.OpenMode.CREATE_OR_APPEND );

		Path path = Paths.get(nameOrPath);

		directory = FSDirectory.open(path);
		indexWriter = new IndexWriter(directory, config);

		return new LuceneIndexWriter(indexWriter, directory);
	}

	@Override
	public void createIndex(String nameOrPath) {

		Directory directory = null;
		IndexWriter indexWriter = null;
		Analyzer analyzer = new StandardAnalyzer();

		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setCommitOnClose(true);
		config.setOpenMode( IndexWriterConfig.OpenMode.CREATE_OR_APPEND );

		Path path = Paths.get(nameOrPath);

		try {
			directory = FSDirectory.open(path);
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

		Directory directory = null;
		IndexWriter indexWriter = null;
		Analyzer analyzer = new StandardAnalyzer();

		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setCommitOnClose(true);
		config.setOpenMode( IndexWriterConfig.OpenMode.CREATE_OR_APPEND );
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
