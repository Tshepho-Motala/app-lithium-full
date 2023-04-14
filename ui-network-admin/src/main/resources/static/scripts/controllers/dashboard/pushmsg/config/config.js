'use strict';

angular.module('lithium')
.controller('PushmsgConfigController', ["$translate", "$scope", "$userService", "$stateParams", "$state", "$filter", "$rootScope",
	function($translate, $scope, $userService, $stateParams, $state, $filter, $rootScope) {
		var controller = this;

		controller.selectedDomain = null
		controller.textTitle = 'UI_NETWORK_ADMIN.PAGE_HEADER.PUSH_MESSAGES.TITLE'
		controller.textDescr = 'UI_NETWORK_ADMIN.PAGE_HEADER.PUSH_MESSAGES.DESCRIPTION'
		
		controller.tabs = [
			{ name: "dashboard.pushmsg.config.providers", title: "UI_NETWORK_ADMIN.PUSHMSG.TAB.PROVIDERS", roles: "ADMIN" },
			{ name: "dashboard.pushmsg.config.templates", title: "UI_NETWORK_ADMIN.PUSHMSG.TAB.TEMPLATES", roles: "PUSHMSG_TEMPLATES_VIEW"},
			{ name: "dashboard.pushmsg.config.users", title: "UI_NETWORK_ADMIN.PUSHMSG.TAB.USERS", roles: "PUSHMSG_USERS_VIEW"},
			{ name: "dashboard.pushmsg.config.history", title: "UI_NETWORK_ADMIN.PUSHMSG.TAB.HISTORY", roles: "PUSHMSG_HISTORY_VIEW"}
		];
		
		controller.setTab = function(tab) {
			controller.tab = tab;
			$state.go(tab.name, {
				domainName: controller.selectedDomain
			});
		}

		if ($stateParams.domainName != null) controller.selectedDomain = $stateParams.domainName;
		
		angular.forEach(controller.tabs, function(tab) {
			if ($state.includes(tab.name)) controller.tab = tab;
		});

		$rootScope.provide.pageHeaderProvider.getDomainsList = () => {
			return $userService.domainsWithAnyRole(["ADMIN", "PUSHMSG_TEMPLATES_VIEW", "PUSHMSG_USERS_VIEW"]);
		}

		$rootScope.provide.pageHeaderProvider.domainSelect = ( item, isAlreadyChecked ) =>  {
			if ( item === null) {
				$rootScope.provide.pageHeaderProvider.clearSelectedDomain()
				return
			}

			controller.selectedDomain = item.name;
			if (angular.isUndefined(controller.tab)) {
				controller.setTab(controller.tabs[0]);
			} else {
				controller.setTab(controller.tab);
			}
		}

		$rootScope.provide.pageHeaderProvider.clearSelectedDomain = ( ) =>  {
			$scope.description = "";
			controller.selectedDomain = null;
		}

		$rootScope.provide.pageHeaderProvider.textTitle = ( ) =>  {
			return controller.textTitle ? controller.textTitle : ''
		}

		$rootScope.provide.pageHeaderProvider.textDescr = ( ) =>  {
			return controller.textDescr ? controller.textDescr : ''
		}

		window.VuePluginRegistry.loadByPage("page-header")
	}
]);