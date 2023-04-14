'use strict';

angular.module('lithium')
	.controller('TemplateAdd', ["domainName", "$uibModalInstance", "EmailTemplateRest", "notify", "errors", "$q",
	function(domainName, $uibModalInstance, rest, notify, errors, $q) {
		let controller = this;
		
		controller.model = { domain: { name: domainName }, lang: "en", enabled: true, userOpenStatusOnly: false };
		controller.options = {};
		
		controller.fields = 
		[
			{
				className: "col-xs-12",
				key: "lang",
				type: "ui-select-single",
				templateOptions: {
					label: "", description: "", placeholder: "",
					required: true,
					valueProp : 'locale2',
					labelProp : 'description',
					optionsAttr: 'ui-options', ngOptions: 'ui-options',
					options: []
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.LANG.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.LANG.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.LANG.DESCRIPTION" | translate'
				},
				controller: ['$scope', '$http', function($scope, $http) {
					$http.get("services/service-translate/apiv1/languages/all").then(function(response) {
						$scope.to.options = response.data;
					});
				}]
			},
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
					'templateOptions.label': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.NAME.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.NAME.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.NAME.DESCRIPTION" | translate'
				},
				asyncValidators: {
					nameUnique: {
						expression: function($viewValue, $modelValue, scope) {
							let success = false;
							return rest.findByNameAndLangAndDomainName(encodeURIComponent($viewValue), controller.fields[0].value(), domainName).then(function(emailTemplate) {
								if (angular.isUndefined(emailTemplate) || (emailTemplate._status === 404) || (emailTemplate.length === 0)) {
									success = true;
								}
							}).catch(function() {
								scope.options.validation.show = true;
								errors.catch("UI_NETWORK_ADMIN.EMAILTEMPLATE.FIELDS.NAME.UNIQUE", false);
							}).finally(function () {
								scope.options.templateOptions.loading = false;
								if (success) {
									return $q.resolve("No such mail template");
								} else {
									return $q.reject("The mail template already exists");
								}
							});
						},
						message: '"UI_NETWORK_ADMIN.EMAILTEMPLATE.FIELDS.NAME.UNIQUE" | translate'
					}
				}
			},
			{
				className: "col-xs-12",
				key: "current.subject",
				type: "input",
				optionsTypes: ['editable'],
				templateOptions: {
					label: "", description: "", placeholder: "", required: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.SUBJECT.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.SUBJECT.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.SUBJECT.DESCRIPTION" | translate'
				}
			},
            {
                className: "col-xs-12",
                key: "current.emailFrom",
                type: "input",
                optionsTypes: ['editable'],
                templateOptions: {
                    label: "", description: "", placeholder: "",
                    required: false, minlength: 3
                },
                expressionProperties: {
                    'templateOptions.label': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.FROM.NAME" | translate',
                    'templateOptions.placeholder': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.FROM.NAME" | translate',
                    'templateOptions.description': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.FROM.DESCRIPTION" | translate'
                },
                validators: {
                    emailValid: {
                        expression: function($viewValue, $modelValue, scope) {
                            let value = $modelValue || $viewValue;
                            if (value !== undefined || value.length === 0) {
                                return true;
                            }
                            return /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(value);
                        },
                        message: '($viewValue != undefined && $viewValue != "")? $viewValue + " is not a valid email address" : ""'
                    }
                }
            },
			{
				className: 'pull-left',
				type: 'checkbox',
				key: 'userOpenStatusOnly',
				templateOptions: {
					label: '', description: ''
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.USER_OPEN_STATUS_ONLY.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.USER_OPEN_STATUS_ONLY.DESCRIPTION" | translate'
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
					'templateOptions.label': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.ENABLED.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.ENABLED.DESCRIPTION" | translate'
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
				notify.success("UI_NETWORK_ADMIN.MAIL.TEMPLATES.SUCCESS.ADD");
				$uibModalInstance.close(response);
			}).catch(function(error) {
				notify.error("UI_NETWORK_ADMIN.MAIL.TEMPLATES.ERRORS.ADD");
				errors.catch("", false)(error)
			});
		}
		
		controller.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	}
]);