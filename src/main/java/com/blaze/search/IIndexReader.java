package com.blaze.search;

import java.io.IOException;
import java.util.List;

public interface IIndexReader {
	public List runQueryString(String queryString) throws IOException;

	public void close();

}
