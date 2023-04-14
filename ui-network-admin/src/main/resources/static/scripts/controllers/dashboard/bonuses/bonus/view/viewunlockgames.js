'use strict';

angular.module('lithium')
	.controller('BonusViewUnlockGamesController', ["bonus", "bonusRevision", "notify", "$state", "$translate", "$scope", "rest-casino", "rest-games",
	function(bonus, bonusRevision, notify, $state, $translate, $scope, casinoRest, gamesRest) {
		var controller = this;
		controller.unlockGames = [];
		controller.bonus = bonus;
		controller.bonusRevision = bonusRevision;
		
		controller.imageUrl = "/services/service-games/games/"+controller.bonusRevision.domain.name+"/getImage?";
		controller.demoUrl = "/services/service-games/games/"+controller.bonusRevision.domain.name+"/demoGame?";
		
		if (bonus.current === null) {
			$state.go("dashboard.bonuses.bonus.nocurrent");
			return;
		}
		
		
		$translate('UI_NETWORK_ADMIN.BONUS.VIEW.TITLE', { bonusCode:controller.bonusRevision.bonusCode }).then(function (translations) {
			$scope.$parent.title = translations;
		});
		$translate('UI_NETWORK_ADMIN.BONUS.VIEW.DESCRIPTION', { bonusName:controller.bonusRevision.bonusName }).then(function (translations) {
			$scope.$parent.description = translations;
		});
		
		controller.imageUrl = "/services/service-games/games/"+controller.bonusRevision.domain.name+"/getImage?";
		controller.demoUrl = "/services/service-games/games/"+controller.bonusRevision.domain.name+"/demoGame?";
		
		controller.gameInfoByGameGuid = function(game) {
			gamesRest.findByGameGuid(game.gameGuid, controller.bonusRevision.domain.name).then(function(response) {
				game.gameInfo = response.plain();
			}).catch(
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
			).finally(function () {
				
			});
		}
		
		controller.getUnlockGames = function() {
			casinoRest.bonusUnlockGames(controller.bonusRevision.id).then(function(response) {
				controller.unlockGames = response.plain();
				console.log(response.plain());
				for (var g in controller.unlockGames) {
					var game = controller.unlockGames[g];
					controller.gameInfoByGameGuid(game);
				}
//				controller.getGameList();
			}).catch(function(error) {
				console.log("loadUnlockGameRules error : "+error);
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
			});
		}
		
		controller.getUnlockGames();
	}
]);