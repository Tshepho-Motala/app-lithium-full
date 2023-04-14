'use strict';

angular.module('lithium')
.controller('GameStudioListController', ['domainName', '$scope', '$stateParams', '$state', '$translate', 'errors', 'notify', '$dt', 'DTOptionsBuilder',
function(domainName, $scope, $stateParams, $state, $translate, errors, notify, $dt, DTOptionsBuilder) {
    var controller = this;
    controller.domainName = domainName;

    var baseUrl = 'services/service-games/backoffice/' + controller.domainName + '/game-studio/table?1=1';
    var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[0, 'asc']]);
    controller.table = $dt.builder()
        .column($dt.column('name').withTitle($translate('UI_NETWORK_ADMIN.GAME-STUDIO.FIELDS.NAME.LABEL')))
        .column(
            $dt.linkscolumn(
                '',
                [
                    {
                        permission: 'game_studio_view',
                        permissionType: 'any',
                        permissionDomain: function(data) {
                            return data.domain.name;
                        },
                        title: 'GLOBAL.ACTION.OPEN',
                        href: function(data) {
                            return $state.href('dashboard.casino.game-studio.view', { domainName:data.domain.name, id:data.id });
                        }
                    }
                ]
            )
        )
        .options(
            {
                url: baseUrl,
                type: 'POST',
                data: function(d) {
                    d.domainName = controller.domainName
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
