'use strict'

angular.module('lithium').controller('SettlementsListController', ['batchSettlement', '$log', '$translate', '$dt', 'DTOptionsBuilder', '$state', 'notify', 'BatchSettlementsRest', '$http',
    function(batchSettlement, $log, $translate, $dt, DTOptionsBuilder, $state, notify, batchSettlementsRest, $http) {
        var controller = this;
        controller.batchSettlement = batchSettlement;

        var baseUrl = 'services/service-settlement/settlements/' + batchSettlement.id + '/settlementsTable';
        var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [0, 'desc']);
        controller.table = $dt.builder()
            .column($dt.columnformatdatetime('dateStart').withTitle($translate("UI_NETWORK_ADMIN.SERVICE_SETTLEMENT.SETTLEMENTS.LIST.DATESTART")))
            .column($dt.columnformatdatetime('dateEnd').withTitle($translate("UI_NETWORK_ADMIN.SERVICE_SETTLEMENT.SETTLEMENTS.LIST.DATEEND")))
            .column($dt.columnformatdatetime('createdDate').withTitle($translate("UI_NETWORK_ADMIN.SERVICE_SETTLEMENT.SETTLEMENTS.LIST.CREATEDDATE")))
            .column($dt.column('createdBy').withTitle($translate("UI_NETWORK_ADMIN.SERVICE_SETTLEMENT.SETTLEMENTS.LIST.CREATEDBY")))
            .column($dt.column('total').withTitle($translate("UI_NETWORK_ADMIN.SERVICE_SETTLEMENT.SETTLEMENTS.LIST.TOTAL")))
            .column($dt.column('externalUser.username').withTitle($translate("UI_NETWORK_ADMIN.SERVICE_SETTLEMENT.SETTLEMENTS.LIST.USER")))
            .column(
                $dt.linkscolumn(
                    "",
                    [
                        {
                            permission: "settlements_*",
                            permissionType: "any",
                            permissionDomain: function (data) {
                                return data.domain.name;
                            },
                            title: "GLOBAL.ACTION.OPEN",
                            href: function (data) {
                                return $state.href("^.settlement", {batchSettlementId: batchSettlement.id, settlementId: data.id});
                            }
                        }
                    ]
                )
            )
            .options({
                url: baseUrl, type: 'GET', data: function (d) {
                }
            }, null, dtOptions, null)
            .build();

        controller.refresh = function() {
            batchSettlementsRest.get(batchSettlement.id).then(function(response) {
                controller.batchSettlement = response.plain();
            });
            controller.table.instance.reloadData(function(){}, false);
        }

        controller.finalizeBatchSettlements = function() {
            batchSettlementsRest.finalizeBatchSettlements(batchSettlement.id).then(function(response) {
                if (response._status === 0) {
                    notify.success("UI_NETWORK_ADMIN.SERVICE_SETTLEMENT.SETTLEMENTS.LIST.FINALIZE.SUCCESS");
                } else {
                    notify.warning(response._message);
                }
            }).catch(function(error) {
                notify.error("Could not initiate batch settlements finalization");
                errors.catch("", false)(error)
            });
        }

        controller.exportToXls = function() {
            console.log("Downloading XLS");
            $http.get('services/service-settlement/batch/settlements/' + controller.batchSettlement.id + '/export/xls', { responseType: 'blob' }).then(function onSuccess(response) {
                var blob = response.data;
                var url = URL.createObjectURL(blob);
                var a = document.createElement('a');
                document.body.appendChild(a);
                a.setAttribute('style', 'display: none');
                a.href = url;
                a.download = "batch-settlements-" + batchSettlement.id + ".xlsx";
                a.click();
                window.URL.revokeObjectURL(url);
                a.remove();
            });
        }

        controller.exportToNets = function() {
            console.log("Downloading Nets");
            $http.get('services/service-settlement/batch/settlements/' + controller.batchSettlement.id + '/export/nets', { responseType: 'blob' }).then(function onSuccess(response) {
                var blob = response.data;
                var url = URL.createObjectURL(blob);
                var a = document.createElement('a');
                document.body.appendChild(a);
                a.setAttribute('style', 'display: none');
                a.href = url;
                a.download = "batch-settlements-" + batchSettlement.id + ".xml";
                a.click();
                window.URL.revokeObjectURL(url);
                a.remove();
            });
        }
    }
]);
