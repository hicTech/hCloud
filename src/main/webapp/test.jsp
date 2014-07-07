<%@page import="com.hictech.util.h.HTree"%>
<%@page import="com.hictech.hictml.cluster_test.Runner"%>
<%@page import="com.hictech.util.h.HCommon"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ page import="com.hictech.util.*,com.hictech.hictml.cluster_test.Tester" %><%!

	public static Map<?,?> doJob(HttpServletRequest request) {
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
    
		try{
			Tester tester = Tester.get(cluster_mode);
			result = tester.test(runner);
		}catch(Exception e){
            e.printStackTrace();
			error = new HashMap<String, Object>();
			error.put("class", e.getClass().getName());
			error.put("message", e.getMessage());
			error.put("stack_trace", FormatUtils.printStackTrace(e));
		}
		ms = System.currentTimeMillis()-ms;
		
		HTree ret = new HTree();
		ret.put("set",set_id);
		ret.put("object",obj_id);
		ret.put("millis",runner.getMSeconds());
        ret.put("mbytes", runner.getMBytes());
        ret.put("cycles", runner.getCycles());
		if(result!=null)
			ret.put("result",result);
		if(error!=null)
			ret.put("error",error);
		return ret;
	}

%><%= doJob(request) %>