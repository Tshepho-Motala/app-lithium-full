'use strict';

angular.module('lithium')
	.controller('Group', ["group", "tabs", "$translate", "$scope", "$state", "notify", "rest-group",
	function(group, tabs, $translate, $scope, $state, notify, restGroup) {
		$translate('UI_NETWORK_ADMIN.GROUPS.VIEW.HEADER.TITLE', {groupName:group.name}).then(function(title) {
			$scope.$parent.title = title;
			$state.params.groupName = group.name;
		});
		$scope.$parent.title = 'UI_NETWORK_ADMIN.GROUPS.VIEW.HEADER.TITLE';
		$scope.$parent.description = 'UI_NETWORK_ADMIN.GROUPS.VIEW.HEADER.DESCRIPTION';
		var controller = this;
		$scope.group = group;
		$scope.domainName = $state.params.domainName;
		
		controller.tabs = tabs;
		
		controller.setTab = function(tab) {
			controller.tab = tab;
			$state.go(tab.name);
		}
		
		controller.changeEnabled = function(id, enabled) {
			restGroup.enabled($scope.domainName, id, enabled).then(function() {
				group.enabled = enabled;
				notify.success("UI_NETWORK_ADMIN.GROUP.SAVE.SUCCESS");
			}, function(response) {
				notify.warning("UI_NETWORK_ADMIN.GROUP.SAVE.FAIL");
			});
		}
		
		angular.forEach(controller.tabs, function(tab) {
			if ($state.includes(tab.name)) controller.tab = tab;
		});
	}
]);