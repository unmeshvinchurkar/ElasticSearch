package com.blaze.search.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.blaze.search.IIndexReader;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

public class ESIndexReader implements IIndexReader {

	private String indexName = null;
	private JestClient client = null;

	public ESIndexReader(String indexName, JestClient client) {
		this.indexName = indexName;
		this.client = client;
	}

	public String runQuery(String queryString) throws IOException {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		searchSourceBuilder.query(QueryBuilders.queryStringQuery(queryString));
		Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(indexName).build();
		SearchResult result = client.execute(search);

		// List<Hit<String, Void>> hits = result.getHits(String.class);

		return result.getSourceAsString();

	}

	public List runQueryString(String queryString) {

		JestClient client = JestClientBuilderFactory.getInstance();
		List resultDocs = new ArrayList();

		String query = "{" + " \"query\": {" + "    \"query_string\": { " + "   \"query\": \"" + queryString + "\" "
				+ " }    } 	}";

		Search.Builder searchBuilder = new Search.Builder(query).addIndex(indexName);
		try {
			SearchResult result = client.execute(searchBuilder.build());

			com.google.gson.JsonObject jsonObj = result.getJsonObject();
			JsonObject hits = jsonObj.getAsJsonObject("hits");
			JsonArray docs = hits.getAsJsonArray("hits");

			for (int i = 0; i < docs.size(); i++) {

				Map docMap = new HashMap();

				JsonObject doc = docs.get(i).getAsJsonObject();
				JsonObject docFields = doc.getAsJsonObject("_source");
				docMap.put("score", doc.get("_score".toString()));
				docMap.put("id", doc.get("_id".toString()));
				docMap.put("type", doc.get("_type".toString()));
				docMap.put("index", doc.get("_index".toString()));

				Set entrySet = docFields.entrySet();
				Iterator iter = entrySet.iterator();

				while (iter.hasNext()) {

					Entry entry = (Entry) iter.next();
					docMap.put(entry.getKey().toString(), entry.getValue().toString());
				}

				resultDocs.add(docMap);

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultDocs;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
