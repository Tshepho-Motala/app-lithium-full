'use strict';

angular.module('lithium')
	.controller('SendPlayerSMS', ["$uibModalInstance", "notify", "errors", "user", "rest-sms",
	function($uibModalInstance, notify, errors, user, smsRest) {
		var controller = this;
		
		controller.model = {};
		controller.options = {};
		
		controller.fields = [
			{
				className : 'col-xs-12',
				key: "text",
				type: "textarea",
				templateOptions: {
					label: "Text",
					description: "The SMS content",
					placeholder: "",
					required: true
				}
			}
		];
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}

			smsRest.saveForPlayerWithText(user.guid, controller.model.text).then(function(response) {
				if (response._status !== undefined && response._status !== 0) {
					notify.error("Failed to queue sms for player. " + response._message);
					$uibModalInstance.close();
				} else {
					notify.success("Successfully queued sms for player.");
					$uibModalInstance.close(response);
				}
			}).catch(function(error) {
				console.error(error);
				notify.error("Failed to queue sms for player.");
				errors.catch("", false)(error)
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);