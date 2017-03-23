package com.blaze.search;

import java.io.IOException;

public interface IndexManager {

	public void createIndex(String name);

	public void deleteIndex(String indexName);

	public boolean exists(String indexName);

	public IIndexWriter getIndexWriter(String name)  throws IOException;

	public IIndexReader getIndexReader(String name)  throws IOException;

}
