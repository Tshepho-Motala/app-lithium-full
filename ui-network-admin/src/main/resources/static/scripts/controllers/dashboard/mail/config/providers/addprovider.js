'use strict';

angular.module('lithium')
	.controller('ProviderAdd', ["domainName", "$uibModalInstance", "mailRest", "notify", "errors",
	function(domainName, $uibModalInstance, mailRest, notify, errors) {
		var controller = this;
		
		controller.model = {};
		controller.options = {};
		
		controller.fields = [{
			"className":"col-xs-12",
			"type":"input",
			"key":"description",
			"templateOptions":{
				"type":"",
				"label":"",
				"required":true,
				"placeholder":"",
				"description":"",
				"options":[]
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.MAIL.PROVIDERS.FIELDS.DESCRIPTION.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.MAIL.PROVIDERS.FIELDS.DESCRIPTION.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.MAIL.PROVIDERS.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
			}
		},{
			"className":"col-xs-12 form-group",
			"type":"ui-select-single",
			"key":"provider",
			"templateOptions":{
				"label":"",
				"placeholder":"",
				"description":"",
				"required":true,
				"optionsAttr": "bs-options",
				"valueProp": "id",
				"labelProp": "name",
				"options":[]
			},
			"expressionProperties": {
				'templateOptions.label': '"UI_NETWORK_ADMIN.MAIL.PROVIDERS.FIELDS.PROVIDER.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.MAIL.PROVIDERS.FIELDS.PROVIDER.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.MAIL.PROVIDERS.FIELDS.PROVIDER.DESCRIPTION" | translate'
			},
			controller: ['$scope', function($scope) {
				mailRest.providers().then(function(response) {
					$scope.options.templateOptions.options = response;
					return response;
				});
			}]
		}];
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			mailRest.domainProviderAdd(domainName, controller.model.description, controller.model.provider).then(function(domainProvider) {
				notify.success("UI_NETWORK_ADMIN.MAIL.PROVIDERS.ADD.SUCCESS");
				$uibModalInstance.close(domainProvider);
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.MAIL.PROVIDERS.ADD.ERROR");
				errors.catch("", false)(error)
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);