'use strict';

angular.module('lithium')
.controller('RAFConfigController', ["$translate", "$scope", "$userService", "$stateParams", "$state", "$filter", '$rootScope',
	function($translate, $scope, $userService, $stateParams, $state, $filter, $rootScope) {
		var controller = this;
		controller.domains = $userService.domainsWithAnyRole(["ADMIN", "RAF_CONFIG"]);

		controller.selectedDomain = null
		controller.textTitle = 'UI_NETWORK_ADMIN.PAGE_HEADER.RAF_CONFIG.TITLE'
		controller.textDescr = 'UI_NETWORK_ADMIN.PAGE_HEADER.RAF_CONFIG.DESCRIPTION'
		
		controller.tabs = [
			{ name: "dashboard.raf.config.settings", title: "Settings", roles: "ADMIN,RAF_CONFIG" }
		];
		
		controller.setTab = function(tab) {
			controller.tab = tab;
			$state.go(tab.name, {
				domainName: controller.selectedDomain
			});
		}
		
		controller.domainSelect = function(item) {
			controller.selectedDomain = item.name;
			if (angular.isUndefined(controller.tab)) {
				controller.setTab(controller.tabs[0]);
			} else {
				controller.setTab(controller.tab);
			}
		}
		controller.clearSelectedDomain = function() {
			$scope.description = "";
			controller.selectedDomain = null;
		}
		
		if ($stateParams.domainName != null) controller.selectedDomain = $stateParams.domainName;
		
		angular.forEach(controller.tabs, function(tab) {
			if ($state.includes(tab.name)) controller.tab = tab;
		});

		$rootScope.provide.pageHeaderProvider.getDomainsList = () => {
			return $userService.domainsWithAnyRole(["ADMIN", "RAF_CONFIG"]);
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
			$state.go('dashboard.raf.config');
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