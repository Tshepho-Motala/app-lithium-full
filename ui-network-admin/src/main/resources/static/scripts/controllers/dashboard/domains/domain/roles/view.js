'use strict';

angular.module('lithium')
	.controller('DomainRoles', ["domain", "rest-domain", "notify", "$uibModal", "rest-roles", "$security", "$rootScope",
	function(domain, restDomain, notify, $uibModal, restRoles, security, $rootScope) {
		var controller = this;
		
		controller.listRoles = function() {
			restDomain.defaultroles(domain.name).then(function(roles) {
				controller.defaultroles = roles;
			});
		}
		
		controller.deleteRole = function(id) {
			restDomain.roledelete(domain.name, id).then(function() {
				notify.success("UI_NETWORK_ADMIN.DOMAIN.ROLES.SAVE.SUCCESS");
				controller.listRoles();
			}, function(response) {
				notify.warning("UI_NETWORK_ADMIN.DOMAIN.ROLES.UPDATE.FAIL");
			});
		}
		
		controller.changeEnabled = function(id, enabled) {
			restDomain.roleenable(domain.name, id, enabled).then(function() {
				notify.success("UI_NETWORK_ADMIN.DOMAIN.ROLES.UPDATE.SUCCESS");
			}, function(response) {
				notify.warning("UI_NETWORK_ADMIN.DOMAIN.ROLES.UPDATE.FAIL");
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
					},
					restRoles: restRoles
				}
			});
			modalInstance.result.then(function (response) {
				if (response != undefined) {
					console.log(domain.name);
					console.log(response.plain());
					restDomain.adddefaultroles(domain.name, response).then(function(response) {
						console.log(response.plain());
						controller.listRoles();
						notify.success("UI_NETWORK_ADMIN.DOMAIN.ROLES.ADD.SUCCESS");
					});
				}
			}, function () {
				controller.listRoles();
			});
		}
	}
]).controller('RoleAddModal', function ($uibModalInstance, $translate, $userService, notify, domainName, restRoles) {
	var controller = this;
	
	controller.getRoles = function() {
		console.log("getRoles");
		restRoles.listall().then(function(response) {
			console.log(response.plain());
			controller.roles = response;
		}, function(response) {
			notify.warning("Could not find group roles.");
		});
	}
	
	controller.addRoles = function() {
		$uibModalInstance.close(controller.roles);
	}
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
});
