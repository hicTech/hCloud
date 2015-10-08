package com.hictech.hictml.cluster_test.infinispan;

import static com.hictech.htmlplus.cache.PlusCacheInfinispan.cacheContainer;

import com.hictech.hictml.cluster_test.Cache;
import com.hictech.hictml.cluster_test.FileSystem;
import com.hictech.hictml.cluster_test.Locker;
import com.hictech.hictml.cluster_test.TestSystem;
import com.hictech.hictml.cluster_test.single_host.SingleHostFileSystem;

public class InfinispanTestSystem implements TestSystem {
	
	
	public InfinispanTestSystem() {
	}
	
	@Override
	public Cache getCache() {
		return new InfinispanCache(cacheContainer().getCache("cache"));
	}

	@Override
	public FileSystem getFileSystem() {
		return new SingleHostFileSystem();
	}

	@Override
	public Locker getLocker() {
		return new InfinispanLocker(cacheContainer().getCache("locks"));
	}

}
