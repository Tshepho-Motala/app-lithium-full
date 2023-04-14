'use strict';

angular.module('lithium')
	.controller('BonusEditController', ["types", "bonus", "bonusRevision", "notify", "$dt", "$filter", "$translate", "$scope", "$state", "$uibModal", "rest-casino", "rest-games", "rest-domain", "NotificationRest", "$q",
	function(types, bonus, bonusRevision, notify, $dt, $filter, $translate, $scope, $state, $uibModal, casinoRest, gamesRest, domainRest, notificationRest, $q) {
		var controller = this;
		controller.bonus = bonus;
		controller.bonusRevision = bonusRevision;
		controller.activeFreespinProviderGames = {};
		controller.activeInstantRewardProviderGames = {};
		controller.activeInstantRewardFreespinProviderGames = {};

		controller.options = {};
		controller.model = bonusRevision;
		controller.model.activeTime = {};
		controller.model.selectedfreespinprovider = '';
		controller.model.selectedInstantRewardprovider = '';
		controller.model.selectedInstantRewardFreespinprovider = '';
		controller.model.selectedunlockgameprovider = '';
		controller.model.activeTime.start = bonusRevision.activeStartTime;
		controller.model.activeTime.end = bonusRevision.activeEndTime;
		controller.model.unlockGames = {};
		controller.model.unlockGames.games = [];
		controller.triggerTypes = [];

		controller.enableFreeSpinValueInput = false;
		controller.enableFreeSpinPlayThroughEnabled = false;
		controller.gamesLoading = true;

		var triggerTranslations = [
			'GLOBAL.BONUS.TYPE.TRIGGER.'+controller.model.bonusTriggerType
		];

		$translate(triggerTranslations).then(function(translations) {
			angular.forEach(translations, function(v,k) {
				this.push({id:k.slice(-1), name:v});
			}, controller.triggerTypes);
		});

		if (angular.isDefined(controller.model.graphic) && controller.model.graphic != null) {
			controller.model.image = {};
			controller.model.image.id = controller.model.graphic.id;
			controller.model.image.version = controller.model.graphic.version;
			controller.model.image.base64 = controller.model.graphic.image;
			controller.model.image.filetype = controller.model.graphic.fileType;
		}

//		console.log(controller.bonus);
//		console.log(controller.bonusRevision);

		// Currently used to hide the free money wager req when its a trigger bonus and not a raf trigger bonus
		controller.hideWhenTriggerType = function(viewValue, modelValue, $scope) {
//			console.log('this is being called', controller.model);
			if (controller.model.bonusType === 2 &&
				(controller.model.bonusTriggerType !== 3 && controller.model.bonusTriggerType !== 0)) {
				//manual trigger type and raf trigger type can have free money wagering req
				return true;
			} else {
				return false;
			}
		}

		// Currently used to hide the free money wager req when its a trigger bonus and not a raf trigger bonus
		controller.hideWhenTokenBonusType = function(viewValue, modelValue, $scope) {
//			console.log('this is being called', controller.model);
			if (controller.model.bonusType === 3) {
				return true;
			} else {
				return false;
			}
		}

		domainRest.findByName(controller.bonusRevision.domain.name).then(function(response) {
			controller.model.domainName = response.displayName;
		}).catch(function() {
			errors.catch("", false)(error)
		});

		controller.getPercentageCategories = function() {
			controller.model.gameCategories = [];
			casinoRest.percentageCategories(bonusRevision.id).then(function(response) {
				var foundCustomCat = false;
				angular.forEach(response.plain(), function(gc) {
					foundCustomCat = true;
					casinoRest.gameCategory(gc.gameCategory).then(function(response2) {
						controller.model.gameCategories.push({
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
							gc.id = 0;
							controller.model.gameCategories.push(gc);
						});
					}).catch(function() {
						errors.catch("", false)(error)
					});
				}
			});
		}
		controller.getPercentageCategories();

		if (typeof controller.model.activeDays === 'string' && controller.model.activeDays.length !== 0) {
			var activeDays = controller.model.activeDays.split(',');
//			console.log(activeDays);
			var activeDaysMap = {};
			for (var day in activeDays) {
				activeDaysMap[activeDays[day]] = 'true';
			}
			controller.model.activeDays = activeDaysMap;
		}

		$scope.hasActiveDay = function (activeDays) {
			var hasActiveDay = false
			angular.forEach(activeDays, function(value,key) {
				if (value === 'true') hasActiveDay = true;
			});
			return hasActiveDay;
		};

		controller.imageUrl = "/services/service-games/games/"+controller.bonusRevision.domain.name+"/getImage?";
		controller.demoUrl = "/services/service-games/games/"+controller.bonusRevision.domain.name+"/demoGame?";

		if (controller.bonusRevision.bonusType == 1) {
			casinoRest.findDepositBonusRequirements(controller.bonusRevision.id).then(function(response) {
				controller.model.depositRequirements = response.plain();
			}).catch(function(error) {
				console.log("depositRequirements error : "+error);
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
			});
		}


		controller.loadInstantRewardRules = function() {
			casinoRest.findInstantRewardRules(controller.bonusRevision.id).then(function(response) {
				controller.model.instantRewardRules = response.plain();

				for (var r in controller.model.instantRewardRules) {
					var rule = controller.model.instantRewardRules[r];

					var gameInfoPromiseList = [];
					for (var g in rule.bonusRulesInstantRewardGames) {
						var game = rule.bonusRulesInstantRewardGames[g];
						gameInfoPromiseList.push(controller.gameInfoPromise(game, game.gameId, rule.provider))
					}

					for (var g in rule.bonusRulesInstantRewardGames) {
						var game = rule.bonusRulesInstantRewardGames[g];
						controller.gameInfo(game, game.gameId, rule.provider);
						controller.model.selectedInstantRewardprovider = rule.provider.replace("service-casino-provider-","");
						console.log("rule provider: ", rule.provider);
					}
				}

				if (controller.model.instantRewardRules.length === 0) {
					controller.model.instantRewardRules.push({
						id: -1,
						numberOfUnits: 1,
						instantRewardUnitValue: 0,
						volatility: 'FIXED',
						provider: "",
						bonusRulesInstantRewardGames: []
					});
				}
			}).catch(function(error) {
				console.log("findInstantRewardRules error : "+error);
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
			});
		}
		controller.loadInstantRewardRules();

		controller.loadInstantRewardFreespinRules = function() {
			casinoRest.findInstantRewardFreespinRules(controller.bonusRevision.id).then(function(response) {
				controller.model.instantRewardFreespinRules = response.plain();

				for (var r in controller.model.instantRewardFreespinRules) {
					var rule = controller.model.instantRewardFreespinRules[r];

					var gameInfoPromiseList = [];
					for (var g in rule.bonusRulesInstantRewardFreespinGames) {
						var game = rule.bonusRulesInstantRewardFreespinGames[g];
						gameInfoPromiseList.push(controller.gameInfoPromise(game, game.gameId, rule.provider))
					}

					for (var g in rule.bonusRulesInstantRewardFreespinGames) {
						var game = rule.bonusRulesInstantRewardFreespinGames[g];
						controller.gameInfo(game, game.gameId, rule.provider);
						controller.model.selectedInstantRewardFreespinprovider = rule.provider.replace("service-casino-provider-","");
						console.log("rule provider: ", rule.provider);
					}
				}

				if (controller.model.instantRewardFreespinRules.length === 0) {
					controller.model.instantRewardFreespinRules.push({
						id: -1,
						numberOfUnits: 1,
						instantRewardUnitValue: 0,
						volatility: 'FIXED',
						provider: "",
						bonusRulesInstantRewardFreespinGames: []
					});
				}
			}).catch(function(error) {
				console.log("findInstantRewardFreespinRules error : "+error);
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
			});
		}
		controller.loadInstantRewardFreespinRules();

		controller.loadCasinoChipRules = function() {
			casinoRest.findCasinoChipRules(controller.bonusRevision.id).then(function(response) {
				controller.model.casinoChipRules = response.plain();
				for (var r in controller.model.casinoChipRules) {
					var rule = controller.model.casinoChipRules[r];

					var gameInfoPromiseList = [];
					for (var g in rule.bonusRulesCasinoChipGames) {
						var game = rule.bonusRulesCasinoChipGames[g];
						gameInfoPromiseList.push(controller.gameInfoPromise(game, game.gameId, rule.provider))
					}

					for (var g in rule.bonusRulesCasinoChipGames) {
						var game = rule.bonusRulesCasinoChipGames[g];
						controller.gameInfo(game, game.gameId, rule.provider);
						controller.model.selectedCasinoChipprovider = rule.provider.replace("service-casino-provider-","");
						console.log("rule provider: ", rule.provider);
					}
				}

				if (controller.model.casinoChipRules.length === 0) {
					controller.model.casinoChipRules.push({
						id: -1,
						casinoChipValue: 0,
						provider: "",
						bonusRulesCasinoChipGames: []
					});
				}
			}).catch(function(error) {
				console.log("findCasinoChipRules error : "+error);
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
			});
		}
		controller.loadCasinoChipRules();


		controller.loadFreespinRules = function() {
			casinoRest.findFreespinsRules(controller.bonusRevision.id).then(function(response) {
				controller.model.freespinRules = response.plain();
	//			console.log(controller.model.freespinRules);
				var enableFreeSpinValue = false;
				var enableFreeSpinPlayThrough = false;
				for (var r in controller.model.freespinRules) {
					var rule = controller.model.freespinRules[r];

					var gameInfoPromiseList = [];
					for (var g in rule.bonusRulesFreespinGames) {
						var game = rule.bonusRulesFreespinGames[g];
						gameInfoPromiseList.push(controller.gameInfoPromise(game, game.gameId, rule.provider))
					}

					$q.all(gameInfoPromiseList).then(function(result) {
						for (var gameItem in result) {
							var game = result[gameItem];
							if (game.freeSpinValueRequired) {
								enableFreeSpinValue = true;
							}
							if (game.freeSpinPlayThroughEnabled) {
								enableFreeSpinPlayThrough = true;
							}
						}

						controller.enableFreeSpinValueInput = enableFreeSpinValue;
						controller.enableFreeSpinPlayThroughEnabled = enableFreeSpinPlayThrough;
					})

					for (var g in rule.bonusRulesFreespinGames) {
						var game = rule.bonusRulesFreespinGames[g];
						controller.gameInfo(game, game.gameId, rule.provider);
						controller.model.selectedfreespinprovider = rule.provider.replace("service-casino-provider-","");
	//					console.log("rule provider: ", rule.provider);
					}
				}

				if (controller.model.freespinRules.length === 0) {
	//				console.log("0 free spins");
					controller.model.freespinRules.push({
						id: -1,
						freespins: 0,
						wagerRequirements: 0,
						provider: "",
						bonusRulesFreespinGames: []
					});
				}
			}).catch(function(error) {
				console.log("findFreespinsRules error : "+error);
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
			});
		}
		controller.loadFreespinRules();

		controller.loadUnlockGameRules = function() {
			casinoRest.bonusUnlockGames(controller.bonusRevision.id).then(function(response) {
				controller.model.unlockGames.games = response.plain();
				for (var g in controller.model.unlockGames.games) {
					var game = controller.model.unlockGames.games[g];
					controller.gameInfoByGameGuid(game);
				}
			}).catch(function(error) {
				console.log("loadUnlockGameRules error : "+error);
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
			});
		}
		controller.loadUnlockGameRules();

		controller.save = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			if (controller.form.$valid) {
				var validFreespins = false;
				if (controller.model.activeDays === "") controller.model.activeDays = null;

				if (controller.model.enableFreeSpinValueInput) {
					 if (!(controller.model.freespinRules[0].freeSpinValueInCents > 0)) {
						 notify.warning("UI_NETWORK_ADMIN.BONUS.EDIT.FREESPINS.FREE_SPIN_VALUE_MISSING");
						 return false;
					 }
				}

				if ((controller.model.freespinRules[0].freespins > 0) || (controller.model.freespinRules[0].wagerRequirements > 0)) {
					if (controller.model.freespinRules[0].bonusRulesFreespinGames.length > 0) {
						validFreespins = true;
					}
				} else {
					validFreespins = true;
					controller.model.freespinRules[0].bonusRulesFreespinGames = [];
				}

				if(controller.model.instantRewardRules[0].instantRewardUnitValue > 0 && controller.model.instantRewardRules[0].numberOfUnits > 0) {
					if(controller.model.instantRewardRules[0].bonusRulesInstantRewardGames.length <= 0){
						notify.warning("UI_NETWORK_ADMIN.BONUS.EDIT.INSTANT_REWARDS.NOGAMES");
						return false;
					}
				}

				if(controller.model.instantRewardFreespinRules[0].instantRewardUnitValue > 0 && controller.model.instantRewardFreespinRules[0].numberOfUnits > 0) {
					if(controller.model.instantRewardFreespinRules[0].bonusRulesInstantRewardFreespinGames.length <= 0){
						notify.warning("UI_NETWORK_ADMIN.BONUS.EDIT.INSTANT_REWARDS_FREESPINS.NOGAMES");
						return false;
					}
				}

				if (validFreespins) {
					casinoRest.save(controller.model).then(function(response) {
						controller.getPercentageCategories();
						controller.getGamePercentages();
						controller.loadFreespinRules();
						controller.loadCasinoChipRules();
						controller.loadInstantRewardRules();
						controller.loadInstantRewardFreespinRules();
						controller.loadUnlockGameRules();
						controller.getGameList();
						$translate("UI_NETWORK_ADMIN.BONUS.EDIT.SAVE.SUCCESS").then(function success(translate) {
							notify.success(translate);
						});
					}).catch(function(error) {
						$translate("UI_NETWORK_ADMIN.BONUS.EDIT.SAVE.ERROR").then(function success(translate) {
							notify.error(translate);
						});
						//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
					});
				} else {
					notify.warning("UI_NETWORK_ADMIN.BONUS.EDIT.FREESPINS.NOGAMES");
				}
			}
		}
		controller.saveAndExit = function() {
			controller.save();
			$state.go("dashboard.bonuses.bonus.revisions", {bonusId:bonus.id});
		}

		controller.markCurrent = function(bonusRevisionId) {
			casinoRest.markBonusRevisionCurrent(bonus.id, bonusRevisionId).then(function (response) {
				$translate("UI_NETWORK_ADMIN.BONUS.EDIT.MARK.SUCCESSCURRENT", {bonusRevisionId: bonusRevisionId}).then(function success(translate) {
					notify.success(translate);
					$state.go("dashboard.bonuses.bonus.revisions", {bonusId: bonus.id});
				});
			}).catch(function () {
				$translate("UI_NETWORK_ADMIN.BONUS.EDIT.MARK.ERRORCURRENT", {bonusRevisionId: bonusRevisionId}).then(function success(translate) {
					notify.error(translate);
					$state.go("dashboard.bonuses.bonus.revisions", {bonusId: bonus.id});
				});
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
			}).finally(function () { });
		}

		controller.gameInfo = function(game, gameId, provider) {
			gamesRest.findByGameGuid(provider+"_"+gameId, controller.bonusRevision.domain.name).then(function(response) {
				game.gameInfo = response.plain();
			}).catch(
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
			).finally(function () {

			});
		}

		controller.gameInfoPromise = function(game, gameId, provider) {
			return gamesRest.findByGameGuid(provider+"_"+gameId, controller.bonusRevision.domain.name);
		}
		controller.gameInfoByGameGuid = function(game) {
			gamesRest.findByGameGuid(game.gameGuid, controller.bonusRevision.domain.name).then(function(response) {
				game.gameInfo = response.plain();
			}).catch(
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
			).finally(function () {

			});
		}

		controller.getGamePercentages = function() {
			controller.model.gamePercentages = [];
			casinoRest.bonusRulesGamesPercentages(bonusRevision.id).then(function(response) {
				var gamePercentages = response.plain();
				for (var g in gamePercentages) {
					var game = gamePercentages[g];
					game.gameId = game.gameGuid.split('/')[1];
					controller.gameInfo(game, game.gameGuid.split('/')[1], game.gameGuid.split('/')[0]);
					controller.model.gamePercentages.push(game);
				}
			}).catch(
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
			).finally(function () {
			});
		}
		controller.getGamePercentages();

		//Game Percentages
		var baseGamePercentagesUrl = "services/service-games/games/"+controller.bonusRevision.domain.name+"/listDomainGamesDT";
		controller.percentagesRowClickHandler = function (info) {
			console.log(controller.model.gamePercentages, info);
			var gamePercentages = controller.model.gamePercentages;
			if (angular.isUndefined(gamePercentages)) gamePercentages = [];
			var newGame = {
				gameId: info.providerGameId,
				gameCategory: info.labels.category.value
			}
			controller.gameInfo(newGame, newGame.gameId, info.providerGuid);

			var exists = false;
			angular.forEach(gamePercentages, function(game) {
				if (game.gameInfo.guid === info.guid) {
					exists = true;
				}
			})
			if (exists) {
				notify.warning("UI_NETWORK_ADMIN.BONUS.EDIT.GAME_PERCENTAGES.EXISTS");
			} else {
				gamePercentages.push(newGame);
			}
			console.log(gamePercentages);
		}

		controller.removeGamePercentage = function(id, index) {
			var gamePercentages = controller.model.gamePercentages;
			gamePercentages.splice(index, 1);
			console.log(id);

			if (id) {
				casinoRest.deleteBonusRulesGamesPercentage(id).then(function(response) {
					console.log(response);
				});
			}
		}

		controller.gamePercentagesList = $dt.builder()
			.column($dt.column('name').withTitle("Name"))
			.column($dt.column("labels.os.value").withTitle("OS").notSortable())
			.column($dt.column("labels.category.value").withTitle("Category").notSortable())
			.options(baseGamePercentagesUrl, controller.percentagesRowClickHandler)
			.build();

		//Freespins
//		var baseFreespinUrl = "services/service-games/games/"+controller.bonusRevision.domain.name+"/listProviderGames?providerGuid=service-casino-provider-nucleus&labelValues=Desktop,Mobile";

		//AllGames
		controller.getGameList = function() {
			gamesRest.listGamesFreeSpin(controller.bonusRevision.domain.name, null).then(function(response) {
				controller.gameListByProvider = {};
				controller.instantRewardGameListByProvider = {}
				controller.instantRewardFreespinGameListByProvider = {}
				controller.freeGameListByProvider = {};
				controller.casinoChipGameListByProvider = {}
				angular.forEach(response, function(value, key) {
					var provider = value.providerGuid.replace("service-casino-provider-","");
					if (angular.isUndefined(controller.gameListByProvider[provider])) {
						controller.gameListByProvider[provider] = [];
					}
					controller.gameListByProvider[provider].push(value);

					if (angular.isUndefined(controller.freeGameListByProvider[provider])) {
						controller.freeGameListByProvider[provider] = [];
					}
					if (value.freeSpinEnabled) {
						controller.freeGameListByProvider[provider].push(value);
					}

					if (angular.isUndefined(controller.instantRewardGameListByProvider[provider])) {
						controller.instantRewardGameListByProvider[provider] = [];
					}
					if (value.instantRewardEnabled) {
						controller.instantRewardGameListByProvider[provider].push(value);
					}

					if (angular.isUndefined(controller.instantRewardFreespinGameListByProvider[provider])) {
						controller.instantRewardFreespinGameListByProvider[provider] = [];
					}
					if (value.instantRewardFreespinEnabled) {
						controller.instantRewardFreespinGameListByProvider[provider].push(value);
					}

					if (angular.isUndefined(controller.casinoChipGameListByProvider[provider])) {
						controller.casinoChipGameListByProvider[provider] = [];
					}
					if (value.casinoChipEnabled) {
						controller.casinoChipGameListByProvider[provider].push(value);
					}
				})

				controller.providers = Object.keys(controller.gameListByProvider);
	//			console.log("Got the list: ", response);
	//			console.log("Got the hash: ", controller.gameListByProvider);
	//			console.log("Got the keys: ", Object.keys(controller.gameListByProvider));
	//			console.log("nucleus list: ", controller.gameListByProvider["nucleus"]);

				controller.availableFreespinGamesTable = $dt.builder()
				.column($dt.column('name').withTitle("Name"))
				.column($dt.column("labels.os.value").withTitle("OS").notSortable())
				.optionsLocalData(controller.freeGameListByProvider[controller.model.selectedfreespinprovider], controller.freespinRowClickHandler)
				.build();

				controller.availableUnlockGamesTable = $dt.builder()
				.column($dt.column('name').withTitle("Name"))
				.column($dt.column("labels.os.value").withTitle("OS").notSortable())
				.optionsLocalData(controller.gameListByProvider[controller.model.selectedunlockgameprovider], controller.unlockGameRowClickHandler)
				.build();


				controller.availableInstantRewardGamesTable = $dt.builder()
					.column($dt.column('name').withTitle("Name"))
					.column($dt.column("labels.os.value").withTitle("OS").notSortable())
					.optionsLocalData(controller.instantRewardGameListByProvider[controller.model.selectedInstantRewardprovider], controller.instantRewardRowClickHandler)
					.build();

				controller.availableInstantRewardFreespinGamesTable = $dt.builder()
					.column($dt.column('name').withTitle("Name"))
					.column($dt.column("labels.os.value").withTitle("OS").notSortable())
					.optionsLocalData(controller.instantRewardFreespinGameListByProvider[controller.model.selectedInstantRewardFreespinprovider], controller.instantRewardFreespinRowClickHandler)
					.build();

				controller.availableCasinoChipGamesTable = $dt.builder()
					.column($dt.column('name').withTitle("Name"))
					.column($dt.column("labels.os.value").withTitle("OS").notSortable())
					.optionsLocalData(controller.casinoChipGameListByProvider[controller.model.selectedCasinoChipprovider], controller.casinoChipRowClickHandler)
					.build();

				controller.gamesLoading = false;
			});
		}
		controller.getGameList();

		controller.changeInstantRewardProvider = function() {
			if (controller.gamesLoading) {
				return
			}
			var incomingProvider ="service-casino-provider-"+controller.model.selectedInstantRewardprovider;
			if (incomingProvider != controller.model.instantRewardRules[0].provider) {
				controller.model.instantRewardRules[0].bonusRulesInstantRewardGames = [];
				controller.model.instantRewardRules[0].provider = incomingProvider;
			}
			controller.activeInstantRewardProviderGames = controller.instantRewardGameListByProvider[controller.model.selectedInstantRewardprovider];
			controller.availableInstantRewardGamesTable.options.$$state.value.data = controller.activeInstantRewardProviderGames;
			controller.availableInstantRewardGamesTable.instance._renderer.options.data = controller.activeInstantRewardProviderGames;
			controller.availableInstantRewardGamesTable.instance._renderer.options.aaData = controller.activeInstantRewardProviderGames;
			controller.availableInstantRewardGamesTable.instance._renderer.rerender();
		}

		controller.changeUnlockGameProvider = function() {
			if (controller.gamesLoading) {
				return
			}
			var incomingProvider ="service-casino-provider-"+controller.model.selectedunlockgameprovider;
			if (incomingProvider != controller.model.unlockGames.provider) {
				controller.model.unlockGames.provider = incomingProvider;
			}
			controller.activeUnlockGameProviderGames = controller.gameListByProvider[controller.model.selectedunlockgameprovider];
//			console.log(controller.activeFreespinProviderGames);
//			console.log(controller.availableFreespinGamesTable);
			controller.availableUnlockGamesTable.options.$$state.value.data = controller.activeUnlockGameProviderGames;
			controller.availableUnlockGamesTable.instance._renderer.options.data = controller.activeUnlockGameProviderGames;
			controller.availableUnlockGamesTable.instance._renderer.options.aaData = controller.activeUnlockGameProviderGames;
			controller.availableUnlockGamesTable.instance._renderer.rerender();
//			console.log(controller.availableFreespinGamesTable);
		}

		controller.changeFreespinProvider = function() {
			if (controller.gamesLoading) {
				return
			}
			var incomingProvider ="service-casino-provider-"+controller.model.selectedfreespinprovider;
			if (incomingProvider != controller.model.freespinRules[0].provider) {
				controller.model.freespinRules[0].bonusRulesFreespinGames = [];
				controller.model.freespinRules[0].provider = incomingProvider;
			}
			controller.activeFreespinProviderGames = controller.freeGameListByProvider[controller.model.selectedfreespinprovider];
			controller.availableFreespinGamesTable.options.$$state.value.data = controller.activeFreespinProviderGames;
			controller.availableFreespinGamesTable.instance._renderer.options.data = controller.activeFreespinProviderGames;
			controller.availableFreespinGamesTable.instance._renderer.options.aaData = controller.activeFreespinProviderGames;
			controller.availableFreespinGamesTable.instance._renderer.rerender();
		}

		controller.changeInstantRewardFreespinProvider = function() {
			if (controller.gamesLoading) {
				return
			}
			var incomingProvider ="service-casino-provider-"+controller.model.selectedInstantRewardFreespinprovider;
			if (incomingProvider != controller.model.instantRewardFreespinRules[0].provider) {
				controller.model.instantRewardFreespinRules[0].bonusRulesInstantRewardFreespinGames = [];
				controller.model.instantRewardFreespinRules[0].provider = incomingProvider;
			}
			controller.activeInstantRewardFreespinProviderGames = controller.instantRewardFreespinGameListByProvider[controller.model.selectedInstantRewardFreespinprovider];
			controller.availableInstantRewardFreespinGamesTable.options.$$state.value.data = controller.activeInstantRewardFreespinProviderGames;
			controller.availableInstantRewardFreespinGamesTable.instance._renderer.options.data = controller.activeInstantRewardFreespinProviderGames;
			controller.availableInstantRewardFreespinGamesTable.instance._renderer.options.aaData = controller.activeInstantRewardFreespinProviderGames;
			controller.availableInstantRewardFreespinGamesTable.instance._renderer.rerender();
		}


		controller.changeCasinoChipProvider = function() {
			if (controller.gamesLoading) {
				return
			}
			var incomingProvider ="service-casino-provider-"+controller.model.selectedCasinoChipprovider;
			if (incomingProvider != controller.model.casinoChipRules[0].provider) {
				controller.model.casinoChipRules[0].bonusRulesCasinoChipGames = [];
				controller.model.casinoChipRules[0].provider = incomingProvider;
			}
			controller.activeCasinoChipProviderGames = controller.casinoChipGameListByProvider[controller.model.selectedCasinoChipprovider];
			controller.availableCasinoChipGamesTable.options.$$state.value.data = controller.activeCasinoChipProviderGames;
			controller.availableCasinoChipGamesTable.instance._renderer.options.data = controller.activeCasinoChipProviderGames;
			controller.availableCasinoChipGamesTable.instance._renderer.options.aaData = controller.activeCasinoChipProviderGames;
			controller.availableCasinoChipGamesTable.instance._renderer.rerender();
		}

		controller.removeGame = function(gameId, index) {
			var freeSpinRules = controller.model.freespinRules[0];
			var freeSpinGames = freeSpinRules.bonusRulesFreespinGames;
			freeSpinGames.splice(index, 1);
			var enableFreeSpinValue = false;
			var enableFreeSpinPlayThrough = false;

			for (var g in freeSpinRules.bonusRulesFreespinGames) {
				var game = freeSpinRules.bonusRulesFreespinGames[g];

				if (game.gameInfo.freeSpinValueRequired) {
					enableFreeSpinValue = true;
				}

				if (game.gameInfo.enableFreeSpinPlayThrough) {
					enableFreeSpinPlayThrough = true;
				}
			}

			controller.enableFreeSpinValueInput = enableFreeSpinValue;
			if (!enableFreeSpinValue) {
				controller.model.freespinRules[0].freeSpinValueInCents = null
			}
			controller.enableFreeSpinPlayThroughEnabled = enableFreeSpinPlayThrough;
			if (!enableFreeSpinPlayThrough) {
				controller.model.freespinRules[0].wagerRequirements = null
			}
			console.log(controller.model.freespinRules);
		}

		controller.removeInstantGame = function(id, index) {
			var instantRewards = controller.model.instantRewardRules[0];
			var instantRewardGames = instantRewards.bonusRulesInstantRewardGames;
			instantRewardGames.splice(index, 1);
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false
				console.log(controller.model.instantRewardRules);
		}

		controller.removeCasinoGame = function(id, index) {
			var casinoChips = controller.model.casinoChipRules[0];
			var casinoChipGames = casinoChips.bonusRulesCasinoChipGames;
			casinoChipGames.splice(index, 1);
			//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false
			console.log(controller.model.casinoChipRules);
		}


		controller.removeInstantFreespinGame = function(id, index) {
			var instantRewardFreespins = controller.model.instantRewardFreespinRules[0];
			var instantRewardFreespinGames = instantRewardFreespins.bonusRulesInstantRewardFreespinGames;
			instantRewardFreespinGames.splice(index, 1);
			//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false
			console.log(controller.model.instantRewardRules);
		}

		controller.removeUnlockGame = function(id, index) {
			var unlockGames = controller.model.unlockGames;
			var unlockGamesList = unlockGames.games;
			casinoRest.deleteBonusUnlockGame(id).then(function(response) {
				unlockGamesList.splice(index, 1);
			}).catch(
				//errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BONUS", false)
			).finally(function () {
				console.log(controller.model.unlockGames);
			});
		}


		controller.casinoChipRowClickHandler = function (info) {
			console.log("info: ", info);
			var chipRules = controller.model.casinoChipRules[0];
			var chipGames = chipRules.bonusRulesCasinoChipGames;
			var newGame = {
				gameId: info.providerGameId
			}
			controller.gameInfo(newGame, newGame.gameId, info.providerGuid);
			var exists = false;

			angular.forEach(chipGames, function(game) {
				if (game.gameInfo.guid === info.guid) {
					exists = true;
				}
			});

			if (exists) {
				notify.warning("UI_NETWORK_ADMIN.BONUS.EDIT.FREESPINS.EXISTS");
			} else {
				chipGames.push(newGame);
			}
		}


		controller.casinoChipRowClickHandler = function (info) {
			console.log("info: ", info);
			var chipRules = controller.model.casinoChipRules[0];
			var chipGames = chipRules.bonusRulesCasinoChipGames;
			var newGame = {
				gameId: info.providerGameId
			}
			controller.gameInfo(newGame, newGame.gameId, info.providerGuid);
			var exists = false;

			angular.forEach(chipGames, function(game) {
				if (game.gameInfo.guid === info.guid) {
					exists = true;
				}
			});

			if (exists) {
				notify.warning("UI_NETWORK_ADMIN.BONUS.EDIT.FREESPINS.EXISTS");
			} else {
				chipGames.push(newGame);
			}
		}


		controller.instantRewardRowClickHandler = function (info) {
			console.log("info: ", info);
			var instantRewardRules = controller.model.instantRewardRules[0];
			var instantRewardGames = instantRewardRules.bonusRulesInstantRewardGames;
			var newGame = {
				gameId: info.providerGameId
			}
			controller.gameInfo(newGame, newGame.gameId, info.providerGuid);
			var exists = false;

			angular.forEach(instantRewardGames, function(game) {
				if (game.gameInfo.guid === info.guid) {
					exists = true;
				}
			});

			if (exists) {
				notify.warning("UI_NETWORK_ADMIN.BONUS.EDIT.FREESPINS.EXISTS");
			} else {
				instantRewardGames.push(newGame);
			}
		}

		controller.instantRewardFreespinRowClickHandler = function (info) {
			var instantRewardFreespinRules = controller.model.instantRewardFreespinRules[0];
			var instantRewardFreespinGames = instantRewardFreespinRules.bonusRulesInstantRewardFreespinGames;
			var newGame = {
				gameId: info.providerGameId
			}
			controller.gameInfo(newGame, newGame.gameId, info.providerGuid);
			var exists = false;

			angular.forEach(instantRewardFreespinGames, function(game) {
				if (game.gameInfo.guid === info.guid) {
					exists = true;
				}
			});

			if (exists) {
				notify.warning("UI_NETWORK_ADMIN.BONUS.EDIT.FREESPINS.EXISTS");
			} else {
				instantRewardFreespinGames.push(newGame);
			}
		}

		controller.freespinRowClickHandler = function (info) {
			var freeSpinRules = controller.model.freespinRules[0];
			var freeSpinGames = freeSpinRules.bonusRulesFreespinGames;
			var newGame = {
				gameId: info.providerGameId
			}
			controller.gameInfo(newGame, newGame.gameId, info.providerGuid);
			var exists = false;
			var freeSpinValueRequired = info.freeSpinEnabled;
			var freeSpinPlayThroughEnabled = info.freeSpinPlayThroughEnabled;

			angular.forEach(freeSpinGames, function(game) {
				if (game.gameInfo.guid === info.guid) {
					exists = true;
				}
				if (game.gameInfo.freeSpinEnabled) {
					freeSpinValueRequired = true;
				}

				if (game.gameInfo.freeSpinPlayThroughEnabled) {
					freeSpinPlayThroughEnabled = true;
				}
			});

			if (exists) {
				notify.warning("UI_NETWORK_ADMIN.BONUS.EDIT.FREESPINS.EXISTS");
			} else {
				freeSpinGames.push(newGame);
			}

			controller.enableFreeSpinValueInput = freeSpinValueRequired;
			controller.enableFreeSpinPlayThroughEnabled = freeSpinPlayThroughEnabled;
		}

		controller.unlockGameRowClickHandler = function (info) {
			var unlockGames = controller.model.unlockGames;
			console.log("info: ", info, unlockGames);
			var unlockGamesList = unlockGames.games;
			var newGame = {
				gameId: info.id,
				gameGuid: info.guid
			}
			controller.gameInfoByGameGuid(newGame);
			var exists = false;
			angular.forEach(unlockGamesList, function(game) {
				if (game.gameInfo.guid === info.guid) {
					exists = true;
				}
			})
			if (exists) {
				notify.warning("UI_NETWORK_ADMIN.BONUS.EDIT.UNLOCKGAME.EXISTS");
			} else {
				unlockGamesList.push(newGame);
			}
		}

//		controller.availableFreespinGames = $dt.builder()
//			.column($dt.column('name').withTitle("Name"))
//			.column($dt.column("labels.os.value").withTitle("OS").notSortable())
//			.data(controller.gameListByProvider["nucleus"])
//			//.options(baseFreespinUrl, controller.freespinRowClickHandler)
//			.build();

	controller.rewardRulesInstantRewardFields = [{
		key: "instantRewardRules",
		type: "instantRewardGames",
		templateOptions: {
			hidebuttons: true,
			fields: [{
				key: "instantRewardUnitValue",
				type: "ui-number-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					required: false,
					decimals: 0,
					hidesep: true,
					neg: false,
					min: '',
					max: ''
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.INSTANT_REWARD_NEW.INSTANT_REWARD_UNIT_VALUE" | translate',
				},
				validators:{
					instantRewardUnitValue:{
		             expression: function(viewValue, modelValue) {
			              var value = modelValue || viewValue;
			               return value > 0;
			             },
		             message: '$viewValue + " is not a valid unit value"'
			           },
				}
			}, {
				key: "description",
				type: "examplewell",
				templateOptions: {
					label: "",
					explain: ""
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.INSTANT_REWARD_NEW.INSTANT_REWARD_UNIT_VALUE_EXAMPLE" | translate',
					'templateOptions.explain': function (viewValue, modelValue, $scope) {
						$translate("UI_NETWORK_ADMIN.BONUS.EDIT.INSTANT_REWARD_NEW.INSTANT_REWARD_UNIT_VALUE_EXPLAIN", {}).then(function success(translate) {
							$scope.options.templateOptions.explain = translate;
						});
					}
				}
			}
			// ,{
			// 	key: "numberOfUnits",
			// 	type: "ui-number-mask",
			// 	optionsTypes: ['editable'],
			// 	templateOptions : {
			// 		label: "",
			// 		required: false,
			// 		decimals: 0,
			// 		hidesep: true,
			// 		neg: false,
			// 		min: '',
			// 		max: ''
			// 	},
			// 	expressionProperties: {
			// 		'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.INSTANT_REWARD.INSTANT_REWARD_NUMBER_OF_UNITS" | translate',
			// 		'templateOptions.disabled' : function(viewValue, modelValue, scope) {
			// 			return !(controller.model.instantRewardRules[0].instantRewardUnitValue > 0.00
			// 				&& controller.model.instantRewardRules[0].bonusRulesInstantRewardGames.length > 0);
			// 		}
			// 	},
			// 	validators:{
			// 		numberOfUnits:{
			// 			expression: function(viewValue, modelValue) {
			// 				var value = modelValue || viewValue;
			// 				return value > 0;
			// 			},
			// 			message: '$viewValue + " is not a valid value for the number of units"'
			// 		},
			// 	}
			// }
			// 	,{
			// 	key: "description",
			// 	type: "examplewell",
			// 	templateOptions: {
			// 		label: "",
			// 		explain: ""
			// 	},
			// 	expressionProperties: {
			// 		'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.INSTANT_REWARD.INSTANT_REWARD_NUMBER_OF_UNITS_EXAMPLE" | translate',
			// 		'templateOptions.explain': function(viewValue, modelValue, $scope) {
			// 			$translate("UI_NETWORK_ADMIN.BONUS.EDIT.INSTANT_REWARD.INSTANT_REWARD_NUMBER_OF_UNITS_EXPLAIN", {}).then(function success(translate) {
			// 				$scope.options.templateOptions.explain = translate;
			// 			});
			// 		}
			// 	}
			// }
			]
		}
	}];


		controller.rewardRulesInstantRewardFreespinFields = [{
			key: "instantRewardFreespinRules",
			type: "instantRewardFreespinGames",
			templateOptions: {
				hidebuttons: true,
				fields: [{
					key: "instantRewardUnitValue",
					type: "ui-number-mask",
					optionsTypes: ['editable'],
					templateOptions : {
						label: "",
						required: false,
						decimals: 0,
						hidesep: true,
						neg: false,
						min: '',
						max: ''
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.INSTANT_REWARD_FREESPINS.INSTANT_REWARD_UNIT_VALUE" | translate',
					},
					validators:{
						instantRewardUnitValue:{
							expression: function(viewValue, modelValue) {
								var value = modelValue || viewValue;
								return value > 0;
							},
							message: '$viewValue + " is not a valid unit value"'
						},
					}
				},{
					key: "description",
					type: "examplewell",
					templateOptions: {
						label: "",
						explain: ""
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.INSTANT_REWARD_FREESPINS.INSTANT_REWARD_UNIT_VALUE_EXAMPLE" | translate',
						'templateOptions.explain': function(viewValue, modelValue, $scope) {
							$translate("UI_NETWORK_ADMIN.BONUS.EDIT.INSTANT_REWARD_FREESPINS.INSTANT_REWARD_UNIT_VALUE_EXPLAIN", {}).then(function success(translate) {
								$scope.options.templateOptions.explain = translate;
							});
						}
					}
				}
				// ,{
				// 	key: "numberOfUnits",
				// 	type: "ui-number-mask",
				// 	optionsTypes: ['editable'],
				// 	templateOptions : {
				// 		label: "",
				// 		required: false,
				// 		decimals: 0,
				// 		hidesep: true,
				// 		neg: false,
				// 		min: '',
				// 		max: ''
				// 	},
				// 	expressionProperties: {
				// 		'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.INSTANT_REWARD_FREESPINS.INSTANT_REWARD_NUMBER_OF_UNITS" | translate',
				// 		'templateOptions.disabled' : function(viewValue, modelValue, scope) {
				// 			return !(controller.model.instantRewardFreespinRules[0].instantRewardUnitValue > 0.00
				// 				&& controller.model.instantRewardFreespinRules[0].bonusRulesInstantRewardFreespinGames.length > 0);
				// 		}
				// 	},
				// 	validators:{
				// 		numberOfUnits:{
				// 			expression: function(viewValue, modelValue) {
				// 				var value = modelValue || viewValue;
				// 				return value > 0;
				// 			},
				// 			message: '$viewValue + " is not a valid value for the number of units"'
				// 		},
				// 	}
				// },{
				// 	key: "description",
				// 	type: "examplewell",
				// 	templateOptions: {
				// 		label: "",
				// 		explain: ""
				// 	},
				// 	expressionProperties: {
				// 		'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.INSTANT_REWARD_FREESPINS.INSTANT_REWARD_NUMBER_OF_UNITS_EXAMPLE" | translate',
				// 		'templateOptions.explain': function(viewValue, modelValue, $scope) {
				// 			$translate("UI_NETWORK_ADMIN.BONUS.EDIT.INSTANT_REWARD_FREESPINS.INSTANT_REWARD_NUMBER_OF_UNITS_EXPLAIN", {}).then(function success(translate) {
				// 				$scope.options.templateOptions.explain = translate;
				// 			});
				// 		}
				// 	}
				// }
				]
			}
		}];


		controller.bonusRulesCasinoChipFields = [{
			key: "casinoChipRules",
			type: "casinoChip",
			templateOptions: {
				hidebuttons: true,
				fields: [{
					key: "casinoChipValue",
					type: "ui-number-mask",
					optionsTypes: ['editable'],
					templateOptions : {
						label: "",
						required: false,
						decimals: 0,
						hidesep: true,
						neg: false,
						min: '',
						max: ''
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.CASINO_CHIPS.CASINO_CHIP_VALUE" | translate',
					}
				},{
					key: "description",
					type: "examplewell",
					templateOptions: {
						label: "",
						explain: ""
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.CASINO_CHIPS.CASINO_CHIP_VALUE_EXAMPLE" | translate',
						'templateOptions.explain': function(viewValue, modelValue, $scope) {
							$translate("UI_NETWORK_ADMIN.BONUS.EDIT.CASINO_CHIPS.CASINO_CHIP_VALUE_EXPLAIN", {}).then(function success(translate) {
								$scope.options.templateOptions.explain = translate;
							});
						}
					}
				}]
			}
		}];

		controller.bonusRulesFreespinsFields = [{
			key: "freespinRules",
			type: "freespinGames",
			templateOptions: {
				hidebuttons: true,
				fields: [{
					fieldGroup: [{
						key: 'freespins',
						type: 'ui-number-mask',
						optionsTypes: ['editable'],
						templateOptions : {
							label: "",
							required: true,
							decimals: 0,
							hidesep: true,
							neg: false,
							min: '',
							max: ''
						},
						expressionProperties: {
							'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.FREESPINS.FREESPINS" | translate'
						},
						controller: ['$scope', function($scope) {
						}]
					},{
						key: "freeSpinValueInCents",
						type: "ui-number-mask",
						optionsTypes: ['editable'],
						templateOptions : {
							label: "",
							required: false,
							decimals: 0,
							hidesep: true,
							neg: false,
							min: '',
							max: ''
						},
						expressionProperties: {
							'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.FREESPINS.FREE_SPIN_VALUE" | translate',
							'templateOptions.disabled' : function(viewValue, modelValue, scope) {
								return !controller.enableFreeSpinValueInput;
							}
						}
					},{
						key: "freeSpinValueInCentsExample",
						type: "examplewell",
						templateOptions: {
							label: "",
							explain: ""
						},
						expressionProperties: {
							'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.FREESPINS.FREE_SPIN_VALUE_EXAMPLE" | translate',
							'templateOptions.explain': function(viewValue, modelValue, $scope) {
								if (controller.enableFreeSpinValueInput) {
									$translate("UI_NETWORK_ADMIN.BONUS.EDIT.FREESPINS.FREE_SPIN_VALUE_ENABLED_EXPLAIN", {
									}).then(function success(translate) {
										$scope.options.templateOptions.explain = translate;
									});
								} else {
									$translate("UI_NETWORK_ADMIN.BONUS.EDIT.FREESPINS.FREE_SPIN_VALUE_DISABLED_EXPLAIN", {
									}).then(function success(translate) {
										$scope.options.templateOptions.explain = translate;
									});
								}
							}
						}
					},{
						key: "wagerRequirements",
						type: "ui-number-mask",
						optionsTypes: ['editable'],
						templateOptions : {
							label: "",
							required: false,
							decimals: 0,
							hidesep: true,
							neg: false,
							min: '',
							max: ''
						},
						expressionProperties: {
							'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.FREESPINS.WAGER" | translate',
							'templateOptions.disabled' : function(viewValue, modelValue, scope) {
								return !controller.enableFreeSpinPlayThroughEnabled;
							}
						}
					},{
						key: "freespinsExample",
						type: "examplewell",
						templateOptions: {
							label: "",
							explain: ""
						},
						expressionProperties: {
							'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.FREESPINS.EXAMPLE" | translate',
							'templateOptions.explain': function(viewValue, modelValue, $scope) {
								if (controller.enableFreeSpinPlayThroughEnabled) {
									$translate("UI_NETWORK_ADMIN.BONUS.EDIT.FREESPINS.EXPLAIN", {
										wager: $scope.model.wagerRequirements,
										freespins: $scope.model.freespins
									}).then(function success(translate) {
										$scope.options.templateOptions.explain = translate;
									});
								} else {
									$translate("UI_NETWORK_ADMIN.BONUS.EDIT.FREESPINS.FREE_SPIN_PLAY_THROUGH_DISABLED_EXPLAIN", {
									}).then(function success(translate) {
										$scope.options.templateOptions.explain = translate;
									});
								}
							}
						}
					}]
				}]
			}
		}];

		controller.triggerFields = [{
			key: "bonusTriggerType",
			type: "uib-btn-radio",
			templateOptions : {
				label : "Trigger Type",
				required : true,
				btnclass: 'default',
				showicons: false,
				optionsAttr: 'bs-options',
				description : "",
				valueProp : 'id',
				labelProp : 'name',
				optionsAttr: 'ui-options', "ngOptions": 'ui-options',
				placeholder : '',
				options : controller.triggerTypes,
				disabled: true
			},
			hideExpression: function($viewValue, $modelValue, scope) {
				if (controller.model.bonusType === 2) return false;
				return true;
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.ADD.BASIC.TRIGGERTYPE.LABEL" | translate'
			},
			controller: ['$scope', function($scope) {
//				$scope.to.options = controller.triggerTypes;
					console.log(controller.model.bonusTriggerType);
				}]
			},{
				key : "triggerTypeAny",
				type : "checkbox2",
				templateOptions : {
					label : "",
					required : true,
					description : "",
					fontWeight: 'bold'
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					if ((controller.model.bonusType === 2) && (controller.model.bonusTriggerType === 1)) return false;
					return true;
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.TRIGGER.ANY" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.TRIGGER.ANYEXPLAIN" | translate'
				},
				controller: ['$scope', function($scope) {
					$scope.model.triggerTypeAny = true;
				}]
			},{
				key: "triggerAmount",
				type: "ui-number-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					required: true,
					decimals: 0,
					hidesep: true,
					neg: false,
					min: '1',
					max: ''
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					if (((controller.model.bonusTriggerType === 1) && (!controller.model.triggerTypeAny)) || (controller.model.bonusTriggerType === 2) || (controller.model.bonusTriggerType === 6)) return false;
					return true;
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.TRIGGERAMOUNT.LABEL" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.TRIGGERAMOUNT.DESCRIPTION" | translate'
				}
			},{
				key: "triggerAmount",
				type: "ui-number-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					required: true,
					decimals: 0,
					hidesep: true,
					neg: false,
					min: 2,
					max: ''
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					if (controller.model.bonusTriggerType === 9) return false;
					return true;
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.HOURLY.LABEL" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.HOURLY.DESCRIPTION" | translate'
				}
			},{
				key: "triggerGranularity",
				type: "granularityfor",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					required: false,
					showicons: true
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					if (((controller.model.bonusTriggerType === 1) && (!controller.model.triggerTypeAny)) || (controller.model.bonusTriggerType === 2)) return false;
					return true;
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.TRIGGERGRANULARITY.LABEL" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.TRIGGERGRANULARITY.DESCRIPTION" | translate'
				},
				controller: ['$scope', function($scope) {
					$scope.model.triggerGranularity = 3;
				}]
			},{
				key: "triggerExample",
				type: "examplewell",
				templateOptions: {
					label: "",
					explain: ""
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					if (((controller.model.bonusTriggerType === 1) && (!controller.model.triggerTypeAny)) || (controller.model.bonusTriggerType === 2)) return false;
					return true;
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EXAMPLE.LABEL" | translate',
					'templateOptions.explain': function(viewValue, modelValue, $scope) {
						if ((controller.model.bonusTriggerType === 1) || (controller.model.bonusTriggerType === 2)) {
							$translate("UI_NETWORK_ADMIN.BONUS.TRIGGER.EXPLAIN."+controller.model.triggerGranularity, {
								type: (controller.triggerTypes[controller.model.bonusTriggerType].name).toLowerCase(),
								amount: controller.model.triggerAmount
							}).then(function success(translate) {
								$scope.options.templateOptions.explain = translate;
							});
						} else {
							$scope.options.templateOptions.explain = '';
						}
					}
				}
			}];

			controller.basicFields = [{
				key : "domainName",
				type : "input",
				templateOptions : {
					label : "Domain",
					description : "",
					disabled: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.BASIC.DOMAIN.LABEL" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.BONUS.EDIT.BASIC.DOMAIN.PLACEHOLDER" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.EDIT.BASIC.DOMAIN.DESCRIPTION" | translate'
				}
			},{
				key: "bonusType",
				type: "uib-btn-radio",
				templateOptions : {
					label : "Type",
					required : true,
					btnclass: 'default',
					showicons: true,
					optionsAttr: 'bs-options',
					description : "",
					valueProp : 'id',
					labelProp : 'name',
					optionsAttr: 'ui-options', "ngOptions": 'ui-options',
					placeholder : '',
					options : [],
					disabled: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.BASIC.TYPE.LABEL" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.BONUS.EDIT.BASIC.TYPE.PLACEHOLDER" | translate'
//				'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.EDIT.BASIC.TYPE.DESCRIPTION" | translate'
				},
				controller: ['$scope', function($scope) {
					$scope.to.options = types;
				}]
			},{
				key: "bonusCode",
				type: "input",
				templateOptions: {
					label: "Code", description: "", placeholder: "", required: false,
					minlength: 2, maxlength: 35, disabled: "disabled"
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.BASIC.CODE.LABEL" | translate',
					'templateOptions.placeholder': function(viewValue, modelValue, $scope) {
						if ($scope.originalModel.bonusCode === '') {
							return '';
						} else {
							$translate("UI_NETWORK_ADMIN.BONUS.EDIT.BASIC.CODE.PLACEHOLDER").then(function success(translate) {
								return translate;
							});
						}
					},
					'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.EDIT.BASIC.CODE.DESCRIPTION" | translate'
				}
			},{
				key: "bonusName",
				type: "input",
				templateOptions: {
					label: "Name", description: "", placeholder: "", required: true,
					minlength: 2, maxlength: 35
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.BASIC.NAME.LABEL" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.BONUS.EDIT.BASIC.NAME.PLACEHOLDER" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.EDIT.BASIC.NAME.DESCRIPTION" | translate'
				}
			},{
				key: "forDepositNumber",
				type: "ui-number-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					required: false,
					decimals: 0,
					hidesep: true,
					neg: false,
					min: '1',
//				minlength: 1,
					max: ''
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					if (controller.model.bonusType === 1) return false;
					return true;
				},
//			validators: {
//				pattern: {
//					expression: function($viewValue, $modelValue, scope) {
//						console.log($viewValue, $modelValue, /^[1-9]+$/.test($viewValue));
//						return /^[1-9]+$/.test($viewValue);
//					},
//					message: '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.PATTERN" | translate'
//				}
//			},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.BASIC.FORDEPOSITNUMBER.LABEL" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.BONUS.EDIT.BASIC.FORDEPOSITNUMBER.PLACEHOLDER" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.EDIT.BASIC.FORDEPOSITNUMBER.DESCRIPTION" | translate'
				}
			},
				{
					key : "bonusDescription",
					type : "textarea",
					templateOptions : {
						label : "",
						required : true,
						description : "",
						fontWeight: 'bold'
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.DESCRIPTION.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.EDIT.DESCRIPTION.DESCRIPTION" | translate'
					}
				},
				{
					key: 'maxPayout',
					type: 'ui-money-mask',
					optionsTypes: ['editable'],
					templateOptions : {
						label: "",
						description: "",
						required: false
					},
					hideExpression: function($viewValue, $modelValue, scope) {
						return controller.hideWhenTokenBonusType($viewValue, $modelValue, scope);
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.BASIC.MAXPAYOUT.LABEL" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.BONUS.EDIT.BASIC.MAXPAYOUT.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.EDIT.BASIC.MAXPAYOUT.DESCRIPTION" | translate'
					},
					controller: ['$scope', function($scope) {
						if (angular.isUndefined($scope.model.maxPayoutDiv)) {
							$scope.model.maxPayoutDiv = true;
							$scope.model.maxPayout = $scope.model.maxPayout/100;
						}
//				$scope.originalModel.minDeposit = $scope.originalModel.minDeposit/100;
					}]
				},
				{
					key : "publicView",
					type : "checkbox2",
					templateOptions : {
						label : "",
						required : true,
						description : "",
						fontWeight: 'bold'
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.VISIBLETOPUBLIC" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.EDIT.VISIBLETOPUBLICEXPLAIN" | translate'
					},
					hideExpression: function($viewValue, $modelValue, scope) {
						return controller.hideWhenTokenBonusType($viewValue, $modelValue, scope);
					}
				},
				{
					"className":"col-xs-12",
					"type":"image-upload",
					"key":"image",
					"templateOptions":{
						"type":"",
						"label":"",
						"required":false,
						"description":"",
						"maxsize": 500,  //Maximum file size in kilobytes (KB)
						"minsize": 1,
						"accept": "image/*",
						"preview": true
					},
					hideExpression: function($viewValue, $modelValue, scope) {
						return controller.hideWhenTokenBonusType($viewValue, $modelValue, scope);
					},
					"expressionProperties": {
						'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.GRAPHIC.NAME" | translate',
						'templateOptions.description': '' //'"UI_NETWORK_ADMIN.CASHIER.METHODS.FIELDS.IMAGE.DESCRIPTION" | translate'
					}
				}
			];

			controller.notificationFields = [
				{
					className: "col-xs-12",
					key: "activationNotificationName",
					type: "ui-select-single",
					templateOptions: {
						label: "",
						description: "",
						placeholder: "",
						optionsAttr: 'bs-options',
						valueProp : 'name',
						labelProp : 'name',
						optionsAttr: 'ui-options',
						ngOptions: 'ui-options',
						options: [],
						appendToBody: true
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.NOTIFICATIONS.ACTIVATION.NAME" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.NOTIFICATIONS.ACTIVATION.DESCRIPTION" | translate'
					},
					controller: ['$scope', '$http', function($scope, $http) {
						notificationRest.findByDomainName(controller.bonusRevision.domain.name).then(function(response) {
							$scope.to.options = response.plain();
						});
					}]
				}
			];

