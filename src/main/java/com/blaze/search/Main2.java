package com.blaze.search;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.blaze.parser.InstanceFileContentParser;
import com.blaze.parser.InstanceFileLoader;
import com.blaze.search.elasticsearch.DocumentManager;
import com.blaze.search.elasticsearch.ESIndexManager;
import com.blaze.search.elasticsearch.EntityLoader;
import com.blaze.search.elasticsearch.JestClientBuilderFactory;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.SearchResult.Hit;
import net.sf.json.JSONObject;

public class Main2 {

	public static void main(String[] args) {

		String indexDir = "C:\\Users\\unmeshvinchurkar\\Desktop\\luceneIndex";

		IndexManager indexmanager = SearchFactory.getIndexManager(SearchFactory.LUCENE_SEARCH);

		//indexmanager.deleteIndex(indexDir);

	//	indexmanager.createIndex(indexDir);

		InstanceFileLoader loader = new InstanceFileLoader(InstanceFileContentParser.path);
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			IIndexReader reader = indexmanager.getIndexReader(indexDir);
			String re = reader.runQueryString("national card");
			System.out.println(re);

			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.exit(0);

	}

}
