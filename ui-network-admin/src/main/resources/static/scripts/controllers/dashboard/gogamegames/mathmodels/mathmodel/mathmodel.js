'use strict';

angular.module('lithium')
	.controller('GoGameMathModelController', ["mathModel", "notify", "$scope", "$state", "bsLoadingOverlayService", "$translate", "$stateParams",
	function(mathModel, notify, $scope, $state, bsLoadingOverlayService, $translate, $stateParams) {
		var controller = this;
		
		controller.mathModel = mathModel;
		
		controller.tabs = [
			{ name: "dashboard.gogamegames.mathmodels.mathmodel.view", title: "View", roles: "gogamegames_mathmodels_*" },
			{ name: "dashboard.gogamegames.mathmodels.mathmodel.revisions", title: "Revisions", roles: "gogamegames_mathmodels_*" }
		];
		
		if (mathModel.current !== null) {
			controller.tabsCurrent = [
				{ name: "dashboard.gogamegames.mathmodels.mathmodel.view.details", title: "Details", roles: "gogamegames_mathmodels_*" },
			];
		}
		
		controller.setTab = function(tab) {
			if (tab.tclass !== 'disabled') {
				$state.go(tab.name, {id:mathModel.id, mathModelRevisionId:$stateParams.mathModelRevisionId});
			}
		}
	}
]);