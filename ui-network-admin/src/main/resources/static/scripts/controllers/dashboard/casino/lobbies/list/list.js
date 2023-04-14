'use strict';

angular.module('lithium')
.controller('CasinoLobbiesListController', ['domainName', '$scope', '$stateParams', '$state', '$translate', 'errors', 'notify', '$dt', 'DTOptionsBuilder', 'CasinoCMSRest',
function(domainName, $scope, $stateParams, $state, $translate, errors, notify, $dt, DTOptionsBuilder, rest) {
    var controller = this;
    controller.domainName = domainName;

    var baseUrl = 'services/service-casino-cms/backoffice/' + controller.domainName + '/lobbies/table?1=1';
    var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[0, 'desc']]);
    controller.table = $dt.builder()
        .column($dt.column('id').withTitle('ID'))
        .column(
            $dt.linkscolumn(
                '',
                [
                    {
                        permission: 'casino_lobbies_view',
                        permissionType: 'any',
                        permissionDomain: function(data) {
                            return data.domain.name;
                        },
                        title: 'GLOBAL.ACTION.OPEN',
                        href: function(data) {
                            return $state.href('dashboard.casino.lobbies.lobby.view', { domainName:data.domain.name, lobbyId:data.id, lobbyRevisionId: data.current.id });
                        }
                    }
                ]
            )
        )
        .column($dt.column('current.description').withTitle($translate('UI_NETWORK_ADMIN.CASINO.LOBBIES.FIELDS.DESCRIPTION')))
        .column($dt.columnformatdatetime('current.createdDate').withTitle($translate('UI_NETWORK_ADMIN.CASINO.LOBBIES.FIELDS.CREATED_DATE')))
        .column($dt.column('current.createdBy.fullName').withTitle($translate('UI_NETWORK_ADMIN.CASINO.LOBBIES.FIELDS.CREATED_BY')))
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

    controller.add = function() {
        rest.lobbyExists(controller.domainName)
            .then(function (lobbyExists) {
                if (!lobbyExists) {
                    $state.go("^.add", {domainName: controller.domainName});
                } else {
                    notify.warning('UI_NETWORK_ADMIN.CASINO.LOBBIES.ADD.LOBBY_EXISTS')
                }
            }).catch(
                errors.catch('UI_NETWORK_ADMIN.CASINO.LOBBIES.ADD.LOBBY_EXISTS_CHECK_FAIL', false)
            );
    }
}]);