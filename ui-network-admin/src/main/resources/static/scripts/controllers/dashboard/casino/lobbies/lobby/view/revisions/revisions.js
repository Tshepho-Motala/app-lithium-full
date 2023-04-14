'use strict';

angular.module('lithium')
.controller('CasinoLobbyRevisionsController', ['lobby', '$scope', '$stateParams', '$state', '$translate', 'errors', 'notify', '$dt', 'DTOptionsBuilder',
	function(lobby, $scope, $stateParams, $state, $translate, errors, notify, $dt, DTOptionsBuilder) {
		var controller = this;
		$scope.setDescription('UI_NETWORK_ADMIN.CASINO.LOBBY.TAB.REVISIONS');

		controller.lobby = lobby;

		var baseUrl = 'services/service-casino-cms/backoffice/' + lobby.domain.name + '/lobby/' + lobby.id + '/revisions?1=1';
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[0, 'desc']]);
		controller.table = $dt.builder()
			.column($dt.column('id').withTitle($translate('UI_NETWORK_ADMIN.CASINO.LOBBIES.FIELDS.ID')))
			.column(
				$dt.linkscolumn(
					'',
					[
						{
							permission: 'casino_lobbies_view',
							permissionType: 'any',
							permissionDomain: controller.domainName,
							title: 'GLOBAL.ACTION.OPEN',
							href: function(data) {
								return $state.href('dashboard.casino.lobbies.lobby.view', { domainName:lobby.domain.name, lobbyId:lobby.id, lobbyRevisionId: data.id });
							}
						}
					]
				)
			)
			.column($dt.column('description').withTitle($translate('UI_NETWORK_ADMIN.CASINO.LOBBIES.FIELDS.DESCRIPTION')))
			.column($dt.columnformatdatetime('createdDate').withTitle($translate('UI_NETWORK_ADMIN.CASINO.LOBBIES.FIELDS.CREATED_DATE')))
			.column($dt.column('createdBy.fullName').withTitle($translate('UI_NETWORK_ADMIN.CASINO.LOBBIES.FIELDS.CREATED_BY')))//accessing fullname
			.column($dt.columnformatdatetime('modifiedDate').withTitle($translate('UI_NETWORK_ADMIN.CASINO.LOBBIES.FIELDS.MODIFIED_DATE')))
			.column($dt.column('modifiedBy.fullName').withTitle($translate('UI_NETWORK_ADMIN.CASINO.LOBBIES.FIELDS.MODIFIED_BY')))//accessing fullname.
			.column(
				$dt.labelcolumn(
					'',
					[{lclass: function(data) {
							if (data.id === lobby.current.id) return 'success';
							if (lobby.edit && data.id === lobby.edit.id) return 'primary';
							return '';
						},
						text: function(data) {
							if (data.id === lobby.current.id) return 'CURRENT'
							if (lobby.edit && data.id === lobby.edit.id) return 'EDIT';
							return '';
						},
						uppercase:true
					}]
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
	}
]);
