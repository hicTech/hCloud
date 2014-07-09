package com.hictech.hictml.cluster_test.infinispan;
import java.util.Date;

import javax.transaction.TransactionManager;

import org.infinispan.AdvancedCache;

import com.hictech.hictml.cluster_test.Cache;
import com.hictech.hictml.cluster_test.CacheSource;

public class InfinispanCache implements Cache {
	
	private AdvancedCache<Object, Object> cache;
	
	public InfinispanCache(org.infinispan.Cache<Object, Object> cache) {
		this.cache = cache.getAdvancedCache();
	}
	
	@Deprecated
	public static void log(Object... args) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("[").append(new Date().getTime()).append("] ");
		for( Object arg: args ) {
			sb.append(arg);
		}
		
		System.out.println(sb);
	}
	
	public Object get(String key) {
		try {
			log("executing get with key ", key, "on infinispan");
			Object value = null;
			
			
			TransactionManager tx = cache.getTransactionManager();

			try {
				log("obtain lock for key ", key);
				tx.begin();
				cache.lock(key);
				
				value = cache.get(key);
				log("get object with key ", key," and value ", value);
				
				tx.commit();
			}
			catch( Exception e ) {
				tx.rollback();
			}
			
			log("release lock for key ", key);
			
			return value;
		}
		catch( Exception e ) {
			throw new RuntimeException(e);
		}
	}

	public Object getOrLoad(String key, CacheSource source) {
		try {
			log("executing getOrLoad with key ", key, " on infinispan");
			Object value = null;
			
			
			TransactionManager tx = cache.getTransactionManager();

			try {
				log("obtain lock for key ", key);
				tx.begin();
				cache.lock(key);

				value = cache.get(key);
				log("get object with key ", key, " and value", key);
				
				if( value == null ) {
					value = source.load(key);
					cache.put(key, value);
				}
				
				log("put object with key ", key, " and value %s", value);
				tx.commit();
			}
			catch( Exception e ) {
				log("error on get on load of key %s", key);
				log(e);
				e.printStackTrace();
				
				tx.rollback();
			}
			
			log("release lock for key ", key);
			
			return value;
		}
		catch( Exception e ) {
			throw new RuntimeException(e);
		}
	}

	public Object put(String key, Object value) {
		try {
			log("executing put with key ", key, " and value on infinispan", value);
			Object oldValue = null;
			
			TransactionManager tx = cache.getTransactionManager();

			try {
				log("obtain lock for key ", key);
				tx.begin();
				cache.lock(key);

				oldValue = cache.get(key);
				log("get old object with key ", key, " and value ", key, value);
	
				cache.put(key, value);
				log("put new object with key ", key, " and value ", key, value);
				
				tx.commit();
			}
			catch( Exception e ) {
				e.printStackTrace();
				
				tx.rollback();
			}
			
			log("release lock for key ", key);
			
			return oldValue;
		}
		catch( Exception e ) {
			throw new RuntimeException(e);
		}
	}

}
