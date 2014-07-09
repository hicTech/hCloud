package com.hictech.hictml.cluster_test;

public interface TestSystem{

	public Cache getCache();
	
	public FileSystem getFileSystem();
	
	public Locker getLocker();
}
