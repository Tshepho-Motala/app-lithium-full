'use strict';

angular.module('lithium')
	.controller('DomainClientsListController', ['domain', 'notify', '$scope', 'errors', '$dt', '$uibModal','$state', '$translate',
	function(domain, notify, $scope, errors, $dt, $uibModal, $state, $translate) {
		var controller = this;
		var url = 'services/service-domain/domain/providerauthclient/'+domain.name+'/table?1=1';
		controller.domainUserClientsTable = $dt.builder()
		.column($dt.column('code').withTitle($translate('UI_NETWORK_ADMIN.DOMAIN.CLIENTS.FIELDS.CODE.NAME')))
		.column(
			$dt.linkscolumn(
				"",
				[
					{
						permission: "DOMAIN_PROVIDERAUTH_VIEW",
						permissionType: "any",
						permissionDomain: domain.name,
						title: "GLOBAL.ACTION.OPEN",
						href: function(data) {
							return $state.href("dashboard.domains.domain.clients.client", { id: data.id });
						}
					}
				]
			)
		)
		.column($dt.column('guid').withTitle($translate('UI_NETWORK_ADMIN.DOMAIN.CLIENTS.FIELDS.GUID.NAME')))
		.column($dt.columnlength('description').withTitle($translate('UI_NETWORK_ADMIN.DOMAIN.CLIENTS.FIELDS.DESCRIPTION.NAME')))
		.options(url) //, controller.rowClickHandler)
		.order([0, 'asc'])
		.build();

		controller.add = function() {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				backdrop: 'static',
				// templateUrl: 'scripts/controllers/dashboard/domains/domain/currencies/add/add.html',
				templateUrl: 'scripts/controllers/dashboard/domains/domain/clients/add/add.html',
				controller: 'DomainClientAddModal',
				controllerAs: 'controller',
				size: 'md',
				resolve: {
					domain: function () {
						return domain;
					}
				}
			});
			
			modalInstance.result.then(function(result) {
				controller.domainUserClientsTable.instance.reloadData(function(){}, false);
			});
		}
	}
]);
