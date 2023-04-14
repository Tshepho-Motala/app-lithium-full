'use strict';

angular.module('lithium')
.directive('dt', ['$timeout', '$log', '$parse', '$translate', function($timeout, $log, $parse, $translate) {
	return {
		templateUrl:'scripts/directives/dt/dt.html',
		scope: {
			table: "="
		},
		restrict: 'E',
		replace: true,
		link: function(scope) {
		}
	}
}])
.directive('dtReady', function($parse) {
	return {
		restrict : 'A',
		link : function($scope, elem, attrs) {
			console.log("dtReady");
			elem.ready(function() {
				$scope.$apply(function() {
					var func = $parse(attrs.dtReady);
					func($scope);
				})
			})
		}
	}
});