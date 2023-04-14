'use strict';

angular.module('lithium')
	.directive('accountinggraph', function() {
		return {
			templateUrl:'scripts/directives/accounting/graph/graph.html',
			scope: {
				data: "=",
				granularity: "=",
				ranges: "=",
				selectedDomains: "=?"
			},
			restrict: 'E',
			replace: true,
			controller: ['$q', '$http', '$scope', '$rootScope', '$log', '$timeout', '$translate', '$interval',
			function($q, $http, $scope, $rootScope, $log, $timeout, $translate, $interval) {
				var completed = false;
				
				$rootScope.$on("$translateChangeEnd", function(event, args) {
					$scope.loadTranslations();
				});
				
				$scope.dataColumns = 
					[
					 {"id": "debit", "type": "line", "name": $scope.data.debit.titleKey},
					 {"id": "credit", "type": "line", "name": $scope.data.credit.titleKey},
					 {"id": "net", "type": "area", "name": $scope.data.net.titleKey}
					];
				$scope.dataX = {"id": "date"};
				$scope.callback = function (o) { $scope.o = o; }
				$scope.dataPoints = [];

				$scope.loadTranslations = function() {
					$translate($scope.data.debit.titleKey).then(function(value) { $scope.dataColumns[0].name = value });
					$translate($scope.data.credit.titleKey).then(function(value) { $scope.dataColumns[1].name = value });
					$translate($scope.data.net.titleKey).then(function(value) { $scope.dataColumns[2].name = value });
				};

				$scope.formattime = function (d) {
					if ($scope.granularity == 1) { return moment(d).format("YYYY"); } 
					if ($scope.granularity == 2) { return moment(d).format("YYYY MMM"); } 
					if ($scope.granularity == 3) { return moment(d).format("DD MMMM YYYY"); } 
					if ($scope.granularity == 4) { return "Week starting " + moment(d).startOf('isoWeeks').format("YYYY/MM/DD"); }
					return d;
				};
				
				$scope.formatcurrency = function (d) {
					return $scope.data.currency.code + " "+ d.toFixed(2).replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,");
				};
				
				$scope.clickEnabled = true;
				$scope.click = function (data) {
					if (!$scope.clickEnabled) return;
					$scope.clickEnabled = false;
					
					var clickdate = moment(data.x);
					
					if ($scope.granularity == 2) {
						$scope.granularity = 3;
						$scope.ranges[3].dateStart = moment(clickdate).startOf("months"); 
						$scope.ranges[3].dateEnd = moment(clickdate).startOf("months").add(1, "months");
						$scope.refresh();
					}

					if ($scope.granularity == 1) {
						$scope.granularity = 2;
						$scope.ranges[2].dateStart = moment(clickdate).startOf("years"); 
						$scope.ranges[2].dateEnd = moment(clickdate).startOf("years").add(1, "years");
						$scope.refresh();
					}

					if ($scope.granularity == 4) {
						$scope.granularity = 3;
						$scope.ranges[3].dateStart = moment(clickdate).startOf("isoWeeks"); 
						$scope.ranges[3].dateEnd = moment(clickdate).add(1, "weeks").startOf("isoWeeks");
						$scope.refresh();
					}
					$timeout(function() { $scope.clickEnabled = true }, 1000);
				}
				
				$scope.refresh = function() {
					var range = $scope.ranges[$scope.granularity];
					var promises = [];
					
					$scope.textGraphDuration = $scope.formattime(range.dateStart) + " - " + $scope.formattime(range.dateEnd);
					$scope.tempData = {};
					
					var rangeParams = "&dateStart=" + encodeURIComponent(moment(range.dateStart).format("YYYY-MM-DD hh:mm:ss")) + "&dateEnd=" + encodeURIComponent(moment(range.dateEnd).format("YYYY-MM-DD hh:mm:ss"));
					
					if (angular.isUndefined($scope.selectedDomains)) {
						$scope.selectedDomains = [];
						$scope.selectedDomains = $scope.data.domains;
					}
					
					for (var i in $scope.selectedDomains) {
						var domainName = $scope.selectedDomains[i];
						/* Obtain graph data */
						var url = "services/service-accounting/summary/domaintrantype/" + domainName + "/findLimited?";
						if ($scope.data.user != null) {
							url = "services/service-accounting/summary/trantype/" + domainName + "/findLimitedByOwnerGuid?ownerGuid=" + $scope.data.user.guid + "&";
						}
						promises.push( 
							$http.get(url + 
								"granularity=" + $scope.granularity + 
								"&accountCode=" + $scope.data.accountCode + 
								"&transactionType=" + $scope.data.debit.tran + 
								"&currency=" + $scope.data.currency.code + 
								rangeParams)
								.success((
									function (domainName) { 
										return function(response, status) {
											for (var i in response.data) {
												var item = response.data[i];
												var date = moment.utc(item.dateEnd).subtract(1, 'seconds').format("YYYY-MM-DD");
												if (!$scope.tempData[domainName]) $scope.tempData[domainName] = {};
												if (!$scope.tempData[domainName][date]) $scope.tempData[domainName][date] = {};
												$scope.tempData[domainName][date].debit = (item.debitCents - item.creditCents) / 100 * 
														(($scope.data.inverse)? -1: 1);
											}
										}
									}
								)(domainName)
							)
						);
						promises.push( 
							$http.get(url + 
								"granularity=" + $scope.granularity + 
								"&accountCode=" + $scope.data.accountCode + 
								"&transactionType=" + $scope.data.credit.tran + 
								"&currency=" + $scope.data.currency.code + 
								rangeParams)
								.success((
									function (domainName) { 
										return function(response, status) {
											for (var i in response.data) {
												var item = response.data[i];
												var date = moment.utc(item.dateEnd).subtract(1, 'seconds').format("YYYY-MM-DD");
												if (!$scope.tempData[domainName]) $scope.tempData[domainName] = {};
												if (!$scope.tempData[domainName][date]) $scope.tempData[domainName][date] = {};
												$scope.tempData[domainName][date].credit = (item.creditCents - item.debitCents) / 100 * 
														(($scope.data.inverse)? -1: 1);
											}
										}
									}
								)(domainName)
							)
						);
					}
					
					$q.all(promises).then(function() {
						var totals = {};
						
						for (var i in $scope.selectedDomains) {
							var domainName = $scope.selectedDomains[i];
							var tempData = $scope.tempData[domainName];
							for (var date in tempData) {
								if (!tempData[date].debit) tempData[date].debit = 0;
								if (!tempData[date].credit) tempData[date].credit = 0;
								if (!totals[date]) totals[date] = { debit: 0, credit: 0, net: 0 };
								tempData[date].net = tempData[date].debit - tempData[date].credit;
								totals[date].debit += tempData[date].debit;
								totals[date].credit += tempData[date].credit;
								totals[date].net += tempData[date].net;
							}
						}
						
						$scope.dataPoints.length = 0;
						
						for (var date in totals) {
							var dataPoint = { "date": date, 
									"debit": totals[date].debit,
									"credit": totals[date].credit,
									"net": totals[date].net
								};
							$scope.dataPoints.push(dataPoint);
						}
						if ($scope.o) {
							$scope.o.flush();
							$scope.o.unzoom();
						}
						$scope.tempData = {};
						completed = true;
						console.log("graph | updated completed flag");
					});
				};
				
				$scope.$watch("granularity", function() {
					$scope.refresh();
				});
				$scope.$watch("selectedDomains", function() {
					$scope.refresh();
				});
				
				$scope.loadTranslations();
				
				if ($scope.data.updateSeconds !== undefined && $scope.data.updateSeconds !== null) {
					var intervalPromise = null;
					intervalPromise = $interval(function() {
						if (completed) {
							console.log("graph | completed, refreshing");
							completed = false;
							$scope.refresh();
						} else {
							console.log("graph | not completed, cannot refresh yet");
						}
					}, $scope.data.updateSeconds * 1000);
					$scope.$on('$destroy',function(){
						if(intervalPromise)
							$interval.cancel(intervalPromise);
					});
				}
			}]
		}
	});