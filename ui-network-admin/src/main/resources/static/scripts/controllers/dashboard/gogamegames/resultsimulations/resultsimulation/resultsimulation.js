'use strict'

angular.module('lithium').controller('GoGameResultSimulationsResultSimulationController', ['resultSimulation', '$translate', '$filter', '$uibModal', '$state', '$scope', 'errors', 'notify', '$q', 'GoGameGamesRest', '$dt', 'DTOptionsBuilder',
	function(resultSimulation, $translate, $filter, $uibModal, $state, $scope, errors, notify, $q, gogameGamesRest, $dt, DTOptionsBuilder) {
		var controller = this;
		
		controller.resultSimulation = resultSimulation.plain();
		
		controller.refresh = function() {
			gogameGamesRest.findResultSimulationById(controller.resultSimulation.id).then(function(response) {
				controller.resultSimulation = response.plain();
			});
		}
		
		controller.downloadxls = function() {
			console.log("Downloading XLS");
			window.location = 'services/service-casino-provider-gogame/admin/resultsimulation/'+resultSimulation.id+'/xls';
		}
	}
]);