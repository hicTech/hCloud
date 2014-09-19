<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.hictech.hictml.cluster_test.Tester" %>
<html>
<head>

	<style>
		
		.test_set_container{
			width:500px;
			float:left;
			margin:20px
		}
		
		.test_element{
			float:left;
			margin:0px 10px 0px 10px;
		}
		
		.test_element_div{
			float:left;
			margin:0px 10px 0px 10px;
		}
		
	</style>
	
	<script type="text/javascript" src="js/_.js"></script>
	<script type="text/javascript" src="js/_string.js"></script>
	<script type="text/javascript" src="js/_plus.js"></script>
	<script type="text/javascript" src="js/jquery-v1.7.1.js"></script>
	
	<script>
	
		var CLUSTER_TEST = {
			test_sets: {},
			
			jsValue: function(id_or_dom, bool){
				var $elem = _.isString(id_or_dom) ? $("#"+id_or_dom) : $(id_or_dom); 
				return (!!bool) ? _.tryParse($elem.val(), null, false) : _.tryParse($elem.val());
			},
			
			clusterMode: function(){
				return $("#cluster_mode").val();
			},
			
			addTestSet: function(){
				var objects_number = this.jsValue("objects_number");
				var starting_object = this.jsValue("starting_object");
				var time_sec = this.jsValue("time_sec");
				var time_millis = (time_sec * 1000);
				var delay_min = this.jsValue("delay_min");
				var size_mb = this.jsValue("size_mb");
				
				var run_mode = $("#run_mode").val();
				
				
				var $elems = $("<div data-test_set_role='elements'></div>");
				for(var i=0; i<objects_number; i++){
					var num = (starting_object + i);
					var html = "<div data-test_set_role='element' data-test_object='"+(num)+"' data-test_millis='"+time_millis+"' data-test_mbytes='"+size_mb+"' data-test_delay='"+delay_min+"' data-test_run_mode='"+run_mode+"' class='test_element'>"+
									"<div data-test_set_role='element_title' class='test_element_div' onclick='CLUSTER_TEST.showObject(\""+num+"\")'>Oggetto N. "+num+": </div>"+
									"<div data-test_set_role='element_state' class='test_element_div'> - </div>"+
								+"</div>";
					var $elem = $(html);
					$elems.append($elem);
				}
				
				
				var set_id = _.random(1,999999, true);
				var html = "<div data-test_set_role='container' data-test_set_id='"+set_id+"' class='test_set_container'>"+
								"<div data-test_set_role='header'>Set # "+set_id+", task time: "+time_sec+" seconds --> "+
									"<button onclick='CLUSTER_TEST.testSet(this)'>TEST SET</button>"+
								"</div>"+
							"</div>";
				var $set = $(html);
				$set.append($elems);
				$("#test_sets").append($set);
				
				this.test_sets[(""+set_id)] = $set; 
			},
			
			testAllSets: function(){
				var that = this;
				_.each(this.test_sets, function(test_set){
						that.testSet(test_set);
				}, this);
			},
			
			testSet: function(id_or_dom){
				var $set = (_.isString(id_or_dom) || _.isNumber(id_or_dom)) ? $("[data-test_set_role='container'][data-test_set_id='"+set_id+"']") : 
						( $(id_or_dom).is("[data-test_set_role='container']") ? $(id_or_dom) : $(id_or_dom).closest("[data-test_set_role='container']") );
				var set_id = $set.attr("data-test_set_id");
				var $elems = $set.find("[data-test_set_role='element']");
				var that = this;
				$elems.each(function(){
					that.testSetElement(this, set_id);
				});
			},
			
			testSetElement: function(elem, set_id){
				var $elem = $(elem);
				var obj_id = $elem.attr("data-test_object");
				var millis = _.tryParse($elem.attr("data-test_millis"),-1,true);
				var mbytes = _.tryParse($elem.attr("data-test_mbytes"),-1,true);
				var delay = _.tryParse($elem.attr("data-test_delay"),-1,true);
				delay = delay * 60000;
				var run_mode = $elem.attr("data-test_run_mode");
				var cluster_mode = this.clusterMode();
				
				if(_.isNotEmptyString(obj_id) && (millis>0)){
					var $state = $elem.find("[data-test_set_role='element_state']");
					var bindState = function(data, err){
						if(!!err){
							$state.html("ERR");
			            	$state.unbind("click");
			            	$state.click(function(){
			            		alert("AJAX ERROR: "+_.toStr(data));
			            	});
						}
						else{
							$state.html("OK");
			            	$state.unbind("click");
			            	$state.click(function(){
			            		alert("SUCCESS: "+_.toStr(data));
			            	});
						}
					};
					
					var url = "test.jsp";
					var ajax_opts = {
			            data: {
			            	set: set_id,
			            	object: obj_id,
			            	millis: millis,
			            	mbytes: mbytes,
			            	run_mode: run_mode,
			            	cluster_mode: cluster_mode
			            },
			            dataType: "text",
			            success: function(data, status, xhr){
			            	//alert("Risposta: "+data);
			            	data = _.parse(data);
			            	var errors = (_.is(data.error) || _.is(data.errors));
			            	bindState(data, errors);
			            },
			            error: function(xhr, status){
			            	//alert("AJAX ERROR: "+status);
			            	bindState("AJAX ERROR: "+status, true);	
			            }
			        };
					

					if( delay > 0 ) {
    			        $state.html("SLEEPING");
    					setTimeout(function(){
    				        $state.html("WAITING");
    				        
    				        $.ajax(url,ajax_opts);
    					}, delay);
					}
					else {
						$state.html("WAITING");
						$.ajax(url,ajax_opts);
					}
				}
				else
					alert("Unable to test set element: [Object: "+obj_id+", millis: "+millis+"]");
			},
			
			showObject: function(id){
				window.open("test_objects/"+id);
			}
			
		};
	</script>
	
	
