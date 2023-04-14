'use strict'

angular.module('lithium').controller('GoGameTutorialsListController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$uibModal', '$userService', '$state', '$scope',
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
		
		var baseUrl = 'services/service-casino-provider-gogame/admin/tutorials/table';
		
		var dtOptions = null; //DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[4, 'desc']]);
		controller.gogameTutorialsTable = $dt.builder()
		.column($dt.column('domain.name').withTitle('Domain'))
		.column($dt.column('engine.id').withTitle('Engine'))
		.column($dt.column('mathModelRevision.id').withTitle('Math Model Revision: ID'))
		.column($dt.column('mathModelRevision.name').withTitle('Math Model Revision: Name'))
		.column($dt.column('pbCents').withTitle('Player Balance Cents'))
		.column($dt.column('winCents').withTitle('Win Cents'))
		.column($dt.linkscolumn(
			"",
			[
				{ 
					permission: "gogamegames_tutorials_*",
					permissionType: "any",
					title: "GLOBAL.ACTION.OPEN",
					href: function(data) {
						return $state.href("dashboard.gogamegames.tutorials.tutorial", { id:data.id });
					}
				}
			]
		))
		.options({ url: baseUrl, type: 'GET', data: function(d) { d.domains = controller.commaSeparatedSelectedDomains() } }, null, dtOptions, null)
		.build();
		
		controller.refresh = function() {
			controller.gogameTutorialsTable.instance.reloadData(function(){}, false);
		}
		
		$scope.$watch(function() { return controller.selectedDomains }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				controller.refresh();
			}
		});
		
		controller.add = function() {
			
		}
	}
]);
