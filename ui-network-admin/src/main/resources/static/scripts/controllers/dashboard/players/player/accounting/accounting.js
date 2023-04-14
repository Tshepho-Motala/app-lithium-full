'use strict';

angular.module('lithium')
	.controller('PlayerAccountingController', ["domain", "user", "userFields", "$uibModal", "$translate", "$log", "$dt", "$state", "$rootScope", "notify", "rest-accounting",
	function(domain, user, userFields, $uibModal, $translate, $log, $dt, $state, $rootScope, notify, acctRest) {
		var controller = this;
		
		controller.user = user;
		controller.domain = domain;
		
		console.log("user: ", user);
		controller.tabs = [
			//{ name: "dashboard.players.player.accounting.summary", title: "UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.TAB.SUMMARY", roles: "PLAYER_ACCOUNTING_SUMMARY" },
		//	{ name: "dashboard.players.player.accounting.adjustments", title: "UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.TAB.ADJUSTMENTS", roles: "PLAYER_ACCOUNTING_ADJUSTMENTS" },
			{ name: "dashboard.players.player.accounting.history", title: "UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.TAB.HISTORY", roles: "PLAYER_VIEW" }
		];
		
		controller.setTab = function(tab) {
			controller.tab = tab;
			$log.info("Go to state ",tab);
			$state.go(tab.name);
		}
		
		angular.forEach(controller.tabs, function(tab) {
			if ($state.includes(tab.name)) controller.tab = tab;
		});
}]);