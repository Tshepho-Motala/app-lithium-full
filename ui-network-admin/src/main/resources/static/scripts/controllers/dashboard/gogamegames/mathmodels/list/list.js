'use strict'

angular.module('lithium').controller('GoGameMathModelsListController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', '$scope',
	function($log, $translate, $dt, DTOptionsBuilder, $filter, $state, $scope) {
		var controller = this;
		
		var baseUrl = 'services/service-casino-provider-gogame/admin/mathmodels/table';
		
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[0, 'asc']]);
		controller.gogameMathModelsTable = $dt.builder()
//		.column($dt.column('id').withTitle('ID'))
		.column($dt.column('current.name').withTitle('Name'))
		.column($dt.linkscolumn(
			"",
			[
				{ 
					permission: "gogamegames_math_models_*",
					permissionType: "any",
					title: "GLOBAL.ACTION.OPEN",
					href: function(data) {
						return $state.href("dashboard.gogamegames.mathmodels.mathmodel.view", { id:data.id, mathModelRevisionId: data.current.id });
					}
				}
			]
		))
		.column($dt.column('engine.id').withTitle('Engine'))
		.options(baseUrl, null, dtOptions, null)
		.build();
	}
]);
