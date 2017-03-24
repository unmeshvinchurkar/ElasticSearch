package com.blaze.search.elasticsearch;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.blaze.SearchDocConstants;

import net.sf.json.JSONObject;

public class EntityLoader {

	private String path = "C:\\Users\\unmeshvinchurkar\\Desktop\\TestRepo\\PublishRepoWs\\Metaphors and Templates";

	private DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	private List<JSONObject> jsonDocs = new ArrayList<JSONObject>();

	public List<JSONObject> getJsonDocs() {
		return jsonDocs;
	}

	public void fetchInstanceFiles(File file) {

		if (file == null) {
			file = new File(path);
		}

		if (!file.exists()) {
			return;
		}

		if (file.isDirectory()) {

			File[] files = file.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.indexOf(".") == -1;
				}
			});

			for (File file1 : files) {
				fetchInstanceFiles(file1);
			}
		} else {

			String type = "template";

			if (!new File(file.getAbsolutePath() + ".innovator_attbs").exists()) {
				return;
			}

			Properties props = loadInnovatorAttribs(file.getAbsolutePath() + ".innovator_attbs");
			if (props.isEmpty()) {
				return;
			}

			type = props.getProperty("type");
			String contentType = props.getProperty("contentType");

			try {
				// String content = readXmlFile(node);
				DocumentBuilder dBuilder = null;
				dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(file);

				XPath xPath = XPathFactory.newInstance().newXPath();

				JSONObject json = new JSONObject();

				json.put(SearchDocConstants.NAME, file.getName());
				json.put(SearchDocConstants.PATH, file.getAbsolutePath());
				json.put(SearchDocConstants.BLAZE_TYPE, type);
				json.put(SearchDocConstants.CONTENT_TYPE, contentType);
				json.put(SearchDocConstants.TYPE, SearchDocConstants.ENTITY_TYPE);
				json.put(SearchDocConstants.DATE, new Date().getTime());
				
				json.put(SearchDocConstants.CONTENT, loadFile(file).replaceAll("<.*?>", "").replaceAll("\\s{2,}", " "));
				
				//props.remove("type");

				Set keys = props.keySet();
				
				String values = "";

				for (Object key : keys) {
					String value = (String) props.get(key);
					
					if (value != null && !value.trim().equals("")) {
						String keyStr = (String) key;
//						if (((String) key).startsWith("managementProperty")) {
//							keyStr = keyStr.substring(keyStr.lastIndexOf(".") + 1);
//						}
						//json.put(keyStr, value);
						values = values+ ";" +keyStr +" is " + value;
					}
				}
				
				json.put("properties", values);
				
				/*System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++");
				
				System.out.println(json.get("name"));
				System.out.println("===================");
				
				System.out.println(json.get("comments"));
				
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++");*/
				
				/**

				StringBuffer sb = new StringBuffer(200);

				if (contentType.equalsIgnoreCase("provider")) {

					if (type.equalsIgnoreCase("Generic")) {
						NodeList comments = (NodeList) xPath.compile("/template/comment").evaluate(doc,
								XPathConstants.NODESET);

						for (int i = 0; i < comments.getLength(); i++) {
							sb.append(comments.item(i).getTextContent());
							sb.append(" ");

						}
					}
									
				} else if (contentType.equalsIgnoreCase("template")) {

					NodeList comments = (NodeList) xPath.compile("/template/comment").evaluate(doc,
							XPathConstants.NODESET);

					NodeList templateComments = (NodeList) xPath.compile("/template/template/comment").evaluate(doc,
							XPathConstants.NODESET);

					NodeList contentComments = (NodeList) xPath.compile("/template/template:content/comment")
							.evaluate(doc, XPathConstants.NODESET);

					NodeList ruleComments = (NodeList) xPath.compile("/template/template/ruleset-body/rule/comment")
							.evaluate(doc, XPathConstants.NODESET);

					for (int i = 0; i < comments.getLength(); i++) {
						sb.append(comments.item(i).getTextContent());
						sb.append(" ");

					}

					for (int i = 0; i < templateComments.getLength(); i++) {
						sb.append(templateComments.item(i).getTextContent());
						sb.append(" ");

					}

					for (int i = 0; i < contentComments.getLength(); i++) {
						sb.append(contentComments.item(i).getTextContent());
						sb.append(" ");

					}

					for (int i = 0; i < ruleComments.getLength(); i++) {
						sb.append(ruleComments.item(i).getTextContent());
						sb.append(" ");
					}								

				} else if (contentType.equalsIgnoreCase("fixed")) {

					if (type.equalsIgnoreCase("SRL Class")) {

						NodeList contentnames = (NodeList) xPath.compile("/template/content/name").evaluate(doc,
								XPathConstants.NODESET);
						
						NodeList propNames = (NodeList) xPath.compile("/template/class/properties/property/name").evaluate(doc,
								XPathConstants.NODESET);

						for (int i = 0; i < contentnames.getLength(); i++) {
							sb.append(contentnames.item(i).getTextContent());
							sb.append(" ");
						}						
						
						for (int i = 0; i < propNames.getLength(); i++) {
							sb.append(propNames.item(i).getTextContent());
							sb.append(" ");

						}
						
					} else if (type.equalsIgnoreCase("SRL Ruleset")) {

						NodeList comments = (NodeList) xPath.compile("/template/content/comment").evaluate(doc,
								XPathConstants.NODESET);

						NodeList rulebody = (NodeList) xPath.compile("/template/content/ruleset/ruleset-body/rule/body")
								.evaluate(doc, XPathConstants.NODESET);

						NodeList ruleNames = (NodeList) xPath.compile("/template/content/ruleset/ruleset-body/rule/name")
								.evaluate(doc, XPathConstants.NODESET);

						for (int i = 0; i < comments.getLength(); i++) {
							sb.append(comments.item(i).getTextContent());
							sb.append(" ");

						}

						for (int i = 0; i < rulebody.getLength(); i++) {
							sb.append(rulebody.item(i).getTextContent());
							sb.append(" ");

						}

						for (int i = 0; i < ruleNames.getLength(); i++) {
							sb.append(ruleNames.item(i).getTextContent());
							sb.append(" ");

						}
					}
					
				} else {
					json.put("comments", node.getName());
				}
				
				json.put("comments",  node.getName() + " " +sb.toString());
				**/
				
				jsonDocs.add(json);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
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
		EntityLoader r = new EntityLoader();
		r.fetchInstanceFiles(null);
	}

}
