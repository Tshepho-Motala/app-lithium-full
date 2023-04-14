'use strict';

angular.module('lithium')
	.controller('EditProviderController', ["domainProvider", "rest-sms", "notify", "errors", "$uibModalInstance",
	function(domainProvider, smsRest, notify, errors, $uibModalInstance) {
		var controller = this;
		
		controller.model = {};
		controller.options = {};
		
		controller.model = domainProvider;
		
		console.log(domainProvider);
		
		controller.fields = [{
			"className":"col-xs-12",
			"type":"input",
			"key":"provider.name",
			"templateOptions":{
				"label":"",
				"placeholder":"",
				"description":"",
				"required": true,
				"disabled": true,
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.SMS.PROVIDERS.FIELDS.PROVIDER.NAME" | translate'
			}
		},{
			"className":"col-xs-12 form-group",
			"type":"input",
			"key":"description",
			"templateOptions": {
				"label": "",
				"placeholder": "",
				"description": "",
				"required": false,
				"disabled": false,
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.SMS.PROVIDERS.FIELDS.DESCRIPTION.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.SMS.PROVIDERS.FIELDS.DESCRIPTION.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.SMS.PROVIDERS.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
			}
		},{
			"className":"col-xs-12",
			"type": "accessRule",
			"key": "accessRule",
			"templateOptions": {
				"required": false
			}
		}];
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			smsRest.domainProviderUpdate(controller.model).then(function(dp) {
				notify.success("UI_NETWORK_ADMIN.SMS.PROVIDERS.EDIT.SUCCESS");
				$uibModalInstance.close(dp);
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.SMS.PROVIDERS.EDIT.ERROR");
				errors.catch("", false)(error)
			});
		}
	}
]);