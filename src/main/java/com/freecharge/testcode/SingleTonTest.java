package com.freecharge.testcode;

import java.util.concurrent.CountDownLatch;

public class SingleTonTest {

	public static void main(String[] as){
		final CountDownLatch latch  = new CountDownLatch(1);
		
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				try {
					latch.await();
					SingleTonClass.tryCheck();
					
				} catch (Exception  e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		};
		
		new Thread(r).start();
		new Thread(r).start();
		latch.countDown();
		
		
	}
}
