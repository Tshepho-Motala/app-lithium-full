'use strict';

angular.module('lithium-rest-casino', ['restangular'])
.factory('rest-casino', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			var casinoService = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-casino');
			});

			var searchService = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl("services/service-casino-search/backoffice/bethistory");
			});

			var casinoProviderService = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl("services/service-casino-provider-roxor");
			});

			service.getActiveCashBonusTypes = function(domainName) {
				return casinoService.all("backoffice").all(domainName).all("bonus").all("cash-bonus").all("find").one('active').get();
			}

			service.save = function(bonus) {
				return casinoService.all("casino").all("bonus").post(bonus);
			}

			service.changelogs = function(domainName, entityId, page) {
				return casinoService.all("casino").all("bonus").one(entityId+'').one("changelogs").get({ p: page });
			}
			
			service.search = function(bonusType, search) {
				return casinoService.all("casino").all("bonus").all("search").one(bonusType+'').one(search).getList();
			}
			
			service.gameCategories = function() {
				return casinoService.all("casino").all("bonus").all("find").all("game").all("categories").getList();
			}
			service.gameCategory = function(category) {
				return casinoService.all("casino").all("bonus").all("find").all("game").all("category").one(category).get();
			}
			
			service.percentageCategories = function(bonusRevisionId) {
				return casinoService.all("casino").all("bonus").all("find").all("rules").one(bonusRevisionId+'').all("percentages").all("categories").getList();
			}
			
			service.deleteBonusRulesGamesPercentage = function(id) {
				return casinoService.all("casino").all("bonus").all("delete").all("rule").all("percentage").one(id+'').get();
			}
			
			service.deleteBonusUnlockGame = function(id) {
				return casinoService.all("casino").all("bonus").all("unlockgame").one(id+'').remove();
			}
			
			service.markBonusRevisionEnabled = function(bonusRevisionId, enabled) {
				console.log(bonusRevisionId, enabled);
				return casinoService.all("casino").all("bonus").all("revision").one(bonusRevisionId+'').all("mark").all("enabled").one(enabled+'').get();
			}
			service.markBonusRevisionEdit = function(bonusId, bonusRevisionId) {
				return casinoService.all("casino").all("bonus").one(bonusId+'').all("mark").all("edit").one(bonusRevisionId+'').get();
			}
			service.markBonusRevisionCurrent = function(bonusId, bonusRevisionId) {
				return casinoService.all("casino").all("bonus").one(bonusId+'').all("mark").all("current").one(bonusRevisionId+'').get();
			}
			
			service.createNewBonus = function(bonusCreate) {
				return casinoService.all("casino").all("bonus").all("create").post(bonusCreate);
			}

			service.removeBonus = function(id) {
				return casinoService.all('casino').all('bonus').all('remove').remove({id: id});
			}
			
			service.copyBonusRevision = function(bonusRevisionId) {
				return casinoService.all("casino").all("bonus").all("copy").one(bonusRevisionId+'').get();
			}
			
			service.findUnlockGameRule = function(bonusRevisionId) {
				return casinoService.all("casino").all("bonus").all("find").all("unlockgames").one(bonusRevisionId+'').get();
			}
			
			service.findFreespinsRules = function(bonusRevisionId) {
				return casinoService.all("casino").all("bonus").all("find").all("freespins").one(bonusRevisionId+'').get();
			}

			service.findCasinoChipRules = function(bonusRevisionId) {
				return casinoService.all("casino").all("bonus").all("find").all("casino-chip").one(bonusRevisionId+'').get();
			}
			
			service.findFreespinGames = function(bonusRevisionId) {
				return casinoService.all("casino").all("bonus").all("find").all("freespin").all("games").one(bonusRevisionId+'').get();
			}

			service.findInstantRewardRules = function(bonusRevisionId) {
				return casinoService.all("casino").all("bonus").all("find").all("instant-reward").one(bonusRevisionId+'').get();
			}

			service.findInstantRewardFreespinRules = function(bonusRevisionId) {
				return casinoService.all("casino").all("bonus").all("find").all("instant-reward-freespins").one(bonusRevisionId+'').get();
			}
			
			service.bonusRulesGamesPercentages = function(bonusRevisionId) {
				return casinoService.all("casino").all("bonus").all("find").all("games").all("percentages").one(bonusRevisionId+'').getList();
			}
			
			service.bonusUnlockGames = function(bonusRevisionId) {
				return casinoService.all("casino").all("bonus").all("find").all("unlockgames").one(bonusRevisionId+'').getList();
			}
			
			service.findBonusById = function(bonusId) {
				return casinoService.all("casino").all("bonus").all("find").all("id").one(bonusId+'').get();
			}
			
			service.findByBonusRevisionId = function(bonusRevisionId) {
				return casinoService.all("casino").all("bonus").all("find").all("bonusrevision").one(bonusRevisionId+'').get();
			}
			
			service.findBonus = function(bonusRevisionId) {
				return casinoService.all("casino").all("bonus").all("find").one(bonusRevisionId+'').get();
			}
			
			service.findLastBonusRevision = function(bonusCode, domainName, type) {
				return casinoService.all("casino").all("bonus").all("find").all("revision").all(domainName).all(type+'').one(bonusCode).get();
			}
			
			service.findDepositBonusRequirements = function(bonusRevisionId) {
				return casinoService.all("casino").all("bonus").all("find").all("requirements").all("deposit").one(bonusRevisionId+'').get();
			}
			service.findSignupBonusRequirements = function(bonusRevisionId) {
				return casinoService.all("casino").all("bonus").all("find").all("requirements").all("signup").one(bonusRevisionId+'').get();
			}
			
			service.activeBonus = function(playerGuid) {
				var pg = encodeURIComponent(playerGuid);
				return casinoService.all("casino").all("bonus").all("find").all("active").one("p").get({playerGuid:pg});
			}

			service.activeBonusCodes = function(activeDomainsList) {
				return casinoService.all("casino").all("bonus").all("find").all("bonus-token").all("codes").getList({domains: activeDomainsList});
			}
			
			service.cancelActiveBonus = function(playerGuid) {
				var pg = encodeURIComponent(playerGuid);
				return casinoService.all("casino").all("bonus").all("cancel").all("active").one("p").get({playerGuid:pg});
			}
			service.cancelPendingBonus = function(playerGuid, pendingBonusId) {
				var pg = encodeURIComponent(playerGuid);
				return casinoService.all("casino").all("bonus").all("cancel").all("pending").one("p").get({playerGuid:pg, pendingBonusId:pendingBonusId});
			}
			service.cancelActivePlayerBonusToken = function(playerGuid, activePlayerBonusTokenId) {
				var pg = encodeURIComponent(playerGuid);
				return casinoService.all("casino").all("bonus").all("cancel").all("bonus-token").one("p").get({playerGuid:pg, playerBonusTokenId:activePlayerBonusTokenId});
			}
			service.externalBonusInfo = function(playerGuid, provider, domainName, gameId) {
				var pg = encodeURIComponent(playerGuid);
				return casinoService.all("casino").all("bonus").all("info").get("ext", {playerGuid:playerGuid, provider:provider, domainName:domainName, gameId:gameId});
			}
			service.externalBonusCancel = function(extBonusId, provider, domainName, gameId, userId) {
				return casinoService.all("casino").all("bonus").all("cancel").get("ext", {extBonusId:extBonusId, provider:provider, domainName:domainName, gameId:gameId, userId:userId});
			}
			service.bonusHistory = function(playerGuid, page) {
				var pg = encodeURIComponent(playerGuid);
				return casinoService.all("casino").all("bonus").all("find").all("history").one("p").get({playerGuid:pg, page:page});
			}
			service.registerTriggerBonus = function(bonusCode, playerGuid) {
				return casinoService.all("casino").all("bonus").all("manual").all("register").all("trigger").post({bonusCode:bonusCode, playerGuid:playerGuid});
			}

			// Revision id is used for bonus allocations that are manual and from an older version of the bonus
			service.registerTriggerBonusv2 = function(bonusCode, playerGuid, freeMoneyAmountDecimal, revisionId, description) {
				return casinoService.all("casino").all("bonus").all("manual").all("register").all("trigger").all("v2").post({bonusCode: bonusCode, playerGuid: playerGuid, customAmountDecimal: freeMoneyAmountDecimal, revisionId: revisionId, description: description});
			}
			// Revision id is used for bonus allocations that are manual and from an older version of the bonus
			service.registerTriggerBonusv3 = function(domainName, bonusType, bonusCode, playerGuid, freeMoneyAmountDecimal, revisionId, description, noteText) {
				return casinoService.all("backoffice").all(domainName).all("bonus").all(bonusType).all("manual").all("register").all("trigger").all("v3").post({bonusCode: bonusCode, playerGuid: playerGuid, customAmountDecimal: freeMoneyAmountDecimal, revisionId: revisionId, description: description, noteText: noteText});
			}
			// Revision id is used for bonus allocations that are manual and from an older version of the bonus
			service.registerBonusTokenBonusv2 = function(bonusCode, playerGuid, bonusTokenAmountDecimal, revisionId) {
				return casinoService.all("casino").all("bonus").all("manual").all("register").all("bonus-token").all("v2").post({bonusCode:bonusCode, playerGuid:playerGuid, customAmountDecimal: bonusTokenAmountDecimal, revisionId:revisionId});
			}
			//Not used, see fileUpload.uploadFileToUrl
			// service.registerTriggerBonusv2csv = function(csvfile, bonusCode) {
			// 	return casinoService.all("casino").all("bonus").all("manual").all("register").all("trigger").all("v2").all("csv").post({cvsfile:csvfile, bonusCode:bonusCode});
			// }
			service.registerSignupBonus = function(bonusCode, playerGuid, override) {
				return casinoService.all("casino").all("bonus").all("manual").all("register").all("signup").post({bonusCode:bonusCode, playerGuid:playerGuid, override:override});
			}
			// Revision id is used for bonus allocations that are manual and from an older version of the bonus
			service.registerSignupBonusv2 = function(bonusCode, playerGuid, override, revisionId) {
				return casinoService.all("casino").all("bonus").all("manual").all("register").all("signup").all("v2").post({bonusCode:bonusCode, playerGuid:playerGuid, override:override, revisionId:revisionId});
			}
			service.registerDepositBonus = function(bonusCode, playerGuid, userEventId, override) {
				return casinoService.all("casino").all("bonus").all("manual").all("register").all("deposit").post({bonusCode:bonusCode, playerGuid:playerGuid, userEventId:userEventId, override:override});
			}
			// Revision id is used for bonus allocations that are manual and from an older version of the bonus
			service.registerDepositBonusv2 = function(bonusCode, playerGuid, userEventId, override, revisionId) {
				return casinoService.all("casino").all("bonus").all("manual").all("register").all("deposit").all("v2").post({bonusCode:bonusCode, playerGuid:playerGuid, userEventId:userEventId, override:override, revisionId:revisionId});
			}
			service.availableBonuses = function(domainName, type, playerGuid) {
				return casinoService.all("casino").all("bonus").all("find").all(domainName).all(type).all("public").all("player").all("v2").getList({playerGuid:playerGuid});
			}
			service.findPublicBonusListV2 = function(domainName, type, triggerType) {
				return casinoService.all("casino").all("bonus").all("find").all(domainName).all(type).all("public").all("all").all("v2").getList({triggerType:triggerType});
			}

			service.findStatus = function() {
				return searchService.all("status").getList();
			}

			service.findProviders = function(domainName) {
				return searchService.all("providers").getList({domainName:domainName});
			}

			service.getRoundReplay = function (domainName, providerId, gameKey, roundId, playerId) {
				return casinoProviderService.all("rgp").one("game-replay").get({
					domainName: domainName,
					providerId: providerId,
					gameKey: gameKey,
					roundId: roundId,
					playerId: playerId
				});
			}

			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);
