'use strict';

angular.module('lithium').controller('GoGameMathModelsAddPaytablesPayoutCentsController', ["errors", "$scope", "notify", "$uibModalInstance",
function (errors, $scope, notify, $uibModalInstance) {
	var controller = this;
	
	controller.model = {};
	
	controller.fields = [
		{
			key: 'payoutCents',
			type: 'ui-number-mask',
			optionsTypes: ['editable'],
			templateOptions: {
				label: 'Payout Cents',
				decimals: 0,
				hidesep: true,
				neg: false,
				min: '1',
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