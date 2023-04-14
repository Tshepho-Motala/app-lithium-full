'use strict';

angular.module('lithium')
	.directive('balancesummary', function() {
		return {
			templateUrl:'scripts/directives/dashboard/balancesummary/balancesummary.html',
			scope: {
				data: "=",
				granularity: "@",
				selectedDomains: "=?"
			},
			restrict: 'E',
			replace: true,
			controller: ['$q', '$http', '$scope', '$log', '$interval', function($q, $http, $scope, $log, $interval) {
				var completed = false;
				
				$scope.refresh = function() {
					var me = this;
					
					if (angular.isUndefined($scope.selectedDomains)) {
						$scope.selectedDomains = [];
						$scope.selectedDomains = $scope.data.domains;
					}
					
					$scope.data.current = 0;
					$scope.data.last1 = 0;
					$scope.data.last2 = 0;
					$scope.data.currentopening = 0;
					$scope.data.last1opening = 0;
					$scope.data.last2opening = 0;
					$scope.data.currentclosing = 0;
					$scope.data.last1closing = 0;
					$scope.data.last2closing = 0;
					
					$scope.data.countcurrent = 0;
					$scope.data.countlast1 = 0;
					$scope.data.countlast2 = 0;
					
					me.domains = [];
					var promises = [];
										
					for (var i in $scope.selectedDomains) {
						
						var domainName = $scope.selectedDomains[i];
						var domain = { debit: {}};
						
						me.domains[domainName] = domain;
						
						var url = "services/service-accounting-provider-internal/summary/domainaccountcode/" + $scope.selectedDomains[i];
						var findService = "/find?1=1";
						var findLastService = "/findLast?last=3";
						
						if ($scope.data.user != null) {
							url = "services/service-accounting/summary/account/"+domainName+"/";
							findService = "findByOwnerGuid?&ownerGuid="+$scope.data.user.guid;
							findLastService = "findLastByOwnerGuid?last=3&&ownerGuid="+$scope.data.user.guid;
						} else {
							if ($scope.data.beta) {
								url = "services/service-accounting-domain-summary/backoffice/summary/domain-account-code/" + $scope.selectedDomains[i];
								findLastService = "/find-last?last=3";
							} else if ($scope.data.betav2) {
								// assume we are moving to V2 with testUsers default value set to false and nullable
								url = "services/service-accounting-domain-summary-v2/backoffice/summary/domain-account-code/" + $scope.selectedDomains[i];
								findLastService = "/find-last?last=3";
								if($scope.data.isTestUser != null){
									 findLastService = findLastService + "&testUsers="+$scope.data.isTestUser;
								}
							}
						}


//							/* Obtain sum totals */
//							promises.push($http.get(url + findService +
//									"&granularity=5"+
//									"&accountCode="+$scope.data.accountCode+
//									"&currency=" + $scope.data.currency.code)
//								.success((function(domainName) { return function(response, status) {
//									if ($scope.data.ascontra === true) {
//										var tmp = response.data[0].debitCents;
//										response.data[0].debitCents = response.data[0].creditCents;
//										response.data[0].creditCents = tmp;
//									}
//									var domain = me.domains[domainName];
//									
//									if (angular.isUndefined(domain.debit.total)) domain.debit.total = 0; 
//									if (angular.isUndefined(domain.debit.counttotal)) domain.debit.counttotal = 0;
//									if (angular.isUndefined(domain.net.counttotal)) domain.net.counttotal = 0;
//									
//									if (response.data[0]) {
//										if ($scope.data.nocalc) {
//											domain.debit.total += response.data[0].debitCents / 100 * 
//													(($scope.data.inverse)? -1: 1);
//											domain.net.counttotal += response.data[0].tranCount; //This is really supposed to be net, it is not an error
//										} else {
//											domain.debit.total += 
//												(response.data[0].debitCents - response.data[0].creditCents) / 100 * 
//													(($scope.data.inverse)? -1: 1);
//											domain.debit.counttotal += response.data[0].tranCount;
//										}
//									}
//								}})(domainName))
//							);
						



							/* Obtain last 3 */
							promises.push($http.get(url + findLastService + 
									"&granularity="+$scope.granularity+
									"&accountCode="+$scope.data.accountCode+
									"&currency=" + $scope.data.currency.code)

								.success((function(domainName) { return function(response, status) {
									if ($scope.data.ascontra === true) {
										var tmp0 = response.data[0].debitCents;
										var tmp1 = response.data[1].debitCents;
										var tmp2 = response.data[2].debitCents;
										response.data[0].debitCents = response.data[0].creditCents;
										response.data[1].debitCents = response.data[1].creditCents;
										response.data[2].debitCents = response.data[2].creditCents;
										response.data[0].creditCents = tmp0;
										response.data[1].creditCents = tmp1;
										response.data[2].creditCents = tmp2;
									}
									var domain = me.domains[domainName];
									
									if (angular.isUndefined(domain.debit.current)) domain.debit.current = 0;
									if (angular.isUndefined(domain.debit.last1)) domain.debit.last1 = 0;
									if (angular.isUndefined(domain.debit.last2)) domain.debit.last2 = 0;
									if (angular.isUndefined(domain.debit.currentopening)) domain.debit.currentopening = 0;
									if (angular.isUndefined(domain.debit.last1opening)) domain.debit.last1opening = 0;
									if (angular.isUndefined(domain.debit.last2opening)) domain.debit.last2opening = 0;
									if (angular.isUndefined(domain.debit.currentclosing)) domain.debit.currentclosing = 0;
									if (angular.isUndefined(domain.debit.last1closing)) domain.debit.last1closing = 0;
									if (angular.isUndefined(domain.debit.last2closing)) domain.debit.last2closing = 0;
									if (angular.isUndefined(domain.debit.countcurrent)) domain.debit.countcurrent = 0;
									if (angular.isUndefined(domain.debit.countlast1)) domain.debit.countlast1 = 0;
									if (angular.isUndefined(domain.debit.countlast2)) domain.debit.countlast2 = 0;

									if (response.data) {
										domain.debit.current += (response.data[0].debitCents - response.data[0].creditCents) / 100 * (($scope.data.inverse)? -1: 1);
										domain.debit.last1 += (response.data[1].debitCents - response.data[1].creditCents) / 100 * (($scope.data.inverse)? -1: 1);
										domain.debit.last2 += (response.data[2].debitCents - response.data[2].creditCents) / 100 * (($scope.data.inverse)? -1: 1);
										domain.debit.countcurrent += response.data[0].tranCount;
										domain.debit.countlast1 += response.data[1].tranCount;
										domain.debit.countlast2 += response.data[2].tranCount;
										domain.debit.currentopening += response.data[0].openingBalanceCents / 100 * (($scope.data.inverse)? -1: 1);;
										domain.debit.last1opening += response.data[1].openingBalanceCents / 100 * (($scope.data.inverse)? -1: 1);;
										domain.debit.last2opening += response.data[2].openingBalanceCents / 100 * (($scope.data.inverse)? -1: 1);;
										domain.debit.currentclosing += response.data[0].closingBalanceCents / 100 * (($scope.data.inverse)? -1: 1);;
										domain.debit.last1closing += response.data[1].closingBalanceCents / 100 * (($scope.data.inverse)? -1: 1);;
										domain.debit.last2closing += response.data[2].closingBalanceCents / 100 * (($scope.data.inverse)? -1: 1);;
									}
								}})(domainName))
							);
					}
					
					$q.all(promises).then(function() {
						for (var domainName in me.domains) {
							var domain = me.domains[domainName];
//							$scope.data.total += domain.debit.total;
							$scope.data.current += domain.debit.current;
							$scope.data.last1 += domain.debit.last1;
							$scope.data.last2 += domain.debit.last2;
//							$scope.data.counttotal += domain.debit.counttotal;
							$scope.data.countcurrent += domain.debit.countcurrent;
							$scope.data.countlast1 += domain.debit.countlast1;
							$scope.data.countlast2 += domain.debit.countlast2;
							$scope.data.currentopening += domain.debit.currentopening;
							$scope.data.last1opening += domain.debit.last1opening;
							$scope.data.last2opening += domain.debit.last2opening;
							$scope.data.currentclosing += domain.debit.currentclosing;
							$scope.data.last1closing += domain.debit.last1closing;
							$scope.data.last2closing += domain.debit.last2closing;
						}
						completed = true;
						console.debug("balancesummary | updated completed flag");
					});
				};
				
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
							console.debug("balancesummary | completed, refreshing");
							completed = false;
							$scope.refresh();
						} else {
							console.debug("balancesummary | not completed, cannot refresh yet");
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