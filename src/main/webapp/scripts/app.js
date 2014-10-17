angular
.module('hcloud', ['ngResource'])
.constant('cluster', window.CLUSTER_TEST)
.controller('menu', function($scope, $resource, cluster) {
	$scope.cluster = cluster;
	
	$scope.info = $resource('info').get();
})
.controller('hcloud', function($scope, $resource, $timeout, cluster) {
	$scope.cluster = cluster;

	$scope.title = "HCloud Test Page";
	$scope.info = $resource('info').get();
	$scope.tests = cluster.tests;
});