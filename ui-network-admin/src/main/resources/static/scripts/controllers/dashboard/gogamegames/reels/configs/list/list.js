'use strict'

angular.module('lithium').controller('GoGameReelConfigsListController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', '$scope',
	function($log, $translate, $dt, DTOptionsBuilder, $filter, $state, $scope) {
		var controller = this;
		
		var baseUrl = 'services/service-casino-provider-gogame/admin/reels/reelsGenConfig/table';
		
		controller.reelConfigsTable = $dt.builder()
//		.column($dt.column('id').withTitle('ID'))
		.column($dt.column('name').withTitle('Name'))
		.column($dt.linkscolumn(
			"",
			[
				{ 
					permission: "gogamegames_reels_*",
					permissionType: "any",
					title: "GLOBAL.ACTION.OPEN",
					href: function(data) {
						return $state.href("dashboard.gogamegames.reelconfigs.view", { id:data.id });
					}
				}
			]
		))
		.column($dt.column('description').withTitle('Description'))
		.column($dt.column('engine.id').withTitle('Engine'))
		.options(baseUrl)
		.build();
		
		controller.refresh = function() {
			controller.reelConfigsTable.instance.reloadData(function(){}, false);
		}
	}
]);