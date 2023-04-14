'use strict';

angular.module('lithium').controller('EmailTemplateEdit', ['template','EmailTemplateRest','notify','$q','$state','errors','$uibModal',  function(template,rest,notify,$q,$state,errors, $uibModal) {
	var controller = this;
	controller.model = template;
	const templateName = template.name;
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
				'templateOptions.label': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.NAME.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.NAME.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.NAME.DESCRIPTION" | translate'
			},
			asyncValidators: {
				nameUnique: {
					expression: function($viewValue, $modelValue, scope) {
						var success = false;
						return rest.findByNameAndLangAndDomainName(encodeURIComponent($viewValue), controller.model.lang, template.domain.name).then(function(emailTemplate) {
							if (angular.isUndefined(emailTemplate) || (emailTemplate._status == 404) || (emailTemplate.length === 0)) {
								success = true;
							}
							if (emailTemplate != null && emailTemplate.name === $viewValue) {
								success = true;
							}
						}).catch(function() {
							scope.options.validation.show = true;
							errors.catch("UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.NAME.UNIQUE", false);
						}).finally(function () {
							scope.options.templateOptions.loading = false;
							if (success) {
								return $q.resolve("No such email template");
							} else {
								return $q.reject("The email template already exists");
							}
						});
					},
					message: '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.NAME.UNIQUE" | translate'
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
			key: "edit.subject",
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
            key: "edit.emailFrom",
            type: "input",
            optionsTypes: ['editable'],
            templateOptions: {
                label: "", description: "", placeholder: "", required: false
            },
            expressionProperties: {
                'templateOptions.label': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.FROM.NAME" | translate',
                'templateOptions.placeholder': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.FROM.NAME" | translate',
                'templateOptions.description': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.FROM.DESCRIPTION" | translate'
            },
            validators: {
                emailValid: {
                    expression: function($viewValue, $modelValue, scope) {
                        var value = $modelValue || $viewValue;
                        if (value == null || value!= undefined || value.length == 0) {
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
		},
		{
			className: "col-xs-12",
			key: "edit.body",
			type: "ckeditor",
			optionsTypes: ['editable'],
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: false, maxlength: 1000000,
				ckoptions: {
					fullPage: true,
					language: 'en',
					enterMode: CKEDITOR.ENTER_P,
					shiftEnterMode: CKEDITOR.ENTER_BR,
					allowedContent: true,
					entities: false,
					filebrowserBrowseUrl: rest.baseUrl + 'browser/browse.php',
					filebrowserUploadUrl: rest.baseUrl + 'uploader/upload.php'
				}
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.BODY.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.BODY.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.BODY.DESCRIPTION" | translate'
			}
		}
	];
	controller.onSubmit = function() {
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		
		let ckEditorInSrcMode = false;

	    for (var i in CKEDITOR.instances) {     
	      if (CKEDITOR.instances[i].mode === 'source') {            
	          ckEditorInSrcMode = true;
	          break;
	      }
	    }

	    if (ckEditorInSrcMode) {
	      notify.warning("UI_NETWORK_ADMIN.MAIL.TEMPLATES.FIELDS.CKEDITOR");
	      return;
	    }
		if(controller.model.name !== templateName){
			controller.templateNameChange(controller.model);
			return;
		}
		rest.save(controller.model).then(function(response) {
			notify.success("UI_NETWORK_ADMIN.MAIL.TEMPLATES.SUCCESS.SAVE");
			$state.go("^.view", { domainName:response.domain.name, id:response.id });
		}).catch(function(error) {
			notify.error("UI_NETWORK_ADMIN.MAIL.TEMPLATES.ERRORS.SAVE");
			errors.catch("", false)(error)
		});
	}
	
	controller.onContinue = function() {
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		
		let ckEditorInSrcMode = false;

	    for (var i in CKEDITOR.instances) {     
	      if (CKEDITOR.instances[i].mode === 'source') {            
	          ckEditorInSrcMode = true;
	          break;
	      }
	    }

	    if (ckEditorInSrcMode) {
	      notify.warning("The template content editor is still in source mode. Changes will not persist. Please switch the mode of the template content editor.");
	      return;
	    }
		
		rest.continueLater(controller.model).then(function(response) {
			notify.success("UI_NETWORK_ADMIN.MAIL.TEMPLATES.SUCCESS.SAVE");
			$state.go("^.view", { domainName:response.domain.name, id:response.id });
		}).catch(function(error) {
			notify.error("UI_NETWORK_ADMIN.MAIL.TEMPLATES.ERRORS.SAVE");
			errors.catch("", false)(error)
		});
	}
	
	controller.onCancel = function() {
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		rest.cancelEdit(controller.model).then(function(response) {
			notify.success("UI_NETWORK_ADMIN.MAIL.TEMPLATES.SUCCESS.SAVE");
			$state.go("^.view", { domainName:response.domain.name, id:response.id });
		}).catch(function(error) {
			notify.error("UI_NETWORK_ADMIN.MAIL.TEMPLATES.ERRORS.SAVE");
			errors.catch("", false)(error)
		});
	}

	controller.templateNameChange = function (data) {

		let modalInstance = $uibModal.open({
			animation: true,
			ariaLabelledBy: 'modal-title',
			ariaDescribedBy: 'modal-body',
			templateUrl: 'scripts/controllers/dashboard/mail/config/templates/emailTemplateModal.html',
			controller: 'emailTemplateModal',
			controllerAs: 'controller',
			size: 'md cascading-modal',
			backdrop: 'static',
			resolve: {
				formData: function () {
					return data;
				},
				loadMyFiles: function ($ocLazyLoad) {
					return $ocLazyLoad.load({
						name: 'lithium',
						files: [
							'scripts/controllers/dashboard/mail/config/templates/emailTemplateModal.js'
						]
					})
				}
			}
		});

		modalInstance.result.then(function (dm) {
		});
	};
}]);