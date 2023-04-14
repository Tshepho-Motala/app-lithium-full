'use strict';

angular.module('lithium').controller('AccessRulesAddModal', ["errors", "$uibModalInstance", "domains", "notify", "$scope", "$translate", "accessRulesRest", 
function (errors, $uibModalInstance, domains, notify, $scope, $translate, accessRulesRest) {
	var controller = this;
	
	controller.options = {formState: {}};
	controller.model = {};
	
	controller.fields = [{
		key : "domainName",
		type : "ui-select-single",
		templateOptions : {
			label : "",
			required : true,
			optionsAttr: 'bs-options',
			description : "",
			valueProp : 'name',
			labelProp : 'name',
			placeholder : 'Select Access Rule Domain',
			options : []
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.ADD.BASIC.DOMAIN.LABEL" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.ADD.BASIC.DOMAIN.PLACEHOLDER" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.ADD.BASIC.DOMAIN.DESCRIPTION" | translate'
		},
		controller: ['$scope', function($scope) {
			$scope.to.options = domains;
			controller.model.domainName = $scope.to.options[0].name;
		}]
	},{
		key: "name",
		type: "input",
		templateOptions: {
			label: "", description: "", placeholder: "",
			required: true,
			minlength: 2, maxlength: 35,
			//focus: true, --this causes issues when cancelling form
			onKeydown: function(value, options) {
				options.validation.show = false;
			},
			onBlur: function(value, options) {
				options.validation.show = true;
			}
		},
		modelOptions: {
			updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.ADD.BASIC.NAME.LABEL" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.ADD.BASIC.NAME.PLACEHOLDER" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.ADD.BASIC.NAME.DESCRIPTION" | translate'
		},
		validators: {
			pattern: {
				expression: function($viewValue, $modelValue, scope) {
					return /^[0-9a-zA-Z_\\.]+$/.test($viewValue);
				},
				message: '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.ADD.BASIC.NAME.PATTERN" | translate'
			}
		},
		asyncValidators: {
			nameUnique: {
				expression: function($viewValue, $modelValue, scope) {
					var success = false;
					return accessRulesRest.findByName(controller.model.domainName, encodeURIComponent($viewValue)).then(function(response) {
						if (angular.isUndefined(response) || (response._status == 404) || (response.length === 0)) {
							success = true;
						}
					}).catch(function() {
						scope.options.validation.show = true;
						errors.catch("UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.ADD.BASIC.NAME.UNIQUE", false);
					}).finally(function () {
						scope.options.templateOptions.loading = false;
						if (!success) {
							return $q.reject("The access rule already exists");
						}
					});
				},
				message: '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.ADD.BASIC.NAME.UNIQUE" | translate'
			}
		}
	}];
	
	controller.save = function() {
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		accessRulesRest.add(controller.model.domainName, controller.model.name).then(function(response) {
			notify.success("The access rule was added successfully.");
			$uibModalInstance.close(response);
		}, function(status) {
			notify.error("Unable to save. Please try again.");
		});
	}
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	}
	
	$scope.$watch(function() { return controller.model.domainName }, function(newValue, oldValue) {
		if (newValue != oldValue) {
			if (controller.model.name != null && controller.model.name != '') {
				accessRulesRest.findByName(controller.model.domainName, controller.model.name).then(function(response) {
					if (angular.isUndefined(response) || (response._status == 404) || (response.length === 0)) {
						controller.fields[1].formControl.$invalid = false;
						controller.fields[1].formControl.$valid = true;
						controller.fields[1].formControl.$error.nameUnique = false;
					} else {
						controller.fields[1].formControl.$invalid = true;
						controller.fields[1].formControl.$valid = false;
						controller.fields[1].formControl.$error.nameUnique = true;
					}
				});
			}
		}
	});
}]);