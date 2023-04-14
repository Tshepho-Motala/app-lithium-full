'use strict';

angular.module('lithium').controller('ReportActionNotificationNameModal', ["$uibModalInstance", "notify", "notificationName", '$translate',
function ($uibModalInstance, notify, notificationName, $translate) {
	var controller = this;
	
	controller.model = { notificationName: notificationName };
	
	controller.fields = [{
			key: "notificationName",
			type: "input",
			templateOptions: {
				label: "", description: "", placeholder: ""
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.ACTIONS.TABLE.NOTIFICATION_NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.ACTIONS.TABLE.NOTIFICATION_NAME_DESCRIPTION" | translate'
			}
		}
	];
	
	controller.save = function() {
		console.log(controller.model);
		$uibModalInstance.close(controller.model.notificationName);
	}
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	}
}]);
