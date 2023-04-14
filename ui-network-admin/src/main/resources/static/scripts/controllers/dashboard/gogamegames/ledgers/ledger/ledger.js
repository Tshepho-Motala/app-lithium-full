'use strict'

angular.module('lithium').controller('GoGameLedgersLedgerController', ['ledger', 'ledgerAnalysis', 'countNew', 'countSeen', 'countTaken', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$uibModal', '$state', '$scope', 'errors', 'notify', '$q', 'GoGameGamesRest',
	function(ledger, ledgerAnalysis, countNew, countSeen, countTaken, $translate, $dt, DTOptionsBuilder, $filter, $uibModal, $state, $scope, errors, notify, $q, gogameGamesRest) {
		var controller = this;
		
		controller.model = ledger;
		controller.ledgerAnalysis = ledgerAnalysis;
		if (controller.ledgerAnalysis.blocks === undefined || controller.ledgerAnalysis.blocks === null) controller.ledgerAnalysis.blocks = 0;
		controller.countNew = (countNew !== undefined)? countNew: 0;
		controller.countSeen = (countSeen !== undefined)? countSeen: 0;
		controller.countTaken = (countTaken !== undefined)? countTaken: 0;
		
		var baseUrl = 'services/service-casino-provider-gogame/admin/ledger/'+ledger.id+'/blocks';
		
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[0, 'asc']]);
		controller.ledgerBlocksTable = $dt.builder()
		.column($dt.column('id').withTitle('ID'))
		.column($dt.linkscolumn(
			"",
			[
				{ 
					permission: "gogamegames_ledgers_*",
					permissionType: "any",
					title: "GLOBAL.ACTION.OPEN",
					href: function(data) {
						return $state.href("dashboard.gogamegames.ledgers.ledger.block", { id: ledger.id, ledgerBlockId:data.id });
					}
				}
			]
		))
//		.column($dt.column('nonce').withTitle('nonce'))
		.column($dt.columnformatdatetime('created').withTitle('Created'))
		.column($dt.column('processing').withTitle('Processing'))
		.column($dt.columnformatdatetime('processingStarted').withTitle('Processing Started'))
		.column($dt.column('numCompleted').withTitle('No. Completed'))
		.column($dt.column('completed').withTitle('Completed'))
		.column($dt.columnformatdatetime('lastUpdated').withTitle('Last Updated'))
		.column($dt.column('previousHash').withTitle('Previous Hash'))
		.column($dt.column('hash').withTitle('Hash'))
		.options(baseUrl, null, dtOptions, null)
		.build();
		
		controller.refresh = function() {
			gogameGamesRest.findledgerbyid(ledger.id).then(function(ledger) {
				controller.model = ledger;
			});
			gogameGamesRest.ledgerAnalysis(ledger.id).then(function(ledgerAnalysis) {
				controller.ledgerAnalysis = ledgerAnalysis;
				if (controller.ledgerAnalysis.blocks === undefined || controller.ledgerAnalysis.blocks === null) controller.ledgerAnalysis.blocks = 0;
			});
			gogameGamesRest.countEntriesByStatus(ledger.id, null, 'new').then(function(countNew) {
				controller.countNew = (countNew !== undefined)? countNew: 0;
			});
			gogameGamesRest.countEntriesByStatus(ledger.id, null, 'seen').then(function(countSeen) {
				controller.countSeen = (countSeen !== undefined)? countSeen: 0;
			});
			gogameGamesRest.countEntriesByStatus(ledger.id, null, 'taken').then(function(countTaken) {
				controller.countTaken = (countTaken !== undefined)? countTaken: 0;
			});
			controller.ledgerBlocksTable.instance.reloadData(function(){}, false);
		}
		
		controller.changeInfo = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/gogamegames/ledgers/add/add.html',
				controller: 'GoGameLedgersAddController',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					ledger: function() { return angular.copy(controller.model); },
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/gogamegames/ledgers/add/add.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
				controller.refresh();
			});
		}
	}
]);
