'use strict';

angular.module('lithium').controller('ReportFilterAddModal', ["$scope", "$uibModalInstance", "notify", "StatusRest",
function ($scope, $uibModalInstance, notify, StatusRest) {
	var controller = this;
	
	controller.model = {};
	
	controller.fields = [];
	
	controller.setupFields = function() {
		controller.fields = [];
		
		controller.fields.push({
			key : "field",
			type : "ui-select-single",
			templateOptions : {
				label : "",
				required : true,
				description : "",
				valueProp : 'value',
				labelProp : 'name',
				optionsAttr: 'ui-options', "ngOptions": 'ui-options',
				placeholder : 'Select Field',
				options : [
					{ name: "Player Created Date", value: "playerCreatedDate" },
					{ name: "Gender", value: "gender"},
					{ name: "Stage", value: "stage"}

				]
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.FIELD.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.FIELD.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.FIELD.DESCRIPTION" | translate'
			}
		});
		

		if (controller.model.field === 'playerCreatedDate') {
			controller.fields.push({
				key : "operator",
				type : "ui-select-single",
				templateOptions : {
					label : "",
					required : true,
					description : "",
					valueProp : 'value',
					labelProp : 'name',
					optionsAttr: 'ui-options', "ngOptions": 'ui-options',
					placeholder : 'Select Field',
					options : [{ name: "Is", value: "equalTo"},
								{ name: "Before", value: "lessThan"},
								{ name: "After", value: "greaterThan"}]
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.OPERATOR.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.OPERATOR.PLACEHOLDER" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.OPERATOR.DESCRIPTION" | translate'
				}
			});
		} else {
			if (controller.model.field === 'stage') {
				controller.fields.push({
					key: "operator",
					type: "ui-select-single",
					templateOptions: {
						label: "",
						required: true,
						description: "",
						valueProp: 'value',
						labelProp: 'name',
						optionsAttr: 'ui-options', "ngOptions": 'ui-options',
						placeholder: 'Select Field',
						options: [{name: "Equal To", value: "equalTo"},
							{name: "In", value: "in"}]
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.OPERATOR.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.OPERATOR.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.OPERATOR.DESCRIPTION" | translate'
					}
				});
			} else if (controller.model.field !== 'gender') {
				controller.fields.push({
					key: "operator",
					type: "ui-select-single",
					templateOptions: {
						label: "",
						required: true,
						description: "",
						valueProp: 'value',
						labelProp: 'name',
						optionsAttr: 'ui-options', "ngOptions": 'ui-options',
						placeholder: 'Select Field',
						options: [{name: "Equal To", value: "equalTo"},
							{name: "Less Than", value: "lessThan"},
							{name: "Greater Than", value: "greaterThan"}]
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.OPERATOR.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.OPERATOR.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.OPERATOR.DESCRIPTION" | translate'
					}
				});
			}
		}
		
		if (controller.model.field === 'playerCreatedDate') {
			controller.fields.push({
				key : "value",
				type: "input",
				templateOptions: {
					label: "", description: "", placeholder: "", required: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.XDAYSAGO.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.XDAYSAGO.PLACEHOLDER" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.XDAYSAGO.DESCRIPTION" | translate'
				},modelOptions: {
					updateOn: 'default change blur', debounce: 0
				},
				validators: {
					pattern: {
						expression: function($viewValue, $modelValue, scope) {
							return /^[0-9]+$/.test($viewValue);
						},
						message: '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.VALUE.PATTERN.NUMBERS" | translate'
					}
				}
			});
		} else if (controller.model.field === 'gender') {
			controller.fields.push({
				key : "value",
				type : "ui-select-single",
				templateOptions : {
					label : "",
					required : true,
					description : "",
					valueProp : 'value',
					labelProp : 'name',
					optionsAttr: 'ui-options', "ngOptions": 'ui-options',
					placeholder : 'Select Field',
					options : [
						{ name: "Male", value: "male"},
						{ name: "Female", value: "female"}]
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.VALUE.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.VALUE.PLACEHOLDER" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.VALUE.DESCRIPTION" | translate'
				}
			});
		} else {
			if (controller.model.field != null && controller.model.field != '') {
				if (controller.model.field === 'stage') {
					controller.fields.push({
						key: "value",
						type: "input",
						templateOptions: {
							label: "", description: "", placeholder: "", required: true
						},
						expressionProperties: {
							'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.VALUE.NAME" | translate',
							'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.VALUE.PLACEHOLDER" | translate',
							'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.VALUE.DESCRIPTION" | translate'
						}
					});
				} else {
					controller.fields.push({
						key: "value",
						type: "input",
						templateOptions: {
							label: "", description: "", placeholder: "", required: true
						},
						expressionProperties: {
							'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.VALUE.NAME" | translate',
							'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.VALUE.PLACEHOLDER" | translate',
							'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.VALUE.DESCRIPTION" | translate'
						},
						modelOptions: {
							updateOn: 'default change blur', debounce: 0
						},
						validators: {
							pattern: {
								expression: function($viewValue, $modelValue, scope) {
									return /^[0-9]+$/.test($viewValue);
								},
								message: '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.VALUE.PATTERN.NUMBERS" | translate'
							}
						}
					});
				}
			}
		}
	}
	
	controller.setupFields();
	
	$scope.$watch(function() { return controller.model.field }, function(newValue, oldValue) {
		if (newValue != oldValue) {
			controller.model.operator = "";
			controller.model.value = "";
			controller.setupFields();
		}
	});
	
	controller.save = function() {
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		if (controller.model.operator === undefined || controller.model.operator === null || controller.model.operator === '') {
			controller.model.operator = 'equalTo';
		}
		$uibModalInstance.close(controller.model);
	}
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	}
}]);