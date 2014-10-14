angular.module('hcloud', ['ngResource']).controller('hcloud', function($scope, $resource) {
	$scope.title = "HCloud Test Page";
	
	
	$scope.info = $resource('info').get();
});