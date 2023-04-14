'use strict'

angular.module('lithium').controller('GoGameResultBatchesListController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', '$scope', '$uibModal',
	function($log, $translate, $dt, DTOptionsBuilder, $filter, $state, $scope, $uibModal) {
		var controller = this;
		
		var baseUrl = 'services/service-casino-provider-gogame/admin/resultbatches/table';
		
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[0, 'desc']]);
		controller.gogameResultBatchesTable = $dt.builder()
		.column($dt.column('id').withTitle('ID'))
		.column($dt.linkscolumn(
			"",
			[
				{ 
					permission: "gogamegames_result_batches_*",
					permissionType: "any",
					title: "GLOBAL.ACTION.OPEN",
					href: function(data) {
						return $state.href("dashboard.gogamegames.resultbatches.resultbatch", { id:data.id });
					}
				}
			]
		))
		.column($dt.column('mathModelRevision.id').withTitle('Math Model Revision: ID'))
		.column($dt.column('mathModelRevision.name').withTitle('Math Model Revision: Name'))
		.column($dt.columncurrencysymbol('totalPlay', '$', 2).withTitle('Total Play'))
	//	dashboard.gogamegames.resultbatches.resultbatch
		.column($dt.columnformatdatetime('created').withTitle('Created'))
		.column($dt.column('quantity').withTitle('Quantity'))
		.column($dt.column('processing').withTitle('Processing'))
		.column($dt.columnformatdatetime('processingStarted').withTitle('Processing Started'))
		.column($dt.column('numCompleted').withTitle('No. Completed'))
		.column($dt.column('completed').withTitle('Completed'))
		.column($dt.columnformatdatetime('lastUpdated').withTitle('Last Updated'))
		.options(baseUrl, null, dtOptions, null)
		.build();
		
		controller.refresh = function() {
			controller.gogameResultBatchesTable.instance.reloadData(function(){}, false);
		}
		
		controller.addResultBatch = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/gogamegames/resultbatches/add/add.html',
				controller: 'GoGameResultBatchesAddController',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/gogamegames/resultbatches/add/add.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
				controller.refresh();
				$state.go("dashboard.gogamegames.resultbatches.resultbatch", { id:response.id });
			});
		}
	}
]);
