'use strict'

angular.module('lithium').directive('changelog', function() {
	return {
		restrict: 'E',
		templateUrl: '/scripts/directives/changelog/changelog.html',
		replace: true,
		transclude: true,
		scope: {
			options: "=",
			model: "="
		},
		controller: ['$scope', '$changelogService','UserRest', '$log', '$filter', '$timeout', function($scope, changelogService, userRest, $log, $filter, $timeout) {
			$scope.formatDate = function(date) {
				var dateFormat = 'dd/MM/yyyy';
				date = $filter('date')(date, dateFormat, 'GMT');
				return date;
			}
			
			var options = $scope.options;
			
			$scope.list = [];
			$scope.page = 0;
			$scope.hasMore = false;
			
			$scope.loadChangeLogs = function(reset, restangularService, domainName, entityId) {
				if (reset) {
					$scope.page = 0;
					$scope.list = [],
					$scope.hasMore = false;
				}
				
				$timeout(function() {
					restangularService.changelogs(domainName, entityId, $scope.page).then(async function(cl) {
						
						const withAuthors = await changelogService.mapAuthorNameToChangeLogs(options.domainName, cl.list);
						
						if ($scope.list.length === 0) {
							$scope.list = withAuthors;
						} else {
							for (var i = 0; i < cl.list.length; i++) {
								$scope.list.push(withAuthors[i]);
							}
						}
						$scope.hasMore = cl.hasMore;
					});
				}, 200);
			};
						
			$scope.loadChangeLogs(true, options.restService, options.domainName, options.entityId);
			
			$scope.changelogsLoadMore = function() {
				$scope.page++;
				$scope.loadChangeLogs(false, options.restService, options.domainName, options.entityId);
			}
			
			$scope.$watch(function() { return $scope.model }, function(newValue, oldValue) {
				if (newValue != oldValue) {
					$scope.loadChangeLogs(true, options.restService, options.domainName, options.entityId);
				}
			});
			
			$scope.$watch(function() { return $scope.options.reload }, function(newValue, oldValue) {
				if (newValue != oldValue) {
					if (newValue != false) {
						$scope.loadChangeLogs(true, options.restService, options.domainName, options.entityId);
					}
				}
			});
		}]
	}
});
