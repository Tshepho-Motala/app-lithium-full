'use strict';

angular.module('lithium')
	.controller('CheckBankAccountModal', ['user', 'banks','$uibModalInstance', '$scope', 'notify', 'KycVerifyRest', 'bsLoadingOverlayService', 'formlyValidators',
		function (user, banks, $uibModalInstance, $scope, notify, kycVerifyRest, bsLoadingOverlayService, formlyValidators,) {
			var controller = this;
			controller.options = {removeChromeAutoComplete: true};

			controller.model = {};
			controller.fields = [];
			controller.model.accountNumber = undefined;
			controller.model.bank = undefined;
			controller.fields = [
				{
					key: "bank",
					className: "col-xs-12 form-group",
					type: "ui-select-single",
					templateOptions : {
						label: "", description: "", placeholder: "", required : true,
						// optionsAttr: 'bs-options',
						valueProp: 'code',
						labelProp: 'name',
						optionsAttr: 'ui-options', ngOptions: 'ui-options',
						options: banks
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.VERIFY_BY_BANK_ACCOUNT.BANK_CODE" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYER.VERIFY_BY_BANK_ACCOUNT.BANK_PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.VERIFY_BY_BANK_ACCOUNT.BANK_DESCRIPTION" | translate'
					}
				},
				{
					className: "col-xs-12",
					key: "accountNumber",
					type: "input",
					optionsTypes: ['editable'],
					templateOptions: {
						label: "", description: "", placeholder: "",
						required: true, minlength: 10, maxlength: 10,
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.VERIFY_BY_BANK_ACCOUNT.ACCOUNT_NUMBER" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYER.VERIFY_BY_BANK_ACCOUNT.ACCOUNT_PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.VERIFY_BY_BANK_ACCOUNT.ACCOUNT_DESCRIPTION" | translate'
					}
				}
			];

			controller.submit = function () {
				$scope.referenceId = 'personal-overlay';
				if (controller.form.$invalid) {
					angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
					notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
					return false;
				}
				var checkRequest = {
					userGuid: user.guid,
					bankCode: controller.model.bank,
					identifier: controller.model.accountNumber,
					methodType: "METHOD_BANK_ACCOUNT"
				}
				kycVerifyRest.verify(checkRequest).then(function (response) {
					if (response._successful===false) {
						notify.error(response._message);
					} else {
						notify.success("User verified successfully");
						$uibModalInstance.close(response);
					}
				}, function (status) {
					notify.error("UI_NETWORK_ADMIN.PLAYER.VERIFY_BY_BANK_ACCOUNT.ERROR");
				});
				bsLoadingOverlayService.stop({referenceId: $scope.referenceId});
			};

			controller.cancel = function () {
				$uibModalInstance.dismiss('cancel');
			};
		}]);
