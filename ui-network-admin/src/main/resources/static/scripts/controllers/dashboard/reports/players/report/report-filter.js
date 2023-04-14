'use strict';

angular.module('lithium').controller('ReportFilterAddModal', ["$scope", "$uibModalInstance", "notify", "StatusRest", '$translate',
function ($scope, $uibModalInstance, notify, StatusRest, $translate) {
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
				options : [{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYER_FILTERS.BIRTHDAY'), value: "playerBirthday" },
					{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYER_FILTERS.DEPOSIT_COUNT'), value: "playerDepositCount" },
					{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYER_FILTERS.LAST_LOGIN_DATE'), value: "playerLastLoginDate"},
					{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYER_FILTERS.CASINO_BET_CENTS'), value: "playerCasinoBetAmountCents" },
					{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYER_FILTERS.CREATED_DATE'), value: "playerCreatedDate" },
					{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYER_FILTERS.DEPOSIT_CENTS'), value: "playerDepositAmountCents" },
					{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYER_FILTERS.STATUS'), value: "playerStatus"}]
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.FIELD.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.FIELD.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.FIELD.DESCRIPTION" | translate'
			}
		});
		
		if (controller.model.field === 'playerStatus') {
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
					options : [{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYER_FILTERS.EQUAL_TO'), value: "equalTo"}]
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.OPERATOR.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.OPERATOR.PLACEHOLDER" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.OPERATOR.DESCRIPTION" | translate'
				}
			});
		} else {
			if (controller.model.field === 'playerBirthday' ||
					controller.model.field === 'playerLastLoginDate' ||
					controller.model.field === 'playerCreatedDate') {
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
						options : [{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYER_FILTERS.IS'), value: "equalTo"},
							{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYER_FILTERS.BEFORE'), value: "lessThan"},
							{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYER_FILTERS.AFTER'), value: "greaterThan"}]
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.OPERATOR.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.OPERATOR.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.OPERATOR.DESCRIPTION" | translate'
					}
				});
			} else {
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
						options : [{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYER_FILTERS.EQUAL_TO'), value: "equalTo"},
							{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYER_FILTERS.LESS_THAN'), value: "lessThan"},
							{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYER_FILTERS.GREATER_THAN'), value: "greaterThan"}]
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.OPERATOR.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.OPERATOR.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.FILTER.OPERATOR.DESCRIPTION" | translate'
					}
				});
			}
		}
		
		if (controller.model.field === 'playerStatus') {
			controller.fields.push({
				key: "value",
				type: "ui-select-single",
				templateOptions : {
					label: "", description: "", placeholder: "", required : true,
					valueProp: 'name',
					labelProp: 'name',
					optionsAttr: 'ui-options', ngOptions: 'ui-options',
					options: []
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.NAME" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.PLACEHOLDER" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.USER.FIELDS.STATUS.DESCRIPTION" | translate'
				},
				controller: ['$scope', function($scope) {
					StatusRest.findAll().then(function(response) {
						$scope.options.templateOptions.options = response;
						return response;
					});
				}]
			});
		} else if (controller.model.field === 'playerBirthday' ||
			controller.model.field === 'playerLastLoginDate' ||
			controller.model.field === 'playerCreatedDate') {
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
		} else {
			if (controller.model.field != null && controller.model.field !== '') {
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
