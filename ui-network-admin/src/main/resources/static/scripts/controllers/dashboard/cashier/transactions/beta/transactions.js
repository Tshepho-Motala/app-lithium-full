'use strict';

angular.module('lithium')
.controller('CashierBetaTransController',
["$scope", "$stateParams", "$state", "$userService", "$rootScope",
function($scope, $stateParams, $state, $userService, $rootScope) {
	var controller = this;

	controller.selectedDomain = null;

	controller.textTitle = 'BETA  Cashier'
	controller.textDescr = 'This page is in test mode.'

	$scope.setDescription("UI_NETWORK_ADMIN.CASHIER.HEADER.TRANSACTIONS");

	$rootScope.provide.pageHeaderProvider.getDomainsList = () => {
		return $userService.playerDomainsWithAnyRole(["ADMIN", "CASHIER_VIEW"]);
	}

	$rootScope.provide.pageHeaderProvider.domainSelect = ( item, isAlreadyChecked ) =>  {
		if ( item === null) {
			$rootScope.provide.pageHeaderProvider.clearSelectedDomain()
			return
		}
		if (isAlreadyChecked) {
			$state.go('dashboard.cashier.beta-transactions.list', {
				domainName: item.name
			});
		}

		controller.selectedDomain = item.name;
		$stateParams.domainName = item.name;
	}

	$rootScope.provide.pageHeaderProvider.clearSelectedDomain = ( ) =>  {
		controller.selectedDomain = null;
		$scope.setDescription("");
		$state.go('dashboard.cashier.beta-transactions');
	}

	$rootScope.provide.pageHeaderProvider.textTitle = ( ) =>  {
		return controller.textTitle ? controller.textTitle : ''
	}

	$rootScope.provide.pageHeaderProvider.textDescr = ( ) =>  {
		return controller.textDescr ? controller.textDescr : ''
	}

	window.VuePluginRegistry.loadByPage("page-header")
}]);
