'use strict';

angular.module('lithium').controller('GoGameMathModelsAddLinesController', ["errors", "$scope", "notify", "$uibModalInstance",
function (errors, $scope, notify, $uibModalInstance) {
	var controller = this;
	
	controller.model = {};
	
	controller.fields = [
		{
			key: 'position',
			type: 'ui-number-mask',
			optionsTypes: ['editable'],
			templateOptions: {
				label: 'Position',
				decimals: 0,
				hidesep: true,
				neg: false,
				min: '0',
				max: '',
				required: true,
				focus: true
			},
//			expressionProperties: {
//				'templateOptions.label': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.NAME" | translate',
//				'templateOptions.description': '"UI_NETWORK_ADMIN.MISSIONS.FIELDS.XPLEVEL.DESCRIPTION" | translate'
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