'use strict'

angular.module('lithium').controller('RAFClicksController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', '$scope', '$userService', 'rest-domain', 'UserRest', '$rootScope',
	function($log, $translate, $dt, DTOptionsBuilder, $filter, $state, $scope, $userService, restDomain, userRest, $rootScope) {
		var controller = this;

		controller.selectedDomain = null
		controller.textTitle = 'UI_NETWORK_ADMIN.PAGE_HEADER.RAF_CLICKS.TITLE'
		controller.textDescr = 'UI_NETWORK_ADMIN.PAGE_HEADER.RAF_CLICKS.DESCRIPTION'
		
		var baseUrl = 'services/service-raf/admin/click/table';
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [2, 'desc']);
		controller.rafClicksTable = $dt.builder()
		.column($dt.column('domain.name').withTitle($translate("UI_NETWORK_ADMIN.RAF.CLICKS.LIST.DOMAIN")))
		.column($dt.column('referrer.playerGuid').withTitle($translate("UI_NETWORK_ADMIN.RAF.CLICKS.LIST.REFERRER")))
		.column($dt.columnformatdatetime('timestamp').withTitle($translate("UI_NETWORK_ADMIN.RAF.CLICKS.LIST.TIMESTAMP")))
		.column($dt.column('ip').withTitle($translate("UI_NETWORK_ADMIN.RAF.CLICKS.LIST.IP")))
		.column($dt.column('userAgent').withTitle($translate("UI_NETWORK_ADMIN.RAF.CLICKS.LIST.USERAGENT")))
		.options({ url: baseUrl, type: 'GET', data: function(d) { d.domainName = controller.selectedDomain } }, null, dtOptions, null)
		.build();
		
		controller.refreshRAFClicksTable = function() {
			controller.rafClicksTable.instance.reloadData(function(){}, false);
		}
		
		$scope.$watch(function() { return controller.selectedDomains }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				controller.rafClicksTable.instance.reloadData(function(){}, false);
			}
		});

		$rootScope.provide.pageHeaderProvider.getDomainsList = () => {
			return $userService.domainsWithAnyRole($userService.roles());
		}

		$rootScope.provide.pageHeaderProvider.domainSelect = ( item, isAlreadyChecked ) =>  {
			if ( item === null) {
				$rootScope.provide.pageHeaderProvider.clearSelectedDomain()
				return
			}

			controller.selectedDomain = item.name;

			if(controller.rafClicksTable.instance.id) {// find out exist table or not
				controller.refreshRAFClicksTable();
			}
		}

		$rootScope.provide.pageHeaderProvider.clearSelectedDomain = ( ) =>  {
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
