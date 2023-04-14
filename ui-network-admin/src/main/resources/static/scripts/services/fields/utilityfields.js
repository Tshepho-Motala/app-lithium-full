'use strict';

angular.module('lithium')
.factory('utilityFields', [ 'UserRest', 'rest-accounting-internal', 'StatusRest', '$q', 'timezoneRest', 'formlyValidators',
	function(UserRest, accountingRest, StatusRest, $q,timezoneRest, formlyValidators) {
		var service = {};
		
		service.currencyTypeAhead = function(className, required, displayOnly) {
			var field = {
				key: 'currencyCode',
				type: "uib-typeahead",
				className: className,
				optionsTypes: ['editable'],
				templateOptions : {
					label: "",
					description: "",
					placeholder: "",
					required : required,
					valueProp: 'code',
					labelProp: 'name',
					displayProp: 'name',
					disabled: false,
					displayOnly:displayOnly,
					editable: true
				},
				expressionProperties: {
					'templateOptions.label': '"GLOBAL.CURRENCY.NAME" | translate',
					'templateOptions.placeholder': '"GLOBAL.CURRENCY.PLACEHOLDER" | translate',
					'templateOptions.description': '"GLOBAL.CURRENCY.DESCRIPTION" | translate'
//					,'templateOptions.disabled': function($viewValue, $modelValue, scope) {return (!!$modelValue);}
				},
				controller: ['$scope', function($scope) {
					$scope.searchTypeAhead = function(searchValue) {
						return accountingRest.allCurrenciesSearch(searchValue).then(function(response) {
							$scope.to.options = response;
							return response;
						});
					}
					$scope.resetTypeAhead = function() {
						$scope.model.currency = null;
						$scope.model.currencyCode = null;
					}
					$scope.selectTypeAhead = function($item, $model, $label, $event) {
						$scope.model.currency = $item.name;
						$scope.model.currencyCode = $item.code;
					}
				}]
			}
			return field;
		}

		return service;
	}
]);