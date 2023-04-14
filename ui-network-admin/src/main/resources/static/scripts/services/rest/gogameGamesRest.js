'use strict';

angular.module('lithium')
.factory('GoGameGamesRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			
			service.baseUrl = 'services/service-casino-provider-gogame/admin';
			
			var config = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl(service.baseUrl);
			});
			
			service.findAllEngines = function() {
				return config.all('engines').one("all").getList();
			}
			
			service.add = function(game) {
				return config.all('games').all('add').post(game);
			}
			
			service.findgamebyid = function(gameId, domainName) {
				return config.all('game').one(gameId).one(domainName).get();
			}
			
			service.update = function(gameId, game) {
				return config.all('game').all(gameId).all('update').post(game);
			}
			
			service.findGameLedgers = function(gameId, domainName) {
				return config.all('game').all(gameId).all(domainName).all('findGameLedgers').getList();
			}
			
			service.addGameLedger = function(gameId, domainName, ledgerId, depositAmountCents, depositedDaysAgo) {
				var params = {depositAmountCents:depositAmountCents, depositedDaysAgo:depositedDaysAgo};
				return config.all('game').all(gameId).all(domainName).one('addGameLedger', ledgerId).post('',params);
			}
			
			service.removeGameLedger = function(gameId, domainName, gameLedgerId) {
				return config.all('game').all(gameId).all(domainName).one('removeGameLedger', gameLedgerId).remove();
			}
			
			service.findAll = function() {
				return config.all('ledgers').all('findAll').getList();
			}
			
			service.addLedger = function(ledger) {
				return config.all('ledgers').all('add').post(ledger);
			}
			
			service.findLedgersByDomainAndEngine = function(domainName, engineId) {
				return config.all('ledgers').one('domain', domainName).one('findByEngine', engineId).getList();
			}
			
			service.findEligableLedgersForGame = function(gameId, domainName) {
				return config.all('ledgers').all(domainName).one('findEligableLedgersForGame', gameId).getList();
			}
			
			service.findEligableLedgersForResultBatch = function(resultBatchId) {
				return config.all('ledgers').one('findEligableLedgersForResultBatch', resultBatchId).getList();
			}
			
			service.findledgerbyid = function(ledgerId) {
				return config.all('ledger').one(ledgerId+"").get();
			}
			
			service.updateLedger = function(ledgerId, ledger) {
				return config.all('ledger').all(ledgerId).all('update').post(ledger);
			}
			
			service.findledgerblockbyid = function(ledgerId, ledgerBlockId) {
				return config.all('ledger').one(ledgerId+"").all('block').one(ledgerBlockId+"").get();
			}
			
			service.countBlocks = function(ledgerId) {
				return config.all('ledger').one(ledgerId+"").one('countBlocks'+"").get();
			}
			
			service.countEntriesByStatus = function(ledgerId, ledgerBlockId, status) {
				var params = null;
				if (ledgerBlockId !== null) {
					params = {ledgerBlockId: ledgerBlockId};
				}
				return config.all('ledger').one(ledgerId+"").one("countEntriesByStatus", status).get(params);
			}
			
			service.ledgerAnalysis = function(ledgerId) {
				return config.all('ledger').one(ledgerId+"").one('analysis').get();
			}
			
			service.findenginebyid = function(engineId) {
				return config.all('engine').one(engineId).get();
			}
			
			service.findEngineFeatures = function(engineId) {
				return config.all('engine').all(engineId).all('features').getList();
			}
			
			service.findEngineSymbols = function(engineId) {
				return config.all('engine').all(engineId).all('symbols').getList();
			}
			
			service.createMathModel = function(mathModel) {
				return config.all('mathmodels').all('create').post(mathModel);
			}
			
			service.findAllMathModels = function() {
				return config.all('mathmodels').all('findAll').getList();
			}
			
			service.findMathModelsByEngine = function(engineId) {
				return config.all('mathmodels').all('findByEngine').getList({engineId: engineId});
			}

			service.findMathModelRevisionsByEngine = function(engineId) {
				return config.all('mathmodels').all('findMathModelRevisionsByEngine').getList({engineId: engineId});
			}
			
			service.findmathmodelbyid = function(mathModelId) {
				return config.all('mathmodel').one(mathModelId).get();
			}
			
			service.findmathmodelrevisionbyid = function(mathModelId, mathModelRevisionId) {
				return config.all('mathmodel').one(mathModelId).all("revision").one(mathModelRevisionId).get();
			}
			
			service.modifyMathModel = function(mathModelId) {
				return config.all('mathmodel').one(mathModelId, 'modify').get();
			}
			
			service.modifyMathModelPost = function(mathModelId, mathModel) {
				return config.all('mathmodel').all(mathModelId).all('modify').post(mathModel);
			}
			
			service.modifyAndSaveCurrentMathModel = function(mathModelId, mathModel) {
				return config.all('mathmodel').all(mathModelId).all('modifyAndSaveCurrent').post(mathModel);
			}
			
			service.addResultBatch = function(resultBatch) {
				return config.all('resultbatches').all('create').post(resultBatch);
			}
			
			service.findResultBatchById = function(resultBatchId) {
				return config.all('resultbatch').one(""+resultBatchId).get();
			}
			
			service.findResultBatchAnalysisByResultBatchId = function(resultBatchId) {
				return config.all('resultbatch').one(""+resultBatchId).one('analysis').get();
			}
			
			service.assignToLedger = function(resultBatchId, ledgerId) {
				return config.all('resultbatch').one(""+resultBatchId).one('assignToLedger', ledgerId).post();
			}
			
			service.addReelsGenConfig = function(reelsGenConfig) {
				return config.all('reels').all('reelsGenConfig').all('add').post(reelsGenConfig);
			}
			
			service.findreelconfigbyid = function(id) {
				return config.all('reels').all('reelsGenConfig').one('view', id).get();
			}
			
			service.findreelsgenconfigs = function() {
				return config.all('reels').all('reelsGenConfig').all('all').getList();
			}
			
			service.addReelSet = function(reelSet) {
				return config.all('reels').all('reelSets').all('add').post(reelSet);
			}
			
			service.findReelSetById = function(reelSetId) {
				return config.all('reels').all('reelSets').one('view', reelSetId).get();
			}
			
			service.generateReels = function(reelsGenConfigId) {
				return config.all('reels').all('reelSets').one(reelsGenConfigId+'', 'generateReels').get();
			}
			
			service.findReelSetsByEngine = function(engineId) {
				return config.all('reels').all('reelSets').one('findByEngine', engineId).getList();
			}
			
			service.findDailyGamePlay = function(id) {
				return config.all('dailygame').one(id+"").get();
			}
			
			service.addTutorial = function(tutorial) {
				return config.all('tutorials').all('add').post(tutorial);
			}
			
			service.findtutorialbyid = function(tutorialId) {
				return config.all('tutorial').one(tutorialId+"").get();
			}
			
			service.editTutorial = function(tutorialId, tutorial) {
				return config.all('tutorial').all(tutorialId).all('edit').post(tutorial);
			}
			
			service.addDebugResult = function(debugResult) {
				return config.all('debugresults').all('add').post(debugResult);
			}
			
			service.finddebugresultbyid = function(id, engineId, mathModelRevisionId) {
				return config.all('debugresult').one(id+"").one(engineId+"").one(mathModelRevisionId+"").get();
			}
			
			service.editDebugResult = function(id, engineId, mathModelRevisionId, debugResult) {
				return config.all('debugresult').all(id+"").all(engineId+"").all(mathModelRevisionId+"").all('edit').post(debugResult);
			}

			service.removeDebugResult = function(id, engineId, mathModelRevisionId) {
				return config.all('debugresult').all(id+"").all(engineId+"").all(mathModelRevisionId+"").all('delete').remove();
			}

			service.removeDebugResultByEngineId = function(engine) {
				return config.all('debugresults').all(engine.engineId+"").all('delete').remove();
			}
			
			service.addResultSimulation = function(resultSimulation) {
				return config.all('resultsimulations').all('add').post(resultSimulation);
			}
			
			service.findResultSimulationById = function(resultSimulationId) {
				return config.all('resultsimulation').one(resultSimulationId+"").get();
			}
			
			service.addExhaustionRateTest = function(exhaustionRate) {
				return config.all('exhaustionrates').all('add').post(exhaustionRate);
			}
			
			service.findExhaustionRateTestById = function(id) {
				return config.all('exhaustionrate').one(''+id).get();
			}
			
			service.findExhaustionRateDataById = function(id) {
				return config.all('exhaustionrate').one(''+id).one('data').getList();
			}

			service.getComparators = function() {
				return config.all('windistributions').one('get').one('comparators').getList();
			}

			service.findWinDistributionTestById = function(id) {
				return config.all('windistribution').one(''+id).get();
			}

			service.addWinDistributionTest = function(winDistribution) {
				return config.all('windistributions').all('add').post(winDistribution);
			}

			service.findWinDistributionDataById = function(id) {
				return config.all('windistribution').one(''+id).one('data').getList();
			}

			return service;
		} catch (err) {
			$log.error(err);
			throw err;
		}
	}
]);