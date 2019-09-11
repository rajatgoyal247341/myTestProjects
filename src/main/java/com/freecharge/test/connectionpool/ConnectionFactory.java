package com.freecharge.test.connectionpool;

public interface ConnectionFactory {
	public Connection getNewConnection() throws Exception;
}
