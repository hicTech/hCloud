package com.hictech.hictml.cluster_test.infinispan;

import static com.hictech.hictml.cluster_test.infinispan.InfinispanTestSystem.infinispan;
import static com.hictech.util.h.HCommon.printfln;

import javax.transaction.TransactionManager;

import org.infinispan.AdvancedCache;

import com.hictech.hictml.cluster_test.Locker;

public class InfinispanLocker implements Locker {

	private static AdvancedCache<Object, Object> cache = infinispan().getCache().getAdvancedCache();

	private TransactionManager tx;

	public InfinispanLocker(){
		tx = cache.getTransactionManager();
	}
	
	public synchronized void lock(String key) throws InterruptedException {
		begin();

		printfln("obtaining infinispan lock on key %s", key);
		cache.lock(key);
	}
	
	public void unlock(String key) throws InterruptedException {
		printfln("releasing infinispan lock on key %s", key);
		commit();
	}
	
	private void begin() throws InterruptedException {
		try {
			tx.begin();
		}
		catch( Exception e ) {
			e.printStackTrace();
			
			rollback();
			
			throw new InterruptedException(e.getMessage());
		}
	}
	
	private void commit() throws InterruptedException {
		try {
			tx.commit();
		}
		catch( Exception e ) {
			e.printStackTrace();
			
			throw new InterruptedException(e.getMessage());
		}
	}
	
	private void rollback() throws InterruptedException {
		try {
			tx.rollback();
		}
		catch( Exception e ) {
			e.printStackTrace();
			
			throw new InterruptedException(e.getMessage());
		}
	}
	
}
