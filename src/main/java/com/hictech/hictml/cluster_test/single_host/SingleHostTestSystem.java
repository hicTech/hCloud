package com.hictech.hictml.cluster_test.single_host;

import com.hictech.hictml.cluster_test.Cache;
import com.hictech.hictml.cluster_test.FileSystem;
import com.hictech.hictml.cluster_test.Locker;
import com.hictech.hictml.cluster_test.TestSystem;

public class SingleHostTestSystem implements TestSystem{

	public Cache getCache(){
		return new SingleHostCache();
	}
	
	public FileSystem getFileSystem(){
		return new SingleHostFileSystem();
	}

	public Locker getLocker() {
		return new SingleHostLocker();
	}
	
}
