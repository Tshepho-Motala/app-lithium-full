'use strict';

angular.module('lithium').controller('ReportActionSMSTemplateModal', ["$uibModalInstance", "notify", "smsTemplate", '$translate',
function ($uibModalInstance, notify, smsTemplate, $translate) {
	let controller = this;
	
	controller.model = { smsTemplateName: smsTemplate };
	
	controller.fields = [{
			key: "smsTemplateName",
			type: "input",
			templateOptions: {
				label: "", description: "", placeholder: ""
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.ACTION.SMSTEMPLATE.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.ACTION.SMSTEMPLATE.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.ACTION.SMSTEMPLATE.DESCRIPTION" | translate'
			}
		}
	];
	
	controller.save = function() {
		console.log(controller.model);
		$uibModalInstance.close(controller.model.smsTemplateName);
	}
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	}
}]);
