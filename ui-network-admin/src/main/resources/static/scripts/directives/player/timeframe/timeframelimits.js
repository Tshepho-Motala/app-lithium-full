'use strict';

angular.module('lithium')
	.directive('timeframelimit', function() {
		return {
			templateUrl:'scripts/directives/player/timeframe/timeframelimits.html',
			scope: {
				domain: "=",
				data: "=",
				user: "=ngModel"
			},
			restrict: 'E',
			replace: true,
			controller: ['$q', '$uibModal', '$scope', 'notify', 'errors', 'bsLoadingOverlayService', 'userLimitsRest',
				function($q, $uibModal, $scope, notify, errors, bsLoadingOverlayService, userLimitsRest) {
					$scope.referenceId = 'timeframe-limit-overlay-'+$scope.user.guid;

					// var controller = this;
					$scope.limitFromUtc = 0;
					$scope.limitToUtc = 0;
					$scope.limitFromDisplay = null
					$scope.limitToDisplay = null
					$scope.hasTimeLimit = false;
					$scope.accessTimeLimit = true;

					var isNotUndefined = function (object) {
						return object !== null && object !== undefined
					}

					$scope.changeLimit = function() {
						$uibModal.open({
							animation: true,
							ariaLabelledBy: 'modal-title',
							ariaDescribedBy: 'modal-body',
							templateUrl: 'scripts/directives/player/timeframe/changetimeframe.html',
							controller: 'ChangeTimeFrameModal',
							controllerAs: 'controller',
							backdrop: 'static',
							size: 'md',
							resolve: {
								user: function() {
									return $scope.user;
								},
								loadMyFiles: function($ocLazyLoad) {
									return $ocLazyLoad.load({
										name:'lithium',
										files: [ 'scripts/directives/player/timeframe/changetimeframe.js' ]
									})
								},
							}
						});
					};

					//Remove
					$scope.removeLimit = function() {
						userLimitsRest.removeTimeSlotLimit($scope.user.guid, $scope.user.id ,$scope.user.domain.name)
							.then((response) => {
								if (response) { // Response returns as a boolean or undefined
									$scope.resetTimeSlotLimit()
									notify.success('UI_NETWORK_ADMIN.TIME_SLOT.DELETE.SUCCESS');
								} else {
									notify.error('UI_NETWORK_ADMIN.TIME_SLOT.DELETE.ERROR');
								}
							});
					}

					$scope.resetTimeSlotLimit = function() {
						$scope.hasTimeLimit = false
						$scope.limitFromUtc = 0
						$scope.limitToUtc = 0
						$scope.limitFromDisplay = null
						$scope.limitToDisplay = null
					}

					$scope.setTimeSlotLimit = function(response) {
						$scope.hasTimeLimit = response.exists;
						$scope.limitFromUtc = response.limitFromUtc;
						$scope.limitToUtc = response.limitToUtc;
						$scope.limitFromDisplay = response.limitFromDisplay;
						$scope.limitToDisplay = response.limitToDisplay;
					}

					$scope.init = async function() {
						try {
							if($scope.domain.playerTimeSlotLimits) {
								const response = await userLimitsRest.findTimeSlotLimit($scope.user.guid, $scope.user.domain.name);
								$scope.setTimeSlotLimit(response);
							}

							$scope.accessTimeLimit = $scope.domain.playerTimeSlotLimits; // This returns a boolean for domain settings, if enabled or not.
						} catch (e) {
							errors.catch('', false);
						}

					}

					$scope.init();
					userLimitsRest.onTimeSlotLimitFetched = () => {
						$scope.init();
					}
				}]
			}
	});

