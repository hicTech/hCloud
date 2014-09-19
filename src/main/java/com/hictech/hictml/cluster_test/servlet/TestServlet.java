package com.hictech.hictml.cluster_test.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hictech.hictml.cluster_test.Runner;
import com.hictech.hictml.cluster_test.Tester;
import com.hictech.util.FormatUtils;
import com.hictech.util.h.HCommon;
import com.hictech.util.h.HTree;
import com.hictech.util.h.json.HJSON;

@WebServlet("/test")
public class TestServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			doJob(request, response);
		}
		catch (Exception e) {
			throw new ServletException(e);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			doJob(request, response);
		}
		catch (Exception e) {
			throw new ServletException(e);
		}
	}

	public static void doJob(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String set_id = request.getParameter("set");
		String obj_id = request.getParameter("object");
		long millis = Long.parseLong(request.getParameter("millis"));
		int mbytes = HCommon.toInt(request.getParameter("mbytes"));
		String cluster_mode = request.getParameter("cluster_mode");
		String run_mode = request.getParameter("run_mode");

		/* Create the runner object */
		Runner runner = new Runner(set_id, obj_id, run_mode, millis, mbytes);

		long ms = System.currentTimeMillis();
		Map<String, Object> result = null;
		Map<String, Object> error = null;

		try {
			Tester tester = Tester.get(cluster_mode);
			result = tester.test(runner);
		} catch (Exception e) {
			e.printStackTrace();
			error = new HashMap<String, Object>();
			error.put("class", e.getClass().getName());
			error.put("message", e.getMessage());
			error.put("stack_trace", FormatUtils.printStackTrace(e));
		}
		ms = System.currentTimeMillis() - ms;

		HTree ret = new HTree();
		ret.put("set", set_id);
		ret.put("object", obj_id);
		ret.put("millis", runner.getMSeconds());
		ret.put("mbytes", runner.getMBytes());
		ret.put("cycles", runner.getCycles());
		if (result != null)
			ret.put("result", result);
		if (error != null)
			ret.put("error", error);
		
		response.getWriter().write(HJSON.toString(ret));

	}

}
