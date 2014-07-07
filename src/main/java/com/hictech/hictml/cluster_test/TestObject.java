package com.hictech.hictml.cluster_test;

import static com.hictech.util.h.HCommon.str;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hictech.util.FormatUtils;
import com.hictech.util.NestedException;
import com.hictech.util.h.HChecks;
import com.hictech.util.h.HCommon;
import com.hictech.util.h.HTree;
import com.hictech.util.h.json.HJSON;

public class TestObject extends HashMap<String, Object> implements Serializable {

	private static final long serialVersionUID = -6292071362762849284L;
	
	public static String TEXT_ENCODING = "UTF-8";
	
	TestObject(Map<String, Object> map) {
		super(map);
	}
	
	@SuppressWarnings("unchecked")
	public static TestObject wrap(Object object) {
		try {
			if( object == null ) {
				return new TestObject(new HTree());
			}
			else if( object instanceof TestObject ) {
				return (TestObject) object;
			}
			if( object instanceof Map ) {
				return new TestObject((Map<String,Object>) object);
			}
			else {
				throw HChecks.illegalArg("enable to wrap %s as %s", HCommon.getClass(object), HTree.class);
			}
		}
		catch( Exception e ) {
			throw new NestedException(e);
		}
	}
	
	
	public static TestObject parse(String json){
		try{
			Map<String, Object> map = HJSON.toObject(json);
			
			return new TestObject(map);
		}catch(Exception e){
			throw new NestedException(e);
		}
	}
	public static TestObject parse(byte[] bytes){
		try{
			String text = new String(bytes, TEXT_ENCODING);
			return parse(text);
		}catch(Exception e){
			throw new NestedException(e);
		}
	}
	public static TestObject create(String id){
		return parse("{ \"id\": "+id+" }");
	}
	public static TestObject create(int id){
		return create(str(id));
	}
	
	
	public byte[] bytes(){
		try{
			String text = this.toString();
			return text.getBytes(TEXT_ENCODING);
		}catch(Exception e){
			throw new NestedException(e);
		}
	}
	
	
	
	
	public int id(){
		return HCommon.toInt(get("id"));
	}
	
	public String dateStr(long millis){
		return FormatUtils.getInstant(millis, false, true, true);
	}
	
	
	public void addTestResult(Map<String, Object> result) {
		List<Object> arr = new ArrayList<Object>();
		arr.add(result);
		
		if( get("tests") != null ) {
			@SuppressWarnings("unchecked")
			List<Object> prev = (List<Object>) get("tests");
			int size = prev.size();
			for(int i=0; i<size; i++)
				arr.add(prev.get(i));
		}
		
		this.put("tests", arr);
	}
	
	
	public Map<String, Object> test(Runner runner) throws Exception{
		Map<String, Object> test_obj = null;
		
		long millis = runner.getMSeconds();
		String obj_id = str(this.id());
		long start_ms = System.currentTimeMillis();
		System.out.println("Test started for object "+obj_id+" within test set: "+runner.getSetId()+" (millis: "+millis+")");
		
		runner.run();
		
		//CommandUtils.ProcessResult if_config_result = CommandUtils.executeCommand("ifconfig", CommandUtils.getBufferingListener());
		Map<String, Object> host_info = Tester.hostInfo();
		
		long end_ms = System.currentTimeMillis();
		System.out.println("Test done for object "+obj_id+" within test set: "+runner.getSetId()+" (elapsed millis: "+(end_ms-start_ms)+"/"+millis+")");
		
		test_obj = new HashMap<String, Object>();
		test_obj.put("host", host_info);
		test_obj.put("set", runner.getSetId());
		test_obj.put("start", this.dateStr(start_ms));
		test_obj.put("end", this.dateStr(end_ms));
		
		this.addTestResult(test_obj);
			
		return test_obj;
	}
	
	
}
