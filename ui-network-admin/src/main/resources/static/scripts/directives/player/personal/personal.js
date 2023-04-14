'use strict';

angular.module('lithium')
.directive('personal', function() {
	return {
		templateUrl:'scripts/directives/player/personal/personal.html',
		scope: {
			data: "=",
			user: "=ngModel"
		},
		restrict: 'E',
		replace: true,
		controller: ['$q', '$uibModal', '$scope', '$filter', 'UserRest', 'notify', 'errors', 'bsLoadingOverlayService',
		function($q, $uibModal, $scope, $filter, UserRest, notify, errors, bsLoadingOverlayService) {
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

			let domainSettings = $scope.data.domainSettings;
			$scope.changePersonalInfo = function() {
				var modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					backdrop: 'static',
					templateUrl: 'scripts/directives/player/personal/changepersonal.html',
					controller: 'ChangePersonalModal',
					controllerAs: 'controller',
					size: 'md',
					resolve: {
						type: function() {return $scope.data.type;},
						user: function() {return angular.copy($scope.user);},
						profile: function() {return $scope.data.profile;},
						domainSettings: function() {return $scope.data.domainSettings;},
						loadMyFiles: function($ocLazyLoad) {
							return $ocLazyLoad.load({
								name:'lithium',
								files: [ 'scripts/directives/player/personal/changepersonal.js' ]
							})
						}
					}
				});
				
				modalInstance.result.then(function (user) {
					$scope.user = angular.copy(user, $scope.user);
					UserRest.findAdditionalDataByUserGuid(user.domain.name, user.id).then(function(additionalData) {
						$scope.user.additionalData = additionalData.plain();
					}).catch(function(error) {
						errors.catch('', false)(error)
					});
					notify.success("Personal details updated successfully");
				});
			};


			$scope.changeDateOfBirth = function() {
                var modalInstance = $uibModal.open({
                    animation: true,
                    ariaLabelledBy: 'modal-title',
                    ariaDescribedBy: 'modal-body',
                    templateUrl: 'scripts/directives/player/personal/changeDateOfBirth.html',
                    controller: 'ChangeDoBModal',
                    controllerAs: 'controller',
                    size: 'md',
                    resolve: {
                        type: function () {
                            return $scope.data.type;
                        },
                        user: function () {
                            return angular.copy($scope.user);
                        },
                        profile: function () {
                            return $scope.data.profile;
                        },
						domainSettings: function () {
							return $scope.data.domainSettings
						},
						loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'lithium',
                                files: ['scripts/directives/player/personal/changeDateOfBirth.js']
                            })
                        }
                    }

                });

                modalInstance.result.then(function (user) {
                    $scope.user = angular.copy(user, $scope.user);
                    notify.success("UI_NETWORK_ADMIN.PLAYER.DATE_OF_BIRTH_UPDATE_SUCCESS");
                });
            };

			$scope.dateOfBirthFormatted = function() {
				var user = $scope.user;
				if (user.dobDay != null && user.dobMonth != null && user.dobYear != null) {
					var date = new Date(user.dobYear, user.dobMonth - 1, user.dobDay);
					return $filter('date')(date, 'dd.MM.yyyy');
				} else {
					return "";
				}
			};

			$scope.ageInYears = function() {
				var user = $scope.user;
				if (user.dobDay != null && user.dobMonth != null && user.dobYear != null) {
					var date = new Date(user.dobYear, user.dobMonth - 1, user.dobDay);
					return "(" + moment().diff(date, 'years', false) + " years old)";
				} else {
					return "";
				}
			};

			$scope.toggleEmailValidation = function() {
				$scope.referenceId = 'personal-overlay';
				bsLoadingOverlayService.start({referenceId:$scope.referenceId});
				UserRest.toggleEmailValidation($scope.data.domainName, $scope.data.userId).then(function(response) {
					$scope.user.emailValidated = response.emailValidated;
					notify.success($scope.user.emailValidated === true? "UI_NETWORK_ADMIN.USER.EMAIL.VALIDATION.SUCCESS" : "UI_NETWORK_ADMIN.USER.EMAIL.INVALIDATION.SUCCESS");
				}).catch(
					errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.VALIDATE_EMAIL", false)
				).finally(function () {
					bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
				});
			};

			
			$scope.toggleMobileValidation = function() {
				$scope.referenceId = 'personal-overlay';
				bsLoadingOverlayService.start({referenceId:$scope.referenceId});
				UserRest.toggleMobileValidation($scope.data.domainName, $scope.data.userId).then(function(response) {
					$scope.user.cellphoneValidated = response.cellphoneValidated;
					notify.success($scope.user.cellphoneValidated === true? "UI_NETWORK_ADMIN.USER.MOBILE.VALIDATION.SUCCESS" : "UI_NETWORK_ADMIN.USER.MOBILE.INVALIDATION.SUCCESS");
				}).catch(
					errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.VALIDATE_MOBILE", false)
				).finally(function () {
					bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
				});
			}

			$scope.getPendingEmail = function (user) {
				//on update user.current.labelValue list is null and won't be updated till a full page refresh occurs
				if (domainSettings == null) {
					return '';
				}

				if (user.additionalData !== null) {
					if (user.additionalData['pendingEmail'] != null && domainSettings['pending_email_validation_activate']) {
						return user.additionalData['pendingEmail'];
					}
				}

				return '';
			}

			$scope.redoValidation = function (pendingEmail, userId) {
				$scope.referenceId = 'personal-overlay';
				bsLoadingOverlayService.start({referenceId:$scope.referenceId});
				UserRest.redoEmailValidation($scope.data.domainName, userId, pendingEmail).then(function() {
					notify.success("UI_NETWORK_ADMIN.USER.EMAIL.VALIDATION.PROCESSED");
				}).catch(
						errors.catch("UI_NETWORK_ADMIN.USER.EMAIL.VALIDATION.FAILED", false)
				).finally(function () {
					bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
				});
			}
		}]
	}
});
