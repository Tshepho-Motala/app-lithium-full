'use strict';

angular.module('lithium')
	.controller('roleView', ["$log", "$scope", "$state", "$stateParams", "$http", function($log, $scope, $state, $stateParams, $http) {
		var controller = this;
		$scope.id = $stateParams.domainId;
		
		controller.back = function() {
			$state.go("dashboard.roles");
		}
		
		$http.get('services/service-domain/domain/' + $stateParams.domainId + '/view').then(function(response) {
			$scope.data = response.data;
			$scope.authProviders = response.data.authProviders;
			$scope.userProviders = response.data.userProviders;
			
			$log.info(response);
		})
	}
]);