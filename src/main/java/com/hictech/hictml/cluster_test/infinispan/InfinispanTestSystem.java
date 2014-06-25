package com.hictech.hictml.cluster_test.infinispan;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.infinispan.manager.EmbeddedCacheManager;

import com.hictech.hictml.cluster_test.Cache;
import com.hictech.hictml.cluster_test.FileSystem;
import com.hictech.hictml.cluster_test.Locker;
import com.hictech.hictml.cluster_test.TestSystem;
import com.hictech.hictml.cluster_test.single_host.SingleHostFileSystem;

public class InfinispanTestSystem implements TestSystem {
	
	private static EmbeddedCacheManager instance;
	
	public static EmbeddedCacheManager infinispan() {
		if( instance == null ) {
			try {
				instance = (EmbeddedCacheManager) InitialContext.doLookup("java:jboss/infinispan/container/web");
			}
			catch( NamingException e ) {
				e.printStackTrace();

				throw new RuntimeException(e);
			}
		}
		
		return instance;
	}
	
	public Cache createCache() {
		return new InfinispanCache();
	}

	public FileSystem createFileSystem() {
		return new SingleHostFileSystem();
	}

	@Override
	public Locker createLocker() {
		return new InfinispanLocker();
	}

}
