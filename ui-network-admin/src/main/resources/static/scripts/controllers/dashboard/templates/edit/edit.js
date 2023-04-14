'use strict';

angular.module('lithium').controller('TemplatesEditController', ['template','TemplatesRest','rest-provider','cdn-google-rest','notify','$q','$state', "$scope",'errors',"domainName", function(template,rest, provider,cdn,notify,$q,$state, $scope,errors,domainName) {
	var controller = this;
	controller.model = template;
	controller.cdnUrl = '';
	$scope.cdnEnabled = false;
	$scope.loading = false;

	controller.fields =
		[
			{
				className: "col-xs-12",
				key: "name",
				type: "input",
				templateOptions: {
					label: "", description: "", placeholder: "",
					required: true, minlength: 2, maxlength: 100,
					disabled: false
				},
				modelOptions: {
					updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.TEMPLATES.FIELDS.NAME.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.TEMPLATES.FIELDS.NAME.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.TEMPLATES.FIELDS.NAME.DESCRIPTION" | translate',
					'templateOptions.disabled': function() {
						return new Promise((res,rej) => {
							
							checkProviders().then(() => {
								console.log(controller.cdnUrl)
								res(controller.cdnUrl && controller.cdnUrl.length >0)
							})
						})
					}
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
								errors.catch("UI_NETWORK_ADMIN.TEMPLATES.FIELDS.NAME.UNIQUE", false);
							}).finally(function () {
								scope.options.templateOptions.loading = false;
								if (success) {
									return $q.resolve("No such template");
								} else {
									return $q.reject("The template already exists");
								}
							});
						},
						message: '"UI_NETWORK_ADMIN.TEMPLATES.FIELDS.NAME.UNIQUE" | translate'
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
					'templateOptions.label': '"UI_NETWORK_ADMIN.TEMPLATES.FIELDS.LANG.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.TEMPLATES.FIELDS.LANG.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.TEMPLATES.FIELDS.LANG.DESCRIPTION" | translate'
				},
				controller: ['$scope', '$http', function($scope, $http) {
					$http.get("services/service-translate/apiv1/languages/all").then(function(response) {
						$scope.to.options = response.data;
					});
				}]
			},
			{
				className: "col-xs-12",
				key: "edit.description",
				type: "input",
				optionsTypes: ['editable'],
				templateOptions: {
					label: "", description: "", placeholder: "", required: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.TEMPLATES.FIELDS.DESCRIPTION.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.TEMPLATES.FIELDS.DESCRIPTION.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.TEMPLATES.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
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
					'templateOptions.label': '"UI_NETWORK_ADMIN.TEMPLATES.FIELDS.ENABLED.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.TEMPLATES.FIELDS.ENABLED.DESCRIPTION" | translate'
				}
			},
			{
				className: "col-xs-12",
				key: "edit.content",
				type: "ckeditor",
				optionsTypes: ['editable'],
				templateOptions: {
					label: "", description: "", placeholder: "",
					required: false, maxlength: 1000000,
					ckoptions: {
	//					fullPage: true,
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
					'templateOptions.label': '"UI_NETWORK_ADMIN.TEMPLATES.FIELDS.CONTENT.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.TEMPLATES.FIELDS.CONTENT.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.TEMPLATES.FIELDS.CONTENT.DESCRIPTION" | translate'
				}
			},
			{
				className: "col-xs-12",
				key: "edit.head",
				type: "textarea",
				optionsTypes: ['editable'],
				templateOptions: {
					label: "Head", description: "", placeholder: "", required: false
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.TEMPLATES.FIELDS.HEAD.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.TEMPLATES.FIELDS.HEAD.NAME" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.TEMPLATES.FIELDS.HEAD.DESCRIPTION" | translate'
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
			notify.warning("The template content editor is still in source mode. Changes will not persist. Please switch the mode of the template content editor.");
			return;
		}

		console.log(controller.model);
		rest.save(domainName, controller.model).then(function(response) {
			notify.success("UI_NETWORK_ADMIN.TEMPLATES.SUCCESS.SAVE");
			$state.go("^.view", { domainName:response.domain.name, id:response.id });
		}).catch(function(error) {
			notify.error("UI_NETWORK_ADMIN.TEMPLATES.ERRORS.SAVE");
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

		rest.continueLater(domainName, controller.model).then(function(response) {
			notify.success("UI_NETWORK_ADMIN.TEMPLATES.SUCCESS.SAVE");
			$state.go("^.view", { domainName:response.domain.name, id:response.id });
		}).catch(function(error) {
			notify.error("UI_NETWORK_ADMIN.TEMPLATES.ERRORS.SAVE");
			errors.catch("", false)(error)
		});
	}

	controller.onCancel = function() {
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		rest.cancelEdit(domainName, controller.model).then(function(response) {
			notify.success("UI_NETWORK_ADMIN.TEMPLATES.SUCCESS.SAVE");
			$state.go("^.view", { domainName:response.domain.name, id:response.id });
		}).catch(function(error) {
			notify.error("UI_NETWORK_ADMIN.TEMPLATES.ERRORS.SAVE");
			errors.catch("", false)(error)
		});
	}

	function checkProviders() {
		$scope.loading = true

		return provider.listByType(controller.model.domain.name, "CDN").then(function (provResponse) {
			angular.forEach(provResponse, function (prov) {
				$scope.cdnEnabled = prov.enabled;
			});
		}).then(checkTemplateOnCdn)
			.catch(function (error) {
			errors.catch("", false)(error)
		}).finally(() => {
			$scope.loading = false
		});
	}

	function checkTemplateOnCdn() {
		$scope.loading = true

		return cdn.link(controller.model.domain.name, controller.model.name, controller.model.lang).then(function (response) {
			if (!response._status) {
				controller.cdnUrl = response
			}
		}).catch(function (error) {
			errors.catch("", false)(error)
		}).finally(() => {
			$scope.loading = false
		})
	}

	
}]);
