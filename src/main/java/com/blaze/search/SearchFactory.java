package com.blaze.search;

import com.blaze.search.elasticsearch.ESIndexManager;
import com.blaze.search.lucene.LuceneIndexManager;

public class SearchFactory {

	public static final String ELASTIC_SEARCH = "ELASTIC_SEARCH";
	public static final String LUCENE_SEARCH = "LUCENE_SEARCH";

	public static IndexManager getIndexManager(String searchProvider) {

		if (searchProvider.equals(ELASTIC_SEARCH)) {
			return new ESIndexManager();
		} else {
			return new LuceneIndexManager();
		}

	}

}
