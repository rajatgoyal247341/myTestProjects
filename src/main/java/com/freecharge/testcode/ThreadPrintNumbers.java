package com.freecharge.testcode;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ThreadPrintNumbers {

	private Lock lock  = new ReentrantLock();
	private Condition numberCondition = lock.newCondition(); 
	private Condition charCondition = lock.newCondition();
	
	private int counter = 0;
	private char currCharacter = 'a';
	private boolean printChar = false;
	private int NumberOfNumberThread = 2;
	
	class NumberPrinter implements Runnable{

		private int myId;
		
		public NumberPrinter(int id){
			this.myId = id;
		}
		@Override
		public void run() {
			while(true){
				lock.lock();
				while((counter % NumberOfNumberThread) != myId || printChar == true){
					try {
						numberCondition.await();
					} catch (InterruptedException e) {}
				}
				System.out.println(Thread.currentThread().getName() + " printing "+ counter);
				try {
					Thread.currentThread().sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				counter++;
				printChar = true;
				charCondition.signal();
				lock.unlock();
			}
			
		}
		
	}
	
	class CharPrinter implements Runnable{

		@Override
		public void run() {
			while(true){
				lock.lock();
				while(printChar == false ){
					try {
						charCondition.await();
					} catch (InterruptedException e) {}
				}
				System.out.println(Thread.currentThread().getName() + " printing "+ currCharacter);
				try {
					Thread.currentThread().sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				printChar = false;
				currCharacter++;
				numberCondition.signalAll();
				lock.unlock();
			}
		}
		
	}
	
	public static void main(String[] as){
		ThreadPrintNumbers tnp = new ThreadPrintNumbers();
		Runnable np1 = tnp.new NumberPrinter(0);
		Runnable np2 = tnp.new NumberPrinter(1);
		Runnable cp = tnp.new CharPrinter();
		Thread t1 =  new Thread(np1);
		Thread t2 =  new Thread(np2);
		Thread t3 =  new Thread(cp);
		t1.start();
		t2.start();
		t3.start();
		
	}
	
}
