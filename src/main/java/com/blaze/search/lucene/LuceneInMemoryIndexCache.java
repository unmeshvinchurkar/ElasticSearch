package com.blaze.search.lucene;

import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.store.RAMDirectory;

public class LuceneInMemoryIndexCache {

	private static Map indexCache = new HashMap();

	public static void addIndexMemory(String indexName, RAMDirectory indexMemory) {
		indexCache.put(indexName, indexMemory);
	}

	public static RAMDirectory getIndexMemory(String indexName) {
		return (RAMDirectory) indexCache.get(indexName);
	}

	public static void deleteIndexMemory(String indexName) {
		if (exist(indexName)) {
			RAMDirectory dir = (RAMDirectory) indexCache.remove(indexName);
			dir.close();
		}
	}

	public static boolean exist(String indexName) {
		return indexCache.get(indexName) != null;
	}

}
