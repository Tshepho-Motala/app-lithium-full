'use strict';

angular.module('lithium')
.controller('CashierTransController',
["$scope", "$stateParams", "$state", "$userService", "$rootScope",
function($scope, $stateParams, $state, $userService, $rootScope) {
	var controller = this;

	controller.selectedDomain = null;

	controller.textTitle = 'UI_NETWORK_ADMIN.PAGE_HEADER.CASHIER_TRANSACTIONS.TITLE'
	controller.textDescr = 'UI_NETWORK_ADMIN.PAGE_HEADER.CASHIER_TRANSACTIONS.DESCRIPTION'

	$scope.setDescription("UI_NETWORK_ADMIN.CASHIER.HEADER.TRANSACTIONS");

	$rootScope.provide.pageHeaderProvider.getDomainsList = () => {
		return $userService.playerDomainsWithAnyRole(["ADMIN", "CASHIER_VIEW"]);
	}

	$rootScope.provide.pageHeaderProvider.domainSelect = ( item, isAlreadyChecked ) =>  {
		if ( item === null) {
			$rootScope.provide.pageHeaderProvider.clearSelectedDomain()
			return
		}

		controller.selectedDomain = item.name;
		$stateParams.domainName = item.name;

		if (isAlreadyChecked && !window.location.toString().includes("add")) {
			$state.go('dashboard.cashier.transactions.list', {
				domainName: item.name
			});
		}
	}

	$rootScope.provide.pageHeaderProvider.clearSelectedDomain = ( ) =>  {
		controller.selectedDomain = null;
		$scope.setDescription("");
		$state.go('dashboard.cashier.transactions');
	}

	$rootScope.provide.pageHeaderProvider.textTitle = ( ) =>  {
		return controller.textTitle ? controller.textTitle : ''
	}

	$rootScope.provide.pageHeaderProvider.textDescr = ( ) =>  {
		return controller.textDescr ? controller.textDescr : ''
	}

	window.VuePluginRegistry.loadByPage("page-header")
}]);
