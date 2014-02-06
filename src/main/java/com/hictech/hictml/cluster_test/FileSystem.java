package com.hictech.hictml.cluster_test;

import java.io.IOException;

public interface FileSystem {

	public String rootPath() throws IOException;
	
	public boolean doesExist(String path) throws IOException;
	
	public byte[] readFile(String path) throws IOException;
	
	public void writeFile(String path, byte[] content) throws IOException;
	
}
