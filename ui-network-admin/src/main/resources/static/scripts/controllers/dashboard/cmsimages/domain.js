'use strict';

angular.module('lithium')
.controller('CmsAssetDomainController', ["$translate", "$scope", "$userService", "$stateParams", "$state", "$filter", "$rootScope",
	function($translate, $scope, $userService, $stateParams, $state, $filter, $rootScope) {
		var controller = this;

		controller.selectedDomain = null
		controller.textTitle = 'UI_NETWORK_ADMIN.PAGE_HEADER.CMS_IMAGES.TITLE'
		controller.textDescr = 'UI_NETWORK_ADMIN.PAGE_HEADER.CMS_IMAGES.DESCRIPTION'

		controller.tabs = [
			{ name: "dashboard.cmsimages.domain.list", title: "UI_NETWORK_ADMIN.TEMPLATES.TAB.LIST", roles: "ADMIN,TEMPLATES_MANAGE,TEMPLATES_VIEW,TEMPLATES_ADD,TEMPLATES_EDIT" }
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
			return $userService.domainsWithAnyRole(["ADMIN","TEMPLATES_MANAGE","TEMPLATES_VIEW","TEMPLATES_ADD","TEMPLATES_EDIT"]);
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
