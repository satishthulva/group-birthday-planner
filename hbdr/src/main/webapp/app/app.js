/**
 * Angular module
 */
(function(){
	'use strict';
	
	angular.module('app', [])
	.controller('appController', appControllerFunc);
	
	function appControllerFunc($scope, $http) {
		
		$scope.groups = {};
		
		$scope.fetchAllGroups = function() {
			$http.get("rest/v1/hbdr/list").then(
				function(data){
					$scope.groups = data.data;
				}
			);
		}
		
		$scope.fetchAllGroups();
	}
	
	appControllerFunc.$inject = ['$scope', '$http'];
	
})();