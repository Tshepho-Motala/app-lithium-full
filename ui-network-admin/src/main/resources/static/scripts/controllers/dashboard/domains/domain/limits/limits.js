'use strict';

angular.module('lithium')
	.controller('domainLimits', ['domain', 'notify', '$uibModal', '$scope', 'domainLimitsRest', 'errors', 'bsLoadingOverlayService',
	function(domain, notify, $uibModal, $scope, domainLimitsRest, errors, bsLoadingOverlayService) {
		$scope.dailyLossLimit = undefined;
		$scope.weeklyLossLimit = undefined;
		$scope.monthlyLossLimit = undefined;
		$scope.dailyWinLimit = undefined;
		$scope.weeklyWinLimit = undefined;
		$scope.monthlyWinLimit = undefined;
		
		$scope.changelogs = {
			domainName: domain.name,
			entityId: domain.id,
			restService: domainLimitsRest,
			reload: 0
		}
		
		$scope.refresh = function() {
			domainLimitsRest.findDomainLimit(domain.name, domainLimitsRest.GRANULARITY_DAY, domainLimitsRest.LIMIT_TYPE_WIN).then(function(response) {
				$scope.dailyWinLimit = (response.amount != undefined)? response.amount / 100: null;
			}).catch(function() {
				errors.catch('', false);
			});
			domainLimitsRest.findDomainLimit(domain.name, domainLimitsRest.GRANULARITY_DAY, domainLimitsRest.LIMIT_TYPE_LOSS).then(function(response) {
				$scope.dailyLossLimit = (response.amount != undefined)? response.amount / 100: null;
			}).catch(function() {
				errors.catch('', false);
			});
			domainLimitsRest.findDomainLimit(domain.name, domainLimitsRest.GRANULARITY_WEEK, domainLimitsRest.LIMIT_TYPE_WIN).then(function(response) {
				$scope.weeklyWinLimit = (response.amount != undefined)? response.amount / 100: null;
			}).catch(function() {
				errors.catch('', false);
			});
			domainLimitsRest.findDomainLimit(domain.name, domainLimitsRest.GRANULARITY_WEEK, domainLimitsRest.LIMIT_TYPE_LOSS).then(function(response) {
				$scope.weeklyLossLimit = (response.amount != undefined)? response.amount / 100: null;
			}).catch(function() {
				errors.catch('', false);
			});
			domainLimitsRest.findDomainLimit(domain.name, domainLimitsRest.GRANULARITY_MONTH, domainLimitsRest.LIMIT_TYPE_WIN).then(function(response) {
				$scope.monthlyWinLimit = (response.amount != undefined)? response.amount / 100: null;
			}).catch(function() {
				errors.catch('', false);
			});
			domainLimitsRest.findDomainLimit(domain.name, domainLimitsRest.GRANULARITY_MONTH, domainLimitsRest.LIMIT_TYPE_LOSS).then(function(response) {
				$scope.monthlyLossLimit = (response.amount != undefined)? response.amount / 100: null;
			}).catch(function() {
				errors.catch('', false);
			});
		}
		
		$scope.changeLimits = function(granularity, type) {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/domains/domain/limits/changelimits.html',
				controller: 'ChangeDomainLimitsModal',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'md',
				resolve: {
					domain: function() {
						return domain;
					},
					dailyLimit: function() {
						if (granularity === domainLimitsRest.GRANULARITY_DAY) {
							if (type === domainLimitsRest.LIMIT_TYPE_WIN) {
								return $scope.dailyWinLimit;
							} else if (type === domainLimitsRest.LIMIT_TYPE_LOSS) {
								return $scope.dailyLossLimit;
							}
						} else {
							return null;
						}
					},
					weeklyLimit: function() {
						if (granularity === domainLimitsRest.GRANULARITY_WEEK) {
							if (type === domainLimitsRest.LIMIT_TYPE_WIN) {
								return $scope.weeklyWinLimit;
							} else if (type === domainLimitsRest.LIMIT_TYPE_LOSS) {
								return $scope.weeklyLossLimit;
							}
						} else {
							return null;
						}
					},
					monthlyLimit: function() {
						if (granularity === domainLimitsRest.GRANULARITY_MONTH) {
							if (type === domainLimitsRest.LIMIT_TYPE_WIN) {
								return $scope.monthlyWinLimit;
							} else if (type === domainLimitsRest.LIMIT_TYPE_LOSS) {
								return $scope.monthlyLossLimit;
							}
						} else {
							return null;
						}
					},
					granularity: function() { return granularity; },
					type: function() { return type; },
					loadMyFiles: function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [ 'scripts/controllers/dashboard/domains/domain/limits/changelimits.js' ]
						})
					}
				}
			});
			
			modalInstance.result.then(function (result) {
				if (result) {
					if (granularity === domainLimitsRest.GRANULARITY_DAY) {
						if (type === domainLimitsRest.LIMIT_TYPE_WIN) {
							$scope.dailyWinLimit = result.amount / 100;
						} else if (type === domainLimitsRest.LIMIT_TYPE_LOSS) {
							$scope.dailyLossLimit = result.amount / 100;
						}
					} else if (granularity === domainLimitsRest.GRANULARITY_WEEK) {
						if (type === domainLimitsRest.LIMIT_TYPE_WIN) {
							$scope.weeklyWinLimit = result.amount / 100;
						} else if (type === domainLimitsRest.LIMIT_TYPE_LOSS) {
							$scope.weeklyLossLimit = result.amount / 100;
						}
					} else if (granularity === domainLimitsRest.GRANULARITY_MONTH) {
						if (type === domainLimitsRest.LIMIT_TYPE_WIN) {
							$scope.monthlyWinLimit = result.amount / 100;
						} else if (type === domainLimitsRest.LIMIT_TYPE_LOSS) {
							$scope.monthlyLossLimit = result.amount / 100;
						}
					}
					$scope.changelogs.reload++;
				}
			});
		};
		
		$scope.removeLimit = function(granularity, type) {
			domainLimitsRest.removeDomainLimit(domain.name, granularity, type).then(function(response) {
				if (response === true) {
					if (granularity === domainLimitsRest.GRANULARITY_DAY) {
						if (type === domainLimitsRest.LIMIT_TYPE_WIN) {
							$scope.dailyWinLimit = null;
						} else if (type === domainLimitsRest.LIMIT_TYPE_LOSS) {
							$scope.dailyLossLimit = null;
						}
					} else if (granularity === domainLimitsRest.GRANULARITY_WEEK) {
						if (type === domainLimitsRest.LIMIT_TYPE_WIN) {
							$scope.weeklyWinLimit = null;
						} else if (type === domainLimitsRest.LIMIT_TYPE_LOSS) {
							$scope.weeklyLossLimit = null;
						}
					} else if (granularity === domainLimitsRest.GRANULARITY_MONTH) {
						if (type === domainLimitsRest.LIMIT_TYPE_WIN) {
							$scope.monthlyWinLimit = null;
						} else if (type === domainLimitsRest.LIMIT_TYPE_LOSS) {
							$scope.monthlyLossLimit = null;
						}
					}
					$scope.changelogs.reload++;
					notify.success('UI_NETWORK_ADMIN.LIMITS.DOMAIN.REMOVE.LIMIT.SUCCESS');
				}
			}).catch(function() {
				errors.catch('', false);
			});
		}
		
		$scope.refresh();
	}
]);
