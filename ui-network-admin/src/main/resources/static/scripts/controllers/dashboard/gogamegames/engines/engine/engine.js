'use strict'

angular.module('lithium').controller('GoGameEnginesEngineController', ['engine', 'features', 'symbols', '$translate', '$filter', '$uibModal', '$state', '$scope', 'errors', 'notify', '$q', 'GoGameGamesRest',
	function(engine, features, symbols, $translate, $filter, $uibModal, $state, $scope, errors, notify, $q, gogameGamesRest) {
		var controller = this;
		
		controller.model = engine;
		controller.features = features.plain();
		controller.symbols = symbols.plain();
		
		
//		console.log(controller.features);
	}
]);