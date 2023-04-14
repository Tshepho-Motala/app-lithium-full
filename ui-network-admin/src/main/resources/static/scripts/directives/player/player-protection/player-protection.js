'use strict';

angular.module('lithium').directive('playerprotection', function() {
    return {
        templateUrl: 'scripts/directives/player/player-protection/player-protection.html',
        scope: {
            user: "=ngModel",
            domain: "=",
        },
        restrict: 'E',
        replace: true,
        controller: ['$q', '$uibModal', '$scope', 'notify', 'errors', 'bsLoadingOverlayService', 'userThresholdRest', '$stateParams', '$dt', 'DTOptionsBuilder', '$translate', '$filter',
            function ($q, $uibModal, $scope, notify, errors, bsLoadingOverlayService, userThresholdRest, $stateParams, $dt, DTOptionsBuilder, $translate, $filter) {
                var controller = this;
                $scope.domainName = $stateParams.domainName;
                $scope.notification = undefined;
                let userGuid = $scope.user.guid;
                $scope.referenceId = 'player-protection-overlay';
                bsLoadingOverlayService.start({referenceId: $scope.referenceId});

                $scope.$watch(function() { return $scope.user.lossLimitVisibility }, function(newValue, oldValue) {
                    if (newValue != oldValue) {
                        $scope.lossLimitVisibility = $scope.user.lossLimitVisibility;
                    }
                }, true);

                var baseUrl = "services/service-user-threshold/backoffice/threshold/warnings/"+$scope.domainName+"/v1/find";
                var dtOptions = DTOptionsBuilder.newOptions()
                .withOption('stateSave', false)
                .withOption('order', [[6, 'desc']])
                .withOption('paging', false)
                .withOption('changeLength', false)
                .withOption('searching', false);

                $scope.thresholdWarningsTable = $dt.builder()
                .column($dt.column('id').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_LABELS.PLAYER_PROTECTION.EVENT")).notVisible())
                .column($dt.column('thresholdRevision.threshold.type.name').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_LABELS.PLAYER_PROTECTION.EVENT")).renderWith(function (data, type, row, meta) {
                    $translate("UI_NETWORK_ADMIN.THRESHOLDS.EVENT."+row.thresholdRevision.threshold.type.name).then(function success(t) {
                        $(meta.settings.aoData[meta.row].anCells[meta.col]).html(t);
                    });
                    return "..";
                }))
                .column($dt.columnperiod('thresholdRevision.threshold.granularity').withTitle($translate("Type")))
                .column($dt.column('id').withTitle($translate("Amount")).renderWith(function (data, type, row, meta) {
                    if (row.thresholdRevision.threshold.type.name == "TYPE_LOSS_LIMIT") {
                        switch (row.thresholdRevision.threshold.granularity) {
                            case '3':
                                return $filter('currency')(row.dailyLimitUsed,
                                  row.defaultDomainCurrencySymbol, 2);
                            case '4':
                                return $filter('currency')(row.weeklyLimitUsed,
                                  row.defaultDomainCurrencySymbol, 2);
                            case '2':
                                return $filter('currency')(row.monthlyLimitUsed,
                                  row.defaultDomainCurrencySymbol, 2);
                            case '1':
                                return $filter('currency')(row.annualLimitUsed,
                                  row.defaultDomainCurrencySymbol, 2);
                            default:
                                return row.amount;
                        }
                    } else {
                        return $filter('currency')(row.amount,
                          row.defaultDomainCurrencySymbol, 2);
                    }
                }))
                .column($dt.column('id').withTitle($translate("Threshold")).renderWith(function (data, type, row, meta) {
                    if (row.thresholdRevision.percentage) {
                        return row.thresholdRevision.percentage + "%";
                    }
                    return $filter('currency')(row.thresholdRevision.amount,
                      row.defaultDomainCurrencySymbol, 2);
                }))
                .column($dt.column('id').withTitle($translate("Loss Limit")).renderWith(function (data, type, row, meta) {
                    switch (row.thresholdRevision.threshold.granularity) {
                        case '3':
                            return $filter('currency')(row.dailyLimit, row.defaultDomainCurrencySymbol, 2);
                        case '4':
                            return $filter('currency')(row.weeklyLimit, row.defaultDomainCurrencySymbol, 2);
                        case '2':
                            return $filter('currency')(row.monthlyLimit, row.defaultDomainCurrencySymbol, 2);
                        case '1':
                            return $filter('currency')(row.annualLimit, row.defaultDomainCurrencySymbol, 2);
                        default:
                            return "";
                    }
                }))
                .column($dt.columnformatdatetime('thresholdHitDate').withTitle($translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_LABELS.PLAYER_PROTECTION.DATE")))
                .options({
                    url: baseUrl,
                    type: 'POST',
                    data: function(d, e) {
                        // let nd = {};
                        // nd.tableRequest = d;
                        d.length = 100;
                        d.playerGuid = userGuid;
                        d.domainNames = $scope.domainName;
                        d.typeName = "TYPE_LOSS_LIMIT,TYPE_DEPOSIT_LIMIT";
                    }
                  },
                  null,
                  dtOptions,
                  null
                )
                .build();

                $scope.refresh = function() {
                    $scope.getNotifications();
                    $scope.thresholdWarningsTable.instance.rerender(true)
                }

                $scope.getNotifications = function () {
                    userThresholdRest.getNotifications($scope.domainName,
                      userGuid).then(function(response) {
                        if (response === undefined) {
                            $scope.notification = false;
                        } else {
                            $scope.notification = response;
                        }
                    }).catch(function() {
                        errors.catch('', false);
                    });
                }
                $scope.getNotifications();
                bsLoadingOverlayService.stop({referenceId: $scope.referenceId});

                $scope.activateNotification = function () {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/player-protection/player-notification.html',
                        controller: 'PlayerNotificationModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            user: function () {
                                return $scope.user;
                            },
                            notification: function () {
                                return $scope.notification;
                            },
                            loadMyFiles: function ($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name: 'lithium',
                                    files: ['scripts/directives/player/player-protection/player-notification.js']
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        $scope.notification = result;
                    });
                }
            }
        ]
    }
});
