'use strict';

angular.module('lithium')
.controller('ConfirmChangeFlagModal',
['$uibModalInstance', '$translate', 'entityId', 'flagType', 'flagValue', 'rest-games', 'notify', 'errors', 'bsLoadingOverlayService',
function ($uibModalInstance, $translate, entityId, flagType, flagValue, restGames, notify, errors, bsLoadingOverlayService) {
	var controller = this;
	bsLoadingOverlayService.start({referenceId:controller.referenceId});
	controller.referenceId = 'addcomment-overlay';
	controller.changeFlagValueTo = flagValue === false ? true : false;
	controller.flagType = flagType;
	controller.flagValue = flagValue;
	controller.game = {};
	var gameId = entityId + '';

	controller.getGame = function(gameId) {
		restGames.findByGameId(gameId).then(function(game) {
			return game.plain();
		});
	}

	controller.submit = function() {
		restGames.findByGameId(gameId).then(function(game) {
			controller.game = game.plain();
			if (flagType === "enabled") {
				controller.game.enabled = controller.changeFlagValueTo;
			} else if (flagType === "visible") {
				controller.game.visible = controller.changeFlagValueTo;
			}
			controller.saveGame(controller.game);
		});
	};
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};

	controller.saveGame = function(game) {
		restGames.save(controller.game).then(function(game) {
			notify.success("UI_NETWORK_ADMIN.GAMES.EDIT.SUCCESS");
			$uibModalInstance.close(game);
		}).catch(function(error) {
			errors.catch("Could not save game information.", false)(error)
		});
	}
}]);
