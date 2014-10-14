angular
.module('hcloud', ['ngResource'])
.factory('cluster', function($rootScope) {
	cluster = window.CLUSTER_TEST;
	
	return {
		tests: function() {
			return cluster.tests;
		},
		addTest: function(event) {
			cluster.addTestSet();
			
			$rootScope.$emit('addtest');
		},
		doTestObj: function(event) {
			cluster.doTestObj(event);
		},
		doTestSet: function(event) {
			cluster.doTestSet(event);
		}
	}
})
.controller('menu', function($scope, $resource, cluster) {
	$scope.cluster = cluster;
	
	$scope.info = $resource('info').get();
})
.controller('hcloud', function($scope, $resource, cluster) {
	$scope.cluster = cluster;

	$scope.title = "HCloud Test Page";
	$scope.info = $resource('info').get();
	$scope.tests = cluster.tests();
	$scope.$on('addtest', function() {
		$scope.tests = cluster.tests();
    });
});