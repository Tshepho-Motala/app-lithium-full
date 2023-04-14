'use strict';

angular.module('lithium')
	.controller('TransactionTypeAccounts', ["domain", "rest-domain", "notify", "$uibModal", "rest-tranta", "$security", "$rootScope",
	function(domain, restDomain, notify, $uibModal, restTranta, security, $rootScope) {
		var controller = this;
		controller.listTranTas = function() {
			restTranta.all(domain.name).then(function(response) {
				controller.ttas = response;
			});
		}
		
		controller.roleAddModal = function() {
			var modalInstance = $uibModal.open({
				animation: false,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/domains/domain/roles/add.html',
				controller: 'RoleAddModal',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'lg',
				resolve: {
					domainName: function () {
						return domain.name;
					}
				}
			});
			modalInstance.result.then(function (response) {
				if (response != undefined) {
					console.log(domain.name);
					console.log(response.plain());
//					restDomain.adddefaultroles(domain.name, response).then(function(response) {
//						console.log(response.plain());
//						controller.listRoles();
//						notify.success("UI_NETWORK_ADMIN.DOMAIN.ROLES.ADD.SUCCESS");
//					});
				}
			}, function () {
				controller.listRoles();
			});
		}
	}
]).controller('TranTaAddModal', function ($uibModalInstance, $translate, $userService, notify, domainName, restRoles) {
	var controller = this;
	
	controller.addTranTa = function() {
		$uibModalInstance.close(controller.roles);
	}
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
});
