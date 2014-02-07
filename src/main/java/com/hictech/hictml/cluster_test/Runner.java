package com.hictech.hictml.cluster_test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.hictech.util.h.HChecks;
import com.hictech.util.h.HCommon;
import com.hictech.util.h.HStrings;

public class Runner implements Runnable {

	private static long counter = 0;
	private static Map<String, byte[]> map = new HashMap<String, byte[]>();
	
	private static synchronized String getKey() {
		return String.valueOf(++counter);
	}
	
	private static byte[] getValue(int mb) {
		byte[] bytes = new byte[1048576*mb];
		
//		Arrays.fill(bytes, (byte) 1);
		
		return bytes;
	}
	
	private String set_id;
	private String obj_id;
	private long mseconds;
	private int mbytes;
	private String mode;
	private long cycles;
	
	public Runner(String set_id, String obj_id, String mode, long ms, int size) {
		this.set_id = set_id;
		this.obj_id = obj_id;
		this.mode = mode;
		this.mseconds = ms;
		this.mbytes = size;
		this.cycles = 0;
	}
	
	public long getMSeconds() {
		return mseconds;
	}
	
	public long getMBytes() {
		return mbytes;
	}
	
	public long getCycles() {
		return cycles;
	}
	
	private void sleep(long ms) throws Exception {
		HCommon.printfln("executing sleep test %s of object %s for %sms", set_id, obj_id, ms);
		Thread.sleep(ms);
		HCommon.printfln("executed sleep test %s of object %s for %sms", set_id, obj_id, ms);
	}
	
	private void cpu(long ms) throws Exception {
		HCommon.printfln("executing cpu test %s of object %s for %sms", set_id, obj_id, ms);
		long start = HCommon.time();
		
		while( true ) {
			long now = HCommon.time();
			if( (now-start) > ms ) {
				break;
			}
			
			Math.sqrt(new Random().nextLong());
			
			cycles++;
		}
		
		HCommon.printfln("executed cpu test %s of object %s for %sms", set_id, obj_id, ms);
	}
	
	@Override
	public void run() {
		String key = null;
		
		try {
			if( mbytes > 0 ) {
				map.put(getKey(), getValue(mbytes));
			}
			
			if( mseconds > 0 ) {
				if( HStrings.equals(mode, "sleep") ) {
					sleep(mseconds);
				}
				
				else if( HStrings.equals(mode, "sqrt") ) {
					cpu(mseconds);
				}
				
				else {
					throw HChecks.illegalState("invalid mode %s", mode);
				}
			}
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		finally {
			map.remove(key);
		}
	}
	
}
