'use strict';

angular.module('lithium')
.controller('MailConfigController', ["$translate", "$scope", "$userService", "$stateParams", "$state", "$filter", "$rootScope",
	function($translate, $scope, $userService, $stateParams, $state, $filter, $rootScope) {
		var controller = this;

		controller.selectedDomain = null
		controller.textTitle = 'UI_NETWORK_ADMIN.PAGE_HEADER.MAIL_CONFIG.TITLE'
		controller.textDescr = 'UI_NETWORK_ADMIN.PAGE_HEADER.MAIL_CONFIG.DESCRIPTION'
		
		controller.tabs = [
			{ name: "dashboard.mail.config.providers", title: "UI_NETWORK_ADMIN.MAIL.TAB.PROVIDERS", roles: "MAIL_CONFIG" },
			{ name: "dashboard.mail.config.templates", title: "UI_NETWORK_ADMIN.MAIL.TAB.TEMPLATES", roles: "EMAIL_TEMPLATES_VIEW"},
			{ name: "dashboard.mail.config.defaulttemplates", title: "UI_NETWORK_ADMIN.MAIL.TAB.DEFAULTTEMPLATES", roles: "EMAIL_TEMPLATES_VIEW"}
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
			return $userService.domainsWithAnyRole(["MAIL_CONFIG", "EMAIL_TEMPLATES_VIEW"]);
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