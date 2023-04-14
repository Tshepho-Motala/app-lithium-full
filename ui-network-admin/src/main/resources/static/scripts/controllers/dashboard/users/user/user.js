'use strict';

angular.module('lithium')
	.controller('UserController', ["user", "$log", "$scope", "$state", 
	function(user, $log, $scope, $state) {
		var controller = this;
		controller.user = user;
		
		$scope.domain = user.domain;
		
		controller.tabs = [
			{ name: "dashboard.domains.domain.users.user.view", title: "UI_NETWORK_ADMIN.USERS.TAB.SUMMARY", roles: "USER_VIEW" },
			{ name: "dashboard.domains.domain.users.user.loginevents", title: "UI_NETWORK_ADMIN.USERS.TAB.LOGINEVENTS", roles: "PLAYER_VIEW" },
		];
		
		controller.setTab = function(tab) {
			if (tab.tclass !== 'disabled') {
				controller.tab = tab;
				$state.go(tab.name);
			}
		}
		
		angular.forEach(controller.tabs, function(tab) {
			if ($state.includes(tab.name)) controller.tab = tab;
		});
	}
]);