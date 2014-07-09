package com.hictech.hictml.cluster_test.infinispan;

import static com.hictech.util.h.HCommon.printfln;

import javax.transaction.TransactionManager;

import org.infinispan.AdvancedCache;
import org.infinispan.Cache;

import com.hictech.hictml.cluster_test.Locker;

public class InfinispanLocker implements Locker {

	private AdvancedCache<Object, Object> cache;
	private TransactionManager tx;
	
	public InfinispanLocker(Cache<Object, Object> cache) {
		this.cache = cache.getAdvancedCache();
		
		this.tx = this.cache.getTransactionManager();
	}

	@Override
	public void lock(String key) throws InterruptedException {
		printfln("obtaining infinispan lock on key %s", key);

		begin();
		cache.lock(key);
	}
	
	@Override
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
