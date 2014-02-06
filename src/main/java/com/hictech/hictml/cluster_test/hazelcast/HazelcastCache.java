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
		HCommon.print("executing get with key %s on hazelcast", key);
		Object value;
		
		HCommon.println("obtain lock for key %s", key);
		cache.lock(key);
		try {
			value = cache.get(key);
			HCommon.println("get object with key %s and value %s", key, value);
		}
		finally {
			cache.unlock(key);
		}
		HCommon.println("release lock for key %s", key);
		
		return value;
	}

	public Object getOrLoad(String key, CacheSource source) {

		HCommon.print("executing getOrLoad with key %s on hazelcast", key);
		Object value;
		
		HCommon.println("obtain lock for key %s", key);
		cache.lock(key);
		try {
			value = cache.get(key);
			HCommon.println("get object with key %s and value %s", key, value);
	
			if( value == null ) {
				value = source.load(key);
	
				cache.put(key, value);
				HCommon.println("put object with key %s and value %s", key, value);
			}
		}
		finally {
			cache.unlock(key);
		}
		HCommon.println("release lock for key %s", key);

		return value;
	}

	public Object put(String key, Object value) {
		HCommon.print("executing put with key %s and value on hazelcast", key, value);
		Object oldValue;
		
		HCommon.println("obtain lock for key %s", key);
		cache.lock(key);
		try {
			oldValue = cache.get(key);
			HCommon.println("get old object with key %s and value %s", key, value);

			cache.put(key, value);
			HCommon.println("put new object with key %s and value %s", key, value);

		}
		finally {
			cache.unlock(key);
		}
		HCommon.println("release lock for key %s", key);

		return oldValue;
	}

}
