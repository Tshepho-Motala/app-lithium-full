'use strict';

angular.module('lithium').controller('EditRulesetRule',
	["$uibModalInstance", "notify", "$scope", "$translate", "accessRule", "rule", "accessRulesRest",
function ($uibModalInstance, notify, $scope, $translate, accessRule, rule, accessRulesRest) {
	var controller = this;
	controller.options = {formState: {}};
	if (angular.isUndefined(rule)) {
		controller.model = {};
		controller.model.default = true;
		controller.model.actionSuccess = accessRule.defaultAction;
	} else {
		controller.model = rule;
		controller.model.default = false;
	}
	controller.accessRule = accessRule;

	controller.model.outcomes = {};

	accessRulesRest.getStatusOptionOutputList().then(function(response) {
		controller.outputList = response.plain();
	});

	angular.forEach(controller.model.ruleOutcomes, function(e) {
		controller.model.outcomes[e.outcome.name] = e.output.name;
	});
	
	controller.fields = [{
		key: "description",
		type: "input",
		templateOptions: {
			label: "", description: "", placeholder: "",
			required: false,
			minlength: 0, maxlength: 235
		},
		optionsTypes: ['editable'],
		modelOptions: {
			updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
		},
		hideExpression: function($viewValue, $modelValue, scope) {
			return (controller.model.default === true);
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDITRULE.DESC.LABEL" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDITRULE.DESC.PLACEHOLDER" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDITRULE.DESC.DESCRIPTION" | translate'
		}
	},{
		key : "actionSuccess",
		type : "ui-select-single",
		templateOptions : {
			label : "",
			required : true,
			optionsAttr: 'bs-options',
			description : "",
			valueProp : 'value',
			labelProp : 'name',
			placeholder : '',
			options : []
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDITRULE.ACTIONSUCCESS.LABEL" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDITRULE.ACTIONSUCCESS.PLACEHOLDER" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDITRULE.ACTIONSUCCESS.DESCRIPTION" | translate',
			'templateOptions.options': function() {
				if (controller.model.default === true) {
					return [
						{name: 'ACCEPT', value: "ACCEPT"},
						{name: 'REJECT', value: "REJECT"},
						{name: 'ACCEPT_AND_VERIFY', value: "ACCEPT_AND_VERIFY"}
					];
				} else {
					return [
						{name: 'ACCEPT', value: "ACCEPT"},
						{name: 'REJECT', value: "REJECT"},
						{name: 'CONTINUE', value: "CONTINUE"},
						{name: 'ACCEPT_AND_VERIFY', value: "ACCEPT_AND_VERIFY"}
					];
				}
			},
		}
	},{
		key : "actionFailed",
		type : "ui-select-single",
		templateOptions : {
			label : "",
			required : true,
			optionsAttr: 'bs-options',
			description : "",
			valueProp : 'value',
			labelProp : 'name',
			placeholder : '',
			options : []
		},
		hideExpression: function($viewValue, $modelValue, scope) {
			return (controller.model.default === true);
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDITRULE.ACTIONFAILED.LABEL" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDITRULE.ACTIONFAILED.PLACEHOLDER" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDITRULE.ACTIONFAILED.DESCRIPTION" | translate',
			'templateOptions.options': function() {
				return [
					{name: 'ACCEPT', value: "ACCEPT"},
					{name: 'REJECT', value: "REJECT"},
					{name: 'CONTINUE', value: "CONTINUE"},
					{name: 'ACCEPT_AND_VERIFY', value: "ACCEPT_AND_VERIFY"}
				]
			},
		}
	},{
		key : "ipResetTime",
		type : "input",
		optionsTypes: ['editable'],
		templateOptions : {
			label: "Reset time",
			description: "",
			placeholder: "",
			required: false,
		},
		hideExpression: function($viewValue, $modelValue, scope) {
			return (controller.model.type === "provider") || (controller.model.default === true);
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.IP_RESET_TIME.LABEL" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.IP_RESET_TIME.PLACEHOLDER" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.IP_RESET_TIME.DESCRIPTION" | translate'
		}
	},{
		type: 'checkbox',
		key: 'validateOnce',
		templateOptions: {
			label: 'Validate Once',
			description: 'Request to external provider once, thereafter use local data store for subsequent requests, unless details are being updated.',
			disabled: false
		},
		hideExpression: function($viewValue, $modelValue, scope) {
			return (controller.model.type === "list") || (controller.model.default === true);
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSRULES.EXTERNAL.LIST.FIELDS.VALIDATEONCE.NAME" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSRULES.EXTERNAL.LIST.FIELDS.VALIDATEONCE.DESCRIPTION" | translate'
		}
	},{
		type: 'checkbox',
		key: 'enabled',
		templateOptions: {
			label: 'Enabled',
			description: 'Is this rule enabled?',
			disabled: false
		},
		hideExpression: function($viewValue, $modelValue, scope) {
			return (controller.model.default === true);
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDITRULE.ENABLED.LABEL" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDITRULE.ENABLED.DESCRIPTION" | translate'
		}
	}];
	
	controller.save = function() {
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		if (controller.model.default) {
			accessRulesRest.rulesetDefaultAction(
				accessRule.id,
				controller.model.actionSuccess
			).then(function (response) {
				notify.success("The default action was successfully updated.");
				$uibModalInstance.close(response);
			});
		} else {
			accessRulesRest.editRule(
				accessRule.id,
				controller.model.type,
				controller.model.id,
				controller.model.description,
				controller.model.actionFailed,
				controller.model.actionSuccess,
				controller.model.ipResetTime,
				controller.model.validateOnce,
				controller.model.enabled,
				controller.model.outcomes
			).then(function (response) {
				notify.success("The rule was successfully updated.");
				$uibModalInstance.close(response);
			});
		}
	}
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	}
}]);