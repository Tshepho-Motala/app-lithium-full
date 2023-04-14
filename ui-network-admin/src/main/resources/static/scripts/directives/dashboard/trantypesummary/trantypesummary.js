'use strict';

angular.module('lithium')
	.directive('trantypesummary', function() {
		return {
			templateUrl: 'scripts/directives/dashboard/trantypesummary/trantypesummary.html',
			scope: {
				data: "=",
				granularity: "@",
				selectedDomains: "=?"
			},
			restrict: 'E',
			replace: true,
			controller: ['$q', '$http', '$scope', '$log', '$interval', function ($q, $http, $scope, $log, $interval) {
				var completed = false;

				$scope.refresh = function() {
					var me = this;

					if (angular.isUndefined($scope.selectedDomains)) {
						$scope.selectedDomains = [];
						$scope.selectedDomains = $scope.data.domains;
					}

					$scope.data.link = $scope.data.link;
					$scope.data.user = $scope.data.user;
					$scope.data.total = 0;
					$scope.data.current = 0;
					$scope.data.last1 = 0;
					$scope.data.last2 = 0;
					$scope.data.counttotal = 0;
					$scope.data.countcurrent = 0;
					$scope.data.countlast1 = 0;
					$scope.data.countlast2 = 0;

					me.domains = [];
					var promises = [];

					for (var i in $scope.selectedDomains) {

						var domainName = $scope.selectedDomains[i];

						var url = "services/service-accounting/summary/domaintrantype/" + $scope.selectedDomains[i];
						var findService = "/find?1=1";
						var findLastService = "/findLast?last=3";

						if ($scope.data.labelName !== undefined && $scope.data.labelName !== null &&
							$scope.data.labelValue !== undefined && $scope.data.labelValue !== null) {
								url = "services/service-accounting/summary/domainlabelvalue/"+$scope.selectedDomains[i];
								if ($scope.data.beta) {
									url = "services/service-accounting-domain-summary/backoffice/summary/domain-label-value/" + $scope.selectedDomains[i];
									findLastService = "/find-last?last=3";
								} else if ($scope.data.betav2) {
									url = "services/service-accounting-domain-summary-v2/backoffice/summary/domain-label-value/" + $scope.selectedDomains[i] + "/";
									findLastService = "/find-last?last=3";
									if($scope.data.isTestUser != null){
										findLastService = findLastService + "&testUsers="+$scope.data.isTestUser;
										findService = findService + "&testUsers="+$scope.data.isTestUser;
									}
								}
								var labelAndValue = "&labelName="+$scope.data.labelName+"&labelValue="+$scope.data.labelValue;
								findService += labelAndValue;
								findLastService += labelAndValue;
						} else if ($scope.data.user != null) {
							url = "services/service-accounting/summary/trantype/"+domainName+"/";
							findService = "findByOwnerGuid?&ownerGuid="+$scope.data.user.guid;
							findLastService = "findLastByOwnerGuid?last=3&&ownerGuid="+$scope.data.user.guid;
						} else {
							if ($scope.data.beta) {
								url = "services/service-accounting-domain-summary/backoffice/summary/domain-tran-type/" + $scope.selectedDomains[i];
								findLastService = "/find-last?last=3";
							} else if ($scope.data.betav2) {
								url = "services/service-accounting-domain-summary-v2/backoffice/summary/domain-tran-type/" + $scope.selectedDomains[i];
								findLastService = "/find-last?last=3";
								if($scope.data.isTestUser != null){
									findLastService = findLastService + "&testUsers="+$scope.data.isTestUser
								}
							}
						}

						promises.push($http.get(url + findService +
							"&granularity=5"+
							"&accountCode="+$scope.data.accountCode+
							"&transactionType="+$scope.data.transactionType+
							"&currency="+$scope.data.currency.code)
							.success((function(domainName) { return function(response, status) {
								if (response.data[0]) {
									$scope.data.total +=
										(response.data[0].debitCents - response.data[0].creditCents) / 100 *
										(($scope.data.inverse)? -1: 1);
									$scope.data.counttotal += response.data[0].tranCount;
								}
							}})(domainName))
						);

						promises.push($http.get(url + findLastService +
							"&granularity="+$scope.granularity+
							"&accountCode="+$scope.data.accountCode+
							"&transactionType="+$scope.data.transactionType+
							"&currency="+$scope.data.currency.code)
							.success((function(domainName) { return function(response, status) {
								if (angular.isUndefined($scope.data.current)) $scope.data.current = 0;
								if (angular.isUndefined($scope.data.last1)) $scope.data.last1 = 0;
								if (angular.isUndefined($scope.data.last2)) $scope.data.last2 = 0;
								if (angular.isUndefined($scope.data.countcurrent)) $scope.data.countcurrent = 0;
								if (angular.isUndefined($scope.data.countlast1)) $scope.data.countlast1 = 0;
								if (angular.isUndefined($scope.data.countlast2)) $scope.data.countlast2 = 0;

								if (response.data) {
									$scope.data.current += (response.data[0].debitCents - response.data[0].creditCents) / 100 * (($scope.data.inverse)? -1: 1);
									$scope.data.last1 += (response.data[1].debitCents - response.data[1].creditCents) / 100 * (($scope.data.inverse)? -1: 1);
									$scope.data.last2 += (response.data[2].debitCents - response.data[2].creditCents) / 100 * (($scope.data.inverse)? -1: 1);
									$scope.data.countcurrent += response.data[0].tranCount;
									$scope.data.countlast1 += response.data[1].tranCount;
									$scope.data.countlast2 += response.data[2].tranCount;
								}
							}})(domainName))
						);
					}
				}

				$scope.$watch("granularity", function() {
					$scope.refresh();
				});
				$scope.$watch("selectedDomains", function(newValue, oldValue) {
					if (newValue != oldValue) {
						$scope.refresh();
					}
				});

				if ($scope.data.updateSeconds !== undefined && $scope.data.updateSeconds !== null) {
					var intervalPromise = null;
					intervalPromise = $interval(function() {
						if (completed) {
							console.debug("trantypesummary | completed, refreshing");
							completed = false;
							$scope.refresh();
						} else {
							console.debug("trantypesummary | not completed, cannot refresh yet");
						}
					}, $scope.data.updateSeconds * 1000);
					$scope.$on("$destroy",function(){
						if (intervalPromise)
							$interval.cancel(intervalPromise);
					});
				}
			}]
		}
	});