'use strict'

angular.module('lithium').controller('SettlementsBatchListController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$state',
    function($log, $translate, $dt, DTOptionsBuilder, $state) {
        var controller = this;

        var baseUrl = 'services/service-settlement/batch/settlements/table';
        var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [0, 'desc']);
        controller.table = $dt.builder()
        .column($dt.column('id').withTitle($translate("UI_NETWORK_ADMIN.SERVICE_SETTLEMENT.BATCH.LIST.ID")))
        .column($dt.column('domain.name').withTitle("Domain"))
        .column($dt.column('name').withTitle($translate("UI_NETWORK_ADMIN.SERVICE_SETTLEMENT.BATCH.LIST.NAME")))
        .column($dt.columnformatdatetime('createdDate').withTitle($translate("UI_NETWORK_ADMIN.SERVICE_SETTLEMENT.BATCH.LIST.CREATEDDATE")))
        .column($dt.column('open').withTitle($translate("UI_NETWORK_ADMIN.SERVICE_SETTLEMENT.BATCH.LIST.OPEN")))
        .column(
            $dt.linkscolumn(
                "",
                [
                    {
                        permission: "settlements_*",
                        permissionType: "any",
                        permissionDomain: function(data) {
                            return data.domain.name;
                        },
                        title: "GLOBAL.ACTION.OPEN",
                        href: function(data) {
                            return $state.href("^.batch", { batchSettlementId:data.id });
                        }
                    }
                ]
            )
        )
        .options({ url: baseUrl, type: 'GET', data: function(d) { } }, null, dtOptions, null)
        .build();
    }
]);
