'use strict';

angular.module('lithium')
	.controller('DefaultEmailTemplates', ["domainName", "$translate", "$log", "$dt", "$state", "$rootScope", "$uibModal", function(domainName, $translate, $log, $dt, $state, $rootScope, $uibModal) {
		var controller = this;
		var baseUrl = "services/service-mail/defaultemailtemplates/table";
		
		controller.rowClickHandler = function(data) {
			$state.go('dashboard.mail.config.defaulttemplates.view', {
				id: data.id,
				domainName: domainName
			});
		}
		
		controller.table = $dt.builder()
			.column($dt.column('name').withTitle($translate("UI_NETWORK_ADMIN.EMAILTEMPLATE.FIELDS.NAME.NAME")))
			.column($dt.column('subject').withTitle($translate("UI_NETWORK_ADMIN.EMAILTEMPLATE.FIELDS.SUBJECT.NAME")))
			.options(baseUrl,controller.rowClickHandler)
			.build();
}]);