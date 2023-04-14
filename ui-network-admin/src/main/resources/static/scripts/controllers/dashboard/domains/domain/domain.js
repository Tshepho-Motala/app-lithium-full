'use strict';

angular.module('lithium')
	.controller('Domain', ["domain", "$log", "$scope", "$state","notify","$translate",'rest-provider','$timeout',
	function(domain, $log, $scope, $state,notify,$translate,providerRest,$timeout) {
		var controller = this;
		$scope.domain = domain;
		controller.tabs = [
			{ name: "dashboard.domains.domain.view", title: "Info", roles: "DOMAIN_*,USER_*,PLAYER_*", displayForPlayerDomains: true, displayForAdminDomains: true },
			{ name: "dashboard.domains.domain.users", title: "Users", roles: "USER_*", displayForPlayerDomains: true, displayForAdminDomains: true  },
			{ name: "dashboard.domains.domain.groups", title: "Groups", roles: "GROUP_VIEW,GROUP_EDIT", displayForPlayerDomains: true, displayForAdminDomains: true },
			// { name: "dashboard.domains.domain.roles", title: "Default Roles", roles: "DOMAIN_DEFAULT_ROLES_*", displayForPlayerDomains: true, displayForAdminDomains: true },
			{ name: "dashboard.domains.domain.trantypeaccounts", title: "Transaction Type Accounts", roles: "ADMIN", displayForPlayerDomains: true, displayForAdminDomains: true },
			{ name: "dashboard.domains.domain.providers", title: "Providers", roles: "PROVIDERS_LIST,PROVIDER_VIEW,PROVIDER_EDIT", displayForPlayerDomains: true, displayForAdminDomains: true },
			{ name: "dashboard.domains.domain.clients", title: "Clients", roles: "DOMAIN_PROVIDERAUTH_*", displayForPlayerDomains: true, displayForAdminDomains: true },
			// { name: "dashboard.domains.domain.limits", title: 'Limits', roles: "DOMAIN_*", displayForPlayerDomains: true, displayForAdminDomains: true },
			{ name: "dashboard.domains.domain.currencies", title: 'Currencies', roles: "DOMAIN_CURRENCIES", displayForPlayerDomains: true, displayForAdminDomains: true },
			{ name: "dashboard.domains.domain.avatars", title: 'Avatars', roles: "AVATARS_*", displayForPlayerDomains: true, displayForAdminDomains: true },
			{ name: "dashboard.domains.domain.settings", title: 'Settings', roles: "DOMAIN_SETTINGS_*", displayForPlayerDomains: true, displayForAdminDomains: true },
			{ name: "dashboard.domains.domain.limitsystemaccess", title: 'Limit System Access', roles: "SYSTEMACCESS_*,ADMIN", displayForPlayerDomains: true, displayForAdminDomains: false },
			{ name: "dashboard.domains.domain.closurereasons", title: 'Closure reasons', roles: "CLOSURE_REASONS_*,ADMIN", displayForPlayerDomains: true, displayForAdminDomains: false },
		];

		if (domain.players === true) {
			controller.tabs.splice(1, 1);
		}
//		cataboomCampaignRest.checkCatConfigured(domain.name).then(function(response) {
//			if (response === 'Y') {
//				controller.tabs.push({ name: "dashboard.domains.domain.cataboomcampaigns", title: 'Cataboom Campaigns' });
//			}
//		},function(error) {
//		console.log("Vishay error ",error);}
//		).catch(function(error) {
//		console.log(error);
//
//		});
		
		providerRest.listForDomain(domain.name).then(function(response) {
			var flag = false ;
			for (var i = 0; i < response.length; i++) {
				if(response[i].url==="service-casino-provider-cataboom")
				flag=true;
				
			}
			if(flag===true){
				controller.tabs.push({ name: "dashboard.domains.domain.cataboomcampaigns", title: 'Cataboom Campaigns' });
			}
			
		});
		
		controller.setTab = function(tab) {
			controller.tab = tab;
			//Transition superseded error occurs when you try to trigger a state change before previous state change event has completed.
			// A simple way to solve this problem is by calling the state change after a timeout.
			// Which allows current state change to be completed and then triggers the next state change.
			//Note: The timeout here has no defined time, that simply means it will be executed after all current processes are done
			$timeout(function () {
				$state.go(tab.name, {
					domainName: domain.name
				}, { reload: tab.title === 'Info' });
			});

		}
		
		angular.forEach(controller.tabs, function(tab) {
			if ($state.includes(tab.name)) controller.tab = tab;
		});

//		if (!controller.tab) controller.tab = controller.tabs[0];
		
	}
]);
