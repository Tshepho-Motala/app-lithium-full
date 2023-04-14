'use strict';

angular.module('lithium')
	.directive('summary', function() {
		return {
			templateUrl:'scripts/directives/dashboard/summary/summary.html',
			scope: {
				summaryData: "=",
				granularity: "=",
				domain: "=",
				currency: "@",
				currencySymbol: "@",
			},
			restrict: 'E',
			replace: true,
			controller: ['$scope', '$state', function($scope, $state) {
				if (!$scope.summaryData.color) {
					$scope.summaryData.color = "gray";
				}

				console.debug("summary", $scope.granularity);

				$scope.link = function(offset) {
					if (($scope.summaryData.link !== undefined) && ($scope.domain.length === 1)) {

						var userGuid = ($scope.summaryData.user !== undefined && $scope.summaryData.user !== null)
							? $scope.summaryData.user.guid : null;
						console.debug('userGuid', userGuid);

						$state.go($scope.summaryData.link, {
							domainName: $scope.domain[0],
							granularity: $scope.granularity,
							tranType: $scope.summaryData.tran,
							offset: offset,
							userGuid: userGuid,
							status: $scope.summaryData.status
						});
					}
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