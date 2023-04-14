'use strict';

angular.module('lithium').controller('GoGameMathModelsAddReelsController', ["reelSets", "errors", "$scope", "notify", "$uibModalInstance",
function (reelSets, errors, $scope, notify, $uibModalInstance) {
	var controller = this;
	
	controller.model = {};
	
	controller.fields = [
		{
			key: 'reelsetId',
			type: 'ui-select-single',
			templateOptions: {
				label: 'Reel Set',
				required: true,
				optionsAttr: 'bs-options',
				description: "",
				valueProp: 'id',
				labelProp: 'name',
				optionsAttr: 'ui-options', "ngOptions": 'ui-options',
				placeholder: '',
				options: []
			},
//			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.NAME" | translate',
//				'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.DOMAIN.DESCRIPTION" | translate'
//			},
			controller: ['$scope', function($scope) {
				$scope.to.options = reelSets;
			}]
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