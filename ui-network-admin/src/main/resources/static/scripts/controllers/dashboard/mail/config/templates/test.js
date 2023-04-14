'use strict';

angular.module('lithium')
	.controller('TemplateTest', ["template", "$uibModal", "$uibModalInstance", "notify", "errors", "EmailTemplateRest",
	function(template, $uibModal, $uibModalInstance, notify, errors, emailTemplateRest) {
		var controller = this;
		
		controller.fields = [{
			className: "col-xs-12",
			key: "recipientEmail",
			type: "input",
			templateOptions: {
				label: "", description: "", placeholder: "", required: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.TEST.FIELDS.RECIPIENTEMAIL.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.TEST.FIELDS.RECIPIENTEMAIL.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.TEST.FIELDS.RECIPIENTEMAIL.DESCRIPTION" | translate'
			}
		},{
			className: "col-xs-12",
			key: "transactionId",
			type: "input",
			templateOptions: {
				label: "", description: "", placeholder: "", required: false
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.TEST.FIELDS.TRANSACTION_ID.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.TEST.FIELDS.TRANSACTION_ID.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.TEST.FIELDS.TRANSACTION_ID.DESCRIPTION" | translate'
			}
		}];
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			var emailRegex = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
			if (emailRegex.test(controller.model.recipientEmail) === false) {
				notify.error("Enter a valid email address");
				return false;
			}
			emailTemplateRest.testEmailTemplate(template.id, controller.model.recipientEmail, controller.model.transactionId).then(function(response) {
				notify.success("Email successfully added to queue");
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