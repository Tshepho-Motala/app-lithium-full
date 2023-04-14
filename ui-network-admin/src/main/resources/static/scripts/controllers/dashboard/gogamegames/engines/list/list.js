'use strict'

angular.module('lithium').controller('GoGameEnginesListController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', '$scope',
	function($log, $translate, $dt, DTOptionsBuilder, $filter, $state, $scope) {
		var controller = this;
		
		var baseUrl = 'services/service-casino-provider-gogame/admin/engines/table';
		
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[0, 'asc']]);
		controller.gogameEnginesTable = $dt.builder()
		.column($dt.column('id').withTitle('Engine'))
		.column($dt.linkscolumn(
			"",
			[
				{ 
					permission: "gogamegames_engines_*",
					permissionType: "any",
					title: "GLOBAL.ACTION.OPEN",
					href: function(data) {
						return $state.href("dashboard.gogamegames.engines.engine", { id:data.id });
					}
				}
			]
		))
		.options(baseUrl, null, dtOptions, null)
		.build();
	}
]);
