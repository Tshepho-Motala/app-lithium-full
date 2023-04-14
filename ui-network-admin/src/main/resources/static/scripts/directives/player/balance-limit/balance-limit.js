'use strict';

angular.module('lithium')
    .directive('balanceLimit', function () {
        return {
            templateUrl: 'scripts/directives/player/balance-limit/balance-limit.html',
            scope: {
                domain: "=",
                balanceLimits: "=",
                user: "=ngModel"
            },
            restrict: 'E',
            replace: true,
            controller: ['$q', '$uibModal', '$scope', 'notify', 'errors', 'bsLoadingOverlayService', 'userLimitsRest', 'rest-domain',
                function ($q, $uibModal, $scope, notify, errors, bsLoadingOverlayService, userLimitsRest, restDomain) {
                    $scope.referenceId = 'bal-limit-overlay-' + $scope.user.guid;

                    $scope.modifyBalanceLimit = function () {
                        var modalInstance = $uibModal.open({
                            animation: true,
                            ariaLabelledBy: 'modal-title',
                            ariaDescribedBy: 'modal-body',
                            templateUrl: 'scripts/directives/player/balance-limit/change-limit.html',
                            controller: 'ChangeBalanceLimitModal',
                            controllerAs: 'controller',
                            backdrop: 'static',
                            size: 'md',
                            resolve: {
                                user: function() {
                                    return $scope.user;
                                },
                                domain: function () {
                                    return $scope.domain;
                                },
                                newLimit: function() {
                                        return ($scope.balanceLimits.current === null) ? undefined : $scope.balanceLimits.current.amount / 100;
                                },
                                currentLimit: function() {
                                        return ($scope.balanceLimits.current === null) ? undefined : $scope.balanceLimits.current.amount / 100;
                                },
                                loadMyFiles: function($ocLazyLoad) {
                                    return $ocLazyLoad.load({
                                        name: 'lithium',
                                        files: [ 'scripts/directives/player/balance-limit/change-limit.js' ]
                                    })
                                }
                            }
                        });

                        modalInstance.result.then(function(result) {
                            if (result) {
                                console.log("Changed balance limit to : ", result);
                                $scope.balanceLimits = result;
                            }
                        });
                    };

                    $scope.removePendingBalanceLimit = function () {
                        userLimitsRest.balanceLimitRemovePending($scope.domain.name, $scope.user.guid).then(function(response) {
                            $scope.balanceLimits = response;
                        });
                    }
                }]
        }
    });
