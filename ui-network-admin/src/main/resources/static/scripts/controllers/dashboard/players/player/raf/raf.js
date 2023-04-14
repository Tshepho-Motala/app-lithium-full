'use strict'

angular.module('lithium').controller('PlayerRAFController', ['user', '$translate', '$dt', 'DTOptionsBuilder', '$filter',
	function(user, $translate, $dt, DTOptionsBuilder, $filter) {
		var controller = this;
		
		var baseUrl = 'services/service-raf/referrals/table';
		baseUrl += '?guid='+user.guid;
		
		var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [0, 'desc']);
		controller.rafTable = $dt.builder()
		.column($dt.columnformatdatetime('timestamp').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.RAF.DATE')))
		.column($dt.column('playerGuid').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.RAF.PLAYER')))
		.column($dt.column('converted').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.RAF.CONV')))
		.options(baseUrl, null, dtOptions, null)
		.build();
	}
]);
