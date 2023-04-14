'use strict';

angular.module('lithium').controller('DomainClientAddModal', ['$uibModalInstance', 'domain', 'notify', 'rest-provider-auth-client', 'errors', 'bsLoadingOverlayService',
function ($uibModalInstance, domain, notify, restProviderAuthClient, errors, bsLoadingOverlayService) {
	var controller = this;

	controller.referenceId = 'addclient-overlay';
	controller.model = {};

	controller.fields = [{
		className : 'col-xs-12',
		key: "code",
		type: "input",
		templateOptions: {
			label: "", description: "", placeholder: "",
			required: true
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.CLIENTS.FIELDS.CODE.NAME" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.CLIENTS.FIELDS.CODE.DESCRIPTION" | translate'
		}
	},{
		className : 'col-xs-12',
		key: "description",
		type: "input",
		templateOptions: {
			label: "", description: "", placeholder: "",
			required: false
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.CLIENTS.FIELDS.DESCRIPTION.NAME" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.CLIENTS.FIELDS.DESCRIPTION.DESCRIPTION" | translate'
		}
	},{
		className : 'col-xs-12',
		key: "password",
		type: "input",
		templateOptions: {
			label: "", description: "", placeholder: "",
			required: true
		},
		expressionProperties: {
			'templateOptions.label': '"UI_NETWORK_ADMIN.DOMAIN.CLIENTS.FIELDS.PASSWORD.NAME" | translate',
			'templateOptions.description': '"UI_NETWORK_ADMIN.DOMAIN.CLIENTS.FIELDS.PASSWORD.DESCRIPTION" | translate'
		}
	}]

	controller.onSubmit = function() {
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			return false;
		}
		bsLoadingOverlayService.start({referenceId:controller.referenceId});

		restProviderAuthClient.add(domain.name, controller.model).then(function(response) {
			if (response._status === 0) {
				notify.success('UI_NETWORK_ADMIN.DOMAIN.CLIENTS.ADD.SUCCESS');
			} else {
				notify.error('UI_NETWORK_ADMIN.DOMAIN.CLIENTS.ADD.ERROR');
			}
			$uibModalInstance.close(response.plain());
		}).catch(function(error) {
			errors.catch('UI_NETWORK_ADMIN.DOMAIN.CLIENTS.ADD.ERROR', false);
		}).finally(function () {
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
		});
	}

	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);

