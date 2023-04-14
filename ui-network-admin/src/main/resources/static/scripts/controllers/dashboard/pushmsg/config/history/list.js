'use strict';

angular.module('lithium').controller('PushMsgHistory', ["domainName", "$translate", "$log", "$dt", "$state", "$rootScope", "$uibModal",
	function(domainName, $translate, $log, $dt, $state, $rootScope, $uibModal) {
		var controller = this;
		var baseUrl = "services/service-pushmsg/pushmsg/"+ domainName +"/table";
		
		controller.table = $dt.builder()
			.column($dt.column('id').withTitle($translate("UI_NETWORK_ADMIN.PUSHMSG.TBL.ID")))
			.column($dt.columnsize('users').withTitle($translate("UI_NETWORK_ADMIN.PUSHMSG.TBL.USERS")))
			.column($dt.columnformatdatetime('createdDate').withTitle($translate("UI_NETWORK_ADMIN.PUSHMSG.TBL.CREATED")))
			.column($dt.columnformatdatetime('sentDate').withTitle($translate("UI_NETWORK_ADMIN.PUSHMSG.TBL.SENT")))
			.column($dt.column('templateId').withTitle($translate("UI_NETWORK_ADMIN.PUSHMSG.TBL.TEMPLATE")))
			.column($dt.column('providerReference').withTitle($translate("UI_NETWORK_ADMIN.PUSHMSG.TBL.REF")))
			.column($dt.column('domainProvider.provider.name').withTitle($translate("UI_NETWORK_ADMIN.PUSHMSG.TBL.PROVIDER")))
			.column($dt.column('failed').withTitle($translate("UI_NETWORK_ADMIN.PUSHMSG.TBL.FAILED")))
//			.column($dt.linkscolumn("", [{ title: "GLOBAL.ACTION.OPEN", href: function(data) { return $state.href("dashboard.pushmsg.config.templates.view", {id: data.id, domainName: domainName}) } }]))
//			.column($dt.labelcolumn($translate("UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.FIELDS.ENABLED.TABLE"), [{lclass: function(data) { return (data.enabled === true)?"default":"danger"; }, text: function(data) { return (data.enabled === true)?"GLOBAL.FIELDS.ENABLED":"GLOBAL.FIELDS.DISABLED"; }, uppercase:true }]))
			.options(baseUrl)
			.build();
	}
]);