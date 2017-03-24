package com.blaze.search;

import java.io.IOException;
import java.util.List;

import com.blaze.parser.InstanceFileContentParser;
import com.blaze.parser.InstanceFileLoader;

import net.sf.json.JSONObject;

public class Main3 {

	public static void main(String[] args) {

		String indexDir = "blaze-docs";
		String BLAZE_PROJ_PATH = "C:\\Users\\unmeshvinchurkar\\Desktop\\TestRepo\\PublishRepoWs\\Metaphors and Templates";

		IndexManager indexmanager = SearchFactory.getIndexManager(SearchFactory.ELASTIC_SEARCH);

		// indexmanager.deleteIndex(indexDir);
		// indexmanager.createIndex(indexDir);

		InstanceFileLoader loader = new InstanceFileLoader(BLAZE_PROJ_PATH);
		InstanceFileContentParser r = new InstanceFileContentParser(loader);
		r.parseInstanceFiles();

		List<JSONObject> jsonDocs = r.getJsonDocs();

		try {

			IIndexWriter writer = indexmanager.getIndexWriter(indexDir);

			for (JSONObject obj : jsonDocs) {
				writer.addDocument(obj);
			}

			try {
				writer.commit();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			IIndexReader reader = indexmanager.getIndexReader(indexDir);
			reader.runQueryString("national card");

			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.exit(0);

	}

}
