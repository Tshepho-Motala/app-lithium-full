'use strict';

angular.module('lithium')
	.controller('UsersList', ["domain", "$uibModal", "$translate", "$log", "$dt", "$state", "$rootScope", "notify", function(domain, $uibModal, $translate, $log, $dt, $state, $rootScope, notify) {
			
		var controller = this;
		var baseUrl = "services/service-user/" + domain.name + "/users/table?1=1";
		
		controller.domain = domain;
		
		controller.usersTable = $dt.builder()
			.column($dt.column('username').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.USERNAME.NAME")))
			.column($dt.column('domain.name').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.DOMAIN.NAME")))
			.column($dt.columnWithClass('status.name', 'label label-lg label-default').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.STATUS.NAME")))
			.column($dt.column('firstName').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.FIRSTNAME.NAME")))
			.column($dt.column('lastName').withTitle($translate("UI_NETWORK_ADMIN.USER.FIELDS.LASTNAME.NAME")))
			.options(baseUrl, function(data) { $state.go("^.user", {id:data.id}) } )
			.build();

		controller.addModal = function () {
			var modalInstance = $uibModal.open({
				animation: true,
				ariaLabelledBy: 'modal-title',
				ariaDescribedBy: 'modal-body',
				templateUrl: 'scripts/controllers/dashboard/users/add/add.html',
				controller: 'UserAddModal',
				controllerAs: 'controller',
				backdrop: 'static',
				size: 'md',
				resolve: {
					domain: function () {
						return domain;
					}
				}
			});
			
			modalInstance.result.then(function (user) {
				$state.go("^.user", { id: user.id });
			});
		};

}]);
