'use strict';

angular.module('lithium').controller('AccessRulesList', ["$scope", "$userService", "$translate", "$log", "$dt", "$state", "$rootScope", "$uibModal", "DTOptionsBuilder",
	function($scope, $userService, $translate, $log, $dt, $state, $rootScope, $uibModal, DTOptionsBuilder) {
		var controller = this;
		
		controller.domains = $userService.domainsWithAnyRole(["ADMIN", "ACCESSRULES_VIEW"]);
		
		controller.domainSelect = function() {
			controller.selectedDomains = [];
			controller.selectedDomainsCommaSeperated = '';
			for (var d = 0; d < controller.domains.length; d++) {
				if (controller.domains[d].selected) {
					controller.selectedDomains.push(controller.domains[d].name);
					if (controller.selectedDomainsCommaSeperated.length > 0) {
						controller.selectedDomainsCommaSeperated += ",";
					}
					controller.selectedDomainsCommaSeperated += controller.domains[d].name;
				}
			}
			if (controller.selectedDomains.length == controller.domains.length) {
				controller.selectedDomainsDisplay = "All Domains";
			} else {
				controller.selectedDomainsDisplay = "Selected (" + controller.selectedDomains.length + ")";
			}
		};
		
		controller.domainSelectAll = function() {
			for (var d = 0; d < controller.domains.length; d++) controller.domains[d].selected = true;
			controller.domainSelect();
		};
		
		controller.domainSelectAll();
		
		var baseUrl = "services/service-access/accessrules/table";

		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('createdRow', createdRow).withOption('order', [[4, 'desc']]); //,[2, 'desc'],[0, 'desc']]);

		controller.table = $dt.builder()
			.column($dt.column('name').withTitle("Name"))
			.column($dt.linkscolumn("", [{ permission: "accessrules_view", permissionType:"any", permissionDomain: function(data) { return data.domain.name;}, title: "GLOBAL.ACTION.OPEN", href: function(data) { return $state.href("^.view", {id:data.id, domainName:data.domain.name}) } }]))
			.column($dt.column('domain.name').withTitle("Domain"))
			.column($dt.column('defaultAction').withTitle("Default Rule"))
			.column($dt.column('enabled').withTitle("Enabled"))
			.options({ url: baseUrl, type: 'GET', data: function(d) { d.domainNamesCommaSeperated = controller.selectedDomainsCommaSeperated } }, null, dtOptions, null)
			.build();

		function createdRow(row, data, dataIndex) {
			if (!data.enabled){
				$(row).addClass('danger danger-modal danger-text');
			}
		}

		$scope.$watch(function() { return controller.selectedDomains }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				controller.table.instance.reloadData(function(){}, false);
			}
		});
		
		controller.addModal = function () {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/accesscontrol/rules/add/add.html',
				controller: 'AccessRulesAddModal',
				controllerAs: 'controller',
				size: 'md cascading-modal',
				backdrop: 'static',
				resolve: {
					domains: function() {
						return controller.domains;
					}
				}
			});
			
			modalInstance.result.then(function (accessRule) {
				$state.go("^.view", { id: accessRule.id });
			});
		};

		// Domain select
		$rootScope.provide.dropDownMenuProvider['domainList']  = () => {
			return controller.domains
		}
		$rootScope.provide.dropDownMenuProvider['domainsChange'] = (data) => {
			const domainNames = []
			data.forEach(el=> {
				domainNames.push(el.name)
			})
			controller.selectedDomains = domainNames
			controller.selectedDomainsCommaSeperated = domainNames.join(',')
			controller.table.instance.rerender(true);
		}

		window.VuePluginRegistry.loadByPage("DomainSelect")

	}
]);
