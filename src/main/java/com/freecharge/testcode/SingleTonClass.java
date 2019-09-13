package com.freecharge.testcode;

public class SingleTonClass {

	private SingleTonClass(){
		System.out.println(Thread.currentThread().getName() +" private cons " + System.currentTimeMillis());
	}
	
	public static SingleTonClass getInstance(){
		System.out.println(Thread.currentThread().getName() +" gettingInstance " + System.currentTimeMillis());
		return NestedPrivateConsClass.instance;
	}
	
	public static void tryCheck(){
		System.out.println(Thread.currentThread().getName() +" trying " + System.currentTimeMillis());
	}
	
	private static class NestedPrivateConsClass{
		private static final SingleTonClass instance = new SingleTonClass();
	}
}

