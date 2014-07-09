package com.hictech.hictml.cluster_test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.hictech.hictml.cluster_test.infinispan.InfinispanTestSystem;
import com.hictech.hictml.cluster_test.single_host.SingleHostTestSystem;
import com.hictech.util.CommandUtils;
import com.hictech.util.NestedException;
import com.hictech.util.h.HChecks;
import com.hictech.util.h.HCommon;
import com.hictech.util.h.json.HJSON;
import com.hictech.util.linuxsystem.HardwareResources;

public class Tester{
	
	
	private static boolean inited = false;
	private static Tester single_host_tester;
	private static Tester bigmemory_tester;
	private static Tester hazelcast_tester;
	private static Tester infinispan_tester;

	public static synchronized void initSystems(){
		if(!inited){
			single_host_tester = new Tester(new SingleHostTestSystem());
			//bigmemory_tester = new Tester(new BigmemoryTestSystem());
			//hazelcast_tester = new Tester(new HazelcastTestSystem());
			infinispan_tester = new Tester(new InfinispanTestSystem());
			inited = true;
		}
	}
	
	public static Tester get(String cluster){
		initSystems();
		
		if( HCommon.equals(cluster, "single_host") ) {
			return single_host_tester;
		}
		else if( HCommon.equals(cluster, "cluster_bigmemory") ) {
			return bigmemory_tester;
		}
		else if( HCommon.equals(cluster, "cluster_hazelcast") ) {
			return hazelcast_tester;
		}
		else if( HCommon.equals(cluster, "cluster_infinispan") ) {
			return infinispan_tester;
		}
		else {
			throw HChecks.illegalArg("invalid cluster option %s", cluster);
		}
	}
	
	public static Tester get(){
		return get("single_host");
	}
	
	
	
	
	
	private Cache cache;
	private FileSystem file_system;
	private Locker locker;
	
	public Tester(TestSystem system){
		cache = system.getCache();
		file_system = system.getFileSystem();
		locker = system.getLocker();
	}
	
	public Cache cache(){
		return cache;
	}
	public FileSystem fileSystem(){
		return file_system;
	}
	
	public Locker locker(){
		return locker;
	}
	
	
	public class FileCacheSource implements CacheSource{

		public Object load(String key) {
			TestObject ret = readObj(key);
			if(ret==null){
				ret = TestObject.create(key);
				writeObj(ret);
			}
			return ret;
		}
		
	}
	
	
	
	
	
	public Map<String, Object> test(Runner runner) throws Exception{
		HCommon.printfln("starting cache getting of object %s", runner.getObjId());
		Object cache_obj = cache.getOrLoad(runner.getObjId(), new FileCacheSource());
		
		TestObject obj = TestObject.wrap(cache_obj);
		
		HCommon.printfln("starting lock block on object %s", runner.getObjId());
		Map<String, Object> result;
		locker.lock(runner.getObjId());
		{
			result = obj.test(runner);

			writeObj(obj);
		}
		locker.unlock(runner.getObjId());

		HCommon.printfln("last cache put on object %s", obj);
		cache.put(runner.getObjId(), obj);
		return result;
	}
	
	
	
	
	
	
	
	
	
	public static Map<String, Object> hostInfo() throws IOException{
		Map<String, Object> ret = new HashMap<String, Object>();
		
		CommandUtils.ProcessResult result = CommandUtils.executeCommand("hostname", CommandUtils.getBufferingListener());
		ret.put("host_name", result.getNormalOutput().replace('\n',' ').trim());
		
		result = CommandUtils.executeCommand("whoami", CommandUtils.getBufferingListener());
		ret.put("user_name", result.getNormalOutput().replace('\n',' ').trim());
		
		Map<String, Object> ips = new HashMap<String, Object>();
		String[] nets = new String[]{"eth0","wlan0","eth1","wlan1","eth2","wlan2"};
		for(String net:nets){
			try{
				String ip = HardwareResources.getCurrentIp(net);
				ips.put(net, ip);
			}catch(Exception e){}
		}
		ret.put("ips",ips);
		
		return ret;
	}
	

	
	
	
	
	
	public String objFilePath(String obj_id){
		return (obj_id+".json");
	}
	
	public String objFilePath(int obj_id){
		return objFilePath(""+obj_id);
	}
	
	public TestObject readObj(String obj_id){
		try{
			String path = objFilePath(obj_id);
			if(file_system.doesExist(path)){
				byte[] bytes = file_system.readFile(objFilePath(obj_id));
				return TestObject.parse(bytes);
			}
			else
				return null;
		}catch(Exception e){
			throw new NestedException(e);
		}
	}
	
	public void writeObj(TestObject obj){
		try{
			String path = objFilePath(obj.id());
			byte[] bytes = HJSON.toString(obj).getBytes();
			file_system.writeFile(path, bytes);
		}catch(Exception e){
			throw new NestedException(e);
		}
	}
	
	
}