</head>

<body>
	
	<div id="test_body">
	
		<div id="test_header">
			<div id="test_info">
				<span>HicTML Cluster Test [mode:</span>
                <select id="cluster_mode">
                  <option value="single_host">Single Host</option>
                  <option value="cluster_bigmemory">Cluster via BigMemory</option>
                  <option value="cluster_hazelcast">Cluster via Hazelcast</option>
                  <option value="cluster_infinispan">Cluster via Infinispan</option>
                </select>  
                ]
                
				<button onclick='CLUSTER_TEST.testAllSets();'>TEST ALL SETS!</button>
				<%
					String single_host_root = Tester.get().fileSystem().rootPath();
					String cluster_root = Tester.get("single_host").fileSystem().rootPath();
				%>
                <span>
				[FileSystems Root - Single host: <b><%= single_host_root %></b>, Cluster: <b><%= cluster_root %></b>]
                </span>
			</div>
			<div id="test_control">Aggiungi Client Set - 
				Oggetti: <input id="objects_number" type="text" value="2" size="1"/>, 
				ID iniziale: <input id="starting_object" type="text" value="1" size="1" />,
                Memoria  (mb): <input id="size_mb" type="text" value="-1" size="1"/>,
                Tempo (sec): <input id="time_sec" type="text" value="5" size="1"/>,
                Ritardo (min): <input id="delay_min" type="text" value="-1" size="1"/>
                Computazione: <select id="run_mode">
                  <option value="sleep">sleep</option>
                  <option value="sqrt">sqrt</option>
                </select> -
				<button onclick="CLUSTER_TEST.addTestSet();">ADD TEST SET</button> 
			</div>
			<!-- <div id="test_all_sets"><button onclick='CLUSTER_TEST.testAllSets();'>TEST ALL SETS!</button></div>  -->
		</div>
		
		<div id="test_sets"></div>
	
	</div>

</body>

</html>
