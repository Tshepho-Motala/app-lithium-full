'use strict'

angular.module('lithium').controller('GoGameGamesListController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', '$scope', '$uibModal',
	function($log, $translate, $dt, DTOptionsBuilder, $filter, $state, $scope, $uibModal) {
		var controller = this;
		
		var baseUrl = 'services/service-casino-provider-gogame/admin/games/table';
		
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[0, 'asc']]);
		controller.gogameGamesTable = $dt.builder()
		.column($dt.column('gameId.gameId').withTitle('Game ID'))
		.column($dt.column('gameId.domain.name').withTitle('Domain'))
		.column($dt.column('name').withTitle('Name'))
		.column($dt.linkscolumn(
			"",
			[
				{ 
					permission: "gogamegames_games_*",
					permissionType: "any",
					title: "GLOBAL.ACTION.OPEN",
					href: function(data) {
						return $state.href("dashboard.gogamegames.games.game", { id:data.gameId.gameId, domainName: data.gameId.domain.name });
					}
				}
			]
		))
		.column($dt.column('descriptionEng').withTitle('Description'))
		.column($dt.column('engine.id').withTitle('Engine'))
		.column($dt.column('currencyCode').withTitle('Currency'))
		.column($dt.column('real').withTitle('Real'))
		.column($dt.column('payoutCurrencyCode').withTitle('Payout Currency'))
		.column($dt.column('mathModelRevision.id').withTitle('Math Model Revision: ID'))
		.column($dt.column('mathModelRevision.name').withTitle('Math Model Revision: Name'))
		.options(baseUrl, null, dtOptions, null)
		.build();
		
		controller.refresh = function() {
			controller.gogameGamesTable.instance.reloadData(function(){}, false);
		}
		
		controller.addGame = function() {
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
					game: function() { return null; },
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
				controller.refresh();
				$state.go("dashboard.gogamegames.games.game", { id:response.gameId.gameId, domainName: response.gameId.domain.name });
			});
		}
	}
]);
