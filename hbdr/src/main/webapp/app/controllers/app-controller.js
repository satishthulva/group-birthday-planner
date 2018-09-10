/**
 * Controller definition
 */
(function(){
	'use strict';
	
	angular.module('app')
	.controller('appController', appControllerFunc);
	
	function appControllerFunc($scope, $http, $cookies, $httpParamSerializerJQLike) {
		
		var onSignIn = function (googleUser) {
			var id_token = googleUser.getAuthResponse().id_token;
			var profile = googleUser.getBasicProfile();
			console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
			console.log('Name: ' + profile.getName());
			console.log('Image URL: ' + profile.getImageUrl());
			console.log('Email: ' + profile.getEmail()); // This is null if the 'email' scope is not present.
			console.log('id_token: ' + id_token);
			
			var params = {
				idToken : id_token,	
			};
			
			$http({
				  url: 'rest/v1/hbdr/login',
				  method: 'POST',
				  data: $httpParamSerializerJQLike(params), // Make sure to inject the service you choose to the controller
				  headers: {
				    'Content-Type': 'application/x-www-form-urlencoded' // Note the appropriate header
				  }
				}).then(function(response) { console.log('LoggedIn') });
		}
		
		window.onSignIn = onSignIn;
		
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
	
	appControllerFunc.$inject = ['$scope', '$http', '$cookies', '$httpParamSerializerJQLike'];
	
})();