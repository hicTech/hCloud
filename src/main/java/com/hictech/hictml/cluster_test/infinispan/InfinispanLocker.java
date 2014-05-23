package com.hictech.hictml.cluster_test.infinispan;

import static com.hictech.hictml.cluster_test.hazelcast.HazelcastTestSystem.cluster;

import com.hictech.hictml.cluster_test.Locker;
import com.hictech.util.h.HCommon;

public class InfinispanLocker implements Locker {

	public InfinispanLocker(){
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
