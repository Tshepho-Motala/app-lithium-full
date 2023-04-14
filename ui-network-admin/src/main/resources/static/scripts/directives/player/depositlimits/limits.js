'use strict';

angular.module('lithium')
.directive('userdepositlimits', function() {
	return {
		templateUrl:'scripts/directives/player/depositlimits/limits.html',
		scope: {
			domain: "=",
			data: "=",
			user: "=ngModel"
		},
		restrict: 'E',
		replace: true,
		controller: ['$q', '$uibModal', '$scope', 'notify', 'errors', 'bsLoadingOverlayService', 'userLimitsRest', 'rest-domain',
		function($q, $uibModal, $scope, notify, errors, bsLoadingOverlayService, userLimitsRest, restDomain) {
			$scope.referenceId = 'dep-limit-overlay-'+$scope.user.guid;
			// $scope.dailyLimit = undefined;
			// $scope.dailyLimitUsed = undefined;
			// $scope.weeklyLimit = undefined;
			// $scope.weeklyLimitUsed = undefined;
			// $scope.monthlyLimit = undefined;
			// $scope.monthlyLimitUsed = undefined;

			$scope.dailyLimitPending = undefined;
			$scope.dailyLimitPendingCreated = undefined;
			$scope.weeklyLimitPending = undefined;
			$scope.weeklyLimitPendingCreated = undefined;
			$scope.monthlyLimitPending = undefined;
			$scope.monthlyLimitPendingCreated = undefined;

			$scope.dailyLimitSupposed = undefined;
			$scope.dailyLimitSupposedCreated = undefined;
			$scope.weeklyLimitSupposed = undefined;
			$scope.weeklyLimitSupposedCreated = undefined;
			$scope.monthlyLimitSupposed = undefined;
			$scope.monthlyLimitSupposedCreated = undefined;

			$scope.changeLimits = function(granularity) {
				var modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					templateUrl: 'scripts/directives/player/depositlimits/changelimits.html',
					controller: 'ChangeDepositLimitsModal',
					controllerAs: 'controller',
					backdrop: 'static',
					size: 'md',
					resolve: {
						user: function() {
							return $scope.user;
						},
						limit: function() {
							switch (granularity) {
								case userLimitsRest.GRANULARITY_DAY:
									return $scope.data.dailyLimit;
								case userLimitsRest.GRANULARITY_WEEK:
									return $scope.data.weeklyLimit;
								case userLimitsRest.GRANULARITY_MONTH:
									return $scope.data.monthlyLimit;
							}
						},
						limitUsed: function() {
							switch (granularity) {
								case userLimitsRest.GRANULARITY_DAY:
									return $scope.data.dailyLimitUsed;
								case userLimitsRest.GRANULARITY_WEEK:
									return $scope.data.weeklyLimitUsed;
								case userLimitsRest.GRANULARITY_MONTH:
									return $scope.data.monthlyLimitUsed;
							}
						},
						granularity: function() { return granularity; },
						loadMyFiles: function($ocLazyLoad) {
							return $ocLazyLoad.load({
								name: 'lithium',
								files: [ 'scripts/directives/player/depositlimits/changelimits.js' ]
							})
						}
					}
				});
				
				modalInstance.result.then(function(result) {
					if (result) {
						$scope.init();
						var user = angular.copy($scope.user);
						user.forceChangelogReload = (user.forceChangelogReload != undefined)? user.forceChangelogReload + 1: 1;
						$scope.user = user;
					}
				});
			};

			$scope.removePendingLimit = function(granularity) {
				userLimitsRest.depositLimitRemovePending($scope.user.guid, granularity).then(function() {
					$scope.init();
				});
			}
			$scope.removeSupposedLimit = function(granularity) {
				userLimitsRest.depositLimitRemoveSupposed($scope.user.guid, granularity).then(function() {
					$scope.init();
				});
			}
			$scope.applySupposedLimit = function(granularity) {
				userLimitsRest.depositLimitApplySupposed($scope.user.guid, granularity).then(function() {
					$scope.init();
				});
			}
			$scope.removeLimit = function(granularity) {
				userLimitsRest.removePlayerLimit($scope.user.guid, $scope.user.id, $scope.user.domain.name, granularity, userLimitsRest.DEPOSIT_LIMIT).then(function(response) {
					if (granularity === userLimitsRest.GRANULARITY_DAY) {
						$scope.data.dailyLimit = undefined;
						$scope.data.dailyLimitUsed = undefined;
					} else if (granularity === userLimitsRest.GRANULARITY_WEEK) {
						$scope.data.weeklyLimit = undefined;
						$scope.data.weeklyLimitUsed = undefined;
					} else if (granularity === userLimitsRest.GRANULARITY_MONTH) {
						$scope.data.monthlyLimit = undefined;
						$scope.data.monthlyLimitUsed = undefined;
					}
					var user = angular.copy($scope.user);
					user.forceChangelogReload = (user.forceChangelogReload != undefined)? user.forceChangelogReload + 1: 1;
					$scope.user = user;
					notify.success('UI_NETWORK_ADMIN.LIMITS.PLAYER.REMOVED');
				});
			}

			$scope.refresh = function() {
				$scope.init();
			}
			$scope.init = function() {
				// bsLoadingOverlayService.start({referenceId:$scope.referenceId});
				// restDomain.findByName($scope.user.domain.name).then(function(domain) {
					if (!$scope.symbol) {
						$scope.symbol = $scope.domain.currencySymbol+' ';
					}
				// }).catch(
				// 	errors.catch("UI_NETWORK_ADMIN.DEPOSITLIMITS.ERRORS.GETLIMITS", false)
				// );

				$scope.disabledMessage = undefined;

				// $scope.data.dailyLimit = undefined;
				// $scope.data.dailyLimitUsed = undefined;
				// $scope.data.weeklyLimit = undefined;
				// $scope.data.weeklyLimitUsed = undefined;
				// $scope.data.monthlyLimit = undefined;
				// $scope.data.monthlyLimitUsed = undefined;

				$scope.dailyLimitPending = undefined;
				$scope.dailyLimitPendingCreated = undefined;
				$scope.weeklyLimitPending = undefined;
				$scope.weeklyLimitPendingCreated = undefined;
				$scope.monthlyLimitPending = undefined;
				$scope.monthlyLimitPendingCreated = undefined;

				$scope.dailyLimitSupposed = undefined;
				$scope.dailyLimitSupposedCreated = undefined;
				$scope.weeklyLimitSupposed = undefined;
				$scope.weeklyLimitSupposedCreated = undefined;
				$scope.monthlyLimitSupposed = undefined;
				$scope.monthlyLimitSupposedCreated = undefined;

				userLimitsRest.depositLimitsPending($scope.user.guid).then(function(response) {
					if (!response._successful && (response._status === 481)) {
						$scope.disabledMessage = response._message;
					}
					restDomain.findCurrentDomainSetting($scope.user.guid.split("/")[0], 'default-deposit-limit-pending-periods-in-hr').then(function (resp) {
						$scope.pendingDepositTimeLeft = resp.id === undefined ? 24 : resp.labelValue.value;
					});
					angular.forEach(response.plain(), function(v,k) {
						if (v.granularity === userLimitsRest.GRANULARITY_DAY) {
							$scope.dailyLimitPending = v.amount / 100;
							$scope.dailyLimitPendingCreated = v.createdDate;
						} else if (v.granularity === userLimitsRest.GRANULARITY_WEEK) {
							$scope.weeklyLimitPending = v.amount / 100;
							$scope.weeklyLimitPendingCreated = v.createdDate;
						} else if (v.granularity === userLimitsRest.GRANULARITY_MONTH) {
							$scope.monthlyLimitPending = v.amount / 100;
							$scope.monthlyLimitPendingCreated = v.createdDate;
						}
					});
				}).catch(
					errors.catch("UI_NETWORK_ADMIN.DEPOSITLIMITS.ERRORS.GETLIMITS", false)
				);
				userLimitsRest.depositLimitsSupposed($scope.user.guid).then(function(response) {
					if (!response._successful && (response._status === 481)) {
						$scope.disabledMessage = response._message;
					}
					angular.forEach(response.plain(), function(v,k) {
						if (v.granularity === userLimitsRest.GRANULARITY_DAY) {
							$scope.dailyLimitSupposed = v.amount / 100;
							$scope.dailyLimitSupposedCreated = v.createdDate;
						} else if (v.granularity === userLimitsRest.GRANULARITY_WEEK) {
							$scope.weeklyLimitSupposed = v.amount / 100;
							$scope.weeklyLimitSupposedCreated = v.createdDate;
						} else if (v.granularity === userLimitsRest.GRANULARITY_MONTH) {
							$scope.monthlyLimitSupposed = v.amount / 100;
							$scope.monthlyLimitSupposedCreated = v.createdDate;
						}
					});
				}).catch(
					errors.catch("UI_NETWORK_ADMIN.DEPOSITLIMITS.ERRORS.GETLIMITS", false)
				);
				userLimitsRest.depositLimits($scope.user.guid).then(function(response) {
					if (!response._successful && (response._status === 481)) {
						$scope.disabledMessage = response._message;
					}
					angular.forEach(response.plain(), function(v,k) {
						if (v.granularity === userLimitsRest.GRANULARITY_DAY && v.id !== null) {
							$scope.data.dailyLimit = v.amount / 100;
							$scope.data.dailyLimitUsed = v.amountUsed / 100;
						} else if (v.granularity === userLimitsRest.GRANULARITY_WEEK && v.id !== null) {
							$scope.data.weeklyLimit = v.amount / 100;
							$scope.data.weeklyLimitUsed = v.amountUsed / 100;
						} else if (v.granularity === userLimitsRest.GRANULARITY_MONTH && v.id !== null) {
							$scope.data.monthlyLimit = v.amount / 100;
							$scope.data.monthlyLimitUsed = v.amountUsed / 100;
						}
					});
				}).catch(
					errors.catch("UI_NETWORK_ADMIN.DEPOSITLIMITS.ERRORS.GETLIMITS", false)
				).finally(function () {
					bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
				});
			}

			// TODO FIXME: This is here because after a change to the limits, the pending limits need to be retrieved.
			//  		   This really could have been done in a much nicer way but I am out of time.
			$scope.$watch('[data.dailyLimit, data.weeklyLimit, data.monthlyLimit]', function(newValue, oldValue) {
				if (newValue !== oldValue) {
					$scope.init();
				}
			});
	
			$scope.init();
		}]
	}
});
