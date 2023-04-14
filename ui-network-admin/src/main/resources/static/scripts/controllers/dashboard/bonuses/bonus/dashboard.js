'use strict';

angular.module('lithium')
	.controller('BonusDashboardController', ["bonus", "bonusRevision", "notify", "$state", "$translate", "$scope", "rest-casino", "rest-games",
	function(bonus, bonusRevision, notify, $state, $translate, $scope, casinoRest, gamesRest) {
		var controller = this;
		
		if (bonus.current === null) {
			$state.go("dashboard.bonuses.bonus.nocurrent");
			return;
		}
		controller.bonus = bonus;
		controller.bonusRevision = bonusRevision;
		
		console.log(controller.bonus);
		console.log(controller.bonusRevision);
		
		$scope.setGranularity = function (g) {
			$scope.ranges = {
					1: { dateStart : moment().subtract(20, 'years'), dateEnd: moment().add(1, "years") },
					2: { dateStart : moment().subtract(48, 'months'), dateEnd: moment().add(1, "months") },
					3: { dateStart : moment().subtract(365, 'days'), dateEnd: moment().add(1, "days") },
					4: { dateStart : moment().subtract(104, 'weeks'), dateEnd: moment().add(1, "weeks").startOf("isoWeeks") }
				};	
			$scope.granularity = g;
		}
		
		$scope.summaries = [{
			domains: [ bonusRevision.domain.name ],
			currency: { code: "USD", format: "$##.00" },
			accountCode: "PLAYER_BALANCE_CASINO_BONUS",
			inverse: true,
			debit: { color: "blue", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.TRANSFERTOBONUS", tran: "TRANSFER_TO_CASINO_BONUS" },
			credit: { color: "yellow", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.TRANSFERFROMBONUS", tran: "TRANSFER_FROM_CASINO_BONUS" },
			net: { color: "green", titleKey: "UI_NETWORK_ADMIN.DASHBOARD.BONUSNET" }
		}];
		
		$scope.bonusGraph = { 
			id: "bonusgraph", 
			titleKey: "UI_NETWORK_ADMIN.DASHBOARD.BONUSHISTORY",
			domains: [ bonusRevision.domain.name ],
			currency: { code: "USD", format: "$##.00" },
			accountCode: "PLAYER_BALANCE_CASINO_BONUS",
			inverse: true,
			debit: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.TRANSFERTOBONUS", tran: "TRANSFER_TO_CASINO_BONUS" },
			credit: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.TRANSFERFROMBONUS", tran: "TRANSFER_FROM_CASINO_BONUS" },
			net: { titleKey: "UI_NETWORK_ADMIN.DASHBOARD.BONUSNET" }
		};
		
		$scope.setGranularity(2);
		
		controller.imageUrl = "/services/service-games/games/"+controller.bonusRevision.domain.name+"/getImage?";
		controller.demoUrl = "/services/service-games/games/"+controller.bonusRevision.domain.name+"/demoGame?";
		
		//console.log(bonus.plain());
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
		
		controller.gameInfo = function(game, provider) {
			gamesRest.findByGameGuid(provider+"_"+game.gameId, controller.bonusRevision.domain.name).then(function(response) {
				game.gameInfo = response.plain();
			}).catch(
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
			).finally(function () {
				
			});
		}
		
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