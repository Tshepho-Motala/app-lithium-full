'use strict';

angular.module('lithium').controller('BonusFreeMoneyAddController', ["domainName", "$scope", "notify", "$uibModalInstance", "rest-accounting-internal", "$translate", 
function (domainName, $scope, notify, $uibModalInstance, accountingInternalRest, $translate) {
	var controller = this;
	
	controller.fields = [
		{
			key: "currency",
			type: "ui-select-single",
			templateOptions: {
				label: "",
				description: "",
				optionsAttr: 'bs-options',
				valueProp: 'value',
				labelProp: 'label',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.BONUSFREEMONEY.CURRENCY.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.BONUSFREEMONEY.CURRENCY.DESCRIPTION" | translate'
			},
			controller: ['$scope', function($scope) {
				accountingInternalRest.findDomainCurrencies(domainName).then(function(response) {
					console.log(response.plain());
					var c = [];
					for (var i = 0; i < response.plain().length; i++) {
						c.push({value: response.plain()[i].currency.code, label: response.plain()[i].currency.code});
					}
					$scope.to.options = c;
				});
			}]
		}, {
			key: 'amount',
			type: 'ui-money-mask',
			optionsTypes: ['editable'],
			templateOptions : {
				label: "",
				description: "",
				required: true,
				addFormControlClass: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.BONUSFREEMONEY.AMOUNT.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.BONUSFREEMONEY.AMOUNT.DESCRIPTION" | translate'
			}
		}, {
			type: 'checkbox',
			key: 'immediateRelease',
			templateOptions: {
				label: '', description: ''
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.BONUSFREEMONEY.IMMEDIATERELEASE.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.BONUSFREEMONEY.IMMEDIATERELEASE.DESCRIPTION" | translate'
			}
		}
//		},{
//			key: "wagerRequirement",
//			type: "ui-number-mask",
//			optionsTypes: ['editable'],
//			templateOptions : {
//				label: "Wager Requirement",
//				required: false,
//				decimals: 0,
//				hidesep: true,
//				neg: false,
//				min: '',
//				max: ''
//			},
//			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.BONUSFREEMONEY.WAGERREQUIREMENT.NAME" | translate'
//			}
//		},{
//			key: "freeMoneyExample",
//			type: "examplewell",
//			templateOptions: {
//				label: "",
//				explain: ""
//			},
//			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.FREEMONEY.EXAMPLE" | translate',
//				'templateOptions.explain': function(viewValue, modelValue, $scope) {
//					if (($scope.model.amount > 0) && ($scope.model.wagerRequirement > 0)) {
//						$translate("UI_NETWORK_ADMIN.BONUS.FREEMONEY.ADDITIONAL.EXPLAIN", {
//							amount: (Math.round($scope.model.amount*100)),
//							wager: (Math.round($scope.model.amount*100)*$scope.model.wagerRequirement),
//							currency: $scope.model.currency
//						}).then(function success(translate) {
//							$scope.options.templateOptions.explain = translate;
//						});
//					} else {
//						$scope.options.templateOptions.explain = '';
//					}
//				}
//			}
//		}
	];
	
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