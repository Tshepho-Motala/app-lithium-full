'use strict';
angular.module('lithium').controller(
    'ConfirmDeleteEcosystem', ['$uibModalInstance', 'data', 'ecosystemRest', 'bsLoadingOverlayService','errors',
    function ($uibModalInstance, data, ecosystemRest, bsLoadingOverlayService, errors) {

        let controller = this;
        controller.referenceId = 'deleteecosystem-overlay';
        controller.submit = function () {
            bsLoadingOverlayService.start({referenceId: controller.referenceId});
            ecosystemRest.addModifyEcosystems(data).then(function(response) {
                $uibModalInstance.close(response);
            }).catch(
                errors.catch('UI_NETWORK_ADMIN.ECOSYSTEM.MESSAGES.ERROR_ECOSYSTEM', false)
            ).finally(function () {
                bsLoadingOverlayService.stop({referenceId: controller.referenceId});
            });
        }

        controller.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        }
    }]
)