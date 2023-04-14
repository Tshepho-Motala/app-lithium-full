'use strict';

angular.module('lithium')
.controller('GameSuppliersListController', ['domainName', '$scope', '$stateParams', '$state', '$translate', 'errors', 'notify', '$dt', 'DTOptionsBuilder',
function(domainName, $scope, $stateParams, $state, $translate, errors, notify, $dt, DTOptionsBuilder) {
    var controller = this;
    controller.domainName = domainName;

    var baseUrl = 'services/service-games/backoffice/' + controller.domainName + '/game-suppliers/table?1=1';
    var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[0, 'asc']]);
    controller.table = $dt.builder()
        .column($dt.column('name').withTitle($translate('UI_NETWORK_ADMIN.GAME-SUPPLIERS.FIELDS.NAME.LABEL')))
        .column(
            $dt.linkscolumn(
                '',
                [
                    {
                        permission: 'game_supplier_view',
                        permissionType: 'any',
                        permissionDomain: function(data) {
                            return data.domain.name;
                        },
                        title: 'GLOBAL.ACTION.OPEN',
                        href: function(data) {
                            return $state.href('dashboard.casino.game-suppliers.view', { domainName:data.domain.name, id:data.id });
                        }
                    }
                ]
            )
        )
        .options(
            {
                url: baseUrl,
                type: 'GET',
                data: function(d) {
                }
            },
            null,
            dtOptions,
            null
        )
        .build();

    controller.refresh = function() {
        controller.table.instance.reloadData(function(){}, false);
    }
}]);