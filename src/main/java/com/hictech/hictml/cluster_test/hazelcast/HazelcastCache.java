package com.hictech.hictml.cluster_test.hazelcast;

import com.hazelcast.core.IMap;
import com.hictech.hictml.cluster_test.Cache;
import com.hictech.hictml.cluster_test.CacheSource;
import com.hictech.util.h.HCommon;

public class HazelcastCache implements Cache {
	
	private IMap<String, Object> cache;
	
	public HazelcastCache() {
		this.cache = HazelcastTestSystem.cluster.getMap("cache");
	}
	
	public Object get(String key) {
		Object value;
		
		cache.lock(key);
		try {
			value = cache.get(key);
		}
		finally {
			cache.unlock(key);
		}
		
		return value;
	}

	public Object getOrLoad(String key, CacheSource source) {
		Object value;
		
		cache.lock(key);
		try {
			value = cache.get(key);
			HCommon.println("Cache ", this, " -> richiesto oggetto ", key, ", presente ? ", (value != null));
	
			if( value == null ) {
				value = source.load(key);
	
				cache.put(key, value);
			}
		}
		finally {
			cache.unlock(key);
		}

		return value;
	}

	public Object put(String key, Object value) {
		Object oldValue;
		
		cache.lock(key);
		try {
			HCommon.println("Cache ", this, " -> inserimento oggetto ", key);
	
			oldValue = cache.get(key);
	
			cache.put(key, value);
		}
		finally {
			cache.unlock(key);
		}

		return oldValue;
	}

}
