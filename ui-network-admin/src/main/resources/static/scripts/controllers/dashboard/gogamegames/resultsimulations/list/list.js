'use strict'

angular.module('lithium').controller('GoGameResultSimulationsListController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$uibModal', '$state', '$scope',
	function($log, $translate, $dt, DTOptionsBuilder, $uibModal, $state, $scope) {
		var controller = this;
		
		var baseUrl = 'services/service-casino-provider-gogame/admin/resultsimulations/table';
		
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[0, 'desc']]);
		controller.gogameResultSimulationsTable = $dt.builder()
		.column($dt.column('id').withTitle('ID'))
		.column($dt.column('mathModelRevision.id').withTitle('Math Model Revision: ID'))
		.column($dt.column('mathModelRevision.name').withTitle('Math Model Revision: Name'))
		.column($dt.columnformatdatetime('created').withTitle('Created'))
		.column($dt.column('quantity').withTitle('Quantity'))
		.column($dt.columnformatdatetime('processingStarted').withTitle('Processing Started'))
		.column($dt.column('processing').withTitle('Processing'))
		.column($dt.columnformatdatetime('processingCompleted').withTitle('Processing Completed'))
		.column($dt.linkscolumn(
			"",
			[
				{ 
					permission: "gogamegames_resultsimulations_*",
					permissionType: "any",
					title: "GLOBAL.ACTION.OPEN",
					href: function(data) {
						return $state.href("dashboard.gogamegames.resultsimulations.resultsimulation", { id:data.id });
					}
				}
			]
		))
		.options({ url: baseUrl, type: 'GET' }, null, dtOptions, null)
		.build();
		
		controller.refresh = function() {
			controller.gogameResultSimulationsTable.instance.reloadData(function(){}, false);
		}
		
		controller.add = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/gogamegames/resultsimulations/add/add.html',
				controller: 'GoGameResultSimulationsAddController',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					game: function() { return null; },
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/gogamegames/resultsimulations/add/add.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
				controller.refresh();
				$state.go("dashboard.gogamegames.resultsimulations.resultsimulation", { id:response.id });
			});
		}
	}
]);
