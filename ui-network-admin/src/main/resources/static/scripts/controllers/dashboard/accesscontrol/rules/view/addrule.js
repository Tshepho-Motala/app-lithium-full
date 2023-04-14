'use strict';

angular.module('lithium').controller('AccessRuleAddListModal', ["$uibModalInstance", "notify", "$scope", "$translate", "lists", "externalProviders", "accessRulesRest", "accessRule", "$state",
function ($uibModalInstance, notify, $scope, $translate, lists, externalProviders, accessRulesRest, accessRule, $state) {
	var controller = this;

	controller.options = {formState: {}};
	controller.model = {validateOnce: false};
	controller.model.outputList = {};
	controller.model.type = 'list';
	controller.model.enabled = true;
	
	controller.internalFields = [{
		key : "listId",
		type : "ui-select-single",
		templateOptions : {
			label : "",
			required : true,
			optionsAttr: 'bs-options',
			description : "",
			valueProp : 'id',
			labelProp : 'name',
			placeholder : 'Select ACL',
			options : []
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSRULES.LIST.LABEL" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ACCESSRULES.LIST.PLACEHOLDER" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSRULES.LIST.DESCRIPTION" | translate'
		},
		controller: ['$scope', function($scope) {
			$scope.to.options = lists;
		}]
	}, {
		key: "ipResetTime",
		type: "input",
		templateOptions: {
			label: "Reset time", description: "", placeholder: "", required: false
		},
		hideExpression: function($viewValue, $modelValue, scope) {
			const selectedList = lists.find(
				list => {
					return list.name === controller.model.listName
				}
			);
			return !(selectedList !== undefined && selectedList.listType.name === "IP_List");
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.IP_RESET_TIME.LABEL" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.IP_RESET_TIME.PLACEHOLDER" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.IP_RESET_TIME.DESCRIPTION" | translate'
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
				return [
					{name: 'ACCEPT', value: "ACCEPT"},
					{name: 'REJECT', value: "REJECT"},
					{name: 'CONTINUE', value: "CONTINUE"},
					{name: 'ACCEPT_AND_VERIFY', value: "ACCEPT_AND_VERIFY"}
				];
			}
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
	// },
	// {
	// 	key: "message",
	// 	type: "textarea",
	// 	templateOptions: {
	// 		label: "Message", description: "", placeholder: "", required: false
	// 	},
	// 	modelOptions: {
	// 		updateOn: 'default blur', debounce: { 'default': 1000, 'blur': 0 }
	// 	},
	// 	expressionProperties: {
	// 		'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.MESSAGE.LABEL" | translate',
	// 		'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.MESSAGE.PLACEHOLDER" | translate',
	// 		'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.BASIC.MESSAGE.DESCRIPTION" | translate'
	// 	}
	}
	];
	
	controller.externalFields = [{
		key : "providerUrl",
		type : "ui-select-single",
		templateOptions : {
			required : true,
			optionsAttr: 'bs-options',
			description : "",
			valueProp : 'url',
			labelProp : 'name',
			options : []
		},
		controller: ['$scope', function($scope) {
			$scope.to.options = externalProviders;
		}],
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.PUSHMSG.TBL.PROVIDER" | translate',
			'templateOptions.placeholder': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.VIEW.ADD_RULE.SELECT_PROVIDER" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.VIEW.ADD_RULE.PROVIDER_DESCRIPTION" | translate',
		}
	}, {
		key: "listName",
		type: "input",
		optionsTypes: ['editable'],
		templateOptions: {
			label: "Rule Name",
			description: "",
			placeholder: "Enter Rule Name",
			required: true
		},
		expressionProperties: {
			//'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSRULES.EXTERNAL.LIST.LABEL" | translate',
			//'templateOptions.expternalPlaceholder': '"UI_NETWORK_ADMIN.ACCESSRULES.EXTERNAL.LIST.PLACEHOLDER" | translate',
			//'templateOptions.externalDescription': '"UI_NETWORK_ADMIN.ACCESSRULES.EXTERNAL.LIST.DESCRIPTION" | translate'
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
				return [
					{name: 'ACCEPT', value: "ACCEPT"},
					{name: 'REJECT', value: "REJECT"},
					{name: 'CONTINUE', value: "CONTINUE"},
					{name: 'ACCEPT_AND_VERIFY', value: "ACCEPT_AND_VERIFY"}
				];
			}
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
		type: 'checkbox',
		key: 'validateOnce',
		templateOptions: {
			label: 'Validate Once',
			description: 'Request to external provider once, thereafter use local data store for subsequent requests, unless details are being updated.'
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.ACCESSRULES.EXTERNAL.LIST.FIELDS.VALIDATEONCE.NAME" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.ACCESSRULES.EXTERNAL.LIST.FIELDS.VALIDATEONCE.DESCRIPTION" | translate'
		}
	}];
	
	controller.tabs = [
		{ name: "internal", title: "Internal" },
		{ name: "external", title: "External" }
	];
	
	controller.tab = controller.tabs[0];
	controller.fields = controller.internalFields;
	
	controller.setTab = function(tab) {
		controller.model = {validateOnce: false};
		controller.model.outputList = {};
		controller.model.enabled = true;

		if (tab.name === 'internal') {
			controller.model.type = 'list';
			controller.fields = controller.internalFields;
		} else if (tab.name === 'external') {
			controller.model.type = 'provider';
			controller.fields = controller.externalFields;
		}

		accessRulesRest.getStatusOptionOutcomeList().then(function(response) {
			controller.outcomeList = response.plain();
		});
		accessRulesRest.getStatusOptionOutputList().then(function(response) {
			controller.outputList = response.plain();
		});
		
		controller.tab = tab;
		$state.go(tab.name);
	}
	
	controller.save = function() {
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}

		accessRulesRest.addRule(
			accessRule.id,
			controller.model.type,
			controller.model.providerUrl,
			controller.model.listId,
			controller.model.listName,
			controller.model.actionFailed,
			controller.model.actionSuccess,
			controller.model.ipResetTime,
			controller.model.validateOnce,
			controller.model.enabled,
			controller.model.outputList
		).then(function (response) {
			notify.success("UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.VIEW.ADD_RULE.SUCCESS");
			$uibModalInstance.close(response);
		});
	}
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	}
}]);
