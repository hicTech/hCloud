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
		HCommon.printfln("executing get with key %s on hazelcast", key);
		Object value;
		
		HCommon.printfln("obtain lock for key %s", key);
		cache.lock(key);
		try {
			value = cache.get(key);
			HCommon.printfln("get object with key %s and value %s", key, value);
		}
		finally {
			cache.unlock(key);
		}
		HCommon.printfln("release lock for key %s", key);
		
		return value;
	}

	public Object getOrLoad(String key, CacheSource source) {

		HCommon.printfln("executing getOrLoad with key %s on hazelcast", key);
		Object value;
		
		HCommon.printfln("obtain lock for key %s", key);
		cache.lock(key);
		try {
			value = cache.get(key);
			HCommon.printfln("get object with key %s and value %s", key, value);
	
			if( value == null ) {
				value = source.load(key);
	
				cache.put(key, value);
				HCommon.printfln("put object with key %s and value %s", key, value);
			}
		}
		finally {
			cache.unlock(key);
		}
		HCommon.printfln("release lock for key %s", key);

		return value;
	}

	public Object put(String key, Object value) {
		HCommon.printfln("executing put with key %s and value on hazelcast", key, value);
		Object oldValue;
		
		HCommon.printfln("obtain lock for key %s", key);
		cache.lock(key);
		try {
			oldValue = cache.get(key);
			HCommon.printfln("get old object with key %s and value %s", key, value);

			cache.put(key, value);
			HCommon.printfln("put new object with key %s and value %s", key, value);

		}
		finally {
			cache.unlock(key);
		}
		HCommon.printfln("release lock for key %s", key);

		return oldValue;
	}

}
