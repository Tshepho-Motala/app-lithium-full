'use strict'

angular.module('lithium').controller('GoGameSpinsListController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$uibModal', '$userService', '$state', '$scope',
	function($log, $translate, $dt, DTOptionsBuilder, $filter, $uibModal, $userService, $state, $scope) {
		var controller = this;
		
		controller.domains = $userService.domainsWithAnyRole(["ADMIN", "GOGAMEGAMES_SPINS_*"]);
		
		controller.domainSelect = function() {
			controller.selectedDomains = [];
			for (var d = 0; d < controller.domains.length; d++) {
				if (controller.domains[d].selected) 
					controller.selectedDomains.push(controller.domains[d].name);
			}
			if (controller.selectedDomains.length == controller.domains.length) {
				controller.selectedDomainsDisplay = "Domain";
			} else {
				controller.selectedDomainsDisplay = "Selected (" + controller.selectedDomains.length + ")";
			}
		};
		
		controller.domainSelectAll = function() {
			for (var d = 0; d < controller.domains.length; d++) controller. domains[d].selected = true;
			controller.domainSelect();
		};
		
		controller.domainSelectAll();
		
		controller.commaSeparatedSelectedDomains = function() {
			var s = '';
			for (var i = 0; i < controller.selectedDomains.length; i++) {
				if (s.length > 0) s += ',';
				s += controller.selectedDomains[i];
			}
			return s;
		}
		
		var baseUrl = 'services/service-casino-provider-gogame/admin/spins/table';
		
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[4, 'desc']]);
		controller.gogameSpinsTable = $dt.builder()
		.column($dt.column('player.guid').withTitle('Player'))
		.column($dt.column('game.name').withTitle('Game'))
		.column($dt.columncurrencysymbol('ledgerEntry.ledger.totalPlay', '$', 2).withTitle('Total Play'))
		.column($dt.columncurrency('ledgerEntry.result.winCents', '$', 2).withTitle('Win'))
		.column($dt.columnformatdatetime('seenDate').withTitle('Seen Date'))
		.column($dt.column('lastSeenEventIdx').withTitle('Last Seen Event Index'))
		.column($dt.column('taken').withTitle('Taken'))
		.column($dt.columnformatdatetime('takenDate').withTitle('Taken Date'))
		.column($dt.column('sessionId').withTitle('Session ID'))
		.column($dt.column('betTranId').withTitle('Bet Transaction ID'))
		.column($dt.column('winTranId').withTitle('Win Transaction ID'))
		.options({ url: baseUrl, type: 'GET', data: function(d) { d.domains = controller.commaSeparatedSelectedDomains() } }, null, dtOptions, null)
		.build();
		
		controller.refresh = function() {
			controller.gogameSpinsTable.instance.reloadData(function(){}, false);
		}
		
		$scope.$watch(function() { return controller.selectedDomains }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				controller.refresh();
			}
		});
	}
]);
