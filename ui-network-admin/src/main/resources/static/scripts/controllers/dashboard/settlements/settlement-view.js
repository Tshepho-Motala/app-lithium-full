'use strict'

angular.module('lithium').controller('SettlementViewController', ['settlement', '$translate', 'notify', '$state', '$stateParams', 'SettlementsRest', '$dt', '$http',
    function(settlement, $translate, notify, $state, $stateParams, settlementsRest, $dt, $http) {
        var controller = this;
        controller.settlement = settlement;

        var baseUrl = 'services/service-settlement/settlement/' + settlement.id + '/table';
        var dtOptions = null; //DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [0, 'desc']);
        controller.table = $dt.builder()
        .column($dt.columnformatdatetime('dateStart').withTitle($translate("UI_NETWORK_ADMIN.SERVICE_SETTLEMENT.SETTLEMENTS.VIEW.LIST.DATESTART")))
        .column($dt.columnformatdatetime('dateEnd').withTitle($translate("UI_NETWORK_ADMIN.SERVICE_SETTLEMENT.SETTLEMENTS.VIEW.LIST.DATEEND")))
        .column($dt.columnformatdatetime('description').withTitle($translate("UI_NETWORK_ADMIN.SERVICE_SETTLEMENT.SETTLEMENTS.VIEW.LIST.DESCRIPTION")))
        .column($dt.column('amount').withTitle($translate("UI_NETWORK_ADMIN.SERVICE_SETTLEMENT.SETTLEMENTS.VIEW.LIST.AMOUNT")))
        .options({url: baseUrl, type: 'GET', data: function (d) {} }, null, dtOptions, null)
        .build();

        controller.back = function() {
            $state.go("^.batch", { batchSettlementId: $stateParams.batchSettlementId });
        }

        controller.refresh = function() {
            settlementsRest.findSettlementById(settlement.id).then(function(response) {
                controller.settlement = response.plain();
            });
            controller.table.instance.reloadData(function(){}, false);
        }

        controller.finalize = function() {
            settlementsRest.finalize(settlement.id).then(function(response) {
                if (response._status === 0) {
                    notify.success("UI_NETWORK_ADMIN.SERVICE_SETTLEMENT.SETTLEMENTS.VIEW.FINALIZE.SUCCESS");
                } else {
                    notify.warning(response._message);
                }
            }).catch(function(error) {
                notify.error("Could not finalize the settlement");
                errors.catch("", false)(error)
            });
        }

        controller.previewPdfStatement = function() {
            $http.get("services/service-settlement/settlement/" + settlement.id + "/pdf/preview", { responseType: 'blob' }).then(function onSuccess(response) {
                var blob = response.data;
                var fileURL = URL.createObjectURL(blob);
                window.open(fileURL);
            });
        }

        controller.downloadPdfStatement = function() {
            $http.get("services/service-settlement/settlement/" + settlement.id + "/pdf/download", { responseType: 'blob' }).then(function onSuccess(response) {
                var blob = response.data;
                var url = URL.createObjectURL(blob);
                const a = document.createElement('a');
                document.body.appendChild(a);
                a.setAttribute('style', 'display: none');
                a.href = url;
                const name = controller.settlement.externalUser.firstName + ' ' + controller.settlement.externalUser.lastName;
                a.download =  name + '-settlement-' + controller.settlement.dateStart + '-' + controller.settlement.dateEnd + '.pdf';
                a.click();
                window.URL.revokeObjectURL(url);
                a.remove();
            });
        }

        controller.resendPdfStatement = function() {
            settlementsRest.resendPdfStatement(settlement.id).then(function(response) {
                if (response._status === 0) {
                    notify.success("UI_NETWORK_ADMIN.SERVICE_SETTLEMENT.SETTLEMENTS.VIEW.PDF.RESEND.SUCCESS");
                } else {
                    notify.warning(response._message);
                }
            }).catch(function(error) {
                notify.error("Could not resend the pdf settlement");
                errors.catch("", false)(error)
            });
        }
    }
]);
