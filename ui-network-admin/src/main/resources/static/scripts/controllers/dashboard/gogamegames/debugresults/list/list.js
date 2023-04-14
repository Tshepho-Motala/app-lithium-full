'use strict'

angular.module('lithium').controller('GoGameDebugResultsListController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$uibModal', '$state', '$scope',
	function($log, $translate, $dt, DTOptionsBuilder, $uibModal, $state, $scope) {
		var controller = this;


		var baseUrl = 'services/service-casino-provider-gogame/admin/debugresults/table';
		
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[0, 'asc']]);
		controller.gogameDebugResultsTable = $dt.builder()
		.column($dt.column('debugResultId.debugResultId').withTitle('Debug Result ID'))
		.column($dt.column('debugResultId.engine.id').withTitle('Engine'))
		.column($dt.column('debugResultId.mathModelRevision.id').withTitle('Math Model Revision'))
		.column($dt.column('debugResultId.mathModelRevision.name').withTitle('Math Model Revision: Name'))
		.column($dt.column('winCents').withTitle('Win Cents'))
		.column($dt.linkscolumn(
			"",
			[
				{ 
					permission: "gogamegames_debugresults_*",
					permissionType: "any",
					title: "GLOBAL.ACTION.OPEN",
					href: function(data) {
						return $state.href("dashboard.gogamegames.debugresults.debugresult",
							{ id:data.debugResultId.debugResultId,
								engineId: data.debugResultId.engine.id,
								mathModelRevisionId: data.debugResultId.mathModelRevision.id });
					}
				}
			]
		))
		.options({ url: baseUrl, type: 'GET' }, null, dtOptions, null)
		.build();

		controller.refresh = function() {
			controller.gogameDebugResultsTable.instance.reloadData(function(){}, false);
		}
	}
]);
