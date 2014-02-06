package com.hictech.hictml.cluster_test.hazelcast;

import java.io.InputStream;
import java.util.concurrent.Executor;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import com.hictech.hictml.cluster_test.Cache;
import com.hictech.hictml.cluster_test.FileSystem;
import com.hictech.hictml.cluster_test.Locker;
import com.hictech.hictml.cluster_test.TestSystem;
import com.hictech.hictml.cluster_test.single_host.SingleHostFileSystem;
import com.hictech.util.h.HCommon;

public class HazelcastTestSystem implements TestSystem{
	public static HazelcastInstance cluster;
	
	static {
		InputStream input = HCommon.getResource(HazelcastCache.class, "/hazelcast-client.xml");
		ClientConfig clientConfig = new XmlClientConfigBuilder(input).build();
		
		cluster = HazelcastClient.newHazelcastClient(clientConfig);
	}

	public Cache createCache() {
		return new HazelcastCache();
	}

	public FileSystem createFileSystem() {
		return new SingleHostFileSystem();
	}

	@Override
	public Locker createLocker() {
		return new HazelcastLocker();
	}
	
	

}
