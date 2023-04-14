'use strict';

angular.module('lithium').controller('ExternalBonusGameAddController', ["$scope", "notify", "$uibModalInstance", "$translate",
function ($scope, notify, $uibModalInstance, $translate) {
	var controller = this;
	
	controller.fields = [
		{
			key: "provider",
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
				'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.BONUSEXTERNALGAME.PROVIDER.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.BONUSEXTERNALGAME.PROVIDER.DESCRIPTION" | translate'
			},
			controller: ['$scope', function($scope) {
				$scope.to.options = [
						{ label: "service-casino-provider-cataboom" , value: "service-casino-provider-cataboom" },
						{ label: "Squads (IG)" , value: "svc-reward-pr-ext-ig" }
				];
				// accountingInternalRest.findDomainCurrencies(domainName).then(function(response) {
				// 	console.log(response.plain());
				// 	var c = [];
				// 	for (var i = 0; i < response.plain().length; i++) {
				// 		c.push({value: response.plain()[i].currency.code, label: response.plain()[i].currency.code});
				// 	}
				// 	$scope.to.options = c;
				//});
			}]
		}, {
			key: 'campaignId',
			type: 'input',
			optionsTypes: ['editable'],
			templateOptions : {
				label: "",
				description: "",
				required: true,
				addFormControlClass: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.BONUS.BONUSEXTERNALGAME.CAMPAIGNID.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.BONUS.BONUSEXTERNALGAME.CAMPAIGNID.DESCRIPTION" | translate'
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
