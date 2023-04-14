'use strict'

angular.module('lithium').controller('GoGameTutorialsTutorialController', ['tutorial', '$state',
	function(tutorial, $state) {
		var controller = this;
		
		controller.model = tutorial;
		
		if (typeof(controller.model.result) === 'string') {
			var isJsonStr = true;
			try {
				JSON.parse(controller.model.result);
			} catch (error) {
				isJsonStr = false;
			}
			if (isJsonStr) {
				controller.model.result = JSON.parse(controller.model.result);
			}
		}
		
		controller.edit = function() {
			$state.go("dashboard.gogamegames.tutorials.tutorial.edit", { id:tutorial.id });
		}
	}
]);