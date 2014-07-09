package com.hictech.hictml.cluster_test.bigmemory;

import com.hictech.hictml.cluster_test.Cache;
import com.hictech.hictml.cluster_test.FileSystem;
import com.hictech.hictml.cluster_test.Locker;
import com.hictech.hictml.cluster_test.TestSystem;
import com.hictech.hictml.cluster_test.single_host.SingleHostFileSystem;
import com.hictech.hictml.cluster_test.single_host.SingleHostLocker;

public class BigmemoryTestSystem implements TestSystem{

	public Cache getCache() {
		return new BigmemoryCache();
	}

	public FileSystem getFileSystem() {
		return new SingleHostFileSystem();
	}
	
	public Locker getLocker() {
		return new SingleHostLocker();
	}

}
