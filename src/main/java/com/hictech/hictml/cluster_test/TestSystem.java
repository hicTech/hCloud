package com.hictech.hictml.cluster_test;

public interface TestSystem{

	public Cache createCache();
	
	public FileSystem createFileSystem();
	
	public Locker createLocker();
}
