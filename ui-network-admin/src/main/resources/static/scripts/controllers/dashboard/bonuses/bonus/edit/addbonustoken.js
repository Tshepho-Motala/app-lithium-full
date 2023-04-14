'use strict';

angular.module('lithium').controller('BonusTokenAddController', ["domainName", "$scope", "notify", "$uibModalInstance", "rest-accounting-internal", "$translate",
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
				'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.BONUSTOKEN.CURRENCY.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.BONUSTOKEN.CURRENCY.DESCRIPTION" | translate'
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
				'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.BONUSTOKEN.AMOUNT.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.BONUSTOKEN.AMOUNT.DESCRIPTION" | translate'
			}
		}, {
			key: 'minimumOdds',
			type: 'ui-number-mask',
			templateOptions : {
				label: "",
				description: "",
				required: false,
				decimals: 3,
				hidesep: true,
				neg: false,
				min: '',
				max: '',
				hidden: true,
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.BONUSTOKEN.MINIMUMODDS.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.BONUSTOKEN.MINIMUMODDS.DESCRIPTION" | translate'
			}
		}
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
