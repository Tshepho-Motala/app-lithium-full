'use strict';

angular.module('lithium')
    .controller('ProgressiveJackpotFeedsController', ['domainName', 'rest-provider', 'GameSuppliersRest', '$scope', '$stateParams', '$state', '$translate', 'errors', 'notify', '$dt', 'DTOptionsBuilder','rest-progressive-feeds',"$uibModal","$compile",
        function(domainName, $providerRest, $gameSupplierRest, $scope, $stateParams, $state, $translate, errors, notify, $dt, DTOptionsBuilder, restRegisteredProgressiveFeeds, $uibModal, $compile) {
            var controller = this;
            controller.domainName = domainName;

            var filterApplied = true;
            controller.legendCollapsed = true;
            controller.model = {};
            controller.enabled = false;
            //controller.model.isEnabled = 0;


            function arrayAsString(arr, fieldName) {
                var str = "";
                angular.forEach(arr, function (d) {
                    if(str != "")
                        str += "," + d[fieldName];
                    else
                        str += d[fieldName];
                });
                return str;
            }

            controller.resolveChoice = function(choice) {
                if (choice === undefined || choice === null) return null; // Both
                switch (choice) {
                    case 0:
                        return null; // Both
                    case 1:
                        return true; // Auto approved only
                    case 2:
                        return false; // Not auto approved only
                }
            }

            controller.updateEnableStatus = function (id, currentValue) {
                controller.updateRegisteredProgressiveFeeds(id, currentValue, "enabled");
            }
            controller.updateRegisteredProgressiveFeeds = function (id, currentValue, flagType) {
                var modalInstance = $uibModal.open({
                    animation: true,
                    ariaLabelledBy: 'modal-title',
                    ariaDescribedBy: 'modal-body',
                    templateUrl: 'scripts/controllers/dashboard/casino/progressive-balances/changeflag/confirmchangeflag.html',
                    controller: 'ConfirmEnableChangeFlagModal',
                    controllerAs: 'controller',
                    backdrop: 'static',
                    size: 'md',
                    resolve: {
                        entityId: function () {
                            return id;
                        },
                        flagType: function () {
                            return flagType;
                        },
                        flagValue: function () {
                            return currentValue;
                        },
                        restService: function () {
                            return restRegisteredProgressiveFeeds;
                        },
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'lithium',
                                files: ['scripts/controllers/dashboard/casino/progressive-balances/changeflag/confirmchangeflag.js']
                            })
                        }
                    }
                });

                modalInstance.result.then(function(response) {
                    controller.tableLoad();
                });
            }
            var baseUrl = 'services/service-games/backoffice/jackpot-feeds/progressive/' + controller.domainName + '/registered-feeds/table?1=1';
            var dtOptions = DTOptionsBuilder.newOptions().withOption('createdRow', function(row, data, dataIndex) {
                $compile(angular.element(row).contents())($scope);}).withOption('stateSave', false).withOption('order', [[0, 'desc']]);
            controller.table = $dt.builder()
                .column($dt.column('id')
                    .withTitle($translate('UI_NETWORK_ADMIN.CASINO.PROGRESSIVE_BALANCES.REGISTERED_FIELDS.ID')))
                .column($dt.column('registeredOn')
                    .withTitle($translate('UI_NETWORK_ADMIN.CASINO.PROGRESSIVE_BALANCES.REGISTERED_FIELDS.REGISTERED_ON')))
                .column($dt.column('lastUpdatedOn')
                    .withTitle($translate('UI_NETWORK_ADMIN.CASINO.PROGRESSIVE_BALANCES.REGISTERED_FIELDS.LAST_UPDATED_ON')))
                .column($dt.column('module.name')
                    .withTitle($translate('UI_NETWORK_ADMIN.CASINO.PROGRESSIVE_BALANCES.REGISTERED_FIELDS.MODULE_NAME')).notSortable())
                .column($dt.column('gameSupplier.name')
                    .withTitle($translate('UI_NETWORK_ADMIN.CASINO.PROGRESSIVE_BALANCES.REGISTERED_FIELDS.GAME_SUPPLIER_NAME')).notSortable())
                .column($dt.column(function (data) {
                    if (data.enabled) {
                        return '<label class="switch"><input type="checkbox" checked="checked" lit-if-domains-permission="JACKPOT_BALANCE_VIEW" ng-click="controller.updateEnableStatus(' + data.id + ', ' + data.enabled + ')"><span class="slider"></span></label>';
                    } else {
                        return '<label class="switch"><input type="checkbox" lit-if-domains-permission="JACKPOT_BALANCE_VIEW" ng-click="controller.updateEnableStatus(' + data.id + ', ' + data.enabled + ')"><span class="slider"></span></label>';
                    }

                }).withTitle($translate("UI_NETWORK_ADMIN.CASINO.PROGRESSIVE_BALANCES.REGISTERED_FIELDS.ENABLED")))
                .options(
                    {
                        url: baseUrl,
                        type: 'POST',
                        data: function (d) {
                            d.start = 0;
                            d.length = 1000;
                            d.enabled = controller.enabled;

                        }
                    },
                    null,
                    dtOptions,
                    null
                )
                .build();
        controller.progressiveBalancesSearch = function (search) {

        }

        controller.tableLoad = function () {
            controller.table.instance.rerender(true);
        }

        controller.refresh = function() {
            controller.table.instance.reloadData(function(){}, false);
        }
        }]);
