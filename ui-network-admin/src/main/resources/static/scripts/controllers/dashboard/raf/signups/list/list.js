'use strict'

angular.module('lithium').controller('RAFSignupsController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', '$scope', '$userService', 'rest-domain', 'UserRest', '$rootScope',
	function($log, $translate, $dt, DTOptionsBuilder, $filter, $state, $scope, $userService, restDomain, userRest, $rootScope) {
		var controller = this;
		
		controller.selectedDomain = null
		controller.textTitle = 'UI_NETWORK_ADMIN.PAGE_HEADER.RAF_SIGNUPS.TITLE'
		controller.textDescr = 'UI_NETWORK_ADMIN.PAGE_HEADER.RAF_SIGNUPS.DESCRIPTION'

		var baseUrl = 'services/service-raf/admin/referral/table/signups';
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [3, 'desc']);
		controller.rafSignupsTable = $dt.builder()
		.column($dt.column('domain.name').withTitle($translate("UI_NETWORK_ADMIN.RAF.SIGNUPS.LIST.DOMAIN")))
		.column($dt.column('referrer.playerGuid').withTitle($translate("UI_NETWORK_ADMIN.RAF.SIGNUPS.LIST.REFERRER")))
		.column($dt.column('playerGuid').withTitle($translate("UI_NETWORK_ADMIN.RAF.SIGNUPS.LIST.PLAYER")))
		.column($dt.columnformatdatetime('timestamp').withTitle($translate("UI_NETWORK_ADMIN.RAF.SIGNUPS.LIST.TIMESTAMP")))
		.column($dt.column('converted').withTitle($translate("UI_NETWORK_ADMIN.RAF.SIGNUPS.LIST.CONVERTED")))
		.options({ url: baseUrl, type: 'GET', data: function(d) { d.domainName = controller.selectedDomain } }, null, dtOptions, null)
		.build();
		
		controller.refreshRAFSignupsTable = function() {
			controller.rafSignupsTable.instance.reloadData(function(){}, false);
		}
		
		$scope.$watch(function() { return controller.selectedDomains }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				controller.rafSignupsTable.instance.reloadData(function(){}, false);
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

			if(controller.rafSignupsTable.instance.id) {// find out exist table or not
				controller.refreshRAFSignupsTable();
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
