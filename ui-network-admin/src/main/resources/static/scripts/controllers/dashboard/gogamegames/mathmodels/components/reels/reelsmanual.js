'use strict';

angular.module('lithium').controller('GoGameMathModelsAddReelsManualController', ["reelSet", "errors", "$scope", "notify", "$uibModalInstance",
function (reelSet, errors, $scope, notify, $uibModalInstance) {
	var controller = this;
	
	controller.model = {};
	if (reelSet !== null) controller.model.reelSet = reelSet;
	
	controller.fields = [
		{
			key: "reelSet",
			type: "textarea",
			templateOptions: {
				label: 'Reels',
				description: "Ensure proper JSON formatting. E.g. [{\"symbols\":[\"LO2\",\"LO2\",\"LO2\",...]},{\"symbols\":[\"LO2\",\"LO2\",\"LO2\",...]},...]",
				placeholder: "",
				required: true,
				cols: 10,
				rows: 10
			},
//			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.NAME" | translate',
//				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.PLACEHOLDER" | translate',
//				'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
//			}
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