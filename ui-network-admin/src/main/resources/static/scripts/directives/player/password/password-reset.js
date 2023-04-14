'use strict';

angular.module('lithium')
.controller('PasswordResetModal',
['$uibModalInstance', 'user', "ProfileRest", 'userFields', 'notify', 'errors', 'bsLoadingOverlayService',
function ($uibModalInstance, user, ProfileRest, userFields, notify, errors, bsLoadingOverlayService) {
	var controller = this;
	
	controller.options = {};
	controller.model = { };

	controller.fields = [
		{
			className: "col-xs-12",
			key: "type",
			type: "ui-select-single",
			templateOptions: {
				label: "",
				description: "",
				placeholder: "",
				valueProp: 'value',
				labelProp: 'label',
				optionsAttr: 'ui-options',
				ngOptions: 'ui-options',
				options: [
					{ value: 'email', label: 'Email' },
					{ value: 'sms', label: 'SMS' },
					{ value: 'all', label: 'Both' }
				],
				required: true
			},
			defaultValue: 'email',
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.PASSWORDRESET.FIELDS.MESSAGETYPE.LABEL" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.PASSWORDRESET.FIELDS.MESSAGETYPE.DESCRIPTION" | translate'
			},
		},
		{
			className: "col-xs-12",
			key: "token",
			type: "ui-select-single",
			templateOptions: {
				label: "",
				description: "",
				placeholder: "",
				valueProp: 'value',
				labelProp: 'label',
				optionsAttr: 'ui-options',
				ngOptions: 'ui-options',
				 options: [
					{ value: 'n', label: 'Numeric' },
					{ value: 'an', label: 'Alphanumeric' }
				],
				required: true
			},
			defaultValue: 'n',
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.PASSWORDRESET.FIELDS.TOKENTYPE.LABEL" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.PASSWORDRESET.FIELDS.TOKENTYPE.DESCRIPTION" | translate'
			}
		},
		{
			className: 'col-xs-12',
			key: 'tokenLength',
			type: 'input',
			templateOptions: {
				label: '',
				description: '',
				required: true,
				type: 'number',
				min: 4,
			},
			defaultValue: 5,
			expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.PASSWORDRESET.FIELDS.TOKENLENGTH.LABEL" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.PASSWORDRESET.FIELDS.TOKENLENGTH.DESCRIPTION" | translate'
			}
		}
	];
	
	controller.referenceId = 'passwordreset-overlay';
	controller.submit = function() {
		bsLoadingOverlayService.start({referenceId:controller.referenceId});
		if (controller.form.$invalid) {
			angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
			notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
			bsLoadingOverlayService.stop({referenceId:controller.referenceId});
			return false;
		}

		ProfileRest.resetPassword(user.domain.name, user.guid, controller.model.type, controller.model.token, controller.model.tokenLength).then(function(response) {
			$uibModalInstance.close(response);
		}).catch(function(data) {
			if (!data.message.includes('Token cool off period not yet reached')) {
				errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.PASSWORDSAVE", false)
			}
		}).finally(function () {
			bsLoadingOverlayService.stop({referenceId: controller.referenceId});
		});
	};
	
	controller.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}]);
