'use strict';

angular.module('lithium')
	.controller('CasinoLobbyController', ['lobby', '$state', '$stateParams',
	function(lobby, $state, $stateParams) {
		var controller = this;
		controller.lobby = lobby;
		
		controller.tabs = [
			{ name: 'dashboard.casino.lobbies.lobby.view', title: 'View', roles: 'casino_lobbies_view' },
			{ name: 'dashboard.casino.lobbies.lobby.revisions', title: 'Revisions', roles: 'casino_lobbies_view' }
		];
		
		controller.setTab = function(tab) {
			if (tab.tclass !== 'disabled') {
				$state.go(tab.name, {domainName: $stateParams.domainName, lobbyId:lobby.id, lobbyRevisionId:$stateParams.lobbyRevisionId});
			}
		}
	}
]);