package com.hictech.hictml.cluster_test.hazelcast;

import java.io.IOException;

import com.hictech.hictml.cluster_test.FileSystem;

public class HazelcastFileSystem implements FileSystem{

	
	public String rootPath() throws IOException{
		return "/shared_data/";
	}
	
	public boolean doesExist(String path) throws IOException {
		return false;
	}

	public byte[] readFile(String path) throws IOException {
		System.out.println("\nCLUSTER FILE SYSTEM -> READ("+path+")\n");
		return new byte[0];
	}

	public void writeFile(String path, byte[] content) throws IOException {
		System.out.println("\nCLUSTER FILE SYSTEM -> WRITE("+path+", "+ ((content!=null) ? (content.length+" bytes") : "null") +")\n");
	}

}
