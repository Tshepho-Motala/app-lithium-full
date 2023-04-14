'use strict';

/**
 * @ngdoc directive
 * @name izzyposWebApp.directive:adminPosHeader
 * @description
 * # adminPosHeader
 */
angular.module('lithium')
	.directive('lithiumbar', ['$translate', '$log',
	function($translate, $log) {
		return {
			templateUrl:'scripts/directives/lithiumbar/lithiumbar.html',
			replace: true,
			controller: function($scope) {
				$scope.closeAlert = function() {
					$scope.litbar.show = false;
				};
			}
		}
	}]);