//		controller.model.dependsOnBonus = [];
//		console.log(bonus);
//		console.log(controller.bonusRevision);
//		if (controller.bonusRevision.dependsOnBonus !== null) {
//			controller.model.dependsOnBonus = controller.bonusRevision.dependsOnBonus;
//
//			//model should be different...dependsOnBonus
//		}
			if ((controller.model.dependsOnBonus) && (controller.model.dependsOnBonus.current)) {
				var bonusId = controller.model.dependsOnBonus.id;
				controller.model.dependsOnBonus = controller.model.dependsOnBonus.current;
				controller.model.dependsOnBonus.bonusId = bonusId;
			}
//		console.log(controller.model.dependsOnBonus);
			controller.dependencyFields = [{
				key: "dependsOnBonus",
				type: "uib-typeahead",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					description: "",
					placeholder: "",
					required: false,
					disabled: false,
					valueProp: 'id',
					labelProp: 'bonusCode',
					displayProp: 'bonusName',
					displayOnly: false
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.DEPENDENCY.LABEL" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.BONUS.EDIT.DEPENDENCY.PLACEHOLDER" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.EDIT.DEPENDENCY.DESCRIPTION" | translate'
				},
				controller: ['$scope', function($scope) {
					$scope.searchTypeAhead = function(searchValue) {
						var search = casinoRest.search(controller.bonusRevision.bonusType, searchValue).then(function(response) {
							console.log(response.plain());
							var currentlist = [];
							angular.forEach(response.plain(), function(value) {
								console.log(value);
								if (value.id !== bonus.id) {
									var current = value.current;
									current.bonusId = value.id;
									this.push(current);
								}
							}, currentlist);

							$scope.options.templateOptions.options = currentlist;
							return currentlist;
						});
						console.log(search);
						$scope.loadingLocations = null;
						return search;
					}
					$scope.resetTypeAhead = function() {
						$scope.loadingLocations = null;
						$scope.model.dependsOnBonus = null;
					}
					$scope.selectTypeAhead = function($item, $model, $label, $event) {
						console.log($item);
						$scope.model.dependsOnBonus = $item;
						$scope.model.dependsOnBonus.bonusId = $item.bonusId;
					}
				}]
			}];

			controller.restrictionFields = [{
				key: "startingDate",
				type: "datepicker",
				optionsTypes: ['editable'],
				templateOptions: {
					label: "",
					required: false,
					datepickerOptions: {
						format: 'dd.MM.yyyy HH:mm:ss'
					}
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.RESTRICTION.STARTINGDATE.LABEL" | translate'
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					return controller.hideWhenTokenBonusType($viewValue, $modelValue, scope);
				}
			},{
				key: "expirationDate",
				type: "datepicker",
				optionsTypes: ['editable'],
				templateOptions: {
					label: "",
					required: false,
					datepickerOptions: {
						format: 'dd.MM.yyyy HH:mm:ss'
					}
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.RESTRICTION.EXPIRATIONDATE.LABEL" | translate'
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					return controller.hideWhenTokenBonusType($viewValue, $modelValue, scope);
				}
			},{
				key: "timezone",
				type: "timezone-selector",
				optionsTypes: ['editable'],
				templateOptions: {
					label: "",
					required: false
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.RESTRICTION.TIMEZONE.LABEL" | translate'
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					return controller.hideWhenTokenBonusType($viewValue, $modelValue, scope);
				}
			},{
				key: "validDays",
				type: "ui-number-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					required: false,
					decimals: 0,
					hidesep: true,
					neg: false,
					min: '',
					max: ''
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.RESTRICTION.VALIDFOR.LABEL" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.EDIT.RESTRICTION.VALIDFOR.DESCRIPTION" | translate'
				}
			},{
				key: "maxRedeemable",
				type: "ui-number-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					required: true,
					decimals: 0,
					hidesep: true,
					neg: false,
					min: '1',
					max: ''
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					return controller.hideWhenTokenBonusType($viewValue, $modelValue, scope);
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.RESTRICTION.MAXREDEEMABLE.LABEL" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.EDIT.RESTRICTION.MAXREDEEMABLE.DESCRIPTION" | translate'
				}
			},{
				key: "maxRedeemableGranularity",
				type: "granularity",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					required: false
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					return controller.hideWhenTokenBonusType($viewValue, $modelValue, scope);
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.RESTRICTION.MAXREDEEMABLEGRANULARITY.LABEL" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.EDIT.RESTRICTION.MAXREDEEMABLEGRANULARITY.DESCRIPTION" | translate'
				}
			},{
				key: "activeDays",
				type: "weekday",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					required: false
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.RESTRICTION.ACTIVEDAYS.LABEL" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.EDIT.RESTRICTION.ACTIVEDAYS.DESCRIPTION" | translate'
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					return controller.hideWhenTokenBonusType($viewValue, $modelValue, scope);
				}
			},{
				key: "activeTime",
				type: "timerange",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					required: false
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.RESTRICTION.ACTIVETIMES.LABEL" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.EDIT.RESTRICTION.ACTIVETIMES.DESCRIPTION" | translate'
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					return controller.hideWhenTokenBonusType($viewValue, $modelValue, scope);
				}
			}];

			controller.freeMoneyFields = [{
				key: 'freeMoneyAmount',
				type: 'ui-money-mask',
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					required: false
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.FREEMONEY.AMOUNT" | translate'
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					return controller.hideWhenTokenBonusType($viewValue, $modelValue, scope);
				},
				controller: ['$scope', function($scope) {
					if ($scope.originalModel.freeMoneyAmount === null) $scope.originalModel.freeMoneyAmount = 0;
					if ($scope.originalModel.freeMoneyWagerRequirement === null) $scope.originalModel.freeMoneyWagerRequirement = 0;
					if (angular.isUndefined($scope.originalModel.freeMoneyAmountDiv)) {
						$scope.originalModel.freeMoneyAmountDiv = true;
						$scope.originalModel.freeMoneyAmount = $scope.originalModel.freeMoneyAmount/100;
					}
				}]
			},{
				key: "freeMoneyWagerRequirement",
				type: "ui-number-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					required: false,
					decimals: 0,
					hidesep: true,
					neg: false,
					min: '',
					max: '',
					hidden: true,
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.FREEMONEY.WAGER" | translate'
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					return (controller.hideWhenTriggerType($viewValue, $modelValue, scope) || controller.hideWhenTokenBonusType($viewValue, $modelValue, scope));
				}
			},{
				key: "freeMoneyExample",
				type: "examplewell",
				templateOptions: {
					label: "",
					explain: "",
					hidden: true,
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.FREEMONEY.EXAMPLE" | translate',
					'templateOptions.explain': function(viewValue, modelValue, $scope) {
						if (($scope.model.freeMoneyAmount > 0) && ($scope.model.freeMoneyWagerRequirement > 0)) {
							$translate("UI_NETWORK_ADMIN.BONUS.FREEMONEY.EXPLAIN", {
								amount: (Math.round($scope.model.freeMoneyAmount*100)),
								wager: (Math.round($scope.model.freeMoneyAmount*100)*$scope.model.freeMoneyWagerRequirement)
							}).then(function success(translate) {
								$scope.options.templateOptions.explain = translate;
							});
						} else {
							$scope.options.templateOptions.explain = '';
						}
					}
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					return (controller.hideWhenTriggerType($viewValue, $modelValue, scope) || controller.hideWhenTokenBonusType($viewValue, $modelValue, scope));
				}
			}];

			controller.cancelRulesFields = [{
				key : "playerMayCancel",
				type : "checkbox2",
				templateOptions : {
					label : "",
					required : true,
					description : "",
					fontWeight: 'bold'
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.CANCEL.PLAYERMAYCANCEL" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.CANCEL.PLAYERMAYCANCELEXPLAIN" | translate'
				},
				hideExpression: function($viewValue, $modelValue, scope) {
					return controller.hideWhenTokenBonusType($viewValue, $modelValue, scope);
				}
			},{
				key: 'cancelOnDepositMinimumAmount',
				type: 'ui-money-mask',
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					description: "",
					required: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.CANCEL.MINAMOUNT" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.CANCEL.MINAMOUNTEXPLAIN" | translate'
				},
				controller: ['$scope', function($scope) {
					if (angular.isUndefined($scope.originalModel.cancelOnDepositMinimumAmountDiv)) {
						$scope.originalModel.cancelOnDepositMinimumAmountDiv = true;
						$scope.originalModel.cancelOnDepositMinimumAmount = $scope.originalModel.cancelOnDepositMinimumAmount/100;
					}
				}],
				hideExpression: function($viewValue, $modelValue, scope) {
					return controller.hideWhenTokenBonusType($viewValue, $modelValue, scope);
				}
			},{
				key : "cancelOnBetBiggerThanBalance",
				type : "checkbox2",
				defaultValue: true,
				templateOptions : {
					label : "",
					required : true,
					description : "",
					fontWeight: 'bold',
					disabled: true,
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.CANCEL.CANCELONBETBIGGERTHANBALANCE" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.CANCEL.CANCELONBETBIGGERTHANBALANCEEXPLAIN" | translate'
				},
				controller: ['$scope', function($scope) {
					$scope.originalModel.cancelOnBetBiggerThanBalance = true;
				}],
				hideExpression: function($viewValue, $modelValue, scope) {
					return controller.hideWhenTokenBonusType($viewValue, $modelValue, scope);
				}
			}];

			controller.requirementFields = [{
				key: "depositRequirements",
				type: "depositRequirements",
				templateOptions: {
					fields: [{
						fieldGroup: [{
							key: 'minDeposit',
							type: 'ui-money-mask',
							optionsTypes: ['editable'],
							templateOptions : {
								label: "",
								description: "",
								required: true
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.DEPOSIT.MIN.LABEL" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.BONUS.EDIT.DEPOSIT.MIN.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.EDIT.DEPOSIT.MIN.DESCRIPTION" | translate'
							},
							controller: ['$scope', function($scope) {
								if (angular.isUndefined($scope.originalModel.minDepositDiv)) {
									$scope.originalModel.minDepositDiv = true;
									$scope.originalModel.minDeposit = $scope.originalModel.minDeposit/100;
								}
							}]
						},{
							key: 'maxDeposit',
							type: 'ui-money-mask',
							optionsTypes: ['editable'],
							templateOptions : {
								label: "",
								description: "",
								required: false
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.DEPOSIT.MAX.LABEL" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.BONUS.EDIT.DEPOSIT.MAX.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.EDIT.DEPOSIT.MAX.DESCRIPTION" | translate'
							},
							controller: ['$scope', function($scope) {
								if (angular.isUndefined($scope.originalModel.maxDepositDiv)) {
									$scope.originalModel.maxDepositDiv = true;
									$scope.originalModel.maxDeposit = $scope.originalModel.maxDeposit/100;
								}
							}]
						},{
							key: "bonusPercentage",
							type: "ui-percentage-mask",
							optionsTypes: ['editable'],
							templateOptions : {
								label: "",
								description: "",
								required: false,
								hidesep: true,
								decimals: 0
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.DEPOSIT.PERCENTAGE.LABEL" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.BONUS.EDIT.DEPOSIT.PERCENTAGE.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.EDIT.DEPOSIT.PERCENTAGE.DESCRIPTION" | translate'
							}
						},{
							key: "wagerRequirements",
							type: "ui-number-mask",
							optionsTypes: ['editable'],
							templateOptions : {
								label: "",
								description: "",
								required: true,
								decimals: 0,
								hidesep: true,
								neg: false,
								min: 1,
								max: ''
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.DEPOSIT.WAGER.LABEL" | translate',
								'templateOptions.placeholder': '"UI_NETWORK_ADMIN.BONUS.EDIT.DEPOSIT.WAGER.PLACEHOLDER" | translate',
								'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.EDIT.DEPOSIT.WAGER.DESCRIPTION" | translate'
							}
						},{
							key: "example",
							type: "examplewell",
							templateOptions: {
								label: "",
								explain: ""
							},
							expressionProperties: {
								'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.EDIT.DEPOSIT.EXAMPLE" | translate',
								'templateOptions.explain': function(viewValue, modelValue, $scope) {
									if ($scope.model.minDeposit && !$scope.model.bonusPercentage && $scope.model.wagerRequirements) {
										$translate("UI_NETWORK_ADMIN.BONUS.EDIT.DEPOSIT.EXPLAIN1", {minDeposit: $scope.model.minDeposit, wager: $scope.model.wagerRequirements, wagerTotal: ($scope.model.minDeposit*$scope.model.wagerRequirements) }).then(function success(translate) {
											$scope.options.templateOptions.explain = translate;
										});
									} else if ($scope.model.minDeposit && $scope.model.bonusPercentage && $scope.model.wagerRequirements) {
										$translate("UI_NETWORK_ADMIN.BONUS.EDIT.DEPOSIT.EXPLAIN2", {
											minDeposit: $scope.model.minDeposit,
											bonusAmount: ($scope.model.minDeposit*($scope.model.bonusPercentage/100)),
											bonusTotal: (($scope.model.minDeposit*($scope.model.bonusPercentage/100))+$scope.model.minDeposit),
											wager: $scope.model.wagerRequirements,
											wagerTotal: ((($scope.model.minDeposit*($scope.model.bonusPercentage/100))+$scope.model.minDeposit)*$scope.model.wagerRequirements)
										}).then(function success(translate) {
											$scope.options.templateOptions.explain = translate;
										});
									}
								}
							}
						}]
					}]
				}
			}];

			controller.addFreeMoney = function() {
				var modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					templateUrl: 'scripts/controllers/dashboard/bonuses/bonus/edit/addfreemoney.html',
					controller: 'BonusFreeMoneyAddController',
					controllerAs: 'controller',
					size: 'md',
					resolve: {
						domainName: function() { return controller.bonusRevision.domain.name; },
						loadMyFiles: function($ocLazyLoad) {
							return $ocLazyLoad.load({
								name:'lithium',
								files: ['scripts/controllers/dashboard/bonuses/bonus/edit/addfreemoney.js']
							})
						}
					}
				});

				modalInstance.result.then(function(freeMoney) {
					console.log("freemoney result ", freeMoney);
					if (controller.bonusRevision.bonusFreeMoney === undefined) controller.bonusRevision.bonusFreeMoney = [];
					freeMoney.amount = freeMoney.amount * 100;
					controller.bonusRevision.bonusFreeMoney.push(freeMoney);
				});
			}

			controller.removeAdditionalFreeMoney = function(item) {
				var index = controller.bonusRevision.bonusFreeMoney.indexOf(item);
				controller.bonusRevision.bonusFreeMoney.splice(index, 1);
			}

			controller.addBonusToken = function() {
				var modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					templateUrl: 'scripts/controllers/dashboard/bonuses/bonus/edit/addbonustoken.html',
					controller: 'BonusTokenAddController',
					controllerAs: 'controller',
					size: 'md',
					resolve: {
						domainName: function() { return controller.bonusRevision.domain.name; },
						loadMyFiles: function($ocLazyLoad) {
							return $ocLazyLoad.load({
								name:'lithium',
								files: ['scripts/controllers/dashboard/bonuses/bonus/edit/addbonustoken.js']
							})
						}
					}
				});

				modalInstance.result.then(function(bonustoken) {
					console.log("bonustoken result ", bonustoken);
					if (controller.bonusRevision.bonusTokens === undefined) controller.bonusRevision.bonusTokens = [];
					bonustoken.amount = bonustoken.amount * 100;
					controller.bonusRevision.bonusTokens.push(bonustoken);
				});
			}

			controller.removeBonusToken = function(item) {
				var index = controller.bonusRevision.bonusTokens.indexOf(item);
				controller.bonusRevision.bonusTokens.splice(index, 1);
			}

			controller.addExternalBonusGame = function() {
				console.log("got to the click for external bonus game");
				var modalInstance = $uibModal.open({
					animation: true,
					ariaLabelledBy: 'modal-title',
					ariaDescribedBy: 'modal-body',
					templateUrl: 'scripts/controllers/dashboard/bonuses/bonus/edit/addexternalbonusgame.html',
					controller: 'ExternalBonusGameAddController',
					controllerAs: 'controller',
					size: 'md',
					resolve: {
						loadMyFiles: function($ocLazyLoad) {
							return $ocLazyLoad.load({
								name:'lithium',
								files: ['scripts/controllers/dashboard/bonuses/bonus/edit/addexternalbonusgame.js']
							})
						}
					}
				});

				modalInstance.result.then(function(externalBonusGame) {
					if (controller.bonusRevision.bonusExternalGameConfigs === undefined) controller.bonusRevision.bonusExternalGameConfigs = [];
					controller.bonusRevision.bonusExternalGameConfigs.push(externalBonusGame);
				});
			}

			controller.removeExternalBonusGame = function(item) {
				var index = controller.bonusRevision.bonusExternalGameConfigs.indexOf(item);
				controller.bonusRevision.bonusExternalGameConfigs.splice(index, 1);
			}
		}
	]);
