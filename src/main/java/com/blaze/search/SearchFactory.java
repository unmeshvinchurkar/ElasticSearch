package com.blaze.search;

import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.da.DanishAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import com.blaze.search.elasticsearch.ESIndexManager;
import com.blaze.search.lucene.LuceneIndexManager;

public class SearchFactory {

	public static final String ELASTIC_SEARCH = "ELASTIC_SEARCH";
	public static final String LUCENE_SEARCH = "LUCENE_SEARCH";
	
	public static IndexManager getIndexManager(String searchProvider) {
		return getIndexManager(searchProvider, Locale.US); 

	}

	public static IndexManager getIndexManager(String searchProvider, Locale locale) {

		if (searchProvider.equals(ELASTIC_SEARCH)) {
			return new ESIndexManager(locale);
		} else {
			return new LuceneIndexManager(locale);
		}

	}

	public static Analyzer getAnalyzer(Locale locale) {

		Analyzer analyzer = null;

		if (locale.equals(Locale.CHINA) || locale.equals(Locale.JAPAN) || locale.equals(Locale.KOREA)) {
			return new CJKAnalyzer();
		} else if (locale.equals(Locale.CHINESE) || locale.equals(Locale.JAPANESE) || locale.equals(Locale.KOREAN)) {
			return new CJKAnalyzer();
		} else if (locale.equals(Locale.FRANCE) || locale.equals(Locale.FRENCH)) {
			return new FrenchAnalyzer();
		} else if (locale.equals(Locale.GERMAN) || locale.equals(Locale.GERMANY)) {
			return new GermanAnalyzer();
		} else if (locale.equals(Locale.ITALIAN) || locale.equals(Locale.ITALY)) {
			return new SpanishAnalyzer();
		} else if (locale.equals(Locale.ITALIAN) || locale.equals(Locale.ITALY)) {
			return new SpanishAnalyzer();
		} else if (locale.getCountry().equalsIgnoreCase("Russia") || locale.getLanguage().equalsIgnoreCase("Russian")) {
			return new RussianAnalyzer();
		} else if (locale.getLanguage().equalsIgnoreCase("Danish")) {
			return new DanishAnalyzer();
		}

		return new StandardAnalyzer();

	}

}
