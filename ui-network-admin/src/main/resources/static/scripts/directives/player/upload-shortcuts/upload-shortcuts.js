'use strict';

angular.module('lithium').directive('uploadShortcuts', function () {
    return {
        templateUrl: '/scripts/directives/player/upload-shortcuts/upload-shortcuts.html',
        scope: {
            data: "=",
            user: "=ngModel",
        },
        restrict: 'E',
        replace: true,
        controllerAs: 'controller',
        controller: ['$scope', '$rootScope', '$translate', 'notify', '$state', 'UserRest', 'errors', '$uibModal', 'bsLoadingOverlayService', 'rest-accounting',
            function ($scope, $rootScope, $translate, notify, $state, userRest, errors, $uibModal, bsLoadingOverlayService, restAcc) {
                var controller = this;

                controller.createAndEditDocument = function (name, functionName, statusName, isExternal) {
                    userRest.createDocument($scope.user.domain.name, name, statusName, functionName, isExternal, $scope.user.id).then(function (document) {
                        if (document) {
                            notify.success("UI_NETWORK_ADMIN.DOCUMENT.ADD.SUCCESS");
                            document.functionName = functionName;
                            document.statusName = statusName;
                            controller.editDocument(document);
                        } else {
                            notify.error("Unable to create document");
                        }
                    });
                }

                controller.editDocument = function (document) {
                    $scope.document = document;
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        backdrop: 'static',
                        templateUrl: 'scripts/controllers/dashboard/players/player/document-old/edit/edit.html',
                        controller: 'documentEdit',
                        controllerAs: 'controller',
                        size: 'md',
                        resolve: {
                            user: function () {
                                return $scope.user;
                            },
                            domainName: function () {
                                return $scope.user.domain.name;
                            },
                            username: function () {
                                return $scope.user.username;
                            },
                            document: function () {
                                return $scope.document;
                            },
                            loadMyFiles: function ($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name: 'lithium',
                                    files: [
                                        'scripts/controllers/dashboard/players/player/document-old/edit/edit.js'
                                    ]
                                })
                            }
                        }
                    });
                }
            }
        ]
    }
});