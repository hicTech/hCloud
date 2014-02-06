package com.hictech.hictml.cluster_test;

public interface Cache {

	public Object get(String key);
	
	public Object getOrLoad(String key, CacheSource source);
	
	public Object put(String key, Object obj);
	
}
