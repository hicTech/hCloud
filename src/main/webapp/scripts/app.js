angular
.module('hcloud', ['ngResource'])
.factory('random', function() {
	return function(n) {
	    var text = "";
	    var chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	    for( var i=0; i < n; i++ )
	        text += chars.charAt(Math.floor(Math.random() * chars.length));

	    return text;
	}
})
.factory('cluster', function($resource) {
	var instance = window.CLUSTER_TEST;
	
	return {
		info: $resource('info').get(),
		tests: instance.tests_tree,
		add: function(options) {
			return instance.add.call(instance, options);
		},
		run: function(set_id, obj_id) {
			return instance.run.apply(instance, [set_id, obj_id]);
		}
	}
})
.controller('menu', function($scope, $rootScope, $resource, random, cluster) {
	$scope.info = cluster.info;
	
	$scope.options = {
		starting_object: 0,
		objects_number: 2,
		cluster_mode: 'single_host',
		run_mode: 'sleep',
		time_sec: -1,
		delay_min: 0,
		size_mb: -1
	};
	
	$scope.newTest = function() {
		$scope.options.set_id = random(5);
		
		cluster.add($scope.options);
		
		$rootScope.$broadcast('mosconi');
	};
})
.controller('hcloud', function($scope, $rootScope, $resource, $timeout, cluster) {
	$scope.title = "HCloud Test Page";
	$scope.info = cluster.info;
	$scope.tests = cluster.tests;
	
	$scope.doTest = function(event) {
		$scope.doTestObj(event.target);
	};
	
	$scope.doTestObj = function(el) {
		var $el = $(el);
		var $obj_id = $el.closest('[data-obj_id]');
		var $set_id = $obj_id.closest('[data-set_id]');
		var $state = $obj_id.find('[data-state]').html('SLEEPING');
		var $result = $obj_id.find('[data-result]').empty();
		
		var obj_id = $obj_id.data('obj_id');
		var set_id = $set_id.data('set_id');
		var promises = cluster.run(set_id, obj_id);

		var waiting = promises[0];
		waiting.done(function() {
			$state.html('WAITING');
		});
	
		var complete = promises[1];
		complete.done(function(data, status) {
			$result.JSONView(data, {collapsed: true});
			
			$state.html(status.toUpperCase());
		});
	};
	
	$scope.doTestSet = function(event) {
		var $el = $(event.target);
		var $set_id = $el.closest('[data-set_id]');
		
		var set_id = $set_id.data('set_id');
		$set_id.find('[data-obj_id]').each(function() {
			$scope.doTestObj(this); 
		});
		
	};
	
	$scope.doTestAll = function() {
		$('[data-obj_id]').each(function() {
			$scope.doTestObj(this); 
		});
	};
	
	$scope.doResult = function(event) {
		$(event.target).closest('[data-obj_id]').find('[data-result]').toggle();
	};
	
	$scope.$on('mosconi', function() {
		$scope.tests = cluster.tests;
	});
});