'use strict';

angular.module('lithium').controller('GoGameMathModelsFeatureConfigsAddOptionController', ["option", "showTextArea", "errors", "$scope", "notify", "$uibModalInstance",
function (option, showTextArea, errors, $scope, notify, $uibModalInstance) {
	var controller = this;
	
	controller.model = {};
	if (option != null) {
		controller.model.option = option;
		controller.changing = true;
	}
	
	controller.fields = [
	];

	if (!showTextArea) {
		controller.fields.push({
			key: "option",
			type: "input",
			templateOptions: {
				label: "Option",
				description: "",
				placeholder: "",
				required: true
			},
//			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.NAME" | translate',
//				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.PLACEHOLDER" | translate',
//				'templateOptions.description': '"UI_NETWORK_ADMIN.GAME.FIELDS.NAME.DESCRIPTION" | translate'
//			}
		});
	} else {
		controller.fields.push({
			key: "option",
			type: "textarea",
			templateOptions: {
				label: 'Option',
				description: "Ensure proper JSON formatting.",
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
		});
	}
	
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