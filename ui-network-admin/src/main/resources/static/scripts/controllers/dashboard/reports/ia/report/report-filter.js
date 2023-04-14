'use strict';

angular.module('lithium').controller('ReportFilterIaAddModal', ["$scope", "$uibModalInstance", "notify",
function ($scope, $uibModalInstance, notify) {
	let controller = this;
	
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
					{ name: "Income Access Casino Bet Amount Cents", value: "iaCasinoBetAmountCents" },
					{ name: "Income Access Casino Bet Count", value: "iaCasinoBetCount" },
					{ name: "Income Access Casino Win Amount Cents", value: "iaCasinoWinAmountCents" },
					{ name: "Income Access Casino Win Count", value: "iaCasinoWinCount" },
					{ name: "Income Access Casino Net Amount Cents", value: "iaCasinoNetAmountCents" },
					{ name: "Income Access Casino Bonus Bet Amount Cents", value: "iaCasinoBonusBetAmountCents"},
					{ name: "Income Access Casino Bonus Bet Count", value: "iaCasinoBonusBetCount" },
					{ name: "Income Access Casino Bonus Win Amount Cents", value: "iaCasinoBonusWinAmountCents" },
					{ name: "Income Access Casino Bonus Win Count", value: "iaCasinoBonusWinCount"},
					{ name: "Income Access Casino Bonus Net Amount Cents", value: "iaCasinoBonusNetAmountCents" }
				]
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.IA.FIELDS.FILTER.FIELD.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.IA.FIELDS.FILTER.FIELD.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.IA.FIELDS.FILTER.FIELD.DESCRIPTION" | translate'
			}
		});
		
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
				options : [{ name: "Equal To", value: "equalTo"},
							{ name: "Less Than", value: "lessThan"},
							{ name: "Greater Than", value: "greaterThan"}]
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.IA.FIELDS.FILTER.OPERATOR.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.IA.FIELDS.FILTER.OPERATOR.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.IA.FIELDS.FILTER.OPERATOR.DESCRIPTION" | translate'
			}
		});
		
//		if (controller.model.field === 'playerBirthday' ||
//			controller.model.field === 'playerLastLoginDate' ||
//			controller.model.field === 'playerCreatedDate') {
//			controller.fields.push({
//				key : "value",
//				type: "input",
//				templateOptions: {
//					label: "", description: "", placeholder: "", required: true
//				},
//				expressionProperties: {
//					'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.IA.FIELDS.FILTER.XDAYSAGO.NAME" | translate',
//					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.IA.FIELDS.FILTER.XDAYSAGO.PLACEHOLDER" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.IA.FIELDS.FILTER.XDAYSAGO.DESCRIPTION" | translate'
//				},modelOptions: {
//					updateOn: 'default change blur', debounce: 0
//				},
//				validators: {
//					pattern: {
//						expression: function($viewValue, $modelValue, scope) {
//							return /^[0-9]+$/.test($viewValue);
//						},
//						message: '"UI_NETWORK_ADMIN.REPORTS.IA.FIELDS.FILTER.VALUE.PATTERN.NUMBERS" | translate'
//					}
//				}
//			});
//		} else {
			if (controller.model.field != null && controller.model.field !== '') {
				controller.fields.push({
					key: "value",
					type: "input",
					templateOptions: {
						label: "", description: "", placeholder: "", required: true
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.IA.FIELDS.FILTER.VALUE.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.IA.FIELDS.FILTER.VALUE.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.IA.FIELDS.FILTER.VALUE.DESCRIPTION" | translate'
					},
					modelOptions: {
						updateOn: 'default change blur', debounce: 0
					},
					validators: {
						pattern: {
							expression: function($viewValue, $modelValue, scope) {
								return /^[0-9]+$/.test($viewValue);
							},
							message: '"UI_NETWORK_ADMIN.REPORTS.IA.FIELDS.FILTER.VALUE.PATTERN.NUMBERS" | translate'
						}
					}
				});
			}
//		}
	}
	
	controller.setupFields();
	
	$scope.$watch(function() { return controller.model.field }, function(newValue, oldValue) {
		if (newValue !== oldValue) {
			controller.setupFields();
		}
	});
	
	controller.save = function() {
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		$uibModalInstance.close(controller.model);
	}
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	}
}]);
