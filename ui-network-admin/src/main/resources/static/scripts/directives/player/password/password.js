'use strict';

angular.module('lithium')
.directive('password', function() {
	return {
		templateUrl:'scripts/directives/player/password/password.html',
		scope: {
			data: "=",
			user: "=ngModel"
		},
		restrict: 'E',
		replace: true,
		controller: ['$q', '$uibModal', '$scope', 'UserRest', 'notify', 'errors', 'bsLoadingOverlayService',
		function($q, $uibModal, $scope, UserRest, notify, errors, bsLoadingOverlayService) {
//			$scope.referenceId = 'password-overlay';
//			$scope.loadUser = function() {
//				bsLoadingOverlayService.start({referenceId:$scope.referenceId});
//				UserRest.findById($scope.data.domainName, $scope.data.userId).then(function(response) {
//					$scope.user = response;
//				}).catch(
//					errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.PASSWORD", false)
//				).finally(function () {
//					bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
//				});
//			}
//			$scope.loadUser();
			$scope.changePassword = function() {
				var modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					templateUrl: 'scripts/directives/player/password/changepassword.html',
					controller: 'ChangePasswordModal',
					controllerAs: 'controller',
					size: 'md',
					resolve: {
						type: function() {return $scope.data.type;},
						user: function() {return $scope.user;},
						profile: function() {return $scope.data.profile;},
						loadMyFiles: function($ocLazyLoad) {
							return $ocLazyLoad.load({
								name:'lithium',
								files: [ 'scripts/directives/player/password/changepassword.js' ]
							})
						}
					}
				});
				
				modalInstance.result.then(function (user) {
					$scope.user.passwordUpdated = user.passwordUpdated;
					$scope.user.passwordUpdatedBy = user.passwordUpdatedBy;
					notify.success("Password updated successfully");
				});
			};

			$scope.resetPassword = function() {
				let modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					templateUrl: 'scripts/directives/player/password/password-reset.html',
					controller: 'PasswordResetModal',
					controllerAs: 'controller',
					size: 'md',
					resolve: {
						type: function() {return $scope.data.type;},
						user: function() {return $scope.user;},
						profile: function() {return $scope.data.profile;},
						loadMyFiles: function($ocLazyLoad) {
							return $ocLazyLoad.load({
								name:'lithium',
								files: [ 'scripts/directives/player/password/password-reset.js' ]
							})
						}
					}
				});

				modalInstance.result.then(function () {
					notify.success("Password reset token sent");
				});
			};
		}]
	}
});
