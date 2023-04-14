'use strict';

angular.module('lithium')
	.controller('DomainSettingsController', ["domain", "notify", "$scope", "$state", "bsLoadingOverlayService", "$translate", "$stateParams",
	function(domain, notify, $scope, $state, bsLoadingOverlayService, $translate, $stateParams) {
		var controller = this;
		
		controller.tabs = [
			{ name: "dashboard.domains.domain.settings.view", title: "View", roles: "domain_settings_*" },
			{ name: "dashboard.domains.domain.settings.history", title: "History", roles: "domain_settings_*" }
		];
		
		controller.setTab = function(tab) {
			controller.tab = tab;
			var data = {};
			if (tab.name === 'dashboard.domains.domain.settings.view') {
				data.domainRevisionId = (domain.current === undefined || domain.current === null)? -1: domain.current.id;
			}
			console.log(data);
			$state.go(tab.name, data);
		}
		
		console.log('state',$state.current.name);
		
		switch ($state.current.name) {
			case 'dashboard.domains.domain.settings': controller.setTab(controller.tabs[0]); break;
			case 'dashboard.domains.domain.settings.history': controller.setTab(controller.tabs[1]); break;
		}
	}
]);