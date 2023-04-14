'use strict';

angular.module('lithium').controller('XPSchemesAddLevelModal', ['level', 'domainName', 'number', "$scope", "notify", "$uibModalInstance", 'rest-casino', 'XPRest',
function (level, domainName, number, $scope, notify, $uibModalInstance, casinoRest, xpRest) {
	var controller = this;
	
	if (level !== null) controller.model = level;
	else controller.model = { number: number };
	
	controller.fields = [
		{
			key: "number",
			type: "input",
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true, disabled: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.XP.FIELDS.LEVELS.NUMBER.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.XP.FIELDS.LEVELS.NUMBER.DESCRIPTION" | translate'
			}
		},
		{
			key: 'requiredXp',
			type: 'ui-number-mask',
			optionsTypes: ['editable'],
			templateOptions : {
				label: "",
				required: true,
				decimals: 0,
				hidesep: true,
				neg: false,
				min: '',
				max: ''
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.XP.FIELDS.LEVELS.REQUIREDXP.NAME" | translate'
			}
		},
		{
			key: "description",
			type: "textarea",
			templateOptions: {
				label: "", description: "", placeholder: "",
				required: true
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.XP.FIELDS.LEVELS.DESCRIPTION.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.XP.FIELDS.LEVELS.DESCRIPTION.DESCRIPTION" | translate'
			}
		},
		{
			key: "bonus.bonusCode", 
			type: "ui-select-single",
			templateOptions : {
				label: "",
				description: "",
				placeholder: "",
				valueProp: 'bonusCode',
				labelProp: 'bonusName',
				optionsAttr: 'ui-options', ngOptions: 'ui-options',
				options: []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.XP.FIELDS.LEVELS.BONUS.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.XP.FIELDS.LEVELS.BONUS.DESCRIPTION" | translate'
			},
			controller: ['$scope', function($scope) {
				casinoRest.findPublicBonusListV2(domainName, 2, 4).then(function(response) {
					$scope.to.options = response;
				});
			}]
		},
		{
			key: 'milestone',
			type: 'checkbox',
			optionsTypes: ['editable'],
			templateOptions: {
				label: '',
				description: ''
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.XP.FIELDS.MILESTONE.NAME" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.XP.FIELDS.MILESTONE.DESCRIPTION" | translate'
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