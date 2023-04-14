'use strict';

angular.module('lithium')
	.controller('BonusController', ["bonus", "notify", "$scope", "$state", "bsLoadingOverlayService", "$translate", "rest-casino",
	function(bonus, notify, $scope, $state, bsLoadingOverlayService, $translate, casinoRest) {
		var controller = this;
		controller.bonus = bonus;
		controller.domainName = '';
		if (bonus.current) {
			controller.domainName = bonus.current.domain.name;
		} else if (bonus.edit) {
			controller.domainName = bonus.edit.domain.name;
		}
		
//		$translate('UI_NETWORK_ADMIN.BONUS.VIEW.TITLE', { bonusCode:''+bonus.current.bonusCode }).then(function (translations) {
//			$scope.$$childTail.title = translations;
//		});
//		$translate('UI_NETWORK_ADMIN.BONUS.VIEW.DESCRIPTION', { bonusName:''+bonus.current.bonusName }).then(function (translations) {
//			$scope.$$childTail.description = translations;
//		});
		
		controller.tabs = [
			{ name: "dashboard.bonuses.bonus.view", title: "UI_NETWORK_ADMIN.BONUS.TAB.CURRENT.TAB", roles: "BONUS_VIEW" },
			{ name: "dashboard.bonuses.bonus.revisions", title: "UI_NETWORK_ADMIN.BONUS.TAB.REVISIONS.TAB", roles: "BONUS_VIEW" },
			{ name: "dashboard.bonuses.bonus.dashboard", title: "UI_NETWORK_ADMIN.BONUS.TAB.DASHBOARD", roles: "BONUS_DASHBOARD", tclass: "disabled" },
			{ name: "dashboard.bonuses.bonus.activation", title: "UI_NETWORK_ADMIN.BONUS.TAB.ACTIVATION", roles: "BONUS_VIEW" },
			{ name: "dashboard.bonuses.bonus.pending", title: "Pending Player Bonuses", roles: "BONUS_VIEW" },
			{ name: "dashboard.bonuses.bonus.csvbonusallocation", title: "Csv Allocation List", roles: "BONUS_VIEW" }
		];
		
		if (bonus.current !== null) {
			console.log("bonus type: ", bonus);
			controller.tabsCurrent = [
				{
					name: "dashboard.bonuses.bonus.view.summary",
					title: "UI_NETWORK_ADMIN.BONUS.TAB.CURRENT.SUMMARY",
					roles: "BONUS_VIEW"
				}
			];
			if (bonus.current.bonusType !== 3) {
				controller.tabsCurrent.push({
					name: "dashboard.bonuses.bonus.view.unlockgames",
					title: "UI_NETWORK_ADMIN.BONUS.TAB.CURRENT.UNLOCKGAMES",
					roles: "BONUS_VIEW"
				});
				controller.tabsCurrent.push({
					name: "dashboard.bonuses.bonus.view.gamepercentages",
					title: "UI_NETWORK_ADMIN.BONUS.TAB.CURRENT.GAME_PERCENTAGES",
					roles: "BONUS_VIEW"
				});
				controller.tabsCurrent.push({
					name: "dashboard.bonuses.bonus.view.gamecategories",
					title: "UI_NETWORK_ADMIN.BONUS.TAB.CURRENT.GAME_CAT_PERCENTAGES",
					roles: "BONUS_VIEW"
				});
			}
		}
		
		controller.setTab = function(tab) {
			if (tab.tclass !== 'disabled') {
				var bonusRevisionId = $state.params.bonusRevisionId;
				if (angular.isUndefined($state.params.bonusRevisionId)) {
					if (bonus.current === null) {
						bonusRevisionId = bonus.edit.id;
					} else {
						bonusRevisionId = bonus.current.id;
					}
				}
				$state.go(tab.name, {bonusId:bonus.id, bonusRevisionId:bonusRevisionId});
			}
		}
		
		controller.modify = function() {
			if (bonus.edit === null) {
				casinoRest.findBonusById(bonus.id).then(function(response) {
					var freshBonus = response.plain();
					bonus = freshBonus;
					if (freshBonus.edit === null) {
						casinoRest.copyBonusRevision(freshBonus.current.id).then(function(response) {
							$state.go('dashboard.bonuses.edit', {bonusId:bonus.id, bonusRevisionId:response.edit.id});
						}).catch(function() {
							errors.catch("", false)(error)
						});
					} else {
						$state.go('dashboard.bonuses.edit', {bonusId:bonus.id, bonusRevisionId:freshBonus.edit.id});
					}
				}).catch(function(error) {
					errors.catch("", false)(error)
				});
			} else {
				$state.go('dashboard.bonuses.edit', {bonusId:bonus.id, bonusRevisionId:bonus.edit.id});
			}
		}
		
		controller.enable = function(enabled) {
			casinoRest.markBonusRevisionEnabled(bonus.current.id, enabled).then(function(response) {
				if (enabled === true) {
					$translate("UI_NETWORK_ADMIN.BONUS.EDIT.MARK.SUCCESSENABLE").then(function success(translate) {
						notify.success(translate);
						$state.go('dashboard.bonuses');
					});
				} else {
					$translate("UI_NETWORK_ADMIN.BONUS.EDIT.MARK.SUCCESSDISABLE").then(function success(translate) {
						notify.success(translate);
						$state.go('dashboard.bonuses');
					});
				}
			}).catch(function() {
				if (enabled === "true") {
					$translate("UI_NETWORK_ADMIN.BONUS.EDIT.MARK.ERRORENABLE").then(function success(translate) {
						notify.error(translate);
						$state.go('dashboard.bonuses');
					});
				} else {
					$translate("UI_NETWORK_ADMIN.BONUS.EDIT.MARK.ERRORDISABLE").then(function success(translate) {
						notify.error(translate);
						$state.go('dashboard.bonuses');
					});
				}
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
			}).finally(function () {
				
			});
		}
	}
]);
