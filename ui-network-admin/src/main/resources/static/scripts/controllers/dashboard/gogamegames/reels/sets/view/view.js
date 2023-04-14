'use strict'

angular.module('lithium').controller('GoGameReelSetViewController', ['reelSet', '$translate', '$filter', '$uibModal', '$state', '$scope', 'errors', 'notify', '$q',
	function(reelSet, $translate, $filter, $uibModal, $state, $scope, errors, notify, $q) {
		var controller = this;
		
		controller.reelSet = reelSet;
		
		controller.reels = JSON.parse(controller.reelSet.json);
		
		controller.reelPositions = [];
		controller.maxReelLen = 0;
		
		for (var i = 0; i < 5; i++) {
			if (controller.reels[0][i].symbols.length > controller.maxReelLen)
				controller.maxReelLen = controller.reels[0][i].symbols.length;
		}
		
		for (var i = 0; i < controller.maxReelLen; i++) {
			controller.reelPositions.push(i + 1);
		}
	}
]);