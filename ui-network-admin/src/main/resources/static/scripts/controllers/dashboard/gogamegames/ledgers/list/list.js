'use strict'

angular.module('lithium').controller('GoGameLedgersListController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$uibModal', '$userService', '$state', '$scope',
	function($log, $translate, $dt, DTOptionsBuilder, $filter, $uibModal, $userService, $state, $scope) {
		var controller = this;
		
		controller.domains = $userService.domainsWithAnyRole(["ADMIN", "GOGAMEGAMES_LEDGERS_*"]);
		
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
		
		var baseUrl = 'services/service-casino-provider-gogame/admin/ledgers/table';
		
		var dtOptions = null; //DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[0, 'asc']]);
		controller.gogameLedgersTable = $dt.builder()
//		.column($dt.column('id').withTitle('ID'))
		.column($dt.column('domain.name').withTitle('Domain'))
		.column($dt.column('name').withTitle('Name'))
		.column($dt.linkscolumn(
			"",
			[
				{ 
					permission: "gogamegames_ledgers_*",
					permissionType: "any",
					title: "GLOBAL.ACTION.OPEN",
					href: function(data) {
						return $state.href("dashboard.gogamegames.ledgers.ledger", { id:data.id });
					}
				}
			]
		))
		.column($dt.column('description').withTitle('Description'))
		.column($dt.column('currencyCode').withTitle('Currency'))
		.column($dt.columncurrencysymbol('totalPlay', '$', 2).withTitle('Total Play'))
		.column($dt.column('engine.id').withTitle('Engine'))
		.column($dt.columnformatdatetime('lastValidated').withTitle('Last Validated'))
		.column(
			$dt.labelcolumn(
				'Validity',
				[{lclass: function(data) {
					switch (data.valid) {
						case true: return "success";
						case false: return "danger";
						default: return "";
					}
				},
				text: function(data) {
					switch (data.valid) {
						case true: return "valid";
						case false: return "invalid";
						default: return "";
					}
				},
				uppercase:true
				}]
			)
		)
		.options({ url: baseUrl, type: 'GET', data: function(d) { d.domains = controller.commaSeparatedSelectedDomains() } }, null, dtOptions, null)
		.build();
		
		controller.refresh = function() {
			controller.gogameLedgersTable.instance.reloadData(function(){}, false);
		}
		
		$scope.$watch(function() { return controller.selectedDomains }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				controller.refresh();
			}
		});
		
		controller.addLedger = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/gogamegames/ledgers/add/add.html',
				controller: 'GoGameLedgersAddController',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					ledger: function() { return null; },
					loadMyFiles:function($ocLazyLoad) {
						return $ocLazyLoad.load({
							name:'lithium',
							files: [
								'scripts/controllers/dashboard/gogamegames/ledgers/add/add.js'
							]
						})
					}
				}
			});
			
			modalInstance.result.then(function(response) {
				controller.refresh();
				$state.go("dashboard.gogamegames.ledgers.ledger", { id:response.id });
			});
		}
	}
]);
