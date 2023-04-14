'use strict';

angular.module('lithium')
	.controller('BonusViewGameCategoriesController', ["bonus", "bonusRevision", "notify", "$state", "$translate", "$scope", "rest-casino", "rest-games",
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
		
		controller.getPercentageCategories = function() {
			controller.gameCategories = [];
			casinoRest.percentageCategories(bonusRevision.id).then(function(response) {
				var foundCustomCat = false;
				angular.forEach(response.plain(), function(gc) {
					foundCustomCat = true;
					casinoRest.gameCategory(gc.gameCategory).then(function(response2) {
						controller.gameCategories.push({
							percentage: gc.percentage,
							displayName: response2.displayName,
							id: gc.id,
							casinoCategory: gc.gameCategory
						});
					});
				});
				if (foundCustomCat === false) {
					casinoRest.gameCategories().then(function(response) {
						angular.forEach(response.plain(), function(gc) {
							gc.percentage = 100;
							controller.gameCategories.push(gc);
						});
					}).catch(function() {
						errors.catch("", false)(error)
					});
				}
			});
		}
		controller.getPercentageCategories();
	}
]);