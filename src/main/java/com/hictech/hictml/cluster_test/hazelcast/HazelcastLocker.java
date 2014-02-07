package com.hictech.hictml.cluster_test.hazelcast;

import static com.hictech.hictml.cluster_test.hazelcast.HazelcastTestSystem.cluster;

import com.hictech.hictml.cluster_test.Locker;
import com.hictech.util.h.HCommon;

public class HazelcastLocker implements Locker {

	public HazelcastLocker(){
	}
	
	public synchronized void lock(String key) throws InterruptedException {
		HCommon.printfln("obtaining hazelcast lock on key %s", key);
		cluster.getLock(key).lock();
	}
	
	public void unlock(String key) throws InterruptedException {
		HCommon.printfln("releasing hazelcast lock on key %s", key);
		cluster.getLock(key).unlock();
	}
	
}
