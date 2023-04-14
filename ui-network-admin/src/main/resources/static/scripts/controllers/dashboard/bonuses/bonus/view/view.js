'use strict';

angular.module('lithium')
	.controller('BonusViewController', ["bonus", "bonusRevision", "notify", "$state", "$translate", "$scope", "rest-casino", "rest-games","rest-domain",
	function(bonus, bonusRevision, notify, $state, $translate, $scope, casinoRest, gamesRest, domainRest) {
		var controller = this;

		if (bonus.current === null) {
			$state.go("dashboard.bonuses.bonus.nocurrent");
			return;
		}
		controller.bonus = bonus;
		controller.bonusRevision = bonusRevision;

		if (controller.bonusRevision.bonusFreeMoney.length > 0) {
			for (var i = 0; i < controller.bonusRevision.bonusFreeMoney.length; i++) {
				controller.bonusRevision.bonusFreeMoney[i].amount = controller.bonusRevision.bonusFreeMoney[i].amount / 100;
			}
		}

//		console.log(controller.bonus);
//		console.log(controller.bonusRevision);

		controller.imageUrl = "/services/service-games/games/"+controller.bonusRevision.domain.name+"/getImage?";
		controller.demoUrl = "/services/service-games/games/"+controller.bonusRevision.domain.name+"/demoGame?";

//		console.log(bonus.plain());
		if (controller.bonusRevision.activeDays != null) {
			var activeDays = controller.bonusRevision.activeDays.split(',');
			var translatedDays = [];
			for (var day in activeDays) {
				translatedDays.push("GLOBAL.DAYS."+activeDays[day]);
			}
			controller.activeDays = '';
			$translate(translatedDays).then(function (translations) {
				for (var day in translations) {
					controller.activeDays += translations[day]+',';
				}
				controller.activeDays = controller.activeDays.slice(0, -1);
			});
		}

		casinoRest.findFreespinsRules(controller.bonusRevision.id).then(function(response) {
			controller.freespinRules = response.plain();
			for (var r in controller.freespinRules) {
				var rule = controller.freespinRules[r];
				for (var g in rule.bonusRulesFreespinGames) {
					var game = rule.bonusRulesFreespinGames[g];
					controller.gameInfo(game, rule.provider);
				}
			}
		}).catch(
			//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
		).finally(function () {

		});

		casinoRest.findCasinoChipRules(controller.bonusRevision.id).then(function(response) {
			controller.casinoChipRules = response.plain();
			for (var r in controller.casinoChipRules) {
				var rule = controller.casinoChipRules[r];
				for (var g in rule.bonusRulesCasinoChipGames) {
					var game = rule.bonusRulesCasinoChipGames[g];
					controller.gameInfo(game, rule.provider);
				}
			}
		}).catch(
			//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
		).finally(function () {

		});

		controller.gameInfo = function(game, provider) {
			gamesRest.findByGameGuid(provider+"_"+game.gameId, controller.bonusRevision.domain.name).then(function(response) {
				game.gameInfo = response.plain();
			}).catch(
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
			).finally(function () {

			});
		}

		domainRest.findByName(controller.bonusRevision.domain.name).then(function(response) {
			controller.domainInfo = response.plain();
		});

		if (controller.bonusRevision.bonusType == 1) {
			casinoRest.findDepositBonusRequirements(controller.bonusRevision.id).then(function(response) {
				//console.log(response.plain());
				controller.depositRequirements = response;
			}).catch(
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
			).finally(function () {

			});
		} else if (controller.bonusRevision.bonusType == 0) {
			casinoRest.findSignupBonusRequirements(controller.bonusRevision.id).then(function(response) {
//				console.log(response.plain());
				controller.signupRequirements = response;
			}).catch(
					//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
			).finally(function () {

			});
		}

		switch (controller.bonusRevision.maxRedeemableGranularity) {
			case 1:
				controller.maxRedeemableGranularity = 'UI_NETWORK_ADMIN.BONUS.MAXREDEEMABLE.GRANULARITY_YEAR';
				break;
			case 2:
				controller.maxRedeemableGranularity = 'UI_NETWORK_ADMIN.BONUS.MAXREDEEMABLE.GRANULARITY_MONTH';
				break;
			case 3:
				controller.maxRedeemableGranularity = 'UI_NETWORK_ADMIN.BONUS.MAXREDEEMABLE.GRANULARITY_DAY';
				break;
			case 4:
				controller.maxRedeemableGranularity = 'UI_NETWORK_ADMIN.BONUS.MAXREDEEMABLE.GRANULARITY_WEEK';
				break;
			case 5:
				controller.maxRedeemableGranularity = 'UI_NETWORK_ADMIN.BONUS.MAXREDEEMABLE.GRANULARITY_TOTAL';
				break;
			default:
				controller.maxRedeemableGranularity = 'UI_NETWORK_ADMIN.BONUS.MAXREDEEMABLE.GRANULARITY_TOTAL';
				break;
		}
	}
]);
