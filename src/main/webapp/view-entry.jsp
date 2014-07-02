<%@page language="java" contentType="text/plain; charset=UTF-8" pageEncoding="UTF-8"%>

<%@page import="com.hictech.util.h.json.HJSON"%>
<%@page import="com.hictech.hictml.cluster_test.infinispan.InfinispanTestSystem"%>
<%@page import="org.infinispan.Cache"%>

<%
	Cache<?,?> cache = InfinispanTestSystem.infinispan().getCache();
	String key = request.getParameter("key");
%>
	
<%= HJSON.toString(cache.get(key)) %>
