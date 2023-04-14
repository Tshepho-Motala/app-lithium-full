'use strict';

angular.module('lithium')
	.controller('DefaultSMSTemplates', ["domainName", "$translate", "$log", "$dt", "$state", "$rootScope", "$uibModal", function(domainName, $translate, $log, $dt, $state, $rootScope, $uibModal) {
		var controller = this;
		var baseUrl = "services/service-sms/defaultsmstemplates/table";
		
		controller.rowClickHandler = function(data) {
			$state.go('dashboard.sms.config.defaulttemplates.view', {
				id: data.id,
				domainName: domainName
			});
		}
		
		controller.table = $dt.builder()
			.column($dt.column('name').withTitle($translate("UI_NETWORK_ADMIN.SMS.TEMPLATES.FIELDS.NAME.NAME")))
			.column($dt.column('description').withTitle($translate("UI_NETWORK_ADMIN.SMS.TEMPLATES.FIELDS.DESCRIPTION.NAME")))
			.options(baseUrl, controller.rowClickHandler)
			.build();
}]);