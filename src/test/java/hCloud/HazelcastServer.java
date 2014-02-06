package hCloud;

import com.hazelcast.core.Hazelcast;

public class HazelcastServer {

	public static void main(String[] args) {
        Hazelcast.newHazelcastInstance();
	}

}
