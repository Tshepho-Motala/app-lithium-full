'use strict'

angular.module('lithium').controller('GoGameResultBatchesResultBatchController', ['resultBatch', 'resultBatchAnalysis', '$translate', '$filter', '$uibModal', '$state', '$scope', 'errors', 'notify', '$q', 'GoGameGamesRest', '$dt', 'DTOptionsBuilder',
	function(resultBatch, resultBatchAnalysis, $translate, $filter, $uibModal, $state, $scope, errors, notify, $q, gogameGamesRest, $dt, DTOptionsBuilder) {
		var controller = this;
		
		controller.resultBatch = resultBatch.plain();
		controller.resultBatchAnalysis = resultBatchAnalysis.plain();
		
		var baseUrl = 'services/service-casino-provider-gogame/admin/resultbatch/'+resultBatch.id+'/results';
		
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[0, 'asc']]);
		controller.resultsTable = $dt.builder()
		.column($dt.column('id').withTitle('ID'))
		.column($dt.columncurrency('winCents', '$', 2).withTitle("Win"))
		.column($dt.column('json').withTitle("JSON"))
		.options(baseUrl, null, dtOptions, null)
		.build();
		
		controller.refresh = function() {
			gogameGamesRest.findResultBatchById(controller.resultBatch.id).then(function(response) {
				controller.resultBatch = response.plain();
			});
			gogameGamesRest.findResultBatchAnalysisByResultBatchId(controller.resultBatch.id).then(function(response) {
				controller.resultBatchAnalysis = response.plain();
			});
			controller.resultsTable.instance.reloadData(function(){}, false);
		}
		
		controller.downloadxls = function() {
			console.log("Downloading XLS");
			window.location = 'services/service-casino-provider-gogame/admin/resultbatch/'+resultBatch.id+'/xls';
		}
		
		controller.assignToLedger = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/gogamegames/resultbatches/resultbatch/assigntoledger/assigntoledger.html',
				controller: 'GoGameResultBatchAssignToLedgerModal',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					resultBatch: function() { return resultBatch; },
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/gogamegames/resultbatches/resultbatch/assigntoledger/assigntoledger.js'
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
