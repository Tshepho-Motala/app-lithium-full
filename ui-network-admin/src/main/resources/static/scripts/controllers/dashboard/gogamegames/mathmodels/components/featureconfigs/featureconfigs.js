'use strict';

angular.module('lithium').controller('GoGameMathModelsAddFeatureConfigsController', ["features", "errors", "$scope", "notify", "$uibModalInstance",
function (features, errors, $scope, notify, $uibModalInstance) {
	var controller = this;
	
	controller.features = features;
	
	controller.model = { type: undefined };
	
	controller.featureFields = [
		{
			key: 'feature',
			type: 'ui-select-single',
			templateOptions: {
				label: 'Feature',
				required: true,
				description: "",
				valueProp: 'value',
				labelProp: 'label',
				optionsAttr: 'ui-options',
				ngOptions: 'ui-options',
				placeholder: '',
				options: []
			},
//			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.NAME" | translate',
//				'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.DESCRIPTION" | translate'
//			},
			controller: ['$scope', function($scope) {
				var options = [];
				for (var i = 0; i < controller.features.length; i++) {
					options.push({label: controller.features[i].feature.code, value: controller.features[i].feature.code});
				}
				$scope.to.options = options;
			}]
		}
	];
	
	controller.setupObject = function() {
		controller.model.object = undefined;
		switch (controller.model.feature) {
			case 'FREESPIN':
				controller.model.type = 'freespin';
				controller.model.object = { numFreespins: [] };
				break;
			case 'RETRIGGER':
				controller.model.type = 'retrigger';
				controller.model.object = { retriggerLimit: 1, numFreespins: [] };
				break;
			case 'FREESPIN_WHEEL':
				controller.model.type = 'freespinWheel';
				controller.model.object = { results: [], resultsProbabilities: [] };
				break;
			case 'MULTIPLIER_WILD':
				controller.model.type = 'multiplierWild';
				controller.model.object = { baseMultiplierWildOptions: [], baseMultiplierWildProbabilities: [], freespinMultiplierWildOptions: [], freespinMultiplierWildProbabilities: [] };
				break;
			case 'ANY_SYMBOL':
				controller.model.type = 'anySymbol';
				controller.model.object = { symbolIds: [], paytableSymId: "HI1" };
				break;
			case 'MEGA_LINK_BONUS':
				controller.model.type = 'megaLinkBonus';
				controller.model.object = { noOfTriggersForBonus: 6, noOfSpins: 3, triggers: [
					{name: "1x", value: 1, isBetMultiplier: true, jackpot: false, weight: 420},
					{name: "2x", value: 2, isBetMultiplier: true, jackpot: false, weight: 200}
				]};
				break;

		}
	}
	
	$scope.$watch(function() { return controller.model.feature }, function(newValue, oldValue) {
		if (newValue != oldValue) {
			if (newValue !== undefined && newValue !== null) {
//				console.log("Selected feature changed", newValue);
				controller.setupObject();
			}
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