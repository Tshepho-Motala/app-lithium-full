'use strict';

angular.module('lithium')
.directive('userlimits', function() {
	return {
		templateUrl:'scripts/directives/player/limits/limits.html',
		scope: {
			data: "=",
			user: "=ngModel"
		},
		restrict: 'E',
		replace: true,
		controller: ['$q', '$uibModal', '$scope', 'notify', 'errors', 'bsLoadingOverlayService', 'userLimitsRest', 'rest-domain',
		function($q, $uibModal, $scope, notify, errors, bsLoadingOverlayService, userLimitsRest, restDomain) {
			$scope.showHelp = false;

				$scope.loadCurrentLosses = function() {
				return restDomain.findByName(encodeURIComponent($scope.user.domain.name)).then(function(domain) {
					$scope.data.domain = domain.plain();
					userLimitsRest.findNetLossToHouse($scope.user.domain.name, $scope.user.guid, $scope.data.domain.currency, userLimitsRest.GRANULARITY_DAY).then(function(response) {
						$scope.data.dailyNetLossToHouse = (response.data != undefined)? response.data / 100: null;
					}).catch(function() {
						errors.catch('', false);
					});
					userLimitsRest.findNetLossToHouse($scope.user.domain.name, $scope.user.guid, $scope.data.domain.currency, userLimitsRest.GRANULARITY_WEEK).then(function(response) {
						$scope.data.weeklyNetLossToHouse = (response.data != undefined)? response.data / 100: null;
					}).catch(function() {
						errors.catch('', false);
					});
					userLimitsRest.findNetLossToHouse($scope.user.domain.name, $scope.user.guid, $scope.data.domain.currency, userLimitsRest.GRANULARITY_MONTH).then(function(response) {
						$scope.data.monthlyNetLossToHouse = (response.data != undefined)? response.data / 100: null;
					}).catch(function() {
						errors.catch('', false);
					});
					userLimitsRest.findNetLossToHouse($scope.user.domain.name, $scope.user.guid, $scope.data.domain.currency, userLimitsRest.GRANULARITY_YEAR).then(function(response) {
						$scope.data.annualNetLossToHouse = (response.data != undefined)? response.data / 100: null;
					}).catch(function() {
						errors.catch('', false);
					});
				}).catch(function() {
					errors.catch('', false);
				});
			}
			
			$scope.changeLimits = function(granularity) {
				var modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					backdrop: 'static',
					templateUrl: 'scripts/directives/player/limits/changelimits.html',
					controller: 'ChangeLimitsModal',
					controllerAs: 'controller',
					size: 'md',
					resolve: {
						user: function() {
							return $scope.user;
						},
						dailyLossLimit: function() {
							if (granularity === userLimitsRest.GRANULARITY_DAY) {
								return $scope.data.dailyLossLimit;
							} else {
								return null;
							}
						},
						weeklyLossLimit: function() {
							if (granularity === userLimitsRest.GRANULARITY_WEEK) {
								return $scope.data.weeklyLossLimit;
							} else {
								return null;
							}
						},
						monthlyLossLimit: function() {
							if (granularity === userLimitsRest.GRANULARITY_MONTH) {
								return $scope.data.monthlyLossLimit;
							} else {
								return null;
							}
						},
						annualLossLimit: function() {
							if (granularity === userLimitsRest.GRANULARITY_YEAR) {
								return $scope.data.annualLossLimit;
							} else {
								return null;
							}
						},
						granularity: function() { return granularity; },
						loadMyFiles: function($ocLazyLoad) {
							return $ocLazyLoad.load({
								name:'lithium',
								files: [ 'scripts/directives/player/limits/changelimits.js' ]
							})
						}
					}
				});
				
				modalInstance.result.then(function (result) {
					if (result) {
						$scope.loadCurrentLosses();
						if (granularity === userLimitsRest.GRANULARITY_DAY) {
							$scope.data.dailyLossLimit = result.amount / 100;
						} else if (granularity === userLimitsRest.GRANULARITY_WEEK) {
							$scope.data.weeklyLossLimit = result.amount / 100;
						} else if (granularity === userLimitsRest.GRANULARITY_MONTH) {
							$scope.data.monthlyLossLimit = result.amount / 100;
						} else if (granularity === userLimitsRest.GRANULARITY_YEAR) {
							$scope.data.annualLossLimit = result.amount / 100;
						}
						var user = angular.copy($scope.user);
						user.forceChangelogReload = (user.forceChangelogReload != undefined)? user.forceChangelogReload + 1: 1;
						$scope.user = user;
					}
				});
			};

			$scope.showHelpRow = function() {
				$scope.showHelp = !$scope.showHelp;
			}

			$scope.changeLossLimitVisibility = function() {
				var modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					backdrop: 'static',
					templateUrl: 'scripts/directives/player/limits/change-losslimit-visibility.html',
					controller: 'LossLimitVisibilityModal',
					controllerAs: 'controller',
					size: 'md',
					resolve: {
						user: function() {
							return $scope.user;
						},
						visibility: function() {
							return $scope.lossLimitVisibility;
						},
						loadMyFiles: function($ocLazyLoad) {
							return $ocLazyLoad.load({
								name: 'lithium',
								files: [ 'scripts/directives/player/limits/change-losslimit-visibility.js' ]
							})
						}
					}
				});

				modalInstance.result.then(function (result) {
					if (result) {
						$scope.lossLimitVisibility = result.lossLimitsVisibility;
						$scope.user.lossLimitVisibility = result.lossLimitsVisibility;
					}
				});
			};

			$scope.removeLimit = function(granularity) {
				userLimitsRest.removePlayerLimit($scope.user.guid, $scope.user.id, $scope.user.domain.name, granularity, userLimitsRest.LIMIT_TYPE_LOSS).then(function(response) {
					if (response === true) {
						if (granularity === userLimitsRest.GRANULARITY_DAY) {
							$scope.data.dailyLossLimit = null;
						} else if (granularity === userLimitsRest.GRANULARITY_WEEK) {
							$scope.data.weeklyLossLimit = null;
						} else if (granularity === userLimitsRest.GRANULARITY_MONTH) {
							$scope.data.monthlyLossLimit = null;
						} else if (granularity === userLimitsRest.GRANULARITY_YEAR) {
							$scope.data.annualLossLimit = null;
						}
						var user = angular.copy($scope.user);
						user.forceChangelogReload = (user.forceChangelogReload != undefined)? user.forceChangelogReload + 1: 1;
						$scope.user = user;
						notify.success('UI_NETWORK_ADMIN.LIMITS.PLAYER.REMOVED');
					}
				}).catch(function() {
					errors.catch('', false);
				});
			}

			$scope.getLossLimitVisibility = function() {
				userLimitsRest.getLossLimitVisibility($scope.user.domain.name, $scope.user.guid).then(function(response) {
					$scope.lossLimitVisibility = response.lossLimitsVisibility;
					$scope.user.lossLimitVisibility = response.lossLimitsVisibility;
				}).catch(function() {
					errors.catch('', false);
				});
			}
			$scope.getLossLimitVisibility();

			$scope.init = function() {
				$scope.loadCurrentLosses();
				$scope.getLossLimitVisibility();

				// daily loss limit
				userLimitsRest.findPlayerLimit($scope.user.guid, $scope.user.domain.name, userLimitsRest.GRANULARITY_DAY, userLimitsRest.LIMIT_TYPE_LOSS).then(function(response) {
					$scope.data.dailyLossLimit = (response.amount != undefined)? response.amount / 100: null;
				}).catch(function() {
					errors.catch('', false);
				});

				// weekly loss limit
				userLimitsRest.findPlayerLimit($scope.user.guid, $scope.user.domain.name, userLimitsRest.GRANULARITY_WEEK, userLimitsRest.LIMIT_TYPE_LOSS).then(function(response) {
					$scope.data.weeklyLossLimit = (response.amount != undefined)? response.amount / 100: null;
				}).catch(function() {
					errors.catch('', false)
				});

				// monthly loss limit
				userLimitsRest.findPlayerLimit($scope.user.guid, $scope.user.domain.name, userLimitsRest.GRANULARITY_MONTH, userLimitsRest.LIMIT_TYPE_LOSS).then(function(response) {
					$scope.data.monthlyLossLimit = (response.amount != undefined)? response.amount / 100: null;
				}).catch(function() {
					errors.catch('', false)
				});
				// annual loss limit
				userLimitsRest.findPlayerLimit($scope.user.guid, $scope.user.domain.name, userLimitsRest.GRANULARITY_YEAR, userLimitsRest.LIMIT_TYPE_LOSS).then(function(response) {
					$scope.data.annualLossLimit = (response.amount != undefined)? response.amount / 100: null;
				}).catch(function() {
					errors.catch('', false)
				});

			}

			if (!$scope.data)
				$scope.init();
		}]
	}
});
