package com.hictech.htmlplus.cache;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.infinispan.AdvancedCache;
import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;


public class PlusCacheInfinispan implements PlusCache {
	
	final private static String CONTAINER = "java:jboss/infinispan/container/plus";
	
	final public static String SESSIONS_DEFAULT = "default";
	final public static String SESSIONS_HOSTS = "hosts";
	final public static String SESSIONS_CLIENTS = "clients";

	public static EmbeddedCacheManager cacheContainer() {
		try {
			return (EmbeddedCacheManager) InitialContext.doLookup(CONTAINER);
		}
		catch( NamingException e ) {
			throw new IllegalStateException(e);
		}
	}
	
	public static Object lockGet(PlusCacheInfinispan cache, String key) {
		Object result = null;
		
		try {
			cache.begin();
			cache.lock(key);
			
			result = cache.get(key);
			
			cache.commit();
			cache.dispose();
		}
		catch( Exception e ) {
			e.printStackTrace();
			
			cache.rollback();
		}
		finally {
			cache.dispose();
		}
		
		return result;
	}
	
	public static Object lockPut(PlusCacheInfinispan cache, String key, Object value, Runnable callback) {
		Object result = null;
		
		try {
			cache.begin();
			cache.lock(key);
			
			result = cache.put(key, value);
			if( callback != null ) {
				callback.run();
			}
			
			cache.commit();
			cache.dispose();
		}
		catch( Exception e ) {
			e.printStackTrace();
			
			cache.rollback();
		}
		finally {
			cache.dispose();
		}
		
		return result;
	}
	
	private AdvancedCache<Object, Object> cache;
	
	private TransactionManager tx;
	
	public PlusCacheInfinispan() {
		this((String) null);
	}
	
	public PlusCacheInfinispan(String name) {
		if( name == null ) {
			this.cache = cacheContainer().getCache().getAdvancedCache();
		}
		else {
			this.cache = cacheContainer().getCache(name).getAdvancedCache();
		}
	}
	
	public PlusCacheInfinispan(Cache<Object, Object> cache) {
		this.cache = cache.getAdvancedCache();
	}

	@Override
	public String name() {
		return cache.getName();
	}
	
	@Override
	public void begin() throws Exception {
		if( tx != null ) {
			throw new IllegalStateException("enable to begin a transaction because there is anothe one, call a commit before");
		}
		
		tx = cache.getTransactionManager();
		tx.begin();
	}

	@Override
	public void lock(String key) throws Exception {
		if( tx == null ) {
			throw new IllegalStateException("enable to call a lock because there is no active transaction");
		}
		
		boolean locked = cache.lock(key);
		if( !locked ) {
			throw new IllegalStateException("enable to aquire the lock on key "+key);
		}
	}

	@Override
	public Object get(String key) throws Exception {
		return cache.get(key);
	}

	@Override
	public Object getOrLoad(String key, PlusSource cacheSource) throws Exception {
		Object value = cache.get(key);
		
		if( value == null && cacheSource != null) {
			value = cacheSource.load(key);
			
			cache.put(key, value);
		}
		
		return value;
	}


	@Override
	public Object put(String key, Object value) throws Exception {
		if( value == null ) {
			return cache.remove(key);
		}
		
		return cache.put(key, value);
	}

	@Override
	public void commit() throws Exception {
		if( tx == null ) {
			throw new IllegalStateException("enable to call a commit because there is no active transaction");
		}
		
		tx.commit();
	}

	@Override
	public void rollback() {
		if( tx == null ) {
			throw new IllegalStateException("enable to call a rollback because there is no active transaction");
		}
		
		try {
			tx.rollback();
		}
		catch( Exception e ) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void dispose() {
		if( tx == null ) {
			return;
		}
		
		try {
			tx.suspend();
			tx = null;
		}
		catch( SystemException e ) {
			throw new RuntimeException(e);
		}
	}

}
