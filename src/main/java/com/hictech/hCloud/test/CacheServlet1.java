package com.hictech.hCloud.test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CacheServlet
 */
@WebServlet("/cache1")
public class CacheServlet1 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CacheServlet1() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {
			doGetUnsafe(request, response);
		}
		catch( Exception e ) {
			throw new RuntimeException(e);
		}
	}

	protected void doGetUnsafe(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter writer = response.getWriter();
		writer.write("test n 2...........................\n");

		writer.write("cache creating.....................\n");
		//SimpleCache cache = InitialContext.doLookup("java:app/hwildfly/SimpleCache");
 		SimpleCache cache = SimpleCache.getInstance();
		writer.write("cache created......................\n");
		
//		writer.write("cache getting......................\n");
//		String test1 = (String) cache.get("test");
//		writer.write("cache getted.......................\n");
//		writer.write("result: "+test1+"\n");
		
		
		writer.write("cache putting......................\n");
		cache.put("test", "this is a string test for wildfly cache\n");
		writer.write("cache putted.......................\n");
		
//		writer.write("cache getting......................\n");
//		String test2 = (String) cache.get("test");
//		writer.write("cache getted.......................\n");
		
//		writer.write("result: "+test2);
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
