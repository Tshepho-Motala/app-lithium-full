'use strict';

angular.module('lithium')
    .directive('verificationstatus', function() {
        return {
            templateUrl:'scripts/directives/player/verificationstatus/verificationstatus.html',
            scope: {
                data: "=",
                user: "=ngModel"
            },
            restrict: 'E',
            replace: true,
            controller: ['$q', '$uibModal', '$scope', 'UserRest', 'notify', 'errors', 'bsLoadingOverlayService',
                function($q, $uibModal, $scope, UserRest, notify, errors, bsLoadingOverlayService) {
                    $scope.data.showHistory = ($scope.data.showHistory) || false;
                    $scope.changeStatus = function() {
                        $scope.userCopy = angular.copy($scope.user);
                        var modalInstance = $uibModal.open({
                            animation: true,
                            ariaLabelledBy: 'modal-title',
                            ariaDescribedBy: 'modal-body',
                            templateUrl: 'scripts/directives/player/verificationstatus/changestatus.html',
                            controller: 'ChangeStatusVerificationModal',
                            controllerAs: 'vm',
                            backdrop: 'static',
                            size: 'md',
                            resolve: {
                                user: function() {return $scope.userCopy;},
                                loadMyFiles: function($ocLazyLoad) {
                                    return $ocLazyLoad.load({
                                        name:'lithium',
                                        files: [ 'scripts/directives/player/verificationstatus/changestatus.js' ]
                                    })
                                }
                            }
                        });
                        modalInstance.result.then(function (user) {
                            $scope.user.verificationStatus = user.verificationStatus;
                            $scope.user.ageVerified = user.ageVerified;
                            $scope.user.addressVerified = user.addressVerified;
                            notify.success("Status updated successfully");
                        });
                    };

                    $scope.toggleAddressVerification = function () {
                        $scope.referenceId = 'personal-overlay';
                        bsLoadingOverlayService.start({referenceId: $scope.referenceId});
                        UserRest.toggleAddressVerification($scope.user.domain.name, $scope.user.id).then(function (response) {
                            $scope.user.addressVerified = response.addressVerified;
                            $scope.user.verificationStatus = response.verificationStatus;
                            notify.success($scope.user.addressVerified === true ? "UI_NETWORK_ADMIN.USER.ADDRESS_VERIFIED.VALIDATION.SUCCESS" : "UI_NETWORK_ADMIN.USER.ADDRESS_VERIFIED.INVALIDATION.SUCCESS");
                        }).catch(
                            errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.ADDRESS_VERIFIED", false)
                        ).finally(function () {
                            bsLoadingOverlayService.stop({referenceId: $scope.referenceId});
                        });
                    };

                    $scope.toggleAgeVerification = function () {
                        $scope.referenceId = 'personal-overlay';
                        bsLoadingOverlayService.start({referenceId: $scope.referenceId});
                        UserRest.toggleAgeVerification($scope.user.domain.name, $scope.user.id).then(function (response) {
                            $scope.user.ageVerified = response.ageVerified;
                            $scope.user.verificationStatus = response.verificationStatus;
                            notify.success($scope.user.ageVerified === true ? "UI_NETWORK_ADMIN.USER.AGE_VERIFIED.VALIDATION.SUCCESS" : "UI_NETWORK_ADMIN.USER.AGE_VERIFIED.INVALIDATION.SUCCESS");
                        }).catch(
                            errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.AGE_VERIFIED", false)
                        ).finally(function () {
                            bsLoadingOverlayService.stop({referenceId: $scope.referenceId});
                        });
                    };
            }]
        }
    });
