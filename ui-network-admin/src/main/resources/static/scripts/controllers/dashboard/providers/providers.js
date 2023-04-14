'use strict';

angular.module('lithium')
	.controller('Providers', ["providers", "$translate", "$scope", "$state", "notify", "rest-group",
	function(providers, $translate, $scope, $state, notify, restGroup) {
		$translate('UI_NETWORK_ADMIN.GROUPS.VIEW.HEADER.TITLE', {groupName:group.name}).then(function(title) {
			$scope.$parent.title = title;
			$state.params.groupName = group.name;
		});
		$scope.$parent.title = 'UI_NETWORK_ADMIN.PROVIDERS.LIST.TITLE';
		$scope.$parent.description = 'UI_NETWORK_ADMIN.PROVIDERS.LIST.DESCRIPTION';
		var controller = this;
		$scope.providers = providers;
		$scope.domainName = $state.params.domainName;
	}
]);