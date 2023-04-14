'use strict'

angular.module('lithium').controller('GoGameExhaustionRatesExhaustionRateController', ['exhaustionRate', 'exhaustionRateData', '$translate', '$filter', '$uibModal', '$state', '$scope', 'errors', 'notify', '$q', 'GoGameGamesRest', '$dt', 'DTOptionsBuilder',
	function(exhaustionRate, exhaustionRateData, $translate, $filter, $uibModal, $state, $scope, errors, notify, $q, gogameGamesRest, $dt, DTOptionsBuilder) {
		var controller = this;
		
		controller.exhaustionRate = exhaustionRate;
		controller.data = exhaustionRateData;
		
		controller.formatcurrency = function (d) {
			d = d / 100;
			return controller.exhaustionRate.ledger.currencyCode + ' ' + d.toFixed(2).replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,");
		};
		
		controller.formatspinno = function (d) {
			return 'Spin ' + d;
		}
		
		controller.drawEhrGraph = function() {
			console.log('Drawing EHR graph');
			
			var data = controller.data;
			var dataPoints = [];
			
			for (var i = 0; i < data.length; i++) {
				var pbCentsAfter = data[i].pbCentsAfter;
				dataPoints.push({winCents: data[i].ledgerEntry.result.winCents, pb: pbCentsAfter});
			}
			
			controller.exhaustionRateData = {
				dataPoints: dataPoints,
				dataColumns: [
					{id: 'pb', type: 'line', name: 'Player Balance (After spin)'},
					{id: 'winCents', type: 'line', name: 'Win Amount (Of spin)'}
				]
			}
		}
		
		controller.refresh = function() {
			gogameGamesRest.findExhaustionRateTestById(controller.exhaustionRate.id).then(function(response) {
				controller.exhaustionRate = response.plain();
			});
			gogameGamesRest.findExhaustionRateDataById(controller.exhaustionRate.id).then(function(response) {
				controller.data = response.plain();
				
				// FIXME - data is not plotted on refresh
				controller.drawEhrGraph();
			});
		}
		
		controller.downloadxls = function() {
			console.log("Downloading XLS");
			window.location = 'services/service-casino-provider-gogame/admin/exhaustionrate/'+exhaustionRate.id+'/xls';
		}
		
		controller.drawEhrGraph();
	}
]);