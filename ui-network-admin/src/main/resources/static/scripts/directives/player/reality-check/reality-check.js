'use strict';

angular.module('lithium').directive('realitycheck', function() {
    return {
        templateUrl:'scripts/directives/player/reality-check/reality-check.html',
        scope: {
            realityCheck: "=",
            user: "=ngModel",
            data: "="
        },
        restrict: 'E',
        replace: true,
        controller: ['$q', '$uibModal', '$scope', 'notify', 'errors', 'bsLoadingOverlayService', 'RealityCheckRest',
            function($q, $uibModal, $scope, notify, errors, bsLoadingOverlayService, realityCheckRest) {
                $scope.init = function() {
                    realityCheckRest.get($scope.user.guid, $scope.user.domain.name).then(function(response) {
                        console.log(response);
                        var r = response;
                        if (r.length !== undefined && r.length === 0) {
                            r = undefined;
                        }
                        $scope.realityCheck = r;
                    }).catch(function() {
                        errors.catch('', false);
                    });
                }

                $scope.timer = "none";

                $scope.setRealityCheckTime = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        backdrop: 'static',
                        templateUrl: 'scripts/directives/player/reality-check/reality-check-player.html',
                        controller: 'RealityCheckPlayerModal',
                        controllerAs: 'controller',
                        size: 'md',
                        resolve: {
                            user: function() {
                                return $scope.user;
                            },
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/reality-check/reality-check-player.js' ]
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        $scope.realityCheck = result;
                        $scope.timer = msToTime($scope.realityCheck.timerTime);
                    });
                }

                function msToTime(duration) {
                    var milliseconds = parseInt((duration % 1000) / 100),
                        seconds = Math.floor((duration / 1000) % 60),
                        minutes = Math.floor((duration / (1000 * 60)) % 60),
                        hours = Math.floor((duration / (1000 * 60 * 60)) % 24);

                    hours = (hours < 10) ? "0" + hours : hours;
                    minutes = (minutes < 10) ? "0" + minutes : minutes;
                    seconds = (seconds < 10) ? "0" + seconds : seconds;

                    return hours + ":" + minutes;
                }

                if ($scope.data && $scope.data.init === true) {
                    $scope.init();
                }

                if($scope.realityCheck !== undefined && $scope.realityCheck !== null) {
                    $scope.timer = msToTime($scope.realityCheck.timerTime);
                } else {
                    $scope.timer = "none";
                }
            }
        ]
    }
});
