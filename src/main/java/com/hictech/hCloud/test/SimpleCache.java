package com.hictech.hCloud.test;

import java.util.Date;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;

import org.infinispan.AdvancedCache;
import org.infinispan.manager.EmbeddedCacheManager;

public class SimpleCache {
	
	//@Resource(lookup = "java:jboss/infinispan/container/isolation")
	private static EmbeddedCacheManager manager;
	static {
		try {
			manager = (EmbeddedCacheManager) InitialContext.doLookup("java:jboss/infinispan/container/hcache");
		}
		catch (NamingException e) {
			e.printStackTrace();
		}
	}

	
	private static SimpleCache instance = null;
	public static SimpleCache getInstance() throws NamingException {
		if( instance == null ) {
			instance = new SimpleCache();
		}
		
		return instance;
	}

	
	private AdvancedCache<Object, Object> cache;

	public SimpleCache() throws NamingException {
		this.init();
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
	
	//@PostConstruct
	private void init() throws NamingException {
		
		this.cache = manager.getCache().getAdvancedCache();
	}
	
	public Object get(String key) {
		try {
			log("executing get with key ", key, " on infinispan");
			Object value = null;
			
			TransactionManager tx = cache.getTransactionManager();
			System.out.println("CLASS IS "+cache.getClass());
			System.out.println("CLASS IS "+tx.getClass());

			
			try {
				tx.begin();

				log("obtain lock for key ", key);
				cache.lock(key);
				log("obtained lock for key ", key);
				
				value = cache.get(key);
				log("get object with key ", key," and value ", value);

				log("execution in started");
				Thread.sleep(10000);
				log("execution in completed");

				log("release lock for key ", key);
				tx.commit();
			}
			catch( Exception e ) {
				e.printStackTrace();
				tx.rollback();
			}
			
			return value;
		}
		catch( Exception e ) {
			throw new RuntimeException(e);
		}
	}

	public Object put(String key, Object value) {
		try {
			log("executing put with key ", key, " and value ", value, " on infinispan");
			Object oldValue = null;
			
			TransactionManager tx = cache.getTransactionManager();
			
			try {
				tx.begin();

				log("obtain lock for key ", key);
				cache.lock(key);
				log("obtained lock for key ", key);
				
				log("execution in started");
				Thread.sleep(10000);
				cache.put(key, value);
				log("execution in completed");
				
				log("release lock for key ", key);
				tx.commit();
			}
			catch( Exception e ) {
				tx.rollback();
			}
			
			return oldValue;
		}
		catch( Exception e ) {
			throw new RuntimeException(e);
		}
	}

}