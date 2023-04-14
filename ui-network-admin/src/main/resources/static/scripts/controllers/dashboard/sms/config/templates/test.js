'use strict';

angular.module('lithium')
	.controller('TemplateTest', ["template", "$uibModal", "$uibModalInstance", "notify", "errors", "SMSTemplateRest",
	function(template, $uibModal, $uibModalInstance, notify, errors, smsTemplateRest) {
		var controller = this;
		
		controller.fields = [{
			className: "col-xs-12",
			key: "recipientMobile",
			type: "input",
			templateOptions: {
				label: "", description: "", placeholder: "", required: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.SMS.TEMPLATES.TEST.FIELDS.RECIPIENTMOBILE.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.SMS.TEMPLATES.TEST.FIELDS.RECIPIENTMOBILE.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.SMS.TEMPLATES.TEST.FIELDS.RECIPIENTMOBILE.DESCRIPTION" | translate'
			}
		}];
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			// var emailRegex = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
			// if (emailRegex.test(controller.model.recipientEmail) === false) {
			// 	notify.error("Enter a valid mobile number");
			// 	return false;
			// }
			smsTemplateRest.testSMSTemplate(template.id, controller.model.recipientMobile).then(function(response) {
				notify.success("SMS successfully added to queue");
				$uibModalInstance.close(response);
			}).catch(function(error) {
				notify.error("There was an error whilst trying to send a test email.");
				errors.catch("", false)(error)
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);