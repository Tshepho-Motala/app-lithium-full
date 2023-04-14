'use strict'

angular.module('lithium').controller('GoGameLedgersLedgerBlockController', ['ledger', 'ledgerBlock', 'countNew', 'countSeen', 'countTaken', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$uibModal', '$state', '$scope', 'errors', 'notify', '$q', 'GoGameGamesRest', 'bsLoadingOverlayService',
	function(ledger, ledgerBlock, countNew, countSeen, countTaken, $translate, $dt, DTOptionsBuilder, $filter, $uibModal, $state, $scope, errors, notify, $q, gogameGamesRest, bsLoadingOverlayService) {
		var controller = this;
		
		controller.ledger = ledger;
		controller.ledgerBlock = ledgerBlock;
		controller.countNew = (countNew !== undefined)? countNew: 0;
		controller.countSeen = (countSeen !== undefined)? countSeen: 0;
		controller.countTaken = (countTaken !== undefined)? countTaken: 0;
		
		var baseUrl = 'services/service-casino-provider-gogame/admin/ledger/'+ledger.id+'/block/'+ledgerBlock.id+'/entries';
		
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[0, 'asc']]);
		controller.ledgerBlocksTable = $dt.builder()
		.column($dt.column('id').withTitle('ID'))
		.column($dt.column('result.resultBatch.id').withTitle('Result Batch'))
		.column($dt.column('result.id').withTitle('Result ID'))
		.column($dt.columncurrency('result.winCents').withTitle('Win'))
		.column(
			$dt.labelcolumn(
				'Status',
				[{lclass: function(data) {
					switch (data.status) {
						case 1: return "info";
						case 2: return "warning";
						case 3: return "success";
						default: return "default";
					}
				},
				text: function(data) {
					switch (data.status) {
						case 1: return "new";
						case 2: return "seen";
						case 3: return "taken";
						default: return "";
					}
				},
				uppercase:true
				}]
			)
		)
		.column($dt.column('previousHash').withTitle('Previous Hash'))
		.column($dt.column('hash').withTitle('Hash'))
		.options(baseUrl, null, dtOptions, null)
		.build();
		
		controller.refresh = function() {
			gogameGamesRest.findledgerblockbyid(ledger.id, ledgerBlock.id).then(function(lb) {
				controller.ledgerBlock = lb;
			});
			gogameGamesRest.countEntriesByStatus(ledger.id, ledgerBlock.id, 'new').then(function(countNew) {
				controller.countNew = (countNew !== undefined)? countNew: 0;
			});
			gogameGamesRest.countEntriesByStatus(ledger.id, ledgerBlock.id, 'seen').then(function(countSeen) {
				controller.countSeen = (countSeen !== undefined)? countSeen: 0;
			});
			gogameGamesRest.countEntriesByStatus(ledger.id, ledgerBlock.id, 'taken').then(function(countTaken) {
				controller.countTaken = (countTaken !== undefined)? countTaken: 0;
			});
			controller.ledgerBlocksTable.instance.reloadData(function(){}, false);
		}
	}
]);
