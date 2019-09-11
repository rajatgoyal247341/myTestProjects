package com.freecharge.testcode;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTest {

	private final ThreadPoolExecutor executor;

	public ThreadPoolTest() {
		executor = new ThreadPoolExecutor(5, 8, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(5));
	}

	public void test() {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				try {
					System.out.println("sleeping thread " + Thread.currentThread().getName());
					Thread.currentThread().sleep(5000);
					System.out.println("sleep completed " + Thread.currentThread().getName());
				} catch (InterruptedException e) {

				}

			}
		};

		for (int i = 0; i < 10; i++) {
			executor.execute(run);
		}
		
		while(true){
			System.out.println(executor.getPoolSize());
			try {
				Thread.currentThread().sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] as) {
		ThreadPoolTest test = new ThreadPoolTest();
		test.test();
	}
}
