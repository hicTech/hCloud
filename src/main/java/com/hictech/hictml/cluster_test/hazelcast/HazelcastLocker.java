package com.hictech.hictml.cluster_test.hazelcast;

import static com.hictech.hictml.cluster_test.hazelcast.HazelcastTestSystem.cluster;

import com.hictech.hictml.cluster_test.Locker;

public class HazelcastLocker implements Locker {

	public HazelcastLocker(){
	}
	
	public synchronized void lock(String key) throws InterruptedException {
		cluster.getLock(key).lock();
	}
	
	public void unlock(String key) throws InterruptedException {
		cluster.getLock(key).unlock();
	}
	
}
