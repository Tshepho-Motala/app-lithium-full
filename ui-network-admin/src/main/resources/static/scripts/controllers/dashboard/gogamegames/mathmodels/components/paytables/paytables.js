'use strict';

angular.module('lithium').controller('GoGameMathModelsAddPaytablesController', ["errors", "$scope", "notify", "$uibModalInstance",
function (errors, $scope, notify, $uibModalInstance) {
	var controller = this;
	
	controller.model = {};
	
	controller.fields = [
		{
			key: "symbol",
			type: "input",
			templateOptions: {
				label: "Symbol",
				description: "The symbol",
				placeholder: "",
				required: true,
				focus: true
			},
//			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.NAME" | translate',
//				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.PLACEHOLDER" | translate',
//				'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.DESCRIPTION" | translate'
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