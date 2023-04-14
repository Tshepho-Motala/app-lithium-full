'use strict'

angular.module('lithium').controller('GoGameWinDistributionsListController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$uibModal', '$state', '$scope',
    function($log, $translate, $dt, DTOptionsBuilder, $uibModal, $state, $scope) {
        var controller = this;

        var baseUrl = 'services/service-casino-provider-gogame/admin/windistributions/table';

        var dtOptions = null;//DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[2, 'desc']]);
        controller.gogameWinDistributionsTable = $dt.builder()
            .column($dt.column('id').withTitle('ID'))
            .column($dt.columnformatdatetime('created').withTitle('Created'))
            .column($dt.column('processing').withTitle('Processing'))
            .column($dt.columnformatdatetime('processingStarted').withTitle('Processing Started'))
            .column($dt.columnformatdatetime('processingCompleted').withTitle('Processing Completed'))
            .column($dt.column('ledger.name').withTitle('Ledger'))
            .column($dt.linkscolumn(
                "",
                [
                    {
                        permission: "gogamegames_windistributions_*",
                        permissionType: "any",
                        title: "GLOBAL.ACTION.OPEN",
                        href: function(data) {
                            return $state.href("dashboard.gogamegames.windistributions.windistribution", { id:data.id });
                        }
                    }
                ]
            ))
            .options({ url: baseUrl, type: 'GET' }, null, dtOptions, null)
            .build();

        controller.refresh = function() {
            controller.gogameWinDistributionsTable.instance.reloadData(function(){}, false);
        }
    }
]);
