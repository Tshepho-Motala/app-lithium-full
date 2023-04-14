'use strict';

angular.module('lithium').controller('ReportRecipientIaAddModal', ["$uibModalInstance", "notify", "recipients",
function ($uibModalInstance, notify, recipients) {
	var controller = this;
	
	controller.recipients = recipients;
	
	controller.model = {};
	
	controller.fields = [{
			key: "recipient",
			type: "input",
			templateOptions: {
				label: "", description: "", placeholder: ""
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.IA.FIELDS.ACTION.RECIPIENT.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.IA.FIELDS.ACTION.RECIPIENT.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.IA.FIELDS.ACTION.RECIPIENT.DESCRIPTION" | translate'
			}
		}
	];
	
	controller.deleteRecipient = function(recipient) {
		for (var i = 0; i < controller.recipients.length; i++) {
			if (controller.recipients[i] === recipient) {
				controller.recipients.splice(i, 1);
				break;
			}
		}
	}
	
	controller.addRecipient = function() {
		var emailRegex = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
		if (emailRegex.test(controller.model.recipient) === false) {
			notify.error("Enter a valid email address");
			return false;
		}
		for (var i = 0; i < controller.recipients.length; i++) {
			if (controller.recipients[i] === controller.model.recipient) return;
		}
		controller.recipients.push(controller.model.recipient);
	}
	
	controller.save = function() {
		$uibModalInstance.close(controller.recipients);
	}
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	}
}]);
