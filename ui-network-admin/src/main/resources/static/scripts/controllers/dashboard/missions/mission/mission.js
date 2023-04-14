'use strict';

angular.module('lithium')
	.controller('MissionController', ["mission", "notify", "$scope", "$state", "bsLoadingOverlayService", "$translate", "$stateParams",
	function(mission, notify, $scope, $state, bsLoadingOverlayService, $translate, $stateParams) {
		var controller = this;
		
		controller.mission = mission;
		controller.domainName = mission.current.domain.name;
		
		controller.tabs = [
			{ name: "dashboard.missions.mission.view", title: "View", roles: "missions" },
			{ name: "dashboard.missions.mission.revisions", title: "Revisions", roles: "missions" }
		];
		
		if (mission.current !== null) {
			controller.tabsCurrent = [
				{ name: "dashboard.missions.mission.view.details", title: "Details", roles: "missions" },
				{ name: "dashboard.missions.mission.view.usermissions", title: "UI_NETWORK_ADMIN.USERMISSIONS.LIST.TITLE", roles: "usermissions" }
			];
		}
		
		controller.setTab = function(tab) {
			if (tab.tclass !== 'disabled') {
				$state.go(tab.name, {id:mission.id, missionRevisionId:$stateParams.missionRevisionId});
			}
		}
	}
]);