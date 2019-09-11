package com.freecharge.test.connectionpool.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.freecharge.test.connectionpool.Connection;
import com.freecharge.test.connectionpool.ConnectionFactory;

public class ConnectionPool {

	private final ConnectionFactory connFactory;
	private final long nanoSecondsIdleTime;
	private BlockingQueue<PoolConnection> idleConnections;
	
	private final AtomicInteger currCnt = new AtomicInteger(0);
	private final int maxConnections;

	private final ReentrantLock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();

	public ConnectionPool(ConnectionFactory factory, int maxConnections, int maxConnectionIdleTime) {
		this.maxConnections = maxConnections;
		this.connFactory = factory;
		this.nanoSecondsIdleTime = TimeUnit.NANOSECONDS.convert(maxConnectionIdleTime, TimeUnit.SECONDS);

		idleConnections = new LinkedBlockingDeque<PoolConnection>();

		releaseIdleConnections();
	}

	private class PoolConnection implements Connection {

		long connRefreshTime;
		private Connection conn;
		private ResultTask resultTask;

		PoolConnection(Connection conn) {
			this.conn = conn;
			refreshConnection();
			resultTask = new ResultTask(this);
		}

		@Override
		public Object getResultByConnection() throws Exception {
			if(removeConnectionIfExpired(this) ==  null ){
				throw new Exception("Connection aquired has expired");
			}
			resultTask.run();
			return resultTask.get();
		}

		@Override
		public void releaseConnection() {
			conn.releaseConnection();
			
		}
		
		public PoolConnection refreshConnection() {
			this.connRefreshTime = System.nanoTime();
			return this;
		}

		public long getRemainingIdleTime() {
			return this.connRefreshTime + nanoSecondsIdleTime - System.nanoTime();
		}
		
		private Connection getConnection(){
			return conn;
		}
	}

	private void releaseIdleConnections() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					PoolConnection p = idleConnections.peek();
					lock.lock();
					try {
						if (p == null) {
							condition.await();
						} else {
							long idleTime = p.getRemainingIdleTime();
							if (idleTime > 0) {
								condition.awaitNanos(idleTime);
								continue;
							} 
							if( (p = idleConnections.poll() ) != null){
								p.releaseConnection();
								currCnt.getAndDecrement();
							}
						}
					} catch (InterruptedException e) {
						continue;
					} finally {
					}
				}
			}
		});
		t.setDaemon(true);
		t.start();
	}

	private PoolConnection borrowIdleConnection() {
		PoolConnection p = idleConnections.poll();
		if(p == null){
			return null;
		}
		
		if (lock.tryLock()) {
			condition.signal();
			lock.unlock();
		}
		
		removeConnectionIfExpired(p);
		return p;
	}
		
	public Connection getConnection() throws Exception{
		
		PoolConnection p = borrowIdleConnection();
		if(p != null){
			return p;
		}else{
			int curr = currCnt.get();
			while((curr = currCnt.get()) < maxConnections){
				if(currCnt.compareAndSet(curr, curr +1)){
					try {
						p = new PoolConnection(connFactory.getNewConnection());
						return p;
					} catch (Exception e) {
						currCnt.getAndDecrement();
						return  null;
					}
				}
			}
			throw new Exception("Max Connections");
		}
	}
		
	private PoolConnection removeConnectionIfExpired(PoolConnection p){
		if (p.getRemainingIdleTime() <= 0) {
			p.releaseConnection();
			currCnt.getAndDecrement();
			return null;
		}
		return p;
	}
	private class ResultTask extends FutureTask<Object>{
		private final PoolConnection conn;
		public ResultTask(final PoolConnection conn){
			
			super(new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					return conn.getConnection().getResultByConnection();
				}
			});
		
			this.conn = conn;
		}
		
		@Override
		protected void done() { 
			idleConnections.offer(conn.refreshConnection());
			
			if (lock.tryLock()) {
				condition.signal();
				lock.unlock();
			}
		}
		
	}
}
