'use strict';
angular.module('lithium').controller('ConfirmUpdateFailedLoginBlock', ['$uibModalInstance', 'user', 'referenceId', 'errors', 'UserRest', 'bsLoadingOverlayService',
    function ($uibModalInstance, user, referenceId, errors, UserRest, bsLoadingOverlayService) {

        let controller = this;
        controller.referenceId = referenceId;

        controller.submit = function () {
            bsLoadingOverlayService.start({referenceId: controller.referenceId});

            UserRest.updateFailedLoginBlock(user.domain.name, user.id, false).then(function (response) {
                $uibModalInstance.close(response);
            }).catch(
                errors.catch("UI_NETWORK_ADMIN.PLAYER.EXCESSIVE_FAILED_LOGIN_BLOCK_UPDATE_ERROR", false)
            ).finally(function () {
                bsLoadingOverlayService.stop({referenceId: controller.referenceId});
            });
        }

        controller.cancel = function () {
            $uibModalInstance.dismiss('cancel')
        }
    }]
);
