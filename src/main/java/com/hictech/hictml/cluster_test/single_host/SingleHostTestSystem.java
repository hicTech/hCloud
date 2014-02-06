package com.hictech.hictml.cluster_test.single_host;

import com.hictech.hictml.cluster_test.Cache;
import com.hictech.hictml.cluster_test.FileSystem;
import com.hictech.hictml.cluster_test.Locker;
import com.hictech.hictml.cluster_test.TestSystem;
import com.hictech.hictml.cluster_test.single_host.SingleHostLocker;
import com.hictech.hictml.cluster_test.single_host.SingleHostFileSystem;

public class SingleHostTestSystem implements TestSystem{

	public Cache createCache(){
		return new SingleHostCache();
	}
	
	public FileSystem createFileSystem(){
		return new SingleHostFileSystem();
	}

	public Locker createLocker() {
		return new SingleHostLocker();
	}
	
}
