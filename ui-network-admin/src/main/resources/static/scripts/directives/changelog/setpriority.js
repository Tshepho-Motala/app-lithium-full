'use strict';

angular.module('lithium')
.controller('SetChangelogPriorityModal',
['$scope','$uibModalInstance', 'entry', 'notify', 'errors', 'bsLoadingOverlayService', 'ChangelogsRest', '$translate',
function ($scope, $uibModalInstance, entry, notify, errors, bsLoadingOverlayService, changelogsRest, $translate) {
	var controller = this;
	
	controller.model = {};

	controller.fields = [
	];
	
	controller.init = function() {
		controller.model.entry = entry;
		controller.model.priority = entry.priority;
		controller.fields.push({
			className: 'col-xs-12',
			key: 'priority',
			type: 'ui-number-mask',
			templateOptions : {
				description: '',
				required: false,
				decimals: 0,
				hidesep: true,
				neg: false,
				min: '',
				max: '',
				hidden: false,
			},
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.LABEL" | translate',
				'templateOptions.placeholder': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.LABEL" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.CHANGE.DESCRIPTION" | translate'
			}
		});
	}
	
	controller.init();
	controller.referenceId = 'setchangelogpriority-overlay';
	controller.submit = function() {
		bsLoadingOverlayService.start({referenceId:controller.referenceId});
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			return false;
		}
		changelogsRest.setPriority(controller.model.entry.id, controller.model.priority).then(function(response) {
			if (response._successful) {
				notify.success('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.CHANGE.SUCCESS');
				$uibModalInstance.close(response);
			} else {
				notify.error('UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.PRIORITY.CHANGE.ERROR');
				$uibModalInstance.close();
			}
			$uibModalInstance.close(response);
		}).catch(
			errors.catch('', false)
		).finally(function () {
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
		});
	};
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);
