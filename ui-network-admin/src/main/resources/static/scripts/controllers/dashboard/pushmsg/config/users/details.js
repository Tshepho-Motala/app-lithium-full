'use strict';

angular.module('lithium').controller('PushMsgUserDetails', ["domainName", "guid", "$translate", "$uibModalInstance", "rest-pushmsg", "$state", "$rootScope", "$dt",
	function(domainName, guid, $translate, $uibModalInstance, pmRest, $state, $rootScope, $dt) {
		var controller = this;
		
		console.log(domainName, guid);
		
		var baseUrl = "services/service-pushmsg/"+ domainName +"/pushmsgusers/details/table?guid="+guid;
		
		controller.table = $dt.builder()
		.column($dt.column('uuid').withTitle($translate("UI_NETWORK_ADMIN.PUSHMSG.USERS.DETAILS.UUID")))
		.column($dt.column('ip').withTitle($translate("UI_NETWORK_ADMIN.PUSHMSG.USERS.DETAILS.IP")))
		.column($dt.column('deviceOs').withTitle($translate("UI_NETWORK_ADMIN.PUSHMSG.USERS.DETAILS.OS")))
		.column($dt.column('deviceType').withTitle($translate("UI_NETWORK_ADMIN.PUSHMSG.USERS.DETAILS.TYPE")))
		.column($dt.column('deviceModel').withTitle($translate("UI_NETWORK_ADMIN.PUSHMSG.USERS.DETAILS.MODEL")))
//		.column($dt.columnformatdate('lastActive').withTitle($translate("UI_NETWORK_ADMIN.COMMENTS.COMMENTED_ON")))
		.options(baseUrl)
		.build();
		
		pmRest.userDetails(domainName, guid).then(function(list) {
			console.log(list.plain());
		});
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);