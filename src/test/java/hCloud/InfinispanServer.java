package hCloud;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class InfinispanServer {
	private static EmbeddedCacheManager manager = new DefaultCacheManager(true);
	
	public static void main(String[] args) {
		org.infinispan.Cache<String, Object> cache;
		
		cache = manager.getCache();
		
		String key = "key";
		cache.put(key, "value");
		Object value = cache.get(key);
		
		System.out.println("value is "+value);
		
		
	}
	
	
}
