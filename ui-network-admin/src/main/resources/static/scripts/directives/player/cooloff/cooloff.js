'use strict';

angular.module('lithium').directive('cooloff', function() {
    return {
        templateUrl:'scripts/directives/player/cooloff/cooloff.html',
        scope: {
            coolOff: "=",
            user: "=ngModel",
            data: "="
        },
        restrict: 'E',
        replace: true,
        controller: ['$q', '$uibModal', '$scope', 'notify', 'errors', 'bsLoadingOverlayService', 'CoolOffRest',
            function($q, $uibModal, $scope, notify, errors, bsLoadingOverlayService, coolOffRest) {

                $scope.init = function() {
                    coolOffRest.lookup($scope.user.guid, $scope.user.domain.name).then(function(response) {
                        var r = response;
                        if (r.length !== undefined && r.length === 0) {
                            r = undefined;
                        } else if(r.length !== undefined && r.data === null){
                            r = undefined;
                        }
                        $scope.coolOff = r;
                    }).catch(function() {
                        errors.catch('', false);
                    });
                }



                $scope.cooloffPlayer = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/cooloff/cooloff-player.html',
                        controller: 'CooloffPlayerModal',
                        controllerAs: 'controller',
                        size: 'md',
                        resolve: {
                            user: function() {
                                return $scope.user;
                            },
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/cooloff/cooloff-player.js' ]
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        $scope.coolOff = result;
                    });
                }

                $scope.clearPlayerCooloff = function() {
                    coolOffRest.clear($scope.user.guid, $scope.user.domain.name).then(function(response) {
                        if (response.data === true || response === true ) {
                            $scope.coolOff = undefined;
                            notify.success('UI_NETWORK_ADMIN.PLAYER.COOLOFF.CLEAR.SUCCESS');
                        } else {
                            notify.error('UI_NETWORK_ADMIN.PLAYER.COOLOFF.CLEAR.ERROR');
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
