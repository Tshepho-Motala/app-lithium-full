'use strict';

angular.module('lithium')
.controller('LossLimitVisibilityModal',
['$uibModalInstance', 'user', 'visibility', '$scope', 'notify', 'errors', 'bsLoadingOverlayService', 'userLimitsRest', 'domainLimitsRest', '$translate',
function ($uibModalInstance, user, visibility, $scope, notify, errors, bsLoadingOverlayService, userLimitsRest, domainLimitsRest, $translate) {
	var controller = this;
	
	controller.options = {};
	controller.model = {};
	controller.model.visibility = visibility;

	controller.fields = [{
		className: 'col-xs-12',
		key: 'visibility',
		type: 'ui-select-single',
		templateOptions : {
			label: 'Visibility',
			required: true,
			valueProp: 'value',
			labelProp: 'label',
			optionsAttr: 'ui-options',
			ngOptions: 'ui-options',
			options: [
				{value: 'DISABLED', label: $translate.instant('UI_NETWORK_ADMIN.LOSS_LIMITS.VISIBILITY.DISABLED')},
				{value: 'ENABLED', label: $translate.instant('UI_NETWORK_ADMIN.LOSS_LIMITS.VISIBILITY.ENABLED')},
				{value: 'OFF', label: $translate.instant('UI_NETWORK_ADMIN.LOSS_LIMITS.VISIBILITY.OFF')}
			]
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.LOSS_LIMITS.VISIBILITY.LABEL" | translate'
		}
	},{
		className: 'col-xs-12',
		key: "explanation",
		type: "examplewell",
		templateOptions: {
			label: "",
			explain: ""
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.LOSS_LIMITS.VISIBILITY.CHANGE.EXPLANATION" | translate',
			'templateOptions.explain': function (viewValue, modelValue, $scope) {
				$translate("UI_NETWORK_ADMIN.LOSS_LIMITS.VISIBILITY.CHANGE.EXPLAIN_"+((controller.model.visibility !== visibility)?"":"CURRENT_")+controller.model.visibility, {
					visibility: visibility,
				}).then(function success(translate) {
					$scope.options.templateOptions.explain = translate;
				});
			}
		}
	}];
	
	controller.referenceId = 'LossLimitVisibilityModal-overlay';
	controller.submit = function() {
		bsLoadingOverlayService.start({referenceId:controller.referenceId});
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			return false;
		}

		userLimitsRest.setLossLimitVisibility(user.domain.name, user.guid, controller.model.visibility).then(function(response) {
			notify.success('UI_NETWORK_ADMIN.LOSS_LIMITS.VISIBILITY.CHANGE.SUCCESS');
			$uibModalInstance.close(response.plain());
		}).catch(function() {
			errors.catch('', false);
		}).finally(function() {
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
		});
	};
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);
