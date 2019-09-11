package com.freecharge.test.ratelimiter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RateLimiterUtils {

	private static final TimeUnit defaultRatelimitTimeUnit = TimeUnit.MINUTES;
	
	private BlockingQueue<Long> blockingQueue;
	private long nanoSecondsInTimeUnit;
	
	private final Lock lock = new ReentrantLock();
	private final Condition conditionToPutInQueue = lock.newCondition();
	
	public RateLimiterUtils(String timeUnit, int noOfRequestAllowed){
		TimeUnit timeUnitEnum = null;
		try{
			timeUnitEnum = TimeUnit.valueOf(timeUnit);
    	}catch(IllegalArgumentException e){
    		timeUnitEnum = defaultRatelimitTimeUnit;
    	}
		nanoSecondsInTimeUnit = timeUnitEnum.toNanos(1);
		blockingQueue = new LinkedBlockingDeque<Long>(noOfRequestAllowed);
	}
	
	public RateLimiterUtils(int noOfRequestAllowed){
		this(defaultRatelimitTimeUnit.name(), noOfRequestAllowed);
	}
	
	public void tryUnderRateLimits(){
		lock.lock();
		try{
			Long nanosToWait = null;
			while(addIfEligible() == false){
				nanosToWait = nanosToWait != null ? nanosToWait : (nanoSecondsInTimeUnit - (System.nanoTime() - blockingQueue.peek()));
				if(nanosToWait <= 0){
					continue;
				}
				try {
					nanosToWait = conditionToPutInQueue.awaitNanos(nanosToWait);
				} catch (InterruptedException e) {
					continue;
				} 
			}
		}finally {
			lock.unlock();
		}
	}
	
	private boolean addIfEligible(){
		Long headTimeStamp = blockingQueue.peek();
		if(headTimeStamp == null){
			return blockingQueue.offer(System.nanoTime());
		}
		long currTimeStamp = System.nanoTime();
		long diffFromFront = currTimeStamp - headTimeStamp;
		if(diffFromFront > nanoSecondsInTimeUnit){
			blockingQueue.remove();
		}
		return blockingQueue.offer(currTimeStamp);
	}
}