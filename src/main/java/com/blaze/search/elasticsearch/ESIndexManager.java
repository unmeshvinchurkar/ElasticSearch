package com.blaze.search.elasticsearch;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import com.blaze.search.IIndexReader;
import com.blaze.search.IIndexWriter;
import com.blaze.search.IndexManager;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.IndicesExists;
import io.searchbox.indices.mapping.PutMapping;

public class ESIndexManager implements IndexManager {

	private Locale locale = Locale.US;

	private static String _defaultSettings = "\"settings\" : {\n" + "        \"number_of_shards\" : 1,\n"
			+ "        \"number_of_replicas\" : 1\n" + "    }\n";

	public ESIndexManager(Locale locale) {
		if (locale != null) {
			this.locale = locale;
		}
	}

	@Override
	public IIndexReader getIndexReader(String name) throws IOException {
		return new ESIndexReader(name, JestClientBuilderFactory.getInstance());

	}

	@Override
	public IIndexWriter getIndexWriter(String name) throws IOException {
		return new ESIndexWriter(name, JestClientBuilderFactory.getInstance());
	}

	public boolean exists(String indexName) {

		JestClient client = JestClientBuilderFactory.getInstance();

		try {
			return client.execute(new IndicesExists.Builder(indexName).build()).isSucceeded();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;

	}

	public void deleteIndex(String indexName) {
		JestClient client = JestClientBuilderFactory.getInstance();

		try {
			client.execute(new Delete.Builder(indexName).build());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createIndex(String name) {

		JestClient client = JestClientBuilderFactory.getInstance();

		try {
			boolean indexExists = exists(name);

			if (!indexExists) {

				URL url = ESIndexManager.class.getResource("/elastic-mapping.json");
				String mapping = Resources.toString(url, Charsets.UTF_8);
				PutMapping putMapping = new PutMapping.Builder(name, "Entity", mapping).build();

				// Map<String, String> settings =
				// ImmutableSettings.builder().loadFromClasspath("jestconfiguration.json")
				// .build().getAsMap();

				client.execute(new CreateIndex.Builder(name).build());

				JestResult result = client.execute(putMapping);

				if (!result.isSucceeded()) {
					System.err.println(result.getErrorMessage());
					throw new RuntimeException(String.format("Failed to create mapping: %s", result.getErrorMessage()));
				}

				// UpdateSettings updateSettings = new
				// UpdateSettings.Builder(settings).addIndex(name).build();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void createIndex(String nameOrPath, boolean inMemory) {
		createIndex(nameOrPath);
	}

}
