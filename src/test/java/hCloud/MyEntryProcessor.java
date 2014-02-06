package hCloud;

import java.io.Serializable;
import java.util.Map.Entry;

import com.hazelcast.map.AbstractEntryProcessor;
import com.hictech.util.h.HCommon;

@SuppressWarnings("serial")
public class MyEntryProcessor extends AbstractEntryProcessor<String, String> implements Serializable {

	@Override
	public Object process(Entry<String, String> arg0) {
		HCommon.println(System.currentTimeMillis(), " SERVER EXECUTION");
		return null;
	}

}
