'use strict';

angular.module('lithium').controller('ReportActionNotificationNameModal', ["$uibModalInstance", "notify", "notificationName",
function ($uibModalInstance, notify, notificationName) {
	var controller = this;
	
	controller.model = { notificationName: notificationName };
	
	controller.fields = [{
			key: "notificationName",
			type: "input",
			templateOptions: {
				label: "Notification Name", description: "The name of the notification", placeholder: ""
			},
//			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.ACTION.SMSTEMPLATE.NAME" | translate',
//				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.ACTION.SMSTEMPLATE.PLACEHOLDER" | translate',
//				'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.ACTION.SMSTEMPLATE.DESCRIPTION" | translate'
//			}
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