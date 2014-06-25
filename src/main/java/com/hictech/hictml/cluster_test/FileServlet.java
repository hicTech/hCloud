package com.hictech.hictml.cluster_test;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hictech.util.MimeObject;

@WebServlet("/test_objects")
public class FileServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doJob(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException{
		String file_path=request.getPathInfo();
		if(file_path.startsWith("/"))
			file_path = file_path.substring(1);
		
		file_path = Tester.get().fileSystem().rootPath()+"/"+file_path+".json";
		MimeObject mime=MimeObject.readFile(file_path);
		if(mime==null)
			throw new IllegalArgumentException("File not found: "+file_path);
		response.setContentType(mime.getContentType());
		response.setContentLength((int)mime.getContentSize());
		OutputStream out = response.getOutputStream();
	    out.write(mime.getContent(),0,mime.getContent().length);
        out.close();
        //System.out.println("File served: "+file_path);
	}
	
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException{
		doJob(request, response);
	}
		
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException{
		doJob(request, response);
	}
}
