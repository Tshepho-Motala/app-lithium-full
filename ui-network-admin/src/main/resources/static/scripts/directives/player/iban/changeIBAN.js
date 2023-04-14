'use strict';

angular.module('lithium')
    .controller('ChangeIBANModal',
        ['$uibModalInstance', 'user', "UserRest", 'userFields', 'notify', 'errors', 'bsLoadingOverlayService',
            function ($uibModalInstance, user, UserRest, userFields, notify, errors, bsLoadingOverlayService) {
                let controller = this;
                controller.submitCalled = false;
                controller.options = {removeChromeAutoComplete:true};
                controller.model = user;
             
                controller.fields = userFields.iban();
                controller.referenceId = 'changeIBAN-overlay';
                controller.model.iban = user.additionalData['iban'] || ''

                controller.submit = function() {
                    bsLoadingOverlayService.start({referenceId:controller.referenceId});
                    if (controller.form.$invalid) {
                        angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
                        notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
                        bsLoadingOverlayService.stop({referenceId:controller.referenceId});
                        return false;
                    }

                        UserRest.saveAdditionalDataByUserGuid(user.domain.name, user.id, {
                            iban: controller.model.iban,
                            reason_for_change: controller.model.reason
                        }).then(function (response) {
                            user.additionalData.iban = controller.model.iban;
                            setTimeout(() => controller.model.reason = '', 200)
                            $uibModalInstance.close(response);
                        }).catch(function (error) {
                            errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.PERSONALSAVE", false)
                        }).finally(function () {
                            bsLoadingOverlayService.stop({referenceId: controller.referenceId});
                        });
                };

                controller.cancel = function() {
                    $uibModalInstance.dismiss('cancel');
                };
            }]);
