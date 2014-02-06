package com.hictech.hictml.cluster_test.single_host;

import java.io.IOException;

import com.hictech.hictml.cluster_test.FileSystem;
import com.hictech.util.FileUtils;
import com.hictech.util.RuntimeUtils;

public class SingleHostFileSystem implements FileSystem{

	public String filePath(String path){
		return (rootPath()+FileUtils.SEPARATOR+path);
	}
	
	
	
	public String rootPath(){
		return RuntimeUtils.getUserHomeDirectory()+FileUtils.SEPARATOR+"hictml_cluster_test";
	}
	
	public boolean doesExist(String path) throws IOException {
		return FileUtils.doesExist(filePath(path));
	}
	
	public byte[] readFile(String path) throws IOException{
		return FileUtils.readFile(filePath(path));
	}
	
	public void writeFile(String path, byte[] content) throws IOException{
		FileUtils.writeFile(filePath(path), content);
	}

}
