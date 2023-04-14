'use strict';

angular.module('lithium').controller('ReportActionIaAddModal', ["$uibModalInstance", "notify", "actions",
function ($uibModalInstance, notify, actions) {
	let controller = this;
	
	controller.model = {};
	
	controller.fields = [{
			key : "actionType",
			type : "ui-select-single",
			templateOptions : {
				label : "",
				required : true,
				description : "",
				valueProp : 'value',
				labelProp : 'name',
				optionsAttr: 'ui-options', "ngOptions": 'ui-options',
				placeholder : 'Select Field',
				options : []
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.REPORTS.IA.FIELDS.ACTION.TYPE.NAME" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.REPORTS.IA.FIELDS.ACTION.TYPE.PLACEHOLDER" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.REPORTS.IA.FIELDS.ACTION.TYPE.DESCRIPTION" | translate'
			},
			controller: ['$scope', function($scope) {
				$scope.to.options = actions;
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
