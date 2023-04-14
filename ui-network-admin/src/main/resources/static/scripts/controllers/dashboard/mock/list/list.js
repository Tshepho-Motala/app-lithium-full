'use strict'

angular.module('lithium').controller('GameTestingList', ['$uibModal', '$log', '$userService', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$scope', '$state',
	function($uibModal, $log, $userService, $translate, $dt, DTOptionsBuilder, $filter, $scope, $state) {
		var controller = this;

		controller.domains = $userService.domainsWithAnyRole(["ADMIN", "ACCESSCONTROL_VIEW"]);
		
		controller.domainSelect = function() {
			controller.selectedDomains = [];
			controller.selectedDomainsCommaSeperated = '';
			for (var d = 0; d < controller.domains.length; d++) {
				if (controller.domains[d].selected) {
					controller.selectedDomains.push(controller.domains[d].name);
					//if (controller.selectedDomainsCommaSeperated.length > 0) {
						//controller.selectedDomainsCommaSeperated += ",";
					//}
					controller.selectedDomainsCommaSeperated = controller.domains[d].name;
				}
			}
			if (controller.selectedDomains.length == controller.domains.length) {
				controller.selectedDomainsDisplay = "All Domains";
			} else {
				controller.selectedDomainsDisplay = "Selected (" + controller.selectedDomains.length + ")";
			}
		};
		
		// controller.domainSelectAll = function() {
		// 	for (var d = 0; d < controller.domains.length; d++) controller.domains[d].selected = true;
		// 	controller.domainSelect();
		// };
		//
		// controller.domainSelectAll();
		
		var baseUrl = 'services/service-access/lists/table';
		controller.accessControlListTable = $dt.builder()
		.column($dt.column('name').withTitle($translate('UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.NAME.LABEL')))
		.column($dt.linkscolumn("", [{ permission: "accesscontrol_view", permissionType:"any", permissionDomain: function(data) { return data.domain.name;}, title: "GLOBAL.ACTION.OPEN", href: function(data) { return $state.href("^.view", {id:data.id, domainName:data.domain.name}) } }]))
		.column($dt.column('domain.name').withTitle($translate('UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.DOMAIN.LABEL')))
		.column($dt.column('listType.displayName').withTitle($translate('UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.TYPE.LABEL')))
		.column($dt.column('enabled').withTitle($translate('UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.ENABLED.LABEL')))
		.options({ url: baseUrl, type: 'GET', data: function(d) { d.domainNamesCommaSeperated = controller.selectedDomainsCommaSeperated } }, null, null, null)
		.build();
		
		$scope.$watch(function() { return controller.selectedDomains }, function(newValue, oldValue) {
			if (newValue != oldValue) {
				controller.accessControlListTable.instance.reloadData(function(){}, false);
			}
		});
		
		controller.addModal = function () {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				backdrop: 'static',
				templateUrl: 'scripts/controllers/dashboard/accesscontrol/lists/add/add.html',
				controller: 'AccessControlAddModal',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					domains: function() {
						return controller.domains;
					},
					types: ["accessControlRest", function(accessControlRest) {
						return accessControlRest.findAllListTypes().then(function(data) {
							return data;
						}).catch(function(error) {
							errors.catch("", false)(error)
						});
					}]
				}
			});
			
			modalInstance.result.then(function (list) {
				$state.go("^.view", { id: list.id });
			});
		};
	}
]);
