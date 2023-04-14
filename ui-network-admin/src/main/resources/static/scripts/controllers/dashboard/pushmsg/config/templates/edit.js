'use strict';

angular.module('lithium').controller('PushMsgTemplateEdit', ['template','PushMsgTemplateRest','notify','$q','$state','errors', function(template,rest,notify,$q,$state,errors) {
	var controller = this;
	controller.model = template;
	
	controller.fields = [{
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
			'templateOptions.label': '"UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.FIELDS.NAME.NAME" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.FIELDS.NAME.NAME" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.FIELDS.NAME.DESCRIPTION" | translate'
		},
		asyncValidators: {
			nameUnique: {
				expression: function($viewValue, $modelValue, scope) {
					var success = false;
					return rest.findByNameAndDomainName(encodeURIComponent($viewValue), template.domain.name).then(function(pushmsgTemplate) {
						if (angular.isUndefined(pushmsgTemplate) || (pushmsgTemplate._status == 404) || (pushmsgTemplate.length === 0)) {
							success = true;
						}
						if (pushmsgTemplate != null && pushmsgTemplate.name === $viewValue) {
							success = true;
						}
					}).catch(function() {
						scope.options.validation.show = true;
						errors.catch("UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.FIELDS.NAME.UNIQUE", false);
					}).finally(function () {
						scope.options.templateOptions.loading = false;
						if (success) {
							return $q.resolve("No such pushmsg template");
						} else {
							return $q.reject("The pushmsg template already exists");
						}
					});
				},
				message: '"UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.FIELDS.NAME.UNIQUE" | translate'
			}
		}
	},
//	{
//		className: "col-xs-12",
//		key: "lang",
//		type: "ui-select-single",
//		templateOptions: {
//			label: "", description: "", placeholder: "",
//			required: true,
//			optionsAttr: 'bs-options',
//			valueProp : 'locale2',
//			labelProp : 'description',
//			optionsAttr: 'ui-options', ngOptions: 'ui-options',
//			options: []
//		},
//		expressionProperties: {
//			'templateOptions.label': '"UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.FIELDS.LANG.NAME" | translate',
//			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.FIELDS.LANG.NAME" | translate',
//			'templateOptions.description': '"UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.FIELDS.LANG.DESCRIPTION" | translate'
//		},
//		controller: ['$scope', '$http', function($scope, $http) {
//			$http.get("services/service-translate/apiv1/languages/all").then(function(response) {
//				$scope.to.options = response.data;
//			});
//		}]
//	},
	{
		className: "col-xs-12",
		key: "edit.providerTemplateId",
		type: "input",
		optionsTypes: ['editable'],
		templateOptions: {
			label: "", description: "", placeholder: ""
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.FIELDS.PROVIDERTEMPLATEID.NAME" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.FIELDS.PROVIDERTEMPLATEID.NAME" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.FIELDS.PROVIDERTEMPLATEID.DESCRIPTION" | translate'
		}
	},
	{
		className: "col-xs-12",
		key: "edit.description",
		type: "input",
		optionsTypes: ['editable'],
		templateOptions: {
			label: "", description: "", placeholder: ""
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.FIELDS.DESCRIPTION.NAME" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.FIELDS.DESCRIPTION.NAME" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
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
			'templateOptions.label': '"UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.FIELDS.ENABLED.NAME" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.FIELDS.ENABLED.DESCRIPTION" | translate'
		}
	}];
	
	controller.onSubmit = function() {
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		rest.save(controller.model).then(function(response) {
			notify.success("UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.SUCCESS.SAVE");
			$state.go("^.view", { domainName:response.domain.name, id:response.id });
		}).catch(function(error) {
			notify.error("UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.ERRORS.SAVE");
			errors.catch("", false)(error)
		});
	}
	
	controller.onContinue = function() {
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		rest.continueLater(controller.model).then(function(response) {
			notify.success("UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.SUCCESS.SAVE");
			$state.go("^.view", { domainName:response.domain.name, id:response.id });
		}).catch(function(error) {
			notify.error("UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.ERRORS.SAVE");
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
			notify.success("UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.SUCCESS.SAVE");
			$state.go("^.view", { domainName:response.domain.name, id:response.id });
		}).catch(function(error) {
			notify.error("UI_NETWORK_ADMIN.PUSHMSG.TEMPLATES.ERRORS.SAVE");
			errors.catch("", false)(error)
		});
	}
}]);