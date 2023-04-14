'use strict'

angular.module('lithium').controller('GoGameReelSetsListController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', '$scope',
	function($log, $translate, $dt, DTOptionsBuilder, $filter, $state, $scope) {
		var controller = this;
		
		var baseUrl = 'services/service-casino-provider-gogame/admin/reels/reelSets/table';
		
		controller.reelSetsTable = $dt.builder()
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
						return $state.href("dashboard.gogamegames.reelsets.view", { id:data.id });
					}
				}
			]
		))
		.column($dt.column('description').withTitle('Description'))
		.column($dt.column('config.name').withTitle('Configuration'))
		.options(baseUrl)
		.build();
		
		controller.refresh = function() {
			controller.reelSetsTable.instance.reloadData(function(){}, false);
		}
	}
]);