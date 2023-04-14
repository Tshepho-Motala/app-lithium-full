'use strict'

angular.module('lithium').controller('UserLoginEventsController', ['user', '$translate', '$dt', 'DTOptionsBuilder', '$filter',
	function(user, $translate, $dt, DTOptionsBuilder, $filter) {
		var controller = this;
		var baseUrl = 'services/service-user/'+user.domain.name+'/users/'+user.id+'/loginevents/table';
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [0, 'desc']);
		controller.loginEventsTable = $dt.builder()
		.column($dt.columnformatdatetime('date').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.DATE')))
		.column($dt.columnformatcountryflag('countryCode').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.COUNTRY')))
		.column($dt.column('ipAddress').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.IPADDRESS')))
		.column($dt.column('successful').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.SUCCESSFUL')))
		.column($dt.column('comment').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.COMMENT')))
		.column($dt.column('user.username').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.USERNAME')))
		.column($dt.column('user.firstName').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.FIRSTNAME')))
		.column($dt.column('user.lastName').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.LOGINEVENTS.LASTNAME')))
		.options(baseUrl, null, dtOptions, null)
		.build();
	}
]);
