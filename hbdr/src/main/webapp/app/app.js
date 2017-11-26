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
		
		$scope.peopleOfInterest = {};
		$scope.fetchPeopleWithUpcomingBirthdays = function () {
			$http.get("rest/v1/hbdr/upcomingBirthDays").then(
				function(data){
					$scope.peopleOfInterest = data.data;
				}
			);
		}
		
		var init = function () {
			$scope.fetchAllGroups();
			$scope.fetchPeopleWithUpcomingBirthdays();
		}
		
		init();
	}
	
	appControllerFunc.$inject = ['$scope', '$http'];
	
})();