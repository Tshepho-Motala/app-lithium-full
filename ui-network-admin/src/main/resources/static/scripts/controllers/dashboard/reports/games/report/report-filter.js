'use strict';

angular.module('lithium').controller('ReportFilterGamesAddModal', ["$scope", "$uibModalInstance", "notify", '$translate',
function ($scope, $uibModalInstance, notify, $translate) {
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
					{ name: "Game Casino Bet Amount Cents", value: "gameCasinoBetAmountCents" },
					{ name: "Game Casino Bet Count", value: "gameCasinoBetCount" },
					{ name: "Game Casino Win Amount Cents", value: "gameCasinoWinAmountCents" },
					{ name: "Game Casino Win Count", value: "gameCasinoWinCount" },
					{ name: "Game Casino Net Amount Cents", value: "gameCasinoNetAmountCents" },
					{ name: "Game Casino Bonus Bet Amount Cents", value: "gameCasinoBonusBetAmountCents"},
					{ name: "Game Casino Bonus Bet Count", value: "gameCasinoBonusBetCount" },
					{ name: "Game Casino Bonus Win Amount Cents", value: "gameCasinoBonusWinAmountCents" },
					{ name: "Game Casino Bonus Win Count", value: "gameCasinoBonusWinCount"},
					{ name: "Game Casino Bonus Net Amount Cents", value: "gameCasinoBonusNetAmountCents" }
				]
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.FILTER.FIELD.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.FILTER.FIELD.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.FILTER.FIELD.DESCRIPTION" | translate'
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
				options : [{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYER_FILTERS.EQUAL_TO'), value: "equalTo"},
							{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYER_FILTERS.LESS_THAN'), value: "lessThan"},
							{ name: $translate.instant('UI_NETWORK_ADMIN.REPORTS.PLAYERS.FIELDS.PLAYER_FILTERS.GREATER_THAN'), value: "greaterThan"}]
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.FILTER.OPERATOR.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.FILTER.OPERATOR.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.FILTER.OPERATOR.DESCRIPTION" | translate'
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
//					'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.FILTER.XDAYSAGO.NAME" | translate',
//					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.FILTER.XDAYSAGO.PLACEHOLDER" | translate',
//					'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.FILTER.XDAYSAGO.DESCRIPTION" | translate'
//				},modelOptions: {
//					updateOn: 'default change blur', debounce: 0
//				},
//				validators: {
//					pattern: {
//						expression: function($viewValue, $modelValue, scope) {
//							return /^[0-9]+$/.test($viewValue);
//						},
//						message: '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.FILTER.VALUE.PATTERN.NUMBERS" | translate'
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
						'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.FILTER.VALUE.NAME" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.FILTER.VALUE.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.FILTER.VALUE.DESCRIPTION" | translate'
					},
					modelOptions: {
						updateOn: 'default change blur', debounce: 0
					},
					validators: {
						pattern: {
							expression: function($viewValue, $modelValue, scope) {
								return /^[0-9]+$/.test($viewValue);
							},
							message: '"UI_NETWORK_ADMIN.REPORTS.GAMES.FIELDS.FILTER.VALUE.PATTERN.NUMBERS" | translate'
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
