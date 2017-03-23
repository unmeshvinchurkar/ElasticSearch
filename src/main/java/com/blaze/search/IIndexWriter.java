package com.blaze.search;

import java.io.IOException;

import net.sf.json.JSONObject;

public interface IIndexWriter {
	public void addDocument(JSONObject json) throws IOException;
	
	public void close();
	
	public void commit();
	
	public void unlock();

}
