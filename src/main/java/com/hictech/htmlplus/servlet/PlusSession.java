package com.hictech.htmlplus.servlet;
import static com.hictech.htmlplus.cache.PlusCacheInfinispan.lockPut;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import com.hictech.htmlplus.cache.PlusCache;
import com.hictech.htmlplus.cache.PlusCacheInfinispan;

public class PlusSession {

	private String sessionID;
	private PlusCacheInfinispan cache;
	
	public PlusSession(String type, String id) throws Exception {
		this.sessionID = id;
		
		this.cache = new PlusCacheInfinispan(type);
	}
	
	public String id() {
		return sessionID;
	}
	
	public PlusCache cache() {
		return cache();
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> get() throws Exception {
		Map<String, Object> result = null;
		
		try {
			cache.begin();
			cache.lock(sessionID);
			
			result = (Map<String, Object>) cache.get(sessionID);
			if( result == null ) {
				result = new HashMap<String, Object>();
				result.put("id", sessionID);
				
				cache.put(sessionID, result);
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
	
	public void invalidate() throws Exception {
		lockPut(cache, sessionID, null, null);
	}
	
	@Override
	public String toString() {
		try {
			return JSONValue.toJSONString(get());
		}
		catch( Exception e ) {
			throw new RuntimeException(e);
		}
	}
	
}
