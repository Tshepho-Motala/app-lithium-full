'use strict';

angular.module('lithium').controller('GrantMassBonusesController', ["$scope", "$stateParams", "$state", "$userService", "$rootScope",
function($scope, $stateParams, $state, $userService, $rootScope) {

	var controller = this;

	controller.selectedDomain = null;
	controller.textTitle = 'UI_NETWORK_ADMIN.PAGE_HEADER.BONUSES_GRANT.TITLE'
	controller.textDescr = 'UI_NETWORK_ADMIN.PAGE_HEADER.BONUSES_GRANT.DESCRIPTION'

	$rootScope.provide.pageHeaderProvider.getDomainsList = () => {
		return $userService.playerDomainsWithAnyRole(["ADMIN", "MASS_BONUS_ALLOCATION_VIEW"]);
	}

	$rootScope.provide.pageHeaderProvider.domainSelect = ( item, isAlreadyChecked ) =>  {
		if ( item === null) {
			$rootScope.provide.pageHeaderProvider.clearSelectedDomain()
			return
		}

		controller.selectedDomain = item.name;

		$state.go('dashboard.bonuses.grantmass.tool', {
			domainName: controller.selectedDomain
		});
	}

	$rootScope.provide.pageHeaderProvider.clearSelectedDomain = ( ) =>  {
		controller.selectedDomain = null;
		$state.go('dashboard.bonuses.grantmass');
	}

	$rootScope.provide.pageHeaderProvider.textTitle = ( ) =>  {
		return controller.textTitle ? controller.textTitle : ''
	}

	$rootScope.provide.pageHeaderProvider.textDescr = ( ) =>  {
		return controller.textDescr ? controller.textDescr : ''
	}

	window.VuePluginRegistry.loadByPage("page-header")
}]);
