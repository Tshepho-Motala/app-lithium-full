'use strict';

angular.module('lithium')
.controller('AccountingTransController',
["$scope", "$stateParams", "$state", "$userService",
function($scope, $stateParams, $state, $userService) {
	var controller = this;
	controller.domains = $userService.playerDomainsWithAnyRole(["ADMIN", "PLAYER_ACCOUNTING_SPORTSBOOK_HISTORY_VIEW", "PLAYER_ACCOUNTING_HISTORY_VIEW", "GLOBAL_ACCOUNTING_VIEW"]);

	controller.domainSelect = function(item) {
		controller.selectedDomain = item.name;
		// $state.go('dashboard.accounting.transactions.list', {
		// 	domainName: controller.selectedDomain
		// });
	}
	controller.clearSelectedDomain = function() {
		controller.selectedDomain = null;
		$state.go('dashboard.accounting.transactions');
	}
}]);
