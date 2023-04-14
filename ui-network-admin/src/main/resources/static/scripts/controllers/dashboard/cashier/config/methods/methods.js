'use strict';

angular.module('lithium')
.controller('CashierMethodsController',
["$scope", "$stateParams", "$state",
function($scope, $stateParams, $state) {
	var controller = this;
	$scope.setDescription("UI_NETWORK_ADMIN.CASHIER.HEADER.METHODS");
	
	controller.tabs = [
		{ name: "dashboard.cashier.config.methods.list", type: "deposit", title: "UI_NETWORK_ADMIN.CASHIER.TAB.METHODS.DEPOSIT", roles: "CASHIER_CONFIG,CASHIER_CONFIG_VIEW" },
		{ name: "dashboard.cashier.config.methods.list", type: "withdraw", title: "UI_NETWORK_ADMIN.CASHIER.TAB.METHODS.WITHDRAWAL", roles: "CASHIER_CONFIG,CASHIER_CONFIG_VIEW" }
	];
	
	controller.setTab = function(tab) {
		controller.tab = tab;
		$state.go(tab.name, {
			domainName: $stateParams.domainName,
			type: tab.type
		});
	}
	
	controller.setTab(controller.tabs[0]);
}]);
