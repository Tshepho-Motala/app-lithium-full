'use strict';

angular.module('lithium').controller('AccessControlAddModal', ["$uibModalInstance", "notify", "$filter", "domains", "types", "$scope", "$translate", "accessControlRest",
function ($uibModalInstance, notify, $filter, domains, types, $scope, $translate, accessControlRest) {
	var controller = this;

	controller.options = {formState: {}};
	controller.model = {enabled: true};

	types = $filter('orderBy')(types, 'name');

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
			placeholder : 'Select ACL Domain',
			options : []
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.DOMAIN.LABEL" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.DOMAIN.PLACEHOLDER" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.DOMAIN.DESCRIPTION" | translate'
		},
		controller: ['$scope', function($scope) {
			$scope.to.options = domains;
		}]
	},{
		key: "name",
		type: "input",
		templateOptions: {
			label: "Name", description: "", placeholder: "", required: true
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.NAME.LABEL" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.NAME.PLACEHOLDER" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.NAME.DESCRIPTION" | translate'
		}
	},{
		type: 'checkbox',
		key: 'enabled',
		templateOptions: {
			label: 'Enabled'
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.ENABLED.LABEL" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.ENABLED.DESCRIPTION" | translate'
		}
	},{
		key : "type",
		type : "ui-select-single",
		templateOptions : {
			label : "",
			required : true,
			optionsAttr: 'bs-options',
			description : "",
			valueProp : 'name',
			labelProp : 'displayName',
			placeholder : 'Select ACL Type',
			options : []
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.TYPE.LABEL" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.TYPE.PLACEHOLDER" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.TYPE.DESCRIPTION" | translate'
		},
		controller: ['$scope', function($scope) {
			$scope.to.options = types;
		}]
	},{
		key: "description",
		type: "textarea",
		templateOptions: {
			label: "Description", description: "", placeholder: "", required: true
		},
		modelOptions: {
			updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.DESCRIPTION.LABEL" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.DESCRIPTION.PLACEHOLDER" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.DESCRIPTION.DESCRIPTION" | translate'
		}
	}];

	controller.save = function() {
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		accessControlRest.addList(controller.model).then(function(response) {
			notify.success("The ACL was added successfully.");
			$uibModalInstance.close(response);
		}, function(status) {
			notify.error("Unable to save. Please try again.");
		});
	}

	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	}
}]);
