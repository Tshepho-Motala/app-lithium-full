'use strict';

angular.module('lithium')
.controller('CashierDomainProfileController', ["profile", "$scope", "$state",
	function(profile, $scope, $state) {
		var controller = this;
		controller.profile = profile;
		
		$scope.setDescription("");
		
		controller.tabs = [
			{ name: "dashboard.cashier.config.profile.edit", title: "UI_NETWORK_ADMIN.CASHIER.PROFILES.TAB.EDIT", roles: "ADMIN" },
			{ name: "dashboard.cashier.config.profile.users", title: "UI_NETWORK_ADMIN.CASHIER.PROFILES.TAB.USERS", roles: "ADMIN" }
		];
		
		controller.setTab = function(tab) {
			controller.tab = tab;
			$state.go(tab.name);
		}
		
		angular.forEach(controller.tabs, function(tab) {
			if ($state.includes(tab.name)) controller.tab = tab;
		});
	}
]);