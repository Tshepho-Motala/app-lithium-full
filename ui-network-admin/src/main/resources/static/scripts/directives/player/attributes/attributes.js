'use strict';

angular.module('lithium')
.directive('attributes', function() {
	return {
		templateUrl:'scripts/directives/player/attributes/attributes.html',
		scope: {
			data: "=",
			user: "=ngModel"
		},
		restrict: 'E',
		replace: true,
		controller: ['$q', '$uibModal', '$scope', '$filter', 'UserRest', 'notify', 'errors', 'bsLoadingOverlayService','userLimitsRest',
		function($q, $uibModal, $scope, $filter, UserRest, notify, errors, bsLoadingOverlayService, userLimitsRest) {
//			$scope.referenceId = 'personal-overlay';
//			$scope.loadUser = function() {
//				bsLoadingOverlayService.start({referenceId:$scope.referenceId});
//				UserRest.findById($scope.data.domainName, $scope.data.userId).then(function(response) {
//					$scope.user = response;
//				}).catch(
//					errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.PERSONAL", false)
//				).finally(function () {
//					bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
//				});
//			}
//			$scope.loadUser();


			$scope.getLossLimitVisibility = function() {
				userLimitsRest.getLossLimitVisibility($scope.user.domain.name, $scope.user.guid).then(function(response) {
					$scope.lossLimitVisibility = response.lossLimitsVisibility;
					$scope.user.lossLimitVisibility = response.lossLimitsVisibility;
				}).catch(function() {
					errors.catch('', false);
				});
			}
			$scope.getLossLimitVisibility();

			console.log("Loading attribute ", $scope.lossLimitVisibility);

			let domainSettings = $scope.data.domainSettings;


			$scope.updateFailedLoginBlock = function () {
				let failedLoginBlock = $uibModal.open({
					animation : true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					templateUrl: 'scripts/directives/player/personal/updateFailedLoginBlock.html',
					controller: 'ConfirmUpdateFailedLoginBlock',
					controllerAs: 'controller',
					backdrop: 'static',
					size: 'md',
					resolve: {
						user: function () {
							return $scope.user;
						},
						referenceId: function() {
							return $scope.referenceId;
						},
						loadMyFiles: function($ocLazyLoad) {
							return $ocLazyLoad.load({
								name: 'lithium',
								files: ['scripts/directives/player/personal/updateFailedLoginBlock.js']
							});
						}
					}
				});

				failedLoginBlock.result.then(function(user) {
					$scope.user = angular.copy(user, $scope.user);
					notify.success("UI_NETWORK_ADMIN.PLAYER.EXCESSIVE_FAILED_LOGIN_BLOCK_UPDATE_SUCCESS");
				});
			}

			$scope.toggleAutoWithdrawalAllowed = function() {
				$scope.referenceId = 'personal-overlay';
				bsLoadingOverlayService.start({referenceId:$scope.referenceId});
				UserRest.toggleAutoWithdrawalAllowed($scope.data.domainName, $scope.data.userId).then(function(response) {
					$scope.user.autoWithdrawalAllowed = response.autoWithdrawalAllowed;
					notify.success($scope.user.autoWithdrawalAllowed === true? "Successfully allowed auto withdrawals for player" : "Successfully disallowed auto withdrawals for player");
				}).catch(
					errors.catch("", false)
				).finally(function () {
					bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
				});
			};


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

			$scope.toggleSowValidation = function() {
				$scope.referenceId = 'personal-overlay';
				bsLoadingOverlayService.start({referenceId:$scope.referenceId});
				UserRest.toggleSowValidation($scope.data.domainName, $scope.data.userId).then(function(response) {
					$scope.user.requireSowDocument = response.requireSowDocument;
					notify.success($scope.user.requireSowDocument === true? "UI_NETWORK_ADMIN.PLAYER.MESSAGE.SUCCESS_ENABLE" : "UI_NETWORK_ADMIN.PLAYER.MESSAGE.SUCCESS_DISABLE");
				}).catch(
						errors.catch("UI_NETWORK_ADMIN.PLAYER.MESSAGE.ERROR", false)
				).finally(function () {
					bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
				});
			};
		}]
	}
});
