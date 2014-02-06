package com.hictech.hictml.cluster_test;

public interface Locker {

	public void lock(String key) throws InterruptedException;
	
	public void unlock(String key) throws InterruptedException;
	
}
