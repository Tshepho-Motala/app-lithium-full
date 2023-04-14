'use strict'

angular.module('lithium').controller('GoGameHotOMetersListController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', '$scope',
	function($log, $translate, $dt, DTOptionsBuilder, $filter, $state, $scope) {
		var controller = this;
		
		var baseUrl = 'services/service-casino-provider-gogame/admin/hotometers/table';
		
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[0, 'asc']]);
		controller.gogameHotMetersTable = $dt.builder()
		.column($dt.column('id').withTitle('id'))
		.column($dt.column('game.name').withTitle('game'))
		.column($dt.column('game.gameId.domain.name').withTitle('domain'))
		.column($dt.columncurrencysymbol('totalPlay', '$', 2).withTitle('totalPlay'))
		.column($dt.column('requiredSpins').withTitle('spins'))
		.column($dt.column('levelMin').withTitle('levelMin'))
		.column($dt.column('levelMax').withTitle('levelMax'))
		.column($dt.column('activationTimeSeconds').withTitle('activationTimeSeconds'))
//		.column($dt.linkscolumn(
//			"",
//			[
//				{ 
//					permission: "gogamegames_hotometers_*",
//					permissionType: "any",
//					title: "GLOBAL.ACTION.OPEN",
//					href: function(data) {
//						return $state.href("dashboard.gogamegames.hotometers.hotometer", { id:data.id });
//					}
//				}
//			]
//		))
		.options(baseUrl, null, dtOptions, null)
		.build();
	}
]);
