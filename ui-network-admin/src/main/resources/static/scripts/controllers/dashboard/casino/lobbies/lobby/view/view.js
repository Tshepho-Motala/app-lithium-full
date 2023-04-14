'use strict'

angular.module('lithium').controller('CasinoLobbyViewController', ['lobby', 'lobbyRevision', '$scope',
	function(lobby, lobbyRevision, $scope) {
		var controller = this;
		$scope.setDescription('UI_NETWORK_ADMIN.CASINO.LOBBY.TAB.VIEW');

		controller.lobby = lobby;
		controller.lobbyRevision = lobbyRevision;

		controller.model = lobbyRevision;

		controller.fields = [
			{
				key: 'description',
				type: 'textarea',
				templateOptions: {
					disabled: true,
					cols: 5,
					rows: 5,
					maxlength: 2000
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.CASINO.LOBBIES.ADD.FIELDS.DESCRIPTION.NAME" | translate',
				}
			}, {
				key: 'json',
				type: 'json-editor',
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.CASINO.LOBBIES.ADD.FIELDS.JSON.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.CASINO.LOBBIES.ADD.FIELDS.JSON.DESCRIPTION" | translate'
				},
				templateOptions: {
					readOnly: true,
					height: '400px'
				}
			}
		]
	}
]);
