package com.freecharge.testcode;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockTest {

	private Lock lock = new ReentrantLock();

	public void tryToDoThings() {
		Runnable r1 = new Runnable() {

			@Override
			public void run() {
				synchronized (lock) {
					System.out.println("executing " + Thread.currentThread().getName());
					try {
						Thread.currentThread().sleep(90000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		};
		Thread t1 = new Thread(r1);
		t1.start();
		try {
			Thread.currentThread().sleep(7000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Runnable r2 = new Runnable() {

			@Override
			public void run() {
				synchronized (lock) {
					System.out.println("executing " + Thread.currentThread().getName());
					try {
						Thread.currentThread().sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		};
		
		Thread t2 = new Thread(r2);
		t2.start();
		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(t2.getState());
	}
	
	
	public static void main(String[] as){
		new LockTest().tryToDoThings();
	}

}
