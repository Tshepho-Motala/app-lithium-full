'use strict';

angular.module('lithium').controller('ReportActionEmailTemplateModal', ["$uibModalInstance", "notify", "emailTemplate", '$translate',
function ($uibModalInstance, notify, emailTemplate, $translate) {
	let controller = this;
	
	controller.model = { emailTemplateName: emailTemplate };
	
	controller.fields = [{
			key: "emailTemplateName",
			type: "input",
			templateOptions: {
				label: "", description: "", placeholder: ""
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.ACTION.EMAILTEMPLATE.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.ACTION.EMAILTEMPLATE.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.ACTION.EMAILTEMPLATE.DESCRIPTION" | translate'
			}
		}
	];
	
	controller.save = function() {
		console.log(controller.model);
		$uibModalInstance.close(controller.model.emailTemplateName);
	}
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	}
}]);
