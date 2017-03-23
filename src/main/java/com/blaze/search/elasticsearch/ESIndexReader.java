package com.blaze.search.elasticsearch;

import java.io.IOException;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.blaze.search.IIndexReader;

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

	public String runQueryString(String queryString) throws IOException {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		searchSourceBuilder.query(QueryBuilders.queryStringQuery(queryString));
		Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(indexName).build();
		SearchResult result = client.execute(search);

		// List<Hit<String, Void>> hits = result.getHits(String.class);

		return result.getSourceAsString();

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
