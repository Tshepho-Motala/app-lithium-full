'use strict';

angular.module('lithium')
	.controller('PlayersTagView', ["tag", "$uibModal", "$translate", "$log", "$dt", "$state", "$rootScope", "notify", "UserRest", "$scope",
	function(tag, $uibModal, $translate, $log, $dt, $state, $rootScope, notify, UserRest, $scope) {
		var controller = this;
		controller.tag = tag;
		
		controller.tabs = [
			{ id:0, name: "dashboard.players.tag.view.details", title: "UI_NETWORK_ADMIN.PLAYERS.TAGS.TABS.DETAILS", roles: "PLAYER_EDIT" },
			{ id:1, name: "dashboard.players.tag.view.players", title: "UI_NETWORK_ADMIN.PLAYERS.TAGS.TABS.PLAYERS", roles: "PLAYER_EDIT" }
		];
		
		controller.setTab = function(tab) {
			controller.tab = tab;
			$state.go(tab.name, {
				tag: tag
			});
		}
		
		angular.forEach(controller.tabs, function(tab) {
			if ($state.includes(tab.name)) controller.tab = tab;
		});
		controller.setTab(controller.tabs[1]);
}]);