'use strict'

angular.module('lithium').controller('GoGameExhaustionRatesListController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$uibModal', '$state', '$scope',
	function($log, $translate, $dt, DTOptionsBuilder, $uibModal, $state, $scope) {
		var controller = this;
		
		var baseUrl = 'services/service-casino-provider-gogame/admin/exhaustionrates/table';
		
		var dtOptions = null;//DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[2, 'desc']]);
		controller.gogameExhaustionRatesTable = $dt.builder()
		.column($dt.column('id').withTitle('ID'))
		.column($dt.columnformatdatetime('created').withTitle('Created'))
		.column($dt.column('processing').withTitle('Processing'))
		.column($dt.columnformatdatetime('processingStarted').withTitle('Processing Started'))
		.column($dt.columnformatdatetime('processingCompleted').withTitle('Processing Completed'))
		.column($dt.column('ledger.name').withTitle('Ledger'))
		.column($dt.linkscolumn(
			"",
			[
				{ 
					permission: "gogamegames_exhaustionrates_*",
					permissionType: "any",
					title: "GLOBAL.ACTION.OPEN",
					href: function(data) {
						return $state.href("dashboard.gogamegames.exhaustionrates.exhaustionrate", { id:data.id });
					}
				}
			]
		))
		.options({ url: baseUrl, type: 'GET' }, null, dtOptions, null)
		.build();
		
		controller.refresh = function() {
			controller.gogameExhaustionRatesTable.instance.reloadData(function(){}, false);
		}
		
		controller.add = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/gogamegames/exhaustionrates/add/add.html',
				controller: 'GoGameExhaustionRatesAddController',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/gogamegames/exhaustionrates/add/add.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
				controller.refresh();
				$state.go("dashboard.gogamegames.exhaustionrates.exhaustionrate", { id:response.id });
			});
		}
	}
]);
