package hCloud;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hictech.util.h.HCommon;

public class HazelcastClientTest extends Thread {

	public static void main(String[] args) {
		List<Thread> threads = new ArrayList<Thread>();
		int n = 1;
		for (int i = 0; i < n; i++) {
			threads.add(new HazelcastClientTest());
		}

		for (Thread thread : threads) {
			thread.start();
		}
	}

	public void run() {
		try {
			unsafeRun();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void unsafeRun() throws Exception {
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.addAddress(
			"10.11.12.2:5701",
			"10.11.12.2:5701",
			"10.11.12.3:5701",
			"10.11.12.4:5701",
			"10.11.12.5:5701",
			"10.11.12.6:5701",
			"10.11.12.7:5701",
			"10.11.12.8:5701",
			"10.11.12.9:5701",
			"10.11.12.10:5701"
		);
		InputStream input = HCommon.getResource("/hazelcast-client.xml");
		clientConfig = new XmlClientConfigBuilder(input).build();
		HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);

		IMap<String, String> map = client.getMap("customers");
		for (Object value : map.values()) {
			System.out.println(value);
		}
		map.executeOnKey("" + new Random().nextInt(), new MyEntryProcessor());

		client.shutdown();
	}

}
