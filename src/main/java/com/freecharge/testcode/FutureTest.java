package com.freecharge.testcode;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class FutureTest {

	private class ResultTask extends FutureTask<String>{

		public ResultTask() {
			super(new Callable<String>() {

				@Override
				public String call() throws Exception {
					System.out.println("execu sleeping current Thread " + Thread.currentThread().getName());
					Thread.currentThread().sleep(10000);
					return "rajat";
				}
			});
		}
		
		protected void done() { 
			System.out.println("done sleeping current Thread " + Thread.currentThread().getName());
		}
	}
	
	public static void main(String[] as) throws Exception{
		ResultTask t = new FutureTest().new ResultTask();
		t.run();
		System.out.println(t.get());
		
	}
}
