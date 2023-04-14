'use strict'

angular.module('lithium').controller('GoGameDebugResultController', ['debugResult', 'GoGameGamesRest','$translate','$state','notify',
	function(debugResult, goGameGamesRest,$translate,$state, notify) {
		var controller = this;
		
		controller.model = debugResult;
		
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
			$state.go("dashboard.gogamegames.debugresults.debugresult.edit", { id:debugResult.debugResultId.debugResultId, engineId:debugResult.debugResultId.engine.id });
		}

		controller.doDelete = function() {
			goGameGamesRest.removeDebugResult(debugResult.debugResultId.debugResultId,
				debugResult.debugResultId.engine.id,
				debugResult.debugResultId.mathModelRevision.id).then(function(rs) {
				notify.success("UI_NETWORK_ADMIN.GOGAME.DEBUG_RESULTS.DELETE.SUCCESS");
				$state.go("dashboard.gogamegames.debugresults");
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.GOGAME.DEBUG_RESULTS.DELETE.ERROR");
				errors.catch("", false)(error)
			});
		}
	}
]);