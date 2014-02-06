package com.hictech.hictml.cluster_test.single_host;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import com.hictech.hictml.cluster_test.Locker;

public class SingleHostLocker implements Locker {

	private Map<String, Semaphore> table;
	
	public SingleHostLocker(){
		table = new HashMap<String, Semaphore>();
	}
	
	public synchronized void lock(String key) throws InterruptedException {
		Semaphore lock;
		
		if( table.containsKey(key) ) {
			lock = (Semaphore) table.get(key);
		}
		else {
			lock = new Semaphore(1);
			table.put(key, lock);
		}
		
		lock.acquire();
	}
	
	public void unlock(String key) throws InterruptedException {
		if( !table.containsKey(key) ) {
			return;
		}
		
		Semaphore lock = table.get(key);
		lock.release();

		if( lock.availablePermits() == 1 ) {
//			table.remove(key);
		}
	}
	
}
