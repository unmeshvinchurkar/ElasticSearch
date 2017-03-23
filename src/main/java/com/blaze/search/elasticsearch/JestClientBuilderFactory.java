package com.blaze.search.elasticsearch;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

public class JestClientBuilderFactory {

	private static JestClient _client = null;
	
	private static Object lock = new Object();

	public static JestClient getInstance() {

		if (_client == null) {

			synchronized (lock) {

				if (_client == null) {

					JestClientFactory factory = new JestClientFactory();
					factory.setHttpClientConfig(
							new HttpClientConfig.Builder("http://localhost:9200").multiThreaded(true).build());
					
					
					
					JestClient client = factory.getObject();

					_client = client;
				}
			}
		}

		return _client;
	}

	public static void destroy() {

		if (_client != null) {
			_client.shutdownClient();
			_client = null;
		}
	}
}
