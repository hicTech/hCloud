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

		tests_order: instance.tests_order,
		add: function(options) {
			return instance.add.call(instance, options);
		},

		delay: function(set_id, obj_id) {
			return instance.delay.apply(instance, [set_id, obj_id]);
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
		cluster_mode: 'infinispan',
		run_mode: 'sleep',
		time_sec: -1,
		delay_min: 0,
		size_mb: -1
	};
	
	$scope.newTest = function() {
		$scope.options.set_id = random(5);
		
		cluster.add($scope.options);
		
		$rootScope.$broadcast('update');
	};
	
	$scope.viewTestPage = function() {
		$('#cache').hide();
		$('#view').show();
	};
	
	$scope.viewCachePage = function() {
		$('#cache').show();
		$('#view').hide();
	};
})

.controller('view', function($scope, $rootScope, $interval, cluster) {
	$scope.refresh = function() {
		$.getJSON('view', function(data) {
			$('.jsonview.all').JSONView(data, {collapsed: true});
		});
	};

	
	$scope.query = function(key) {
		$.getJSON('view', {key: key}, function(data) {
			$('.jsonview.entry').JSONView(data, {collapsed: true});
		});
	};
})

.controller('hcloud', function($scope, $rootScope, $resource, $timeout, cluster) {
	$scope.title = "HCloud Test Page";
	$scope.columns = 2;
	
	$scope.info = cluster.info;
	$scope.tests = cluster.tests;
	$scope.tests_order = cluster.tests_order;
	$scope.results = {};
	
	$scope.doChangeColumns = function() {
		$('.column').each(function(i, e) {
		   var classes = $(e).attr('class').split(' ');
		   _.each(classes, function(clazz) {
		       if(clazz.indexOf('col-md-') === 0 ) {
		           $(e).removeClass(clazz); 
		       }
		   });
		   $(e).addClass('col-md-'+$scope.columns);
		}); 
	};
	
	$scope.doTest = function(event) {
		$scope.doTestObj(event.target);
	};
	
	$scope.doTestObj = function(el) {
		var $el = $(el);
		var $obj_id = $el.closest('[data-obj_id]');
		var $set_id = $obj_id.closest('[data-set_id]');
		var $result = $obj_id.find('[data-result]').empty();
		
		var obj_id = $obj_id.data('obj_id');
		var set_id = $set_id.data('set_id');
		
		$scope.results[set_id+obj_id] = $scope.results[set_id+obj_id] || {};
		if( cluster.delay(set_id,obj_id) > 0 ) {
			$scope.results[set_id+obj_id].status = 'SLEEPING';
		}
			
		$timeout(function() {
			$scope.results[set_id+obj_id].status = 'WAITING';
			$scope.$apply();
				
			cluster.run(set_id, obj_id)
			.done(function(data) {
				$result.JSONView(data, {collapsed: true});
				
				$scope.results[set_id+obj_id].status = 'SUCCESS';
				$scope.results[set_id+obj_id].result = data.result;
				$scope.$apply();
			}).fail(function(jqXHR, textStatus, errorThrown) {
				var data = jqXHR.responseJSON || {};
				$result.JSONView(data, {collapsed: true});

				var status = jqXHR.status !== 0? textStatus.toUpperCase() : 'UNREACHABLE';
				$scope.results[set_id+obj_id].status = status;
				$scope.results[set_id+obj_id].result = data;
				$scope.$apply();
			});
		}, cluster.delay(set_id,obj_id)*1000)
		
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
	
	$scope.$on('update', function() {
		$scope.tests = cluster.tests;
	});
});