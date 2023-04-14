'use strict';

angular.module('lithium')
    .directive('userRestrictions', function() {
        return {
            templateUrl:'scripts/directives/player/user-restrictions/user-restrictions.html',
            scope: {
                data: "=",
                user: "=ngModel",
                userRestrictions: "="
            },
            restrict: 'E',
            replace: true,
            controller: ['$q', '$uibModal', '$scope', 'notify', '$translate', 'errors', 'bsLoadingOverlayService', 'UserRestrictionsRest',
                function($q, $uibModal, $scope, notify, $translate, errors, bsLoadingOverlayService, rest) {

                    if ($scope.userRestrictions === undefined) $scope.userRestrictions = [];

                    $scope.getBlockers = function(set) {
                        return set.restrictions.map(function(r) {
                           return r.restriction.name;
                        }).join(', ');
                    }

                    $scope.getActiveFrom = (userRestrictionSet) => {
                        return userRestrictionSet.activeFrom;
                    }

                    $scope.init = function() {
                        rest.get($scope.user.guid).then(function(response) {
                            $scope.userRestrictions = response.plain();
                        }).catch(function() {
                            errors.catch('', false);
                        });
                    }

                    $scope.set = function() {
                        var modalInstance = $uibModal.open({
                            animation: true,
                            ariaLabelledBy: 'modal-title',
                            ariaDescribedBy: 'modal-body',
                            templateUrl: 'scripts/directives/player/user-restrictions/set-user-restriction.html',
                            controller: 'SetUserRestrictionModal',
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
                                        files: [ 'scripts/directives/player/user-restrictions/set-user-restriction.js' ]
                                    })
                                }
                            }
                        });

                        modalInstance.result.then(function (result) {
                            $scope.userRestrictions.push(result);
                        });
                    }

                    $scope.getMessage = (userRestrictionSet) => {
                            
                        const subTypes = angular.range(1, userRestrictionSet.set.altMessageCount)

                        if(subTypes.includes(userRestrictionSet.subType) && $scope.isCompsRestriction(userRestrictionSet.set.restrictions)) {
                            return $translate.instant('UI_NETWORK_ADMIN.PLAYER.RESTRICTIONS.SUBORDINATES.' + userRestrictionSet.subType);
                        } else if(subTypes.includes(userRestrictionSet.subType) && $scope.isCasinoRestriction(userRestrictionSet.set.restrictions)) {
                            return $translate.instant('UI_NETWORK_ADMIN.PLAYER.RESTRICTIONS.CASINO_BLOCK.SUBORDINATE.MESSAGE.' + userRestrictionSet.subType);
                        }

                        return 'N/A';
                    }

                    $scope.isCompsRestriction = function (restrictions) {
                        return restrictions.find(restriction => restriction.restriction.code === 'COMPS') ? true : false;
                    }

                    $scope.isCasinoRestriction = function (restrictions) {
                        return restrictions.find(restriction => restriction.restriction.code === 'CASINO') ? true : false;
                    }

                    $scope.lift = function($index) {
                        var modalInstance = $uibModal.open({
                            animation: true,
                            ariaLabelledBy: 'modal-title',
                            ariaDescribedBy: 'modal-body',
                            templateUrl: 'scripts/directives/player/user-restrictions/lift-user-restriction.html',
                            controller: 'LiftUserRestrictionModal',
                            controllerAs: 'controller',
                            backdrop: 'static',
                            size: 'md',
                            resolve: {
                                user: function() {
                                    return $scope.user;
                                },
                                restriction: function () {
                                  return $scope.userRestrictions[$index];
                                },
                                loadMyFiles: function($ocLazyLoad) {
                                    return $ocLazyLoad.load({
                                        name:'lithium',
                                        files: [ 'scripts/directives/player/user-restrictions/lift-user-restriction.js' ]
                                    })
                                }
                            }
                        });

                        modalInstance.result.then(function (result) {
                            $scope.userRestrictions.splice($index, 1);
                        })
                    }
                }
            ]
        }
    });



