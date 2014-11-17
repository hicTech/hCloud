package com.hictech.htmlplus.servlet;

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
		Map<String, Object> map = (Map<String, Object>) cache.get(sessionID);
		if( map == null ) {
			map = new HashMap<>();
			map.put("id", sessionID);
			
			try {
				cache.begin();
				cache.lock(sessionID);
				cache.put(sessionID, map);
				cache.commit();
				cache.dispose();
			}
			catch( Exception e ) {
				e.printStackTrace();
				cache.rollback();
				
				throw e;
			}
			finally {
				cache.dispose();
			}
		}
		
		return map;
	}
	
	public void invalidate() throws Exception {
		try {
			cache.begin();
			cache.lock(sessionID);
			cache.put(sessionID, null);
			cache.commit();
			cache.dispose();
		}
		catch( Exception e ) {
			cache.rollback();
			
			throw e;
		}
		finally {
			cache.dispose();
		}
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
