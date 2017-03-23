package com.blaze.search.elasticsearch;

import java.io.IOException;

import com.blaze.search.IIndexWriter;

import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import net.sf.json.JSONObject;

public class ESIndexWriter implements IIndexWriter {

	private String indexName = null;
	private JestClient client = null;

	public ESIndexWriter(String indexName, JestClient client) {
		this.indexName = indexName;
		this.client = client;
	}

	public void addDocument(JSONObject json) throws IOException {
		Index index = new Index.Builder(json).index(indexName).type(json.getString("type")).id(json.getString("name"))
				.build();
		client.execute(index);
	}

	@Override
	public void close() {

	}

	@Override
	public void commit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unlock() {
		// TODO Auto-generated method stub
		
	}
	
	

}
