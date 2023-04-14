'use strict';

angular.module('lithium')
	.controller('TemplateAdd', ["domainName", "$uibModalInstance", "SMSTemplateRest", "notify", "errors", "$q",
	function(domainName, $uibModalInstance, rest, notify, errors, $q) {
		var controller = this;
		
		controller.model = { domain: { name: domainName }, lang: "en", enabled: true };
		controller.options = {};
		
		controller.fields = 
		[
			{
				className: "col-xs-12",
				key: "name",
				type: "input",
				templateOptions: {
					label: "", description: "", placeholder: "",
					required: true, minlength: 2, maxlength: 100
				},
				modelOptions: {
					updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.SMS.TEMPLATES.FIELDS.NAME.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.SMS.TEMPLATES.FIELDS.NAME.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.SMS.TEMPLATES.FIELDS.NAME.DESCRIPTION" | translate'
				},
				asyncValidators: {
					nameUnique: {
						expression: function($viewValue, $modelValue, scope) {
							var success = false;
							return rest.findByNameAndLangAndDomainName(encodeURIComponent($viewValue), controller.model.lang, domainName).then(function(smsTemplate) {
								if (angular.isUndefined(smsTemplate) || (smsTemplate._status == 404) || (smsTemplate.length === 0)) {
									success = true;
								}
							}).catch(function() {
								scope.options.validation.show = true;
								errors.catch("UI_NETWORK_ADMIN.SMS.TEMPLATES.FIELDS.NAME.UNIQUE", false);
							}).finally(function () {
								scope.options.templateOptions.loading = false;
								if (success) {
									return $q.resolve("No such sms template");
								} else {
									return $q.reject("The sms template already exists");
								}
							});
						},
						message: '"UI_NETWORK_ADMIN.SMS.TEMPLATES.FIELDS.NAME.UNIQUE" | translate'
					}
				}
			},
			{
				className: "col-xs-12",
				key: "lang",
				type: "ui-select-single",
				templateOptions: {
					label: "", description: "", placeholder: "",
					required: true,
					optionsAttr: 'bs-options',
					valueProp : 'locale2',
					labelProp : 'description',
					optionsAttr: 'ui-options', ngOptions: 'ui-options',
					options: []
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.SMS.TEMPLATES.FIELDS.LANG.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.SMS.TEMPLATES.FIELDS.LANG.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.SMS.TEMPLATES.FIELDS.LANG.DESCRIPTION" | translate'
				},
				controller: ['$scope', '$http', function($scope, $http) {
					$http.get("services/service-translate/apiv1/languages/all").then(function(response) {
						$scope.to.options = response.data;
					});
				}]
			},
			{
				className: "col-xs-12",
				key: "current.description",
				type: "input",
				optionsTypes: ['editable'],
				templateOptions: {
					label: "", description: "", placeholder: ""
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.SMS.TEMPLATES.FIELDS.DESCRIPTION.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.SMS.TEMPLATES.FIELDS.DESCRIPTION.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.SMS.TEMPLATES.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
				}
			},
			{
				className: 'pull-left',
				type: 'checkbox',
				key: 'enabled',
				templateOptions: {
					label: '', description: ''
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.SMS.TEMPLATES.FIELDS.ENABLED.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.SMS.TEMPLATES.FIELDS.ENABLED.DESCRIPTION" | translate'
				}
			}
		];
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			rest.add(controller.model, domainName).then(function(response) {
				notify.success("UI_NETWORK_ADMIN.SMS.TEMPLATES.SUCCESS.ADD");
				$uibModalInstance.close(response);
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.SMS.TEMPLATES.ERRORS.ADD");
				errors.catch("", false)(error)
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);