<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@page import="com.hictech.hictml.cluster_test.infinispan.InfinispanTestSystem"%>
<%@page import="org.infinispan.Cache" %>

<html>
<head>
	<title>cache view</title>
</head>

<body>
	<h1>Cache view</h1>
	
	<table>
		<thead>
			<tr>
				<th>Key</th>
				<th>Value</th>
			</tr>
		</thead>
		<tbody>
			<% 
			Cache<?, ?> cache = InfinispanTestSystem.infinispan().getCache();
			for( Object key: cache.keySet() ) {
			%>
			<tr>
				<td><%= key %></td>
				<td><%= cache.get(key) %></td>
			</tr>
			<% } %>
		</tbody>
	</table>
</body>

</html>
