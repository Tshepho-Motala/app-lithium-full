'use strict'

angular.module('lithium').controller('GoGameGamesGameController', ['game', 'gameLedgers', '$translate', '$filter', '$uibModal', '$state', '$scope', 'errors', 'notify', '$q', 'GoGameGamesRest',
	function(game, gameLedgers, $translate, $filter, $uibModal, $state, $scope, errors, notify, $q, gogameGamesRest) {
		var controller = this;
		
		controller.game = game;
		controller.gameLedgers = gameLedgers.plain();
		
		controller.model = { domainName: game.gameId.domain.name, id: game.gameId.gameId, name: game.name, url: game.url, descriptionEng: game.descriptionEng, currencyCode: game.currencyCode, currencyDivisor: game.currencyDivisor, payoutCurrencyCode: game.payoutCurrencyCode, payoutCurrencyDivisor: game.payoutCurrencyDivisor, engineId: game.engine.id, real: game.real, defaultStakeCents: game.defaultStakeCents };
		
		controller.refresh = function(g) {
			controller.model = { domainName: g.gameId.domain.name, id: g.gameId.gameId, name: g.name, url: g.url, descriptionEng: g.descriptionEng, currencyCode: g.currencyCode, currencyDivisor: g.currencyDivisor, payoutCurrencyCode: g.payoutCurrencyCode, payoutCurrencyDivisor: g.payoutCurrencyDivisor, engineId: g.engine.id, real: g.real, defaultStakeCents: g.defaultStakeCents };
			controller.game.mathModelRevision = g.mathModelRevision;
		}
		
		controller.changeInfo = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/gogamegames/games/add/add.html',
				controller: 'GoGameGamesAddController',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					game: function() { return angular.copy(controller.model); },
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/gogamegames/games/add/add.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
				gogameGamesRest.findGameLedgers(response.gameId.gameId, response.gameId.domain.name).then(function(gl) {
					controller.refresh(response.plain());
				});
			});
		}
		
		controller.addGameLedger = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/gogamegames/games/components/addgameledger/addgameledger.html',
				controller: 'GoGameGamesGameLedgersController',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					game: function() { return game; },
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/gogamegames/games/components/addgameledger/addgameledger.js'
							]
						})
					}
					
				}
			});
			
			modalInstance.result.then(function(response) {
				gogameGamesRest.findGameLedgers(game.gameId.gameId, game.gameId.domain.name).then(function(response) {
					gameLedgers = response;
					controller.gameLedgers = response.plain();
				}).catch(function(error) {
					notify.error('Could not refresh game ledgers');
					errors.catch('', false)(error)
				});
			});
		}
		
		controller.removeGameLedger = function(gameLedger, $index) {
			gogameGamesRest.removeGameLedger(game.gameId.gameId, game.gameId.domain.name, gameLedger.id).then(function(removed) {
				
				if (removed === true) {
					notify.success('Game ledger removed successfully');
					
					gogameGamesRest.findGameLedgers(game.gameId.gameId, game.gameId.domain.name).then(function(response) {
						gameLedgers = response;
						controller.gameLedgers = response.plain();
					}).catch(function(error) {
						notify.error('Could not refresh game ledgers');
						errors.catch('', false)(error)
					});
				}
				
			}).catch(function(error) {
				notify.error('Could not remove game ledger');
				errors.catch('', false)(error)
			});
		}
	}
]);
