'use strict';

angular.module('lithium').directive('cruks', function() {
    return {
        templateUrl:'scripts/directives/player/cruks/cruks.html',
        scope: {
            coolOff: "=",
            user: "=ngModel",
            data: "="
        },
        restrict: 'E',
        replace: true,
        controller: ['$uibModal', '$scope',
            function($uibModal, $scope) {

                let controller = this;
                controller.onInit = function(user) {
                    controller.user = user;
                }
                controller.onInit($scope.user);

                $scope.editCruks = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/cruks/cruks-edit.html',
                        controller: 'CruksEditModal',
                        controllerAs: 'controller',
                        size: 'md',
                        backdrop: 'static',
                        resolve: {
                            user: function() {
                                return controller.user;
                            },
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/cruks/cruks-edit.js' ]
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (user) {
                        controller.onInit(user);
                    });
                }

            }
        ]
    }
});
