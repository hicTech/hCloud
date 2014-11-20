package com.hictech.htmlplus.servlet;
import static com.hictech.htmlplus.cache.PlusCacheInfinispan.SESSIONS_CLIENTS;
import static com.hictech.htmlplus.cache.PlusCacheInfinispan.SESSIONS_HOSTS;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONValue;

import com.hictech.htmlplus.cache.PlusCacheInfinispan;

/**
 * Servlet implementation class CacheServletPlus
 */
@SuppressWarnings("serial")
@WebServlet(name="CacheServlet", urlPatterns={"/hosts", "/clients", "/cache"})
public class PlusServlet extends HttpServlet {

	final private static List<String> parseHTTPRequestAction(HttpServletRequest httpRequest) throws Exception {
		/* Gets charset from request */
		String charset = httpRequest.getCharacterEncoding();
		if( charset == null || charset.length() == 0 ) {
			charset = "UTF-8";
		}
		
		/* Gets the HTTP URI */
		String uri = httpRequest.getRequestURI();
		uri = URLDecoder.decode(uri, charset);
	
		/* Gets the context and path */
		String[] path = StringUtils.split(uri, "/");
		String[] ctx = StringUtils.split(httpRequest.getContextPath(), "/");
		List<String> result = new ArrayList<>();
		for( int i = ctx.length; i < path.length; i++ ) {
			result.add(path[i]);
		}
		
		return result;
	}
	
	final public static PlusSession getSession(String type, HttpSession httpSession) throws Exception {
		if( httpSession == null ) {
			throw new IllegalArgumentException("enable to get htmlplus session because the http session is not defined");
		}
		
		Object attr = httpSession.getAttribute(type);
		if( attr == null ) {
			String id = httpSession.getId();
			attr = new PlusSession(type, id);
			System.out.println("session id: "+id);
			
			
			httpSession.setAttribute(type, attr);
		}
		else if( !(attr instanceof PlusSession) ) { 
			throw new IllegalStateException("invalid http session because the htmlplus session class is "+attr.getClass());
		}
		
		return (PlusSession) attr;
	}


	/*
	 * (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
		try {
			List<String> path = parseHTTPRequestAction(httpRequest);
			if( path.size() == 0 ) {
				httpResponse.getWriter().write("ciao");
			}
			
			else if( path.get(0).equals("hosts") ) {
				doGetHosts(httpRequest, httpResponse);
			}
			
			else if( path.get(0).equals("clients") ) {
				doGetClients(httpRequest, httpResponse);
			}

			else if( path.get(0).equals("cache") ) {
				doGetCache(httpRequest, httpResponse);
			}
		}
		catch( ServletException | IOException e ) {
			throw e;
		}
		catch( Exception e ) {
			e.printStackTrace(httpResponse.getWriter());
		}
	}
	
	private void doGetCache(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		PrintWriter pw = httpResponse.getWriter();
	
		JSONValue.writeJSONString(new PlusCacheInfinispan(), pw);
	}

	private void doGetHosts(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		PrintWriter pw = httpResponse.getWriter();
		
		HttpSession httpSession = httpRequest.getSession(true);
		if( httpRequest.getParameter("invalidate") != null && httpSession != null ) {
			PlusSession session = getSession(SESSIONS_HOSTS, httpSession);
			session.invalidate();
			
			httpSession.invalidate();
		}
		else {
			httpSession = httpRequest.getSession(true);
			PlusSession session = getSession(SESSIONS_HOSTS, httpSession);
			JSONValue.writeJSONString(session, pw);
		}
	}


	private void doGetClients(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		PrintWriter pw = httpResponse.getWriter();
		
		HttpSession httpSession = httpRequest.getSession(true);
		if( httpRequest.getParameter("invalidate") != null && httpSession != null ) {
			PlusSession session = getSession(SESSIONS_CLIENTS, httpSession);
			session.invalidate();
			
			httpSession.invalidate();
		}
		else {
			httpSession = httpRequest.getSession(true);
			PlusSession session = getSession(SESSIONS_CLIENTS, httpSession);
			JSONValue.writeJSONString(session, pw);
		}
		
	}

}
