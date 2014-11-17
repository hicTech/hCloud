package com.hictech.htmlplus.cache;


/**
 * The interface class for htmlplus.
 * 
 * @author Andrea Cardamone
 * @version 1.0
 * @since 1.0
 */
public interface PlusCache {
	
	public String name();
	
	public void begin() throws Exception;
	
	public void lock(String key) throws Exception;

	public Object get(String key) throws Exception;

	public Object getOrLoad(String key, PlusSource cacheSource) throws Exception;

	public Object put(String key, Object value) throws Exception;
	
	public void commit() throws Exception;
	
	public void rollback() throws Exception;
	
	public void dispose() throws Exception;
	
}
