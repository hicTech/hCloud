package com.hictech.hictml.cluster_test.infinispan;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.infinispan.manager.CacheContainer;

import com.hictech.hictml.cluster_test.Cache;
import com.hictech.hictml.cluster_test.FileSystem;
import com.hictech.hictml.cluster_test.Locker;
import com.hictech.hictml.cluster_test.TestSystem;
import com.hictech.hictml.cluster_test.single_host.SingleHostFileSystem;

public class InfinispanTestSystem implements TestSystem {
	
	private CacheContainer getCacheContainer(String name) {
		try {
			return (CacheContainer) InitialContext.doLookup("java:jboss/infinispan/container/"+name);
		}
		catch( NamingException e ) {
			throw new IllegalStateException(e);
		}
	}
	
	@Override
	public Cache getCache() {
		return new InfinispanCache(getCacheContainer("cache").getCache());
	}

	@Override
	public FileSystem getFileSystem() {
		return new SingleHostFileSystem();
	}

	@Override
	public Locker getLocker() {
		return new InfinispanLocker(getCacheContainer("locks").getCache());
	}

}
