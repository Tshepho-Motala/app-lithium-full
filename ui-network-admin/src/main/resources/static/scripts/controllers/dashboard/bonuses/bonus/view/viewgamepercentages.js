'use strict';

angular.module('lithium')
	.controller('BonusViewGamePercentagesController', ["bonus", "bonusRevision", "notify", "$state", "$translate", "$scope", "rest-casino", "rest-games",
	function(bonus, bonusRevision, notify, $state, $translate, $scope, casinoRest, gamesRest) {
		var controller = this;
		if (bonus.current === null) {
			$state.go("dashboard.bonuses.bonus.nocurrent");
			return;
		}
		
		controller.bonus = bonus;
		controller.bonusRevision = bonusRevision;
		
		$translate('UI_NETWORK_ADMIN.BONUS.VIEW.TITLE', { bonusCode:controller.bonusRevision.bonusCode }).then(function (translations) {
			$scope.$parent.title = translations;
		});
		$translate('UI_NETWORK_ADMIN.BONUS.VIEW.DESCRIPTION', { bonusName:controller.bonusRevision.bonusName }).then(function (translations) {
			$scope.$parent.description = translations;
		});
		
		controller.imageUrl = "/services/service-games/games/"+controller.bonusRevision.domain.name+"/getImage?";
		controller.demoUrl = "/services/service-games/games/"+controller.bonusRevision.domain.name+"/demoGame?";
		
		casinoRest.bonusRulesGamesPercentages(controller.bonusRevision.id).then(function(response) {
			controller.games = response.plain();
			for (var g in controller.games) {
				var game = controller.games[g];
				controller.gameInfo(game, controller.bonusRevision.domain.name);
			} 
		}).catch(
			//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
		).finally(function () {
//			console.log(controller.games);
		});
		
		controller.gameInfo = function(game, domain) {
			gamesRest.findByGameGuid(game.gameGuid, domain).then(function(response) {
				game.gameInfo = response.plain();
				game._gameGuid = game.gameGuid.replace(/\//g, '_');
			}).catch(function () {
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
				game.gameInfo = {
					name: game.gameGuid,
					labels: {
						os: {
							value:""
						}
					},
					providerGuid: game.gameGuid
				};
			}).finally(function () {
			
			});
		}
	}
]);