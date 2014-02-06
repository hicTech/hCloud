package com.hictech.hictml.cluster_test.single_host;

import java.util.Hashtable;

import com.hictech.hictml.cluster_test.Cache;
import com.hictech.hictml.cluster_test.CacheSource;

public class SingleHostCache implements Cache{

	private Hashtable<String, Object> table;
	
	public SingleHostCache(){
		table = new Hashtable<String, Object>();
	}
	
	public Object get(String key){
		return table.get(key);
	}
	
	public Object getOrLoad(String key, CacheSource source) {
		Object ret = table.get(key);
		System.out.println("Cache "+this+" -> richiesto oggetto "+key+", presente ? "+(ret!=null));
		if(ret==null){
			ret = source.load(key);
			this.put(key, ret);
		}
		return ret;
	}
	
	
	public Object put(String key, Object obj){
		System.out.println("Cache "+this+" -> inserimento oggetto "+key);
		return table.put(key, obj);
	}
	
}
