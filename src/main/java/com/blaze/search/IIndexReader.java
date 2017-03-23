package com.blaze.search;

import java.io.IOException;

public interface IIndexReader {
	public String runQueryString(String queryString) throws IOException ;
	
	
	public void close();

}
