'use strict';

angular.module('lithium')
.controller('CashierTransListController', ['$scope', '$stateParams', function($scope, $stateParams) {
	$scope.setDescription("UI_NETWORK_ADMIN.CASHIER.TAB.TRANS");

	var controller = this;
	controller.data = {allowUserSearch: true, allowAddManualTran: true};
}]);
