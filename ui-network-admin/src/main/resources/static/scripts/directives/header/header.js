'use strict';

/**
 * @ngdoc directive
 * @name izzyposWebApp.directive:adminPosHeader
 * @description
 * # adminPosHeader
 */
angular.module('lithium')
	.directive('mainHeader',function($interval){
	return {
		templateUrl:'scripts/directives/header/header.html',
		restrict: 'E',
		replace: true,
		controller: ['$scope', '$rootScope', 'jwtHelper', function($scope, $rootScope, jwtHelper) {
			$scope.setCurrentDateUtc = function() {
				$scope.currentDateUtc = moment.utc(new Date()).toString();
			}

			$scope.token = jwtHelper.getTokenExpirationDate($rootScope.token);
			if ($rootScope.refreshToken) $scope.refreshToken = jwtHelper.getTokenExpirationDate($rootScope.refreshToken);
			if (!$rootScope.refreshToken) $scope.refreshToken = 'undefined';

			$interval(function() { $scope.setCurrentDateUtc(); }, 1000);
			
			$scope.toggleSidebar = function() {
				var sidebarOpen = false;
				if (angular.isDefined($rootScope.sidebarOpen)) {
					sidebarOpen = $rootScope.sidebarOpen;
				}
				if (sidebarOpen) {
					sidebarOpen = false;
				} else {
					sidebarOpen = true;
				}
				$rootScope.sidebarOpen = sidebarOpen;
			}
		}]
	}
});