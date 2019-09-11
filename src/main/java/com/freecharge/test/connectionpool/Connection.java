package com.freecharge.test.connectionpool;

public interface Connection {
	
	public Object getResultByConnection() throws Exception;
	public void releaseConnection();

}
