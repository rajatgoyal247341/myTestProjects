package com.freecharge.test.connectionpool.impl;

import java.util.concurrent.CountDownLatch;

import com.freecharge.test.connectionpool.Connection;
import com.freecharge.test.connectionpool.ConnectionFactory;

public class ConnectionPoolTest {

	static class JdbcConnectionFactory implements ConnectionFactory{

		@Override
		public Connection getNewConnection() throws Exception {
			System.out.println(Thread.currentThread().getName() + " get new  connection ");
			Thread.currentThread().sleep(200);
			return new JdbcConnection();
		}
		
	}
	
	static class JdbcConnection implements Connection{

		
		@Override
		public Object getResultByConnection() throws Exception {
			System.out.println(Thread.currentThread().getName() + " get result ");
			Thread.currentThread().sleep(500);
			return null;
		}

		@Override
		public void releaseConnection() {
			System.out.println(Thread.currentThread().getName() + " release connection ");
			
		}
		
	}
	
	public static void main(String[] as) throws Exception{
		JdbcConnectionFactory fac = new JdbcConnectionFactory();
		final ConnectionPool pool = new ConnectionPool(fac, 5, 1);
		final CountDownLatch latch = new CountDownLatch(1);
		final CountDownLatch latch2  = new CountDownLatch(2);
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				try {
					latch.await();
					Connection p  = pool.getConnection();
					System.out.println(Thread.currentThread().getName()  + " connection from pool " + p + " at " + System.currentTimeMillis());
					p.getResultByConnection();
					latch2.countDown();
				} catch (Exception  e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		};
		new Thread(r).start();
		new Thread(r).start();
		new Thread(r).start();
		new Thread(r).start();
		new Thread(r).start();
		
		latch.countDown();
		latch2.await();
		
		Thread.currentThread().sleep(2000);
		
		new Thread(r).start();
		
	}
}
