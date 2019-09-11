package com.freecharge.testcode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockTest {

	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock r = lock.readLock();
	private final Lock w = lock.writeLock();

	public void read() {
		r.lock();
		System.out.println(System.currentTimeMillis() + " reading " + Thread.currentThread().getName());
		try {
			Thread.currentThread().sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//r.unlock();
	}

	public void write() {
		w.lock();
		System.out.println(System.currentTimeMillis() + " writing " + Thread.currentThread().getName());
		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		w.unlock();
	}

	public static void tryThings() {
		final ReadWriteLockTest test = new ReadWriteLockTest();
		Runnable read = new Runnable() {

			@Override
			public void run() {
				test.read();

			}
		};

		Runnable write = new Runnable() {

			@Override
			public void run() {
				test.write();

			}
		};
		
		List<Thread>  threads = new ArrayList<>();
		for(int i = 0; i < 5;i++){
			threads.add(new Thread(read));
		}
		
		for(int i =0;i < 5;i++){
			threads.get(i).start();
		}
		
		new Thread(write).start();
	}
	
	public static void main(String[] as){
		tryThings();
	}
}
