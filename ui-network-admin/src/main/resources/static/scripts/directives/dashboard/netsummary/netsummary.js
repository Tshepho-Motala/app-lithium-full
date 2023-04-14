'use strict';

angular.module('lithium')
	.directive('netsummary', function() {
		return {
			templateUrl:'scripts/directives/dashboard/netsummary/netsummary.html',
			scope: {
				data: "=",
				granularity: "=",
				selectedDomains: "=?"
			},
			restrict: 'E',
			replace: true,
			controller: ['$q', '$http', '$scope', '$state', '$interval', function($q, $http, $scope, $state, $interval) {
				var completed = false;

				$scope.refresh = function() {
					console.debug("refresh", $scope.granularity);
					var me = this;
					
					if (angular.isUndefined($scope.selectedDomains)) {
						$scope.selectedDomains = [];
						$scope.selectedDomains = $scope.data.domains;
					}

					$scope.data.debit.link = $scope.data.link;
					$scope.data.debit.status = $scope.data.status;
					$scope.data.debit.total = 0;
					$scope.data.debit.current = 0;
					$scope.data.debit.last1 = 0;
					$scope.data.debit.last2 = 0;

					$scope.data.credit.link = $scope.data.link;
					$scope.data.credit.status = $scope.data.status;
					$scope.data.credit.total = 0;
					$scope.data.credit.current = 0;
					$scope.data.credit.last1 = 0;
					$scope.data.credit.last2 = 0;
					
					$scope.data.debit.counttotal = 0;
					$scope.data.debit.countcurrent = 0;
					$scope.data.debit.countlast1 = 0;
					$scope.data.debit.countlast2 = 0;
					
					$scope.data.credit.counttotal = 0;
					$scope.data.credit.countcurrent = 0;
					$scope.data.credit.countlast1 = 0;
					$scope.data.credit.countlast2 = 0;
					
					$scope.data.net.total = 0;
					$scope.data.net.current = 0;
					$scope.data.net.last1 = 0;
					$scope.data.net.last2 = 0;
					
					if (angular.isDefined($scope.data.debit.tran)) {
						$scope.data.debit.trans = [$scope.data.debit.tran];
						$scope.data.debit.ascontra = [false];
					}
					
					if (angular.isDefined($scope.data.credit.tran)) {
						$scope.data.credit.trans = [$scope.data.credit.tran];
						$scope.data.credit.ascontra = [false];
					}
					
					if (angular.isUndefined($scope.data.debit.ascontra)) {
						$scope.data.debit.ascontra = new Array();
						for (var i=0; i < $scope.data.debit.trans.length; i++) {
							$scope.data.debit.ascontra.push(false);
						}
					}
					
					if (angular.isUndefined($scope.data.credit.ascontra)) {
						$scope.data.credit.ascontra = new Array();
						for (var i=0; i < $scope.data.credit.trans.length; i++) {
							$scope.data.credit.ascontra.push(false);
						}
					}
					
					if (angular.isUndefined($scope.data.debit.accountCodes)) {
						$scope.data.debit.accountCodes = new Array();
						for (var i=0; i < $scope.data.debit.trans.length; i++) {
							$scope.data.debit.accountCodes.push($scope.data.accountCode);
						}
					}
					
					if (angular.isUndefined($scope.data.credit.accountCodes)) {
						$scope.data.credit.accountCodes = new Array();
						for (var i=0; i < $scope.data.credit.trans.length; i++) {
							$scope.data.credit.accountCodes.push($scope.data.accountCode);
						}
					}
					
					me.domains = [];
					var promises = [];
										
					for (var i in $scope.selectedDomains) {
						
						var domainName = $scope.selectedDomains[i];
						var domain = { debit: {}, credit: {}, net: {} };
						
						me.domains[domainName] = domain;
						
						var url = "services/service-accounting/summary/domaintrantype/" + $scope.selectedDomains[i];
						var findService = "/find?1=1";
						var findLastService = "/findLast?last=3";
						
						if ($scope.data.user != null) {
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
									findLastService = findLastService + "&testUsers="+$scope.data.isTestUser;
									findService = findService + "&testUsers="+$scope.data.isTestUser;
								}
							}
						}

				angular.forEach($scope.data.debit.trans, function(value, key) {
							/* Obtain sum totals */
							promises.push($http.get(url + findService +
									"&granularity=5"+
									"&accountCode="+$scope.data.debit.accountCodes[key]+
									"&transactionType=" + value + 
									"&currency=" + $scope.data.currency.code)
								.success((function(domainName) { return function(response, status) {
									if ($scope.data.debit.ascontra[key] === true && angular.isDefined(response.data[0])) {
										var tmp = response.data[0].debitCents;
										response.data[0].debitCents = response.data[0].creditCents;
										response.data[0].creditCents = tmp;
									}
									var domain = me.domains[domainName];
									
									if (angular.isUndefined(domain.debit.total)) domain.debit.total = 0; 
									if (angular.isUndefined(domain.debit.counttotal)) domain.debit.counttotal = 0;
									if (angular.isUndefined(domain.net.counttotal)) domain.net.counttotal = 0;
									
									if (response.data[0]) {
										if ($scope.data.nocalc) {
											domain.debit.total += response.data[0].debitCents / 100 * 
													(($scope.data.inverse)? -1: 1);
											domain.net.counttotal += response.data[0].tranCount; //This is really supposed to be net, it is not an error
										} else {
											domain.debit.total += 
												(response.data[0].debitCents - response.data[0].creditCents) / 100 * 
													(($scope.data.inverse)? -1: 1);
											domain.debit.counttotal += response.data[0].tranCount;
										}
									}
								}})(domainName))
							);
						});

						angular.forEach($scope.data.credit.trans, function(value, key) {
							promises.push($http.get(url + findService +
									"&granularity=5"+
									"&accountCode="+$scope.data.credit.accountCodes[key]+
									"&transactionType=" + value + 
									"&currency=" + $scope.data.currency.code)
								.success((function(domainName) { return function(response, status) {
									if ($scope.data.credit.ascontra[key] === true && angular.isDefined(response.data[0])) {
										var tmp = response.data[0].debitCents;
										response.data[0].debitCents = response.data[0].creditCents;
										response.data[0].creditCents = tmp;
									}
									var domain = me.domains[domainName];
									
									if (angular.isUndefined(domain.credit.total)) domain.credit.total = 0; 
									if (angular.isUndefined(domain.credit.counttotal)) domain.credit.counttotal = 0;
									if (angular.isUndefined(domain.net.counttotal)) domain.net.counttotal = 0;
									
									if (response.data[0]) {
										if ($scope.data.nocalc) {
											domain.credit.total += response.data[0].creditCents / 100 * 
													(($scope.data.inverse)? -1: 1);
											domain.net.counttotal += response.data[0].tranCount; //This is really supposed to be net, it is not an error
										} else {
											domain.credit.total += 
												(response.data[0].creditCents - response.data[0].debitCents) / 100 * 
													(($scope.data.inverse)? -1: 1);
											domain.credit.counttotal += response.data[0].tranCount;
										}
									}
								}})(domainName))
							);
						});
						
						angular.forEach($scope.data.debit.trans, function(value, key) {
							/* Obtain last 3 */
							promises.push($http.get(url + findLastService + 
									"&granularity="+$scope.granularity+
									"&accountCode="+$scope.data.debit.accountCodes[key]+
									"&transactionType=" + value + 
									"&currency=" + $scope.data.currency.code)
								.success((function(domainName) { return function(response, status) {
									if ($scope.data.debit.ascontra[key] === true && angular.isDefined(response.data[0])) {
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
									if (angular.isUndefined(domain.debit.countcurrent)) domain.debit.countcurrent = 0;
									if (angular.isUndefined(domain.debit.countlast1)) domain.debit.countlast1 = 0;
									if (angular.isUndefined(domain.debit.countlast2)) domain.debit.countlast2 = 0;
									if (angular.isUndefined(domain.net.countcurrent)) domain.net.countcurrent = 0;
									if (angular.isUndefined(domain.net.countlast1)) domain.net.countlast1 = 0;
									if (angular.isUndefined(domain.net.countlast2)) domain.net.countlast2 = 0;

									if (response.data) {
										if ($scope.data.nocalc) {
											domain.debit.current += (response.data[0].debitCents) / 100 * (($scope.data.inverse)? -1: 1);
											domain.debit.last1 += (response.data[1].debitCents) / 100 * (($scope.data.inverse)? -1: 1);
											domain.debit.last2 += (response.data[2].debitCents) / 100 * (($scope.data.inverse)? -1: 1);
											domain.net.countcurrent += response.data[0].tranCount;
											domain.net.countlast1 += response.data[1].tranCount;
											domain.net.countlast2 += response.data[2].tranCount;
										} else {
											domain.debit.current += (response.data[0].debitCents - response.data[0].creditCents) / 100 * (($scope.data.inverse)? -1: 1);
											domain.debit.last1 += (response.data[1].debitCents - response.data[1].creditCents) / 100 * (($scope.data.inverse)? -1: 1);
											domain.debit.last2 += (response.data[2].debitCents - response.data[2].creditCents) / 100 * (($scope.data.inverse)? -1: 1);
											domain.debit.countcurrent += response.data[0].tranCount;
											domain.debit.countlast1 += response.data[1].tranCount;
											domain.debit.countlast2 += response.data[2].tranCount;
										}
									}
								}})(domainName))
							);
						});
						
						angular.forEach($scope.data.credit.trans, function(value, key) {
							promises.push($http.get(url + findLastService + 
									"&granularity="+$scope.granularity+
									"&accountCode="+$scope.data.credit.accountCodes[key]+
									"&transactionType=" + value + 
									"&currency=" + $scope.data.currency.code)
								.success((function(domainName) { return function(response, status) {

									if ($scope.data.credit.ascontra[key] === true) {
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
									if (angular.isUndefined(domain.credit.current)) domain.credit.current = 0;
									if (angular.isUndefined(domain.credit.last1)) domain.credit.last1 = 0;
									if (angular.isUndefined(domain.credit.last2)) domain.credit.last2 = 0;
									if (angular.isUndefined(domain.credit.countcurrent)) domain.credit.countcurrent = 0;
									if (angular.isUndefined(domain.credit.countlast1)) domain.credit.countlast1 = 0;
									if (angular.isUndefined(domain.credit.countlast2)) domain.credit.countlast2 = 0;
									if (angular.isUndefined(domain.net.countcurrent)) domain.net.countcurrent = 0;
									if (angular.isUndefined(domain.net.countlast1)) domain.net.countlast1 = 0;
									if (angular.isUndefined(domain.net.countlast2)) domain.net.countlast2 = 0;
									
									if (response.data) {
										if ($scope.data.nocalc) {
											domain.credit.current += (response.data[0].creditCents) / 100 * (($scope.data.inverse)? -1: 1);
											domain.credit.last1 += (response.data[1].creditCents ) / 100 * (($scope.data.inverse)? -1: 1);
											domain.credit.last2 += (response.data[2].creditCents) / 100 * (($scope.data.inverse)? -1: 1);
											domain.net.countcurrent += response.data[0].tranCount;
											domain.net.countlast1 += response.data[1].tranCount;
											domain.net.countlast2 += response.data[2].tranCount;
										} else {
											domain.credit.current += (response.data[0].creditCents - response.data[0].debitCents) / 100 * (($scope.data.inverse)? -1: 1);
											domain.credit.last1 += (response.data[1].creditCents - response.data[1].debitCents) / 100 * (($scope.data.inverse)? -1: 1);
											domain.credit.last2 += (response.data[2].creditCents - response.data[2].debitCents) / 100 * (($scope.data.inverse)? -1: 1);
											domain.credit.countcurrent += response.data[0].tranCount;
											domain.credit.countlast1 += response.data[1].tranCount;
											domain.credit.countlast2 += response.data[2].tranCount;
										}
									}
								}})(domainName))
							);
						});
					}
					
					$q.all(promises).then(function() {
						for (var domainName in me.domains) {
							var domain = me.domains[domainName];

							$scope.data.debit.total += domain.debit.total;
							$scope.data.debit.current += domain.debit.current;
							$scope.data.debit.last1 += domain.debit.last1;
							$scope.data.debit.last2 += domain.debit.last2;
							$scope.data.debit.counttotal += domain.debit.counttotal;
							$scope.data.debit.countcurrent += domain.debit.countcurrent;
							$scope.data.debit.countlast1 += domain.debit.countlast1;
							$scope.data.debit.countlast2 += domain.debit.countlast2;
	
							$scope.data.credit.total += domain.credit.total;
							$scope.data.credit.current += domain.credit.current;
							$scope.data.credit.last1 += domain.credit.last1;
							$scope.data.credit.last2 += domain.credit.last2;
							$scope.data.credit.counttotal += domain.credit.counttotal;
							$scope.data.credit.countcurrent += domain.credit.countcurrent;
							$scope.data.credit.countlast1 += domain.credit.countlast1;
							$scope.data.credit.countlast2 += domain.credit.countlast2;
							
							if (angular.isDefined(domain.net.counttotal) && angular.isUndefined($scope.data.net.counttotal)) {
								$scope.data.net.counttotal = 0;
								$scope.data.net.countcurrent = 0;
								$scope.data.net.countlast1 = 0;
								$scope.data.net.countlast2 = 0;
							}
							if (angular.isDefined(domain.net.counttotal)) {
								$scope.data.net.counttotal += domain.net.counttotal;
								$scope.data.net.countcurrent += domain.net.countcurrent;
								$scope.data.net.countlast1 += domain.net.countlast1;
								$scope.data.net.countlast2 += domain.net.countlast2;
							}
						}
						$scope.data.net.total = $scope.data.debit.total - $scope.data.credit.total;
						$scope.data.net.current = $scope.data.debit.current - $scope.data.credit.current;
						$scope.data.net.last1 = $scope.data.debit.last1 - $scope.data.credit.last1;
						$scope.data.net.last2 = $scope.data.debit.last2 - $scope.data.credit.last2;
						completed = true;
						console.debug("netsummary | updated completed flag");
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
							console.debug("netsummary | completed, refreshing");
							completed = false;
							$scope.refresh();
						} else {
							console.debug("netsummary | not completed, cannot refresh yet");
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