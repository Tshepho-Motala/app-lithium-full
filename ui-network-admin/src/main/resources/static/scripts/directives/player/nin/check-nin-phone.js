'use strict';

angular.module('lithium')
	.controller('CheckNinPhoneModal', ['user', '$uibModalInstance', '$scope', 'notify', 'KycVerifyRest', 'bsLoadingOverlayService', 'formlyValidators',
		function (user, $uibModalInstance, $scope, notify, kycVerifyRest, bsLoadingOverlayService, formlyValidators,) {
			var controller = this;
			controller.options = {removeChromeAutoComplete: true};

			controller.model = {};
			controller.fields = [];
			controller.model.ninNumber = user.cellphoneNumber;

			controller.fields = [
				{
					className: "col-xs-12",
					key: "ninNumber",
					type: "input",
					optionsTypes: ['editable'],
					templateOptions: {
						label: "", description: "", placeholder: "",
						required: true, minlength: 10, maxlength: 13,
					},
					expressionProperties: {
						'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.NIN_BY_PHONE.NUMBER" | translate',
						'templateOptions.placeholder': '"UI_NETWORK_ADMIN.PLAYER.NIN_BY_PHONE.PLACEHOLDER" | translate',
						'templateOptions.description': '"UI_NETWORK_ADMIN.PLAYER.NIN_BY_PHONE.DESCRIPTION" | translate'
					},
					validators: {
						pattern: formlyValidators.telephone()
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
				var checkNinRequest = {
					userGuid: user.guid,
					identifier: controller.model.ninNumber,
					methodType: "METHOD_NIN_PHONE_NUMBER"
				}
				kycVerifyRest.verify(checkNinRequest).then(function (response) {
					if (response._successful===false) {
						notify.error(response._message);
					} else {
						notify.success("User verified successfully");
						$uibModalInstance.close(response);
					}
				}, function (status) {
					notify.error("UI_NETWORK_ADMIN.PLAYER.NIN_BY_PHONE.ERROR");
				});
				bsLoadingOverlayService.stop({referenceId: $scope.referenceId});
			};

			controller.cancel = function () {
				$uibModalInstance.dismiss('cancel');
			};
		}]);
