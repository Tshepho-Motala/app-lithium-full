'use strict';
angular.module('lithium').controller(
    'ConfirmChangeDateOfBirth', ['$uibModalInstance', 'controllerModel', 'UserRest', 'domainName', 'userUpdates', 'bsLoadingOverlayService','errors',
        function ($uibModalInstance, controllerModel, UserRest, domainName, userUpdates, bsLoadingOverlayService, errors) {

            let controller = this;
            controller.model = controllerModel;
            controller.referenceId = 'confirmchangedateofbirth-overlay';

            controller.submit = function () {
                bsLoadingOverlayService.start({referenceId: controller.referenceId});
                UserRest.changedateofbirth(domainName, userUpdates).then(function (response) {
                    $uibModalInstance.close(response);
                }).catch(
                    errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.PERSONALSAVE", false)
                ).finally(function () {
                    bsLoadingOverlayService.stop({referenceId: controller.referenceId});
                });
            }
            controller.cancel = function () {
                $uibModalInstance.dismiss('cancel');
            }
        }]
)