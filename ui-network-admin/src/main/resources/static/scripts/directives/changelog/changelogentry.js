'use strict';

angular.module('lithium').directive('changelogentry', function() {
    return {
        templateUrl: '/scripts/directives/changelog/changelogentry.html',
        scope: {
            entry: '=',
            backhref: '@'
        },
        restrict: 'E',
        replace: true,
        controllerAs: 'controller',
        controller: ['$scope', 'notify', '$state', '$uibModal', '$translate', 'UserRest',
            function ($scope, notify, $state, $uibModal, $translate, userRest) {
                var controller = this;
                controller.entry = $scope.entry;
                controller.backhref = $scope.backhref;

                if(angular.isUndefinedOrNull(controller.entry.authorFullName)) {
                    const entry = controller.entry;
                    userRest.findFromGuid(entry.domain.name, entry.authorUser.guid).then(response => {
                        const data = response.plain()
                        if(!angular.isUndefinedOrNull(data) && !Array.isArray(data)) {
                            controller.entry.authorFullName = `${data.firstName} ${data.lastName}`
                        }
                    })
                }
                controller.setPriority = function () {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/changelog/setpriority.html',
                        controller: 'SetChangelogPriorityModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            entry: function () {
                                return controller.entry;
                            },
                            loadMyFiles: function ($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name: 'lithium',
                                    files: ['scripts/directives/changelog/setpriority.js']
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (responce) {
                        $state.reload();
                    });

                }

                controller.priorityMap =
                    {
                        low: { from: null, to: 33, name: $translate.instant('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.LOW') },
                        medium: { from: 34, to: 66, name: $translate.instant('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.MEDIUM') },
                        high: { from: 67, to: null, name: $translate.instant('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.HIGH') }
                    };

                controller.getPriority = function(priority) {
                    if (priority <= 33) return controller.priorityMap.low.name
                    else if (priority > 33 && priority <= 66) return controller.priorityMap.medium.name
                    else if (priority > 66) return controller.priorityMap.high.name;
                }

            }
        ]
    }
});
