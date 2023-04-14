'use strict'

angular.module('lithium').controller('GoGameReelConfigurationViewController', ['config', '$translate', '$filter', '$uibModal', '$state', '$scope', 'errors', 'notify', '$q',
	function(config, $translate, $filter, $uibModal, $state, $scope, errors, notify, $q) {
		var controller = this;
		
		controller.config = config;
		
		controller.updateReelsSymbolCount = function() {
			controller.reelsSymbolCount = [{reelNum: 1, count: 0}, {reelNum: 2, count: 0},
				{reelNum: 3, count: 0}, {reelNum: 4, count: 0}, {reelNum: 5, count: 0}];
			for (var i = 0; i < controller.config.symbolConfigs.length; i++) {
				var symbolConfig = controller.config.symbolConfigs[i];
				var stacks = symbolConfig.stack.split(',');
				for (var k = 0; k < symbolConfig.reelConfigs.length; k++) {
					var reelConfig = symbolConfig.reelConfigs[k];
					var numOfAppearances = reelConfig.numOfAppearances.split(',');
					for (var x = 0; x < numOfAppearances.length; x++) {
						controller.reelsSymbolCount[k].count = controller.reelsSymbolCount[k].count + (numOfAppearances[x] * stacks[x]);
					}
				}
			}
		}
		
		controller.updateReelsSymbolCount();
		
		
		controller.copyToAddScreen = function() {
			$state.go('dashboard.gogamegames.reelconfigs.add', { copiedFromId: config.id });
		}
	}
]);