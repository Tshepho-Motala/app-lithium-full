'use strict';

angular.module('lithium')
.factory('rest-games', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-games');
			});
			
//			service.view = function(domainId, providerId) {
//				var providerPromise = config.all("domain/"+domainId+"/provider/"+providerId).one("view").get();
//				return providerPromise;
//			}
//			
//			service.save = function(domainId, providerId, provider) {
//				var providerPromise = config.all("domain/"+domainId+"/provider/"+providerId+"/edit").post(provider);
//				return providerPromise;
//			}
		
			//This does a full update of all configured provider games
			service.updateGamesList = function(domainName) {
				var gamesUpdatePromise = 
					config.one("/games/"+domainName+"/updateProviderGames").get();
				return gamesUpdatePromise;
			}
			
			service.list = function(domainName) {
				var gamesListPromise = config.all("/games/"+domainName+"/listDomainGames").getList();
				return gamesListPromise;
			}

			service.listGamesFreeSpin = function(domainName, freeSpinEnabled) {
				var gamesListPromise =
					config.all("/backoffice/games/"+domainName+"/listDomainGames").customGETLIST("",
						{
							"freeSpinEnabled" : freeSpinEnabled
						});
				return gamesListPromise;
			}
			
//			service.saveDomainGame = function(domainName, gameGuid, selected) {
//				var gamesListPromise = 
//					config.all("/games/"+domainName).customGET("editDomainGame", {"gameGuid" : gameGuid, "selected" : selected});
//				return gamesListPromise;
//			}
			
			service.addGame = function(providerGuid, gameName, commercialName, providerGameId, gameDescription, supplierGameGuid,moduleSupplierId,
									   rtp, introductionDate, activeDate, inactiveDate, domainName, freeSpinEnabled,
									   casinoChipEnabled,instantRewardEnabled,instantRewardFreespinEnabled, freeSpinValueRequired,
									   freeSpinPlayThroughEnabled, gameSupplierId, primaryGameTypeId, secondaryGameTypeId, gameStudioId,
									   progressiveJackpot, networkedJackpotPool, localJackpotPool, freeGame, liveCasino, supplierGameRewardGuid=null
			) {
				var gameAddPromise = 
					config.all("/games").customGET("add",
						{
							"providerGuid" : providerGuid,
							"gameName" : gameName,
							"commercialName": commercialName,
							"providerGameId" : providerGameId,
							"description" : gameDescription,
							"supplierGameGuid": supplierGameGuid,
							"moduleSupplierId": moduleSupplierId,
							"rtp": rtp,
							"introductionDate": introductionDate,
							"activeDate": activeDate,
							"inactiveDate": inactiveDate,
							"domainName" : domainName,
							"freeSpinEnabled" : freeSpinEnabled,
							"casinoChipEnabled" : casinoChipEnabled,
							"instantRewardEnabled" : instantRewardEnabled,
							"instantRewardFreespinEnabled" : instantRewardFreespinEnabled,
							"freeSpinValueRequired" : freeSpinValueRequired,
							"freeSpinPlayThroughEnabled" : freeSpinPlayThroughEnabled,
							"progressiveJackpot": progressiveJackpot,
							"networkedJackpotPool": networkedJackpotPool,
							"localJackpotPool": localJackpotPool,
							"gameSupplierId": gameSupplierId,
							"primaryGameTypeId": primaryGameTypeId,
							"secondaryGameTypeId": secondaryGameTypeId,
							"gameStudioId": gameStudioId,
							"freeGame": freeGame,
							"liveCasino": liveCasino,
							"supplierGameRewardGuid": supplierGameRewardGuid

						});
				return gameAddPromise;
			}
			
			service.findByGameId = function(gameId) {
				return config.all("games").one(gameId, "findById").get();
			}
			
			service.findByGameGuid = function(gameGuid, domain) {
				return config.all("games").all(domain).all("find").all("guid").customGET(gameGuid);
			}
			
			service.editGraphic = function(gameId, graphicFunction, fileForm) {
				var graphicPromise =
					config.all("games/"+gameId+"/editGraphic/"+graphicFunction).post(fileForm);
				return graphicPromise;
			}
			
			service.removeGraphic = function(gameId, domainName, graphicFunction, liveCasino) {
				var graphicPromise =
					config.all("games/"+gameId+"/removeGraphic/"+domainName+"/"+graphicFunction+"/"+liveCasino).post();
				return graphicPromise;
			}
			
			service.save = function(game) {
				var gamePromise = config.all("games/edit").post(game);
				return gamePromise;
			} 
			
			service.toggleLocked = function(gameId, userGuid) {
				return config.all("games").all(''+gameId).all("unlock").all("toggle").post({
					guid: userGuid
				});
			}
			
			service.toggleDelete = function(gameId, userGuid) {
				return config.all("games").all(''+gameId).all("unlock").all("d").remove({
					guid: userGuid
				});
			}

			service.findCdnExternalGameGraphic = function(domainName, gameId, liveCasino) {
				return config.all("backoffice").all("games").all(domainName).all("cdn-external-graphic").one("find").get({gameId: gameId, liveCasino: liveCasino});
			}

			service.saveCdnExternalGameGraphic = function(domainName, gameId, url, liveCasino) {
				return config.all("backoffice").all("games").all(domainName).all("cdn-external-graphic").all("save").customPOST(
					'', '', { gameId: gameId, url: url, liveCasino: liveCasino }, {});
			}

			service.removeCdnExternalGameGraphic = function(domainName, gameId, liveCasino) {
				return config.all("backoffice").all("games").all(domainName).all("cdn-external-graphic").all("remove").customPOST(
					'', '', { gameId: gameId, liveCasino: liveCasino }, {});
			}

			service.findUnlockedFreeGamesForUser = function(userGuid) {
				return config.all("backoffice").all("game-user-status").all("free-games").all("find").all("unlocked").customGETLIST("",
				{
					"userGuid" : userGuid
				});
			}

			service.lockFreeGamesForUser = function(userGuid) {
				return config.all("backoffice").all("game-user-status").all("free-games").all("lock").customPOST(
				'', '', { userGuid: userGuid }, {});
			}

			service.unlockFreeGamesForUser = function(userGuid) {
				return config.all("backoffice").all("game-user-status").all("free-games").all("unlock").customPOST(
				'', '', { userGuid: userGuid }, {});
			}

			service.getGamesByDomainAndEnabled = function (domainName, enabled, visible, channel, liveCasino){
				var gamesListPromise =
				config.all("/backoffice/games/"+domainName+"/listDomainGames").customGETLIST("",
				{
					"enabled" : enabled,
					"visible" : visible,
					"channel" : channel
 				});
				return gamesListPromise;
			}

			service.changelogs = function(domainName, entityId, page) {
				return config.all('games').one(entityId + '').one('changelogs').get({ p: page });
			}

			service.getDomainGamesByGameType = function (domainName, gameTypeId) {
				var gamesUpdatePromise =
					config.one("/games/" + domainName + "/get-domain-games-by-game-type").customGETLIST("",
						{
							"gameTypeId": gameTypeId
						});
				return gamesUpdatePromise;
			}

			service.getChannels = function () {
				return config.all("/backoffice/get-channels").all("/find-all").post();
			}

			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);
