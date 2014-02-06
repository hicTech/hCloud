package com.hictech.hictml.cluster_test.bigmemory;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import com.hictech.hictml.cluster_test.Cache;
import com.hictech.hictml.cluster_test.CacheSource;
import com.hictech.util.h.HCommon;

import static net.sf.ehcache.CacheManager.newInstance;
import static com.hictech.util.h.HCommon.getResource;

public class BigmemoryCache implements Cache {

	private static final String EHCACHE = "/ehcache.xml";
	private static final String EHCACHE_SERVER_ARRAY = "server-array";

//	private static final CacheManager manager = newInstance(getResource(BigmemoryCache.class, EHCACHE));

	private net.sf.ehcache.Cache ehcache;

	public BigmemoryCache() {
//		ehcache = manager.getCache(EHCACHE_SERVER_ARRAY);
	}

	public Object get(String key) {
		Object value;
		
		ehcache.acquireWriteLockOnKey(key);
		{
			 value = ehcache.get(key).getObjectValue();
		}
		ehcache.releaseWriteLockOnKey(key);

		return value;
	}

	public Object getOrLoad(String key, CacheSource source) {
		Object value;
		
		ehcache.acquireWriteLockOnKey(key);
		{
			Element entry = ehcache.get(key);
			HCommon.println("Cache ", this, " -> richiesto oggetto ", key, ", presente ? ", (entry != null));
	
			if( entry == null ) {
				value = source.load(key);
				entry = new Element(key, value);
	
				ehcache.put(entry);
			}
			else {
				value = entry.getObjectValue();
			}
		}
		ehcache.releaseWriteLockOnKey(key);

		return value;
	}

	public Object put(String key, Object value) {
		Element oldValue;
		
		ehcache.acquireWriteLockOnKey(key);
		{
			HCommon.println("Cache ", this, " -> inserimento oggetto ", key);
	
			oldValue = ehcache.get(key);
	
			ehcache.put(new Element(key, value));
		}
		ehcache.releaseWriteLockOnKey(key);

		return oldValue != null ? oldValue.getObjectValue() : null;
	}

}
