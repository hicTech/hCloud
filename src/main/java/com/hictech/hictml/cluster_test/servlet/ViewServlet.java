package com.hictech.hictml.cluster_test.servlet;

import static com.hictech.htmlplus.cache.PlusCacheInfinispan.cacheContainer;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.infinispan.Cache;

import com.hictech.util.h.json.HJSON;

@WebServlet("/view")
public class ViewServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doJob(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doJob(request, response);
	}


	public void doJob(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String key = request.getParameter("key");
		
		String result = null;
		 
		Cache<Object, Object> cache = cacheContainer().getCache("cache");
		if( key == null || key.length() == 0 ) {
			result = HJSON.toString(cache);
		}
		else {
			result = HJSON.toString(cache.get(key));
		}
		
		response.getWriter().write(result);
	}

}
