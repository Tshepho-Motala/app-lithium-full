'use strict';

angular.module('lithium')
	.directive('dt2', ['$timeout', '$log', '$parse', '$translate', function($timeout, $log, $parse, $translate) {
		return {
			templateUrl:'scripts/directives/dt2/dt2.html',
			scope: {
				table: "="
			},
			restrict: 'E',
			replace: true,
			link: function(scope) {
			}
		}
	}]);