package com.blaze.search.elasticsearch;

import java.io.IOException;

import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import net.sf.json.JSONObject;

public class DocumentManager {

	public void addDocument(JSONObject json, String indexName) throws IOException {
		JestClient client = JestClientBuilderFactory.getInstance();

		// Index index = new
		// Index.Builder(json).index(indexName).type("template").id(docId).build();

		Index index = new Index.Builder(json).index(indexName).type(json.getString("type")).id(json.getString("name"))
				.build();
		client.execute(index);
	}

}
