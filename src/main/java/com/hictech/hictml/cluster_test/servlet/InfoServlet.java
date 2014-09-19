package com.hictech.hictml.cluster_test.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hictech.hictml.cluster_test.Tester;
import com.hictech.util.h.HTree;
import com.hictech.util.h.json.HJSON;

@WebServlet("/info")
public class InfoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doJob(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doJob(request, response);
	}


	public void doJob(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HTree tree = new HTree();
		HTree fs = tree.takeTree("fs");
		fs.put("single", Tester.get().fileSystem().rootPath());
		fs.put("cluster", Tester.get("single_host").fileSystem().rootPath());
		
		response.getWriter().write(HJSON.toString(tree));
	}

}
