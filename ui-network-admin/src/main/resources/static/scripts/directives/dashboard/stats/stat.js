'use strict';

angular.module('lithium')
	.directive('stat', function() {
		return {
			templateUrl:'scripts/directives/dashboard/stats/stat.html',
			scope: {
				data: "=",
				granularity: "@"
			},
			restrict: 'E',
			replace: true,
			controller: ['$scope', '$log', function($scope, $log) {
				if (!$scope.data.color) {
					$scope.data.color = "gray";
				}
				
				$scope.$watch("granularity", function() {
					$scope.granularityKey = "";
					switch (parseInt($scope.granularity)) {
						case 1: { $scope.granularityKey = "GLOBAL.GRANULARITY.YEAR"; break }
						case 2: { $scope.granularityKey = "GLOBAL.GRANULARITY.MONTH"; break }
						case 3: { $scope.granularityKey = "GLOBAL.GRANULARITY.DAY"; break }
						case 4: { $scope.granularityKey = "GLOBAL.GRANULARITY.WEEK"; break }
					}
				});
			}]
		}
	});