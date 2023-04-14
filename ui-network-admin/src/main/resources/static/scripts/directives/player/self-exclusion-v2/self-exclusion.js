'use strict';

angular.module('lithium')
    .directive('selfexclusionv2', function() {
        return {
            templateUrl:'scripts/directives/player/self-exclusion-v2/self-exclusion.html',
            scope: {
                data: "=",
                user: "=ngModel",
                exclusion: "="
            },
            restrict: 'E',
            replace: true,
            controller: ['$q', '$uibModal', '$scope', 'notify', 'errors', 'bsLoadingOverlayService', 'ExclusionRest',
                function($q, $uibModal, $scope, notify, errors, bsLoadingOverlayService, exclusionRest) {
                    $scope.init = function() {
                        exclusionRest.lookup($scope.user.guid, $scope.user.domain.name).then(function(response) {
                            console.log(response);
                            var r = response;
                            if (r.length !== undefined && r.length === 0) {
                                r = null;
                            }
                            $scope.exclusion = r;
                        }).catch(function() {
                            errors.catch('', false);
                        });
                    }

                    $scope.excludePlayer = function() {
                        var modalInstance = $uibModal.open({
                            animation: true,
                            ariaLabelledBy: 'modal-title',
                            ariaDescribedBy: 'modal-body',
                            templateUrl: 'scripts/directives/player/self-exclusion-v2/self-exclude-player.html',
                            controller: 'SelfExcludePlayerModal',
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
                                        files: [ 'scripts/directives/player/self-exclusion-v2/self-exclude-player.js' ]
                                    })
                                }
                            }
                        });

                        modalInstance.result.then(function (result) {
                            $scope.exclusion =  result;
                        });
                    }

                    $scope.clearSelfExclusion = function() {
                        exclusionRest.clear($scope.user.guid, $scope.user.domain.name).then(function(response) {
                            if (response.data === true || response === true) {
                                $scope.exclusion = undefined;
                                notify.success('UI_NETWORK_ADMIN.PLAYER.EXCLUSION.CLEAR.SUCCESS');
                            } else {
                                notify.error('UI_NETWORK_ADMIN.PLAYER.EXCLUSION.CLEAR.ERROR');
                            }
                        }).catch(function() {
                            errors.catch('', false);
                        });
                    }
                    if ($scope.data && $scope.data.init === true)
                      $scope.init();
                }
            ]
        }
    });
