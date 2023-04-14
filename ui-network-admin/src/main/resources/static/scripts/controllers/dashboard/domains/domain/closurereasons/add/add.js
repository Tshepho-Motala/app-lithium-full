'use strict';

angular.module('lithium').controller('DomainClosureReasonAddModal', ['$uibModalInstance', 'domain', 'notify', 'ClosureReasonsRest', 'errors', 'bsLoadingOverlayService',
function ($uibModalInstance, domain, notify, closureReasonsRest, errors, bsLoadingOverlayService) {
	var controller = this;

	controller.referenceId = 'addreason-overlay';
	controller.model = {};

	controller.fields = [{
		className : 'col-xs-12',
		key: "description",
		type: "input",
		templateOptions: {
			label: "", description: "", placeholder: "",
			required: false
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.CLOSURE_REASONS.FIELDS.DESCRIPTION.NAME" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.CLOSURE_REASONS.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
		}
	},{
		className : 'col-xs-12',
		key: "text",
		type: "input",
		templateOptions: {
			label: "", description: "", placeholder: "",
			required: true
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.CLOSURE_REASONS.FIELDS.TEXT.NAME" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.CLOSURE_REASONS.FIELDS.TEXT.DESCRIPTION" | translate'
		}
	}]

	controller.onSubmit = function() {
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		bsLoadingOverlayService.start({referenceId:controller.referenceId});

		closureReasonsRest.add(domain.name, controller.model).then(function(response) {
			if (response._status === 0) {
				notify.success('UI_NETWORK_ADMIN.DOMAIN.CLOSURE_REASONS.ADD.SUCCESS');
			} else {
				notify.error('UI_NETWORK_ADMIN.DOMAIN.CLOSURE_REASONS.ADD.ERROR');
			}
			$uibModalInstance.close(response.plain());
		}).catch(function(error) {
			errors.catch('UI_NETWORK_ADMIN.DOMAIN.CLOSURE_REASONS.ADD.ERROR', false);
		}).finally(function () {
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
		});
	}

	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);

