'use strict'

angular.module('lithium').controller('PlayerSMSHistoryController', ['$log', '$translate', '$dt', 'DTOptionsBuilder', '$filter', '$state', 'user',
	function($log, $translate, $dt, DTOptionsBuilder, $filter, $state, user) {
		var controller = this;
		
		var baseUrl = 'services/service-sms/sms/findByUser/table?userGuid='+user.guid;
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [0, 'desc']);
		controller.smsHistoryTable = $dt.builder()
		.column($dt.columnformatdatetime('createdDate').withTitle($translate('UI_NETWORK_ADMIN.SMSQUEUE.SMS.CREATEDDATE')))
		.column($dt.linkscolumn("", [{ permission: "player_view", permissionType:"any", permissionDomain: function(data) { return data.domain.name;}, title: "GLOBAL.ACTION.OPEN", href: function(data) { return $state.href('^.view', {smsId:data.id}) } }]))
		.column($dt.columnformatdatetime('sentDate').withTitle($translate('UI_NETWORK_ADMIN.SMSQUEUE.SMS.SENTDATE')))
		.column($dt.column('from').withTitle($translate('UI_NETWORK_ADMIN.SMSQUEUE.SMS.FROM')))
		.column($dt.column('to').withTitle($translate('UI_NETWORK_ADMIN.SMSQUEUE.SMS.TO')))
		.options(baseUrl, null, dtOptions, null)
		.build();
	}
]);
