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

import com.blaze.parser.EntityLoader;
import com.blaze.search.elasticsearch.DocumentManager;
import com.blaze.search.elasticsearch.ESIndexManager;
import com.blaze.search.elasticsearch.JestClientBuilderFactory;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.SearchResult.Hit;
import net.sf.json.JSONObject;

public class Main {

	public static void main(String[] args) {

		JestClient client = JestClientBuilderFactory.getInstance();
		
		IndexManager indexmanager = SearchFactory.getIndexManager(SearchFactory.ELASTIC_SEARCH);
		 search();
		 
		 if(true) return;

		// IndexManager.createIndex("blaze");
		
		 indexmanager.deleteIndex("blaze-docs");
		
		 indexmanager.createIndex("blaze-docs");

		EntityLoader reader = new EntityLoader();
		reader.fetchInstanceFiles(null);
		List<JSONObject> jsonDocs = reader.getJsonDocs();

		try {
			
			IIndexWriter writer = indexmanager.getIndexWriter("blaze-docs");
			
			//DocumentManager mgr = new DocumentManager();
			for (JSONObject obj : jsonDocs) {
				writer.addDocument(obj);
			}

			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

			// searchSourceBuilder.query(QueryBuilders.matchQuery("comments",
			// "unmesh"));

			searchSourceBuilder.query(QueryBuilders.queryStringQuery("U*mesh"));

			// searchSourceBuilder.query(QueryBuilders.fuzzyQuery("comments",
			// "ummesh"));

			Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("blaze-docs").build();
			SearchResult result = client.execute(search);

			List<Hit<String, Void>> hits = result.getHits(String.class);

			for (Hit<String, Void> hit : hits) {
				System.out.println("Name: " + hit.id + "   Score: " + hit.score);

			}

			List<String> results = result.getSourceAsStringList();

			// for (String str : results) {
			// JSONObject jsonObject = JSONObject.fromObject(str);
			// System.out.println(jsonObject.get("name"));
			// }

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void runQuery() {

		JestClient client = JestClientBuilderFactory.getInstance();

	/**
		String query = "{\n"

				+ "            \"query\" : {\n" + "                \"match\" : {\n"
				+ "                    \"comments\" : \"u*mesh\"\n" + "                }\n" + "            }" + "}";

		
		
		*/
		String query =	"{"+
		   " \"query\": {"+
		    "    \"query_string\": { "+
		         "   \"query\": \" ( name:N?tion?l* OR   comments:cord~ ) AND  date:[2016-12-12 TO 2017-01-12 ]\" "+
		       " }    } 	}";
		
		
		
		Search.Builder searchBuilder = new Search.Builder(query).addIndex("blaze-xml");
		try {
			SearchResult result = client.execute(searchBuilder.build());
			result = result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void search() {
		try {

		/**
			String query = "{"

					+ "            \"query\" : {\n" + "                \"match\" : {"
					+ "                    \"comments\" : \"unmesh\"" + "                }" + "            }" + "}";

			*/
			String query = "{" + " \"query\": {" + "    \"query_string\": { "
					+ "   \"query\": \" ( name:N?tion?l* OR   comments:cord~ ) AND  date:[2016-12-12 TO 2017-01-12 ]\" "
					+ " }    } 	}";
			
			
			URL url = new URL("http://localhost:9200/blaze-xml/_search?pretty");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("content-charset", "UTF-8");
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
			writer.write(query);
			writer.flush();

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;

			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.err.println(output);

			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
