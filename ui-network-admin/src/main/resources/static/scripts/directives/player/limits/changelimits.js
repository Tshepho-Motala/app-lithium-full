'use strict';

angular.module('lithium')
.controller('ChangeLimitsModal',
['$uibModalInstance', 'user', 'dailyLossLimit', 'weeklyLossLimit', 'monthlyLossLimit', 'granularity', '$scope', 'notify', 'errors', 'bsLoadingOverlayService', 'userLimitsRest', 'domainLimitsRest', '$translate',
function ($uibModalInstance, user, dailyLossLimit, weeklyLossLimit, monthlyLossLimit, granularity, $scope, notify, errors, bsLoadingOverlayService, userLimitsRest, domainLimitsRest, $translate) {
	var controller = this;
	
	controller.options = {};
	controller.model = {};

	controller.fields = [
	];
	
	controller.init = function() {
		if (granularity === userLimitsRest.GRANULARITY_DAY) {
			controller.type = 'Daily';
			controller.model.dailyLossLimit = dailyLossLimit;

			controller.fields.push({
				className: 'col-xs-12',
				key: 'dailyLossLimit',
				type: 'ui-money-mask',
				templateOptions: {
					label: '',
					description: '',
					placeholder: '',
					required: true,
					addFormControlClass: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.LIMITS.PLAYER.DAILYLOSSLIMIT.LABEL" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LIMITS.PLAYER.DAILYLOSSLIMIT.LABEL" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.LIMITS.PLAYER.DAILYLOSSLIMIT.DESCRIPTION" | translate'
				}
			});
		} else if (granularity === userLimitsRest.GRANULARITY_WEEK) {
			controller.type = 'Weekly';
			controller.model.weeklyLossLimit = weeklyLossLimit;
			controller.fields.push({
				className: 'col-xs-12',
				key: 'weeklyLossLimit',
				type: 'ui-money-mask',
				templateOptions: {
					label: '',
					description: '',
					placeholder: '',
					required: true,
					addFormControlClass: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.LIMITS.PLAYER.WEEKLYLOSSLIMIT.LABEL" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LIMITS.PLAYER.WEEKLYLOSSLIMIT.LABEL" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.LIMITS.PLAYER.WEEKLYLOSSLIMIT.DESCRIPTION" | translate'
				}
			});
		} else if (granularity === userLimitsRest.GRANULARITY_MONTH) {
			controller.type = 'Monthly';
			controller.model.monthlyLossLimit = monthlyLossLimit;
			controller.fields.push({
				className: 'col-xs-12',
				key: 'monthlyLossLimit',
				type: 'ui-money-mask',
				templateOptions: {
					label: '',
					description: '',
					placeholder: '',
					required: true,
					addFormControlClass: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.LIMITS.PLAYER.MONTHLYLOSSLIMIT.LABEL" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.LIMITS.PLAYER.MONTHLYLOSSLIMIT.LABEL" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.LIMITS.PLAYER.MONTHLYLOSSLIMIT.DESCRIPTION" | translate'
				}
			});
		}
	}
	
	controller.init();
	
	controller.referenceId = 'changeuserlimits-overlay';
	controller.submit = function() {
		bsLoadingOverlayService.start({referenceId:controller.referenceId});
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			return false;
		}
		let limit = null;
		if (granularity === userLimitsRest.GRANULARITY_DAY) {
			limit = controller.model.dailyLossLimit;
		} else if (granularity === userLimitsRest.GRANULARITY_WEEK) {
			limit = controller.model.weeklyLossLimit;
		} else if (granularity === userLimitsRest.GRANULARITY_MONTH) {
			limit = controller.model.monthlyLossLimit;
		}
		limit = limit * 100;
		limit = limit.toFixed(0) + '';
		limit = limit.replace(',', '');
		limit = limit.replace('.', '');

		userLimitsRest.setPlayerLimit(user.guid, user.id, user.domain.name, granularity, limit, userLimitsRest.LIMIT_TYPE_LOSS).then(function(response) {
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			if (response._successful) {
				notify.success('UI_NETWORK_ADMIN.LIMITS.PLAYER.CHANGE.SUCCESS');
				$uibModalInstance.close(response);
			} else {
				notify.error('UI_NETWORK_ADMIN.LIMITS.PLAYER.CHANGE.ERROR');
				$uibModalInstance.close();
			}
		}).catch(function() {
			errors.catch('', false);
		});
	};
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);
