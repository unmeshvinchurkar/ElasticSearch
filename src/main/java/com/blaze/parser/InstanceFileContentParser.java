package com.blaze.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.blaze.BlazeConstants;
import com.blaze.SearchDocConstants;

import net.sf.json.JSONObject;

public class InstanceFileContentParser {

	private static String path = "C:\\Users\\unmeshvinchurkar\\Desktop\\TestRepo\\PublishRepoWs\\Metaphors and Templates";

	private List<JSONObject> jsonDocs = new ArrayList<JSONObject>();
	private InstanceFileLoader fileLoader = null;

	public InstanceFileContentParser(InstanceFileLoader fileLoader) {
		this.fileLoader = fileLoader;

	}

	public List<JSONObject> getJsonDocs() {
		return jsonDocs;
	}

	public void parseInstanceFiles() {

		while (fileLoader.hasMoreFiles()) {

			File file = fileLoader.getNextFile();

			if (file == null || !file.exists()) {
				continue;
			}

			Properties props = loadInnovatorAttribs(
					file.getAbsolutePath() + BlazeConstants.INNOVATOR_ATTRIB_FILE_POST_FIX);
			if (props.isEmpty()) {
				return;
			}

			String type = props.getProperty(BlazeConstants.TYPE);
			String contentType = props.getProperty(BlazeConstants.CONTENT_TYPE);

			if (contentType != null && BlazeConstants.CONTENT_TYPE_INSTANCE.equalsIgnoreCase(contentType)) {

				try {

					JSONObject json = new JSONObject();

					json.put(SearchDocConstants.ID, file.getPath());
					json.put(SearchDocConstants.NAME, file.getName());
					json.put(SearchDocConstants.PATH, file.getAbsolutePath());
					json.put(SearchDocConstants.BLAZE_TYPE, type);
					json.put(SearchDocConstants.CONTENT_TYPE, contentType);
					json.put(SearchDocConstants.TYPE, SearchDocConstants.ENTITY_TYPE);
					json.put(SearchDocConstants.DATE, new Date().getTime());
					json.put(SearchDocConstants.CONTENT,
							loadFile(file).replaceAll("<.*?>", "").replaceAll("\\s{2,}", " "));

					Set keys = props.keySet();

					String values = "";

					for (Object key : keys) {
						String value = (String) props.get(key);

						if (value != null && !value.trim().equals("")) {
							String keyStr = (String) key;
							values = values + ";" + keyStr + " is " + value;
						}
					}

					json.put(SearchDocConstants.PROPERTIES, values);

					jsonDocs.add(json);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

	}

	public static String loadFile(File file) {

		BufferedReader br = null;
		String everything = "";
		try {

			br = new BufferedReader(new FileReader(file));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			everything = sb.toString();
		} catch (Exception e) {
		} finally {
			try {
				br.close();
			} catch (IOException e) {
			}
		}

		return everything;
	}

	private Properties loadInnovatorAttribs(String filepath) {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(filepath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

	public static void main(String[] args) {
		InstanceFileLoader loader = new InstanceFileLoader(InstanceFileContentParser.path);
		InstanceFileContentParser r = new InstanceFileContentParser(loader);
		r.parseInstanceFiles();
	}

}
