'use strict';

angular.module('lithium').controller('GoGameMathModelsAddPaytablesManualController', ["paytables", "errors", "$scope", "notify", "$uibModalInstance",
function (paytables, errors, $scope, notify, $uibModalInstance) {
	var controller = this;
	
	controller.model = {};
	if (paytables !== null) controller.model.paytables = paytables;
	
	controller.fields = [
		{
			key: "paytables",
			type: "textarea",
			templateOptions: {
				label: 'Paytables',
				description: "",
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