'use strict'

angular.module('lithium').controller('GoGameDailyGameViewController', ['dailyGame', '$translate', '$filter', '$uibModal', '$state', '$scope', 'errors', 'notify', '$q', 'GoGameGamesRest',
	function(dailyGame, $translate, $filter, $uibModal, $state, $scope, errors, notify, $q, gogameGamesRest) {
		var controller = this;
		controller.dailyGame = dailyGame;
	}
]);